package thecablequest.graphics;

import thecablequest.gameboard.*;
import thecablequest.gameboard.XMLElementClasses.*;
import thecablequest.helperClasses.*;
import thecablequest.Inventory;
import thecablequest.player.*;

import breitband.preset.*;

import java.awt.Color; 
import java.awt.event.*; 
import javax.swing.*;
import java.lang.*;

import sag.*; 
import sag.elements.*;
import sag.elements.shapes.*;

import java.util.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;


/**
 * A basic implementation of GUI
 * It is based on:
 *  - The Map imported from the config as a GMap class
 *  - The Cities  as MapelementCity classes
 *  - A display of the respective Player-Inventories as PlayerInventoryBox classes
 * 
 * verbrochen von Marc-Arne
 */
public class SAGGUI implements PlayerGUIAccess{
    
    GameBoard gameBoard;
    SAGFrame mainFrame = new SAGFrame("The Cable Quest",30, 1600,1600);
    SAGPanel mainMap = new SAGPanel();

    ArrayList<PlayerInventoryBox> playerInventoryBoxObjects =new ArrayList<>();

    int n = 0;

    GMap testpolygon;


    ArrayList<Color> playerColorById= new ArrayList<>();

    Point framesize;

    /**
     * Sets up the size and dimensions based on the input config.
     * actually draws the GUI
     * takes in the availableContracts and the number of players
     */
    public SAGGUI(ImmutableList<String> availableContracts,int numPlayers){
        try{          

            sag.SAGFrame.enableHardwareAcceleration(true);

            gameBoard = new GameBoard(availableContracts,numPlayers);
            
            //preload Emojis
            List<String> emojisString = new ArrayList<>();
            for(CableConfig cable : gameBoard.getCableTypes().getCableConfigs()) {
                emojisString.add(cable.getEmoji().getHexcode());
            }
            GEmoji.preload(emojisString);

            //create GMap
            testpolygon = new GMap(1,gameBoard.getAllREgions());
            
            framesize = new Point(testpolygon.sizeX*1,testpolygon.sizeY*1);
            mainMap = new SAGPanel((int)(framesize.getX()),(int)(framesize.getY()));

            mainFrame.setSAGPanel(mainMap);

            GGroup mapGroup = mainMap.addLayer(LayerPosition.CENTER_CENTER);
            
            mapGroup.addChild(testpolygon);
            
            KeyListenerForMap scaleListener =new KeyListenerForMap(mapGroup,(int)(testpolygon.sizeX*1.5),(int)(testpolygon.sizeY*1.5),testpolygon);
            mainFrame.addKeyListener(scaleListener);

            mainFrame.setVisible(true);
            
        }catch(Exception e){
            System.out.println("Well, you still didn't put in a proper config, into the SAGGUI, but I found a file "+e);
            System.exit(1);
        }

       


     
    }
    /**
     * returns a standard move of taking up one "Glasfaser" cable and drawing the first contract
     */
    int i = 0;

    public Move requestMoveFromCurrentHumanPlayer(){
        //all the input stuffs

        ArrayList<Move> moves = new ArrayList<>();
        Map<String, Integer> pickedUpCables = Map.of("Glasfaser", 1);

        moves.add(new Move ("CityToCityContract-Berlin-Ulm", pickedUpCables));
        moves.add(new Move (null, pickedUpCables));
        moves.add(new Move (null, pickedUpCables, "Berlin", "Erfurt", "Glasfaser"));
        moves.add(new Move (null, pickedUpCables));
        moves.add(new Move (null, pickedUpCables));
        moves.add(new Move (null, pickedUpCables,"Erfurt","Ingolstadt", "Glasfaser"));
        moves.add(new Move (null, pickedUpCables));
        moves.add(new Move (null, pickedUpCables,"Ingolstadt","Ulm", "Glasfaser"));

        Move move = moves.get(i);
        i++;
        return move;
        //tempory move for test purposes
        //Map<String, Integer> pickedUpCables = Map.of("Glasfaser", 1);
        
        //return new Move(gameBoard.getContractPool().getAllContracts().get(0).getId(),pickedUpCables);
    }

