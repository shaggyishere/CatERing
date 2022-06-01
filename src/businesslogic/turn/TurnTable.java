package businesslogic.turn;

import java.util.List;

public class TurnTable {
    private List<Turn> turns;

	// DUBBIO: if (turns.contains(turn)) dovremmo aggiungerlo anche al DSD? Forse no, bu
	public void specifyTurnCompleteness(Turn turn, boolean isComplete) {
		if (turns.contains(turn))
			turn.setCompleteness(isComplete);
	}

	public List<Turn> getTurns() {
		// DUBBIO: ok così o dovremmo andarli a prendere dal db?
		// Perché loro con getRecipes() fanno:
		//		return FXCollections.unmodifiableObservableList(Recipe.getAllRecipes());
		// in cui getAllRecipes() è un metodo che prende le ricette caricate sul db (precedentemente)
		return turns;
	}
}
