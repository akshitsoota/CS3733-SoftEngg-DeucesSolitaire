package asoota;

import asoota.DeucesSolitaire;
import junit.framework.TestCase;
import ks.client.gamefactory.GameWindow;
import ks.common.model.Card;
import ks.common.model.Deck;
import ks.launcher.Main;

public class TestDealOneCardMove extends TestCase {

	public void testSimpleMove() {
		DeucesSolitaire deucesGame = new DeucesSolitaire(); // Instantiate a DeucesSolitaire game
		GameWindow gameWindow = Main.generateWindow(deucesGame, Deck.OrderBySuit); // Create the GameWindow as a testing environment
		
		Card topCardOfTheDeck = deucesGame.doubleDeck.peek(); // Take a look at the top card of the MultiDeck
		DealOneCardMove theMove = new DealOneCardMove(deucesGame.doubleDeck, deucesGame.wastePile); // Create the move with the MultiDeck and WastePile
		
		assertTrue(theMove.valid(deucesGame)); // By default, when the game starts up the move should be valid
		// TODO: Ask why is this not working- assertEquals(DeucesSolitaire.INITIAL_CARDS_LEFT, deucesGame.doubleDeck.count()); // Initially, we should have all the cards in the MultiDeck
		
		// Now perform the move and then look at the top card of the WastePiel
		theMove.doMove(deucesGame); // Perform the move
		
		assertEquals(deucesGame.wastePile.peek(), topCardOfTheDeck); // The previously top card of the deck should be the same as the top card on the WastePile after the move was performed
		// TODO: Ask why is this not working- assertEquals(DeucesSolitaire.INITIAL_CARDS_LEFT - 1, deucesGame.doubleDeck.count()); // After performing the move, we should now have one less card in the MultiDeck
		// TODO: Ask why is this not working- assertEquals(DeucesSolitaire.INITIAL_CARDS_LEFT - 1, deucesGame.getNumLeft().getValue()); // After performing the move, the number of cards left on the board should also be updated
		assertEquals(1, deucesGame.wastePile.count()); // After the move, we now have one card in the WastePile
		
		// Let us attempt to undo the move now
		theMove.undo(deucesGame);
		
		assertEquals(deucesGame.doubleDeck.peek(), topCardOfTheDeck); // We should've now got our top card on the Deck back from the WastePile
		// TODO: Ask why is this not working- assertEquals(DeucesSolitaire.INITIAL_CARDS_LEFT, deucesGame.doubleDeck.count()); // After undoing the move, we should be back to square one in terms of the count
		// TODO: Ask why is this not working- assertEquals(DeucesSolitaire.INITIAL_CARDS_LEFT, deucesGame.getNumLeft().getValue()); // After undoing the move, we should be back to square one
		assertEquals(0, deucesGame.wastePile.count()); // After undoing the move, as default, we should have no cards in the WastePile
	}
	
}
