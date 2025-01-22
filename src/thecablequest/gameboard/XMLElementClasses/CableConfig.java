package thecablequest.gameboard.XMLElementClasses;

import java.lang.Math;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import sag.IconNotFoundException;
import sag.elements.GEmoji;
import sag.elements.GFontAwesome;
import thecablequest.exceptions.XMLFormatException;


/**
 * Represents the configuration for a cable.
 * 
 * @autor Marius Degenhardt
 */
public class CableConfig 
{
    /**
     * The cabe type
     */
    private String type;

    /**
     * The cable value
     */
    private int value;

    /**
     * The value if the cable can cross a border
     */
    private boolean canCrossBorders;

    /**
     * The cables that can be corssed
     */
    private String canCrossCables;

    /**
     * The font awesome value
     */
    private GFontAwesome fontAwesome;

    /**
     * The emoji value
     */
    private GEmoji emoji;

    /**
     * The distance covered by a cable
     */
    private int distancePerCable;

    /**
     * The max distance that can be covered
     */
    private int maxDistance;

    /**
     * Set the values
     *
     * @param type The type of the cable.
     * @param value The value of the cable.
     * @param canCrossBorders Indicates if the cable can cross borders.
     * @param canCrossCables Indicates if the cable can cross other cables.
     * @param fontAwesome The FontAwesome icon representing the cable.
     * @param emoji The emoji representing the cable.
     * @param distancePerCable The distance covered per cable.
     * @param maxDistance The maximum distance the cable can cover.
     * @throws IconNotFoundException If the FontAwesome or emoji icon is not found.
     */
    public CableConfig(String type, int value, boolean canCrossBorders, String canCrossCables, String fontAwesome, 
            String emoji, int distancePerCable, int maxDistance) throws IconNotFoundException 
    {
        this.type = type;
        this.value = value;
        this.canCrossBorders = canCrossBorders;
        this.canCrossCables = canCrossCables;
        this.fontAwesome = new GFontAwesome(fontAwesome);
        this.emoji = new GEmoji(emoji);
        this.distancePerCable = distancePerCable;
        this.maxDistance = maxDistance;
    }

    /**
     * Gets the type of the cable.
     * 
     * @return The type of the cable.
     */
    public String getType() 
    {
        return this.type;
    }

    /**
     * Gets the value of the cable.
     * 
     * @return The value of the cable.
     */
    public int getValue() 
    {
        return this.value;
    }

    /**
     * Checks if the cable can cross borders.
     * 
     * @return True if the cable can cross borders, false otherwise.
     */
    public boolean getCanCrossBorders() 
    {
        return this.canCrossBorders;
    }

    /**
     * Gets the string indicating if the cable can cross other cables.
     * 
     * @return The string indicating if the cable can cross other cables.
     */
    public String getCanCrossCables() 
    {
        return this.canCrossCables;
    }

    /**
     * Gets the FontAwesome icon representing the cable.
     * 
     * @return The FontAwesome icon.
     */
    public GFontAwesome getFontAwesome() 
    {
        return this.fontAwesome;
    }

    /**
     * Gets the emoji representing the cable.
     * 
     * @return The emoji.
     */
    public GEmoji getEmoji() 
    {
        return this.emoji;
    }

    /**
     * Gets the distance covered per cable.
     * 
     * @return The distance covered per cable.
     */
    public int getDistancePerCable() 
    {
        return this.distancePerCable;
    }
    /**
     * Calculates the number of cables of this type needed to build the given distance
     * 
     * verbrochen von Marc-Arne
     * @return int 
     */
    public int costToBuild(double distance){
        return (int)(Math.ceil(distance/distancePerCable) );
    }

    /**
     * Gets the maximum distance the cable can cover.
     * 
     * @return The maximum distance the cable can cover.
     */
    public int getMaxDistance() 
    {
        return this.maxDistance;
    }


    /**
     * Parse the CableConfig-Element with it's values
     * 
     * @param element The CableConfig-Element
     * @return A CableConfig object
     * @throws XMLStreamException Throws if the element isn't a CableConfig-Element
     * @throws NumberFormatException Throws if value, distancepercable or maxDistance isn't a number
     * @throws IconNotFoundException Throws if the Icon for GEmoji or GFontAwesome isn't found
     * @throws XMLFormatException Throws if cancrossborder isn't a boolean
     */
    public static CableConfig parseCableConfigElement(StartElement element) throws XMLStreamException, NumberFormatException, IconNotFoundException, XMLFormatException
    {
        if(!element.getName().getLocalPart().equals("CableConfig"))
            throw new XMLStreamException("[CableConfig] XML elements name isn't CableConfig");

        String type = element.getAttributeByName(new QName("type")).getValue();
        String valueString = element.getAttributeByName(new QName("value")).getValue();
        String canCrossBordersString = element.getAttributeByName(new QName("cancrossborders")).getValue();
        String canCrossCables = element.getAttributeByName(new QName("cancrosscables")).getValue();
        String fontAwesome = element.getAttributeByName(new QName("fontawesome")).getValue();
        String emoji = element.getAttributeByName(new QName("emoji")).getValue();
        String distancePerCableString = element.getAttributeByName(new QName("distancepercable")).getValue();
        String maxDistanceString = element.getAttributeByName(new QName("maxdistance")).getValue();

        checkMissingAttribute(type, valueString, canCrossBordersString, 
            canCrossCables, fontAwesome, emoji, distancePerCableString, maxDistanceString);


        int value = Integer.parseInt(valueString);
        int distancePerCable = Integer.parseInt(distancePerCableString);
        int maxDistance = Integer.parseInt(maxDistanceString);

        Boolean canCrossBordersObject = Boolean.valueOf(canCrossBordersString);

        if(canCrossBordersObject == null)
            throw new XMLFormatException("[CableConfig] cancrossborders attribute isn't a boolean");

        boolean canCrossBorders = canCrossBordersObject.booleanValue();

        if(maxDistance <= distancePerCable)
            throw new XMLFormatException("[Cable Config] maxdistance needs to be greater than distancepercable");

        return new CableConfig(type, value, canCrossBorders, canCrossCables, fontAwesome, emoji, distancePerCable, maxDistance);
    }

    /**
     * Check if any of the given attributes is missing
     * 
     * @param attributes The given attributes
     * @throws XMLFormatException Throws if one or more argument is missing
     */
    private static void checkMissingAttribute(String... attributes) throws XMLFormatException
    {
        for(String value : attributes)
        {
            if(value == null)
                throw new XMLFormatException("[CableConfig] One ore more arguments are missing");
        }
    }
}
