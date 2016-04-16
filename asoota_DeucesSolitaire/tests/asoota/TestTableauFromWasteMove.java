package asoota;

import junit.framework.TestCase;
import ks.client.gamefactory.GameWindow;
import ks.common.model.Card;
import ks.common.model.Deck;
import ks.launcher.Main;
import ks.tests.model.ModelFactory;

public class TestTableauFromWasteMove extends TestCase {

	private DeucesSolitaire deucesGame;
	private GameWindow gameWindow;
	
	@Override
	protected void setUp() {
		deucesGame = new DeucesSolitaire(); // Instantiate a DeucesSolitaire game
		gameWindow = Main.generateWindow(deucesGame, Deck.OrderBySuit); // Create the GameWindow as a testing environment
		
		// Fix the cards in the WastePile
		ModelFactory.init(this.deucesGame.wastePile, "8S 9H 5C 7D 10S 6C QH 4H 8D");
		// Fix the cards in the Tableau Piles
		ModelFactory.init(this.deucesGame.columns[0], "9S");
		ModelFactory.init(this.deucesGame.columns[1], "JS");
		ModelFactory.init(this.deucesGame.columns[2], "7C");
		ModelFactory.init(this.deucesGame.columns[3], "4C");
		ModelFactory.init(this.deucesGame.columns[4], "9H");
		ModelFactory.init(this.deucesGame.columns[5], "10H");
		ModelFactory.init(this.deucesGame.columns[6], "5H");
		ModelFactory.init(this.deucesGame.columns[7], "9D");
		ModelFactory.init(this.deucesGame.columns[8], "7D");
		ModelFactory.init(this.deucesGame.columns[9], "1H");
	}

	public void testSameSuit() {
		// Verify a few things about the WastePile
		assertEquals(9, this.deucesGame.wastePile.count()); // There are actually 9 cards in the WastePile
		for(int i = 0; i < DeucesSolitaire.TOTAL_COLUMN_COUNT; i++)
			assertEquals(1, this.deucesGame.columns[i].count()); // Each column should only have one card
		
		// Let us try a moved with the fixed cards
		Card wastePileCard1 = this.deucesGame.wastePile.get(); // TODO: Check if Waste Pile count falling by one is okay
		// These moves shouldn't be possible as a Diamond is being dragged to non-Diamond
		TableauFromWasteMove move1 = new TableauFromWasteMove(this.deucesGame.wastePile, wastePileCard1, this.deucesGame.columns[0]);
		assertFalse( move1.valid(deucesGame) ); // Verify it is not a valid move
		assertEquals(8, this.deucesGame.wastePile.count()); // Verify that no card was moved excluding the one that was pulled out for testing purposes
		assertEquals(1, this.deucesGame.columns[0].count()); // Verify that no card was added to that column
		
		TableauFromWasteMove move2 = new TableauFromWasteMove(this.deucesGame.wastePile, wastePileCard1, this.deucesGame.columns[2]);
		assertFalse( move2.valid(deucesGame) ); // Verify it is not a valid move
		assertEquals(8, this.deucesGame.wastePile.count()); // Verify that no card was moved excluding the one that was pulled out for testing purposes
		assertEquals(1, this.deucesGame.columns[2].count()); // Verify that no card was added to that column
		
		TableauFromWasteMove move3 = new TableauFromWasteMove(this.deucesGame.wastePile, wastePileCard1, this.deucesGame.columns[9]);
		assertFalse( move3.valid(deucesGame) ); // Verify it is not a valid move
		assertEquals(8, this.deucesGame.wastePile.count()); // Verify that no card was moved excluding the one that was pulled out for testing purposes
		assertEquals(1, this.deucesGame.columns[9].count()); // Verify that no card was added to that column
		
		// This should be a valid move
		TableauFromWasteMove move4 = new TableauFromWasteMove(this.deucesGame.wastePile, wastePileCard1, this.deucesGame.columns[7]);
		assertTrue( move4.valid(deucesGame) );
		// One should be able to perform it as well
		assertTrue( move4.doMove(deucesGame) );
		// Once this card is added, verify a few things
		assertEquals(8, this.deucesGame.wastePile.count()); // One card should be removed from the WastePile
		assertEquals(2, this.deucesGame.columns[7].count()); // As the move was done, the destination column should now have one more card
		// Now attempt to undo this move
		assertTrue( move4.undo(deucesGame) ); // This should be successful
		assertEquals(9, this.deucesGame.wastePile.count()); // The card that was added to the Column should now be added back to the WastePile
		assertEquals(1, this.deucesGame.columns[7].count()); // As the card was removed, the count for that column should be reset to 1
	}
	
