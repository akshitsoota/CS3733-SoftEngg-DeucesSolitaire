package asoota;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import ks.common.model.Card;
import ks.common.model.Column;
import ks.common.model.Pile;
import ks.common.view.CardView;
import ks.common.view.ColumnView;
import ks.common.view.Container;
import ks.common.view.PileView;
import ks.common.view.RowView;

/**
 * Controls all actions with a FoundationPile on the DeucesSolitaire FoundationPiles
 * @author asoota
 */
public class DeucesFoundationPileController extends MouseAdapter {

	private DeucesSolitaire theGame;
	private PileView pileView;

	public DeucesFoundationPileController(DeucesSolitaire deucesSolitaire, PileView pileView) {
		super(); // Let the super do its job
		// Store the parameters sent to the constructor
		this.theGame = deucesSolitaire;
		this.pileView = pileView;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// Get the container to make UI updates
		Container container = theGame.getContainer();
		// Check if anything is being dragged around the container or not
		assert(container.getActiveDraggingObject() != Container.getNothingBeingDragged());
		// Get the source widget now
		assert(container.getDragSource() != null);
		// Now, attempt to resolve the source widget now
		if( container.getDragSource() != null && container.getDragSource().getModelElement().getName().equals("DeucesSolitaire-WastePile") ) {
			// We know the source of the drag is the DeucesSolitaire-WastePile; Resolve for the WastePile fields
			RowView wastePileView = (RowView)container.getDragSource();
			Column wastePile = (Column)wastePileView.getModelElement();
			// Resolve for the PileView fields
			PileView foundationPileView = pileView;
			Pile foundationPile = (Pile)foundationPileView.getModelElement();
			// Resolve the card being dragged ke fields
			CardView cardBeingDraggedView = (CardView)container.getActiveDraggingObject();
			Card cardBeingDragged = (Card)cardBeingDraggedView.getModelElement();
			// Create the move
			FoundationFromWasteMove theMove = new FoundationFromWasteMove(wastePile, cardBeingDragged, foundationPile);
			// Try to see if the move is valid or not
			if( theMove.valid(theGame) && theMove.doMove(theGame) ) {
				// The move is valid and can be executed
				theGame.pushMove(theMove); // Make the move appear on the UI
				theGame.refreshWidgets(); // Invalidate all the widgets on the GUI
			} else {
				// The move is invalid and we've to send the card back from where it came
				wastePileView.returnWidget(cardBeingDraggedView); // Make sure the card being dragged goes back to the WastePile as the move was invalid
			}
			// Finally, release the dragging object to prevent things from getting wierd
			container.releaseDraggingObject();
			// Get the container to repaint everything
			container.repaint();
		}
	}

}
