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
import java.awt.geom.Area;
import java.awt.Polygon;
//import CmbMultiPhysics.Track.Trackable;
import CmbMultiPhysics.TickForwardable;
import CmbMultiPhysics.Collisions.*;
import java.awt.geom.AffineTransform;
import CmbMultiPhysics.Track.*;

/**
 * The TrackedMotionItem extends MotionItem and implements Trackable.
 * It registers itself with the PhysicsTracker in the constructor, and registers
 * ticks with the PhysicsTracker upon each tickForward.
 *
 * @author Administrator
 */
public class TrackedMotionItem extends MotionItem implements PhysicsTrackable,ComplexCollisionItem,SyncTickable {
    
    PhysicsTracker2 pt;
    Shape currentShape;
    Shape baseShape;
    Rectangle2D baseBounds;
    Rectangle2D currentBounds;
    boolean alive = false;
    boolean collided = true;
    boolean corrected = true;
    int ticknumber;
    
    /** Creates a new instance of TrackedMotionItem */
    public TrackedMotionItem() {
        super();
        initTMI();
    }
    
    private void initTMI() {
        
        Polygon p = new Polygon();
        //Rectangle2D p = new RectanglePoint(getPosition(), 5);
        
        // make a house.
        p.addPoint(0,5);
        //p.addPoint(-5,0);
        p.addPoint(-5,-5);
        p.addPoint(5,-5);
        //p.addPoint(5,0);
        ticknumber = -1;
        setBaseShape(p);
        setShape(getBaseShape());
        // we need a clone of baseBounds.
        currentBounds = baseBounds.getBounds2D();
        pt = pt.getInstance();
        pt.registerItem(this);
        alive = true;
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
        if (alive)
            super.tickForward(deltaT);
        
        //System.out.println(this + "ticking");
        
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
        initTMI();
        pt = pt.getInstance();
        pt.registerItem(this);
    }
    
    public void setBaseShape(Shape inShape) {
        baseShape = inShape;
        baseBounds = baseShape.getBounds2D();
    }
    
    public Shape getBaseShape() {
        //AffineTransform af = new AffineTransform();
        //return(af.createTransformedShape(baseShape));
        return(baseShape);
    }
    
    public void clearCollisions() {
        collided = false;
        corrected = false;
    }
    
    public void syncTick(int n) {
        
        if (n == ticknumber)
            return;
        
        ticknumber = n;
        
        
        //System.out.println("computing shape");
        //setShape(computeShape());
        //setShape(computeShape());
        if (alive) {
            computeBounds();
            //setShape(computeShape());
        }
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
        
        Shape shape = getBaseShape();
        
        float angle;
        
        FloatVector x = FloatVector.getNorth();
        if (getVelocity().getMagnitude() > 0) {
            angle = x.getAngle(getVelocity());
        } else {
            angle = x.getAngle(FloatVector.getNorth());
        }
        
        AffineTransform af = new AffineTransform();
        
        af.scale(0.5,0.5);
        
        
        af.rotate(Math.toRadians(angle));
        
        shape = af.createTransformedShape(shape);
        
        af.setToTranslation(getPosition().getX(), getPosition().getY());
        
        shape = af.createTransformedShape(shape);
        
        //System.out.println(shape.getBounds2D());
        
        //System.out.println(getPosition().toString());
        
        
        //return (new RectanglePoint(getPosition(), 5));
        return(shape);
        
        
    }
    
    public Rectangle2D computeBounds() {
        
        float x;
        float y;
        //synchronized (position) {
            x = position.getX();
            y = position.getY();
        //}
        final float h = (float)baseBounds.getHeight()/2;
        final float w = (float)baseBounds.getWidth()/2;
        
        currentBounds.setRect(x - w, y - h, w, h);
        
        return(currentBounds);
        
    }
    
    public Rectangle2D getBounds() {
        return currentBounds;
    }
    
    public Rectangle2D getBaseBounds() {
        return baseBounds;
    }
    
    public void setBaseBounds(Rectangle2D r) {
        baseBounds = r;
    }
    
    public Shape getShape() {
        //AffineTransform af = new AffineTransform();
        //return ((Shape)af.createTransformedShape(currentShape));
        
        // do this here, since we don't tick to do it anywhere else.
        setShape(computeShape());
        
        return(currentShape);
    }
    
    private void setShape(Shape s) {
        currentShape = s;
    }
    
