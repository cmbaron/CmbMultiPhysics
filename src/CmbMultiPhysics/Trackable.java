/*
 * Trackable.java
 *
 * Created on October 23, 2005, 9:05 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package CmbMultiPhysics;
import java.awt.Shape;

/**
 * This interface defines the methods necessary to for the PhysicsTracker to 
 * track an object.
 *
 * @see PhysicsTracker
 *
 * @author Chris Baron
 */
public interface Trackable {
    
    /** Get a shape object representing the dimensions/position of the object
     *
     * @return a Shape object 
     */
    public Shape getShape();
    /**
     * @return positional vector
     */
    public FloatVector getPosition();
    /** Sets the position
     *
     * NOTE: likely to disappear
     *
     * @param p The new position
     */
    public void setPosition(FloatVector p);
    /** Sets the tick time, and issues a tick for the object to process.
     *
     * Implementations of this method should CALL PhysicsTracker.getInstance().registerTick(self)
     * after the tick is processed!
     *
     * @param deltaT time difference over which numerical integration will occur
     *
     */
    public void tickForward(float deltaT);
}
