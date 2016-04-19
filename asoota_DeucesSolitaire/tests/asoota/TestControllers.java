package asoota;

import java.awt.event.MouseEvent;

import ks.client.gamefactory.GameWindow;
import ks.common.model.Card;
import ks.common.model.Deck;
import ks.launcher.Main;
import ks.tests.KSTestCase;
import ks.tests.model.ModelFactory;

public class TestControllers extends KSTestCase {

	private DeucesSolitaire deucesGame;
	private GameWindow gameWindow;
	
	@Override
	protected void setUp() {
		deucesGame = new DeucesSolitaire(); // Instantiate a DeucesSolitaire game
		gameWindow = Main.generateWindow(deucesGame, Deck.OrderBySuit); // Create the GameWindow as a testing environment
	}
	
	public void testOneDeal() {
		// Run initial test cases
		assertEquals(0, this.deucesGame.wastePile.count());
		assertEquals(86, this.deucesGame.doubleDeck.count());
		// Now perform the press
		MouseEvent deckPress = createPressed(this.deucesGame, this.deucesGame.multiDeckView, 1, 1);
		this.deucesGame.multiDeckView.getMouseManager().handleMouseEvent(deckPress); // Send off the mouse press
		// Now perform some tests
		assertEquals(1, this.deucesGame.wastePile.count()); // As one card was dealt, the number of cards in the WastePile should've increased by one
		assertEquals(85, this.deucesGame.doubleDeck.count()); // As one card was dealt out, the number of cards in the MultiDeck should fall by oe
 	}
	
	public void testWastePileToFoundation() {
		// Fix the WastePile and FoundationPile
		ModelFactory.init(this.deucesGame.wastePile, "5C KD QC");
		ModelFactory.init(this.deucesGame.piles[0], "JC");
		// Run initial test cases
		assertEquals(3, this.deucesGame.wastePile.count());
		assertEquals(1, this.deucesGame.piles[0].count());
		// Now perform the drag from the WastePile to the FoundationPile
		MouseEvent wastePilePress = createPressed(this.deucesGame, this.deucesGame.wastePileRowView, this.deucesGame.getCardImages().getOverlap() * 3, 2);
		this.deucesGame.wastePileRowView.getMouseManager().handleMouseEvent(wastePilePress); // Send off the mouse press
		// Now, release the card on the FoundationPile
		MouseEvent foundationPileReleased = createReleased(this.deucesGame, this.deucesGame.pileViews[0], 1, 1);
		this.deucesGame.pileViews[0].getMouseManager().handleMouseEvent(foundationPileReleased); // Send off the mouse release to trigger the drag event
		// Now perform some tests
		assertEquals(2, this.deucesGame.wastePile.count()); // One card was dragged out, so the number of cards in the WastePile fall by one
		assertEquals(2, this.deucesGame.piles[0].count()); // As a card was dragged here, the number of cards in the WastePile should've increased by one
		// Perform Card Sanity Checks
		assertEquals(new Card(Card.QUEEN, Card.CLUBS), this.deucesGame.piles[0].peek());
		assertEquals(new Card(Card.JACK, Card.CLUBS), this.deucesGame.piles[0].peek(0));
		assertEquals(new Card(Card.KING, Card.DIAMONDS), this.deucesGame.wastePile.peek());
		assertEquals(new Card(Card.FIVE, Card.CLUBS), this.deucesGame.wastePile.peek(0));
	}
	
	public void testFailFoundationRelease() {
		// Fix the FoundationPile
		ModelFactory.init(this.deucesGame.piles[0], "5D");
		// Run initial test cases
		assertEquals(1, this.deucesGame.piles[0].count());
		// Now perform a Release event on the FoundationPile
		MouseEvent releaseOnFoundation = createReleased(this.deucesGame, this.deucesGame.pileViews[0], 1, 1);
		this.deucesGame.pileViews[0].getMouseManager().handleMouseEvent(releaseOnFoundation); // Send off the Mouse Release to trigger the end of a drag
		// Run final test cases
		assertEquals(1, this.deucesGame.piles[0].count());
	}
	
