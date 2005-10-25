/*
 * EngineForce.java
 *
 * Created on October 24, 2005, 7:17 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package CmbMultiPhysics.ForceItems;

import CmbMultiPhysics.*;

/**
 *
 * @author Administrator
 */
public class EngineForce implements ForceItem {
    
    // max accelleration
    float pointPerSecondPerSecond = 10;
    
    /** Creates a new instance of EngineForce */
    public EngineForce() {
    }
    
    public FloatVector getForce(MotionItem m) {
        //System.out.println("hello?");
        if (Drivable.class.isInstance(m)) {
            Drivable d = (Drivable) m;
            
            ///FloatVector f = d.getDirection();
            ///System.out.println("BULLSHIT1: " + f.toString());
            ///f.scale(((100-(100-d.getAcceleration()))/100)*pointPerSecondPerSecond);
            ///System.out.println("BULLSHIT2: " + f.toString());
            
            ///System.out.println("got told to move: "+ Float.toString(d.getAcceleration()/100*pointPerSecondPerSecond));
            ///System.out.println(((FloatVector)(d.getDirection().scale((float)d.getAcceleration()/100*pointPerSecondPerSecond))).toString());
            if (d.getAcceleration() > 0)
                return (d.getDirection().scale((float)d.getAcceleration()/100*pointPerSecondPerSecond));
        }
        return(new FloatVector());
    }
    
    public float getForceMagnitude(MotionItem m) {
        return (getForce(m).getMagnitude());
    }
    
}
