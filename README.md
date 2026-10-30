# GraphTracer — Planification de projets par graphes

Application Java Swing permettant de représenter les tâches d’un projet et leurs dépendances sous forme de graphe orienté. Elle calcule les dates au plus tôt et au plus tard, la durée totale du projet et met en évidence les chemins critiques.

## Fonctionnalités

- Création, renommage, déplacement et suppression des tâches.
- Attribution et modification de la durée de chaque tâche.
- Gestion des dépendances par saisie des prérequis ou ajout d’arcs.
- Calcul de l’ordonnancement et mise en évidence des tâches et arcs critiques en rouge.
- Vérification des dépendances : refus des cycles, des doublons et des références inexistantes.
- Personnalisation des couleurs des tâches et des arcs.
- Sauvegarde et chargement des graphes par sérialisation Java (`.ser`).
- Impression du graphe avec adaptation à la page.

## Technologies et conception

**Java 17 · Swing · AWT · Programmation orientée objet · Théorie des graphes · Sérialisation Java**

Le modèle repose sur les classes `Graphe`, `Tache` et `Arc`. `FenetrePrincipale` et `EditeurDeGraphe` gèrent les fenêtres et les interactions ; `ZoneDeGraphe` dessine le graphe. Le calcul des dates parcourt les tâches dans un ordre topologique, puis dans l’ordre inverse pour déterminer les dates au plus tard.

## Lancement

Installer **Java 17 ou supérieur**, puis ouvrir un terminal dans le dossier du projet :

```bash
java -jar GraphTracer.jar
```

Le JAR contient les classes compilées et les images nécessaires. Aucune dépendance externe n’est requise. L’application nécessite un environnement graphique.

Pour compiler et lancer les sources avec un JDK 17 ou supérieur, depuis ce même dossier :

```bash
javac -encoding UTF-8 graphtracer/*.java
java graphtracer.GraphTracer
```

## Utilisation

1. Cliquer sur **Créer un graphe**. Les sommets `début` et `fin` sont présents dès la création.
2. Dans **Tâche → Ajouter**, saisir un nom unique, une durée entière positive ou nulle et les noms des prérequis séparés par des virgules. Laisser les prérequis vides pour une tâche sans dépendance.
3. Déplacer les tâches à la souris. Cliquer sur une tâche pour la sélectionner ; pour sélectionner un arc, cliquer près de son milieu.
4. Utiliser les menus **Tâche** et **Arc** pour modifier le projet. Les tâches sans prérequis sont reliées à `début` lors de leur création. Les tâches terminales peuvent être reliées à `fin` via **Arc → Ajouter** pour compléter la représentation visuelle.
5. Choisir **Calcul → Calculer chemin critique**. La durée totale est affichée et les chemins critiques sont surlignés. Recalculer après une modification du projet.
6. Utiliser **Fichier → Enregistrer / Ouvrir** pour conserver ou restaurer le graphe.

Toutes les durées doivent utiliser la même unité (par exemple des jours). Le calcul porte sur l’ensemble des tâches, y compris les tâches terminales non reliées visuellement à `fin`. La limite de la durée totale est de 2 147 483 647 unités. Le calcul réinitialise les couleurs des arcs pour distinguer les arcs critiques.

### Exemple rapide

Créer **A** de durée **2**, puis **B** de durée **3** avec **A** comme prérequis. Relier **B** à **fin**, puis lancer le calcul : la durée totale attendue est **5** et le chemin `début → A → B → fin` est critique.

## Organisation des fichiers

- `GraphTracer.jar` : application exécutable.
- `graphtracer/` : les neuf fichiers sources Java.
- `graphtracer/img/` : les trois images utilisées par l’interface, également intégrées au JAR.
- `README.md` : présentation et instructions.

## Sauvegardes

Les fichiers `.ser` conservent les tâches, les durées, les positions, les couleurs et les dépendances. N’ouvrir que des sauvegardes provenant de sources de confiance.

## Contexte et auteurs

Projet universitaire réalisé par **Melissa IKERROUYENE** et **Hocine ABDELAZIZ**.
