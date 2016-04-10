package asoota;

import ks.common.games.Solitaire;
import ks.common.model.Card;
import ks.common.model.Column;
import ks.common.model.Move;
import ks.common.model.Pile;

/**
 * Move a card from the top of WastePile to the top of a FoudatioPile.
 * @author asoota
 */
public class FoundationFromWasteMove extends Move {

	private Column wastePile;
	private Pile destFoundationPile;
	private Card cardBeingDragged;
	
	public FoundationFromWasteMove(Column fromWastePile, Card cardBeingDragged, Pile toFoundationPile) {
		// Save all the parameters for future use
		this.wastePile = fromWastePile;
		this.cardBeingDragged = cardBeingDragged;
		this.destFoundationPile = toFoundationPile;
	}
	
	@Override
	public boolean doMove(Solitaire game) {
		assert(valid(game) == true); // This function can only be called if the FoundationFromWasteMove is valid
		// If the move is valid, perform the move
		if( cardBeingDragged == null ) {
			Card topCardFromTheWastePile = wastePile.get(); // Get the top card of the WastePile that is being moved
			destFoundationPile.add(topCardFromTheWastePile); // Add the card to the top of the destination FoundatioPile that was dragged out of the WastePile
		} else
			destFoundationPile.add(cardBeingDragged); // Add the card that is being dragged right now
		game.updateScore(1); // A card was moved to the FoundationPile and the score is now increased by one
		// The move was successful so,
		return true;
	}

	@Override
	public boolean undo(Solitaire game) {
		// We want to get the top card of the FoundationPile and now add it back to the WastePile
		Card topOfTheFoundationPile = destFoundationPile.get(); // Grab the top of the FoundationPile
		wastePile.add(topOfTheFoundationPile); // Add the card from the top of the FoundtionPile to the WastePile now
		game.updateScore(-1); // A card was removed from the FoundationPile and added back to the WastePile and hence the score is decremented by one
		// The move was successful, so:
		return true;
	}

	@Override
	public boolean valid(Solitaire game) {
		if( destFoundationPile.peek() == null )
			return !wastePile.empty(); // As there is no top card in the destination Foundation Pile, a valid move is determined if the WastePile is empty or not
		// If there is a top card in the Destination Foundation Pile, then check:
		return (destFoundationPile.peek().getRank() == (cardBeingDragged.getRank() - 1)) &&  // The destination foundation pile's top card is one rank lower than the card being dragged from the WastePile
			   (destFoundationPile.peek().sameSuit(cardBeingDragged)) &&                     // The destination FoundationPile is the same rank as the card being dragged from the WastePile
			   (!wastePile.empty());                                                         // The WastePile is not empty
	}

}
