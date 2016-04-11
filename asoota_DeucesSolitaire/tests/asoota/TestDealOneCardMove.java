package asoota;

import asoota.DeucesSolitaire;
import junit.framework.TestCase;
import ks.client.gamefactory.GameWindow;
import ks.common.model.Card;
import ks.common.model.Deck;
import ks.common.model.MutableInteger;
import ks.launcher.Main;

public class TestDealOneCardMove extends TestCase {

	@SuppressWarnings("unused")
	public void testSimpleMove() {
		DeucesSolitaire deucesGame = new DeucesSolitaire(); // Instantiate a DeucesSolitaire game
		GameWindow gameWindow = Main.generateWindow(deucesGame, Deck.OrderBySuit); // Create the GameWindow as a testing environment
		MutableInteger wastePileCountView = new MutableInteger(0); // By default, the number of cards in the WastePile equals zero
		
		Card topCardOfTheDeck = deucesGame.doubleDeck.peek(); // Take a look at the top card of the MultiDeck
		DealOneCardMove theMove = new DealOneCardMove(deucesGame.doubleDeck, deucesGame.wastePile, wastePileCountView); // Create the move with the MultiDeck and WastePile
		
		assertTrue(theMove.valid(deucesGame)); // By default, when the game starts up the move should be valid
		assertEquals(DeucesSolitaire.INITIAL_CARDS_LEFT, deucesGame.doubleDeck.count()); // Initially, we should have all the cards in the MultiDeck
		assertEquals(0, wastePileCountView.getValue()); // Initially, the number of cards in the WastePile equal to zero
		
		// Now perform the move and then look at the top card of the WastePiel
		theMove.doMove(deucesGame); // Perform the move
		
		assertEquals(deucesGame.wastePile.peek(), topCardOfTheDeck); // The previously top card of the deck should be the same as the top card on the WastePile after the move was performed
		assertEquals(DeucesSolitaire.INITIAL_CARDS_LEFT - 1, deucesGame.doubleDeck.count()); // After performing the move, we should now have one less card in the MultiDeck
		assertEquals(DeucesSolitaire.INITIAL_CARDS_LEFT - 1, deucesGame.getNumLeft().getValue()); // After performing the move, the number of cards left on the board should also be updated
		assertEquals(1, deucesGame.wastePile.count()); // After the move, we now have one card in the WastePile
		assertEquals(1, wastePileCountView.getValue()); // After the move, the number of the cards in the WastePile should be one
		
		// Let us attempt to undo the move now
		theMove.undo(deucesGame);
		
		assertEquals(deucesGame.doubleDeck.peek(), topCardOfTheDeck); // We should've now got our top card on the Deck back from the WastePile
		assertEquals(DeucesSolitaire.INITIAL_CARDS_LEFT, deucesGame.doubleDeck.count()); // After undoing the move, we should be back to square one in terms of the count
		assertEquals(DeucesSolitaire.INITIAL_CARDS_LEFT, deucesGame.getNumLeft().getValue()); // After undoing the move, we should be back to square one
		assertEquals(0, deucesGame.wastePile.count()); // After undoing the move, as default, we should have no cards in the WastePile
		assertEquals(0, wastePileCountView.getValue()); // After undoing the previous move, the number of cards on the WastePile are back to zero
	}
	
}
