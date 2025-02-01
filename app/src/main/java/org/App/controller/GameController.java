package org.App.controller;

import org.App.model.SkyjoGame;
import org.App.model.Player;
import org.App.model.HumanPlayer;
import org.App.view.GameView;

import java.util.List;

public final class GameController {
    private final SkyjoGame game;
    private final GameView view;

    public GameController(GameView view) {
        this.view = view;
        this.game = new SkyjoGame(List.of(new HumanPlayer("Joueur 1"), new HumanPlayer("Joueur 2")));
    }

    public void startGame() {
        System.out.println("Le jeu commence avec " + game.getplayers().size() + " joueurs !");
    }
}
