package thecablequest.gameboard.XMLElementClasses;

import java.util.ArrayList;
import java.util.*;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import breitband.preset.ImmutableList;
import sag.IconNotFoundException;
import thecablequest.exceptions.CableNotFoundException;
import thecablequest.exceptions.XMLFormatException;

/**
 * The cable configs represented with an Arraylist of CableConfig objects and the ending length
 * The ending length is the max sum of cable lengths that a player is allowed to build
 * 
 * @author Marius Degenhardt
 */
public class CableConfigs 
{
    /**
     * The ending length
     */
    private int endingLength;

    /**
     * The different cable configs
     */
    private ImmutableList<CableConfig> cableConfigs;

    /**
     * Set the ending Length and the Arraylist of CableConfig objects
     * 
     * @param endingLength The ending length
     * @param cableConfigs The cable configs
     * @throws CableNotFoundException 
     */
    public CableConfigs(int endingLength, ImmutableList<CableConfig> cableConfigs) throws CableNotFoundException
    {
        this.endingLength = endingLength;
        this.cableConfigs = cableConfigs;
    }

    /**
     * Get the ending length
     * 
     * @return The ending length
     */
    public int getEndingLength() 
    {
        return this.endingLength;
    }

    /**
     * Get the cable configs
     * 
     * @return The cable Configs
     */
    public ImmutableList<CableConfig> getCableConfigs() 
    {
        return this.cableConfigs;
    }

    /**
     * Check if the given cableType exists
     * 
     * @param cableConfigs The CableConfig list where the cable types exist
     * @param cableType The cable type to search for
     * @return True if the cable exist, false otherwise
     */
    private static boolean cableExitsts(ImmutableList<CableConfig> cableConfigs, String cableType)
    {
        Iterator<CableConfig> it = cableConfigs.iterator();

        while(it.hasNext())
        {
            if(it.next().getType().equals(cableType))
                return true;
        }

        return false;
    }

    /**
     * Validate if a referenced cable in canCrossCables refer back
     * 
     * @param cableConfigs The cableConfigs
     * @throws CableNotFoundException Throws if a cable don't exist
     * @throws XMLFormatException Throws if a cable don't reference back
     */
    private static void validateReferenceFromCanCrossCables(ImmutableList<CableConfig> cableConfigs) throws CableNotFoundException, XMLFormatException
    {
        for(CableConfig nextCableConfig : cableConfigs)
        {
            String[] cables = nextCableConfig.getCanCrossCables().split(",");

            for(String cable : cables)
            {
                // get the CableConfig from the referenced cable
                CableConfig referencedCables = getCableConfig(cableConfigs, cable);

                // check if in the referenced cable it reference back to the other cable
                if(!containsInCanCrossCables(referencedCables, nextCableConfig.getType()))
                    throw new XMLFormatException("[CableConfigs] The referenced cable in canCrossCables don't refer back");       
            }
        }
    }


    /**
     * Check if the cableType exists in CanCrossCables
     * 
     * @param cableConfig The cable config
     * @param cableType The cable type to search fro
     * @return true if the cable type is contained, false otherwise
     */
    private static boolean containsInCanCrossCables(CableConfig cableConfig, String cableType)
    {
        String[] cablesFromCanCrossCables = cableConfig.getCanCrossCables().split(",");

        for(String cable : cablesFromCanCrossCables)
        {
            if(cable.equals(cableType))
                return true;
        }

        return false;
    }

    /**
     * Get the CableConfig from the cable type
     * 
     * @param cableType The cable type to search
     * @return The CableConfig object associated with the cable type
     * @throws CableNotFoundException Throws if the cable don't exist
     */
    public static CableConfig getCableConfig(ImmutableList<CableConfig> cableConfigs, String cableType) throws CableNotFoundException
    {
        for(CableConfig config : cableConfigs)
        {
            if(config.getType().equals(cableType))
                return config;
        }

        throw new CableNotFoundException("[CableConfigs] " + cableType + " don't exist");
    }

