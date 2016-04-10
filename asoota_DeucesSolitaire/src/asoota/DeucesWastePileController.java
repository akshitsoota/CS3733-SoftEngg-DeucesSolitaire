package asoota;

import java.awt.event.MouseEvent;

import ks.common.controller.SolitaireReleasedAdapter;
import ks.common.model.Column;
import ks.common.view.CardView;
import ks.common.view.Container;
import ks.common.view.RowView;

/**
 * Controls all actions with a WastePile on the DeucesSolitaire WastePiles
 * @author asoota
 */
public class DeucesWastePileController extends SolitaireReleasedAdapter {

	private DeucesSolitaire deucesGame;
	private RowView sourcePileView;

	public DeucesWastePileController(DeucesSolitaire theGame, RowView sourcePileView) {
		super(theGame); // Let the super do its job with the Solitaire game
		// Store the parameters passed to the controller
		this.deucesGame = theGame;
		this.sourcePileView = sourcePileView;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		super.mousePressed(e); // Let the Swing API handle the MouseEvent and then we'll handle it as well
		// We will be making changes to the Container so we need to grab that from the DeucesSolitaire object
		Container container = deucesGame.getContainer();
		// Get the Model Element linked to the PileVew to check if there is something to be dragged or not
		if( ((Column)sourcePileView.getModelElement()).count() == 0 ) {
			// There is nothing being dragged
			container.releaseDraggingObject();
			return; // Ignore the call to this function
		}
		// Check if the user actually clicked the top card of the WastePile
		CardView topCardView = sourcePileView.getCardViewForTopCard(e);
		if( topCardView == null ) {
			// The user didn't click the top of the card; Ignore the drag event and exit
			container.releaseDraggingObject();
			return; // Ignore the call to this function
		}
		// Assert that the card that's being dragged is the same one as being shown being dragged on the container
		assert(container.getActiveDraggingObject() == Container.getNothingBeingDragged());
		// Notify the container that the card is being dragged and should be associated with this MouseEvent
		container.setActiveDraggingObject(topCardView, e);
		// Tell the Container that who initiated the drag
		container.setDragSource(sourcePileView);

		sourcePileView.redraw(); // Invalidate the Source PileView
	}

}
