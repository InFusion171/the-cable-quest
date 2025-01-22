package thecablequest.gameboard.XMLElementClasses;

import javax.xml.stream.events.StartElement;

import thecablequest.exceptions.XMLFormatException;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import java.io.IOException;

/**
 * Country represented with a name and it's country code
 * 
 * @author Marius Degenhardt
 */
public class Country 
{
    /**
     * The country name
     */
    private String name;

    /**
     * The country code
     */
    private String code;    

    /**
     * Set the name and country code
     * 
     * @param name The name of the country
     * @param code The country code
     */
    public Country(String name, String code)
    {
        this.name = name;
        this.code = code;
    }

    /**
     * Get the name
     * 
     * @return the name
     */
    public String getName()
    { 
        return this.name;
    }

    /**
     * Get the code
     * 
     * @return the country code
     */
    public String getCode()
    {
        return this.code;
    }

    /**
     * Parse the Country-Element
     * 
     * @param element The Country-Element
     * @return A new Country-Element
     * @throws XMLStreamException Throws if element differ from the Country-Element
     * @throws XMLFormatException Throws if an attribute don't exist
     * @throws IOException Throws if name or code don't exist
     */
    public static Country parseCountryElement(StartElement element) throws XMLStreamException, XMLFormatException
    {
        if(!element.getName().getLocalPart().equals("Country"))
            throw new XMLStreamException("[Country] XML elements name isn't Country");

        String name = element.getAttributeByName(new QName("name")).getValue();
        String code = element.getAttributeByName(new QName("code")).getValue();

        if(name == null)
            throw new XMLFormatException("[Country] name attribute don't exist");
        
        if(code == null)
            throw new XMLFormatException("[Country] code attribute don't exist");

        return new Country(name, code);
    }
}
