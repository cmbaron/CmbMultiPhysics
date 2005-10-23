/*
 * Gravity.java
 *
 * Created on October 1, 2005, 5:51 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package CmbMultiPhysics.ForceItems;
import CmbMultiPhysics.*;

/** This is a dynamic ForceItem.  It computes the FORCE due to gravity for a
 * specific MotionItem.  The Force due to gravity is: F = mG, vectorialy.
 *
 * @author Christopher Baron
 */
public class Gravity implements ForceItem {
    
    /** Creates a new instance of Gravity 
     * @param dv Vector showing magnitude & direction of gravity
     */
    public Gravity(FloatVector dv) {
        gravity = dv;
    }
    
    /** Compute and return the force of gravity
     *
     * @param m Motion Item of action
     * @return Force Vector of gravitational force on MotionItem m
     */
    public FloatVector getForce(MotionItem m) {
        // we don't care about what m is doing, we're GRAVITY!
        
        FloatVector dv = (FloatVector) gravity.clone();
        
        return (dv.scale(m.getMass()));
    }
    
    /** Gives the magnitude of Gravity for the item.
     *
     * @return magnitude of gravity
     * @param m item from which mass is observed.
     *
     */
    public float getForceMagnitude(MotionItem m) {
        FloatVector dv = (FloatVector) gravity.clone();
        
        return (dv.scale(m.getMass()).getMagnitude());
    }
    
    FloatVector gravity;
}
