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
import java.awt.geom.Area;
import java.awt.Polygon;
//import CmbMultiPhysics.Track.Trackable;
import CmbMultiPhysics.Collisions.*;
import java.awt.geom.AffineTransform;
import CmbMultiPhysics.Track.*;
import java.awt.geom.Ellipse2D;

/**
 * The TrackedMotionItem extends MotionItem and implements Trackable.
 * It registers itself with the PhysicsTracker in the constructor, and registers
 * ticks with the PhysicsTracker upon each tickForward.
 *
 * @author Administrator
 */
public class TrackedMotionItem extends MotionItem implements PhysicsTrackable,ComplexCollisionItem {
    
    PhysicsTracker2 pt;
    Shape currentShape;
    Shape baseShape;
    FloatVector preTickPosition;
    boolean alive;
    
    /** Creates a new instance of TrackedMotionItem */
    public TrackedMotionItem() {
        super();
        setPosition(new FloatVector(0f,0f));
        initTMI();
    }
    
    private void initTMI() {
        
        Polygon p = new Polygon();
        
        // make a house.
        p.addPoint(0,5);
        p.addPoint(-5,0);
        p.addPoint(-5,-5);
        p.addPoint(5,-5);
        p.addPoint(5,0);
        
        setBaseShape(p);
        setShape(computeShape());
        pt = pt.getInstance();
        pt.registerItem(this);
    }
    
    public boolean isAlive() {
        return alive;
    }
    
    public void setAlive(boolean alive) {
        this.alive = alive;
    }
    
    /**
     * Ticks forward and phones the tracker with its new position
     *
     * @param deltaT time difference over which numerical integration will occur
     */
    public void tickForward(float deltaT) {
        // save off position
        preTickPosition = getPosition();
        
        super.tickForward(deltaT);
        setShape(computeShape());
        // tell our master that we've done something
        //pt.registerTick(this);
    }

    public Shape getBaseShape() {
        return baseShape;
    }

    public void setBaseShape(Shape baseShape) {
        this.baseShape = baseShape;
    }
    
    /**
     * Overridden constructor to allow initial position/mass
     *
     * @param position starting position of this object
     * @param mass starting mass of this object
     */
    public TrackedMotionItem(FloatVector position, float mass) {
        super(position, mass);
        initTMI();
    }
    
    public void obliterate() {
        setCollidable(false);
        //pt.unregisterItem(this);
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
    public Shape computeShape() {
        
        
       
        Shape p = getBaseShape();
    
        float angle;
        
        FloatVector x = FloatVector.getNorth();
        if (getVelocity().getMagnitude() > 0) {
            angle = x.getAngle(getVelocity());
        } else {
            angle = x.getAngle(FloatVector.getNorth());
        }
        
        AffineTransform af = new AffineTransform();
        
        //af.scale(0.5,0.5);
  
        af.rotate(Math.toRadians(angle));
        
        Shape shape = af.createTransformedShape(p);
        
        af.setToTranslation(getPosition().getX(), getPosition().getY());

        shape = af.createTransformedShape(shape);
        
        //System.out.println(shape.getBounds2D());
        
        //System.out.println(getPosition().toString());
        
        //return (new RectanglePoint(getPosition(), 5));
        return(shape);
        
    }
    
    public synchronized Shape getShape() {
        return ((Shape)currentShape);
    }
    
    private void setShape(Shape s) {
        currentShape = s;
    }
    
    public void setPosition(FloatVector p) {
        super.setPosition(p);
        computeShape();
    }
    

    /** This defines the position-collision behavior of this object.
     *
     *
     */
    public synchronized void correctPosition(ComplexCollisionItem c) {
        
        if (!getCollidable())
            return;
        
        setPosition(preTickPosition);
        
    }
    

    
}
