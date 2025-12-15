package vue;

import controleur.ControleurFX;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import modele.ModeleTache;
import modele.Priorite;
import modele.TacheAbstraite;
import modele.TacheMere;
import observateur.Observateur;
import observateur.Sujet;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class VueSemaine extends VBox implements Observateur {

    private ControleurFX controleur;
    private TreeView<Object> treeView;

    // Pour retrouver facilement le noeud "Lundi", "Mardi", etc.
    private Map<DayOfWeek, TreeItem<Object>> joursRacines;
    private TreeItem<Object> racineInvisible;

    // Champs du formulaire
    private TextField champTitre;
    private DatePicker datePicker;
    private ComboBox<Priorite> prioriteBox;

    public VueSemaine() {
        this.setPadding(new Insets(10));
        this.setSpacing(15);

        // 1. Initialisation Controleur & Modèle
        this.controleur = new ControleurFX();
        ModeleTache.getInstance().getRacine().enregistrerObservateur(this);

        // 2. Création de l'Arbre (Basé sur TreeDemo)
        initialiserArbre();

        // 3. Création du Formulaire (Identique à VueListe pour la cohérence)
        HBox formulaire = creerFormulaire();

        // 4. Gestion de la sélection (Clic sur l'arbre)
        treeView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.getValue() instanceof TacheAbstraite) {
                // Si on clique sur une TÂCHE, on remplit le formulaire
                TacheAbstraite t = (TacheAbstraite) newVal.getValue();
                remplirFormulaire(t);
            } else {
                // Si on clique sur un JOUR (String), on peut pré-remplir la date
                if (newVal != null && newVal.getValue() instanceof String) {
                    // Logique optionnelle : trouver la date du jour cliqué (ex: prochain Lundi)
                    // Pour l'instant on vide juste
                    champTitre.clear();
                }
            }
        });

        // Assemblage
        Label titreVue = new Label("Vue Semainier (Par Jours)");
        titreVue.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // On fait en sorte que l'arbre prenne toute la place disponible
        VBox.setVgrow(treeView, Priority.ALWAYS);

        this.getChildren().addAll(titreVue, formulaire, treeView);

        // Premier affichage
        actualiser(null);
    }

    private void initialiserArbre() {
        // Racine invisible qui contient tout
        racineInvisible = new TreeItem<>("Racine");
        treeView = new TreeView<>(racineInvisible);
        treeView.setShowRoot(false); // On cache la racine "technique"

        joursRacines = new HashMap<>();

        // Création des items pour chaque jour de la semaine
        for (DayOfWeek jour : DayOfWeek.values()) {
            String nomJour = jour.getDisplayName(TextStyle.FULL, Locale.FRANCE);
            // Majuscule
            nomJour = nomJour.substring(0, 1).toUpperCase() + nomJour.substring(1);

            TreeItem<Object> itemJour = new TreeItem<>(nomJour);
            itemJour.setExpanded(true); // Déplié par défaut

            racineInvisible.getChildren().add(itemJour);
            joursRacines.put(jour, itemJour);
        }

        // --- CELL FACTORY (Inspiré de TreeDemo) ---
        // C'est ici qu'on définit comment s'affiche une ligne selon si c'est un Jour ou une Tâche
        treeView.setCellFactory(new Callback<TreeView<Object>, TreeCell<Object>>() {
            @Override
            public TreeCell<Object> call(TreeView<Object> param) {
                return new TreeCell<Object>() {
                    @Override
                    protected void updateItem(Object item, boolean empty) {
                        super.updateItem(item, empty);

                        if (empty || item == null) {
                            setText(null);
                            setGraphic(null);
                            setStyle("");
                        } else {
                            if (item instanceof String) {
                                // C'est un JOUR (String)
                                setText((String) item);
                                setStyle("-fx-font-weight: bold; -fx-background-color: #e0e0e0;");
                            } else if (item instanceof TacheAbstraite) {
                                // C'est une TÂCHE
                                TacheAbstraite t = (TacheAbstraite) item;
                                setText(t.getTitre() + " (" + t.getPriorite() + ")");

                                // On peut changer la couleur selon la priorité
                                if (t.getPriorite() == Priorite.HAUTE) {
                                    setStyle("-fx-text-fill: red;");
                                } else {
                                    setStyle("");
                                }
                            }
                        }
                    }
                };
            }
        });
    }

    private HBox creerFormulaire() {
        champTitre = new TextField();
        champTitre.setPromptText("Titre de la tâche");

        datePicker = new DatePicker(LocalDate.now());

        prioriteBox = new ComboBox<>();
        prioriteBox.getItems().addAll(Priorite.values());
        prioriteBox.setValue(Priorite.BASSE);

        Button btnAjouter = new Button("Ajouter");
        Button btnModifier = new Button("Modifier");
        Button btnSupprimer = new Button("Supprimer");

        // ACTIONS
        btnAjouter.setOnAction(e -> {
            controleur.creerTache(champTitre.getText(), datePicker.getValue(), prioriteBox.getValue());
            champTitre.clear();
        });

        btnModifier.setOnAction(e -> {
            TreeItem<Object> selectedItem = treeView.getSelectionModel().getSelectedItem();
            if (selectedItem != null && selectedItem.getValue() instanceof TacheAbstraite) {
                controleur.modifierTache((TacheAbstraite) selectedItem.getValue(),
                        champTitre.getText(), datePicker.getValue(), prioriteBox.getValue());
                champTitre.clear();
                treeView.getSelectionModel().clearSelection();
            }
        });

        btnSupprimer.setOnAction(e -> {
            TreeItem<Object> selectedItem = treeView.getSelectionModel().getSelectedItem();
            if (selectedItem != null && selectedItem.getValue() instanceof TacheAbstraite) {
                controleur.supprimerTache((TacheAbstraite) selectedItem.getValue());
                champTitre.clear();
            }
        });

        HBox box = new HBox(10, champTitre, datePicker, prioriteBox, btnAjouter, btnModifier, btnSupprimer);
        return box;
    }

    private void remplirFormulaire(TacheAbstraite t) {
        champTitre.setText(t.getTitre());
        if (t.getDateLimite() != null) datePicker.setValue(t.getDateLimite());
        prioriteBox.setValue(t.getPriorite());
    }

    @Override
    public void actualiser(Sujet s) {
        // 1. On vide les enfants des jours (sans supprimer les jours eux-mêmes)
        for (TreeItem<Object> jourItem : joursRacines.values()) {
            jourItem.getChildren().clear();
        }

        // 2. On récupère les données
        TacheMere racineDonnees = ModeleTache.getInstance().getRacine();

        // 3. On trie les tâches dans les bons dossiers
        for (TacheAbstraite t : racineDonnees.getEnfants()) {
            LocalDate date = t.getDateLimite();

            // Logique de tri simple
            if (date != null) {
                DayOfWeek jourSemaine = date.getDayOfWeek();
                // On ajoute la tâche sous le bon jour
                joursRacines.get(jourSemaine).getChildren().add(new TreeItem<>(t));
            }
        }
    }
}