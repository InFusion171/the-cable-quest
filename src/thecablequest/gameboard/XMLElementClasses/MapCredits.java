package thecablequest.gameboard.XMLElementClasses;

import javax.xml.stream.events.StartElement;

import thecablequest.exceptions.XMLFormatException;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import java.util.HashMap;

import java.io.IOException;

/**
 * The map credits for the given map
 * 
 * @author Marius Degenhardt
 */
public class MapCredits 
{
    /**
     * MapCredits HashMap where the key is the name of the xml attribute and the value is the value
     */
    private HashMap<String, String> mapCredits = new HashMap<>();

    /**
     * Set the MapCredits
     * 
     * @param mapCredits The MapCredits
     */
    public MapCredits(HashMap<String, String> mapCredits)
    {
        this.mapCredits = mapCredits;
    }

    /**
     * Get the MapCredits.
     * The HashMap key is the xml attribute of the MapCredits-Element.
     * The HashMap value is the value of the attribute
     * 
     * @return The MapCredits
     */
    public HashMap<String, String> getMapCredits()
    {
        return this.mapCredits;
    }

    /**
     * Parse the MapCredits-Element
     * 
     * @param element The MapCredits-Element
     * @return A new MapCredits object with the value if the parsed values
     * @throws XMLFormatException Throws if element isn't MapCredits
     */
    public static MapCredits parseMapCredits(StartElement element) throws XMLFormatException
    {
        if(!element.getName().getLocalPart().equals("MapCredits"))
            throw new XMLFormatException("[MapCredits] XML elements name isn't MapCredits");

        HashMap<String, String> credits = new HashMap<>();

        credits.put("license", element.getAttributeByName(new QName("license")).getValue());
        credits.put("licenseshort", element.getAttributeByName(new QName("licenseshort")).getValue());
        credits.put("licenselink", element.getAttributeByName(new QName("licenselink")).getValue());
        credits.put("licenseversion", element.getAttributeByName(new QName("licenseversion")).getValue());
        credits.put("copyright", element.getAttributeByName(new QName("copyright")).getValue());
        credits.put("copyrightlink", element.getAttributeByName(new QName("copyrightlink")).getValue());

        validateMapCredits(credits);

        return new MapCredits(credits);
    }

    /**
     * Validate if the MapCredits are complete
     * 
     * @param element The MapCredits-Element
     * @throws XMLStreamException Throws if the element is not an MapCredits-Element
     * @throws IOException Throws if a credit is missing
     */
    public static void validateMapCredits(HashMap<String, String> mapCredits) throws XMLFormatException
    {
        for(String creditAttribute : mapCredits.keySet())
        {
            if(mapCredits.get(creditAttribute) == null)
                throw new XMLFormatException("[MapCredits] One or more attributes is missing");
        }
    }
}
