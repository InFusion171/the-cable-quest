package thecablequest.gameboard.XMLElementClasses;

import javax.xml.stream.events.StartElement;

import thecablequest.exceptions.XMLFormatException;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import java.io.IOException;

/**
 * The region contract represented with it's id, the referenced region and the completion reward value
 * 
 * @author Marius Degenhardt
 */
public class RegionContract extends Contract
{
    /**
     * The unique region name
     */
    private String region;

    /**
     * Set the uniqe id, the referenced region and the completion reward value
     * 
     * @param id The uniqe id
     * @param region The referenced region
     * @param value The completion reward value
     */
    public RegionContract(String id, String region, int value) 
    {
        super(id,value);
        this.region = region;
    }

    /**
     * get the referenced region
     * 
     * @return The region
     */
    public String getRegion() 
    {
        return this.region;
    }

    /**
     * Parse the RegionContract-Element with it's values
     * 
     * @param element The RegionContract-Element
     * @return A new RegionContract object
     * @throws XMLStreamException Throws if element is not the RegionContract-Element
     * @throws IOException Throws if id or region or value don't exist
     * @throws NumberFormatException Throws if value isn't a number
     */
    public static RegionContract parseRegionContractElement(StartElement element) throws XMLStreamException, XMLFormatException, NumberFormatException
    {
        if(!element.getName().getLocalPart().equals("RegionContract"))
            throw new XMLStreamException("[RegionContract] XML elements name isn't RegionContract");

        String id = element.getAttributeByName(new QName("id")).getValue();
        String region = element.getAttributeByName(new QName("region")).getValue();
        String valueAttr = element.getAttributeByName(new QName("value")).getValue();

        if(id == null)
            throw new XMLFormatException("[RegionContract] id don't exist");

        if(region == null)
            throw new XMLFormatException("[RegionContract] region don't exist");   

        if(valueAttr == null)
            throw new XMLFormatException("[RegionContract] value don't exist");  

        int value = Integer.parseInt(valueAttr);
        
        if(value == 0)
            throw new XMLFormatException("[RegionContract] value = 0");

        return new RegionContract(id, region, value);
    }
}
