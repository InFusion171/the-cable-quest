package thecablequest;

import java.util.*;

import javax.xml.stream.XMLStreamException;

import java.io.*;
import breitband.preset.*;
import thecablequest.gameboard.*;
import thecablequest.gameboard.XMLElementClasses.*;
import thecablequest.graphics.*;
import thecablequest.helperClasses.*;
import thecablequest.player.*;
import thecablequest.exceptions.*;

/** 
 * This starts the game and initialises the game settings
 * 
 * @author Marc-Arne
 */
public class GameStarter{

    
    /** 
     * Defines the project info for the ArgumentParser and creates a new ArgumentParser
     * Creates the GameConfiguration 
     * Creates Graphics
     * Creates Players
     * initilises Players
     * 
     * runs the actual gameturns
     * 
     * runs the last turn
     * 
     * verifys scores
     * 
     * runs Turns @see doSingleTurn()
     * @throws XMLStreamException 
     * @throws FileNotFoundException 
     * 
     * 
     */
    public static void main( String[] args ) throws FileNotFoundException, XMLStreamException {
        //project info
        final String projectName = "The Cable Quest";
        String version = "Alpha 1.0";
        final List<String> authorNames = new ArrayList<>(Arrays.asList("Marius Degenhardt", "Batoul Mhawesh",  "Marc-Arne Schlegel"));
        
        /**
         * List of (Basic) Players
         */
        List<BasicPlayer> listOfPlayers = new ArrayList();
            
        //Argument parsing
        /**
         * the parsed arguments from the ArgumentParser, parses on start
         */
        ArgumentParser parsedArguments = new ArgumentParser(args,projectName,version,authorNames,null,true); // Argument parser: see Readme for more info

        //Put the Players and their Types in a Seperate list, in case I want to implement random draw
        List<String> playerNames = parsedArguments.playerNames;
        List<PlayerType> playerTypes = parsedArguments.playerTypes; 
         //Check if the input config is a file otherwise crash
        try{
            if (!parsedArguments.gameConfigurationFile.isFile()) {
                throw new FileNotFoundException("The specified game configuration file does not exist or is not a file: " + parsedArguments.gameConfigurationFile.toString());
            }
        }catch(FileNotFoundException e){
            System.exit(1);
        }
        
        //loads the xml file via a fileReader that gets the parsed File-Path as a string
        try{
            Reader parsedReader = new FileReader(parsedArguments.gameConfigurationFile.toString() );

            //TODO draw the available contracts out of the contracts in the gameConfigurationXML
            // setup the xml file and the GameConfiguration for everyone
            GameConfiguration.setXMLFile(parsedReader);
            //instance of initialised config
            GameConfiguration config = GameConfiguration.getInstance();
            //do Graphics
            SAGGUI graphicsPlatform = new SAGGUI(config.getContractPool().getAllContractIds(),playerTypes.size());
            //TODO : add ability for graphics not to be rendered if no Human is there and it is not asked for
            try{
                // new x players of x Type
                for (int i = 0;  i < playerNames.size() && i < playerTypes.size(); i++){
                    
                    if(createPlayer(playerNames.get(i),playerTypes.get(i),graphicsPlatform)!= null){
                        listOfPlayers.add(createPlayer(playerNames.get(i),playerTypes.get(i),graphicsPlatform) );
                        
                        graphicsPlatform.drawPlayerInventoryBox(listOfPlayers.get(i),playerTypes.get(i).toString(),parsedArguments.playerColors.get(i),i+1);
                    }
                }
            }catch(Exception e){
                System.out.println("Well, Something in the player creation from GameStarter went wrong: " + e);
                System.exit(1);
            }
            try{

                ImmutableList<String> allContracts = config.getContractPool().getAllContractIds();

                int subsetSize = config.getContractPool().getSubsetSize();

                ImmutableList<String> availableContracts = getRandomSubset(allContracts, subsetSize);           
                //initialise all players:
                for (int i= 0; i<listOfPlayers.size();i++){

                                    //init(Reader gameConfigurationXML, ImmutableList<String> availableContracts,int numPlayers,int playerid)
                    listOfPlayers.get(i).init(parsedReader,availableContracts,listOfPlayers.size(),i+1); 

                }
            }catch(Exception e){
                System.out.println("Well, Something in the player initilisation from GameStarter went wrong: " + e);
                e.printStackTrace();
                System.exit(1);
            }


            int playerIdOfFinshingPlayer=23;
            //the actual turns in the actual game with a maximum of turns The 100th turn of a the player
            for(int turn=1; turn<=(100*listOfPlayers.size());turn++){
                int i = turn % (listOfPlayers.size());
                //does a single turn
                    
                if(listOfPlayers.get(i)!= null){
                    Thread.sleep(parsedArguments.delay);
                    doSingleTurn(i+1,listOfPlayers,graphicsPlatform);       
                }
                if(listOfPlayers.get(i).didMyLastTurn()){
                    playerIdOfFinshingPlayer = i;
                    break;
                }

                
                
            }
            //final turn
            if (playerIdOfFinshingPlayer != 23){
                int i = (playerIdOfFinshingPlayer+1)%listOfPlayers.size();
                do{
                    if(listOfPlayers.get(i)!= null){
                        Thread.sleep(parsedArguments.delay);
                        doSingleTurn(i+1,listOfPlayers,graphicsPlatform);       
                    }
                    i = (i+1)%listOfPlayers.size();

                }while((i%listOfPlayers.size()) != playerIdOfFinshingPlayer);
            }
            
            ArrayList<Integer> scoreList = new ArrayList<>();
            //get and verify scores
            for(int i = 0; i<listOfPlayers.size(); i++){
                scoreList.add(listOfPlayers.get(i).getScore(true));
            }
            ImmutableList<Integer> immutableScoreList = new ImmutableList(scoreList);
            for(BasicPlayer player : listOfPlayers){
                player.verifyGame(immutableScoreList);
            }


             PointWindow finalScoreWindow = new PointWindow(listOfPlayers);

            
            

        }catch(Exception e){
            System.out.println("Well, Something in the main from GameStarter went wrong: " + e);
            e.printStackTrace();
            System.exit(1);
        }
        
        
        
        

        

      }
    
