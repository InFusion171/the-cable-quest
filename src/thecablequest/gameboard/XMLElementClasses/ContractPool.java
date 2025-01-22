package thecablequest.gameboard.XMLElementClasses;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collections;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import breitband.preset.ImmutableList;
import thecablequest.exceptions.XMLFormatException;

import javax.xml.stream.XMLEventReader; 

/**
 * The contract pool with the city to city contacts and region contracts
 * 
 * @author Marius Degenhardt
 */
public class ContractPool 
{
    /**
     * The subset size of how many contracts are displayed at the start of the game
     */
    private int subsetSize;

    /**
     * The city to city contracts in a list
     */
    private ImmutableList<CityToCityContract> cityContracts;

    /**
     * The region contracts
     */
    private ImmutableList<RegionContract> regionContracts;

    /**
     * Set the subset size for the contracts, the city to city contacts and region contracts
     * 
     * @param subsetSize The subset size for the contracts
     * @param cityContracts The city to city contracts
     * @param regionContracts the region contracts
     */
    public ContractPool(int subsetSize, ImmutableList<CityToCityContract> cityContracts, ImmutableList<RegionContract> regionContracts) 
    {
        this.subsetSize = subsetSize;
        this.cityContracts = cityContracts;
        this.regionContracts = regionContracts;
    }

    /**
     * Get the subset size for the contracts
     * 
     * @return The subset size
     */
    public int getSubsetSize() 
    {
        return this.subsetSize;
    } 

    /**
     * Get all Contracts
     * 
     * @return CityToCityContracts and RegionContracts combined
     */
    public ArrayList<Contract> getAllContracts()
    {
        ArrayList<Contract> concatContracts = new ArrayList<>();

        concatContracts.addAll(getCityContracts());
        concatContracts.addAll(getRegionContracts());

        return concatContracts;
    }
    
    /**
     *  Creates an Immutable List of all Contract Ids in this Pool as a string
     * @return ImmutableList<String>
     * verbrochen von Marc-Arne
     */
    public ImmutableList<String> getAllContractIds(){
        ArrayList<String> tempList = new ArrayList<String>();
        for (Contract contract : this.getAllContracts()){
            tempList.add(contract.getId());
        }
        return new ImmutableList<>(tempList);
    }

    /**
     * Get the city to city contracts
     * 
     * @return The city contracts
     */
    public ImmutableList<CityToCityContract> getCityContracts() 
    {
        return this.cityContracts;
    }

    /**
     * Get the region contracts
     * 
     * @return The region contracts
     */
    public ImmutableList<RegionContract> getRegionContracts()
    {
        return this.regionContracts;
    }
    /**
     * Check if the given ContractID is in this Contractpool
     * 
     * verbrochen von Marc-Arne
     * @return boolean
     */
    public boolean contains(String contractID){
        //goes through all possible contracts and checks if a contractid matches
        for (CityToCityContract cityContract : cityContracts){
            if (cityContract.getId().equals(contractID)){
                return true;
            }
        }
        for (RegionContract regionContract : regionContracts){
            if (regionContract.getId().equals(contractID)){
                return true;
            }
        }
        return false;
    }

    /**
     * Find the given Contract by id and return the associated Contract object
     * 
     * @param contract The contract to find or null if the contract don't exist
     */
    public Contract findContract(String ContractID)
    {
       for(RegionContract contract : this.regionContracts)
       {
            if(contract.getId().equals(ContractID))
                return contract;
       }

       for(CityToCityContract contract : this.cityContracts)
       {
            if(contract.getId().equals(ContractID))
                return contract;
       }

       return null;
    }