	public void testWasteToTableauMove() {
		// Fix the WastePile and the TableauPile
		ModelFactory.init(this.deucesGame.columns[0], "5C");
		ModelFactory.init(this.deucesGame.columns[1], "");
		ModelFactory.init(this.deucesGame.wastePile, "3C 4C");
		// Run initial test cases
		assertEquals(1, this.deucesGame.columns[0].count());
		assertEquals(0, this.deucesGame.columns[1].count());
		assertEquals(2, this.deucesGame.wastePile.count());
		// Now perform the drag from the WastePile to the first TableauPile
		MouseEvent wastePilePress1 = createPressed(this.deucesGame, this.deucesGame.wastePileRowView, this.deucesGame.getCardImages().getOverlap() * 2, 2);
		this.deucesGame.wastePileRowView.getMouseManager().handleMouseEvent(wastePilePress1); // Send off the mouse press
		// Now, release the mouse on the TableauPile[0]
		MouseEvent tableauPileRelease1 = createReleased(this.deucesGame, this.deucesGame.columnViews[0], 1, 1);
		this.deucesGame.columnViews[0].getMouseManager().handleMouseEvent(tableauPileRelease1); // Send off the mouse release to finish the drag
		// Now perform some tests
		assertEquals(2, this.deucesGame.columns[0].count()); // The number of cards in the 0th TableauPile should've increased by one as it was a valid move
		assertEquals(0, this.deucesGame.columns[1].count()); // The number of cards shouldn't have changed here as nothing happened here
		assertEquals(1, this.deucesGame.wastePile.count()); // As one card was dragged out, the number of cards should've fallen by one
		// Perform Card Sanity Checks
		assertEquals(new Card(Card.FIVE, Card.CLUBS), this.deucesGame.columns[0].peek(0));
		assertEquals(new Card(Card.FOUR, Card.CLUBS), this.deucesGame.columns[0].peek(1));
		assertEquals(new Card(Card.THREE, Card.CLUBS), this.deucesGame.wastePile.peek());
		// Perform the move from the WastePile to the second TableauPile
		MouseEvent wastePilePress2 = createPressed(this.deucesGame, this.deucesGame.wastePileRowView, 1, 1);
		this.deucesGame.wastePileRowView.getMouseManager().handleMouseEvent(wastePilePress2); // Send off the mouse press
		// Now, release the mouse on the TableauPile[1]
		MouseEvent tableauPileRelease2 = createReleased(this.deucesGame, this.deucesGame.columnViews[1], 1, 1);
		this.deucesGame.columnViews[1].getMouseManager().handleMouseEvent(tableauPileRelease2); // Send off the mouse release to finish the drag
		// Now perform some tests
		assertEquals(2, this.deucesGame.columns[0].count()); // Nothing should've changed here as the move wasn't targeted to this TableauPile
		assertEquals(1, this.deucesGame.columns[1].count()); // The number of cards in this TableauPile rose by one as one card was dragged here
		assertEquals(0, this.deucesGame.wastePile.count()); // The last card was removed from the WastePile
		// Card Sanity Checks
		assertEquals(new Card(Card.THREE, Card.CLUBS), this.deucesGame.columns[1].peek());
	}
	
