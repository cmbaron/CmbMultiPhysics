/*
 * Trackable.java
 *
 * Created on October 23, 2005, 9:05 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package CmbMultiPhysics.Track;
import java.awt.Shape;
import CmbMultiPhysics.*;

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


}