    public void setPosition(FloatVector p) {
        super.setPosition(p);
        //computeShape();
    }
    
    private FloatVector positionCorrection(final Shape theirShape, float divisor) {
        
        
        final Shape ourShape = getShape();
        
        FloatVector dist2Center = ComplexCollider.dist2Center(ourShape, theirShape);
        // these are points.
        FloatVector ourCenter = ComplexCollider.getCenterPoint(ourShape);
        
        // rectangle of intersection
        //Rectangle2D intersection = ourShape.getBounds2D().createIntersection(theirShape.getBounds());
        
        // enhancement of above
        // this version uses an area to get our intersection, instead of intersecting stupid
        // rectangular projection around our shape
        Area ourArea = new Area(ourShape);
        Area theirArea = new Area(theirShape);
        
        ourArea.intersect(theirArea);
        // NO CORRECTION
        if (ourArea.isEmpty()) 
            return null;
        
        
        Rectangle2D intersection = ourArea.getBounds2D();
        
        // dimensions of the intersection
        FloatVector intersectionDimension = new FloatVector((float)intersection.getWidth() + 0.1f, (float)intersection.getHeight() + 0.1f);
        
        FloatVector ourDimension = new FloatVector((float)ourShape.getBounds2D().getWidth(), (float)ourShape.getBounds2D().getHeight());
        
        FloatVector difference = ((FloatVector)intersectionDimension.clone()).subtract(ourDimension);
        
        // check for perfect on-axis collision,
        // we don't want to shift over 4, if we don't need to.
        if (difference.getX() == 0) {
            intersectionDimension.setX(0);
        } else if (difference.getY() == 0) {
            intersectionDimension.setY(0);
        }
        
        // unit vector opposite distance to center
        // this is essentially a direction vector pointing the other way (so
        // we know where to move
        FloatVector d2cuv = dist2Center.unitVector().scale(intersectionDimension.getMagnitude()/2);
        
        // get a new point to where we want to go
        FloatVector movetoPosition = ((FloatVector)d2cuv.clone()).add(ourCenter);
        
        return (movetoPosition);
        
    }
    
    public void doCollision(SimpleCollisionItem c) {
        if (collided) 
            return;
        
        collided = true;
        
        super.doCollision(c);
    }
    
    public FloatVector getCollisionMomentum(SimpleCollisionItem c) {
       if (collided) 
           return(null);
        
        collided = true;
        
        return(super.getCollisionMomentum(c));
    }
    
    /** This defines the position-collision behavior of this object.
     *
     *
     */
    public void correctPosition(ComplexCollisionItem c) {
        
       if (!getCollidable() && corrected)
            return;
        
        corrected = true;
        
        // get a new point to where we want to go
        FloatVector intendedPosition = positionCorrection(c.getShape(), 2f);
        
        if (intendedPosition == null) 
            return;
        
        // tell our colliding body what we want to do about our overlap
        ///System.out.println("1 Going to move to: " + intendedPosition.toString());
        final FloatVector actuallyMovingTo = (FloatVector)c.correctPositionAbout(this, (FloatVector)intendedPosition.clone());
        if (actuallyMovingTo != null) 
        ///System.out.println("1 Actually moving to: " + actuallyMovingTo);
            setPosition(actuallyMovingTo);
        
        
    }
    
    /**  find out where we've been told the other party is willing to move, and move there
     *  this object must be willing to get out of the way because it needs to not stick
     *  to unmovable colliders.
     *
     */
    public FloatVector correctPositionAbout(ComplexCollisionItem c, FloatVector intention) {
        
        // once per tick!
        //if (corrected)
           // return(null);
        
        corrected = true;
        
        Rectangle2D theirPosition = c.getShape().getBounds2D();
        Rectangle2D theirIntendedShape = (Rectangle2D) new RectanglePoint(intention, (float)theirPosition.getWidth(), (float)theirPosition.getHeight());
        
        ///System.out.println("2 Other object wants to go to: " + intention.toString());
        FloatVector moveTo = positionCorrection(theirIntendedShape, 1f);
        ///System.out.println("2 We're going to: " + moveTo.toString());
        
        if (moveTo == null)
            return(null);
        
        if (getCollidable())
            setPosition(moveTo);
        
        // tell them they can go where they want
        return((FloatVector)intention.clone());
    }
    
}
