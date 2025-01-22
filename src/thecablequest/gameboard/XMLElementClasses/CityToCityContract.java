package thecablequest.gameboard.XMLElementClasses;

import javax.xml.stream.events.StartElement;

import thecablequest.exceptions.XMLFormatException;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import java.io.IOException;

/**
 * The city to city contract represented with a uniqe id, the start city A, the city B and the completion reward value
 * 
 * @author Marius Degenhardt
 */
public class CityToCityContract extends Contract
{
    /**
     * The start city A
     */
    private String cityA;

    /**
     * The destination city B
     */
    private String cityB;

    /**
     * Set the uniqe id, the connection between CityA and CityB and the reward value for the completion
     * 
     * @param id The uniqe id
     * @param cityA The start CityA
     * @param cityB The end cityB
     * @param value The value for the completion
     */
    public CityToCityContract(String id, String cityA, String cityB, int value) 
    {
        super(id, value);
        this.cityA = cityA;
        this.cityB = cityB;
    }

    /**
     * Get the id
     * 
     * @return The id 
     */
    public String getId()
    {
        return super.getId();
    }

    /**
     * Get the start cityA
     * 
     * @return The start cityA
     */
    public String getCityA() 
    {
        return this.cityA;
    }

    /**
     * Get the end cityB
     * 
     * @return The end CityB
     */
    public String getCityB() 
    {
        return this.cityB;
    }
    
    /**
     * Parse the CityToCityContract Element with its values
     * 
     * @param element The XML Element CityToCityContract
     * @return a new CityToCityContract object
     * @throws XMLStreamException If the element differ from CityToCityContract
     * @throws IOException If id or a or b or value dont exist
     * @throws NumberFormatException If value isn't a number
     */
    public static CityToCityContract parseCityToCityContractElement(StartElement element) throws XMLStreamException, XMLFormatException, NumberFormatException
    {
        if(!element.getName().getLocalPart().equals("CityToCityContract"))
            throw new XMLStreamException("[CityToCityContract] XML elements name isn't CityToCityContract");

        String id = element.getAttributeByName(new QName("id")).getValue();
        String cityA = element.getAttributeByName(new QName("a")).getValue();
        String cityB = element.getAttributeByName(new QName("b")).getValue();
        String valueAttr = element.getAttributeByName(new QName("value")).getValue();

        if(id == null)
            throw new XMLFormatException("[CityToCityContract] id don't exist");

        if(cityA == null)
            throw new XMLFormatException("[CityToCityContract] a don't exist");

        if(cityB == null)
            throw new XMLFormatException("[CityToCityContract] b don't exist");

        if(valueAttr == null)
            throw new XMLFormatException("[CityToCityContract] value don't exist");

        
        int value = Integer.parseInt(valueAttr);
       
        if(value == 0)
            throw new XMLFormatException("[CityToCityContract] value equals 0");

        if(id.length() == 0)
            throw new XMLFormatException("[CityToCityContract] id need to have atleast one character");

        if(cityA == cityB)
            throw new XMLFormatException("[CityToCityContract] a and b need to be different");

        return new CityToCityContract(id, cityA, cityB, value);
    }
}
