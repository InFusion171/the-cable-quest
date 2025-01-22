package thecablequest.gameboard.XMLElementClasses;

import java.util.*;

/**
 * Contract is a super class for CityToCityContract and RegionContract
 * 
 * not from Marius
 */
public class Contract
{
    /**
     * The contract id
     */
    private String id;

    /**
     * The completion reward value
     */
    private int value;

    /**
     * Set the id and value
     * 
     * @param id The contract id
     * @param value The completion value
     */
    public Contract(String id, int value){
        this.id = id;
        this.value = value;
    }

    /**
     * Get the uniqe id
     * 
     * @return The uniqe id
     */
    public String getId() 
    {
        return this.id;
    }

    /**
     * Get the reward value for the completion
     * 
     * @return The value
     */
    public int getValue() 
    {
        return this.value;
    }

    /**
     * The equals method
     * 
     * @param o The object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contract contract = (Contract) o;
        return value == contract.value && Objects.equals(id, contract.id);
    }

    /**
     * Generate the hash code for the Contract object
     * 
     * @return The hash value
     */
    @Override
    public int hashCode() 
    {
        return Objects.hash(id, value);
    }

    /**
     * Compares two lists of Contract objects to check if they contain the same elements, regardless of order.
     *
     * @param list1 the first list of contracts
     * @param list2 the second list of contracts
     * @return true if the lists contain the same elements, false otherwise
     */
    public static boolean areEqual(ArrayList<Contract> list1, ArrayList<Contract> list2) {
        if (list1.size() != list2.size()) {
            return false;
        }
        return new HashSet<>(list1).equals(new HashSet<>(list2));
    }
}