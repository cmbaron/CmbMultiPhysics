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
import java.awt.geom.Point2D;
//import CmbMultiPhysics.Track.Trackable;
import CmbMultiPhysics.TickForwardable;

/**
 * The TrackedMotionItem extends MotionItem and implements Trackable. 
 * It registers itself with the PhysicsTracker in the constructor, and registers 
 * ticks with the PhysicsTracker upon each tickForward.
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
    /**
     * Ticks forward and phones the tracker with its new position
     *
     * @param deltaT time difference over which numerical integration will occur
     */
    public void tickForward(float deltaT) {
        super.tickForward(deltaT);
        // tell our master that we've done something
        pt.registerTick(this);
    }
    
    /**
     * Overridden constructor to allow initial position/mass
     *
     * @param position starting position of this object
     * @param mass starting mass of this object
     */
    public TrackedMotionItem(FloatVector position, float mass) {
        super(position, mass);
        pt = pt.getInstance();
        pt.registerItem(this);
    }
    
    
    /**
     * Makes a fake-me-out shape to be used for object detection.  This really
     * should be computed from a static (or actually physically rotationg) 
     * Polygon2D.  The Polygon2D should be transposed onto the current position
     * before getting passed.  Perhaps that should be getPositionShape, but
     * at this point its P.O.C.
     *
     * @returns shape at coordinate of object
     * 
     */
    public Shape getShape() {


        return(new RectanglePoint(getPosition(), 2));
    
    }
    
}
