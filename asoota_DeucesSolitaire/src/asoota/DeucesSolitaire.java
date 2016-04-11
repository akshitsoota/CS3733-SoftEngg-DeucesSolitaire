package asoota;

import java.awt.Dimension;
import java.util.Random;
import java.util.Stack;

import ks.common.controller.SolitaireMouseMotionAdapter;
import ks.common.games.Solitaire;
import ks.common.games.SolitaireUndoAdapter;
import ks.common.model.Card;
import ks.common.model.Column;
import ks.common.model.Deck;
import ks.common.model.MultiDeck;
import ks.common.model.MutableInteger;
import ks.common.model.Pile;
import ks.common.view.CardImages;
import ks.common.view.ColumnView;
import ks.common.view.DeckView;
import ks.common.view.IntegerView;
import ks.common.view.PileView;
import ks.common.view.RowView;
import ks.launcher.Main;

public class DeucesSolitaire extends Solitaire {

	public static final int TOTAL_PILE_COUNT = 8;
	public static final int TOTAL_COLUMN_COUNT = 10;
	public static final int INITIAL_CARDS_LEFT = 86;
	public static final int INITIAL_SCORE = 8;
	
	private MutableInteger wastePileNumLeft;
	
	MultiDeck doubleDeck;
	Pile piles[];
	Column[] columns;
	Column wastePile;
	DeckView multiDeckView;
	PileView pileViews[];
	ColumnView[] columnViews;
	RowView wastePileRowView;
	IntegerView scoreView;
	IntegerView stockPileCountView;
	IntegerView wastePileCountView;
	
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
		initializeView();
		initializeControllers();
		// Roll out the deck as an initial move of the game
		rollOutDeck();
	}

	private void initializeModel(int seed) {
		doubleDeck = new MultiDeck("DeucesSolitaire-MultiDeck", 2);
		doubleDeck.create(seed); // Initialize with the seed given
		model.addElement(doubleDeck); // Add this element to the model
		
		// Initialize this MutableInteger for the number of cards in the WastePile
		wastePileNumLeft = new MutableInteger(0);
		
		// Create all the Pile Models
		piles = new Pile[TOTAL_PILE_COUNT];
		for(int i = 0; i < TOTAL_PILE_COUNT; i++)
			piles[i] = new Pile("DeucesSolitaire-Pile" + i);
		
		// Now, add them all to the model
		for(int i = 0; i < TOTAL_PILE_COUNT; i++)
			this.model.addElement(piles[i]);
		
		// Create all the Column Models
		columns = new Column[TOTAL_COLUMN_COUNT];
		for(int i = 0; i < TOTAL_COLUMN_COUNT; i++)
			columns[i] = new Column("DeucesSolitaire-Column" + i);
		wastePile = new Column("DeucesSolitaire-WastePile");
		
		// Now, add them all to the model
		for(int i = 0; i < TOTAL_COLUMN_COUNT; i++)
			this.model.addElement(columns[i]);
		this.model.addElement(wastePile); // Add the waste pile as well
		
		// Set up some start game values
		this.updateNumberCardsLeft(INITIAL_CARDS_LEFT);
		this.updateScore(INITIAL_SCORE);
	}
	
	private void initializeView() {
		// Get the card images as everything is placed relative to card sizes
		CardImages cardImages = getCardImages();
		
		// Now, start off by creating the multiDeck View
		multiDeckView = new DeckView(doubleDeck); // Create the MultiDeck view on the model we created
		multiDeckView.setBounds(60, 40 + (2 * cardImages.getHeight()) + (15 * cardImages.getOverlap()), cardImages.getWidth(), cardImages.getHeight());
		container.addWidget(multiDeckView); // Add this view to be shown to the user
		
		// Now, move onto by creating all the PileViews and adding them to the user container
		pileViews = new PileView[TOTAL_PILE_COUNT];
		for(int i = 0; i < TOTAL_PILE_COUNT; i++) {
			pileViews[i] = new PileView(piles[i]); // Create a new pile view with the respective Pile model
			pileViews[i].setBounds(123 + (20 * i) + (i * cardImages.getWidth()), 20, cardImages.getWidth(), cardImages.getHeight());
			this.container.addWidget(pileViews[i]); // Add this PileView to be shown to the user
		}
		
		// Now, move onto by creating all the ColumnViews and adding them to the user container
		columnViews = new ColumnView[TOTAL_COLUMN_COUNT];
		for(int i = 0; i < TOTAL_COLUMN_COUNT; i++) {
			columnViews[i] = new ColumnView(columns[i]); // Create a new ColumnView with the respective Column model
			columnViews[i].setBounds(30 + (20 * i) + (i * cardImages.getWidth()), 40 + cardImages.getHeight(), cardImages.getWidth(), cardImages.getHeight() + (13 * cardImages.getOverlap()));
			this.container.addWidget(columnViews[i]); // Add this ColumnView to be shown to the user
		}
		
		// Also, add a WastePile RowView
		wastePileRowView = new RowView(wastePile);
		wastePileRowView.setBounds(80 + cardImages.getWidth(), 40 + (2 * cardImages.getHeight()) + (15 * cardImages.getOverlap()), cardImages.getWidth() + (60 * cardImages.getOverlap()), cardImages.getHeight());
		this.container.addWidget(wastePileRowView); // Add this RowView to be shown to the user as well
		
		// Add IntegerViews now
		scoreView = new IntegerView(getScore()); // This will show the score to the user
		scoreView.setBounds(260 + (9 * cardImages.getWidth()), 20, 160, 60); // TODO: Fix the width and height as well
		this.container.addWidget(scoreView); // Add this IntegerView to be shown to the user
		
		stockPileCountView = new IntegerView(getNumLeft());
		stockPileCountView.setBounds(60, 50 + (3 * cardImages.getHeight()) + (15 * cardImages.getOverlap()), cardImages.getWidth(), 60); // TODO: Fix the width and height as well
		this.container.addWidget(stockPileCountView); // Add this IntegerView to be shown to the user as well
		
		wastePileCountView = new IntegerView(getWastePileNumLeft());
		wastePileCountView.setBounds(80 + cardImages.getWidth(), 50 + (3 * cardImages.getHeight()) + (15 * cardImages.getOverlap()), cardImages.getWidth(), 60); // TODO: Fix the width and height as well
		this.container.addWidget(wastePileCountView); // Add this IntegerView to the container to be shown to the user as well
	}

	private void initializeControllers() {
		// Set up the controllers for the MultiDeckView first
		multiDeckView.setMouseAdapter(new DeucesDeckController(DeucesSolitaire.this, doubleDeck, wastePile, wastePileNumLeft)); // TODO: Check with TA if this controller can take this MutableInteger
		multiDeckView.setMouseMotionAdapter(new SolitaireMouseMotionAdapter(DeucesSolitaire.this));
		multiDeckView.setUndoAdapter(new SolitaireUndoAdapter(DeucesSolitaire.this));
		
		// Set up Mouse Controller for the WastePileView
		wastePileRowView.setMouseAdapter(new DeucesWastePileController(DeucesSolitaire.this, wastePileRowView));
		wastePileRowView.setMouseMotionAdapter(new SolitaireMouseMotionAdapter(DeucesSolitaire.this));
		wastePileRowView.setUndoAdapter(new SolitaireUndoAdapter(DeucesSolitaire.this));
		
		// Set up the Mouse Controller for the TableauPiles
		for(int i = 0; i < TOTAL_COLUMN_COUNT; i++) {
			columnViews[i].setMouseAdapter(new DeucesTableauPileController(DeucesSolitaire.this, columnViews[i], wastePileNumLeft)); // TODO: Check with TA if this controller can take this MutableInteger
			columnViews[i].setMouseMotionAdapter(new SolitaireMouseMotionAdapter(DeucesSolitaire.this));
			columnViews[i].setUndoAdapter(new SolitaireUndoAdapter(DeucesSolitaire.this));
		}
		
		// Set up the Mouse Controller for the FoundationPiles
		for(int i = 0; i < TOTAL_PILE_COUNT; i++) {
			pileViews[i].setMouseAdapter(new DeucesFoundationPileController(DeucesSolitaire.this, pileViews[i], wastePileNumLeft)); // TODO: Check with TA if this controller can take this MutableInteger
			pileViews[i].setMouseMotionAdapter(new SolitaireMouseMotionAdapter(DeucesSolitaire.this));
			pileViews[i].setUndoAdapter(new SolitaireUndoAdapter(DeucesSolitaire.this));
		}
	}
	
	private void rollOutDeck() {
		// We unroll the entire deck and place all the Twos to the FoundationPiles
		Stack<Card> cardsRolledOut = new Stack<Card>();
		Card recentlyRolledOutCard = null;
		int foundationPileToAddTo = 0;
		// Iterate over all the cards now
		while( (recentlyRolledOutCard = doubleDeck.get()) != null ) {
			// Check if this is the card we're looking for
			if( recentlyRolledOutCard.getRank() == Card.TWO ) {
				// Don't add this to the Queue, add to the FoundationPile
				piles[foundationPileToAddTo].add(recentlyRolledOutCard);
				pileViews[foundationPileToAddTo].redraw(); // Invalidate the Foundation PileView
				// Increment the foundationPileToAddTo index
				foundationPileToAddTo++;
			} else {
				// Add the card to the stack
				cardsRolledOut.add(recentlyRolledOutCard);
			}
		}
		// Now that we've rolled out all cards, add 10 cards to the Tableau as well
		int tableauPileToAddTo = 0;
		while( tableauPileToAddTo < 10 ) {
			columns[tableauPileToAddTo].add(cardsRolledOut.pop()); // Remove a card and add it to the TableauPile
			columnViews[tableauPileToAddTo].redraw(); // Invalidate the ColumnView
			// Increment the tableau pile count
			tableauPileToAddTo++;
		}
		// Now that we've filled both the FoundationPiles and the TableauPiles, send all the other cards back
		while( cardsRolledOut.size() != 0 ) // Keep unrolling till the Stack is empty
			doubleDeck.add(cardsRolledOut.pop()); // Add the card to the MultiDeck
		// Refresh the MultiDeckView
		multiDeckView.redraw(); // Invalidate the MultiDeckView
	}
	
	private MutableInteger getWastePileNumLeft() {
		return wastePileNumLeft;
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(1300, 840);
	}
	
	public static void main(String[] args) {
		// Start up a new window with the DeucesSolitaire Variant with a random number as the seed
		Main.generateWindow(new DeucesSolitaire(), Deck.OrderByRank);// TODO: new Random().nextInt());
	}

}