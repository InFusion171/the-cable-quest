package thecablequest.player;

import breitband.preset.*;
import thecablequest.gameboard.*;
import thecablequest.Inventory;
import thecablequest.gameboard.XMLElementClasses.*;
import java.io.*;
import java.util.*;

/**
 * Abstract class representing a basic player in the game.
 * Provides common functionalities and properties for all players.
 */

public abstract class BasicPlayer implements Player {
    private String name;
    protected int playerid;
    protected ArrayList<Contract> availableContracts = new ArrayList<>();

    public Inventory myInventory; 

    
    protected int score;
    
    public GameBoard board;

    public static ArrayList<BasicPlayer> allPlayers = new ArrayList<>();


    private static final int MAX_PLAYERS = 5;

    int numPlayers;



    /**
     * Constructor to initialize a BasicPlayer with a name.
     * 
     * @param name The name of the player.
     */
    public BasicPlayer(String name) {
        this.name = name;
        
        
        
    }

    /**
     * Initializes the player with the game configuration, available contracts, number of players, and player ID.
     * 
     * @param gameConfigurationXML Reader for the game configuration XML.
     * @param availableContracts ImmutableList of available contract IDs.
     * @param numPlayers Number of players in the game.
     * @param playerid The ID of this player.
     */
    @Override
    public void init(Reader gameConfigurationXML, ImmutableList<String> availableContracts, int numPlayers, int playerid) {
        
        this.board = new GameBoard(availableContracts, numPlayers);
        this.numPlayers = numPlayers;
        myInventory = board.getPlayerInventorysById().get(playerid);

        score = board.getPlayerInventorysById().get(playerid).getPlayerPoints();

        this.playerid = playerid;
        if (playerid > MAX_PLAYERS) {
            throw new IllegalStateException("Cannot create more than " + MAX_PLAYERS + " players.");
        }

        allPlayers.add(this);

        // ConfigurationElement should be read from gameConfigurationXML
        // allCables and regions should be accessed through the instance of the GameBoard
        // available Contracts should be generated in the main method
        
    }

    /**
     * Returns the name of the player.
     * 
     * @return The name of the player.
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Returns the player ID.
     * 
     * @return The player ID.
     */
    public int getPlayerid() {
        return playerid;
    }

    

    /**
     * Requests a move from the player. Should be overridden by subclasses to provide specific move logic.
     * 
     * @return A Move object representing the player's move.
     */
    @Override
    public Move request() {
        if (!myTurn()) {
            throw new IllegalStateException("It is not your turn.");
        }


        // Temporary move for test purposes
        Map<String, Integer> pickedUpCables = Map.of("Glasfaser", 5);
        return new Move("1", pickedUpCables);
    }

    /**
     * Updates the player with the opponent's move.
     * 
     * @param opponentMove The move made by the opponent.
     */
    @Override
    public void update(Move opponentMove) {

        board.makeMove(opponentMove);
        
    }

    /**
     * Retrieves the player's score.
     * 
     * @param applyEndgameScoring Whether to apply endgame scoring.
     * @return The player's score.
     */
    @Override
    public int getScore(boolean applyEndgameScoring) {
        int tempScore = 0;
        if (applyEndgameScoring) {
            
            for (Contract contract : myInventory.getFulfilledContracts()) {
                tempScore += contract.getValue();
            }
            for (Contract contract : myInventory.getNotFulfilledContracts()){
                tempScore -= contract.getValue();
            }
        }
        return score+tempScore;
    }

    /**
     * Verifies the game results after the game ends.
     * 
     * @param scores ImmutableList of scores.
     */
    @Override
    public void verifyGame(ImmutableList<Integer> scores) {
        // scores: Eine Liste der finalen Punktestände von allen Spielern.
        // Die Liste ist nach der SpielerID von den Spielern sortiert.

        

        for (BasicPlayer player : allPlayers){
            if (player.getScore(true) != scores.get(player.getPlayerid()-1)){
                throw new IllegalStateException("Scores don't match!");
            }
        }
        System.out.println("scores Match");
    }
    

    /**
     * Checks if it is the player's turn.
     * 
     * @return True if it is the player's turn, false otherwise.
     */
    public boolean myTurn() {
        return board.getCurrentPlayerId() == playerid;
    }
    /**
     * gives information over to main
     * true if the game should not continue
     */
    public boolean didMyLastTurn(){
        return !board.isRunning();
    }
}
