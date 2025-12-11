package modele;

import java.time.LocalDate;
import java.util.ArrayList;

public class Tache {
    private String titre;
    private String description;
    private LocalDate dateLimite;
    private Priorite priorite;
    private boolean estArchivee;

    private ArrayList<Tache> sousTaches;

    public Tache(String titre, LocalDate dateLimite, Priorite priorite) {
        this.titre = titre;
        this.dateLimite = dateLimite;
        this.priorite = priorite;
        this.estArchivee = false;
        this.sousTaches = new ArrayList<>();
    }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public LocalDate getDateLimite() { return dateLimite; }

    public Priorite getPriorite() { return priorite; }

    public ArrayList<Tache> getSousTaches() { return sousTaches; }
    public void ajouterSousTache(Tache t) { this.sousTaches.add(t); }
}