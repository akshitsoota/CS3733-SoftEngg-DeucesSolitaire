package asoota;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import ks.common.model.Card;
import ks.common.model.Column;
import ks.common.view.CardView;
import ks.common.view.ColumnView;
import ks.common.view.Container;
import ks.common.view.RowView;

public class DeucesTableauPileController extends MouseAdapter {

	private DeucesSolitaire deucesGame;
	private ColumnView sourceTableauView;

	public DeucesTableauPileController(DeucesSolitaire deucesSolitaire, ColumnView columnView) {
		// Store the parameters passed to the controller
		this.deucesGame = deucesSolitaire;
		this.sourceTableauView = columnView;
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		super.mousePressed(e); // Let the Swing API handle the MouseEvent and then we'll handle it as well
		// We will be making changes to the Container so we need to grab that from the DeucesSolitaire object
		Container container = deucesGame.getContainer();
		// Get the Model Element linked to the ColumnView to check if there is something to be dragged or not
		if( ((Column)sourceTableauView.getModelElement()).count() == 0 ) {
			// There is nothing being dragged
			container.releaseDraggingObject();
			return; // Ignore the call to this function
		}
		// Check if the user actually clicked the top card of the TableauPile
		ColumnView cardsBeingDragged = sourceTableauView.getColumnView(e);
		if( cardsBeingDragged == null ) {
			// The user didn't click the top of the card; Ignore the drag event and exit
			container.releaseDraggingObject();
			return; // Ignore the call to this function
		}
		// Assert that the card that's being dragged is the same one as being shown being dragged on the container
		assert(container.getActiveDraggingObject() == Container.getNothingBeingDragged());
		// Notify the container that the card is being dragged and should be associated with this MouseEvent
		container.setActiveDraggingObject(cardsBeingDragged, e);
		// Tell the Container that who initiated the drag
		container.setDragSource(sourceTableauView);

		sourceTableauView.redraw(); // Invalidate the Source ColumnView
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		// Get the container to make UI updates
		Container container = deucesGame.getContainer();
		// Check if anything is being dragged around the container or not
		assert(container.getActiveDraggingObject() != Container.getNothingBeingDragged());
		// Get the source widget now
		assert(container.getDragSource() != null);
		// Check for null pointers
		if( container.getDragSource() == null || container.getDragSource().getModelElement() == null || container.getDragSource().getModelElement().getName() == null )
			return;
		// Now, attempt to resolve the source widget now
		if( container.getDragSource().getModelElement().getName().equals("DeucesSolitaire-WastePile") ) { // TODO: Check if valid way of checking the model that is giving the card
			// We know the source of the drag is the DeucesSolitaire-WastePile; Resolve for the WastePile fields
			RowView wastePileView = (RowView)container.getDragSource();
			Column wastePile = (Column)wastePileView.getModelElement();
			// Resolve for the PileView fields
			ColumnView tableauPileView = sourceTableauView;
			Column tableauPile = (Column)tableauPileView.getModelElement();
			// Resolve the card being dragged ke fields
			CardView cardBeingDraggedView = (CardView)container.getActiveDraggingObject();
			Card cardBeingDragged = (Card)cardBeingDraggedView.getModelElement();
			// Create the move
			TableauFromWasteMove theMove = new TableauFromWasteMove(wastePile, cardBeingDragged, tableauPile);
			// Try to see if the move is valid or not
			if( theMove.valid(deucesGame) && theMove.doMove(deucesGame) ) {
				// The move is valid and can be executed
				deucesGame.pushMove(theMove); // Make the move appear on the UI
				deucesGame.refreshWidgets(); // Invalidate all the widgets on the GUI
			} else {
				// The move is invalid and we've to send the card back from where it came
				wastePileView.returnWidget(cardBeingDraggedView); // Make sure the card being dragged goes back to the WastePile as the move was invalid
			}
			// Finally, release the dragging object to prevent things from getting weird
			container.releaseDraggingObject();
			// Get the container to repaint everything
			container.repaint();
		} else if( container.getDragSource().getModelElement().getName().startsWith("DeucesSolitaire-Column") ) {
			// We know the source of the drag is DeucesSolitaire-Pile{%d}; Resolve the source PileView fields
			ColumnView sourceTableau_ColumnView = (ColumnView)container.getDragSource();
			Column sourceTableau = (Column)sourceTableau_ColumnView.getModelElement();
			// Resolve the destination PileView fields
			ColumnView destTableau_ColumnView = sourceTableauView;
			Column destTableau = (Column)destTableau_ColumnView.getModelElement();
			// Resolve the card being dragged ke fields
			ColumnView cardsKaColumnViewBeingDragged = (ColumnView)container.getActiveDraggingObject();
			Column cardsBeingDragged = (Column)cardsKaColumnViewBeingDragged.getModelElement();
			// Now, create the move
			TableauFromTableauMove theMove = new TableauFromTableauMove(sourceTableau, cardsBeingDragged, destTableau);
			// Try to see if the move is valid or not
			if( theMove.valid(deucesGame) && theMove.doMove(deucesGame) ) {
				// The move is valid and can be executed
				deucesGame.pushMove(theMove); // Make the move on the Solitaire Board
				deucesGame.refreshWidgets(); // Invalidate all the Widgets on the UI
			} else {
				// The move is invalid and we've to send the card back from where it came
				sourceTableau_ColumnView.returnWidget(cardsKaColumnViewBeingDragged); // Make sure the card being dragged goes back to the source TargetPile as the move was invalid
			}
			// Finally, release the dragging object to prevent things from getting weird
			container.releaseDraggingObject();
			// Get the container to repaint everything
			container.repaint();
		}
	}

}