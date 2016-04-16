package asoota;

import junit.framework.TestCase;
import ks.client.gamefactory.GameWindow;
import ks.common.model.Card;
import ks.common.model.Column;
import ks.common.model.Deck;
import ks.launcher.Main;
import ks.tests.model.ModelFactory;

public class TestTableauToTableauMove extends TestCase {

	private DeucesSolitaire deucesGame;
	private GameWindow gameWindow;
	
	@Override
	protected void setUp() {
		deucesGame = new DeucesSolitaire(); // Instantiate a DeucesSolitaire game
		gameWindow = Main.generateWindow(deucesGame, Deck.OrderBySuit); // Create the GameWindow as a testing environment
	}
	
	public void testToEmptyTableauPile() {
		// Setup the from and destination Tableau Pile
		ModelFactory.init(this.deucesGame.columns[0], "1S KS");
		ModelFactory.init(this.deucesGame.columns[1], "");
		// Run initial test cases
		assertEquals(2, this.deucesGame.columns[0].count()); // We just set it up that way
		assertEquals(0, this.deucesGame.columns[1].count()); // We just set it up this way
		// Create the cardsBeingDragged Column and roll out the cards
		Column cardsBeingDragged = new Column(); // TODO: Check is this how I do this part
		cardsBeingDragged.add(this.deucesGame.columns[0].peek(0)); // Get the Ace out and add it to the Column
		cardsBeingDragged.add(this.deucesGame.columns[0].peek(1)); // Get the King out and add it to the Column as well
		// Finally, just remove the two cards
		Card kingSpades = this.deucesGame.columns[0].get();
		Card aceSpades = this.deucesGame.columns[0].get();
		// Now, create the move
		TableauFromTableauMove theMove = new TableauFromTableauMove(this.deucesGame.columns[0], cardsBeingDragged, this.deucesGame.columns[1]);
		// Assert some things about the Move now
		assertTrue( theMove.valid(deucesGame) ); // The move is valid and should pass
		assertEquals(0, this.deucesGame.columns[1].count()); // As we're checking if the move is valid, nothing should've been changed
		assertEquals(0, this.deucesGame.columns[0].count()); // As we pulled out the two cards to create the move, this class now has zero cards left
		// Now perform the move
		assertTrue( theMove.doMove(deucesGame) ); // Perform the move
		assertEquals(2, this.deucesGame.columns[1].count()); // As the cards were placed here, the number of cards here is two 
		assertEquals(0, this.deucesGame.columns[0].count()); // As the cards were removed from here, there are no longer any cards
		assertEquals(kingSpades, this.deucesGame.columns[1].peek(1)); // Take a look at the top card of the TableauPile
		assertEquals(aceSpades, this.deucesGame.columns[1].peek(0)); // Take a look at the bottom card of the TableauPile to which the cards were added
		// Undo the move
		assertTrue( theMove.undo(deucesGame) ); // We should be able to Undo the move
		assertEquals(0, this.deucesGame.columns[1].count()); // The cards were removed from the source TableauPile and hence the count is back to zero
		assertEquals(2, this.deucesGame.columns[0].count()); // As the move was undo, the cards in the source TableauPile is back to 2
	}
	
