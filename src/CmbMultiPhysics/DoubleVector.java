/*
 * FloatVector.java
 *
 * Created on January 5, 2003, 6:17 PM
 */

package CmbMultiPhysics;

/** these are representing the x acceleration of the player and the y acceleration
 * of the player
 * @author josh
 */
public class DoubleVector implements Cloneable, java.io.Serializable {
    
    float x;
    float y;
    
   
    /** Creates a new instance of FloatVector */
    public DoubleVector() {
    }
    
    /** constructor with parameters
     * @param x x acceleration
     * @param y y acceleration
     */    
    public DoubleVector(final float x, final float y)
    {
        this.x = x;
        this.y = y;
    }
    
    /** sees if two doublevectors are equal
     * @return is it equal?
     * @param o object to compare
     */    
    public boolean equals(final Object o)
    {
        DoubleVector dv = (DoubleVector)o;
        if ((dv.x == x) && (dv.y == y))
        {
            return true;
        }
        return false;
    }
    
    /** Getter for property x.
     * @return Value of property x.
     *
     */
    public float getX() {
        return x;
    }
    
    /** Setter for property x.
     * @param x New value of property x.
     *
     */
    public void setX(final float x) {
        this.x = x;
    }
    
    /** Getter for property y.
     * @return Value of property y.
     *
     */
    public float getY() {
        return y;
    }
    
    /** Setter for property y.
     * @param y New value of property y.
     *
     */
    public void setY(final float y) {
        this.y = y;
    }
    
    /** string representation of the vector
     * @return the resulting string
     */    
    public String toString()
    {
        return new String("DoubleVector=[" + x + "," + y + "]");
    }
    
    public String toNiceString() 
    {
        return new String(x + "," + y);
    }
    
    /** adds a doublevector to this one and returns a new vector which is the result of
     * said operation
     * @param dv the doublevector being added
     * @return the result
     */    
    public DoubleVector add(final DoubleVector dv)
    {
        // setX(dv.getX() + getX());
        //setY(dv.getY() + getY());
        x = dv.x + x;
        y = dv.y + y;
        // totally ok
        return this;
    }
    
    /**
     * @param dv
     * @return
     */    
    public DoubleVector subtract(final DoubleVector dv)
    {
        // setX(getX() - dv.getX());
        //setY(getY() - dv.getY());
        x = x - dv.x;
        y = y - dv.y;
        // totally ok
        return this;
    }
    
    /** produces an absolute magnitude for this vector.
     *  
     *  @return scalar value of the magnitude
     *
     */
    public float getMagnitude() {
        return ((float)Math.sqrt(x*x+y*y));
    }
    
     public DoubleVector invertY()
    {
        this.y *= -1;
        return this;
     }
    
    public DoubleVector invertX()
    {
        this.x *= -1;
        return this;
    }
    
    /** scales a vector
     *
     * @param scalar scalar to multiply against vector
     */
    public DoubleVector scale(final float scalar) {
        x = x*scalar;
        y = y*scalar;
        
        return(this);
    }
    
    /** this gives a unit vector in the direction of this vector,
     * that is the magnitude of this returned vector is 1
     *
     * @return a unit vector that represents the direction
     */
    public DoubleVector unitVector() {
        DoubleVector dv = (DoubleVector) this.clone();
        
        /*
         *
         */
        dv.scale(1/dv.getMagnitude());
        
        return(dv);
    }
    
    /**
     * @return
     */    
    public final DoubleVector abs()
    {
        x = Math.abs(x);
        y = Math.abs(y);
        // setX(Math.abs(getX()));
        // setY(Math.abs(getY()));
        
        // totally ok
        return this;
    }
    
    /**
     * @return
     */    
    public Object clone() {
        DoubleVector dv = new DoubleVector(x, y);
        return dv;
    }
    
    /**
     * @param d
     * @return
     */    
    public final boolean within(final float d)
    {
        if (x < d && y < d)
        {
            return true;
        }
        return false;
    }
    
    /**
     * @return
     */    
    public final DoubleVector  invertVector() {
       
       // setX(getX() * -1);
       // setY(getY() * -1);
       x = x * -1;
       y = y * -1;
       return this;
    }
    
    /**  Projection of a vector U onto this Vector
     *
     * @param U vector to project onto this vector
     * @return a new vector of the projection of U on this
     *   
     */
    public DoubleVector project(DoubleVector U) {
        DoubleVector v = (DoubleVector) this.clone();
        
        
        v.scale(v.dotProduct(U)/(v.getMagnitude()*v.getMagnitude()));
        
        return(v);
    }
    
    /** Finds an angle between this vector and another vector
     * @param U a vector to find the angle between
     * @return the angle between this vector and the vector given
     */
    public float getAngle(final DoubleVector U) {
        return((float)Math.acos((this.dotProduct(U)/(this.getMagnitude()*U.getMagnitude()))));
    }
    
    /** Performs a dot product operation on this vector with another vector
     *
     *  @param U the vector to dot.
     *  @return a scalar dot product
     */
    public float dotProduct(final DoubleVector U) {
        float dotP = 0;
        dotP += this.getX() * U.getX();
        dotP += this.getY() * U.getY();
        
        return (dotP);
    }
    
    /**
     * @return
     */    
    public final DoubleVector reverse()
    {
        return new DoubleVector(this.x * - 1, this.y * - 1);
        
    }
}
