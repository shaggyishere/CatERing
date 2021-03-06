package businesslogic.procedure;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ProcedureManager {

    public ProcedureManager() {
        Recipe.loadAllRecipes();
//        Procedure.loadAllProcedures();
    }

    public ObservableList<Recipe> getRecipes() {
        return FXCollections.unmodifiableObservableList(Recipe.getAllRecipes());
    }
}
