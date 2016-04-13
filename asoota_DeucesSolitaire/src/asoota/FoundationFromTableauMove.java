package asoota;

import java.util.LinkedList;
import java.util.Queue;

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

	private int cardsAddedToDestinationPile; // TODO: Check if this is allowed
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
		// STEP 1: Unroll all the cards and put in a Queue to maintain order
		Queue<Card> unrolledCards = new LinkedList<Card>();
		while( cardsBeingDragged.peek() != null ) // While we have a card to add
			unrolledCards.add(cardsBeingDragged.get()); // Pull out the card from the column
		// STEP 2: Update the score with the number of cards added to the Foundation Pile
		game.updateScore(unrolledCards.size()); // As cards are being dragged to the Foundation Pile adds to the score of the game, this line will do it
		cardsAddedToDestinationPile = unrolledCards.size(); // Also keep track of this for the sake of the undo
		// STEP 3: Roll them into the destination Foundation Pile
		while( unrolledCards.size() != 0 ) // While the Queue has cards
			destFoundationPile.add(unrolledCards.remove()); // Roll the card from the column into the destination foundation pile
		// The move was successful so,
		return true;
	}

	@Override
	public boolean undo(Solitaire game) {
		// If we want to undo the move, we should pull out the cards of the Foundation Pile and add them back to the source Tableau Pile
		// STEP: Unroll the cards from the Foundation Pile into the source Tableau Pile
		while( cardsAddedToDestinationPile > 0 ) {
			cardsAddedToDestinationPile--; // We just pulled one card from the destination foundation pile
			sourceTableauPile.add(destFoundationPile.get()); // Remove from the destination foundation pile and add it to the source tableau pile
			game.updateScore(-1); // As a card is being removed, the game score has to be necessarily updated
		}
		// As the undo was successful, we will
		return true;
	}

	@Override
	public boolean valid(Solitaire game) {
		assert( destFoundationPile.peek() != null ); // There should be atleast one card in the Foundation Pile
		// If there is a top card in the Destination Foundation Pile (which should always be the case), then:
		// Rank check 1- We shouldn't be able to add a card over Ace
		if( destFoundationPile.peek().getRank() == Card.ACE )
			return false; // You can never place a card over a Foundation Pile that has a facing Ace up
		// Rank check 2- Here we check if the destination is King and then card being added to the Foundation is an ACE, then we only check for suit equality and only card should be added as only an Ace can be added (and that's about it)
		if( cardsBeingDragged.peek().getRank() == Card.ACE && destFoundationPile.peek().getRank() == Card.KING )
			return (destFoundationPile.peek().getSuit() == cardsBeingDragged.peek().getSuit()) &&    // Suit check
				   (cardsBeingDragged.count() == 1);                                                 // Only one card (Ace) should be dragged
		// Else, we compare the ranks and the suits
		return (destFoundationPile.peek().getRank() == (cardsBeingDragged.peek().getRank() - 1)) &&   // The Foundation Piles grow upwards. That means the bottom card of a column of cards that is being dragged should have a rank one higher than the card already sitting in the Foundation Pile
			   (destFoundationPile.peek().getSuit() == (cardsBeingDragged.peek().getSuit()));         // All the cards in the foundation pile are of the same suit. This checks that the bottom card of a column of cards that is being dragged is of the suit as the card that is already in the Foundation Pile.
	}

}
