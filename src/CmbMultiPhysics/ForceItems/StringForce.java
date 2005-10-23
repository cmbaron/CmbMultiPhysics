/*
 * String.java
 *
 * Created on October 2, 2005, 2:40 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package CmbMultiPhysics.ForceItems;
import CmbMultiPhysics.*;


/**
 * StringForce is an example of an infinite force that is dynamically 
 * produced.  The string force, represents a string tied to a MotionItem
 * and a vertex.  The string has a length which controls whether the
 * resistive force is on or off.  Until the displacement from MotionItem
 * to vertex is => length of the string there is no resistive force.
 * The resistive force is "equalizing" because it can be infinitely strong
 * this object simply instructs the Motion Item WHEN it can equalize
 * from this Object.
 * 
 * The MotionItem will produce the force vector necessary for this 
 * ForceItem to equalize any other forces opposing it.
 * @author Christopher Baron
 */
public class StringForce implements ForceItem {
    
    /** Creates a new instance of String 
     *
     * @param vertex vertex of the string
     * @param length length of the string
     */
    public StringForce(FloatVector vertex, float length) {
        this.vertex = vertex;
        this.length = length;
    }
   
    /**
     * Get's the magnitude of the force
     *  For a string this will be -1, which indicates to a motion item that
     *  it is a "reflector", meaning it can resist any force in the direction
     *  of the string.
     * @param m a motionitem, preferrably the motionitem calling this force
     * @return Returns the magnitude of the force.  In this case it 
     * always returns -1 to indicate to the MotionItem that this
     * object is an equalizing force, or a reflector, or an infinite
     * force, etc.
     */
    public float getForceMagnitude(MotionItem m) {
        FloatVector displacement = (FloatVector) vertex.clone();
        displacement.subtract(m.getPosition());
        
        if (displacement.getMagnitude() < length) 
            return(0.0f);
        
        return (-1.0f);
    }
    
    /**
     * gets a force associated with this force item
     * 
     * For the string, the force is reflected, as is indicated by the mag function
     * This is merely a direction in which the motion item is to assume equilibrium
     * @param m MotionItem parameter calling
     * @return A vector in the direction of the equlibrium force.
     */
    public FloatVector getForce(MotionItem m) {
        FloatVector displacement = (FloatVector) vertex.clone();
        displacement.subtract(m.getPosition());
      
        if (displacement.getMagnitude() < length) 
            return(new FloatVector(0,0));
        
        //if (displacement.getMagnitude() > length) 
            //System.exit(1);
        
        return(displacement.unitVector());
    }
    
    FloatVector vertex;
    float length;
    
}
