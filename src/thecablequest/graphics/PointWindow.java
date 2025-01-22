package thecablequest.graphics;

import thecablequest.gameboard.*;
import thecablequest.gameboard.XMLElementClasses.*;
import thecablequest.helperClasses.*;
import thecablequest.player.*;
import thecablequest.Inventory;

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


public class PointWindow{
    GameBoard gameboard;
    SAGFrame mainFrame = new SAGFrame("Final Score",30,900,450);
    SAGPanel tablePanel = new SAGPanel();




    public PointWindow(List<BasicPlayer> playerList){
        
        tablePanel = new SAGPanel(900,900);
        mainFrame.setSAGPanel(tablePanel);

        GGroup tableGroup = tablePanel.addLayer(LayerPosition.TOP_LEFT);
        
        float setoff = 0.0f;
        for(BasicPlayer player : playerList){
            GRect rectangle = new GRect(0,0+setoff, 10000, 180,false);
            rectangle.setFill(Color.orange);
            rectangle.setOpacity( (setoff%360)/360 +0.1f);
            tableGroup.addChild(rectangle);
            //playername
            GText nameTag = new GText(player.getName()+"("+player.getPlayerid()+") :"+player.getScore(true));
            nameTag.setFill(new Color(0,0,0));
            nameTag.move(10,100+setoff);
            nameTag.setFontSize​(100);
            tableGroup.addChild(nameTag);






            setoff += 180.0f;

        }




        mainFrame.setVisible(true);
    }

}