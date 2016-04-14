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
		((DeucesSolitaire)game).setWastePileCardCount(-1); // We have now removed a card to the WastePile and thus decrement its count
		// The move was successful so,
		return true;
	}

	@Override
	public boolean undo(Solitaire game) {
		// Validate the undo move
		if( destFoundationPile.peek().getRank() == Card.TWO )
			return false; // You shouldn't be able to remove the base card from the Foundation Pile
		// We want to get the top card of the FoundationPile and now add it back to the WastePile
		Card topOfTheFoundationPile = destFoundationPile.get(); // Grab the top of the FoundationPile
		wastePile.add(topOfTheFoundationPile); // Add the card from the top of the FoundtionPile to the WastePile now
		game.updateScore(-1); // A card was removed from the FoundationPile and added back to the WastePile and hence the score is decremented by one
		((DeucesSolitaire)game).setWastePileCardCount(1); // We have now undone the move and thus have to increment the count
		// The move was successful, so:
		return true;
	}

	@Override
	public boolean valid(Solitaire game) {
		assert( destFoundationPile.peek() != null ); // There must exist atleast one card in the Foundation Pile
		// If there is a top card in the Destination Foundation Pile (which should always be the case), then:
		// Rank check 1- We shouldn't be able to add a card over Ace
		if( destFoundationPile.peek().getRank() == Card.ACE )
			return false; // You can never place a card over a Foundation Pile that has a facing Ace up
		// Rank check 2- Here we check if the destination is King and then card being added to the Foundation is an ACE, then we only check for suit equality
		if( cardBeingDragged.getRank() == Card.ACE && destFoundationPile.peek().getRank() == Card.KING )
			return destFoundationPile.peek().getSuit() == cardBeingDragged.getSuit();
		// Else, we compare the ranks and the suits
		return (destFoundationPile.peek().getRank() == (cardBeingDragged.getRank() - 1)) &&  // The destination foundation pile's top card is one rank lower than the card being dragged from the WastePile
			   (destFoundationPile.peek().getSuit() == cardBeingDragged.getSuit());          // The destination FoundationPile is the same rank as the card being dragged from the WastePile
	}

}