	public void testTableauToTableauMove() {
		// Fix the TableauPiles
		ModelFactory.init(this.deucesGame.columns[0], "8C 7C 6C 5C");
		ModelFactory.init(this.deucesGame.columns[1], "");
		ModelFactory.init(this.deucesGame.columns[2], "7C");
		// Run initial test cases
		assertEquals(4, this.deucesGame.columns[0].count());
		assertEquals(0, this.deucesGame.columns[1].count());
		assertEquals(1, this.deucesGame.columns[2].count());
		// Now perform a drag of 2 cards from TableauPile[0] to TableauPile[2]
		MouseEvent tableauPilePress1 = createPressed(this.deucesGame, this.deucesGame.columnViews[0], 2, (this.deucesGame.getCardImages().getOverlap() * 2) + 1);
		this.deucesGame.columnViews[0].getMouseManager().handleMouseEvent(tableauPilePress1); // Send off the mouse press
		// Now, release the mouse on TableauPile[2]
		MouseEvent tableauPileRelease1 = createReleased(this.deucesGame, this.deucesGame.columnViews[2], 2, this.deucesGame.getCardImages().getOverlap());
		this.deucesGame.columnViews[2].getMouseManager().handleMouseEvent(tableauPileRelease1); // Release the mouse to finish the drag
		// Run some tests now
		assertEquals(2, this.deucesGame.columns[0].count()); // As two cards were dragged out of here, the number of cards should've fallen by two
		assertEquals(0, this.deucesGame.columns[1].count()); // No changes were made to this TableauPile so the count remains the same
		assertEquals(3, this.deucesGame.columns[2].count()); // As two cards were dragged to here, the number of cards should've increased by two
		// Perform Card Sanity Checks
		assertEquals(new Card(Card.SEVEN, Card.CLUBS), this.deucesGame.columns[2].peek(0));
		assertEquals(new Card(Card.SIX, Card.CLUBS), this.deucesGame.columns[2].peek(1));
		assertEquals(new Card(Card.FIVE, Card.CLUBS), this.deucesGame.columns[2].peek(2));
		assertEquals(new Card(Card.EIGHT, Card.CLUBS), this.deucesGame.columns[0].peek(0));
		assertEquals(new Card(Card.SEVEN, Card.CLUBS), this.deucesGame.columns[0].peek(1));
		// Now perform a drag of a card from TableauPile[0] to TableauPile[1]
		MouseEvent tableauPilePress2 = createPressed(this.deucesGame, this.deucesGame.columnViews[0], 2, this.deucesGame.getCardImages().getOverlap() + 1);
		this.deucesGame.columnViews[0].getMouseManager().handleMouseEvent(tableauPilePress2); // Send off the mouse press
		// Now, release the mouse on TableauPile[1]
		MouseEvent tableauPileRelease2 = createReleased(this.deucesGame, this.deucesGame.columnViews[1], 2, this.deucesGame.getCardImages().getOverlap());
		this.deucesGame.columnViews[1].getMouseManager().handleMouseEvent(tableauPileRelease2); // Release the mouse to finish the drag
		// Run some tests now
		assertEquals(1, this.deucesGame.columns[0].count()); // As one card was dragged out of here, the number of cards should've fallen by one to one
		assertEquals(1, this.deucesGame.columns[1].count()); // As one card was dragged to here, the number of cards increased by one
		assertEquals(3, this.deucesGame.columns[2].count()); // No changes were made to this TableauPile so the count remains the same
		// Perform Card Sanity Checks
		assertEquals(new Card(Card.EIGHT, Card.CLUBS), this.deucesGame.columns[0].peek());
		assertEquals(new Card(Card.SEVEN, Card.CLUBS), this.deucesGame.columns[1].peek());		
	}
	
	public void testTableauToFoundationMove() {
		// Fix the TableauPiles
		ModelFactory.init(this.deucesGame.columns[0], "8C 7C 6C 5C");
		ModelFactory.init(this.deucesGame.piles[0], "2C 3C 4C");
		// Run initial test cases
		assertEquals(4, this.deucesGame.columns[0].count());
		assertEquals(3, this.deucesGame.piles[0].count());
		// Now perform a drag of 3 cards from TableauPile[0] to FoundationPile[0]
		MouseEvent tableauPilePress = createPressed(this.deucesGame, this.deucesGame.columnViews[0], 2, this.deucesGame.getCardImages().getOverlap() + 1);
		this.deucesGame.columnViews[0].getMouseManager().handleMouseEvent(tableauPilePress); // Send off the mouse press
		// Now, release the mouse on FoundationPile[0]
		MouseEvent foundationPileRelease = createReleased(this.deucesGame, this.deucesGame.pileViews[2], 2, 1);
		this.deucesGame.pileViews[0].getMouseManager().handleMouseEvent(foundationPileRelease); // Release the mouse to finish the drag
		// Run some final test cases
		assertEquals(1, this.deucesGame.columns[0].count()); // As three cards were pulled out of here to be dragged to the FoundationPile, the number of cards fell by 3 to one
		assertEquals(6, this.deucesGame.piles[0].count()); // As three cards were dragged into this FoundationPile, the number of cards increased by 3 to 6
		// Perform Card Sanity Checks
		assertEquals(new Card(Card.EIGHT, Card.CLUBS), this.deucesGame.columns[0].peek());
		assertEquals(new Card(Card.SEVEN, Card.CLUBS), this.deucesGame.piles[0].peek());		
	}
	
	@Override
	protected void tearDown() {
		gameWindow.dispose(); // To cut down on resources usage
	}
	
}
