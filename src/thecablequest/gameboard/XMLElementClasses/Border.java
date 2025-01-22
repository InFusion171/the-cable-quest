package thecablequest.gameboard.XMLElementClasses;

import java.util.ArrayList;
import java.util.HashSet;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import breitband.preset.ImmutableList;
import thecablequest.exceptions.XMLFormatException;
import thecablequest.helperClasses.Point;

/**
 * A Border represented as points.
 * 
 * The connected points are the border
 * 
 * @author Marius Degenhardt
 */
public class Border
{
    /**
     * The border points
     */
    private ImmutableList<Point> points;

    /**
     * Convert the points in the string to an Arraylist with the type Point
     * 
     * @param points Points in the format "x1,y1 x2,y2 ..."
     */
    public Border(String points)
    {
        this.points = toPoints(points);
    }

    /**
     * Set the border points
     * 
     * @param points Arraylist of Points 
     */
    public Border(ImmutableList<Point> points)
    {
        this.points = points;
    }

    /**
     * Convert a points string in the form of "x1,y1 x2,y2 ..." to an Arraylist of points
     * 
     * @param points String in the form of "x1,y1 x2,y2 ..."
     * @return The Arraylist with the converted points
     * @throws NumberFormatException Throws when the given points aren't integer numbers
     * @throws ArrayIndexOutOfBoundsException Throws when the numbers aren't comma seperated
     */    
    public static ImmutableList<Point> toPoints(String points) throws NumberFormatException, ArrayIndexOutOfBoundsException
    {
        ArrayList<Point> pointsList = new ArrayList<>();

        // Tuple of points in the form of X,Y
        String[] splittedPoints = points.split(" ");

        for(String point : splittedPoints)
        {
            // A list where index 0 is the x value and index 1 is the y value
            String[] pointXY = point.split(",");

            int x, y;

            try
            {
                x = Integer.parseInt(pointXY[0]);
                y = Integer.parseInt(pointXY[1]);
            }
            catch(NumberFormatException e)
            {
                e.printStackTrace();
                throw new NumberFormatException();
            }
            catch(ArrayIndexOutOfBoundsException e)
            {
                e.printStackTrace();
                throw new ArrayIndexOutOfBoundsException();
            }

            Point p = new Point(x, y);

            pointsList.add(p);
        }

        return new ImmutableList<>(pointsList);
    }

    /**
     * Parse the values for the XML Border-Element
     * 
     * @param element The Border-Element
     * @return A new Border Object with the given values
     * @throws XMLStreamException Throws when the element name are unequals "Border"
     * @throws XMLFormatException Throws when no or less than 3 borderpoints were given 
     */
    public static Border parseBorderElement(StartElement element) throws XMLStreamException, XMLFormatException 
    {
        if(!element.getName().getLocalPart().equals("Border"))
            throw new XMLStreamException("[Border] XML elements name isn't Border");

        String pointsAsString = element.getAttributeByName(new QName("points")).getValue();

        if(pointsAsString == null)
            throw new XMLFormatException("[Border] Border-Element attribute points don't exist");

        ImmutableList<Point> points = toPoints(pointsAsString);

        validatePoints(points);

        return new Border(points);
    }

    /**
     * Validates if the more than 3 unique points were given
     * 
     * @param points The given points
     * @throws XMLFormatException Throws if less than 3 unique points were given
     */
    private static void validatePoints(ImmutableList<Point> points) throws XMLFormatException
    {
        if(points.size() < 3)
            throw new XMLFormatException("[Border] Less than 3 borderpoints were given");

        HashSet<Point> hashSet = new HashSet<>(points);

        int uniquePointCount = hashSet.size();

        if(uniquePointCount < 3)
            throw new XMLFormatException("[Border] Less than 3 unique points were given");
    }

    /**
     * Get the border points
     * @return the border points as an Arraylist
     */
    public ImmutableList<Point> getBorderPoints()
    {
        return new ImmutableList<>(this.points);
    }
    /**
     * Get the border points as String
     * 
     * verbrochen von Marc-Arne
     * @return the border points as an String
     */
    public String getBorderPointsAsString()
    {
        String stringpoints = "";
        for (Point point : points){
            stringpoints = stringpoints + point.getPointAsString()+" ";
        }

        return stringpoints;
    }
}
