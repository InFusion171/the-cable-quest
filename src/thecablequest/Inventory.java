package thecablequest;

import java.util.*;

import thecablequest.exceptions.CableNotFoundException;
import thecablequest.exceptions.CableValueTooLargeException;
import thecablequest.gameboard.XMLElementClasses.CableConfig;
import thecablequest.gameboard.XMLElementClasses.CableConfigs;
import thecablequest.gameboard.XMLElementClasses.City;
import thecablequest.gameboard.XMLElementClasses.Contract;

/**
 * The Inventory class for the player
 * 
 * @author Marius Degenhardt
 */
public class Inventory 
{
    /**
     * The sum of the build cable length
     */
    private double buildCableDistanceSum = 0;

    /**
     * How many times the player made a move
     */
    private int playersTurnCount = 0;

    /**
     * The player points
     */
    private int points = 0;
  
    /**
     * The Hashmap where the key is the name of the cable and the value is how many the player picked up
     */
    private HashMap<CableConfig, Integer> cables = new HashMap<>();

    /**
     * The accepted contracts
     */
    private ArrayList<Contract> acceptedContracts = new ArrayList<>();

    /*
     * The fulfilled Contracts
     */
    private ArrayList<Contract> myFulfilledContracts = new ArrayList<>();

    /**
     * The avaible cable types
     */
    private CableConfigs cableTypes;

    /**
     * The Inventory object
     * 
     * @param givenCables The given cables from the gameconfiguration
     */
    public Inventory(CableConfigs cableTypes)
    {
        this.cableTypes = cableTypes;

        this.initCables(cableTypes);
    }

    /**
     * Get the cable which can cover the max distance 
     * 
     * @return The distance covered
     */
    public int maxDistanceCoveredByCable()
    {
        int max = Integer.MIN_VALUE;

        for(Map.Entry<CableConfig, Integer> entry : this.cables.entrySet())
        {
            CableConfig cable = entry.getKey();

            int cableCount = entry.getValue();

            int distanceCovered = cableCount * cable.getDistancePerCable();

            if(distanceCovered > cable.getMaxDistance())
                distanceCovered = cable.getMaxDistance();

            if(distanceCovered > max)
                max = distanceCovered;
        }

        return max;

    }

     /**
     * Get the cable which can cover the max distance and can cross borders
     * 
     * @return The distance covered
     */
    public int maxDistanceCoveredByCableBorderCrossing()
    {
        int max = Integer.MIN_VALUE;

        for(Map.Entry<CableConfig, Integer> entry : this.cables.entrySet())
        {
            CableConfig cable = entry.getKey();

            // we don't want the cables that cannot cross a border
            if(!cable.getCanCrossBorders())
                continue;

            int cableCount = entry.getValue();

            int distanceCovered = cableCount * cable.getDistancePerCable();

            if(distanceCovered > cable.getMaxDistance())
                distanceCovered = cable.getMaxDistance();

            if(distanceCovered > max)
                max = distanceCovered;
        }

        return max;

    }

    /**
     * Add the distance to the build cables sum
     * 
     * @param distance The distance
     */
    public void addCableDistanceSum(double distance)
    {
        this.buildCableDistanceSum += distance;
    }

    /**
     * Get the build cable distance
     * 
     * @return The distance
     */
    public double getBuildCableDistance()
    {
        return this.buildCableDistanceSum;
    }

    /**
     * Increments the player turn count
     */
    public void incrementPlayerTurnCount()
    {
        this.playersTurnCount += 1;
    }

    /**
     * Get the player turns count
     * 
     * @return The count
     */
    public int getPlayerTurnCount()
    {
        return this.playersTurnCount;
    }

    /**
     * Get the player points
     * 
     * @return The player points
     */
    public int getPlayerPoints()
    {
        return this.points;
    }

    /**
     * Decrement the points value of a player by number 
     * 
     * @param number The number to decrement
     */
    public void decrementPlayerPoints(int number)
    {
        this.points -= number;
    }


    /**
     * Set the default values for the cables HashMap
     * 
     * @param cableTypes The avaible cables
     */
    private void initCables(CableConfigs cableTypes)
    {
        Iterator<CableConfig> it = cableTypes.getCableConfigs().iterator();

        while(it.hasNext())
            this.cables.put(it.next(), 0);
        
    }

    /**
     * Returns the amount of a certain cable in the inventory
     * @param name The cable name
     * 
     * @return the amount of the cable. if it does not exist return 0
     */
    public int numberOfThisCableInInventory(String name)
    {
        for (CableConfig cable : cables.keySet())
        {
            if(cable.getType().equals(name))
                return cable.getValue();
            
        }

        return 0;
    }

    /**
     * Get the cables
     * 
     * @return The HashMap where the key is the cable type and the value is how many of the cable are in the inventory
     */
    public HashMap<CableConfig,Integer> getCables()
    {
        return this.cables;
    }

    /**
     * Get the accepted Contracts
     * 
     * @return The accepted contracts
     */
    public ArrayList<Contract> getContracts()
    {
        return acceptedContracts;
    }

