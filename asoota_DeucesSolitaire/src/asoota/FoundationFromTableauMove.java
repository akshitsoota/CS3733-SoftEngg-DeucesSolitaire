package asoota;

import ks.common.games.Solitaire;
import ks.common.model.Card;
import ks.common.model.Column;
import ks.common.model.Move;
import ks.common.model.Pile;

/**
 * Move a card from the top of a column in a TableauPile to the top of a FoudatioPile.
 * @author asoota
 */
public class FoundationFromTableauMove extends Move {

	private Column sourceTableauPile;
	private Column cardsBeingDragged;
	private Pile destFoundationPile;
	
	public FoundationFromTableauMove(Column fromTableauPile, Column cardsBeingDragged, Pile destFoundationPile) {
		// Save all the parameters for future use
		this.sourceTableauPile = fromTableauPile;
		this.cardsBeingDragged = cardsBeingDragged;
		this.destFoundationPile = destFoundationPile;
	}
	
	@Override
	public boolean doMove(Solitaire game) {
		assert(valid(game) == true); // This function can only be called if the FoundationFromTableauMove is valid
		// If the move is valid, perform the move
		destFoundationPile.add(cardsBeingDragged.get()); // Add the card that is being dragged to the destination Foundation Pile
		                                                 // Because it was proved as a valid move (ie: the column has only one card, I will extract the card from the column and give that to be added to the Foundation Pile)
		game.updateScore(1); // As a card being dragged to the Foundation Pile adds to the score of the game, this line will do it
		// The move was successful so,
		return true;
	}

	@Override
	public boolean undo(Solitaire game) {
		// If we want to undo the move, we should pull out the top card of the Foundation Pile and add it back to the source Tableau Pile
		Card cardToPullOut = destFoundationPile.get(); // Pull out the card from the Foundation Pile
		sourceTableauPile.add(cardToPullOut); // This card should now be added to the Source Tableau Pile
		game.updateScore(-1); // As a card is being removed from one of the Foundation Piles, the score is decremented by one
		// As the undo was successful, we will
		return true;
	}

	@Override
	public boolean valid(Solitaire game) {
		assert( destFoundationPile.peek() != null ); // There should be atleast one card in the Foundation Pile
		// If there is a top card in the Destination Foundation Pile (which should always be the case), then check:
		return (cardsBeingDragged.count() == 1) &&                                                    // TODO: Add comment here and check with TA if this is a valid way of checking if it is a single card or a column of cards that is being dragged
			   (destFoundationPile.peek().getRank() == (cardsBeingDragged.peek().getRank() - 1)) &&   // We will only reach here if the Element cardBeingDragged is only one card due to Java Short-Circuiting. We now peek the top card of the column as that is the only card in the column.
			                                                                                          // The Foundation Piles grow upwards. That means the card is being dragged should have a rank one higher than the card already sitting in the Foundation Pile
			   (destFoundationPile.peek().getSuit() == (cardsBeingDragged.peek().getSuit()));         // All the cards in the foundation pile are of the same suit. This checks that the card that will be added is of the suit as the card that is already in the Foundation Pile.
	}

}