    /**
     * Draws the PlayerInventoryBox based on the input (called once on setup)
     */
    public void drawPlayerInventoryBox(BasicPlayer player,String type,Color color,int id){
        GGroup inventoryGroup = mainMap.addAutoScaleLayer(LayerPosition.TOP_LEFT);
        playerColorById.add(color);
        playerInventoryBoxObjects.add(new PlayerInventoryBox(player,gameBoard,type, color, id,n,mainMap.VIEWPORT_WIDTH,mainMap.VIEWPORT_HEIGHT));
        inventoryGroup.addChild(playerInventoryBoxObjects.get(playerInventoryBoxObjects.size()-1));

        n += 1;

    }
    /**
     * updates its gameboard and its map, handles the arrow showing whos turn it is
     */
    public void update(Move move,int playerId){
        
        if(move == null)
            return;

        //draw the line
        try{
            gameBoard.makeMove(move);
        }catch(Exception e){
            System.out.println("gameboard.makeMove in the SAGGUI has thrown the following exception :"+e);
        }
       // 
        if(move.didBuildCable()){
            try{
                testpolygon.addGCableConnection(gameBoard.getAllREgions().findCity(move.getCableCityA()),gameBoard.getAllREgions().findCity(move.getCableCityB()),playerColorById.get(playerId),gameBoard.getCableTypes().getCableConfigByName​(move.getCableType()) );

                
                
                
                
                

            }catch(Exception e){
                System.out.println("wanted to draw the connection but failed"+e);
            }
            
            
        }

        //Set the current player active and the previous inactive
        playerInventoryBoxObjects.get(((playerId))).setActive();
        playerInventoryBoxObjects.get(((playerId-1+playerInventoryBoxObjects.size())%playerInventoryBoxObjects.size())).setInactive();
        

    }
}
/**
 * creates a Map based on the Regions Object [and the scale-value you put in]
 * contains all the infos to correctly format the Window
 */
class GMap implements Drawable{
    float scale;
    GGroup localMapGroup;
    public double sizeX=100;
    public double sizeY=100;
    public Point maxPoint = new Point(-100000,-100000);
    public Point minPoint = new Point(100000,100000);

    float globalStrokeScale=10f;
    public ArrayList<GText> alltext = new ArrayList<>();

    public Point offset;

    /**
     * Sets up all the Region Borders and creates all the ciy Mapelements based on the config
     * moves the Map into a visable space
     */
    public GMap(float scale, Regions regions){
        scale = scale;
        localMapGroup=new GGroup();
        for (Region region : regions.getRegions()){
            for (Border borderobject: region.getBorder()){
                localMapGroup.addChild(PolygonOfBorderRegion(borderobject));
            }
        }
        sizeX = Math.abs(minPoint.getX()-maxPoint.getX());
        sizeY = Math.abs(maxPoint.getY()-minPoint.getY());
        for (Region region : regions.getRegions()){
            for(City city : region.getCities()){
                    localMapGroup.addChild(GCity(city, (float)(sizeY/150)));
            }
        }
        offset = new Point((-minPoint.getX()-(sizeX/2)),(-maxPoint.getY()+(sizeY/2)));
        localMapGroup.move((float)(offset.getX()),(float)(offset.getY()));
        localMapGroup.setStroke(Color.black,globalStrokeScale);
        
    }
    public void resizeStroke(float scaler){
        globalStrokeScale *= scaler;
        for (GText text : alltext){
            text.setFontSize​(5*globalStrokeScale);
        }
        
        localMapGroup.setStrokeWidth(globalStrokeScale);
        localMapGroup.update();
    }
    public void setStroke(){
        globalStrokeScale = 10f;
        for (GText text : alltext){
            text.setFontSize​(5*globalStrokeScale);
        }
        localMapGroup.setStrokeWidth(globalStrokeScale);
        localMapGroup.update();
    }
    /**
     * returns the maximum X value the Map reaches 
     * @return double
     */
    public double getMaxX(){
        return maxPoint.getX();
    }
    /**
     * returns the maximum Y value the Map reaches 
     * @return double
     */
    public double getMaxY(){
        return maxPoint.getY();
    }
    /**
     * Returns the localMapGroup containing all elements
     * 
     */
    public GElement getGElement(){
        return localMapGroup;
    }
    /**
     * This will add a GLine to the localMapGroup that is drawn
     */
    public void addGCableConnection(City cityA, City cityB,Color color,CableConfig cable){
        GLine child = new GLine((float)(cityA.getLocation().getX()),(float)(cityA.getLocation().getY()),(float)(cityB.getLocation().getX()),(float)(cityB.getLocation().getY())); 
        child.setStroke(color);
        localMapGroup.addChild(child);
        try{
            GEmoji emoji = new GEmoji(cable.getEmoji().getHexcode());
            emoji.setPosition​(((float)(cityA.getLocation().getX())+(float)(cityB.getLocation().getX()))/2, ((float)(cityA.getLocation().getY())+(float)(cityB.getLocation().getY()))/2);
            emoji.scale(0.5f,0.5f);
            localMapGroup.addChild(emoji);
        }catch(Exception e){
            //do nothing, emojis are not that important
        }
        
    }
    /**
     * constructs a GPolygon from a Border - Object
     */
    GPolygon PolygonOfBorderRegion(Border borderobject){
        String borderpoints = "";
        borderpoints =borderpoints + borderobject.getBorderPointsAsString();
        maxPoint = maxPointValue(borderobject,maxPoint);
        minPoint = minPointValue(borderobject,minPoint);

        
        GPolygon polygon = new GPolygon(borderpoints);
        polygon.setFill("none");
        polygon.setStroke(Color.black,(float)(5));
       
        polygon.setScale((float)(1));
        return polygon;
    }

