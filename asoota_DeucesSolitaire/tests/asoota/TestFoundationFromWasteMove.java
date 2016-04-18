package asoota;

import junit.framework.TestCase;
import ks.client.gamefactory.GameWindow;
import ks.common.model.Card;
import ks.common.model.Deck;
import ks.launcher.Main;
import ks.tests.model.ModelFactory;

public class TestFoundationFromWasteMove extends TestCase {

	private DeucesSolitaire deucesGame;
	private GameWindow gameWindow;
	
	@Override
	protected void setUp() {
		deucesGame = new DeucesSolitaire(); // Instantiate a DeucesSolitaire game
		gameWindow = Main.generateWindow(deucesGame, Deck.OrderBySuit); // Create the GameWindow as a testing environment
	}
	
	public void testOverAceMove() {
		// Fix the Foundation Pile
		ModelFactory.init(this.deucesGame.piles[0], "AS");
		ModelFactory.init(this.deucesGame.wastePile, "2S 3S");
		// Run initial checks
		assertEquals(1, this.deucesGame.piles[0].count());
		assertEquals(2, this.deucesGame.wastePile.count());
		// Pull out the first card
		Card cardsBeingDragged = new Card(this.deucesGame.wastePile.peek()); // Get 3 of Spades
		// Remove the card from the column
		this.deucesGame.wastePile.get(); // Extract 3 of Spades
		// Finally, create the move and check its validity
		FoundationFromWasteMove theMove = new FoundationFromWasteMove(this.deucesGame.wastePile, cardsBeingDragged, this.deucesGame.piles[0]);
		// Check its validity
		assertFalse( theMove.valid(deucesGame) ); // The move shouldn't be possible as nothing can be placed on top of an Ace
		// Run Card Count Check
		assertEquals(1, this.deucesGame.piles[0].count()); // Nothing should've changed in the destination FoundationPile
		assertEquals(1, this.deucesGame.wastePile.count()); // As we pulled out a card, nothing more should've happened
	}
	
	public void testAceOverKingMove() {
		// Fix the Foundation Pile
		ModelFactory.init(this.deucesGame.piles[0], "KS");
		ModelFactory.init(this.deucesGame.wastePile, "AS");
		// Run initial tests
		assertEquals(1, this.deucesGame.piles[0].count());
		assertEquals(1, this.deucesGame.wastePile.count());
		// Pull out the Aces
		Card cardBeingDragged = new Card(this.deucesGame.wastePile.peek()); // Get the Ace of Spades and add it here
		// Finally, pull out the card from the TableauPiles
		Card aceSpades = this.deucesGame.wastePile.get();
		Card kingSpades = new Card(Card.KING, Card.SPADES);
		// Now create the move
		FoundationFromWasteMove theMove = new FoundationFromWasteMove(this.deucesGame.wastePile, cardBeingDragged, this.deucesGame.piles[0]);
		// Check its validity
		assertTrue( theMove.valid(deucesGame) ); // This should be a valid move
		assertEquals(0, this.deucesGame.wastePile.count()); // This should be zero as we pulled out a card for dragging purposes
		assertEquals(1, this.deucesGame.piles[0].count()); // Nothing should've changed here as only the validity of the move was checked
		// Do the move
		assertTrue( theMove.doMove(deucesGame) ); // Perform the move
		// Run Card Count Check
		assertEquals(0, this.deucesGame.wastePile.count()); // This should be zero as the card was pulled out from here 
		assertEquals(2, this.deucesGame.piles[0].count()); // As the card was moved to this FoundationPile, the count here should be two
		// Run Card Sanity Check
		assertEquals(aceSpades, this.deucesGame.piles[0].peek()); // The top card that is added should be Ace of Spades
		assertEquals(kingSpades, this.deucesGame.piles[0].peek(0)); // The bottom card that the Ace was added over is King of Spades
		// Now, attempt to undo the move
		assertTrue( theMove.undo(deucesGame) ); // The move should be able to be undone
		assertEquals(1, this.deucesGame.piles[0].count()); // As the move was undone, the card count in the FoundationPile should be back to 1
		assertEquals(1, this.deucesGame.wastePile.count()); // As the move was undone, the card was added back to the TableauPile
		assertEquals(aceSpades, this.deucesGame.wastePile.peek()); // The card that was being dragged is the Ace of Spades
		assertEquals(kingSpades, this.deucesGame.piles[0].peek()); // The initial card resting on the FoundationPile is King of Spades
	}
	
