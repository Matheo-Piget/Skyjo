# CLAUDE.md - Base du projet Skyjo

Ce document sert de référence rapide pour contribuer au projet Skyjo.
Objectif: garder une architecture claire, un code cohérent, et un workflow simple pour build/test/livraison.

## 1) Objectif du projet

Skyjo est une implémentation Java/JavaFX du jeu de cartes Skyjo avec:
- logique de jeu complète (pioche, défausse, échange, révélation, score)
- mode local (joueurs humains + IA)
- mode en ligne (client/serveur)
- interface animée
- thèmes (clair/sombre) et options (volume, vitesse d'animation, mode)

## 2) Stack technique

- Java 21 (toolchain Gradle)
- JavaFX 21 (`controls`, `fxml`, `media`)
- Gradle Kotlin DSL
- JUnit 5
- Jackson Databind (sérialisation/désérialisation)
- Guava

Fichiers principaux:
- `settings.gradle.kts` (modules du build)
- `app/build.gradle.kts` (dépendances, tâches, packaging)

## 3) Architecture du code

Base package: `org.App`

Répartition actuelle:
- `org.App.model.game`: coeur métier (cartes, règles, exceptions)
- `org.App.model.player`: joueurs, IA et stratégies
- `org.App.controller`: orchestration du jeu local et en ligne
- `org.App.view.components`: composants UI réutilisables
- `org.App.view.screens`: écrans (menu, jeu, options, lobby)
- `org.App.view.utils`: utilitaires UI (musique, son, options)
- `org.App.network`: client, serveur, protocole, états réseau

Entrées applicatives:
- client graphique: `org.App.App`
- serveur: `org.App.ServerLauncher`

Ressources:
- `app/src/main/resources/themes`: feuilles de style
- `app/src/main/resources/musics`: musiques
- `app/src/main/resources/sounds`: effets sonores
- `app/src/main/resources/config.properties`: configuration utilisateur

## 4) Règles d'architecture

- Séparer strictement les responsabilités:
  - `model`: règles métier, sans dépendance JavaFX
  - `view`: rendu UI et interactions visuelles
  - `controller`: pont entre `model` et `view`
  - `network`: synchronisation et transport des états
- Eviter la logique métier dans les classes de vue.
- Eviter l'accès direct aux composants UI depuis `model`.
- Préférer des méthodes explicites dans les contrôleurs plutôt que des effets de bord implicites.
- En multi, toute action locale qui modifie l'état doit être validée et diffusée côté réseau.

## 5) Conventions de code

- Java:
  - classes/enums: `PascalCase`
  - méthodes/champs: `camelCase`
  - constantes: `UPPER_SNAKE_CASE`
  - package names: minuscules
- Utiliser `final` quand possible sur les variables locales non réassignées.
- Garder les méthodes courtes; extraire les blocs complexes.
- Javadoc:
  - obligatoire pour API publique non triviale
  - claire sur les invariants et préconditions
- Exceptions:
  - lancer des exceptions métier explicites (ex: `InvalidMoveException`)
  - ne pas avaler les exceptions silencieusement
- Logs:
  - messages actionnables (contexte, cause, impact)

## 6) Conventions UI / UX

- Conserver les styles dans `resources/themes`, pas en inline dans le code si évitable.
- Réutiliser les composants de `view/components` avant d'ajouter un nouveau widget.
- Préserver la cohérence clair/sombre pour chaque nouvel écran.
- Les animations doivent rester optionnelles et compatibles avec le réglage de vitesse.

## 7) Conventions IA

- Les stratégies (`EasyStrategy`, `MediumStrategy`, `HardStrategy`) ne doivent jamais lire d'informations cachées.
- Une stratégie prend des décisions uniquement depuis l'état observable autorisé.
- Toute nouvelle difficulté doit implémenter la même interface de stratégie existante.

## 8) Conventions réseau

- Centraliser les types de messages dans `Protocol`.
- Toute évolution du format réseau doit conserver la compatibilité de parsing.
- Les objets d'état réseau (`GameState`, `NetworkPlayerState`, `NetworkCardState`) doivent rester simples, sérialisables, et stables.

## 9) Build, test et exécution

Depuis la racine du repo:

Windows:
- `gradlew.bat build`
- `gradlew.bat test`
- `gradlew.bat run`
- `gradlew.bat :app:clientJar`
- `gradlew.bat :app:serverJar`

Linux/macOS:
- `./gradlew build`
- `./gradlew test`
- `./gradlew run`
- `./gradlew :app:clientJar`
- `./gradlew :app:serverJar`

Notes:
- la classe principale applicative est `org.App.App`
- la classe principale serveur est `org.App.ServerLauncher`

## 10) Stratégie de tests minimale

Priorité des tests:
- règles de score et fin de manche
- validation des coups invalides
- suppression/gestion de colonnes
- comportements IA (sans triche)
- sérialisation des états réseau critiques

Bonnes pratiques:
- tests déterministes (contrôle du hasard quand possible)
- un test = un comportement attendu
- nommer les tests selon le scénario et le résultat

## 11) Workflow de contribution

- Créer une branche par sujet (`feature/...`, `fix/...`).
- Faire des commits petits et ciblés.
- Avant PR/merge:
  - `build` et `test` verts
  - pas de régression visuelle évidente
  - pas de warnings critiques introduits

Template de message de commit conseillé:
- `feat: ...`
- `fix: ...`
- `refactor: ...`
- `test: ...`
- `docs: ...`

## 12) Guidelines pour futures évolutions

Quand tu ajoutes une fonctionnalité:
- définir d'abord l'impact sur `model` (règle métier)
- brancher ensuite le `controller`
- connecter enfin `view` + thème + son si nécessaire
- ajouter au moins un test métier correspondant

Quand tu modifies le protocole réseau:
- mettre à jour `Protocol`
- adapter les objets d'état concernés
- vérifier compatibilité client/serveur

---
Si le projet évolue (nouveaux modules, nouvelle structure), mettre à jour ce fichier en priorité pour garder une base fiable pour l'équipe et les agents IA.
