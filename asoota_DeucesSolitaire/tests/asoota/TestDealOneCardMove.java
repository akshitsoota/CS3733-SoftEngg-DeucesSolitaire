package asoota;

import asoota.DeucesSolitaire;
import junit.framework.TestCase;
import ks.client.gamefactory.GameWindow;
import ks.common.model.Card;
import ks.common.model.Deck;
import ks.launcher.Main;
import ks.tests.model.ModelFactory;

public class TestDealOneCardMove extends TestCase {

	private DeucesSolitaire deucesGame;
	private GameWindow gameWindow;
	
	@Override
	protected void setUp() {
		deucesGame = new DeucesSolitaire(); // Instantiate a DeucesSolitaire game
		gameWindow = Main.generateWindow(deucesGame, Deck.OrderBySuit); // Create the GameWindow as a testing environment
	}

	public void testSimpleMove() {
		
		Card topCardOfTheDeck = deucesGame.doubleDeck.peek(); // Take a look at the top card of the MultiDeck
		DealOneCardMove theMove = new DealOneCardMove(deucesGame.doubleDeck, deucesGame.wastePile); // Create the move with the MultiDeck and WastePile
		
		assertTrue(theMove.valid(deucesGame)); // By default, when the game starts up the move should be valid
		assertEquals(DeucesSolitaire.INITIAL_CARDS_LEFT, deucesGame.doubleDeck.count()); // Initially, we should have all the cards in the MultiDeck
		assertEquals(0, deucesGame.getWastePileCardCount()); // Initially, the number of cards in the WastePile equal to zero
		
		// Now perform the move and then look at the top card of the WastePile
		theMove.doMove(deucesGame); // Perform the move
		
		assertEquals(deucesGame.wastePile.peek(), topCardOfTheDeck); // The previously top card of the deck should be the same as the top card on the WastePile after the move was performed
		assertEquals(DeucesSolitaire.INITIAL_CARDS_LEFT - 1, deucesGame.doubleDeck.count()); // After performing the move, we should now have one less card in the MultiDeck
		assertEquals(DeucesSolitaire.INITIAL_CARDS_LEFT - 1, deucesGame.getNumLeft().getValue()); // After performing the move, the number of cards left on the board should also be updated
		assertEquals(1, deucesGame.wastePile.count()); // After the move, we now have one card in the WastePile
		assertEquals(1, deucesGame.getWastePileCardCount()); // After the move, the number of the cards in the WastePile should be one
		
		// Let us attempt to undo the move now
		theMove.undo(deucesGame);
		
		assertEquals(deucesGame.doubleDeck.peek(), topCardOfTheDeck); // We should've now got our top card on the Deck back from the WastePile
		assertEquals(DeucesSolitaire.INITIAL_CARDS_LEFT, deucesGame.doubleDeck.count()); // After undoing the move, we should be back to square one in terms of the count
		assertEquals(DeucesSolitaire.INITIAL_CARDS_LEFT, deucesGame.getNumLeft().getValue()); // After undoing the move, we should be back to square one
		assertEquals(0, deucesGame.wastePile.count()); // After undoing the move, as default, we should have no cards in the WastePile
		assertEquals(0, deucesGame.getWastePileCardCount()); // After undoing the previous move, the number of cards on the WastePile are back to zero
		
	}
	
	public void testMultipleDeals() {
		DealOneCardMove theMove = new DealOneCardMove(deucesGame.doubleDeck, deucesGame.wastePile); // Create the move with the MultiDeck and WastePile
		
		assertTrue(theMove.valid(deucesGame)); // By default, the move to deal a card from the Deck to the WastePile should be valid
		
		// Now perform three such moves and then look at the top card of the WastePile
		theMove.doMove(deucesGame); // Perform the moves
		theMove.doMove(deucesGame);
		theMove.doMove(deucesGame);
		
		assertEquals(DeucesSolitaire.INITIAL_CARDS_LEFT - 3, deucesGame.doubleDeck.count()); // After performing the moves, we should now have three less cards in the MultiDeck
		assertEquals(DeucesSolitaire.INITIAL_CARDS_LEFT - 3, deucesGame.getNumLeft().getValue()); // After performing the moves, the number of cards left on the board should also be updated
		assertEquals(3, deucesGame.wastePile.count()); // After the moves, we now have three card in the WastePile
		assertEquals(3, deucesGame.getWastePileCardCount()); // After the moves, the number of the cards in the WastePile should be three
		
		// Let us attempt to undo one move now
		theMove.undo(deucesGame);
		
		assertEquals(DeucesSolitaire.INITIAL_CARDS_LEFT - 2, deucesGame.doubleDeck.count()); // After undoing the move, we should still have two less cards in the MultiDeck
		assertEquals(DeucesSolitaire.INITIAL_CARDS_LEFT - 2, deucesGame.getNumLeft().getValue()); // After undoing the move, we should still have two less cards in the MultiDeck
		assertEquals(2, deucesGame.wastePile.count()); // After undoing the move, we should have two cards in the WastePile
		assertEquals(2, deucesGame.getWastePileCardCount()); // After undoing the previous move, the number of cards on the WastePile should be two
		
		// Now perform two such moves and then look at the top card of the WastePile
		theMove.doMove(deucesGame); // Perform the moves
		theMove.doMove(deucesGame);
		
		assertEquals(DeucesSolitaire.INITIAL_CARDS_LEFT - 4, deucesGame.doubleDeck.count()); // After performing the moves, we should now have four less cards in the MultiDeck
		assertEquals(DeucesSolitaire.INITIAL_CARDS_LEFT - 4, deucesGame.getNumLeft().getValue()); // After performing the moves, the number of cards left on the board should also be updated
		assertEquals(4, deucesGame.wastePile.count()); // After the moves, we now have four card in the WastePile
		assertEquals(4, deucesGame.getWastePileCardCount()); // After the moves, the number of the cards in the WastePile should be four
	}
	
	public void testEmptyDeck() {
		// Fix the Deck to empty it
		ModelFactory.init(this.deucesGame.doubleDeck, "");
		// Now run initial testing
		assertEquals(0, this.deucesGame.doubleDeck.count()); // We removed all cards so this one should pass
		// Now attempt to create a move
		DealOneCardMove theMove = new DealOneCardMove(this.deucesGame.doubleDeck, this.deucesGame.wastePile); // Create the move with the MultiDeck and the WastePile
		// The move should fail as the deck is empty
		assertFalse( theMove.valid(deucesGame) );
		// The move cannot be undone as well
		assertFalse( theMove.undo(deucesGame) );
	}
	
	@Override
	protected void tearDown() {
		gameWindow.dispose(); // To cut down on resources usage
	}
	
}
