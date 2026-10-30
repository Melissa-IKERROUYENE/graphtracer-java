package graphtracer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.awt.Color;

// Classe représentant une tâche dans un graphe de dépendances
public class Tache implements Serializable {

    private static final long serialVersionUID = 1L;
    private String nom;
    private long duree;
    private int px, py;
    private Color couleur;
    private List<Arc> arcsSortants;
    private List<Arc> arcsEntrants;
    private List<String> tachesachevées;
    private int dateAuPlusTot;
    private int dateAuPlusTard;

    // Constructeur de la tâche
    public Tache(String nom, long duree, int px, int py) {
        this.nom = nom;
        setDuree(duree);
        this.px = px;
        this.py = py;
        this.couleur = Color.CYAN;
        this.arcsSortants = new ArrayList<>();
        this.arcsEntrants = new ArrayList<>();
        this.tachesachevées = new ArrayList<>();
        this.dateAuPlusTot = 0;
        this.dateAuPlusTard = Integer.MAX_VALUE;
    }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public long getDuree() { return duree; }
    public void setDuree(long duree) {
        if (duree < 0 || duree > Integer.MAX_VALUE)
            throw new IllegalArgumentException("La durée doit être un entier positif ou nul inférieur à 2147483648.");
        this.duree = duree;
    }
    public int getPx() { return px; }
    public void setPx(int px) { this.px = px; }
    public int getPy() { return py; }
    public void setPy(int py) { this.py = py; }
    public Color getCouleur() { return couleur; }
    public void setCouleur(Color couleur) { this.couleur = couleur; }
    public List<Arc> getArcsSortants() { return arcsSortants; }
    public List<Arc> getArcsEntrants() { return arcsEntrants; }
    public int getDateAuPlusTot() { return dateAuPlusTot; }
    public void setDateAuPlusTot(int date) { this.dateAuPlusTot = date; }
    public int getDateAuPlusTard() { return dateAuPlusTard; }
    public void setDateAuPlusTard(int date) { this.dateAuPlusTard = date; }

    // Vérifie si la tâche fait partie du chemin critique
    public boolean estCritique() {
        return dateAuPlusTot == dateAuPlusTard;
    }

    // Ajoute un arc sortant à la tâche
    public void ajouterArcSortant(Arc arc) {
        if (!arcsSortants.contains(arc)) arcsSortants.add(arc);
    }

    // Ajoute un arc entrant à la tâche
    public void ajouterArcEntrant(Arc arc) {
        if (!arcsEntrants.contains(arc)) arcsEntrants.add(arc);
    }

    // Supprime un arc sortant de la tâche
    public void supprimerArcSortant(Arc arc) {
        arcsSortants.remove(arc);
    }

    public void supprimerArcEntrant(Arc arc) {
        arcsEntrants.remove(arc);
    }

    public List<String> getTachesachevées() {
        return tachesachevées;
    }

    public void setTachesachevées(List<String> contraintes) {
        if (contraintes != null) {
            this.tachesachevées = contraintes;
        } else {
            this.tachesachevées = new ArrayList<>();
        }
    }

    // Met à jour les arcs entrants selon la liste tachesachevées
    public void mettreajourarcs(Graphe graphe) {
        graphe.modifierContraintes(this, new ArrayList<>(tachesachevées));
    }
}
