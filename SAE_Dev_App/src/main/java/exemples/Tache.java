package exemples;

public class Tache {
    private String nom;

    public Tache(String nom) {
        this.nom = nom;
    }

    public String getNom() {
        return nom;
    }

    @Override
    public String toString() {
        return "Tache{" +
                "nom='" + nom + '\'' +
                '}';
    }
}
