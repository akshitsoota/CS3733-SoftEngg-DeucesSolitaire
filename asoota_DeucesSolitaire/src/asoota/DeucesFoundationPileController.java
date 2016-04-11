package asoota;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import ks.common.model.Card;
import ks.common.model.Column;
import ks.common.model.MutableInteger;
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

	private MutableInteger wastePileNumLeft;
	private DeucesSolitaire theGame;
	private PileView pileView;

	public DeucesFoundationPileController(DeucesSolitaire deucesSolitaire, PileView pileView, MutableInteger wastePileNumLeft) {
		super(); // Let the super do its job
		// Store the parameters sent to the constructor
		this.wastePileNumLeft = wastePileNumLeft;
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
		// Check for null pointers
		if( container.getDragSource().getModelElement() == null || container.getDragSource().getModelElement().getName() == null )
			return;
		// Now, attempt to resolve the source widget now
		if( container.getDragSource().getModelElement().getName().equals("DeucesSolitaire-WastePile") ) {
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
			FoundationFromWasteMove theMove = new FoundationFromWasteMove(wastePile, cardBeingDragged, foundationPile, wastePileNumLeft);
			// Try to see if the move is valid or not
			if( theMove.valid(theGame) && theMove.doMove(theGame) ) {
				// The move is valid and can be executed
				theGame.pushMove(theMove); // Make the move appear on the UI
				theGame.refreshWidgets(); // Invalidate all the widgets on the GUI
			} else {
				// The move is invalid and we've to send the card back from where it came
				wastePileView.returnWidget(cardBeingDraggedView); // Make sure the card being dragged goes back to the WastePile as the move was invalid
			}
			// Finally, release the dragging object to prevent things from getting weird
			container.releaseDraggingObject();
			// Get the container to repaint everything
			container.repaint();
		} else if( container.getDragSource().getModelElement().getName().startsWith("DeucesSolitaire-Column") ) {
			// We know the source of the drag is DeucesSolitaire-Pile{%d}; Resolve the source ColumnView (TableauPile) fields
			ColumnView sourceTableauPile = (ColumnView)container.getDragSource();
			Column sourceTableau = (Column)sourceTableauPile.getModelElement();
			// Resolve for the Card/Column being dragged
			ColumnView cardsViewBeingDragged = (ColumnView)container.getActiveDraggingObject(); // Because we've extracted a Column from the Container, we do know this will be a Column but may container one or more cards
			Column cardsBeingDragged = (Column)cardsViewBeingDragged.getModelElement();
			// Resolve for the Destination FoundationPile fields
			PileView destinationFoundationPile = pileView;
			Pile theFoundationPile = (Pile)destinationFoundationPile.getModelElement();
			// Create the move
			FoundationFromTableauMove theMove = new FoundationFromTableauMove(sourceTableau, cardsBeingDragged, theFoundationPile);
			// Try to see if the move is valid and if that is the case, perform the move
			if( theMove.valid(theGame) && theMove.doMove(theGame) ) {
				// We were able to execute the move on the board
				theGame.pushMove(theMove); // Push the changes to the board
				theGame.refreshWidgets(); // Invalidate all the widgets on the UI
			} else {
				// The move is not possible and hence the card/cards have to go back to the source
				// As this function can take in a Widget, we can just send that in. We don't need to be worried if the Widget is a Card/Column of Cards
				sourceTableauPile.returnWidget(cardsViewBeingDragged);
			}
			// Finally, release the dragging object to prevent things from getting weird
			container.releaseDraggingObject();
			// Get the container to repaint everything
			container.repaint();
		}
	}

}
