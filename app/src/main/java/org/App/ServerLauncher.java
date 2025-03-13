package org.App;

import org.App.network.GameServer;
import java.util.Scanner;

public class ServerLauncher {
    public static void main(String[] args) {
        System.out.println("Démarrage du serveur Skyjo...");
        GameServer server = new GameServer(5555);
        server.start();
        System.out.println("Serveur prêt! En attente de connexions sur le port 5555");
        
        // Console simple pour contrôler le serveur
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        while (running) {
            System.out.println("\nCommandes disponibles:");
            System.out.println("- start : Démarrer la partie");
            System.out.println("- quit : Arrêter le serveur");
            System.out.print("> ");
            
            String command = scanner.nextLine().trim().toLowerCase();
            switch (command) {
                case "start":
                    server.startGame();
                    break;
                case "quit":
                    running = false;
                    break;
                default:
                    System.out.println("Commande inconnue");
            }
        }
        
        scanner.close();
        System.out.println("Serveur arrêté");
        System.exit(0);
    }
}