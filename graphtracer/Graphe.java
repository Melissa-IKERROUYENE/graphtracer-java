package graphtracer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

public class Graphe implements Serializable {

    private static final long serialVersionUID = 1L;
    private List<Tache> taches;
    private List<Arc> arcs;

    public Graphe() {
        taches = new ArrayList<>();
        arcs = new ArrayList<>();
        ajouterTachesSpeciales();
    }

    private void ajouterTachesSpeciales() {
        ajouterTache(new Tache("début", 0, 100, 100));
        ajouterTache(new Tache("fin", 0, 500, 300));
    }

    public void ajouterTache(Tache tache) {
        if (tache.getNom().equals("début") || tache.getNom().equals("fin")) {
            if (taches.stream().noneMatch(t -> t.getNom().equals(tache.getNom()))) {
                taches.add(tache);
            }
        } else {
            taches.add(tache);
        }
    }

    public void supprimerTache(Tache tache) {
        if (tache.getNom().equals("début") || tache.getNom().equals("fin")) {
            throw new IllegalArgumentException("Impossible de supprimer les tâches système");
        }

        new ArrayList<>(arcs).forEach(arc -> {
            if (arc.getOrigine() == tache || arc.getDestination() == tache) {
                supprimerArc(arc);
            }
        });

        taches.remove(tache);
    }

    public void ajouterArc(Arc arc) {
        Tache source = arc.getOrigine(), destination = arc.getDestination();
        // Le constructeur Arc inscrit déjà le lien dans les deux tâches.
        boolean invalide = source == null || destination == null
            || !taches.contains(source) || !taches.contains(destination)
            || source == destination || source.getNom().equals("fin")
            || destination.getNom().equals("début")
            || arcs.stream().anyMatch(a -> a.getOrigine() == source && a.getDestination() == destination)
            || accessible(destination, source);
        if (invalide) {
            if (source != null) source.supprimerArcSortant(arc);
            if (destination != null) destination.supprimerArcEntrant(arc);
            throw new IllegalArgumentException("Dépendance invalide : doublon, cycle ou tâche système.");
        }
        arcs.add(arc);
        synchroniserContraintes();
    }

    private boolean accessible(Tache source, Tache destination) {
        java.util.Set<Tache> vus = new java.util.HashSet<>();
        Queue<Tache> file = new LinkedList<>();
        file.add(source);
        while (!file.isEmpty()) {
            Tache t = file.remove();
            if (t == destination) return true;
            if (vus.add(t)) for (Arc a : arcs)
                if (a.getOrigine() == t) file.add(a.getDestination());
        }
        return false;
    }

    private void synchroniserContraintes() {
        for (Tache t : taches) {
            List<String> noms = new ArrayList<>();
            for (Arc a : arcs) if (a.getDestination() == t
                    && !a.getOrigine().getNom().equals("début"))
                noms.add(a.getOrigine().getNom());
            t.setTachesachevées(noms);
        }
    }

    public void renommerTache(Tache tache, String nom) {
        nom = nom.trim();
        if (nom.isEmpty() || nom.contains(",") || trouverTacheParNom(nom) != null)
            throw new IllegalArgumentException("Choisissez un nom unique, sans virgule.");
        tache.setNom(nom);
        synchroniserContraintes();
    }

    public void modifierContraintes(Tache tache, List<String> noms) {
        List<Tache> sources = new ArrayList<>();
        for (String nom : noms) {
            Tache source = trouverTacheParNom(nom.trim());
            if (source == null || source == tache || source.getNom().equals("fin")
                    || accessible(tache, source))
                throw new IllegalArgumentException("Prérequis inexistant ou dépendance circulaire : " + nom);
            if (!sources.contains(source)) sources.add(source);
        }
        if (sources.isEmpty()) sources.add(trouverTacheParNom("début"));
        for (Arc a : new ArrayList<>(arcs))
            if (a.getDestination() == tache) supprimerArc(a);
        for (Tache source : sources) ajouterArc(new Arc(source, tache));
    }

