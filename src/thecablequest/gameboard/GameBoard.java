package thecablequest.gameboard;

import breitband.preset.*;
import thecablequest.Inventory;
import thecablequest.exceptions.RegionNotFoundException;
import thecablequest.gameboard.XMLElementClasses.*;
import thecablequest.helperClasses.Pair;
import thecablequest.helperClasses.MathHelper;

import java.util.*;

public class GameBoard
{
    /**
     * The last move
     */
    private static Move lastMove = null;

    /**
     * The value for highest cable value
     */
    private int maxCableValue;

    /**
     * The game configuration
     */
    private GameConfiguration gameConfig;
    
    /**
     * The cable config where te cable types are tored
     */
    private CableConfigs cableTypes;

    /**
     * The regions where all cities and borders live
     */
    private Regions allRegions;

    /**
     * The contract pool where the contracts are stored
     */
    private ContractPool contractPool;

    /**
     * An ArrayList for all avaible contracts
     */
    private ArrayList<Contract> currentlyAvailableContracts= new ArrayList<>();

    /**
     * The current player id starting at 1
     */
    private int currentPlayerId = 1;

    /**
     * the player count of the total players
     */
    private int playerCount = 0;

    /**
     * remaining City connections
     */
    public HashMap<String,Integer> remaingCityConnections = new HashMap<>();
  

    /**
     * A HashMap where the key is Pair of start and end city and the value is the cable type between the cities
     */
    public HashMap<Pair<String, String>, String> buildCables = new HashMap<>();

    /**
     * The HashMap where the key is the player id and the value is they inventory of the player (saved in the 1-5 format)
     */
    private HashMap<Integer, Inventory> playerInventorysById = new HashMap<>();

    /**
     * Collektives verbrechen
     * creates its own GameConfiguration from a reader and parses the relevant information
     */
    public GameBoard(ImmutableList<String> availableContractsAsString, int numPlayers){

        try{
            gameConfig = GameConfiguration.getInstance();
            cableTypes = gameConfig.getCableConfigs();
            allRegions = gameConfig.getRegions();
            contractPool = gameConfig.getContractPool();
            maxCableValue = gameConfig.getCableConfigs().getMaxCableValue();

            for (String contract : availableContractsAsString){
                currentlyAvailableContracts.add(gameConfig.getContractPool().findContract(contract));
            }

            for(City city : allRegions.getAllCities()){
                remaingCityConnections.put(city.getName(),city.getMaxconnections());
            }

            this.playerCount = numPlayers;

            defaultInventoryInit(numPlayers);

        }catch(Exception e){
            System.out.println("Well, you still didn't put in a proper config,into GameBoard, but I found a file ");
            System.exit(1);
        }
    }


    /**
     * Check if a player can connect atleas 2 cities in the given region
     * 
     * @param playerId The player id
     * @param region The region
     * @return true if atleas 2 cities can be connected, false otherwise
     */
    private boolean playerCanConnectCitiesInRegion(int playerId, Region region)
    {
        Inventory playerInventory = this.playerInventorysById.get(playerId);

        for(City cityA : region.avaibleCities())
        {
            for(City cityB : region.avaibleCities())
            {
                // we don't want to compare the same cities
                if(cityA.equals(cityB))
                    continue;

                double distance = cityA.distancebetweencities(cityB);

                // get the max distance that can be covered by the cables in the inventory
                double distanceCoveredByCable = playerInventory.maxDistanceCoveredByCable();

                if(distance > distanceCoveredByCable)
                    return false;
            }
        }

        return true;
    }

    private boolean playerCanConnectCitiesInCountry(int playerId)
    {
        ArrayList<City> allAvaibleCities = new ArrayList<>();

        Inventory playerInventory = this.playerInventorysById.get(playerId);

        for(Region region : this.allRegions.getRegions())
        {
            ImmutableList<City> regionAvaibleCities = region.avaibleCities();

            // add all cities from regionAvaibleCities to allAvaibleCities
            regionAvaibleCities.forEach((c) -> { allAvaibleCities.add(c); });
        }

        for(City cityA : allAvaibleCities)
        {
            for(City cityB : allAvaibleCities)
            {
                // we don't want to compare the same cities
                if(cityA.equals(cityB))
                    continue;

                double distance = cityA.distancebetweencities(cityB);

                // get the max distance that can be covered by the cables in the inventory and the cable can cross borders
                double distanceCoveredByCable = playerInventory.maxDistanceCoveredByCableBorderCrossing();

                if(distance > distanceCoveredByCable)
                    return false;
            }
        }

        return true;
    }

