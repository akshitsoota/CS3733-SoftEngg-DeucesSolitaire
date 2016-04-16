package asoota;

import java.util.Stack;

import ks.common.games.Solitaire;
import ks.common.model.Card;
import ks.common.model.Column;
import ks.common.model.Move;

public class TableauFromTableauMove extends Move {

	private int cardsAddedToDestinationTableauPile; // TODO: Check if this is allowed
	private Column sourceTableauPile;
	private Column cardsBeingDragged;
	private Column destTableauPile;
	
	public TableauFromTableauMove(Column fromTableauPile, Column cardsBeingDragged, Column toTableauPile) {
		// Save all the parameters for future use
		this.sourceTableauPile = fromTableauPile;
		this.cardsBeingDragged = cardsBeingDragged;
		this.destTableauPile = toTableauPile;
	}

	@Override
	public boolean doMove(Solitaire game) {
		assert(valid(game) == true); // This function can only be called if the TableauFromTableauMove is valid
		// If the move is valid, perform the move
		// STEP 1: Unroll all the cards into a queue
		Stack<Card> cards = new Stack<Card>();
		while( !cardsBeingDragged.empty() )
			cards.add(cardsBeingDragged.get());
		// STEP 2: Keep track of the number of cards that are dragged
		cardsAddedToDestinationTableauPile = cards.size();
		// STEP 3: Roll them out into the destination tableau pile
		while( cards.size() != 0 )
			destTableauPile.add(cards.pop());
		// The move was successful so,
		return true;
	}

	@Override
	public boolean undo(Solitaire game) {
		// Validate the move
		if( destTableauPile.count() == 0 )
			return false; // You cannot undo if the destination Tableau pile is empty
		// STEP 1: Unroll the cards from the Foundation Pile into a Stack
		Stack<Card> cardsUnrolled = new Stack<Card>(); 
		while( cardsAddedToDestinationTableauPile > 0 ) {
			cardsAddedToDestinationTableauPile--; // We just pulled one card from the destination foundation pile
			cardsUnrolled.add(destTableauPile.get()); // Remove from the destination foundation pile and add it to the stack
		}
		// STEP 2: Now, roll them out to the Source TableauPile
		while( !cardsUnrolled.isEmpty() )
			sourceTableauPile.add(cardsUnrolled.pop()); // Roll them out from the Stack and add it to the Source TableauPile
		// The move was successful, so:
		return true;
	}

	@Override
	public boolean valid(Solitaire game) {
		// If destination is empty, then atleast a card at the source should exist
		if( destTableauPile.count() == 0 )
			return true; // TODO: Maybe source pile should have one card
		// Unroll all the cards to access the bottom-most card of the CardsBeingDragged Column
		Stack<Card> queueOfCardsBeingDragged = new Stack<Card>();
		Card lastCardExtracted = null;
		// Iterate over now
		while( !cardsBeingDragged.empty() )
			queueOfCardsBeingDragged.add(lastCardExtracted = cardsBeingDragged.get());
		// Roll them all back in
		while( queueOfCardsBeingDragged.size() != 0 )
			cardsBeingDragged.add(queueOfCardsBeingDragged.pop());
		// Check the bottom-most card. For this we need multiple checks
		// Rank Check 1- If the destination tableau pile is an Ace, the bottom-most card of the column being dragged has to be a king
		if( destTableauPile.peek().isAce() && (lastCardExtracted.getRank() != Card.KING) )
			return false; // So, if the destination tableau pile has the top card as an Ace, the card that has to touch it has to be a king
		// Rank Check 2- If the destination tableau pile is NOT an Ace, check that the card that is being added will cause the Tableau pile to grow downward
		if( (!destTableauPile.peek().isAce()) && (destTableauPile.peek().getRank() != (lastCardExtracted.getRank() + 1)) )
			return false; // As the Tableau piles grow downwards, the bottom card of the column of cards that is being dragged should be a rank lower than the top card of the Tableau pile its going to
		// Now return if the suit is the same as we've done both Rank Checks
		return (destTableauPile.peek().getSuit() == (cardsBeingDragged.peek().getSuit())); // All cards in the same Tableau pile should have the same suit. This line checks for that                               
	}

}