	public void testPickingAFewCards() {
		// Fix the Tableau Piles
		ModelFactory.init(this.deucesGame.columns[0], "1S KS QS JS");
		ModelFactory.init(this.deucesGame.columns[1], "1S KS");
		// Run initial test cases
		assertEquals(4, this.deucesGame.columns[0].count()); // This is how we've fixed our TableauPile
		assertEquals(2, this.deucesGame.columns[1].count()); // This is how we've fixed out TableauPile
		// Create the cardsBeingDragged Column and roll out the cards
		Column cardsBeingDragged = new Column(); // Create the new column
		cardsBeingDragged.add(this.deucesGame.columns[0].peek(2)); // Get the Q of Spades
		cardsBeingDragged.add(this.deucesGame.columns[0].peek(3)); // Get the J of Spades
		// Now remove the two cards from the TableauPile
		Card jackSpades = this.deucesGame.columns[0].get(); // Remove the J of Spades
		Card queenSpades = this.deucesGame.columns[0].get(); // Remove the Q of Spades
		Card kingSpades = new Card(Card.KING, Card.SPADES); // Form a King of Spades card
		Card aceSpades = new Card(Card.ACE, Card.SPADES); // Form a Ace of Spades Card
		// Form the move
		TableauFromTableauMove theMove = new TableauFromTableauMove(this.deucesGame.columns[0], cardsBeingDragged, this.deucesGame.columns[1]);
		// Check if a valid move
		assertTrue( theMove.valid(deucesGame) ); // It should be a valid move the way we've defined it
		assertEquals(2, this.deucesGame.columns[0].count()); // Nothing should've been changed as we're just checking if the move was valid (except the fact that we removed the cards)
		assertEquals(2, this.deucesGame.columns[1].count()); // Nothing should've changed in the destination TableauPile as well
		// Now, perform the move
		assertTrue( theMove.doMove(deucesGame) ); // We should be able to perform the move successfully
		assertEquals(2, this.deucesGame.columns[0].count()); // As the cards were moved out, the card count drops by 2
		assertEquals(4, this.deucesGame.columns[1].count()); // As this was the destination TableauPile, the card count increased by 2
		assertEquals(jackSpades, this.deucesGame.columns[1].peek()); // Check if the topmost card of the column is a 4 of Spades
		assertEquals(aceSpades, this.deucesGame.columns[1].peek(0)); // Similarly, check till last card
		assertEquals(queenSpades, this.deucesGame.columns[1].peek(2));
		assertEquals(kingSpades, this.deucesGame.columns[1].peek(1));
		// Now, undo the move
		assertTrue( theMove.undo(deucesGame) ); // We should be able to undo the move
		assertEquals(4, this.deucesGame.columns[0].count()); // As the move was undone, the count of this TableauPile should be restored
		assertEquals(2, this.deucesGame.columns[1].count()); // As the move was undone, the count of the destination TableauPile is back to 2
		assertEquals(aceSpades, this.deucesGame.columns[0].peek(0)); // Run card presence checks for the source TableauPile
		assertEquals(kingSpades, this.deucesGame.columns[0].peek(1));
		assertEquals(queenSpades, this.deucesGame.columns[0].peek(2));
		assertEquals(jackSpades, this.deucesGame.columns[0].peek());
		assertEquals(aceSpades, this.deucesGame.columns[1].peek(0)); // Run card presence checks for the destination Tableau Pile
		assertEquals(kingSpades, this.deucesGame.columns[1].peek());
	}
	
