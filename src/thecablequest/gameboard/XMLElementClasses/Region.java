package thecablequest.gameboard.XMLElementClasses;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collections;

import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import breitband.preset.ImmutableList;
import thecablequest.exceptions.XMLFormatException;
import thecablequest.helperClasses.Point;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLEventReader; 

import java.io.IOException;


/**
 * A Region represented with borders and cities
 * 
 * @author Marius Degenhardt
 */
public class Region 
{
    /**
     * The region name
     */
    private String name;

    /**
     * The list of borders
     */
    private ImmutableList<Border> borders;

    /**
     * The list of cities
     */
    private ImmutableList<City> cities;

    /**
     * 
     * @param name Region name
     * @param borders the given Borders
     * @param cities the given Cities
     */
    public Region(String name, ImmutableList<Border> borders, ImmutableList<City> cities)
    {
        this.name = name;
        this.borders = borders;
        this.cities = cities;
    }

    /**
     * The avaible cities in a region where a the remained connections are > 0
     * 
     * @return The List of City objects
     */
    public ImmutableList<City> avaibleCities()
    {
        ArrayList<City> cityList = new ArrayList<>();

        for(City city : this.cities)
        {
            if(city.getRemainedconnections() > 0)
                cityList.add(city);
        }

        return new ImmutableList<>(cityList);
    }


    /**
     * Get the Region name
     * 
     * @return Region name
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Get the borders in the region
     * 
     * @return borders
     */
    public ImmutableList<Border> getBorder()
    {
        return this.borders;
    }

    /**
     * Get the cities in the region
     * 
     * @return cities
     */
    public ImmutableList<City> getCities()
    {
        return this.cities;
    }

     /**
      * Parse the Region-Element 
      *
      * @param element The Region-Element
      * @param eventReader The XMLEventReader for reading the children
      * @return A new Region object
      * @throws XMLStreamException Throws if the element isn't a Region-Element
      * @throws IOException Throws if the name don't exist or the a Border/City is missing
      */
    public static Region parseRegionElement(StartElement element, XMLEventReader eventReader) throws XMLStreamException, XMLFormatException
    { 
        if(!element.getName().getLocalPart().equals("Region"))
            throw new XMLStreamException("[Region] XML elements name isn't Region");

        String name = element.getAttributeByName(new QName("name")).getValue();

        if(name == null)
            throw new XMLFormatException("[Region] name attribute don't exist");

        ArrayList<Border> borderList = new ArrayList<>();
        ArrayList<City> cityList = new ArrayList<>();

        while(eventReader.hasNext())
        {             
            XMLEvent event = eventReader.nextEvent();

            // if we've encounter an closing element
            if(event.isEndElement() && event.asEndElement().getName().getLocalPart().equals("Region"))
                break;

            // we only want to have start element events
            if(!event.isStartElement())
                continue;

            element = event.asStartElement();

            if(element.getName().getLocalPart().equals("Border"))
                borderList.add(Border.parseBorderElement(element));

            if(element.getName().getLocalPart().equals("City"))
                cityList.add(City.parseCityElement(element));
        }

        if(borderList.size() == 0)
            throw new XMLFormatException("There's no Border-Elements");

        if(cityList.size() == 0)
            throw new XMLFormatException("There's no City-Elements");

        validateUniqueName(cityList);
        validateUniquePosition(cityList);
        validatePosition(cityList, borderList);

        return new Region(name, new ImmutableList<>(borderList), new ImmutableList<>(cityList));
    }

    /**
     * Validate if a city location is on a border
     * 
     * @param cities The given cities
     * @param borders The giveen borders
     * @throws XMLFormatException If a city lies on a border or less than 2 borderpoints were given
     */
    private static void validatePosition(ArrayList<City> cities, ArrayList<Border> borders) throws XMLFormatException
    {
        for(City nextCity : cities)
        {
            for(Border nextBorder : borders)
            {
                if(nextBorder.getBorderPoints().size() < 2)
                    throw new XMLFormatException("[Region] We need more than 2 points");

                Iterator<Point> pointsIt = nextBorder.getBorderPoints().iterator();

                // the start point to compare with
                Point startPoint = pointsIt.next();

                while(pointsIt.hasNext())
                {
                    Point endPoint = pointsIt.next();

                    if(nextCity.getLocation().isOnLine(startPoint, endPoint))
                        throw new XMLFormatException("[Region] A city location can't be on a border");

                    startPoint = endPoint;
                }
            }
        }
    }

    /**
     * Validate if every Region has a unique position
     * 
     * @param cities The cities to validate
     * @throws XMLFormatException Throws if one or more City don't have an unique position
     */
    private static void validateUniquePosition(ArrayList<City> cities) throws XMLFormatException
    {
        ArrayList<Point> list = new ArrayList<>(); 

        // add all cities.getLocation() to list;
        cities.forEach((p) -> { list.add(p.getLocation()); });

        Iterator<City> it = cities.iterator();

        while(it.hasNext())
        {
            City next = it.next();

            int frequency = Collections.frequency(list, next.getLocation());

            if(frequency != 1)
                throw new XMLFormatException("[Region] All City-Elements need to have an unique position");
        }
    }

    /**
     * Get the neighbour cities when the distance is less or equal maximalDistance
     * 
     * 
     * @param cityA The city where you want the neighbours from
     * @param maximalDistance The max distance from cityA
     * @return The City list where the neighbour city is less or equal maximalDistance
     */
    public ImmutableList<City> getNeighbourCities(City cityA, double maximalDistance)
    {
        Iterator<City> cityIt = this.cities.iterator();

        ArrayList<City> cityList = new ArrayList<>();

        while(cityIt.hasNext())
        {
            City next = cityIt.next();

            double distance = cityA.distancebetweencities(next);
            
            if(distance <= maximalDistance)
                cityList.add(next);
            
        }

        return new ImmutableList<>(cityList);
    }

     /**
     * Validate if every Cities has a unique name
     * 
     * @param cities The cities to validate
     * @throws XMLFormatException Throws if one or more City don't have an unique name
     */
    private static void validateUniqueName(ArrayList<City> cities) throws XMLFormatException
    {
        ArrayList<String> list = new ArrayList<>(); 

        // add all cities.getName() to list;
        cities.forEach((c) -> { list.add(c.getName()); });

        Iterator<City> it = cities.iterator();

        while(it.hasNext())
        {
            City next = it.next();

            int frequency = Collections.frequency(list, next.getName());

            if(frequency != 1)
                throw new XMLFormatException("[Region] All City-Elements need to have an unique name");
        }
    }
}
