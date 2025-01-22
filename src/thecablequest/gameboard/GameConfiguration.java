package thecablequest.gameboard;

import thecablequest.helperClasses.Point;
import thecablequest.gameboard.XMLElementClasses.*;

import java.io.IOException;
import java.io.Reader;
import java.util.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader; 
import javax.xml.stream.XMLInputFactory; 
import javax.xml.stream.XMLStreamException; 
import javax.xml.stream.events.*;

import breitband.preset.ImmutableList;
import sag.IconNotFoundException;
import thecablequest.exceptions.CableNotFoundException;
import thecablequest.exceptions.RegionNotFoundException;
import thecablequest.exceptions.XMLFormatException;
import thecablequest.gameboard.XMLElementClasses.CableConfigs;
import thecablequest.gameboard.XMLElementClasses.City;
import thecablequest.gameboard.XMLElementClasses.CityToCityContract;
import thecablequest.gameboard.XMLElementClasses.ContractPool;
import thecablequest.gameboard.XMLElementClasses.MapCredits;
import thecablequest.gameboard.XMLElementClasses.Region;
import thecablequest.gameboard.XMLElementClasses.RegionContract;
import thecablequest.gameboard.XMLElementClasses.Regions;

/**
 * The GameConfiguration class. It parses the given BreitbandGameConfiguration XML file. The values can be accessed with their getters.
 * The values has the same structure as the XML file.
 * For example: Regions->Region->Border
 * 
 * @author Marius Degenhardt
 */
public class GameConfiguration 
{
    /**
     * The xml event reader
     */
    private XMLEventReader eventReader;

    /**
     * The reader file
     */
    private static Reader xmlFile;

    /**
     * The GameConfigration instance
     */
    private static GameConfiguration instance;

    /**
     * The given regions
     */
    private Regions regions;

    /**
     * The ContractPool
     */
    private ContractPool contractPool;

    /**
     * The CableConfigs
     */
    private CableConfigs cableConfigs;

    /**
     * The MapCredits
     */
    private MapCredits mapCredits;


    /**
     * Create the XMLEventReader instance and starts parsing the XML file
     * 
     * @param xmlFile The XML file
     * @throws XMLStreamException Throws if something went wrong while parsing the XML
     * @throws NumberFormatException Throws if an attribute should be a number but isn't
     * @throws IconNotFoundException Throws if the icon isn't found
     * @throws XMLFormatException Throws if the xml format isn't correct
     * @throws RegionNotFoundException Throws if a searched region isn't defined
     * @throws CableNotFoundException Throws if a cable don't exist
     */
    private GameConfiguration(Reader xmlFile) throws XMLStreamException, NumberFormatException, IconNotFoundException, XMLFormatException, RegionNotFoundException, CableNotFoundException 
    {
        XMLInputFactory factory = XMLInputFactory.newInstance();         
        this.eventReader = factory.createXMLEventReader(xmlFile); 

        
        XMLParser();  
    }

    /**
     * Creates the GameConfiguration instance once and returns the instance
     * 
     * @return The GameConfiguration instance
     * @throws IOException Throws if an attribute don't exist or don't match the required limitation
     * @throws NumberFormatException Throws if an attribute should be a number but isn't
     * @throws IconNotFoundException Throws if the icon isn't found
     * @throws XMLFormatException Throws if the xml format isn't correct
     * @throws RegionNotFoundException Throws if a searched region isn't defined
     * @throws CableNotFoundException Throws if a cable don't exist
     */
    public static GameConfiguration getInstance() throws NumberFormatException, XMLStreamException, IOException, IconNotFoundException, XMLFormatException, RegionNotFoundException, CableNotFoundException
    {
        if(GameConfiguration.xmlFile == null)
            throw new IOException(
                "[GameConfiguration] Bevor calling GameConfiguration.getInstance() you need to call GameConfiguration.setXMLFile(Reader xmlFile)");

        if(GameConfiguration.instance == null)
            GameConfiguration.instance = new GameConfiguration(GameConfiguration.xmlFile);

        return GameConfiguration.instance;
    }

    /**
     * Method for setting the XML file
     * 
     * @param xmlFile The XML file
     */
    public static void setXMLFile(Reader xmlFile)
    {
        GameConfiguration.xmlFile = xmlFile;
    }