    /**
     * Check if player can connect 2 cities with his cables in his inventory
     * 
     * @param playerId The given player
     * @return true if theres atleas 2 cities that the player can connect
     */
    private boolean playerCanConnectCity(int playerId)
    {
        for(Region region : this.allRegions.getRegions())
        {
            if(this.playerCanConnectCitiesInRegion(playerId, region))
                return true; 
        }

        return this.playerCanConnectCitiesInCountry(playerId);
    } 

    /**
     * Run this to check if the game should continue or not
     * 
     * @return true if it should continue, false otherwise;
     */
    public boolean isRunning()
    {
        // if less than 5 contracts are avaible
        if(this.currentlyAvailableContracts.size() <= 5)
            return false;

        int playerCanConnectCititesCount = 0;

        // starting at 1 because the playerid starts at 1
        for(int i = 1; i <= this.playerInventorysById.size(); i++)
        {
            Inventory playerInventory = this.playerInventorysById.get(i);

            //if the 100th turn is made
            if(playerInventory.getPlayerTurnCount() >= 100)
                return false;

            // if the the build cables distance is larger than the given ending length
            if(playerInventory.getBuildCableDistance() >= this.cableTypes.getEndingLength())
                return false;

            if(playerCanConnectCity(i))
                ++playerCanConnectCititesCount;
        }

        if(playerCanConnectCititesCount == 0)
            return false;

        return true;
    }

    /**
     * inits the playerInventorysById map. should be called after the cableTypes
     * 
     * @param playerCount The playercount
     */
    private void defaultInventoryInit(int playerCount)
    {
        for(int i = 1; i <= playerCount; i++)
            this.playerInventorysById.put(i, new Inventory(this.cableTypes));

        
    }

    public HashMap<Integer, Inventory> getPlayerInventorysById(){

        return this.playerInventorysById;

    }

 

    /**
     * To make a move on the gameboard
     * 
     * @param move The move object
     * @param playerId The playerId between 1-5 (not 0-4)
     * 
     */
    public void makeMove(Move move)
    {
        if(move == null){
            if( (currentPlayerId+1) > this.playerCount){
                currentPlayerId = 1;
            }
            else{
                currentPlayerId += 1;
            } 


            return;
        }
            

        int playerId = currentPlayerId;

        if(!isLegal(move, playerId,true))
        {
           System.out.println("----------------------------------------------------");
            System.out.println("The following move done by "+playerId+" is NOT legal!");
            if(move!=null)
                System.out.println(move.toString());

            System.out.println("----------------------------------------------------");
            System.out.println("Buhhhh Kill them! With Fire!");
            
            System.exit(1); //Exit method if move is not legal
            return;
        }


        // set the picked up cables
        Inventory playerInventory = this.playerInventorysById.get(playerId);
       
    
        if(playerId < 1 || playerId > this.playerCount)
            System.out.println("[GameBoard] Playerid is too high or too low: " + playerId);

        CableConfig cableConfig = this.cableTypes.getCableConfigByName(move.getCableType());

        City cityA = this.allRegions.findCity(move.getCableCityA());
        City cityB = this.allRegions.findCity(move.getCableCityB());

        playerInventory.incrementPlayerTurnCount();
        
        if(move.didAcceptContract())
        {
            playerInventory.acceptContract(contractPool.findContract(move.getAcceptedContractID()));

            currentlyAvailableContracts.remove(contractPool.findContract(move.getAcceptedContractID()));
        }

        if(move.getPickedUpCables() != null)
            playerInventory.pickUpCables(move.getPickedUpCables());

        if(move.didBuildCable())
        {
            cableConfig = this.cableTypes.getCableConfigByName(move.getCableType());

             // set the build cable
            this.buildCables.put(new Pair<>(move.getCableCityA(), move.getCableCityB()), move.getCableType());

            

            remaingCityConnections.put(move.getCableCityA(),remaingCityConnections.get(move.getCableCityA()) - 1);
            
            remaingCityConnections.put(move.getCableCityB(),remaingCityConnections.get(move.getCableCityB()) - 1);
            
            

            playerInventory.removeCables(move.getCableType(), cityA, cityB);

        }

        
        if(move.didAcceptContract())
        {
            playerInventory.acceptContract(contractPool.findContract(move.getAcceptedContractID()));

            currentlyAvailableContracts.remove(contractPool.findContract(move.getAcceptedContractID()));
        }
        

        //this player made their move, so the next one is the current one
        if( (currentPlayerId+1) > this.playerCount){
            currentPlayerId = 1;
        }
        else{
            currentPlayerId += 1;
        } 

    }

