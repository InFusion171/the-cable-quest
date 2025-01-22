package thecablequest.helperClasses;

/**
 * A Point 
 * 
 * @author Marius Degenhardt
 */
public class Point
{
    private double x, y;
    
    /**
     * Set X, Y values
     * 
     * @param x X point value
     * @param y Y point value
     */
    public Point(double x, double y)
    {
        this.x = x;
        this.y = y;
    }


    /**
     * Check if the current Point lies on the line from A to B
     * 
     * @param A Startpoint of the line
     * @param B Endpoint of the line
     * @return True if the current point is on the line, False otherwise
     */
    public boolean isOnLine(Point A, Point B)
    {
        // calculate the vector from A to B
        Point AB = new Point(B.x - A.x, B.y - A.y);

        // calculate the vector from A to the current point
        Point AP = new Point(this.x - A.x, this.y - A.y);
         
        double crossProduct = AB.x * AP.y - AB.y * AP.x;
        
        // check if the crossProduct isn't collinear
        if (crossProduct != 0) 
            return false;
        
         
        double dotProduct = AP.x * AB.x + AP.y * AB.y;
        if (dotProduct < 0) 
            return false;
        
        
        double squaredLengthAB = AB.x * AB.x + AB.y * AB.y;
        if (dotProduct > squaredLengthAB) 
            return false;
        
        
        return true;
    }

    /**
     * Override euquals method so we can compare Points
     * 
     * @param o The point object
     * @return True if it's the same object or this.x == o.x && this.y == o.y 
     */
    @Override
    public boolean equals(Object o)
    {
        if(o == this)
            return true;

        if(!(o instanceof Point))
            return false;

        Point p = (Point)o;

        return this.x == p.x && this.y == p.y;
    }

    public String getPointAsString(){
        String xString = String.valueOf(x);
        String yString = String.valueOf(y);
        return xString+","+yString;
    }

    /**
     * get X
     * 
     * @return x
     */
    public double getX()
    {
        return this.x;
    }

    /**
     * get Y
     * 
     * @return y
     */
    public double getY()
    {
        return this.y;
    }
}