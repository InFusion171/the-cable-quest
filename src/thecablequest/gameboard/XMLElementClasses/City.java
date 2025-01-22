package thecablequest.gameboard.XMLElementClasses;

import javax.xml.stream.events.StartElement;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import java.lang.Math;

import thecablequest.exceptions.XMLFormatException;
import thecablequest.helperClasses.Point;

/**
 * A city represented with it's name, it's location and the max connections to the city
 * 
 * @author Marius Degenhardt
 */
public class City 
{
    /**
     * The name of the city
     */
    private String name;

    /**
     * The location of the city
     */
    private Point location;

    /**
     * The max connections of the city with a cable
     */
    private int maxconnections;

    /**
     * The remained avaible connections
     */
    private int remainedconnections;

    /**
     * Set the name, location and max connections
     * Convert x and y to a Point
     * 
     * @param name Name of the city
     * @param x The x location
     * @param y The y location
     * @param maxconnections The max connections to the city
     */
    public City(String name, int x, int y, int maxconnections)
    {
        this.name = name;
        this.location = new Point(x, y);
        this.maxconnections = maxconnections;
        this.remainedconnections = maxconnections;
    }

    /**
     * Get the name of the city
     * 
     * @return The city name
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Get the City location
     * 
     * @return The location
     */
    public Point getLocation()
    {
        return this.location;
    }

    /**
     * Get the max connections to the city
     * 
     * @return The max connections
     */
    public int getMaxconnections()
    {
        return this.maxconnections;
    }

    /**
     * Get the remained connections to the city
     * 
     * @return The remianed connections
     */
    public int getRemainedconnections()
    {
        return this.remainedconnections;
    }

    /**
     * Decrement the remained connections
     */
    public void decrementRemainedconnections()
    {
        this.remainedconnections--;
    }

    /**
     *  Calculates the distance to the given City
     * 
     * verbrochen von Marc-Arne
     * changed by Marius
     * @return double 
     */
    public double distancebetweencities(City CityB)
    {
        double cityAB_x = Math.pow(this.location.getX()-CityB.getLocation().getX(), 2);

        double cityAB_y = Math.pow(this.location.getY()-CityB.getLocation().getY(), 2);

        return Math.ceil(Math.sqrt(cityAB_x + cityAB_y));
    }

    /**
     * Parse the City-Element and get the values
     * 
     * @param element The City-Element
     * @return A new City object with the values
     * @throws NumberFormatException Throws when the attributes x, y, maxconnections don't exist 
     * @throws XMLStreamException When the given element isn't a City-Element
     * @throws XMLFormatException Throws when the attribute name don't exist or if the maxconnections attribute is 0
     */
    public static City parseCityElement(StartElement element) throws NumberFormatException, XMLStreamException, XMLFormatException
    {
        if(!element.getName().getLocalPart().equals("City"))
            throw new XMLStreamException("[City] XML elements name isn't City");

        String name = element.getAttributeByName(new QName("name")).getValue();

        if(name == null)
            throw new XMLFormatException("[City] City-Element attribute name don't exist");

        String xAttr = element.getAttributeByName(new QName("x")).getValue();
        String yAttr = element.getAttributeByName(new QName("y")).getValue();
        String maxConnectionsAttr = element.getAttributeByName(new QName("maxconnections")).getValue();

        int x = Integer.parseInt(xAttr);
        int y = Integer.parseInt(yAttr);

        int maxConnections = Integer.parseInt(maxConnectionsAttr);
 
        if(maxConnections == 0)
            throw new XMLFormatException("[City] maxconnections = 0");

        return new City(name, x, y, maxConnections);
    }

    @Override
    public boolean equals(Object o)
    {
        if(o == null)
            return false;

        if(!(o instanceof City))
            return false;

        City c = (City)o;

        // if 2 cities have the same location they are equal
        return this.location.equals(c.location);
    }
}
