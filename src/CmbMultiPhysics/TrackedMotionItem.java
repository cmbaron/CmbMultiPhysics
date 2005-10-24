/*
 * TrackedMotionItem.java
 *
 * Created on October 23, 2005, 9:58 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package CmbMultiPhysics;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author Administrator
 */
public class TrackedMotionItem extends MotionItem implements Trackable {
    
    PhysicsTracker pt;
    
    /** Creates a new instance of TrackedMotionItem */
    public TrackedMotionItem() {
        super();
        pt = pt.getInstance();
        pt.registerItem(this);
    }
    
    public void tickForward(float deltaT) {
        super.tickForward(deltaT);
        
    }
    
    public TrackedMotionItem(FloatVector position, float mass) {
        super(position, mass);
        pt = pt.getInstance();
        pt.registerItem(this);
    }
    
    
    // makes a stupid shape
    public Shape getShape() {
        return(new Rectangle2D.Float(getPosition().getX(), getPosition().getY(), 2, 2));
    }
    
}