    /**
     * Get all regions
     * 
     * @return The regions
     */
    public Regions getAllREgions()
    {
        return this.allRegions;
    }

    /**
     * Get the playerCount
     * 
     * @return The playercount
     */
    public int getPlayerCount()
    {
        return this.playerCount;
    }

    /**
     * Getter for the currently available contracts
     * 
     * @return The avaible contracts
     */
    public ArrayList<Contract> getCurrentlyAvailableContracts()
    {
        return this.currentlyAvailableContracts;
    }
    
    /**
     * Get the cable types
     * 
     * @return The cable types
     */
    public CableConfigs getCableTypes()
    {
        return this.cableTypes;
    }
    
    /**
     * Get ContractPool 
     * 
     * @return all Contracts
     */
    public ContractPool getContractPool(){
        return this.contractPool;
    }

    /**
     * get the player id
     * 
     * @return
     * 
     */
    public int getCurrentPlayerId()
    {
        return this.currentPlayerId;
    }

    public int getMaxCableValue(){

        return maxCableValue;
        
    }

    public GameConfiguration getGameConfig(){

        return gameConfig;
        
    }

    /**
     * Check if all cities in a region are direktly or indirektly connected
     * 
     * @param regionName The region name
     * 
     * @return true if all citites in a region are connected 
     */
    private boolean regionContractFulfilled(String regionName)
    {
        Region region;

        try
        {
            region = this.allRegions.findRegion(regionName);
        }
        catch(RegionNotFoundException e)
        {
            e.printStackTrace();
            return false;
        }

        ImmutableList<City> cities = region.getCities();

        HashSet<String> cityNamesSet = new HashSet<>();

        for(City city : cities)
            cityNamesSet.add(city.getName());
        

        for(Map.Entry<Pair<String, String>, String> entry : buildCables.entrySet()) 
        {
            Pair<String, String> connection = entry.getKey();

            // remove the city if it was connected
            if(cityNamesSet.contains(connection.getFirst()))
                cityNamesSet.remove(connection.getFirst());

            if(cityNamesSet.contains(connection.getSecond()))
                cityNamesSet.remove(connection.getSecond());

        }

        if(!cityNamesSet.isEmpty())
            return false;

        
        return true;
    }


    /**
     * Check if a player fulfilled a contract. Also adding the accepted contract in the player inventory
     * 
     * @param playerId The player id to check
     * @return true if a contract is fulfilled, false otherwise
     */
    private boolean isContractFulfilled(int playerId)
    {
        Inventory playerInventory = this.playerInventorysById.get(playerId);

        for(Contract contract : playerInventory.getContracts())
        {
            if(contract instanceof RegionContract)
            {
                RegionContract regionContract = (RegionContract)contract;

                boolean fulfilled = regionContractFulfilled(regionContract.getRegion());


                if(fulfilled && !playerInventory.getFulfilledContracts().contains(contract))
                    playerInventory.addFulfilledContract(contract);

                return fulfilled;
            }

            if(contract instanceof CityToCityContract)
            {
                CityToCityContract cityContract = (CityToCityContract)contract;

                boolean fulfilled = cityContractFulfilled(new Pair<>(cityContract.getCityA(), cityContract.getCityB()));

                if(fulfilled && !playerInventory.getFulfilledContracts().contains(contract))
                    playerInventory.addFulfilledContract(contract);

                return fulfilled;
            }
        }

        return false;
    }

    /**
     * check if a CityToCityContract is fulfilled
     * 
     * @param cableRoute The cable route of the contract
     * @return true if its fulfilled, false otherwise
     */
    private boolean cityContractFulfilled(Pair<String, String> cableRoute)
    {
       for(Pair<String, String> route : this.buildCables.keySet())
       {
            if(cableRoute.equals(route))
                return true;
       }

       return false;
    }

    /**
     * Check if all accepted contracts are fulfilled
     * 
     * @param playerId The player id to get the Inventory
     */
    public boolean allContractsFulfilled(int playerId)
    {
        Inventory myInventory = playerInventorysById.get(playerId);

        // method also appends fulfilled contracts in the inventory
        isContractFulfilled(playerId);

        if (myInventory.getFulfilledContracts().isEmpty() || myInventory.getContracts().isEmpty())
            return false;
        
        // compare if the fulfilled contracts are the same as in the acapted contracts
        return new HashSet<>(myInventory.getFulfilledContracts()).equals(new HashSet<>(myInventory.getContracts()));
    }

