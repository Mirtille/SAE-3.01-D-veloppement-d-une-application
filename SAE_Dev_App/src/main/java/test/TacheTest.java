package test;

import modele.Priorite;
import modele.SousTache;
import modele.TacheMere;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class TacheTest {

    private TacheMere projetPrincipal;
    private SousTache tacheSimple;

    @BeforeEach
    void setUp() {
        projetPrincipal = new TacheMere("Projet S3", LocalDate.now().plusDays(10), Priorite.HAUTE);
        tacheSimple = new SousTache("Rédiger rapport", LocalDate.now().plusDays(2), Priorite.MOYENNE);
    }

    @Test
    void testCreationTacheSimple() {
        assertNotNull(tacheSimple);
        assertEquals("Rédiger rapport", tacheSimple.getTitre());
        assertEquals(Priorite.MOYENNE, tacheSimple.getPriorite());
    }

    @Test
    void testAjoutEnfantDansComposite() {
        assertTrue(projetPrincipal.getEnfants().isEmpty(), "Le projet devrait être vide au départ");

        // Action : Ajouter une sous-tâche
        projetPrincipal.ajouterEnfant(tacheSimple);

        // Vérification
        assertEquals(1, projetPrincipal.getEnfants().size(), "Le projet devrait contenir 1 tâche");
        assertEquals(tacheSimple, projetPrincipal.getEnfants().get(0), "La tâche ajoutée doit être retrouvée");
    }
}