    public void printAcceptedContracts() {
        System.out.println("Accepted Contracts:");
        for (Contract contract : acceptedContracts) {
            System.out.println(contract);
        }
    }

    // Method to print the myFulfilledContracts ArrayList
    public void printMyFulfilledContracts() {
        System.out.println("My Fulfilled Contracts:");
        for (Contract contract : myFulfilledContracts) {
            System.out.println(contract.getId());
        }
    }

    public void printNotFulfilledContracts() {
        System.out.println("My Not Fulfilled Contracts:");
        for (Contract contract : getNotFulfilledContracts()) {
            System.out.println(contract.getId());
        }
    }

    public void printCables() {
        StringBuilder result = new StringBuilder();
        for (Map.Entry<CableConfig, Integer> entry : cables.entrySet()) {
            result.append(entry.getKey().getType()).append(" -> ").append(entry.getValue()).append(" | ");
        }
        // Remove the trailing " | " if needed
        if (result.length() > 0) {
            result.setLength(result.length() - 3);
        }
        System.out.println(result.toString());
    }
    
    /**
     * add an fulfilled contract. Also increases the player points by the value of the contract
     * 
     * @param contract The fulfilled contract
     */
    public void addFulfilledContract(Contract contract)
    {
        this.myFulfilledContracts.add(contract);
        this.points += contract.getValue();
    }

    /**
     * get the fulfilled contracts
     * 
     * @return The ArrayList of fulfilled contracts
     */
    public ArrayList<Contract> getFulfilledContracts()
    {
        return myFulfilledContracts;
    }

    public ArrayList<Contract> getNotFulfilledContracts(){

        ArrayList<Contract> notFulfilledContracts = acceptedContracts;

        notFulfilledContracts.removeAll(myFulfilledContracts);

        return notFulfilledContracts;
    }

    /**
     * Accept a Contract
     * 
     * @param acceptedContract The contract to accept
     */
    public void acceptContract(Contract acceptedContract)
    {
        acceptedContracts.add(acceptedContract);
    }

    /**
     * Pickup the cables and add them in the inventory
     * 
     * @param pickedCables The cables
     */
    public void pickUpCables(Map<String, Integer> pickedCables) 
    {
        // Loop over the picked up cables
        for (Map.Entry<String, Integer> pickedCableEntry : pickedCables.entrySet()) 
        {
            String pickedCableType = pickedCableEntry.getKey();
            Integer pickedCableCount = pickedCableEntry.getValue();
    
            // Find the corresponding CableConfig object in our inventory
            for (Map.Entry<CableConfig, Integer> cablesEntry : this.cables.entrySet()) 
            {
                CableConfig cable = cablesEntry.getKey();
                Integer currentCount = cablesEntry.getValue();
    
                // Check if the cable type matches
                if (cable.getType().equals(pickedCableType)) 
                {
                    // Increase the count by the picked up amount
                    this.cables.put(cable, currentCount + pickedCableCount);
                    
                    break; 
                }
            }
        }
    }

    
    /**
     * Sets the amount of a certain cable in Inventory calculated by the distance between cityA and cityB
     * 
     * @param numCables numCables is the amount used in Connecting
     * @param cable The cable to decrease
     * @return false if the distance is too large between cityA and cityB, true otherwise
     */
    public boolean removeCables(String cableName, City cityA, City cityB)
    {
        //mostly verbrochen von Marc-Arne
        double distance = cityA.distancebetweencities(cityB);
        int cablesInInventory= this.numberOfThisCableInInventory(cableName);
        CableConfig cable = cableTypes.getCableConfigByName​(cableName);
        int cost = cable.costToBuild(distance);

        int cableCount = cablesInInventory - cost;
       

        if(cableCount < 0)
        {
            System.out.println("The connection between: " + cityA.getName() + " and " + cityB.getName() + " is: " + distance);
            System.out.println("The distance that can be be build with the cable : " + cable.getType() + " " + cablesInInventory+ "-"+ cost);

            //return false;
        }

        this.cables.put(cable, cableCount);
        this.points += cost * cable.getValue();
        
        return true;
    }


    /**
     * Find the CableConfig by name
     * 
     * @param name The name of the cable
     * @return The matched CableConfig object
     * @throws CableNotFoundException Throws if the cable don't exist
     */
    private CableConfig findCableConfig(String name) throws CableNotFoundException
    {
        Iterator<CableConfig> it = this.cableTypes.getCableConfigs().iterator();

        while(it.hasNext())
        {
            CableConfig cable = it.next();

            if(cable.getType().equalsIgnoreCase(name))
                return cable;
        }

        throw new CableNotFoundException("[Inventory] The cable name dont exist");
    }

    /**
     * Set the avaible cables
     * 
     * @param cablesMap
     * @throws CableValueTooLargeException
     * @throws CableNotFoundException
     */
    public void setCables(Map<String, Integer> cablesMap) throws CableValueTooLargeException, CableNotFoundException
    {
        for(String key : cablesMap.keySet())
        {
            //get the cable type from the key
            CableConfig cable = findCableConfig(key);

            this.cables.put(cable, cablesMap.get(key));
        }
    }
}