	public void testAceOverKingFailMove() {
		// Fix the Foundation Pile
		ModelFactory.init(this.deucesGame.piles[0], "KS");
		ModelFactory.init(this.deucesGame.wastePile, "AD");
		// Run initial tests
		assertEquals(1, this.deucesGame.piles[0].count());
		assertEquals(1, this.deucesGame.wastePile.count());
		// Pull out the Aces
		Card cardBeingDragged = new Card(this.deucesGame.wastePile.peek()); // Get the Ace of Diamonds and add it here
		// Finally, pull out the card from the TableauPiles
		Card aceDiamonds = this.deucesGame.wastePile.get();
		Card kingSpades = new Card(Card.KING, Card.SPADES);
		// Now create the move
		FoundationFromWasteMove theMove = new FoundationFromWasteMove(this.deucesGame.wastePile, cardBeingDragged, this.deucesGame.piles[0]);
		// Check its validity
		assertFalse( theMove.valid(deucesGame) ); // This should NOT be a valid move
		assertEquals(0, this.deucesGame.wastePile.count()); // This should be zero as we pulled out a card for dragging purposes
		assertEquals(1, this.deucesGame.piles[0].count()); // Nothing should've changed here as only the validity of the move was checked
		// Card Type Sanity Check
		assertEquals(aceDiamonds, cardBeingDragged); // The card that was being dragged is the Ace of Diamonds
		assertEquals(kingSpades, this.deucesGame.piles[0].peek()); // The initial card resting on the FoundationPile is King of Spades
	}
	
	public void testNormalMove() {
		// Fix the FoundationPile and WastePile
		ModelFactory.init(this.deucesGame.piles[0], "2S");
		ModelFactory.init(this.deucesGame.wastePile, "5D 3S");
		// Run initial tests
		assertEquals(1, this.deucesGame.piles[0].count());
		assertEquals(2, this.deucesGame.wastePile.count());
		// Pull out the 3 of Spades
		Card threeOfSpades = this.deucesGame.wastePile.get();
		// Now, create the move
		FoundationFromWasteMove theMove = new FoundationFromWasteMove(this.deucesGame.wastePile, threeOfSpades, this.deucesGame.piles[0]);
		// Check if the move is valid
		assertTrue( theMove.valid(deucesGame) ); // The move should be valid
		// Run card count checks
		assertEquals(1, this.deucesGame.piles[0].count()); // This shouldn't have changed as we didn't perform the move yet
		assertEquals(1, this.deucesGame.wastePile.count()); // As we pulled out a card for dragging purposes, this count dropped by 1
		// Execute the move
		assertTrue( theMove.doMove(deucesGame) ); // The move should be possible
		// Run card count checks after the move was executed
		assertEquals(2, this.deucesGame.piles[0].count()); // As the move was executed, the count is now up by one
		assertEquals(1, this.deucesGame.wastePile.count()); // As a card was previously pulled out for the purposes of creating the Move class, the count remains unchanged
		// Run Card Sanity Check
		assertEquals(threeOfSpades, this.deucesGame.piles[0].peek()); // The latest card that was added to the FoundationPile is 3 of Spades
		assertEquals(new Card(Card.TWO, Card.SPADES), this.deucesGame.piles[0].peek(0)); // The bottom-most card on the FoundationPile is 2 of Spades
		// Now, undo the move
		assertTrue( theMove.undo(deucesGame) ); // The move can be undone
		// Run Card Count Check
		assertEquals(1, this.deucesGame.piles[0].count()); // As the move was undone, the count of the FoundationPile cards is back to 1
		assertEquals(2, this.deucesGame.wastePile.count()); // As the move was undone, the number of cards in the WastePile is back to 2
		// Run Card Sanity Check
		assertEquals(threeOfSpades, this.deucesGame.wastePile.peek()); // The top card of the WastePile that was just added back is 3 of Spades
		assertEquals(new Card(Card.TWO, Card.SPADES), this.deucesGame.piles[0].peek()); // The top card of the FoundationPile is 2 of Spades
	}
	
