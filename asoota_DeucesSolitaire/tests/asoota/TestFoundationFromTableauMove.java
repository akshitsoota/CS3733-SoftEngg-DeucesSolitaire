package asoota;

import junit.framework.TestCase;
import ks.client.gamefactory.GameWindow;
import ks.common.model.Card;
import ks.common.model.Column;
import ks.common.model.Deck;
import ks.launcher.Main;
import ks.tests.model.ModelFactory;

public class TestFoundationFromTableauMove extends TestCase {

	private DeucesSolitaire deucesGame;
	private GameWindow gameWindow;
	
	@Override
	protected void setUp() {
		deucesGame = new DeucesSolitaire(); // Instantiate a DeucesSolitaire game
		gameWindow = Main.generateWindow(deucesGame, Deck.OrderBySuit); // Create the GameWindow as a testing environment
	}
	
	public void testDestAce() {
		// Fix the FoundationPile and TableauPile
		ModelFactory.init(this.deucesGame.piles[0], "KC AC");
		ModelFactory.init(this.deucesGame.columns[0], "5C 4C 3C");
		// Run initial test cases
		assertEquals(2, this.deucesGame.piles[0].count());
		assertEquals(3, this.deucesGame.columns[0].count());
		// Pull out the two cards from the TableauPile
		Column cardsBeingDragged = new Column(); // Create the column first
		cardsBeingDragged.add(this.deucesGame.columns[0].peek(1)); // Pull out the 4 of Clubs
		cardsBeingDragged.add(this.deucesGame.columns[0].peek(2)); // Pull out the 5 of Clubs
		// Now, actually extract the 2 cards from the TableauPile
		this.deucesGame.columns[0].get(); // Remove the 3 of Clubs
		this.deucesGame.columns[0].get(); // Now, remove the 4 of Clubs
		// Create the move
		FoundationFromTableauMove theMove = new FoundationFromTableauMove(this.deucesGame.columns[0], cardsBeingDragged, this.deucesGame.piles[0]);
		// Check if the move is valid
		assertFalse( theMove.valid(deucesGame) ); // The move shouldn't be valid
		// Run Card Count Checks
		assertEquals(2, this.deucesGame.piles[0].count());
		assertEquals(1, this.deucesGame.columns[0].count()); // As 2 cards were removed to create the move, the count should've fallen by 2
	}
	
	public void testAceOverKing_Success() {
		// Fix the FoundationPile and TableauPile
		ModelFactory.init(this.deucesGame.piles[0], "KC");
		ModelFactory.init(this.deucesGame.columns[0], "AC");
		// Run initial test cases
		assertEquals(1, this.deucesGame.piles[0].count());
		assertEquals(1, this.deucesGame.columns[0].count());
		// Pull out the only card from the TableauPile
		Column cardsBeingDragged = new Column(); // Create the column first
		cardsBeingDragged.add(this.deucesGame.columns[0].get()); // Pull out the Ace of Clubs
		// Create the move
		FoundationFromTableauMove theMove = new FoundationFromTableauMove(this.deucesGame.columns[0], cardsBeingDragged, this.deucesGame.piles[0]);
		// Check if the move is valid
		assertTrue( theMove.valid(deucesGame) ); // The move should be valid
		// Run Card Count Checks
		assertEquals(1, this.deucesGame.piles[0].count()); // As only validity was checked for, the number of cards shouldn't have changed
		assertEquals(0, this.deucesGame.columns[0].count()); // As a card was removed to create the move, the count should've fallen by 1 to zero
		// Now, perform the move
		assertTrue( theMove.doMove(deucesGame) );
		// Run Card Count Checks
		assertEquals(2, this.deucesGame.piles[0].count()); // As the number of cards in the FoundationPile increased by one after executing the move, the test checks for the increase
		assertEquals(0, this.deucesGame.columns[0].count()); // After the move, the card count remains unchanged
		// Run Card Sanity Check
		assertEquals(new Card(Card.ACE, Card.CLUBS), this.deucesGame.piles[0].peek());
		assertEquals(new Card(Card.KING, Card.CLUBS), this.deucesGame.piles[0].peek(0));
		// Undo the move
		assertTrue( theMove.undo(this.deucesGame) ); // The move should be successfully undone
		// Run Card Count Checks
		assertEquals(1, this.deucesGame.piles[0].count()); // Number of cards in the destination FoundationPile should be back to one
		assertEquals(1, this.deucesGame.columns[0].count()); // After undoing the move, the number of cards in the TableauPile should be back to one
		// Run Card Sanity Check
		assertEquals(new Card(Card.KING, Card.CLUBS), this.deucesGame.piles[0].peek());
		assertEquals(new Card(Card.ACE, Card.CLUBS), this.deucesGame.columns[0].peek());
	}
	
