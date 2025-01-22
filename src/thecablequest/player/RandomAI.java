package thecablequest.player;

import thecablequest.gameboard.GameBoard;
import breitband.preset.*;
import thecablequest.Inventory;
import thecablequest.gameboard.XMLElementClasses.*;
import thecablequest.player.CableCombinations;
import java.util.Random;
import java.util.*;

/**
 * Represents an AI player that makes moves randomly.
 */
public class RandomAI extends BasicPlayer {

    /**
     * Constructor to initialize a RandomAI with a name and game board.
     * 
     * @param name The name of the AI player.
     * @param board The game board instance.
     */
    public RandomAI(String name) {
        super(name);
    }

    /**
     * Requests a move from the AI player. The move is determined randomly.
     * 
     * @return A Move object representing the AI player's move.
     */
    @Override
    public Move request() {
        Random random = new Random();

        if (board == null || myInventory == null) {
            System.out.println("Board or inventory is not initialized.");
            return null;
        }

        // Phase 1: Vertrag annhemen/nicht
        boolean takeContract = random.nextBoolean();
        ArrayList<Contract> availableContracts = board.getCurrentlyAvailableContracts();
        if (availableContracts == null || availableContracts.isEmpty()) {
            takeContract = false;
        }

        Contract randomAcceptedContract = null;
        if (takeContract) {
            int randomIndex = random.nextInt(availableContracts.size());
            randomAcceptedContract = availableContracts.get(randomIndex);
        }

        // Phase 2: Kabel aufnehmen/nicht
        boolean takeCables = random.nextBoolean();
        CableConfigs cableTypes = board.getCableTypes();
        if (cableTypes == null) {
            System.out.println("Cable types not available.");
            return null;
        }

        
        Map<String, Integer> toBePicked = new HashMap<>();
        if (takeCables) {
            Map<CableConfig, Integer> randomValidCombination = CableCombinations.getRandomValidCombination(cableTypes.getCableConfigs(), cableTypes.getMaxCableValue());
            for (Map.Entry<CableConfig, Integer> entry : randomValidCombination.entrySet()) {
                toBePicked.put(entry.getKey().getType(), entry.getValue());
            }
        }

        


        
        // Phase 3: Verbindung bauen/nicht
        boolean buildConnection = true;
        if (buildConnection && !myInventory.getCables().isEmpty()) {

            Move move = null;

            


            ArrayList<City> allCities = new ArrayList<>();

            Regions regions = board.getAllREgions();
        
            ArrayList<Region> allRegions = new ArrayList<>(regions.getRegions());
            for (Region region : allRegions){
                allCities.addAll(region.getCities());
            }

            List<CableConfig> cableList = new ArrayList<>(myInventory.getCables().keySet()); 

            boolean found = false;

            for (int i=0; i<100; i++){

                

                int randomIndex = random.nextInt(allCities.size());
                City cityA = allCities.get(randomIndex); 

                for (CableConfig cable : cableList) {
                    if (cable == null) continue;
    
                    int numCables = myInventory.getCables().get(cable);
                    int maximalDistance = numCables * cable.getDistancePerCable();
    
                    List<City> neighbourCities = new ArrayList<>(regions.getNeighbourCities(cityA, (double) maximalDistance));
                    if (!neighbourCities.isEmpty()) {
                        City cityB = neighbourCities.get(new Random().nextInt(neighbourCities.size()));
    
                        
                            move =  new Move(null, null, cityA.getName(), cityB.getName(), cable.getType());
    
                            if (canBeBuilt(move)){
                                found =  true;
                                break;
                            }
                
                    
                    
                    }
                }
            if (found){break;}
            }
            
            
           
                
            

            if (move != null) {
                move = new Move(
                    takeContract ? randomAcceptedContract.getId() : null,
                    takeCables ? toBePicked : null,
                    move.getCableCityA(),
                    move.getCableCityB(),
                    move.getCableType()
                );
            }
            if (canBeBuilt(move)) {
                board.makeMove(move);
                
                return move;
            }
        }
        Move nullmove = new Move(
            takeContract ? randomAcceptedContract.getId() : null,
            takeCables ? toBePicked : null
        );
        if (canBeBuilt(nullmove)) {
            board.makeMove(nullmove);
            return nullmove;
        }
        return null;
        
    }

    

    public boolean canBeBuilt(Move move){
        if(move == null)
            return false;
        

        CableConfigs cableTypes= board.getCableTypes();
    
       
    
        // Check if contract was accepted and if it was one of the available contracts
        if(move.didAcceptContract()) {
            boolean found = false;
            for(Contract contract : board.getCurrentlyAvailableContracts()) {
                if(contract.getId().equals(move.getAcceptedContractID())) {
                    found = true;
                    break;
                }
            }
            if(!found) {
                return false;
            }
        }
    
        // Check if cables were picked up
        if(move.getPickedUpCables() != null) {
            Map<String, Integer> pickedUpCables = move.getPickedUpCables();
            int sum = 0;
            for (Map.Entry<String, Integer> entry : pickedUpCables.entrySet()) {
                String name = entry.getKey();
                int amount = entry.getValue();
                if (!cableTypes.isLegal(name)) {
                    System.out.println("Illegal cable type picked up: " + name);
                    return false;
                }
                sum += amount *board.getCableTypes().getCableConfigByName(name).getValue();
            }
            if (sum > board.getMaxCableValue()) {
                System.out.println("Exceeded max cable value sum!");
                return false;
            }
        }
    
        // Check if a cable was built
        if(move.didBuildCable()) {
            try {
                // Ensure two distinct cities are connected
                if(move.getCableCityA().equals(move.getCableCityB())) {
                    return false;
                }
    
                String cabletype = move.getCableType();
                CableConfig cableConfig =board.getCableTypes().getCableConfigByName(cabletype);
                City cityA = board.getAllREgions().findCity(move.getCableCityA());
                City cityB = board.getAllREgions().findCity(move.getCableCityB());
                double distance = cityA.distancebetweencities(cityB);
    
                int costToBuild = board.getCableTypes().getCableConfigByName(cabletype).costToBuild(distance); 
    
                if (board.citiesAlreadyConnected(cityA, cityB)) {
                    return false;
                }
                if(!board.validateCanCrossCable(move)) {
                    return false;
                }
                if(!board.validateDistance(cityA, cityB, cabletype)) {
                    return false;
                }
                if(!board.validateCableIntersectsCity(cityA, cityB)) {
                    return false;
                }
                int numberOfAvailableCables = myInventory.numberOfThisCableInInventory(cabletype);
                if((move.getPickedUpCables() != null) && move.getPickedUpCables().get(move.getCableType()) != null) {
                    numberOfAvailableCables += move.getPickedUpCables().get(move.getCableType());
                }
                if(costToBuild > numberOfAvailableCables) {
                 
                    return false;
                }
                if (cityA.getRemainedconnections() == 0 || cityB.getRemainedconnections() == 0) {
                   
                    return false;
                }
                if(!cableConfig.getCanCrossBorders() && board.getGameConfig().connectionIntersectsAnyRegionBorder(cityA, cityB)) {

                    return false;
                }
            } catch(Exception e) {
                System.out.println("Something went wrong in canBeBuilt at Marker 1: " + e);
                return false;
            }
        } 
        return true;
    }
}