    /**
     * Check if cableA can cross cableB and vice versa
     * 
     * @param cableA First cable
     * @param cableB Second cable
     * @return true if cableA can cross cableB and vice versa
     */
    public boolean cableAbleToCross(String cableA, String cableB)
    {
        CableConfig cable1 = this.cableTypes.getCableConfigByName(cableA);

        // check if cable1 is contained in canCrossCables from cable2 and vice versa
        return cable1.getCanCrossCables().contains(cableA);
    }

    /**
     * Validates if a cable connection intersects with a city
     * 
     * @return true if it don't intersect, false otherwise
     */
    public boolean validateCableIntersectsCity(City cityConnectionA, City cityConnectionB)
    {
        for(Region region : this.allRegions.getRegions())
        {
            for(City city : region.getCities())
            {
                // check if the connection intersects with a city if the city is neither start nor endpoint
                if(city.getLocation().isOnLine(cityConnectionA.getLocation(), cityConnectionB.getLocation()) && !cityConnectionA.equals(city) && !cityConnectionB.equals(city))
                    return false;
            }
        }

        return true;
    }

    /**
     * Validates if a connection between cityA and cityB is valid from the given cableTypes maxdistance attribute
     * 
     * @param cityA First city
     * @param cityB Second city
     * @param cableType The cable type that connects cityA and cityB
     * @return true if maxdistance > distance between cityA and cityB, false otherwise
     */
    public boolean validateDistance(City cityA, City cityB, String cableType)
    {
        double distance = cityA.distancebetweencities(cityB);

        CableConfig cable = cableTypes.getCableConfigByName(cableType);

        return cable.getMaxDistance() > distance;
    }

    /**
     * Validates if the build cable from cityA to cityB in the move object is allowed to cross another already build cable 
     * (also returns false if cable crosses cable its not allowed to cross)
     * 
     * @param move The move from the player
     * @return false if a cable can't cross another cable, true otherwise
     */
    public boolean validateCanCrossCable(Move move)
    {
        // loop over buildCables and compare each city route with the other city routes in buildCables

        City cityA = this.allRegions.findCity(move.getCableCityA());
        City cityB = this.allRegions.findCity(move.getCableCityB());

        Pair<String,String> newConnecion = new Pair<>(cityA.getName(), cityB.getName());

        String newCable = move.getCableType();

        for(Map.Entry<Pair<String, String>, String> secondCableRoute : this.buildCables.entrySet())
            {
                Pair<String, String> secondCityConnection = secondCableRoute.getKey();

                // we don't want to compare the same city route
                if(secondCityConnection.equals(newConnecion))
                    continue;

                City cityC = this.allRegions.findCity(secondCityConnection.getFirst());
                City cityD = this.allRegions.findCity(secondCityConnection.getSecond());

                // check if the route from cityA to cityB intersect with cityC to cityD
                boolean doIntersect = MathHelper.intersect(cityA.getLocation(), cityB.getLocation(), cityC.getLocation(), cityD.getLocation());

                String cableSecondCityConnection = secondCableRoute.getValue();

                if(doIntersect)
                    return cableAbleToCross(newCable, cableSecondCityConnection);
            }
            return true;
        }

        
    
    /**
     * Checks if a connection between two Cities already exists
     * 
     * @param cityA First city
     * @param cityB Second city
     * @return true if they are already connected
     */
    public boolean citiesAlreadyConnected(City cityA, City cityB) 
    {
        Pair<String, String> pair1 = new Pair<>(cityA.getName(), cityB.getName());
        Pair<String, String> pair2 = new Pair<>(cityB.getName(), cityA.getName());

        return buildCables.containsKey(pair1) || buildCables.containsKey(pair2);
    }

    /**
     * Get the player inventory by playerId
     * 
     * @param playerId The playerId
     * @return The Inventory object
     */
    public Inventory getPlayerInventory(int playerId)
    {
        return this.playerInventorysById.get(playerId);
    }

    public void printCurrentlyAvailableContracts(){
        System.out.println("Currently Available Contarcts: ");
        for (Contract contract : currentlyAvailableContracts) {
            System.out.println(contract.getId());
        }
    }
    


    /**
     * This checks if a move is legal as set by the Game Rules
     * 
     * !!!!RUN THIS BEFORE UPDATING THE GAMEBOARD!!!
     * 
     * if "giveReason" is true it will print out a Comment on call of a illegal turn for debug
     * verbrochen by Marc-Arne
     * @return boolean
     */
    