	public void testColumnGrowingDownwards() {
		// Verify a few things about the WastePile
		assertEquals(9, this.deucesGame.wastePile.count()); // There are actually 8 cards in the WastePile
		for(int i = 0; i < DeucesSolitaire.TOTAL_COLUMN_COUNT; i++)
			assertEquals(1, this.deucesGame.columns[i].count()); // Each column should only have one card
		// Now test that the columns only grow downwards
		Card wastePileCard1 = this.deucesGame.wastePile.get(); // Pull out a card from the WastePile
		// This should be a 8 of Diamonds. It should only be possible to place it to a 9 of Diamonds.
		// We will try to place it on 7 of Diamonds
		TableauFromWasteMove move1 = new TableauFromWasteMove(this.deucesGame.wastePile, wastePileCard1, this.deucesGame.columns[8]);
		assertFalse( move1.valid(deucesGame) ); // This move should not be valid
		// Let us be sure that nothing took place
		assertEquals(8, this.deucesGame.wastePile.count()); // The number of cards should be 8 as we've only pulled out one card for testing purposes
		assertEquals(1, this.deucesGame.columns[8].count()); // Nothing should've been moved to this column
		
		// Now, let us try to move this 8 of Diamonds to a 9 of Diamond
		TableauFromWasteMove move2 = new TableauFromWasteMove(this.deucesGame.wastePile, wastePileCard1, this.deucesGame.columns[7]);
		assertTrue( move2.valid(deucesGame) ); // This move should be possible
		// Let us perform the move and be sure that it actually took place
		assertTrue( move2.doMove(deucesGame) ); // The move should've actually been done
		assertEquals(8, this.deucesGame.wastePile.count()); // The number of cards in the WastePile should remain to 8.
		assertEquals(2, this.deucesGame.columns[7].count()); // As the move was performed, the card should've been added to the column (TableauPile)
		
		// Let us attempt to undo this successful move
		assertTrue( move2.undo(deucesGame) ); // This undo move should be possible to execute and should be done on this line
		assertEquals(9, this.deucesGame.wastePile.count()); // As the move was undone, the card is added back to the WastePile
		assertEquals(1, this.deucesGame.columns[7].count()); // As the move was undo, the number of cards in the column should be down to 1
	}
	
