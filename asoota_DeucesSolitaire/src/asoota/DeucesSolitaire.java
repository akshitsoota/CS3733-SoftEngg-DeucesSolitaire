package asoota;

import ks.common.games.Solitaire;
import ks.common.model.Column;
import ks.common.model.MultiDeck;
import ks.common.model.Pile;

public class DeucesSolitaire extends Solitaire {

	private static final int TOTAL_PILE_COUNT = 8;
	private static final int TOTAL_COLUMN_COUNT = 10;
	private static final int INITIAL_CARDS_LEFT = 100;
	private static final int INITIAL_SCORE = 8;
	
	private MultiDeck doubleDeck;
	private Pile piles[];
	private Column[] columns;
	private Column wastePile;

	@Override
	public String getName() {
		return "DeucesSolitaire";
	}

	@Override
	public boolean hasWon() {
		// TODO: This has to be fixed, for now we will say false so that the game keeps going
		return false;
	}

	@Override
	public void initialize() {
		initializeModel(getSeed());
	}

	private void initializeModel(int seed) {
		doubleDeck = new MultiDeck("DeucesSolitaire-MultiDeck", 2);
		doubleDeck.create(seed); // Initialize with the seed given
		model.addElement(doubleDeck); // Add this element to the model
		
		// Create all the Pile Models
		piles = new Pile[8];
		for(int i = 0; i < TOTAL_PILE_COUNT; i++)
			piles[i] = new Pile("DeucesSolitaire-Pile" + i);
		
		// Now, add them all to the model
		for(int i = 0; i < TOTAL_PILE_COUNT; i++)
			model.addElement(piles[i]);
		
		// Create all the Column Models
		columns = new Column[10];
		for(int i = 0; i < TOTAL_COLUMN_COUNT; i++)
			columns[i] = new Column("DeucesSolitaire-Column" + i);
		wastePile = new Column("DeucesSolitaire-WastePile");
		
		// Now, add them all to the model
		for(int i = 0; i < TOTAL_COLUMN_COUNT; i++)
			model.addElement(columns[i]);
		model.addElement(wastePile); // Add the waste pile as well
		
		// Set up some start game values
		this.updateNumberCardsLeft(INITIAL_CARDS_LEFT);
		this.updateScore(INITIAL_SCORE);
	}

}