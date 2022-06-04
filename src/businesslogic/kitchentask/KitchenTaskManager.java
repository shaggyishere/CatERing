package businesslogic.kitchentask;

import businesslogic.CatERing;
import businesslogic.UseCaseLogicException;
import businesslogic.event.EventInfo;
import businesslogic.event.ServiceInfo;
import businesslogic.procedure.Procedure;
import businesslogic.turn.Turn;
import businesslogic.turn.TurnManager;
import businesslogic.turn.TurnTable;
import businesslogic.user.Cook;
import businesslogic.user.User;

import java.util.ArrayList;
import java.util.List;

public class KitchenTaskManager {
	private KitchenSheet currentSheet;
	private List<KitchenTaskEventReceiver> eventReceivers;

	public KitchenTaskManager() {
		this.eventReceivers = new ArrayList<>();
	}


	public KitchenSheet createKitchenSheet(String title, EventInfo event, ServiceInfo service) throws UseCaseLogicException {
		User user = CatERing.getInstance().getUserManager().getCurrentUser();

		// come mai non ti convince questa sotto?
        if (!user.isChef() ||
                !event.getServices().contains(service) ||
				service.getUsedMenu() == null ||
				!user.getAssignedEvents().contains(event)) {
            throw new UseCaseLogicException();
        }
		KitchenSheet sheet = new KitchenSheet(title, service);
		this.setCurrentSheet(sheet);
		this.notifySheetCreated(sheet);

		return sheet;
	}

	public KitchenSheet chooseKitchenSheet(KitchenSheet sheet, EventInfo event, ServiceInfo service) throws UseCaseLogicException{
		User user = CatERing.getInstance().getUserManager().getCurrentUser();
        if (!user.isChef() ||
                !event.getServices().contains(service) ||
                !user.getAssignedEvents().contains(event) ||
                service.getUsedMenu() == null) {
            throw new UseCaseLogicException();
        }
		this.setCurrentSheet(sheet);

		return sheet;
	}

	public KitchenTask addKitchenTask (Procedure procedure) throws UseCaseLogicException {
		if (currentSheet == null)
			throw new UseCaseLogicException();
		KitchenTask task = this.currentSheet.addKitchenTask(procedure);
		this.notifyTaskAdded(task);

		return task;
	}

	public void deleteKitchenTask(KitchenTask task) throws UseCaseLogicException{
		if (currentSheet == null || !this.currentSheet.getKitchenTasks().contains(task))
			throw new UseCaseLogicException();
		this.currentSheet.deleteKitchenTask(task);
		this.notifyTaskDeleted(task);
	}

	public void restoreOriginalTasks() throws UseCaseLogicException{
		if (currentSheet == null)
			throw new UseCaseLogicException();
		this.currentSheet.restoreOriginalTask();
		this.notifySheetRestored();
	}

	public void moveTask(KitchenTask task, int position) throws UseCaseLogicException {
		if (currentSheet == null)
			throw new UseCaseLogicException();
		if (position < 0 || position >= currentSheet.getKitchenTasks().size())
			throw new IllegalArgumentException();
		this.currentSheet.moveTask(task, position);
		this.notifyTasksRearranged(task, position);
	}

	public void assignTask(KitchenTask task, Turn turn, Cook cook, String timing, String quantity) throws UseCaseLogicException {
		if (this.currentSheet == null || ! this.currentSheet.getKitchenTasks().contains(task))
			throw new UseCaseLogicException();
		this.currentSheet.assignTask(task, turn, cook, timing, quantity);
		this.notifyTaskAssigned(task);
	}

	public void assignTask(KitchenTask task) throws UseCaseLogicException {
		this.assignTask(task, null, null, null, null);
	}

	public void assignTask(KitchenTask task, Turn turn) throws UseCaseLogicException{
		this.assignTask(task, turn, null, null, null);
	}

	public void assignTask(KitchenTask task, Cook cook) throws UseCaseLogicException{
		this.assignTask(task, null, cook, null, null);
	}

	public void assignTaskTiming(KitchenTask task, String timing) throws UseCaseLogicException{
		this.assignTask(task, null, null, timing, null);
	}

	public void assignTaskQuantity(KitchenTask task, String quantity) throws UseCaseLogicException{
		this.assignTask(task, null, null, null, quantity);
	}

	public void assignTask(KitchenTask task, Turn turn, Cook cook) throws UseCaseLogicException{
		this.assignTask(task, turn, cook, null, null);
	}

