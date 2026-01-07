package modele;

import java.io.*;
import java.util.List;


public class GestionnaireSauvegarde {

    private static final String FICHIER_DEFAUT = "projets.bin";


    public static void sauvegarder() {
        sauvegarder(FICHIER_DEFAUT);
    }


    public static void sauvegarder(String cheminFichier) {
        try {
            // Récupération des projets
            List<Projet> projets = SingletonTache.getInstance().getMesProjets();

            // Nettoyage des observateurs avant sérialisation
            nettoyerObservateurs(projets);

            // Sérialisation
            try (ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream(cheminFichier))) {
                oos.writeObject(projets);
            }

            System.out.println("Sauvegarde réussie : " + projets.size() + " projet(s) sauvegardé(s)");

        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde : " + e.getMessage());
            e.printStackTrace();
        }
    }


    @SuppressWarnings("unchecked")
    public static void charger() {
        charger(FICHIER_DEFAUT);
    }


    @SuppressWarnings("unchecked")
    public static void charger(String cheminFichier) {
        File fichier = new File(cheminFichier);

        if (!fichier.exists()) {
            System.out.println("Aucun fichier de sauvegarde trouvé. Démarrage avec projet par défaut.");
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(cheminFichier))) {

            // Désérialisation
            List<Projet> projets = (List<Projet>) ois.readObject();

            // Réinitialisation du singleton avec les projets chargés
            SingletonTache.getInstance().reinitialiserAvecProjets(projets);

            System.out.println("Chargement réussi : " + projets.size() + " projet(s) chargé(s)");

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erreur lors du chargement : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void nettoyerObservateurs(List<Projet> projets) {
        // Nettoyage du singleton
        SingletonTache.getInstance().viderObservateurs();

        // Nettoyage de chaque projet
        for (Projet projet : projets) {
            projet.viderObservateurs();

            // Nettoyage des colonnes
            for (Colonne colonne : projet.getColonnes()) {
                colonne.viderObservateurs();

                // Nettoyage des tâches (récursif)
                for (TacheMere tache : colonne.getTaches()) {
                    nettoyerTacheRecursive(tache);
                }
            }
        }
    }


    private static void nettoyerTacheRecursive(TacheAbstraite tache) {
        tache.viderObservateurs();

        if (tache instanceof TacheMere) {
            TacheMere tacheMere = (TacheMere) tache;
            for (TacheAbstraite enfant : tacheMere.getEnfants()) {
                nettoyerTacheRecursive(enfant);
            }
        }
    }


    public static void supprimerSauvegarde() {
        supprimerSauvegarde(FICHIER_DEFAUT);
    }


    public static void supprimerSauvegarde(String cheminFichier) {
        File fichier = new File(cheminFichier);
        if (fichier.exists()) {
            if (fichier.delete()) {
                System.out.println("Sauvegarde supprimée : " + cheminFichier);
            } else {
                System.err.println("Impossible de supprimer la sauvegarde");
            }
        }
    }


    public static boolean sauvegardeExiste() {
        return sauvegardeExiste(FICHIER_DEFAUT);
    }


    public static boolean sauvegardeExiste(String cheminFichier) {
        return new File(cheminFichier).exists();
    }
}