package thecablequest.gameboard.XMLElementClasses;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.*;

import thecablequest.exceptions.XMLFormatException;

import java.io.IOException;


/**
 * The description for the map
 * 
 * @author Marius Degenhardt
 */
public class Description 
{
    /**
     * Validate if theres a description
     * 
     * @param element The Description-Element
     * @param characters The data inside
     * @throws XMLStreamException Throws if element isn't a Description-Element
     * @throws XMLFormatException Throws if no description text is given
     * @throws IOException Throws if no description is given
     */
    public static void validateDescription(StartElement element, Characters characters) throws XMLFormatException, XMLStreamException
    {
        if(!element.getName().getLocalPart().equals("Description"))
            throw new XMLStreamException("[Description] XML elements name isn't Description");

        if(characters.getData().length() == 0)
            throw new XMLFormatException("[Description] No Description is given");
    }
}
