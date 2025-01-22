package thecablequest.exceptions;

/**
 * Exception when a given cable is not found 
 *  
 * @author Marius Degenhardt
 */
public class CableNotFoundException extends Exception 
{
    public CableNotFoundException(String message)
    {
        super(message);
    }
}
