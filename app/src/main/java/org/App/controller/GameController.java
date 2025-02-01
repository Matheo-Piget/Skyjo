package org.App.controller;

import java.util.List;

import org.App.model.Card;
import org.App.model.HumanPlayer;
import org.App.model.SkyjoGame;
import org.App.view.GameView;

public final class GameController {
    private final SkyjoGame game;
    private final GameView view;

    public GameController(GameView view) {
        this.view = view;
        this.game = new SkyjoGame(List.of(new HumanPlayer("Joueur 1"), new HumanPlayer("Joueur 2")));
    }

    public void startGame() {
        view.afficherJeu(game.getActualPlayer().getNom(), game.getActualPlayer().getCartes());

        view.getPiocherButton().setOnAction(e -> piocher());
        view.getPasserButton().setOnAction(e -> passerTour());
    }

    private void piocher() {
        Card cartePiochee = game.pickCard();
        if (cartePiochee != null) {
            game.getActualPlayer().piocher(cartePiochee);
            System.out.println(game.getActualPlayer().getNom() + " a pioché " + cartePiochee.valeur());
        }
        finDeTour();
    }

    private void passerTour() {
        System.out.println(game.getActualPlayer().getNom() + " passe son tour.");
        finDeTour();
    }

    private void finDeTour() {
        if (game.isFinished()) {
            System.out.println("La partie est terminée !");
            // Affichage du gagnant ou de la fin de la partie
        } else {
            game.nextPlayer();
            view.afficherJeu(game.getActualPlayer().getNom(), game.getActualPlayer().getCartes());
        }
    }
}
