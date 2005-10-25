/*
 * BrakeForce.java
 *
 * Created on October 24, 2005, 7:48 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package CmbMultiPhysics.ForceItems;

import CmbMultiPhysics.*;
import CmbMultiPhysics.ForceItems.*;

/**
 *
 * @author Administrator
 */
public class BrakeForce implements ForceItem {
    
    boolean skid;
    
    /** Creates a new instance of BrakeForce */
    public BrakeForce() {
        skid = false;
    }
    
    public FloatVector getForce(MotionItem m) {
        if (Drivable.class.isInstance(m)) {
            Drivable d = (Drivable) m;
            
            ///System.out.println(((FloatVector)(d.getVelocity().scale((float)(100-(100-d.getBrake()))/100*-1))).toString());
            return (d.getVelocity().scale((float)(100-(100-d.getBrake()))/100*-1));
        }
        return(new FloatVector());
    }
    
    public float getForceMagnitude(MotionItem m) {
        return (getForce(m).getMagnitude());
    }
    
    public boolean isSkidding() {
        return (skid);
    }
    
    public void setSkidding(boolean b) {
        skid = b;
    }
}
