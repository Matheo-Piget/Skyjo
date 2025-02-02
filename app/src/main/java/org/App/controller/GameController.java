package org.App.controller;

import java.util.HashMap;
import java.util.List;

import org.App.model.Card;
import org.App.model.HumanPlayer;
import org.App.model.Player;
import org.App.model.SkyjoGame;
import org.App.view.CardView;
import org.App.view.GameView;

public final class GameController {
    private static GameController instance;
    private final SkyjoGame game;
    private final GameView view;
    private Card pickedCard;

    public GameController(GameView view) {
        this.view = view;
        this.game = new SkyjoGame(List.of(new HumanPlayer("Joueur 1"), new HumanPlayer("Joueur 2"), 
        new HumanPlayer("Joueur 3"), new HumanPlayer("Joueur 4"), 
        new HumanPlayer("Joueur 5"), new HumanPlayer("Joueur 6"), 
        new HumanPlayer("Joueur 7"), new HumanPlayer("Joueur 8")));
        instance = this;
    }

    public static GameController getInstance() {
        return instance;
    }

    public void startGame() {
        game.startGame();
        game.revealInitialCards();
        view.showPlaying(game.getPlayers(), game.getActualPlayer().getName(),
                game.getPick().size(),
                game.getDiscard().isEmpty() ? null : game.getDiscard().get(game.getDiscard().size() - 1));
    }

    public void handlePickClick() {
        pickedCard = game.pickCard();
        if (pickedCard != null) {
            System.out.println(game.getActualPlayer().getName() + " a pioché " + pickedCard.valeur());
        }
    }

    public void handleDiscardClick() {
        if (pickedCard != null) {
            game.addToDiscard(pickedCard);
            pickedCard = null;
            finDeTour();
        } else {
            pickedCard = game.pickDiscard();
            if (pickedCard != null) {
                System.out.println(game.getActualPlayer().getName() + " a pioché " + pickedCard.valeur() + " de la défausse");
            } else {
                view.showMessageBox("La défausse est vide !");
            }
        }
    }

    public void handleCardClick(CardView cardView) {
        if (pickedCard != null) {
            if (cardView.getIndex() == -1) {
                // Défausser la carte piochée et révéler une carte cachée
                game.addToDiscard(pickedCard);
                game.revealCard(game.getActualPlayer(), cardView.getIndex());
            } else {
                // Échanger la carte piochée avec la carte cliquée
                game.exchangeCard(game.getActualPlayer(), pickedCard, cardView.getIndex());
            }
            pickedCard = null;
            finDeTour();
        }
    }
    
    private void finDeTour() {
        if (game.isFinished()) {
            game.revealAllCard();
            HashMap<Player, Integer> ranking =  game.doRanking();
            view.showRanking(ranking);
        } else {
            game.nextPlayer();
            // Mise à jour de l'affichage avec tous les boards
            view.showPlaying(game.getPlayers(), game.getActualPlayer().getName(),
                    game.getPick().size(),
                    game.getDiscard().isEmpty() ? null : game.getDiscard().get(game.getDiscard().size() - 1));
        }
    }

    public GameView getView() {
        return view;
    }
}
