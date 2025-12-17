package modele;

public enum Etat {
    A_FAIRE("À Faire"),
    EN_COURS("En Cours"),
    TERMINE("Terminé");

    private String label;

    Etat(String label) {
        this.label = label;
    }

    public String toString() {
        return label;
    }
}