    Point maxPointValue(Border border,Point input){
        double x =input.getX();
        double y =input.getY();;
        for (Point point : border.getBorderPoints()){
            if(x<point.getX()){
                x=point.getX();
            }
            if(y<point.getY()){
                y=point.getY();
            }
        }
        return new Point(x,y) ;
    }
    Point minPointValue(Border border,Point input){
        double x = input.getX();
        double y = input.getY();;
        for (Point point : border.getBorderPoints()){
            if(x>point.getX()){
                x=point.getX();
            }
            if(y>point.getY()){
                y=point.getY();
            }
        }
        return new Point(x,y) ;
    }

    /**
     * Methode creating city
     */
    public GGroup GCity(City city,float scale){
        GGroup localGGroup = new GGroup();
        GCircle circlepoint = new GCircle(1);
        circlepoint.setFill(Color.black);
        localGGroup.addChild(circlepoint);

        GText cityname = new GText(city.getName()+" ("+city.getMaxconnections()+")");
        cityname.move(0,0);
        cityname.setFill(Color.black);
        alltext.add(cityname);
        localGGroup.addChild(cityname);

        localGGroup.setPosition​((float)(city.getLocation().getX()), (float)(city.getLocation().getY()) );
        localGGroup.setStroke(Color.black,0);
        return localGGroup;

    }
    
}


/**
 * The KeyListener for the map movement and resizing
 */
