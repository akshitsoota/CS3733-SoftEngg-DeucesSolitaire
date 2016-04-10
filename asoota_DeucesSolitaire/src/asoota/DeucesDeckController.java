package asoota;

import java.awt.event.MouseEvent;

import ks.common.controller.SolitaireReleasedAdapter;
import ks.common.model.Column;
import ks.common.model.MultiDeck;
import ks.common.model.MutableInteger;

/**
 * Controls all actions with the MultiDeckView on the DeucesSolitaire MultiDeck
 * @author asoota
 */
public class DeucesDeckController extends SolitaireReleasedAdapter {

	private DeucesSolitaire theDeucesGame;
	private MultiDeck multiDeck;
	private Column wastePile;
	private MutableInteger wastePileNumLeft;

	public DeucesDeckController(DeucesSolitaire deucesSolitaire, MultiDeck doubleDeck, Column wastePile, MutableInteger wastePileNumLeft) {
		super(deucesSolitaire); // Let the super do its job
		// Store all the parameters now
		this.wastePileNumLeft = wastePileNumLeft;
		this.theDeucesGame = deucesSolitaire;
		this.multiDeck = doubleDeck;
		this.wastePile = wastePile;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		super.mousePressed(e); // Let the super do its job
		// Create the DealOneCardMove class
		DealOneCardMove theMove = new DealOneCardMove(multiDeck, wastePile, wastePileNumLeft);
		// Attempt the move
		if( theMove.valid(theDeucesGame) && theMove.doMove(theDeucesGame) ) {
			// If we were able to perform the move and it actually was performed, update the UI
			theDeucesGame.pushMove(theMove); // Ask the DeucesSolitaire game to udpate with the Move
			theDeucesGame.refreshWidgets(); // Cause the UI to invalidate
		}
	}

}