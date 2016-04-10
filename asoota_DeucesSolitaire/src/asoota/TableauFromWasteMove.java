package asoota;

import ks.common.games.Solitaire;
import ks.common.model.Card;
import ks.common.model.Column;
import ks.common.model.Move;

/**
 * Move a card from the top of WastePile to a Tableau Pile.
 * @author asoota
 */
public class TableauFromWasteMove extends Move {

	private Column wastePile;
	private Card cardBeingDragged;
	private Column destTableauPile;
	
	public TableauFromWasteMove(Column fromWastePile, Card cardBeingDragged, Column toTableauPile) {
		// Save all the parameters for future use
		this.wastePile = fromWastePile;
		this.cardBeingDragged = cardBeingDragged;
		this.destTableauPile = toTableauPile;
	}
	
	@Override
	public boolean doMove(Solitaire game) {
		assert(valid(game) == true); // This function can only be called if the TableauFromWasteMove is valid
		// If the move is valid, perform the move
		if( cardBeingDragged == null ) {
			Card topCardFromTheWastePile = wastePile.get(); // Get the top card of the WastePile that is being moved
			destTableauPile.add(topCardFromTheWastePile); // Add the card to the top of the destination TableauPile that was dragged out of the WastePile
		} else
			destTableauPile.add(cardBeingDragged); // Add the card that is being dragged right now
		// The move was successful so,
		return true;
	}

	@Override
	public boolean undo(Solitaire game) {
		// We want to get the top card of the TableauPile and now add it back to the WastePile
		Card topOfTheTableauPile = destTableauPile.get(); // Grab the top of the TableauPile
		wastePile.add(topOfTheTableauPile); // Add the card from the top of the TableauPile to the WastePile now
		// The move was successful, so:
		return true;
	}

	@Override
	public boolean valid(Solitaire game) {
		if( destTableauPile.peek() == null )
			return !wastePile.empty(); // If the destination Tableau Pile is empty, for a valid move a card must exist the WastePile
		// If there is a top card on the destination Tableau Pile, then:
		return (destTableauPile.peek().getRank() == (cardBeingDragged.getRank() + 1)) &&   // The tableau piles grow downwards. So the card being added should be one rank lower to the top card on the Tableau Pile
			   (destTableauPile.peek().getSuit() == cardBeingDragged.getSuit()) &&         // All the cards in the Tableau pile should be of the same suit. So this line will check that
			   !wastePile.empty();                                                         // Finally for a move from the WastePile to a TableauPile, the WastePile must have some cards. This line will check for that
	}

}
