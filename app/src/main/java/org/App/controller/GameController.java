package org.App.controller;

import java.util.List;

import org.App.model.Card;
import org.App.model.HumanPlayer;
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
        this.game = new SkyjoGame(List.of(new HumanPlayer("Joueur 1"), new HumanPlayer("Joueur 2")));
        instance = this;
    }

    public static GameController getInstance() {
        return instance;
    }

    public void startGame() {
        game.startGame();
        game.revealInitialCards();
        view.afficherJeu(game.getActualPlayer().getNom(), game.getActualPlayer().getCartes(), game.getPick().size(), game.getDiscard().isEmpty() ? null : game.getDiscard().get(game.getDiscard().size() - 1));
    }

    public void handlePickClick() {
        pickedCard = game.pickCard();
        if (pickedCard != null) {
            System.out.println(game.getActualPlayer().getNom() + " a pioché " + pickedCard.valeur());
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
                System.out.println(game.getActualPlayer().getNom() + " a pioché " + pickedCard.valeur() + " de la défausse");
            } else {
                view.showMessageBox("La défausse est vide !");
            }

        }
    }

    public void handleCardClick(CardView cardView) {
        if (pickedCard != null) {
            if (cardView.getIndex() == -1) {
                // Discard the picked card and reveal a hidden card
                game.addToDiscard(pickedCard);
                game.revealCard(game.getActualPlayer(), cardView.getIndex());
            } else {
                // Exchange the picked card with the clicked card
                game.exchangeCard(game.getActualPlayer(), pickedCard, cardView.getIndex());
            }
            pickedCard = null;
            finDeTour();
        }
    }
    

    private void finDeTour() {
        if (game.isFinished()) {
            System.out.println("La partie est terminée !");
            // Affichage du gagnant ou de la fin de la partie
        } else {
            game.nextPlayer();
            view.afficherJeu(game.getActualPlayer().getNom(), game.getActualPlayer().getCartes(), game.getPick().size(), game.getDiscard().isEmpty() ? null : game.getDiscard().get(game.getDiscard().size() - 1));
        }
    }

    public GameView getView() {
        return view;
    }
}