	public void testAceOverKing_FailCount() {
		// Fix the FoundationPile and TableauPile
		ModelFactory.init(this.deucesGame.piles[0], "KC");
		ModelFactory.init(this.deucesGame.columns[0], "QC KC AC");
		// Run initial test cases
		assertEquals(1, this.deucesGame.piles[0].count());
		assertEquals(3, this.deucesGame.columns[0].count());
		// Pull out two cards from the TableauPile
		Column cardsBeingDragged = new Column(); // Create the column first
		cardsBeingDragged.add(this.deucesGame.columns[0].peek(1)); // Pull out the King of Clubs
		cardsBeingDragged.add(this.deucesGame.columns[0].peek()); // Pull out the Ace of Clubs
		// Finally, extract the two cards
		this.deucesGame.columns[0].get(); // Extract the Ace of Clubs
		this.deucesGame.columns[0].get(); // Extract the King of Clubs
		// Create the move
		FoundationFromTableauMove theMove = new FoundationFromTableauMove(this.deucesGame.columns[0], cardsBeingDragged, this.deucesGame.piles[0]);
		// Check if the move is valid
		assertFalse( theMove.valid(deucesGame) ); // The move shouldn't be valid
		// Run Card Count Checks
		assertEquals(1, this.deucesGame.piles[0].count()); // As only validity was checked for, the number of cards shouldn't have changed
		assertEquals(1, this.deucesGame.columns[0].count()); // As 2 cards were removed to create the move, the count should've fallen by 2 to one
	}
	
	public void testAceOverKing_FailSuit() {
		// Fix the FoundationPile and TableauPile
		ModelFactory.init(this.deucesGame.piles[0], "KC");
		ModelFactory.init(this.deucesGame.columns[0], "QD KD AD");
		// Run initial test cases
		assertEquals(1, this.deucesGame.piles[0].count());
		assertEquals(3, this.deucesGame.columns[0].count());
		// Pull out two cards from the TableauPile
		Column cardsBeingDragged = new Column(); // Create the column first
		cardsBeingDragged.add(this.deucesGame.columns[0].peek(1)); // Pull out the King of Diamonds
		cardsBeingDragged.add(this.deucesGame.columns[0].peek()); // Pull out the Ace of Diamonds
		// Finally, extract the two cards
		this.deucesGame.columns[0].get(); // Extract the Ace of Diamonds
		this.deucesGame.columns[0].get(); // Extract the King of Diamonds
		// Create the move
		FoundationFromTableauMove theMove = new FoundationFromTableauMove(this.deucesGame.columns[0], cardsBeingDragged, this.deucesGame.piles[0]);
		// Check if the move is valid
		assertFalse( theMove.valid(deucesGame) ); // The move shouldn't be valid
		// Run Card Count Checks
		assertEquals(1, this.deucesGame.piles[0].count()); // As only validity was checked for, the number of cards shouldn't have changed
		assertEquals(1, this.deucesGame.columns[0].count()); // As 2 cards were removed to create the move, the count should've fallen by 2 to one
	}
	
	public void testAceOverQueen_Fail() {
		// Fix the FoundationPile and TableauPile
		ModelFactory.init(this.deucesGame.piles[0], "QC");
		ModelFactory.init(this.deucesGame.columns[0], "AC");
		// Run initial test cases
		assertEquals(1, this.deucesGame.piles[0].count());
		assertEquals(1, this.deucesGame.columns[0].count());
		// Pull out the only card from the TableauPile
		Column cardsBeingDragged = new Column(); // Create the column first
		cardsBeingDragged.add(this.deucesGame.columns[0].get()); // Pull out the Ace of Clubs
		// Create the move
		FoundationFromTableauMove theMove = new FoundationFromTableauMove(this.deucesGame.columns[0], cardsBeingDragged, this.deucesGame.piles[0]);
		// Check if the move is valid
		assertFalse( theMove.valid(deucesGame) ); // The move shouldn't be valid
		// Run Card Count Checks
		assertEquals(1, this.deucesGame.piles[0].count()); // As only validity was checked for, the number of cards shouldn't have changed
		assertEquals(0, this.deucesGame.columns[0].count()); // As a card was removed to create the move, the count should've fallen by 1 to zero
	}
	
