package thecablequest.helperClasses;

public class MathHelper 
{
    public static boolean intersect(Point A, Point B, Point C, Point D)
    {
        // equation AB : P(t)=A+t(B−A) where 0 <= t <= 1
        double AB_x = A.getX() - B.getX(); 
        double AB_y = B.getY() - A.getY(); 
        double t = AB_y * A.getX() + AB_x * A.getY(); 
    
        // equation CD : Q(u)=C+u(D−C) where 0 <= u <= 1
        double CD_x = C.getX() - D.getX(); 
        double CD_y = D.getY() - C.getY(); 
        double u = CD_y * C.getX() + CD_x * C.getY(); 
    
        // calculate the determinant
        double determinant = AB_y * CD_x - CD_y * AB_x;
    
        // check if the lines are parallel
        if (determinant == 0) 
            return false;
       

        double intersectionX = (CD_x * t - AB_x * u) / determinant;
        double intersectionY = (AB_y * u - CD_y * t) / determinant;

        Point intersection = new Point(intersectionX, intersectionY);

        // check if the intersection is on line AB and CD
        return intersection.isOnLine(A, B) && intersection.isOnLine(B, C);
    }
}
