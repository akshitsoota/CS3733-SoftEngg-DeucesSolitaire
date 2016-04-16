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
		ModelFactory.init(this.deucesGame.columns[0], "1S 2S 3S 4S");
		ModelFactory.init(this.deucesGame.columns[1], "1S");
	}
	
	@Override
	protected void tearDown() {
		gameWindow.dispose(); // To cut down on resources usage
	}
	
}