	public void testFailUndo() {
		// Fix the card in the FoundationPile
		ModelFactory.init(this.deucesGame.piles[0], "2S");
		ModelFactory.init(this.deucesGame.wastePile, "3S");
		// Run initial test cases
		assertEquals(1, this.deucesGame.piles[0].count());
		assertEquals(1, this.deucesGame.wastePile.count());
		// Pull out the 3 of Spades to create the move
		Card cardRemoved = this.deucesGame.wastePile.get();
		// Now, create the move
		FoundationFromWasteMove theMove = new FoundationFromWasteMove(this.deucesGame.wastePile, cardRemoved, this.deucesGame.piles[0]);
		// Now, attempt to undo the move
		assertFalse( theMove.undo(this.deucesGame) ); // The undo should not be possible
		// Just check that nothing was done
		assertEquals(1, this.deucesGame.piles[0].count()); // As only the undo was attempted and not executed, the number of cards should remain unchanged
		assertEquals(0, this.deucesGame.wastePile.count()); // As only the card was removed for creating the move, the number of cards fell by one
	}
	
	public void testFailPlaceOverKing() {
		// Fix the card in the FoundationPile
		ModelFactory.init(this.deucesGame.piles[0], "KD");
		ModelFactory.init(this.deucesGame.wastePile, "JD QD");
		// Run initial test cases
		assertEquals(1, this.deucesGame.piles[0].count());
		assertEquals(2, this.deucesGame.wastePile.count());
		// Pull out the Queen of Diamonds
		Card queenOfDiamonds = this.deucesGame.wastePile.get();
		// Now, create the move
		FoundationFromWasteMove theMove = new FoundationFromWasteMove(this.deucesGame.wastePile, queenOfDiamonds, this.deucesGame.piles[0]);
		// Now, check if it is valid
		assertFalse( theMove.valid(deucesGame) ); // The move shouldn't be valid
		// Run Card Count Sanity Checks
		assertEquals(1, this.deucesGame.piles[0].count()); // As we just checked for validity, the number of cards in the FoundationPile shouldn't have changed
		assertEquals(1, this.deucesGame.wastePile.count()); // As we pulled out one card to create the move, the number of cards fell by one
		// Run Card Sanity Check
		assertEquals(new Card(Card.KING, Card.DIAMONDS), this.deucesGame.piles[0].peek());
		assertEquals(new Card(Card.JACK, Card.DIAMONDS), this.deucesGame.wastePile.peek());
	}
	
	public void testFailPlaceOverKing_SuitFail() {
		// Fix the card in the FoundationPile
		ModelFactory.init(this.deucesGame.piles[0], "5D");
		ModelFactory.init(this.deucesGame.wastePile, "JC 6C");
		// Run initial test cases
		assertEquals(1, this.deucesGame.piles[0].count());
		assertEquals(2, this.deucesGame.wastePile.count());
		// Pull out the Six of Clubs
		Card sixClubs = this.deucesGame.wastePile.get();
		// Now, create the move
		FoundationFromWasteMove theMove = new FoundationFromWasteMove(this.deucesGame.wastePile, sixClubs, this.deucesGame.piles[0]);
		// Now, check if it is valid
		assertFalse( theMove.valid(deucesGame) ); // The move shouldn't be valid
		// Run Card Count Sanity Checks
		assertEquals(1, this.deucesGame.piles[0].count()); // As we just checked for validity, the number of cards in the FoundationPile shouldn't have changed
		assertEquals(1, this.deucesGame.wastePile.count()); // As we pulled out one card to create the move, the number of cards fell by one
		// Run Card Sanity Check
		assertEquals(new Card(Card.FIVE, Card.DIAMONDS), this.deucesGame.piles[0].peek());
		assertEquals(new Card(Card.JACK, Card.CLUBS), this.deucesGame.wastePile.peek());
	}
	
	@Override
	protected void tearDown() {
		gameWindow.dispose(); // To cut down on resources usage
	}
	
}