	public void testToAce() {
		// Fix the TableauPiles
		ModelFactory.init(this.deucesGame.columns[0], "AD");
		ModelFactory.init(this.deucesGame.columns[1], "KD QD JD");
		ModelFactory.init(this.deucesGame.columns[2], "3D 2D");
		// Run initial test cases
		assertEquals(1, this.deucesGame.columns[0].count());
		assertEquals(3, this.deucesGame.columns[1].count());
		assertEquals(2, this.deucesGame.columns[2].count());
		// Remove cards from Column Index 2 and form the dragging column
		Column cardsBeingDragged1 = new Column();
		cardsBeingDragged1.add(this.deucesGame.columns[2].peek(0)); // Add the 3 of Diamonds
		cardsBeingDragged1.add(this.deucesGame.columns[2].peek(1)); // Add the 2 of Diamonds
		// Extract the cards
		this.deucesGame.columns[2].get(); // Extract the 2 of diamonds
		this.deucesGame.columns[2].get(); // Extract the 3 of diamonds
		// Create the first move
		TableauFromTableauMove theMove = new TableauFromTableauMove(this.deucesGame.columns[2], cardsBeingDragged1, this.deucesGame.columns[0]);
		// Check if it is a valid move
		assertFalse( theMove.valid(deucesGame) ); // Should be an invalid move
		assertEquals(1, this.deucesGame.columns[0].count()); // Nothing should've changed as we just checked for validity
		assertEquals(3, this.deucesGame.columns[1].count()); // Same here
		assertEquals(0, this.deucesGame.columns[2].count()); // This should be zero as we extract the cards out to create the move
		// Now create a different move, but first, create the column and extract the cards
		Column cardsBeingDragged2 = new Column();
		cardsBeingDragged2.add(this.deucesGame.columns[1].peek(0)); // Add the King of Diamonds
		cardsBeingDragged2.add(this.deucesGame.columns[1].peek(1)); // Add the Queen of Diamonds
		cardsBeingDragged2.add(this.deucesGame.columns[1].peek()); // Add the Jack of Diamonds
		// Extract the cards
		Card jackDiamonds = this.deucesGame.columns[1].get(); // Get the J of Diamonds
		Card queenDiamonds = this.deucesGame.columns[1].get(); // Get the Q of Diamonds
		Card kingDiamonds = this.deucesGame.columns[1].get(); // Get the K of Diamonds
		Card aceDiamonds = new Card(Card.ACE, Card.DIAMONDS); // Form the card Ace of Diamonds
		// Now create the second move
		TableauFromTableauMove theMove2 = new TableauFromTableauMove(this.deucesGame.columns[1], cardsBeingDragged2, this.deucesGame.columns[0]);
		// Attempt to check validity of the move
		assertTrue( theMove2.valid(deucesGame) ); // The move should be valid
		// Nothing should've happened though
		assertEquals(1, this.deucesGame.columns[0].count());
		assertEquals(0, this.deucesGame.columns[1].count()); // As the cards were removed, this should be 0
		// Now, do the move
		assertTrue( theMove2.doMove(deucesGame) ); // The move should've been done
		assertEquals(4, this.deucesGame.columns[0].count()); // The number of cards in the destination TableauPile should've bumped up by 3
		assertEquals(0, this.deucesGame.columns[1].count()); // The number of cards in the source TableauPile should be zero
		// Check the cards
		assertEquals(aceDiamonds, this.deucesGame.columns[0].peek(0));
		assertEquals(kingDiamonds, this.deucesGame.columns[0].peek(1));
		assertEquals(queenDiamonds, this.deucesGame.columns[0].peek(2));
		assertEquals(jackDiamonds, this.deucesGame.columns[0].peek());
		// Now attempt to undo the move
		assertTrue( theMove2.undo(deucesGame) ); // The undo should be possible
		// Run sanity checks on the card counts
		assertEquals(1, this.deucesGame.columns[0].count()); // The number of cards in the destination TableauPile should be back to zero
		assertEquals(3, this.deucesGame.columns[1].count()); // The number of cards in the source TableauPile should be back to 3
		// Check the cards
		assertEquals(aceDiamonds, this.deucesGame.columns[0].peek()); // Only card in the destination TableauPile should be the Ace of Diamonds
		assertEquals(kingDiamonds, this.deucesGame.columns[1].peek(0)); // The bottom card of the source TableauPile should be King of Diamonds
		assertEquals(queenDiamonds, this.deucesGame.columns[1].peek(1)); // The middle card of the source TableauPile should be Queen of Diamonds
		assertEquals(jackDiamonds, this.deucesGame.columns[1].peek()); // The top card of the source TableauPile should be a Jack of Diamonds
	}
	
	public void testFailAceMove() {
		// Fix the TableauPiles
		ModelFactory.init(this.deucesGame.columns[0], "AH");
		ModelFactory.init(this.deucesGame.columns[1], "3H 2H");
		// Run initial tests
		assertEquals(1, this.deucesGame.columns[0].count());
		assertEquals(2, this.deucesGame.columns[1].count());
		// Create the column to be dragged
		Column cardsBeingDragged = new Column();
		cardsBeingDragged.add(this.deucesGame.columns[1].peek(0)); // Add the 3 of Hearts
		cardsBeingDragged.add(this.deucesGame.columns[1].peek(1)); // Add the 2 of Hearts
		// Now extract the cards from the Source TableauPile
		this.deucesGame.columns[1].get(); // Extract the 2 of Hearts
		this.deucesGame.columns[1].get(); // Extract the 3 of Hearts
		// Finally, form the Ace of Hearts Card
		Card aceHearts = new Card(Card.ACE, Card.HEARTS); // Form the Ace of hearts
		// Now create the move
		TableauFromTableauMove theMove = new TableauFromTableauMove(this.deucesGame.columns[1], cardsBeingDragged, this.deucesGame.columns[0]);
		// Now check for its validity
		assertFalse( theMove.valid(deucesGame) ); // The move shouldn't be valid
		// Run card count sanity check
		assertEquals(1, this.deucesGame.columns[0].count());
		assertEquals(0, this.deucesGame.columns[1].count()); // As two cards were removed to create the move, this should be zero
		// Now, run a card sanity check
		assertEquals(aceHearts, this.deucesGame.columns[0].peek());
	}
	
