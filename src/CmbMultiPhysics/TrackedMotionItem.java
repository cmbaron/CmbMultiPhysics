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
import CmbMultiPhysics.Collisions.*;


/**
 * The TrackedMotionItem extends MotionItem and implements Trackable.
 * It registers itself with the PhysicsTracker in the constructor, and registers
 * ticks with the PhysicsTracker upon each tickForward.
 *
 * @author Administrator
 */
public class TrackedMotionItem extends MotionItem implements PhysicsTrackable,ComplexCollisionItem {
    
    PhysicsTracker2 pt;
    
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
        //pt.registerTick(this);
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
    
    
    /** This defines the position-collision behavior of this object.
     *
     *
     */
    public synchronized void correctPosition(ComplexCollisionItem c) {
        
        // these have to be about the least optimized routines ever
        FloatVector dist2Center = ComplexCollider.dist2Center(getShape(), c.getShape());
        
        // these are points.
        FloatVector ourCenter = ComplexCollider.getCenterPoint(getShape());
        
        Rectangle2D intersection = getShape().getBounds2D().createIntersection(c.getShape().getBounds());
        
        // dimensions of the intersection box
        FloatVector intersectionDimension = new FloatVector((float)intersection.getWidth(), (float)intersection.getHeight());
        
        // unit vector opposite distance to center
        // this is essentially a direction vector pointing the other way (so
        // we know where to move
        FloatVector d2cuv = dist2Center.unitVector().scale(-1f*intersectionDimension.getMagnitude()/2);
        
        // get a new point to where we want to go
        FloatVector intendedPosition = ((FloatVector)d2cuv.clone()).add(ourCenter);
        
        // tell our colliding body what we want to do about our overlap
        setPosition((FloatVector)c.correctPositionAbout(this, (FloatVector)intendedPosition.clone()));
        
    }
    
    /**  find out where we've been told the other party is willing to move, and move there
     *  this object must be willing to get out of the way because it needs to not stick
     *  to unmovable colliders.
     *
     */
    public synchronized FloatVector correctPositionAbout(ComplexCollisionItem c, FloatVector intention) {
        Rectangle2D theirPosition = c.getShape().getBounds2D();
        Rectangle2D theirIntendedShape = (Rectangle2D) new RectanglePoint(intention, (float)theirPosition.getWidth(), (float)theirPosition.getHeight());
        FloatVector dist2Center = ComplexCollider.dist2Center(getShape(), theirIntendedShape);
        Rectangle2D intersection = getShape().getBounds2D().createIntersection(theirIntendedShape);
        
        // these are points.
        FloatVector ourCenter = ComplexCollider.getCenterPoint(getShape());
        
        // dimensions of the intersection box
        FloatVector intersectionDimension = new FloatVector((float)intersection.getWidth(), (float)intersection.getHeight());
        
        // unit vector opposite distance to center
        // this is essentially a direction vector pointing the other way (so
        // we know where to move
        FloatVector d2cuv = dist2Center.unitVector().scale(-1f*intersectionDimension.getMagnitude()/2);
        
        
        setPosition(d2cuv.add(ourCenter));
        
        // tell them they can go where they want
        return((FloatVector)intention.clone());
    }
    
}