    /**
     * Parse the ContractPool Element
     * 
     * @param element The ContractPool-Element
     * @param eventReader The XMLEventReader instance to iterate over the child elements
     * @return A new ContractPool object
     * @throws XMLStreamException Throws when the element isn't a ContractPool-Element
     * @throws XMLFormatException Throws when the subsetsize don't exist or is less than 6 or greater than the total contract count
     * @throws NumberFormatException when subsetsize isn't a number
     */
    public static ContractPool parseContractPoolElement(StartElement element, XMLEventReader eventReader) throws XMLStreamException, NumberFormatException, XMLFormatException
    {
        if(!element.getName().getLocalPart().equals("ContractPool"))
            throw new XMLStreamException("[ContractPool] XML elements name isn't ContractPool");

        String subsetSizeString = element.getAttributeByName(new QName("subsetsize")).getValue();

        if(subsetSizeString == null)
            throw new XMLFormatException("[ContractPool] subsetsize don't exist");

        ArrayList<CityToCityContract> cityContractList = new ArrayList<>();
        ArrayList<RegionContract> regionContractList = new ArrayList<>();

        while(eventReader.hasNext())
        {
            XMLEvent event = eventReader.nextEvent();

            // if we've encounter an closing element
            if(event.isEndElement() && event.asEndElement().getName().getLocalPart().equals("ContractPool"))
                break;

            // we only want to have start element events
            if(!event.isStartElement())
                continue;

            StartElement nextElement = event.asStartElement();

            if(nextElement.getName().getLocalPart().equals("CityToCityContract"))
                cityContractList.add(CityToCityContract.parseCityToCityContractElement(nextElement));
       
            else if(nextElement.getName().getLocalPart().equals("RegionContract"))
                regionContractList.add(RegionContract.parseRegionContractElement(nextElement));
        }

        int subsetSize;

        try
        {
            subsetSize = Integer.parseInt(subsetSizeString);
        }
        catch(NumberFormatException e)
        {
            e.printStackTrace();
            throw new NumberFormatException("[ContractPool] subsetsize isn't a number");
        }

        int contractCount = cityContractList.size() + regionContractList.size();

        // if less than 6 contracts are defined
        if(contractCount < 6)
            throw new XMLFormatException("[ContractPool] Less than 6 contracts were given");

        if(subsetSize < 6 || subsetSize > contractCount || subsetSize > 60)
            throw new XMLFormatException("[ContractPool] subsetsize is less than 6 or more than " + contractCount + " contracts or greater than 60");

        validateUniqeIdCityContract(cityContractList);
        validateUniqeIdRegionContract(regionContractList);
        
        return new ContractPool(subsetSize, new ImmutableList<>(cityContractList), new ImmutableList<>(regionContractList));
    }

    /**
     * Validate if all CityToCityContract have a unique id
     * 
     * @param cityContracts The CityToCityContract list
     * @throws XMLFormatException If one or more CityToCityContract-Elements don't have a uniqe id
     */
    private static void validateUniqeIdCityContract(ArrayList<CityToCityContract> cityContracts) throws XMLFormatException
    {
        ArrayList<String> list = new ArrayList<>(); 

        // add all cityContracts.getId() to list;
        cityContracts.forEach((c) -> { list.add(c.getId()); });

        Iterator<CityToCityContract> it = cityContracts.iterator();

        while(it.hasNext())
        {
            CityToCityContract next = it.next();

            int frequency = Collections.frequency(list, next.getId());

            if(frequency != 1)
                throw new XMLFormatException("[ContractPool] All CityToCityContract-Elements need to have an unique id");
        }
    }

    /**
     * Validate if all RegionContract have a unique id
     * 
     * @param cityContracts The RegionContract list
     * @throws XMLFormatException If one or more RegionContract-Elements don't have a uniqe id
     */
    private static void validateUniqeIdRegionContract(ArrayList<RegionContract> regionContracts) throws XMLFormatException
    {
        ArrayList<String> list = new ArrayList<>(); 

        // add all regionContracts.getId() to list;
        regionContracts.forEach((r) -> { list.add(r.getId()); });

        for(RegionContract contract : regionContracts)
        {
            int frequency = Collections.frequency(list, contract.getId());

            if(frequency != 1)
                throw new XMLFormatException("[ContractPool] All RegionContract-Elements need to have an unique id");
        }
    }

}