    public void supprimerArc(Arc arc) {
        arcs.remove(arc);
        arc.supprimer();
        synchroniserContraintes();
    }

    public Tache trouverTacheParNom(String nom) {
        return taches.stream()
                .filter(tache -> tache.getNom().equals(nom))
                .findFirst()
                .orElse(null);
    }

    public List<Tache> getTaches() { return new ArrayList<>(taches); }
    public List<Arc> getArcs() { return new ArrayList<>(arcs); }


    public void calculerCheminCritique() {
        // Les arcs sont la source de vérité, indépendamment des noms et de l'ordre de création.
        java.util.Map<Tache, Integer> degres = new java.util.HashMap<>();
        Queue<Tache> file = new LinkedList<>();
        List<Tache> ordre = new ArrayList<>();
        for (Tache t : taches) {
            if (t.getDuree() < 0 || t.getDuree() > Integer.MAX_VALUE)
                throw new IllegalArgumentException("Durée hors limites.");
            degres.put(t, 0);
        }
        for (Arc a : arcs) degres.put(a.getDestination(), degres.get(a.getDestination()) + 1);
        for (Tache t : taches) if (degres.get(t) == 0) file.add(t);
        while (!file.isEmpty()) {
            Tache t = file.remove();
            ordre.add(t);
            for (Arc a : arcs) if (a.getOrigine() == t) {
                Tache suivant = a.getDestination();
                degres.put(suivant, degres.get(suivant) - 1);
                if (degres.get(suivant) == 0) file.add(suivant);
            }
        }
        if (ordre.size() != taches.size())
            throw new IllegalArgumentException("Le graphe contient un cycle : calcul impossible.");
        for (Tache t : taches) t.setDateAuPlusTot(0);
        int dureeProjet = 0;
        for (Tache t : ordre) {
            long finTache = (long)t.getDateAuPlusTot() + t.getDuree();
            if (finTache > Integer.MAX_VALUE)
                throw new IllegalArgumentException("La durée totale dépasse la limite de calcul.");
            dureeProjet = Math.max(dureeProjet, (int)finTache);
            for (Arc a : arcs) if (a.getOrigine() == t) {
                Tache suivant = a.getDestination();
                suivant.setDateAuPlusTot(Math.max(suivant.getDateAuPlusTot(), (int)finTache));
            }
        }
        // Les tâches terminales se terminent au plus tard à la fin du projet.
        Tache fin = trouverTacheParNom("fin");
        fin.setDateAuPlusTot(dureeProjet);
        for (Tache t : taches) t.setDateAuPlusTard(dureeProjet - (int)t.getDuree());
        Collections.reverse(ordre);
        for (Tache t : ordre) {
            for (Arc a : arcs) if (a.getOrigine() == t)
                t.setDateAuPlusTard(Math.min(t.getDateAuPlusTard(),
                    a.getDestination().getDateAuPlusTard() - (int)t.getDuree()));
        }
        trouverTacheParNom("début").setDateAuPlusTard(0);
        for (Arc a : arcs) {
            Tache source = a.getOrigine(), destination = a.getDestination();
            boolean critique = source.estCritique() && destination.estCritique()
                && source.getDateAuPlusTot() + source.getDuree() == destination.getDateAuPlusTot();
            a.setCouleur(critique ? Color.RED : Color.BLACK);
        }
        synchroniserContraintes();
    }

    public void sauvegarder(String cheminFichier) throws java.io.IOException {
        if (!cheminFichier.toLowerCase().endsWith(".ser")) {
            cheminFichier += ".ser";
        }

        try (java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(
                new java.io.FileOutputStream(cheminFichier))) {
            oos.writeObject(this);
        }
    }

    public static Graphe charger(String cheminFichier) throws java.io.IOException, ClassNotFoundException {
        if (!cheminFichier.toLowerCase().endsWith(".ser")) {
            cheminFichier += ".ser";
        }
        try (java.io.ObjectInputStream ois = new java.io.ObjectInputStream(
                new java.io.FileInputStream(cheminFichier))) {
            return (Graphe) ois.readObject();
        }
    }
}