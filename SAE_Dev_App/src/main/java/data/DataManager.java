package data;

import modele.Tache;
import java.util.ArrayList;

public class DataManager {

    private static DataManager instance;

    private ArrayList<Tache> taches;

    private DataManager() {
        this.taches = new ArrayList<>();

        taches.add(new Tache("Tache test", null, null));
    }

    public static DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    public ArrayList<Tache> getTaches() {
        return taches;
    }

    public void ajouterTache(Tache t) {
        taches.add(t);
    }
}