	public void testCompletelyFailMove() {
		// Fix the TableauPile
		ModelFactory.init(this.deucesGame.columns[0], "2C");
		ModelFactory.init(this.deucesGame.columns[1], "5C 4C");
		// Run initial tests
		assertEquals(1, this.deucesGame.columns[0].count());
		assertEquals(2, this.deucesGame.columns[1].count());
		// Create the column to be dragged
		Column cardsBeingDragged = new Column();
		cardsBeingDragged.add(this.deucesGame.columns[1].peek(0)); // Add the 5 of Clubs
		cardsBeingDragged.add(this.deucesGame.columns[1].peek(1)); // Add the 4 of Clubs
		// Extract these cards
		this.deucesGame.columns[1].get(); // Remove the 4 of Clubs
		this.deucesGame.columns[1].get(); // Remove the 5 of Clubs
		// Create the move
		TableauFromTableauMove theMove = new TableauFromTableauMove(this.deucesGame.columns[1], cardsBeingDragged, this.deucesGame.columns[0]);
		// Now, check if the move is valid
		assertFalse( theMove.valid(deucesGame) ); // The move shouldn't be valid
		// Run some Card Count Sanity Checks
		assertEquals(1, this.deucesGame.columns[0].count()); // Nothing should've changed in the destination TableauPile
		assertEquals(0, this.deucesGame.columns[1].count()); // As the two cards were removed from the Source TableauPile, this is down to zero
		// Run some Card Sanity Checks
		assertEquals(new Card(Card.TWO, Card.CLUBS), this.deucesGame.columns[0].peek()); // The top card of the destination TableauPile should be the same
	}
	
	public void testBadSuitMove() {
		// Fix the TableauPiles
		ModelFactory.init(this.deucesGame.columns[0], "5C");
		ModelFactory.init(this.deucesGame.columns[1], "4S 3S");
		// Run initial tests
		assertEquals(1, this.deucesGame.columns[0].count());
		assertEquals(2, this.deucesGame.columns[1].count());
		// Create the column to be dragged
		Column cardsBeingDragged = new Column();
		cardsBeingDragged.add(this.deucesGame.columns[1].peek(0)); // Add the 4 of Spades
		cardsBeingDragged.add(this.deucesGame.columns[1].peek(1)); // Add the 3 of Spades
		// Extract these cards
		this.deucesGame.columns[1].get(); // Remove the 3 of Spades
		this.deucesGame.columns[1].get(); // Remove the 4 of Spades
		// Create the move
		TableauFromTableauMove theMove = new TableauFromTableauMove(this.deucesGame.columns[1], cardsBeingDragged, this.deucesGame.columns[0]);
		// Now, check if the move is valid
		assertFalse( theMove.valid(deucesGame) ); // The move shouldn't be valid
		// Run some Card Count Sanity Checks
		assertEquals(1, this.deucesGame.columns[0].count()); // Nothing should've changed in the destination TableauPile
		assertEquals(0, this.deucesGame.columns[1].count()); // As the two cards were removed from the Source TableauPile, this is down to zero
		// Run some Card Sanity Checks
		assertEquals(new Card(Card.FIVE, Card.CLUBS), this.deucesGame.columns[0].peek()); // The top card of the destination TableauPile should be the same
	}
	
	public void testFailUndo() {
		// Fix a TableauPile
		ModelFactory.init(this.deucesGame.columns[0], "");
		ModelFactory.init(this.deucesGame.columns[1], "KC");
		// Run card count checks
		assertEquals(0, this.deucesGame.columns[0].count());
		assertEquals(1, this.deucesGame.columns[1].count());
		// Create the ColumnBeingDragged
		Column cardsBeingDragged = new Column();
		cardsBeingDragged.add(this.deucesGame.columns[1].peek()); // Get the King of Clubs
		// Pull out the King of Clubs
		this.deucesGame.columns[1].get(); // Extracted the King Of Clubs
		// Create a move
		TableauFromTableauMove theMove = new TableauFromTableauMove(this.deucesGame.columns[1], cardsBeingDragged, this.deucesGame.columns[0]);
		// Check for its validity
		assertTrue( theMove.valid(deucesGame) ); // Should be a valid move
		assertEquals(0, this.deucesGame.columns[0].count()); // As no move was done the destination tableau pile count should be zero
		assertEquals(0, this.deucesGame.columns[1].count()); // As a card was removed to be dragged, the count of the source TableauPile is zero
		// Try to undo the move
		assertFalse( theMove.undo(deucesGame) ); // Shouldn't be a valid move
	}
	
	@Override
	protected void tearDown() {
		gameWindow.dispose(); // To cut down on resources usage
	}
	
}
