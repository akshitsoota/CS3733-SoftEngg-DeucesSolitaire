package asoota;

import ks.common.games.Solitaire;
import ks.common.model.Card;
import ks.common.model.Column;
import ks.common.model.Move;

public class TableauFromTableauMove extends Move {

	private Column sourceTableauPile;
	private Card cardBeingDragged;
	private Column destTableauPile;
	
	public TableauFromTableauMove(Column fromTableauPile, Card cardBeingDragged, Column toTableauPile) {
		// Save all the parameters for future use
		this.sourceTableauPile = fromTableauPile;
		this.cardBeingDragged = cardBeingDragged;
		this.destTableauPile = toTableauPile;
	}

	@Override
	public boolean doMove(Solitaire game) {
		assert(valid(game) == true); // This function can only be called if the TableauFromTableauMove is valid
		// If the move is valid, perform the move
		destTableauPile.add(cardBeingDragged); // Add the card that is being dragged right now
		// The move was successful so,
		return true;
	}

	@Override
	public boolean undo(Solitaire game) {
		// We want to get the top card of the destination TableauPile and add it back to the source TableauPile
		Card topCardOfTheDestTableauPile = destTableauPile.get(); // Grab the top of the destination TableauPile
		sourceTableauPile.add(topCardOfTheDestTableauPile); // Add the card from the top of the TableauPile to the source TableauPile now
		// The move was successful, so:
		return true;
	}

	@Override
	public boolean valid(Solitaire game) {
		// If destination is empty, then atleast a card at the source should exist
		if( destTableauPile.count() == 0 )
			return true; // TODO: Maybe source pile should have one card
		// If the destination is NOT empty, then:
		return (destTableauPile.peek().getRank() == (cardBeingDragged.getRank() + 1)) &&  // As the Tableau piles grow downwards, the card that is being dragged should be a rank lower than the top card of the Tableau pile its going to
			   (destTableauPile.peek().getSuit() == (cardBeingDragged.getSuit()));      // All cards in the same Tableau pile should have the same suit. This link checks for that
	}

}
