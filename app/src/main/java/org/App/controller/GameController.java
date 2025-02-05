package org.App.controller;

import java.util.List;
import java.util.Map;

import org.App.model.Card;
import org.App.model.Player;
import org.App.model.SkyjoGame;
import org.App.view.CardView;
import org.App.view.GameView;

public final class GameController {
    private static GameController instance;
    private final SkyjoGame game;
    private final GameView view;
    private Card pickedCard;
    private boolean hasDiscard;
    private int count_reveal = 0;

    public GameController(GameView view, List<Player> players) {
        this.view = view;
        this.game = new SkyjoGame(players);
        instance = this;
    }

    public static GameController getInstance() {
        return instance;
    }

    public void startGame() {
        game.startGame();
        game.revealInitialCards();
        updateView();

        List<CardView> cardViews = view.getAllCardViews();
        view.distributeCards(game.getPlayers(), cardViews);
    }

    public void handlePickClick() {
        pickedCard = game.pickCard();

        if (pickedCard != null) {
            System.out.println(game.getActualPlayer().getName() + " a pioché " + pickedCard.valeur());
        }
    }

    public void updateView() {
        view.showPlaying(game.getPlayers(), game.getActualPlayer().getName(), game.getPick().size(), game.getTopDiscard());
    }

    public void handleDiscardClick() {
        if (pickedCard != null) {
            game.addToDiscard(pickedCard);
            pickedCard = null;
            hasDiscard = true;
        } else {
            pickedCard = game.pickDiscard();
            if (pickedCard == null) {
                view.showMessageBox("La défausse est vide !");
            }
        }
    }

    public void handleCardClick(CardView cardView) {
        if (pickedCard != null) {
            game.exchangeOrRevealCard(game.getActualPlayer(), pickedCard, cardView.getIndex());
            pickedCard = null;
            endTurn();
        }
        if (hasDiscard && count_reveal < 2) {
            game.revealCard(game.getActualPlayer(), cardView.getIndex());
            updateView();
            count_reveal++;
        }
        
        if (count_reveal == 2) {
            count_reveal = 0;
            hasDiscard = false;
            endTurn();
        }
    }

    private void endTurn() {
        game.checkColumns();
        if (game.isFinished()) {
            game.revealAllCards();
            Map<Player, Integer> ranking = game.getRanking();
            view.showRanking(ranking);
        } else {
            game.nextPlayer();
            updateView();
        }
    }
}