    /**
     * Start the XMLParser on the given file in the constructor.
     * 
     * @throws XMLStreamException Throws if something went wrong while parsing the XML
     * @throws NumberFormatException Throws if an attribute should be a number but isn't
     * @throws IconNotFoundException Throws if the icon isn't found
     * @throws XMLFormatException Throws if the xml format isn't correct
     * @throws RegionNotFoundException Throws if a searched region isn't defined
     * @throws CableNotFoundException Throws if a cable don't exist
     */
    private void XMLParser() throws XMLStreamException, NumberFormatException, IconNotFoundException, XMLFormatException, RegionNotFoundException, CableNotFoundException
    {
        String xmlElements = "";

        while (this.eventReader.hasNext()) 
        {
            XMLEvent event = this.eventReader.nextEvent();

            if(!event.isStartElement())
                continue;

            StartElement element = event.asStartElement();

            if(element.getName().getLocalPart() == "BreitbandGameConfiguration")
            {
                String version = element.getAttributeByName(new QName("version")).getValue();

                // if the version don't exists or isn't version 2
                if(version == null || !version.equals("2"))
                    throw new XMLFormatException("[GameConfiguration] The version attribute don't exist");
            }

            if(element.getName().getLocalPart() == "Regions")
                this.regions = Regions.parseRegionsElement(element, eventReader);

            if(element.getName().getLocalPart() == "ContractPool")
                this.contractPool = ContractPool.parseContractPoolElement(element, eventReader);

            if(element.getName().getLocalPart() == "CableConfigs")
                this.cableConfigs = CableConfigs.parseCableConfigsElement(element, eventReader);

            if(element.getName().getLocalPart() == "MapCredits")
                this.mapCredits = MapCredits.parseMapCredits(element);

             // add xml elements name to the string
             xmlElements += element.getName().getLocalPart();
        }

        validateXMLFormatStructure(xmlElements);

        validateCityExists(this.contractPool, this.regions);
        validateUniqueCityContract(this.contractPool);

        validateRegionExists(this.contractPool, this.regions);
        validateRegionHasEnoughCities(this.contractPool, this.regions);
    }


    /**
     * Validate if the Region defined in RegionContract has enough cities
     * 
     * @param contractPool The contractPool where the CityToCityContracts are located
     * @param regions The regions where the city exists
     * @throws RegionNotFoundException Throws if the Region don't exist
     * @throws XMLFormatException Throws if the Region has less than 3 cities
     */
    private static void validateRegionHasEnoughCities(ContractPool contractPool, Regions regions) throws RegionNotFoundException, XMLFormatException
    {
        Iterator<RegionContract> regionsContractIt = contractPool.getRegionContracts().iterator();

        while(regionsContractIt.hasNext())
        {
            RegionContract nextRegionContract = regionsContractIt.next();

            Region region = regions.findRegion(nextRegionContract.getRegion());

            if(region.getCities().size() < 3)
                throw new XMLFormatException("[GameConfiguration] Region needs to have atleast 3 cities");
        }
    }

    /**
     * Validate if a Region exists for an RegionContract
     * 
     * @param contractPool The contractPool where the RegionContract is located
     * @param regions The regions where the Region is located
     * @throws XMLFormatException Throws if the Region don't exist
     * @throws RegionNotFoundException 
     */
    private static void validateRegionExists(ContractPool contractPool, Regions regions) throws RegionNotFoundException
    {    
        Iterator<RegionContract> regionContractIt = contractPool.getRegionContracts().iterator();

        while(regionContractIt.hasNext())
        {
            RegionContract nextRegionContract = regionContractIt.next();

            regions.findRegion(nextRegionContract.getRegion());
        }
    }

    /**
     * Validate if there's only one CityToCityContract for the cityA and cityB
     * 
     * @param contractPool The contractPool where the CityToCityContracts are located
     * @throws XMLFormatException If theres two or more CityToCityContracts that have the same city conncetion
     */
    private static void validateUniqueCityContract(ContractPool contractPool) throws XMLFormatException
    {
        ImmutableList<CityToCityContract> cityContracts = contractPool.getCityContracts();

        ArrayList<String> contractsNames = new ArrayList<>();

        for(CityToCityContract contract : cityContracts)
        {
            contractsNames.add(contract.getId());
        }

        HashSet<String> contractsHashSet = new HashSet<>(contractsNames);

        if(contractsHashSet.size() != contractsNames.size())
            throw new XMLFormatException("[GameConfiguration] More than one CityToCityContract"); 

    }

    /**
     * Validate if the cityA and cityB in a CityToCityContract exitsts
     * 
     * @param contractPool The contractPool where the CityToCityContract exitsts
     * @param regions The regions where the city exists
     * @throws XMLFormatException Throws if one or more city don't exist
     */
    private static void validateCityExists(ContractPool contractPool, Regions regions) throws XMLFormatException
    {
        Iterator<CityToCityContract> cityContractIt = contractPool.getCityContracts().iterator();

        while(cityContractIt.hasNext())
        {
            CityToCityContract cityContract = cityContractIt.next();

            String cityA = cityContract.getCityA();
            String cityB = cityContract.getCityB();

            if(!isExsistingCities(cityA, cityB, regions))
                throw new XMLFormatException("[GameConfiguration] City don't exist");
        }
        
    }