    /**
     * creates BasicPlayer based on PlayerType
     * Throws error if unknown playertype is given
     * 
     * @return BasicPlayer
     */
    private static BasicPlayer createPlayer(String name, PlayerType type, PlayerGUIAccess guiAccess){
            if(type.toString().equals("HUMAN")){
                return new Human(name,guiAccess);
            }
            
            if(type.toString().equals("RANDOM_AI")){
                return new RandomAI(name);
            }
            if(type.toString().equals("CHEATING_AI")){
                return new CheatingAI(name);
            }
                
            
            //if no playertype matches:
            System.out.println(type.toString()+" is not a valid Playertype, these are the options:\n HUMAN,\n RANDOM_AI, \n CHEATING_AI");
            System.exit(1);
            return null;
            
        }
    /**
     * Does Turn by requesting the Player to do a Move and giving that move to every other player and the SAGGUI
     */
    private static void doSingleTurn(int currentPlayersID, List<BasicPlayer> listOfPlayers,SAGGUI graphicsPlatform){
        try{

            Move playermove = listOfPlayers.get(currentPlayersID-1).request();

            for(int i=0; i<listOfPlayers.size() ; i++){
                if(i!=(currentPlayersID-1)){
                    listOfPlayers.get(i).update(playermove);
                }
            }
            
            graphicsPlatform.update(playermove,currentPlayersID-1);

            if(playermove == null)
                return;

            
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("Error in the Single turn : "+e);
            System.exit(1);
        }

    }

    /**
     * Creates a Random subset of a Immutable String List
     */
    private static ImmutableList<String> getRandomSubset(ImmutableList<String> allContracts, int subsetSize) {
        //verbrochen von Batoul
        // Convert the immutable list to a mutable list
        List<String> mutableList = new ArrayList<>();
        mutableList.addAll(allContracts);
        
        // Shuffle the list manually using Fisher-Yates algorithm
        Random random = new Random();
        for (int i = mutableList.size() - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            // Swap elements i and j
            String temp = mutableList.get(i);
            mutableList.set(i, mutableList.get(j));
            mutableList.set(j, temp);
        }
        
        // Select the first 'subsetSize' elements manually
        List<String> subset = new ArrayList<>();
        for (int i = 0; i < Math.min(subsetSize, mutableList.size()); i++) {
            subset.add(mutableList.get(i));
        }

        
        
        return new ImmutableList(subset);
    }




}