	public void assignTaskTiming(KitchenTask task, Turn turn, String timing) throws UseCaseLogicException{
		this.assignTask(task, turn, null, timing, null);
	}

	public void assignTaskQuantity(KitchenTask task, Turn turn, String quantity) throws UseCaseLogicException{
		this.assignTask(task, turn, null, null, quantity);
	}

	public void assignTaskTiming(KitchenTask task, Cook cook, String timing) throws UseCaseLogicException{
		this.assignTask(task, null, cook, timing, null);
	}

	public void assignTaskQuantity(KitchenTask task, Cook cook, String quantity) throws UseCaseLogicException{
		this.assignTask(task, null, cook, null, quantity);
	}

	public void assignTask(KitchenTask task, String timing, String quantity) throws UseCaseLogicException{
		this.assignTask(task, null, null, timing, quantity);
	}

	public void assignTaskTiming(KitchenTask task, Turn turn, Cook cook, String timing) throws UseCaseLogicException{
		this.assignTask(task, turn, cook, timing, null);
	}

	public void assignTaskQuantity(KitchenTask task, Turn turn, Cook cook, String quantity) throws UseCaseLogicException{
		this.assignTask(task, turn, cook, null, quantity);
	}

	public void assignTask(KitchenTask task, Turn turn, String timing, String quantity) throws UseCaseLogicException{
		this.assignTask(task, turn, null, timing, quantity);
	}

	public void assignTask(KitchenTask task, Cook cook, String timing, String quantity) throws UseCaseLogicException{
		this.assignTask(task, null, cook, timing, quantity);
	}

	public void specifyCompletedTask(KitchenTask task) throws UseCaseLogicException {
		if (this.currentSheet == null || ! this.currentSheet.getKitchenTasks().contains(task))
			throw new UseCaseLogicException();
		this.currentSheet.specifyCompletedTask(task);
		this.notifyTaskCompleted(task);
	}

	public void specifyTurnCompleteness(Turn turn, boolean isComplete){
		TurnManager turnMgr = CatERing.getInstance().getTurnManager();
		turnMgr.specifyTurnCompleteness(turn, isComplete);
		this.notifyTurnCompletenessChanged(turn, isComplete);
	}

	private void notifySheetCreated(KitchenSheet sheet) {
		for (KitchenTaskEventReceiver eventReceiver : eventReceivers) {
			eventReceiver.updateSheetCreated(sheet);
		}
	}

	private void notifyTaskAdded(KitchenTask task){
		for (KitchenTaskEventReceiver eventReceiver : eventReceivers) {
			eventReceiver.updateTaskAdded(this.currentSheet, task);
		}
	}

	private void notifyTaskDeleted(KitchenTask task){
		for (KitchenTaskEventReceiver eventReceiver : eventReceivers) {
			eventReceiver.updateTaskDeleted(task);
		}
	}

	private void notifySheetRestored(){
		for (KitchenTaskEventReceiver eventReceiver : eventReceivers) {
			eventReceiver.updateSheetRestored(this.currentSheet);
		}
	}

	private void notifyTasksRearranged(KitchenTask task, int position){
		for (KitchenTaskEventReceiver eventReceiver : eventReceivers) {
			eventReceiver.updateTasksRearranged(this.currentSheet);
		}
	}

	private void notifyTaskAssigned(KitchenTask task){
		for (KitchenTaskEventReceiver eventReceiver : eventReceivers) {
			eventReceiver.updateTaskAssigned(this.currentSheet, task);
		}
	}

	private void notifyTaskCompleted(KitchenTask task){
		for (KitchenTaskEventReceiver eventReceiver : eventReceivers) {
			eventReceiver.updateTaskCompleted(this.currentSheet, task);
		}
	}

	private void notifyTurnCompletenessChanged(Turn turn, boolean isComplete){
		for (KitchenTaskEventReceiver eventReceiver : eventReceivers) {
			eventReceiver.updateTurnCompleteness(this.currentSheet, turn, isComplete);
		}
	}


	public KitchenSheet getCurrentSheet() {
		return currentSheet;
	}

	// non so se servirà, nel caso alla fine possiamo poi toglierlo
	public List<KitchenTaskEventReceiver> getEventReceivers() {
		return eventReceivers;
	}

	public void setCurrentSheet(KitchenSheet currentSheet) {
		this.currentSheet = currentSheet;
	}

	public void addEventReceiver(KitchenTaskEventReceiver rec) {
		this.eventReceivers.add(rec);
	}

	public void removeEventReceiver(KitchenTaskEventReceiver rec) {
		this.eventReceivers.remove(rec);
	}
}
