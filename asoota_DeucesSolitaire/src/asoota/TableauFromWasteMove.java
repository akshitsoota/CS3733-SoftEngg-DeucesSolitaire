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
		destTableauPile.add(cardBeingDragged); // Add the card that is being dragged right now
		((DeucesSolitaire)game).setWastePileCardCount(-1); // We have now removed a card to the WastePile and thus decrement its count
		// The move was successful so,
		return true;
	}

	@Override
	public boolean undo(Solitaire game) {
		// Validate the undo move
		if(destTableauPile.count() == 0)
			return false; // You cannot undo a move from the Tableau to the Waste if the Tableau is empty
		// We want to get the top card of the TableauPile and now add it back to the WastePile
		Card topOfTheTableauPile = destTableauPile.get(); // Grab the top of the TableauPile
		wastePile.add(topOfTheTableauPile); // Add the card from the top of the TableauPile to the WastePile now
		((DeucesSolitaire)game).setWastePileCardCount(1); // We have now undone the move and thus have to increment the count
		// The move was successful, so:
		return true;
	}

	@Override
	public boolean valid(Solitaire game) {
		if( destTableauPile.peek() == null )
			return true; // It should be a valid move to an empty Tableau Pile
		// Rank Check 1- If the destination tableau pile has an Ace, the only card that can be added is a King
		if( destTableauPile.peek().isAce() && ( cardBeingDragged.getRank() != Card.KING ) )
			return false; // The card that is being dragged is not a King and cannot sit below the Ace
		// Rank Check 2- If tableau pile doesn't have an Ace at the top and as they grow downwards we should run a rank check
		if( !destTableauPile.peek().isAce() && (destTableauPile.peek().getRank() != (cardBeingDragged.getRank() + 1)) )
			return false; // The tableau piles grow downwards. So the card being added should be one rank lower to the top card on the Tableau Pile
		// Finally, if there is a top card on the destination Tableau Pile, then:
		return (destTableauPile.peek().getSuit() == cardBeingDragged.getSuit()); // All the cards in the Tableau pile should be of the same suit. So this line will check that
	}

}