	public void testForTopCardAce() {
		// Fix the WastePile a little differently
		ModelFactory.init(this.deucesGame.wastePile, "2S 1D KC");
		// Fix a few of the TableauPiles
		ModelFactory.init(this.deucesGame.columns[0], "1S");
		ModelFactory.init(this.deucesGame.columns[1], "1D");
		ModelFactory.init(this.deucesGame.columns[2], "1C");
		// Run a few initial tests
		assertEquals(3, this.deucesGame.wastePile.count()); // The initial number of cards in the WastePile is 3
		for(int i = 0; i < DeucesSolitaire.TOTAL_COLUMN_COUNT; i++)
			assertEquals(1, this.deucesGame.columns[i].count()); // Each of the columns (TableauPiles) should be have only one card
		// Now attempt to create some moves
		TableauFromWasteMove move1 = new TableauFromWasteMove(this.deucesGame.wastePile, this.deucesGame.wastePile.get(), this.deucesGame.columns[2]);
		// Now run a few tests on this move
		assertTrue( move1.valid(deucesGame) ); // This should be a valid move as we're moving a King of Clubs to Ace of Clubs
		assertTrue( move1.doMove(deucesGame) ); // The move should be possible to execute
		assertEquals(2, this.deucesGame.wastePile.count()); // As one card was pulled out the number of cards in the WastePile should've fallen by one
		assertEquals(2, this.deucesGame.columns[2].count()); // As we've performed the move, the number of cards in the Column should've increased by one
		// Undo this move and check necessary stuff
		assertTrue( move1.undo(deucesGame) ); // This should be able to undone as we actually performed the move
		assertEquals(3, this.deucesGame.wastePile.count()); // The move was undone so the card should be added back to the WastePile
		assertEquals(1, this.deucesGame.columns[2].count()); // The move was undone and hence the card that was there should no longer be there
		
		// Test some other fail moves
		this.deucesGame.wastePile.get(); // Remove this card as we don't care about it
		assertEquals(2, this.deucesGame.wastePile.count()); // Assert that the card was actually removed from the WastePile
		// Create some of the fail moves
		TableauFromWasteMove move2 = new TableauFromWasteMove(this.deucesGame.wastePile, this.deucesGame.wastePile.get(), this.deucesGame.columns[1]);
		assertFalse( move2.valid(deucesGame) ); // This move shouldn't be possible as we're trying to move an Ace of Diamonds on another Ace of Diamonds
		// Check that nothing actually happened
		assertEquals(1, this.deucesGame.wastePile.count()); // As the two cards were removed this count should be 1
		assertEquals(1, this.deucesGame.columns[1].count()); // As the move wasn't valid, the number of cards on that TableauPile should be unchanged
		// Create another fail move
		TableauFromWasteMove move3 = new TableauFromWasteMove(this.deucesGame.wastePile, this.deucesGame.wastePile.get(), this.deucesGame.columns[0]);
		assertFalse( move3.valid(deucesGame) ); // This move shouldn't be possible as only a King can be placed on an Ace card
		// Check that nothing actually happened
		assertEquals(0, this.deucesGame.wastePile.count()); // There should be no cards in the WastePile as they were all pulled out
		assertEquals(1, this.deucesGame.columns[0].count()); // As the move wasn't done, the number of cards in the destination TableuPile should remain one
	}
	
	public void testValidToEmptySpotAndExcessiveUndo() {
		// Fix some things in the TableauPile
		ModelFactory.init(this.deucesGame.columns[0], ""); // Remove all cards from this
		// Now run initial test cases
		assertEquals(9, this.deucesGame.wastePile.count()); // Initially WastePile count should be 9
		assertEquals(0, this.deucesGame.columns[0].count()); // Initially the tableau pile from which we removed the card should have zero cards on it
		// Loop over the rest
		for(int i = 1; i < DeucesSolitaire.TOTAL_COLUMN_COUNT; i++)
			assertEquals(1, this.deucesGame.columns[i].count()); // As the other TableauPile should have the one card
		
		// Now pull a card out from the WastePile and add it to the TableauPile
		TableauFromWasteMove move = new TableauFromWasteMove(this.deucesGame.wastePile, this.deucesGame.wastePile.get(), this.deucesGame.columns[0]);
		// Assert that this a valid move as it should be
		assertTrue( move.valid(deucesGame) );
		// Check that nothing changed
		assertEquals(8, this.deucesGame.wastePile.count()); // As one card was pulled out for the move, the count should've fallen by one
		assertEquals(0, this.deucesGame.columns[0].count()); // AS there was originally zero cards, there shouldn't be any cards right not there as well
		// Now, perform the move
		assertTrue( move.doMove(deucesGame) );
		assertEquals(8, this.deucesGame.wastePile.count()); // The waste pile card count should be 8
		assertEquals(1, this.deucesGame.columns[0].count()); // The move was done so the count should now be 1
		// Now undo the move
		assertTrue( move.undo(deucesGame) ); // The undo should also be possible and should be done after this line
		assertEquals(9, this.deucesGame.wastePile.count()); // The waste pile card count is up to 9 as we just undoed the move
		assertEquals(0, this.deucesGame.columns[0].count()); // There are no more cards in the column
		// We shouldn't be able to undo now as the TableauPile is empty
		assertFalse( move.undo(deucesGame) );
	}
	
	@Override
	protected void tearDown() {
		gameWindow.dispose(); // To cut down on resources usage
	}
	
}