package org.App;

import java.util.Scanner;

import org.App.network.GameServer;

public class ServerLauncher {
    public static void main(String[] args) {
        int port = 5555;
        
        // Allow port customization via command line
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port number. Using default port 5555.");
            }
        }
        
        System.out.println("========================================");
        System.out.println("    Démarrage du serveur Skyjo...");
        System.out.println("========================================");
        
        GameServer server = new GameServer(port);
        server.start();
        
        System.out.println("Serveur prêt! En attente de connexions sur le port " + port);
        System.out.println("\nAdresse IP du serveur:");
        displayServerIP();
        
        // Console simple pour contrôler le serveur
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        
        System.out.println("\nCommandes disponibles:");
        System.out.println("- start : Démarrer la partie");
        System.out.println("- stop : Arrêter le serveur");
        System.out.println("- help : Afficher les commandes disponibles");
        
        while (running) {
            System.out.print("\n> ");
            
            String command = scanner.nextLine().trim().toLowerCase();
            switch (command) {
                case "start":
                    System.out.println("Lancement de la partie...");
                    server.startGame();
                    break;
                case "stop", "quit", "exit":
                    running = false;
                    System.out.println("Arrêt du serveur en cours...");
                    server.stop();
                    break;
                case "help":
                    System.out.println("\nCommandes disponibles:");
                    System.out.println("- start : Démarrer la partie");
                    System.out.println("- stop : Arrêter le serveur");
                    System.out.println("- help : Afficher les commandes disponibles");
                    break;
                default:
                    System.out.println("Commande inconnue. Tapez 'help' pour afficher les commandes disponibles.");
            }
        }
        
        scanner.close();
        System.out.println("Serveur arrêté avec succès.");
        System.exit(0);
    }
    
    private static void displayServerIP() {
        try {
            java.net.InetAddress localHost = java.net.InetAddress.getLocalHost();
            System.out.println("- Adresse locale (LAN): " + localHost.getHostAddress());
            
            System.out.println("\nPour se connecter depuis le même ordinateur:");
            System.out.println("- Adresse: localhost:5555");
            
            System.out.println("\nPour se connecter depuis un autre ordinateur sur le même réseau:");
            System.out.println("- Adresse: " + localHost.getHostAddress() + ":5555");
            
            System.out.println("\nPour se connecter depuis Internet (nécessite une redirection de port):");
            System.out.println("- Obtenez votre IP publique en visitant https://whatismyip.com");
            System.out.println("- Configurez la redirection de port 5555 sur votre routeur");
            
        } catch (Exception e) {
            System.out.println("Impossible de déterminer l'adresse IP: " + e.getMessage());
        }
    }
}