     public boolean isLegal(Move move, int playerId, boolean giveReason){
        if(move == null)
            return false;
        

        CableConfigs cableTypes= gameConfig.getCableConfigs();
    
        if(allContractsFulfilled(playerId) && !move.didAcceptContract()) {
            if(giveReason)System.out.println("All contracts fulfilled, and no new contract accepted.");
            return false;
        }
    
        // Check if contract was accepted and if it was one of the available contracts
        if(move.didAcceptContract()) {
            boolean found = false;
            for(Contract contract : currentlyAvailableContracts) {
                if(contract.getId().equals(move.getAcceptedContractID())) {
                    found = true;
                    break;
                }
            }
            if(!found) {
                if(giveReason)System.out.println("Contract not currently available!");
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
                    if(giveReason)System.out.println("Illegal cable type picked up: " + name);
                    return false;
                }
                sum += amount * gameConfig.getCableConfigs().getCableConfigByName(name).getValue();
            }
            if (sum > maxCableValue) {
                if(giveReason)System.out.println("Exceeded max cable value sum!");
                return false;
            }
        }
    
        // Check if a cable was built
        if(move.didBuildCable()) {

            try {
                // Ensure two distinct cities are connected
                if(move.getCableCityA().equals(move.getCableCityB())) {
                    if(giveReason)System.out.println("Player " + playerId + " tried to connect a city with itself: " + move.getCableCityA());
                    //System.out.println(move.toString());
                    return false;
                }
    
                String cabletype = move.getCableType();
                CableConfig cableConfig = gameConfig.getCableConfigs().getCableConfigByName(cabletype);
                City cityA = gameConfig.getRegions().findCity(move.getCableCityA());
                City cityB = gameConfig.getRegions().findCity(move.getCableCityB());
                double distance = cityA.distancebetweencities(cityB);

                int costToBuild = gameConfig.getCableConfigs().getCableConfigByName(cabletype).costToBuild(distance); 
    
                if (citiesAlreadyConnected(cityA, cityB)) {
                    if(giveReason)System.out.println("Tried a connection that already exists between " + cityA.getName() + " and " + cityB.getName());
                    //System.out.println(move.toString());
                    return false;
                }
                if(!validateCanCrossCable(move)) {
                    if(giveReason)System.out.println("Cable cannot cross existing cable: " + move.getCableType());
                    return false;
                }
                if(!validateDistance(cityA, cityB, cabletype)) {
                    if(giveReason)System.out.println("Distance too great for cable type: " + move.getCableType() + "  Distance = " + distance);

                    return false;
                }
                if(!validateCableIntersectsCity(cityA, cityB)) {
                    if(giveReason)System.out.println("Cable intersects a city.");
                    return false;
                }
                int numberOfAvailableCables = playerInventorysById.get(playerId).numberOfThisCableInInventory(cabletype);
                if((move.getPickedUpCables() != null) && move.getPickedUpCables().get(move.getCableType()) != null) {
                    numberOfAvailableCables += move.getPickedUpCables().get(move.getCableType());
                }
                if(costToBuild > numberOfAvailableCables) {
                    if(giveReason)System.out.println("Not enough cables to build: required " + costToBuild + ", available " + numberOfAvailableCables);
                    return false;
                }
                if (remaingCityConnections.get(move.getCableCityA()) == 0 || remaingCityConnections.get(move.getCableCityB()) == 0) {
                    if(giveReason)System.out.println("One of the cities has no remaining connections: " + cityA.getName() + ", " + cityB.getName()+ "\n "+(cityA.getRemainedconnections() == 0) + (cityB.getRemainedconnections() == 0));
                    return false;
                }
                if(!cableConfig.getCanCrossBorders() && gameConfig.connectionIntersectsAnyRegionBorder(cityA, cityB)) {
                    if(giveReason)System.out.println("Cable cannot cross region border between " + cityA.getName() + " and " + cityB.getName());
                    return false;
                }
            } catch(Exception e) {
                System.out.println("Something went wrong in isLegal at Marker 1: " + e);
                return false;
            }
        } 
        return true;
    }

    /**
     * This should ran after the game ended but before the getPlayerPoints()
     * 
     * @param playerId The given playerId
     */
    public void endgame(int playerId)
    {
        Inventory playerInventory = this.playerInventorysById.get(playerId);

        for(Contract contract : playerInventory.getNotFulfilledContracts())
        {
            playerInventory.decrementPlayerPoints(contract.getValue());
        }
    }

}