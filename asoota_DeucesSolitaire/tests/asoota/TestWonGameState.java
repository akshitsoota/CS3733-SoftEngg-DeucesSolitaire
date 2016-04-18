package asoota;

import junit.framework.TestCase;
import ks.client.gamefactory.GameWindow;
import ks.common.model.Deck;
import ks.launcher.Main;
import ks.tests.model.ModelFactory;

public class TestWonGameState extends TestCase {

	private DeucesSolitaire deucesGame;
	private GameWindow gameWindow;
	
	@Override
	protected void setUp() {
		deucesGame = new DeucesSolitaire(); // Instantiate a DeucesSolitaire game
		gameWindow = Main.generateWindow(deucesGame, Deck.OrderBySuit); // Create the GameWindow as a testing environment
	}
	
	public void testWonGameState() {
		// Run initial test case
		assertFalse( this.deucesGame.hasWon() ); // By default, the game shouldn't be in winning state
		// Set up some winning conditions
		this.deucesGame.getScore().setValue(104);
		for(int i = 0; i < 2; i++)
			ModelFactory.init(this.deucesGame.piles[i], "AC"); // Add 2 Ace of Clubs
		for(int i = 2; i < 4; i++)
			ModelFactory.init(this.deucesGame.piles[i], "AS"); // Add 2 Ace of Spades
		for(int i = 4; i < 6; i++)
			ModelFactory.init(this.deucesGame.piles[i], "AD"); // Add 2 Ace of Diamonds
		for(int i = 6; i < 8; i++)
			ModelFactory.init(this.deucesGame.piles[i], "AH"); // Add 2 Ace of Hearts
		// Now test if the user has one
		assertTrue( this.deucesGame.hasWon() ); // We setup all the winning conditions and now the game is in the state that the user has won
	}
	
	@Override
	protected void tearDown() {
		gameWindow.dispose(); // To cut down on resources usage
	}
	
}