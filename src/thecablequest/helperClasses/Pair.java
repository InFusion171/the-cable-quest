package thecablequest.helperClasses;

import java.util.Objects;

/**
 * Pair class to store two values
 * 
 * @author Marius Degenhardt
 * 
 */
public class Pair<T, U> 
{
    private T first;
    private U second;

    /**
     * Sets the first and second value
     * 
     * @param first First value
     * @param second Second value
     */
    public Pair(T first, U second) 
    {
        this.first = first;
        this.second = second;
    }

    /**
     * Get the first value
     * 
     * @return first value
     */
    public T getFirst() 
    {
        return first;
    }

    /**
     * Sets the first value
     * 
     * @param first The value
     */
    public void setFirst(T first) 
    {
        this.first = first;
    }

    /**
     * Get the second value
     * 
     * @return The second value
     */
    public U getSecond() 
    {
        return second;
    }

    /**
     * Set the second value
     * 
     * @param second The second value
     */
    public void setSecond(U second) 
    {
        this.second = second;
    }


    /**
     * Equals method for checking 2 Pair objects
     * 
     * @param o The Pair object
     */
    @Override
    public boolean equals(Object o) 
    {
        if (this == o) 
            return true;

        if (o == null) 
            return false;

        if(!(o instanceof Pair))
            return false;

        Pair<?, ?> pair = (Pair<?, ?>)o;

        return Objects.equals(getFirst(), pair.getFirst()) && Objects.equals(getSecond(), pair.getSecond());
    }

    /**
     * Generate a hash code for the Pair object
     * 
     * @return a hash value as int
     */
    @Override
    public int hashCode()
    {
        return Objects.hash(getFirst(), getSecond());
    }
}