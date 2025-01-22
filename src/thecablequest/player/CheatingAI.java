package thecablequest.player;

import thecablequest.gameboard.GameBoard;
import breitband.preset.*;
import thecablequest.Inventory;
import thecablequest.gameboard.XMLElementClasses.*;
import java.util.Random;
import java.util.*;

/**
 * Represents a cheating AI player that mostly makes random moves but occasionally cheats.
 */
public class CheatingAI extends RandomAI {

    private Random random;
 

    private ArrayList<Contract> nonAvailableContracts = new ArrayList<>();
    /**
     * Constructor to initialize a CheatingAI with a name and game board.
     * 
     * @param name The name of the AI player.
     * @param board The game board instance.
     */
    public CheatingAI(String name) {
        super(name);
        
        this.random = new Random();
   
        

    }


    /**
     * Requests a move from the AI player.
     * 
     * @return A Move object.
     */
    @Override
    public Move request() {
        
        try{
        
        
            nonAvailableContracts.addAll(board.getContractPool().getAllContracts());
            nonAvailableContracts.removeAll(availableContracts);
        }catch(Exception e){
            System.out.println("non-available contracts in request() in CheatingAI:"+e);
            System.exit(1);
        }
        // Decide whether to cheat or not (25% chance to cheat)
        boolean cheat = random.nextInt(4) == 0; // 1 in 4 chance to cheat

        Move result;

        if (cheat) {
            result =  cheatMove();
        } else {
            result =  super.request();
        }

       
        

        board.makeMove(result);
        return result;
       
    }

    /**
     * Generates a move with an unfair advantage (cheating move).
     * 
     * @return A Move object representing the cheating move.
     */
    private Move cheatMove() {

        // Phase 1: choose a non-available Contract

        int randomIndex = random.nextInt(nonAvailableContracts.size());
        Contract randomContract = nonAvailableContracts.get(randomIndex);

        

        // Phase 2: choose an invalid Combination of cables (sum > 5)

        Map<CableConfig, Integer> invalidCombination = CableCombinations.getRandomInvalidCombination(board.getCableTypes().getCableConfigs(), board.getCableTypes().getMaxCableValue());

        Map<String, Integer> toBePicked = new HashMap<>();
        for (Map.Entry<CableConfig, Integer> entry : invalidCombination.entrySet()) {
            toBePicked.put(entry.getKey().getType(), entry.getValue());
        }
        

        // Phase 3: Use cables that are not available in Inventory

        // or maxDistance beim bauen der Strecke überschreiten (use z.B. more than 4 taube) 
        
        ArrayList<CableConfig> unAvailableCables = new ArrayList<>();
        for (CableConfig cableType : board.getCableTypes().getCableConfigs()){
            if (myInventory.getCables().get(cableType) == 0){
                unAvailableCables.add(cableType);
            }
        }

        if (!unAvailableCables.isEmpty()){
            randomIndex = random.nextInt(unAvailableCables.size());
        CableConfig randomCableType = unAvailableCables.get(randomIndex);

        // Now choose 2 totally random Cities and connect them (this will most certainly be invalid)

        ArrayList<City> allCities = new ArrayList<>();
        
        ArrayList<Region> allRegions = new ArrayList<>(board.getAllREgions().getRegions());
        for (Region region : allRegions){
            allCities.addAll(region.getCities());
        }

        randomIndex = random.nextInt(allCities.size());
        City cityA = allCities.get(randomIndex);
        randomIndex = random.nextInt(allCities.size());
        City cityB = allCities.get(randomIndex);
        
        return new Move(
            randomContract.getId(),
            toBePicked,
            cityA.getName(),
            cityB.getName(),
            randomCableType.getType()
        );
        }

        return new Move(
            randomContract.getId(),
            toBePicked
        );

        

        
    }

    @Override
    public int getScore(boolean applyEndgameScoring){
        super.getScore(applyEndgameScoring);

        return score + 100;
    }

}