    /**
     * Check if the given Cities exist in the regions
     * 
     * @param cityA First city
     * @param cityB Second city
     * @param regions The regions where you find the cities
     * @return True if cityA and cityB exist, False otherwise
     */
    private static boolean isExsistingCities(String cityA, String cityB, Regions regions)
    {
        boolean foundCityA = false, foundCityB = false;

        for(Region nextRegion : regions.getRegions())
        {
            for(City nextCity : nextRegion.getCities())
            {                
                if(nextCity.getName().equals(cityA))
                    foundCityA = true;

                if(nextCity.getName().equals(cityB))
                    foundCityB = true;
            }
        }

        return foundCityA && foundCityB;
    }


    /**
     * validates the element structure of the XML file
     * 
     * @param xmlElements The XML elements to match with the given format. The xmlElements shouldn't have a seperator. Lower or upper-case don't matter
     * @param xmlFormat The format to match with.
     * @throws XMLFormatException If the XML structure don't match
     */
    private void validateXMLFormatStructure(String xmlElements) throws XMLFormatException
    {
        String[] format = {
            "BreitbandGameConfiguration", "Country", "Description", "MapCredits", 
            "Regions", "ContractPool", "CableConfigs"};

        Pattern xmlFormat = Pattern.compile(String.join("", format), Pattern.CASE_INSENSITIVE);

        Matcher matcher = xmlFormat.matcher(xmlElements);

        if(!matcher.find())
            throw new XMLFormatException("[GameConfiguration] XML Elements don't match");
    }

    /**
     * Get the MapCredits
     * 
     * @return The MapCredits
     */
    public MapCredits getMapCredits()
    {
        return this.mapCredits;
    }

    /**
     * Get the Regions-Element
     * 
     * @return The Regions-Element
     */
    public Regions getRegions() 
    {
        return this.regions;
    }

    /**
     * Get the ContractPool-Element
     * 
     * @return The ContractPool-Element
     */
    public ContractPool getContractPool() 
    {
        return this.contractPool;
    }

    /**
     * Get the CableConfigs-Element
     * 
     * @return The CableConfigs-Element
     */
    public CableConfigs getCableConfigs() 
    {
        return this.cableConfigs;
    }

    /**
     * This function checks if the connection between the given Cities intersects a border
     * This is currently very recource intensive, USE AS LITTLE AS POSSIBLE
     * @return boolean 
     * 
     * verbrochen von Marc-Arne
     */
    public boolean connectionIntersectsAnyRegionBorder(City CityA, City CityB){
        for(Region region : this.regions.getRegions()){
            if( connectionIntersectsRegionBorder(region, CityA.getLocation(),CityB.getLocation()) ){
                return true;
            }
        }
        return false;
    }
    /**
     * This function Check if the given line from PointA to PointB intersects with the given Regionborder
     * this uses brute force #subject to change
     * @return boolean
     * 
     */
    public boolean connectionIntersectsRegionBorder(Region region,Point pointA,Point pointB){
        //go through all the Polygons (borders) in the region
        for (Border border: region.getBorder()){
            //go throgh all the points in the polygon
            for(int i= 0; i< border.getBorderPoints().size(); i++){
                //and check if pointA->pointB intersects with the line drawn to the next point 
                if (linesIntersect(border.getBorderPoints().get(i),border.getBorderPoints().get( (i+1)% border.getBorderPoints().size() ) , pointA, pointB)){
                    return true;
                }
            }
        }
        return false;
    }
    /**
     * checkst if the lines (given as 2 points) have an intersection
     */
    boolean linesIntersect(Point lineOnePointA, Point lineOnePointB, Point lineTwoPointA, Point lineTwoPointB ){
        double denominator = (lineOnePointA.getX() - lineOnePointB.getX()) * (lineTwoPointA.getY() - lineTwoPointB.getY()) - (lineOnePointA.getY() - lineOnePointB.getY()) * (lineTwoPointA.getX() - lineTwoPointB.getX());
        if (denominator == 0) {
            return false; // lines are parallel, no intersection
        } else {
            double t = ((lineOnePointA.getX() - lineTwoPointA.getX()) * (lineTwoPointA.getY() - lineTwoPointB.getY()) - (lineOnePointA.getY() - lineTwoPointA.getY()) * (lineTwoPointA.getX() - lineTwoPointB.getX())) / denominator;
            double u = -((lineOnePointA.getX() - lineOnePointB.getX()) * (lineOnePointA.getY() - lineTwoPointA.getY()) - (lineOnePointA.getY() - lineOnePointB.getY()) * (lineOnePointA.getX() - lineTwoPointA.getX())) / denominator;
            if (0 <= t && t <= 1 && 0 <= u && u <= 1) {
                return true; // intersection found
            } else {
                return false; // no intersection
            }
    }
    }
    
}
