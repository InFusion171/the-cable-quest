package thecablequest.gameboard.XMLElementClasses;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import breitband.preset.ImmutableList;
import thecablequest.exceptions.RegionNotFoundException;
import thecablequest.exceptions.XMLFormatException;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLEventReader; 

import java.io.IOException;

/**
 * Regions represented with regions
 * 
 * @author Marius Degenhardt
 */
public class Regions 
{
    private ImmutableList<Region> regions;

    /**
     * Set the regions
     * 
     * @param regions The Regions
     */
    public Regions(ImmutableList<Region> regions)
    {
        this.regions = regions;
    }
    public ArrayList<City> getAllCities(){
        ArrayList<City> list =new ArrayList<>();
        for(Region region: regions){
            for(City city : region.getCities()){
                list.add(city);
            }
        } 
        return list;

    }

    /**
     * Get the Regions
     * @return The Regions
     */
    public ImmutableList<Region> getRegions()
    {
        return this.regions;
    }

    

    /**
     * Parse the Regions-Element
     * 
    * @param element The Regions-Element
    * @param eventReader The XMLEventReader for reading the children
    * @return A new Regions object
    * @throws XMLStreamException Throws if element isn't a Regions-Element
    * @throws IOException Throws if no Region is defined
     * @throws XMLFormatException 
    */
    public static Regions parseRegionsElement(StartElement element, XMLEventReader eventReader) throws XMLStreamException, XMLFormatException
    {
        if(!element.getName().getLocalPart().equals("Regions"))
            throw new XMLStreamException("[Regions] XML elements name isn't Regions");


        ArrayList<Region> regionList = new ArrayList<>();

        while(eventReader.hasNext())
        {
            XMLEvent event = eventReader.nextEvent();

            // if we've encounter an closing element
            if(event.isEndElement() && event.asEndElement().getName().getLocalPart().equals("Regions"))
                break;

            // we only want to have start element events
            if(!event.isStartElement())
                continue;

            StartElement nextElement = event.asStartElement();

            if(nextElement.getName().getLocalPart().equals("Region"))
                regionList.add(Region.parseRegionElement(nextElement, eventReader));
                  
        }
    
        if(regionList.size() == 0)
            throw new XMLFormatException("[Regions] There must be atleast one Region-Element");

        validateUniqueName(regionList);

        return new Regions(new ImmutableList<>(regionList));
    }

    /**
     * Validate if every Region has a unique name
     * 
     * @param regions The regions to validate
     * @throws XMLFormatException Throws if one or more Region don't have an unique name
     */
    private static void validateUniqueName(ArrayList<Region> regions) throws XMLFormatException
    {
        ArrayList<String> list = new ArrayList<>(); 

        // add all regions.getName() to list;
        regions.forEach((region) -> { list.add(region.getName()); });

        Iterator<Region> it = regions.iterator();

        while(it.hasNext())
        {
            Region next = it.next();

            int frequency = Collections.frequency(list, next.getName());

            if(frequency != 1)
                throw new XMLFormatException("[Regions] All Region-Elements need to have an unique name");
        }
    }

    /**
     * Find the given city by name and return the associated City object
     * 
     * @param city The city to find or null if the city don't exist
     */
    public City findCity(String cityName)
    {
        for(Region region : this.regions)
        {
            for(City city : region.getCities())
            {
                if(city.getName().equalsIgnoreCase(cityName))
                    return city;
            }
        }

        return null;
    }

    /**
     * Find the Region by name
     * 
     * @param regionName The Region name
     * @return The Region
     * @throws RegionNotFoundException If the Region name isn't an existing Region
     */
    public Region findRegion(String regionName) throws RegionNotFoundException
    {
        for(Region region : this.regions)
        {
            if(region.getName().equals(regionName))
                return region;            
        }

        throw new RegionNotFoundException("[Regions] The Region name: " + regionName + " don't exist");
    }

    public ImmutableList<City> getNeighbourCities(City cityA, double maximalDistance)
    {

        ArrayList<City> allCities = new ArrayList<>();
        
       
        for (Region region : this.regions){
            allCities.addAll(region.getCities());
        
        }
        Iterator<City> cityIt = allCities.iterator();

        ArrayList<City> cityList = new ArrayList<>();

        while(cityIt.hasNext())
        {
            City next = cityIt.next();

            double distance = cityA.distancebetweencities(next);
            
            if(distance <= maximalDistance && distance != 0)
                cityList.add(next);
            
        }

        return new ImmutableList<>(cityList);
    }
}
