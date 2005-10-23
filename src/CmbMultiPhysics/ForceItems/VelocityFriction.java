/*
 * VelocityFriction.java
 *
 * Created on October 2, 2005, 7:33 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package CmbMultiPhysics.ForceItems;
import CmbMultiPhysics.*;

/** This class will create a friction model that depends on the velocity.
 * It'll be a little inaccurate because of the stupidity of the model, but
 * it'll work.
 *
 * @author Christopher Baron
 */
public class VelocityFriction implements ForceItem {
    
    /** Creates a new instance of VelocityFriction 
     * @param coeff The coefficient of friction (0 <= coeff <= 1)
     */
    public VelocityFriction(float coeff) {
        this.coeff = coeff;
    }
    
    /** Sets the coefficient of friction
     *
     * @param coeff a value (0 <= coeff <= 1)
     */
    public void setCoefficient(float coeff) {
        this.coeff = coeff;
    }
    
    /** compute the force of friction
     *
     * @param m the motion item that's MOVING
     * @return a vector, opposite the direction of the velocity vector and
     * scaled by the coefficient of friction
     */
    public FloatVector getForce(MotionItem m) {
        FloatVector dv = (FloatVector) m.getVelocity().clone();
        
        dv.scale(coeff*(-1.0f));
        
        return(dv);
    }
    
    /** returns a bogus force magnitude to fool the reflector
     *
     * @param m the item that is moving
     * @return always 1.
     *
     */
    public float getForceMagnitude(MotionItem m) {
        return (1);
    }
    
    float coeff;
}