class KeyListenerForMap extends KeyAdapter {
    GGroup map;
    GMap gmap;
    int scaleX;
    int scaleY;
    public KeyListenerForMap(GGroup map, int scaleX,int scaleY,GMap actualMap){
        super();
        this.gmap = actualMap;
        this.map = map;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // This method is called when a key is pressed
        int keyCode = e.getKeyCode();
    
        if (keyCode == 521) {// +
            gmap.resizeStroke(0.833339f);
            map.scale((float)(1.2));
            map.update();
        }
        if(keyCode==45){// -
            gmap.resizeStroke(1.2f);
            map.scale((float)(0.8333339));
            map.update();
        }
        //W
        if(keyCode == 87){
            map.move(0,(float)(-scaleY/50));
            map.update();

        }
        // A
        if(keyCode == 65){
            map.move((float)(-scaleX/50),0);
            map.update();

        }
        //S
        if(keyCode == 83){
            map.move(0,(float)(scaleY/50));
            map.update();

        }
        //D
        if(keyCode == 68){
            map.move((float)(scaleX/50),0);
            map.update();

        }
        //O
        if(keyCode== 79){
            gmap.setStroke();
            map.setScale((float)(1));
            map.setPosition(0,0);
            map.update();
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {
        // This method is called when a key is released
        int keyCode = e.getKeyCode();
     
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // This method is called when a key is typed (i.e., when a character is entered)
        char character = e.getKeyChar();

    }
}

class PlayerInventoryBox implements Drawable{
    GGroup localGGroup;
    GPolygon arrow ;
    Inventory playerInventory;
    BasicPlayer player;

    //the inventory tags:
    GText contractsTag;
    HashMap<CableConfig,GText> pickedUpCablesTag = new HashMap<>();
    GText livePoints = new GText("0");
   



    public PlayerInventoryBox(BasicPlayer playerGiven,GameBoard board,String type,Color color,int id,int n,int scaleWidth,int scaleHeight){
        localGGroup = new GGroup();
        this.player = playerGiven;

        String name = player.getName();
   
        playerInventory= board.getPlayerInventory​(id);

        float scale = 0.2f;

        //pre-do arrow
        arrow = new GPolygon("0,0 10,-10 10,-5 20,-5 20,5 10,5 10,10");
        arrow.setFill(Color.red);
        arrow.move((scaleHeight/5)+(scaleHeight/150),(scaleHeight/30));
        arrow.scale(scaleWidth/300,(scaleHeight/300));
        localGGroup.addChild(arrow);
        arrow.setOpacity(0);

        //generate next Box
         GRect box = new GRect(0,0,(scaleHeight/5),(scaleHeight/15)-(scaleHeight/150),false);
        box.setStroke(color,scaleHeight/150);
        box.setFill(Color.white);
        localGGroup.addChild(box);

        //generate name and Type-Tag
        GText nameTag = new GText(name+"("+type+") "+id);
        nameTag.setFill(new Color(0,0,0));
        nameTag.move(scaleWidth/75,scaleHeight/75);
        nameTag.setFontSize​((scaleHeight/120));
        localGGroup.addChild(nameTag);

        //Add the inventory contents
        contractsTag = new GText("Contracts: "+ playerInventory.getContracts().size());
        contractsTag.setFill(Color.black);
        contractsTag.move(scaleWidth/37,scaleHeight/37);
        contractsTag.setFontSize​(scaleHeight/120);
        localGGroup.addChild(contractsTag);

        //add live-score
        livePoints.setText("Points: "+playerInventory.getPlayerPoints()+"");
        livePoints.setFill(Color.black);
        livePoints.move(scaleHeight/37,scaleHeight/20);
        livePoints.setFontSize​(scaleHeight/120);
        localGGroup.addChild(livePoints);

        try{
            //add cable information
            float setoff = 0.0f;
            for (CableConfig cable : board.getCableTypes().getCableConfigs()){
                GEmoji emoji = new GEmoji(cable.getEmoji().getHexcode());
                emoji.move(scaleWidth/8+setoff,scaleHeight/37);
                localGGroup.addChild(emoji);
                


                GText counter = new GText(playerInventory.getCables().get(cable)+"");
                counter.move(scaleWidth/8+setoff,scaleHeight/20);
                pickedUpCablesTag.put(cable,counter);
                localGGroup.addChild(counter);

                setoff += scaleHeight/50;
            }

        }catch(Exception e){
            System.out.println("There once was a GEmoji, now theres not. "+e);
        }
    
        
        
        localGGroup.scale(2,2);
        localGGroup.move(0,(n*(scaleHeight/5)));
    }

    public GElement getGElement(){
        return localGGroup;
    }
    public void setActive(){
        //update inventory information
        contractsTag.setText("Contracts: "+ playerInventory.getContracts().size());
        livePoints.setText("Points: "+playerInventory.getPlayerPoints()+"");
        
        for (CableConfig cable: playerInventory.getCables().keySet()){
            pickedUpCablesTag.get(cable).setText(playerInventory.getCables().get(cable)+"");
        }
        
        //set arrow visable
        arrow.setOpacity(1);
    }
    public void setInactive(){
        arrow.setOpacity(0);
    }
}



