package thecablequest.exceptions;

/**
 * Exception when the cable value is too large to add
 *  
 * @author Marius Degenhardt
 */
public class CableValueTooLargeException extends Exception
{
    public CableValueTooLargeException(String message)
    {
        super(message);
    }
}