    /**
     * Validate if the given canCrossCables exists
     * 
     * @param cableConfigs The CableConfig list
     * @throws CableNotFoundException Throws if the cable don't exist
     */
    private static void validateCableTypeExist(ImmutableList<CableConfig> cableConfigs) throws CableNotFoundException
    {
        Iterator<CableConfig> it = cableConfigs.iterator();

        while(it.hasNext())
        {
            CableConfig next = it.next();

            String[] cables = next.getCanCrossCables().split(",");

            for(String cable : cables)
            {
                if(!cableExitsts(cableConfigs, cable))
                    throw new CableNotFoundException("[CableConfigs] Cable don't exist");
            }
        }

    }

    /**
     * Finds the Cable of the given name if in this CableConfig, otherwise return null
     * 
     * verbrochen by Marc-Arne
     * @return CableConfig
     */
    public CableConfig getCableConfigByName(String name){
        for (CableConfig cable : cableConfigs){
            if(cable.getType().equals(name)){
                return cable;
            }
        }
        return null;
    }
    /**
     * Converts a Map<String,Integer> of cables to Map<CableConfig,Integer>
     * 
     * Verbrochen by Batoul
     */
    public Map<CableConfig, Integer> convertToCableConfigMap(Map<String, Integer> pickedUpCables) {
        Map<CableConfig, Integer> cableConfigMap = new HashMap<>();
    
        for (Map.Entry<String, Integer> entry : pickedUpCables.entrySet()) {
            CableConfig cableConfig = getCableConfigByName(entry.getKey());
            if (cableConfig != null) {
                cableConfigMap.put(cableConfig, entry.getValue());
            }
        }
    
        return cableConfigMap;
    }
    
    /**
     * returns true if name is a cable within this cableConfigs
     * 
     * verbrochen von Marc-Arne
     * @return boolean
     */
    public boolean isLegal(String name){
        for (CableConfig cable : cableConfigs){
            if(name.equals(cable.getType())){
                return true;
            }
        }
        return false;
    }
    /**
     * Goes through all cables in this CableConfigs and returns the max value of a cable
     * 
     * verbrochen von Marc-Arne
     * @return int 
     */
    public int getMaxCableValue(){
        int max = 0;
        for (CableConfig cableConfig : cableConfigs){
            //What is larger? max or the value of the cable?
            max = Math.max(max, cableConfig.getValue());
        }
        return max;
    }

    /**
     * Parse the CableConfigs-Element with its value
     * 
     * @param element CableConfigs-Element
     * @param eventReader The XMLEventReader for reading the children 
     * @return A new CableConfigs object
     * @throws NumberFormatException Throws if endinglength isn't a number
     * @throws XMLStreamException Throws if element isn't a CableConfigs-Element
     * @throws XMLFormatException Throws if endinglength don't exist
     * @throws CableNotFoundException 
     */
    public static CableConfigs parseCableConfigsElement(StartElement element, XMLEventReader eventReader) throws NumberFormatException, XMLStreamException, IconNotFoundException, XMLFormatException, CableNotFoundException
    {
        if(!element.getName().getLocalPart().equals("CableConfigs"))
            throw new XMLStreamException("[CableConfigs] XML elements name isn't CableConfigs");

        String endingLengthString = element.getAttributeByName(new QName("endinglength")).getValue();

        if(endingLengthString == null)
            throw new XMLFormatException("[CableConfigs] endinglength don't exist");

        int endingLength = Integer.parseInt(endingLengthString);
        

        ArrayList<CableConfig> cableConfigList = new ArrayList<>();

        while(eventReader.hasNext())
        {
            XMLEvent event = eventReader.nextEvent();

            // if we've encounter an closing element
            if(event.isEndElement() && event.asEndElement().getName().getLocalPart().equals("CableConfigs"))
                break;

            // we only want to have start element events
            if(!event.isStartElement())
                continue;

            StartElement nextElement = event.asStartElement();

            if(nextElement.getName().getLocalPart().equals("CableConfig"))
                cableConfigList.add(CableConfig.parseCableConfigElement(nextElement));         
        }
    
        if(cableConfigList.size() == 0)
            throw new XMLFormatException("[CableConfigs] There must be atleast one CableConfig-Element");

        ImmutableList<CableConfig> cableConfigs = new ImmutableList<>(cableConfigList);

        validateCableTypeExist(cableConfigs);
        validateReferenceFromCanCrossCables(cableConfigs);

        return new CableConfigs(endingLength, cableConfigs);
    }
}