	public void testSuccessfulMove() {
		// Fix the FoundationPile and TableauPile
		ModelFactory.init(this.deucesGame.piles[0], "3C");
		ModelFactory.init(this.deucesGame.columns[0], "7C 6C 5C 4C");
		// Run initial test cases
		assertEquals(1, this.deucesGame.piles[0].count());
		assertEquals(4, this.deucesGame.columns[0].count());
		// Pull out two cards from the TableauPile
		Column cardsBeingDragged = new Column(); // Create the column first
		cardsBeingDragged.add(this.deucesGame.columns[0].peek(2)); // Pull out the 5 of Clubs
		cardsBeingDragged.add(this.deucesGame.columns[0].peek(3)); // Pull out the 4 of Clubs
		// Extract the cards
		this.deucesGame.columns[0].get(); // Remove the 4 of Clubs
		this.deucesGame.columns[0].get(); // Remove the 5 of Clubs
		// Create the move
		FoundationFromTableauMove theMove = new FoundationFromTableauMove(this.deucesGame.columns[0], cardsBeingDragged, this.deucesGame.piles[0]);
		// Check if the move is valid
		assertTrue( theMove.valid(deucesGame) ); // The move should be valid
		// Run Card Count Checks
		assertEquals(1, this.deucesGame.piles[0].count()); // As only validity was checked for, the number of cards shouldn't have changed
		assertEquals(2, this.deucesGame.columns[0].count()); // As 2 cards were removed to create the move, the count should've fallen by 2 to two
		// Now, perform the move
		assertTrue( theMove.doMove(deucesGame) );
		// Run Card Count Checks
		assertEquals(3, this.deucesGame.piles[0].count()); // As the number of cards in the FoundationPile increased by one after executing the move, the test checks for the increase
		assertEquals(2, this.deucesGame.columns[0].count()); // After the move, the card count remains unchanged
		// Run Card Sanity Check
		assertEquals(new Card(Card.THREE, Card.CLUBS), this.deucesGame.piles[0].peek(0));
		assertEquals(new Card(Card.FOUR, Card.CLUBS), this.deucesGame.piles[0].peek(1));
		assertEquals(new Card(Card.FIVE, Card.CLUBS), this.deucesGame.piles[0].peek());
		assertEquals(new Card(Card.SIX, Card.CLUBS), this.deucesGame.columns[0].peek());
		// Undo the move
		assertTrue( theMove.undo(this.deucesGame) ); // The move should be successfully undone
		// Run Card Count Checks
		assertEquals(1, this.deucesGame.piles[0].count()); // Number of cards in the destination FoundationPile should be back to one
		assertEquals(4, this.deucesGame.columns[0].count()); // After undoing the move, the number of cards in the TableauPile should be back to four
		// Run Card Sanity Check
		assertEquals(new Card(Card.THREE, Card.CLUBS), this.deucesGame.piles[0].peek());
		assertEquals(new Card(Card.FOUR, Card.CLUBS), this.deucesGame.columns[0].peek());
	}
	
	public void testFailRankMove() {
		// Fix the FoundationPile and TableauPile
		ModelFactory.init(this.deucesGame.piles[0], "5C");
		ModelFactory.init(this.deucesGame.columns[0], "6D");
		// Run initial test cases
		assertEquals(1, this.deucesGame.piles[0].count());
		assertEquals(1, this.deucesGame.columns[0].count());
		// Pull out the only card from the TableauPile
		Column cardsBeingDragged = new Column(); // Create the column first
		cardsBeingDragged.add(this.deucesGame.columns[0].get()); // Pull out the 6 of Diamonds
		// Create the move
		FoundationFromTableauMove theMove = new FoundationFromTableauMove(this.deucesGame.columns[0], cardsBeingDragged, this.deucesGame.piles[0]);
		// Check if the move is valid
		assertFalse( theMove.valid(deucesGame) ); // The move shouldn't be valid
		// Run Card Count Checks
		assertEquals(1, this.deucesGame.piles[0].count()); // As only validity was checked for, the number of cards shouldn't have changed
		assertEquals(0, this.deucesGame.columns[0].count()); // As a card was removed to create the move, the count should've fallen by 1 to zero
	}
	
	public void testFailUndo() {
		// Fix the FoundationPile and TableauPile
		ModelFactory.init(this.deucesGame.piles[0], "2C");
		ModelFactory.init(this.deucesGame.columns[0], "KD");
		// Run initial test cases
		assertEquals(1, this.deucesGame.piles[0].count());
		assertEquals(1, this.deucesGame.columns[0].count());
		// Pull out the only card from the TableauPile
		Column cardsBeingDragged = new Column(); // Create the column first
		cardsBeingDragged.add(this.deucesGame.columns[0].get()); // Pull out the King of Diamonds
		// Create the move
		FoundationFromTableauMove theMove = new FoundationFromTableauMove(this.deucesGame.columns[0], cardsBeingDragged, this.deucesGame.piles[0]);
		// Attempt to undo the move
		assertFalse( theMove.undo(deucesGame) ); // We cannot undo the move
		// Run Card Count Checks
		assertEquals(1, this.deucesGame.piles[0].count()); // As only validity was checked for, the number of cards shouldn't have changed
		assertEquals(0, this.deucesGame.columns[0].count()); // As a card was removed to create the move, the count should've fallen by 1 to zero
	}
	
	@Override
	protected void tearDown() {
		gameWindow.dispose(); // To cut down on resources usage
	}
	
}