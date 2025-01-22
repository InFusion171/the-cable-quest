package thecablequest.player;

import breitband.preset.*;
import breitband.preset.PlayerGUIAccess;
import thecablequest.gameboard.XMLElementClasses.*;
import java.util.*;

/**
 * Represents a human player in the game. The player interacts with the game through a GUI.
 */
public class Human extends BasicPlayer {
    private PlayerGUIAccess guiAccess;

    /**
     * Constructor to initialize a Human player with a name and GUI access.
     * 
     * @param name The name of the human player.
     * @param guiAccess The GUI access interface for interacting with the player.
     */
    public Human(String name, PlayerGUIAccess guiAccess) {
        super(name);
        this.guiAccess = guiAccess;
    }

    /**
     * Requests a move from the human player through the GUI. 
     * The move is validated before being accepted.
     * 
     * @return A Move object representing the human player's move.
     */
    @Override
    public Move request() {
        //super.request();
        /*
        Move move;

        do {
            move = guiAccess.requestMoveFromCurrentHumanPlayer();
            if (!board.isLegal(move,this.getPlayerid())) {
                System.out.println("Invalid move! Please try again.");
            }
        } while (!board.isLegal(move, playerid));

        /**if (move.didAcceptContract()) { //passsiert in makeMove, unnötig
            String acceptedContractID = move.getAcceptedContractID();
            // myInventory.acceptContract(findContract(acceptedContractID));
        }

        Map<String, Integer> pickedUpCables = move.getPickedUpCables();
        myInventory.pickUpCables(pickedUpCables);*

        board.makeMove(move, playerid);
        */
        
        return guiAccess.requestMoveFromCurrentHumanPlayer();
    }
}
