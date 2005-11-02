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
public class FloatVector implements Cloneable, java.io.Serializable {
    
    float x;
    float y;
    float z;
    
    private final static FloatVector XVECTOR = new FloatVector(1,0);
    private final static FloatVector YVECTOR = new FloatVector(0,1);
    private final static FloatVector ZVECTOR = new FloatVector(0,0,1);
    
    /** Creates a new instance of FloatVector */
    public FloatVector() {
        x = 0f;
        y = 0f;
        z = 0f;
    }
    
    /** constructor with parameters
     * @param x x acceleration
     * @param y y acceleration
     */    
    public FloatVector(final float x, final float y)
    {
        this.x = x;
        this.y = y;
    }
    
    public FloatVector(final float x, final float y, final float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public static FloatVector getNorth() {
        return ((FloatVector)YVECTOR.clone());
    }
    
    public static FloatVector getWest() {
        return ((FloatVector) XVECTOR.clone());
    }
    
    public static FloatVector getUp() {
        return ((FloatVector) ZVECTOR.clone());
    }
    
    /** sees if two doublevectors are equal
     * @return is it equal?
     * @param o object to compare
     */    
    public boolean equals(final Object o)
    {
        FloatVector dv = (FloatVector)o;
        if ((dv.x == x) && (dv.y == y) && (dv.z == z))
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
        return new String("DoubleVector=[" + x + "," + y + "," + z + "]");
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
    public FloatVector add(final FloatVector dv)
    {
        // setX(dv.getX() + getX());
        //setY(dv.getY() + getY());
        x = dv.x + x;
        y = dv.y + y;
        z = dv.z + z;
        // totally ok
        return this;
    }
    
    /**
     * @param dv
     * @return
     */    
    public FloatVector subtract(final FloatVector dv)
    {
        // setX(getX() - dv.getX());
        //setY(getY() - dv.getY());
        x = x - dv.x;
        y = y - dv.y;
        z = z - dv.z;
        // totally ok
        return this;
    }
    
    /** produces an absolute magnitude for this vector.
     *  
     *  @return scalar value of the magnitude
     *
     */
    public float getMagnitude() {
        float mag = (float)Math.sqrt(x*x+y*y+z*z);
        if (Float.isNaN(mag)) {
            return(0f);
        }
        return (mag);
    }
    
     public FloatVector invertY()
    {
        this.y *= -1;
        return this;
     }
    
    public FloatVector invertX()
    {
        this.x *= -1;
        return this;
    }
    
    public FloatVector invertZ() {
        this.z *= -1;
        return this;
    }
    
    /** scales a vector
     *
     * @param scalar scalar to multiply against vector
     */
    public FloatVector scale(final float scalar) {
        x = x*scalar;
        y = y*scalar;
        z = z*scalar;
        
        return(this);
    }
    
    /** this gives a unit vector in the direction of this vector,
     * that is the magnitude of this returned vector is 1
     *
     * @return a unit vector that represents the direction
     */
    public FloatVector unitVector() {
        FloatVector dv = (FloatVector) this.clone();
        
        /*
         *
         */
        dv.scale(1/dv.getMagnitude());
        
        return(dv);
    }
    
    /**
     * @return
     */    
    public final FloatVector abs()
    {
        x = Math.abs(x);
        y = Math.abs(y);
        z = Math.abs(z);
        // setX(Math.abs(getX()));
        // setY(Math.abs(getY()));
        
        // totally ok
        return this;
    }
    
    /**
     * @return
     */    
    public Object clone() {
        FloatVector dv = new FloatVector(x, y, z);
        return dv;
    }
    
    /**
     * @param d
     * @return
     * 
     */    
    public final boolean within(final float d)
    {
        
        if (getMagnitude() < d)
        {
            return true;
        }
        return false;
    }
    
    /**
     * @return
     */    
    public final FloatVector  invertVector() {
       
       // setX(getX() * -1);
       // setY(getY() * -1);
       x = x * -1;
       y = y * -1;
       z = y * -1;
       return this;
    }
    
    /**  Projection of a vector U onto this Vector
     *
     * @param U vector to project onto this vector
     * @return a new vector of the projection of U on this
     *   
     */
    public FloatVector project(FloatVector U) {
        FloatVector v = (FloatVector) this.clone();
        
        
        v.scale(v.dotProduct(U)/(v.getMagnitude()*v.getMagnitude()));
        
        return(v);
    }
    
    /** Finds an angle between this vector and another vector
     * @param U a vector to find the angle between
     * @return the angle between this vector and the vector given in degrees
     * @bugs this is NOT NOT NOT 3D safe
     */
    public float getAngle(final FloatVector U) {
        float angle = (float)Math.toDegrees((float)Math.acos((this.dotProduct(U)/(this.getMagnitude()*U.getMagnitude()))));
        // use a determinant to find the direction of angle
        if ((this.getX() * U.getY() - this.getY() * U.getX()) < 0) {
            return -1*angle;
        } else {
            return angle;
        }
    }
    
    /** Creates a new Vector the specified number of degrees 
     *
     * @param angle angle in degrees
     * @bugs this is NOT NOT NOT 3D safe
     *
     */
    public FloatVector getRotation(float angle) {
        
        angle = (float) Math.toRadians(angle);
        
        // theorm Angle Between Two Vectors
        // magnitude of orthogonal vector = tan (angle * pi/180) / magnitude of this
        FloatVector orthogonal = getOrthogonal();
        orthogonal = orthogonal.unitVector();
        orthogonal = orthogonal.scale((float)Math.tan( angle ) * getMagnitude());

        // orthogonal vector of the magnitude of this perpendicular displacement of a vector at the angle
        // 
        FloatVector newVector = orthogonal.add(this);
        newVector = newVector.unitVector().scale(getMagnitude());
        
        return(newVector);
        
    }
    
    /**
     * @bugs this is not 3D safe
     *
     */
    public FloatVector getOrthogonal() {
        // returns an orthogonal vector
        //   V = < x, y >
        //   Vorthog = < -y, x >
        return(new FloatVector( -1f * this.getY(), this.getX()));
    }
    
    
    /** Performs a dot product operation on this vector with another vector
     *
     *  @param U the vector to dot.
     *  @return a scalar dot product
     */
    public float dotProduct(final FloatVector U) {
        float dotP = 0;
        dotP += this.getX() * U.getX();
        dotP += this.getY() * U.getY();
        dotP += this.getZ() * U.getZ();
        
        return (dotP);
    }
    
    /**
     * @return
     */    
    public final FloatVector reverse()
    {
        return new FloatVector(this.x * - 1, this.y * - 1, this.z * - 1);
        
    }

    /**
     * Getter for property z.
     * @return Value of property z.
     */
    public float getZ() {

        return this.z;
    }

    /**
     * Setter for property z.
     * @param z New value of property z.
     */
    public void setZ(float z) {

        this.z = z;
    }
}
