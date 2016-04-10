package asoota;

import ks.common.games.Solitaire;
import ks.common.model.Card;
import ks.common.model.Column;
import ks.common.model.Move;
import ks.common.model.MultiDeck;

/**
 * Move card from the MultiDeck to the top of the WastePile
 * @author asoota
 */
public class DealOneCardMove extends Move {

	private MultiDeck multiDeck;
	private Column wastePile;
	
	public DealOneCardMove(MultiDeck multiDeck, Column wastePile) {
		this.multiDeck = multiDeck;
		this.wastePile = wastePile;
	}

	@Override
	public boolean doMove(Solitaire game) {
		assert(valid(game) == true); // This function can only be called if the DealOneCardMove is valid
		// If the move is valid, perform the move
		Card deckCard = multiDeck.get(); // Get the top card of the MultiDeck
		wastePile.add(deckCard); // Add this card to the WastePile now
		game.updateNumberCardsLeft(-1); // We have to update the number of cards left as now one card has left the Deck and Moved to the WastePile
		// TODO: Work on incrementing the WastePileCard Count
		// We were able to move the card, so return true
		return true;
	}

	@Override
	public boolean undo(Solitaire game) {
		// We want to get the top card of the WastePile and now add it to the Deck
		Card topWastePileCard = wastePile.get(); // Get the card
		multiDeck.add(topWastePileCard); // And now add it to the deck
		game.updateNumberCardsLeft(1); // Because the move was undone, the number of cards left in the Deck has been increased by one
		// TODO: Work on decrementing the WastePileCard Count
		return true; // We were able to do the move, so return true
	}

	@Override
	public boolean valid(Solitaire game) {
		// Check if the deck is empty or not to determine if the move is valid
		return !multiDeck.empty();
	}

}
