/*
 * MotionItem.java
 *
 * Created on October 1, 2005, 12:15 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package CmbMultiPhysics;

import java.lang.*;
import java.util.*;
import CmbMultiPhysics.Collisions.SimpleCollisionItem;

/** This is an item which will exhibit motion consistent with newtonian physics
 * Consider a MotionItem a body in a physical model with ForceItems as the forces
 * Acting on this body.
 *
 * Velocity is interpolated from the forces, including a special circumstance where
 * an infinitly large force can cancel out a velocity.  This is a magic property, but
 * it models the way a rigid object controls velocity.  It would oscillate according to
 * Hooke's law, however rather than waste cycles computing velocity at the deltaT resolution
 * necessary to not create energy, we shall simply negate the velocity in the direction opposite
 * the resistive force.
 *
 * @author Christopher Baron
 */
public class MotionItem implements PositionItem,SimpleCollisionItem {
    
    private boolean collidable;
    
    /** Creates a new instance of MotionItem */
    public MotionItem() {
        initMotionItem();
    }
    
    /** Create a new MotionItem
     *
     *  @param position the initial position of the item
     *  @param mass the mass of the item
     */
    public MotionItem(FloatVector position, float mass) {
        initMotionItem();
        this.position = position;
        this.mass = mass;
    }
    
    /** Initializes the variables of the MotionItem
     *
     */
    private void initMotionItem() {
        forceItems = new Vector();
        velocity = new FloatVector();
        position = new FloatVector();
        forceSum = new FloatVector();
        mass = 1;
        collidable = true;
    }
    
    /** add a force item to this motionitem
     *
     * @param f force item to be added
     */
    public void addItem(ForceItem f) {
        forceItems.add(f);
    }
    
    public boolean getCollidable() {
        return collidable;
    }
    
    public void setCollidable(boolean s) {
        collidable = s;
    }
    
    private void removeItem(ForceItem f) {
        forceItems.remove(f);
    }
    
    /** get the forces acting on this motionitem at this instant
     *
     *  @return vector sum of forces acting on this item.
     */
    public FloatVector getForces() {
        return forceSum;
    }
    
    /** Get the motion objects current position
     *
     * @return position vector.  you should take this as an absolute location, even though it's in a vector
     */
    public FloatVector getPosition() {
        
        return (FloatVector)position.clone();
    }
    
    /** Set the position of the MotionItem
     * NOTE: VELOCITY WILL NOT CLEAR
     *  @param position the position in which to put this MotionItem
     */
    public void setPosition(FloatVector position) {
        if (!Float.isNaN(position.getX()) && !Float.isNaN(position.getY()))
            this.position = position;
    }
    
    
    /** Clear velocity
     *
     */
    private void clearVelocity() {
        velocity = new FloatVector(0,0);
    }
    
    
    /** Get the mass of this object
     *
     * @return mass (in whatever units you want, just make sure everything is consistent)
     */
    public float getMass() {
        return mass;
    }
    
    /** Set the mass of the object
     *
     * @param mass the mass in whatever units make you happy
     */
    public void setMass(float mass) {
        this.mass = mass;
    }
    
    public void addMass(float mass) {
        this.mass += mass;
    }
    
    /** Gives the velocity of the object
     * @return Velocity as a vector
     */
    public FloatVector getVelocity() {
        return (FloatVector)velocity.clone();
    }
    
    
    
    /** Combines forces using vector projections to accumulate the
     * "sum" of the forces acting on the body.
     *
     * @param newForce the force to be added into the cumulative force system
     * @return the new cumulate force
     */
    private FloatVector forceCombination(FloatVector newForce) {
        
        // if the force 0, then get the heck out
        if (Math.abs(forceSum.getMagnitude() - 0) < 0.0001) {
            forceSum.add(newForce);
            return(forceSum);
        }
        
        // if there is no new force, again, get out.
        if (Math.abs(newForce.getMagnitude() - 0) < 0.0001) {
            return(forceSum);
        }
        
        // to get the component of the forces on the vector of the old forces
        // we take the projection of the forcesum on the newforce.
        FloatVector proj = newForce.project(forceSum);
        // to get the component of the forces orthogonal to the newForce
        // we take the newForce vector, and subtract the projection
        FloatVector orthog = (FloatVector) forceSum.clone();
        orthog.subtract(proj);
        // the resulting new force is the sum of the components
        // incidentally, this is now the forceSum
        newForce.add(proj);
        newForce.add(orthog);
        
        
        return(newForce);
    }
    
    /** This is a special case method that will equalize the force when
     * a force object has indicated that it is a reflector.  It finds the
     * forces working in the direction opposite the reflector and applies
     * an equilibrium force in the direction of the reflector equal to the forces
     * working against it.
     *
     * @param equilibriumDirection the directional vector, pointing toward the reflector
     *
     */
    private void equalizeForce(FloatVector equilibriumDirection) {
        // take a projection of all our forces in the direction of this equalizer
        FloatVector proj = equilibriumDirection.project(forceSum);
        
        // now, get the magnitude of that projection and apply it to the direction
        equilibriumDirection.scale(proj.getMagnitude());
        
        if (isDebug()) {
//            System.out.println("Project f: " + proj.toString());
//            System.out.println("Pre-eq fo: " + forceSum.toString());
//            System.out.println("Equilizer: " + equilibriumDirection.toString());
        }
        // combine that with our current forces and set the new current forces
        forceSum = forceCombination(equilibriumDirection);
        if (isDebug()) {
//            System.out.println("Post-eq f: " + forceSum.toString());
        }
    }
    
    /** In cases where an infinitely strong reflector has equalized forces acting
     * opposite it, the velocity in that direction must also be equalized for a rapid
     * model to be possible.  This will do as the equalizeForces method does, except
     * for velocity.
     *
     * @param equilibriumDirection a vector in the direction of the reflector
     */
    private void equalizeVelocity(FloatVector equilibriumDirection) {
        FloatVector proj = equilibriumDirection.project(velocity);
        //DoubleVector proj = velocity.project(equilibriumDirection);
        
        equilibriumDirection.scale(proj.getMagnitude());
        if (isDebug()) {
//            System.out.println("VEL EQ:  " + equilibriumDirection.toString());
//            System.out.println("PROJ     " + proj.toString());
        }
        velocity.add(equilibriumDirection);
    }
    
    public FloatVector getMomentum() {
        return (getVelocity().scale(getMass()));
    }
    
    /** This method is called from a colliding objects doCollision method
     *  This is the business end of collision invokation.  It determines for
     *  both objects what the outcome of the collision will be.
     *
     *  This is a pretty simple collision model.  Its completely elastic, and it probably
     *  won't work worth a damn.
     *
     *  @param c the object we're colliding with
     *  @return The new momentum of the caller
     */
    public synchronized FloatVector getCollisionMomentum(SimpleCollisionItem c) {
        //return (c.getMomentum((CollisionItem) this));
        
        // them    us    them     us
        // m1v1 + m2v2 = m1v1' + m2v2'
        // we know our momentum getMomentum(), we know their momentum c.getMomentum()
        FloatVector us = getMomentum();
        FloatVector them = c.getMomentum();
        final float ourMass = getMass();
        final float theirMass = getMass();
        // p = v*m => v = p/m
        FloatVector ourVelocity = ((FloatVector)us.clone()).scale(1/ourMass);
        FloatVector theirVelocity = ((FloatVector)them.clone()).scale(1/theirMass);
        
        // these equations are the result of the conservation of momentum
        // and the conservation of kinetic enegry equations
        //
        // v1 = v1i * (m1 - m2) / (m1 + m2) + v2i * (2 * m2) / (m1 + m2)
        // v2 = v1i * ( 2 * m1) / (m1 + m2) + v2i * (m2 - m1) / (m2 + m1)
        //   (credit: http://www.mcasco.com/p1lmc.html)
        
        FloatVector ourNewVelocity = (FloatVector)ourVelocity.clone();
        // this is the first half of eq1..
        // v1 = v1i * (m1 - m2) / (m1 + m2)
        ourNewVelocity.scale((ourMass-theirMass)/(ourMass+theirMass));
        // second half of eq1
        // + v2i * (2 * m2) / (m1 + m2)
        ourNewVelocity.add(((FloatVector)theirVelocity.clone()).scale((2*theirMass)/(ourMass+theirMass)));
        
        FloatVector theirNewVelocity = (FloatVector)theirVelocity.clone();
        // second part of first eq
        // v2i * (m2 - m1) / (m2 + m1)
        theirNewVelocity.scale((theirMass-ourMass)/(theirMass+ourMass));
        // + v1i * (2 * m1) / (m1 + m2)
        theirNewVelocity.add(((FloatVector)ourVelocity.clone()).scale((2 * ourMass)/(ourMass+theirMass)));
        
        // now we set our velocity, and tell them their new momentum
        if (getCollidable())
            setVelocity(ourNewVelocity);
        
        return(theirNewVelocity);
    }
    
    /** This method invokes a 2-body collision between this object and another object
     *  it calls the colliding object to find out what the result of the collision will
     *  be.
     *
     *  @param c object to collide with (colliding object will determine collision semantics)
     *
     */
    public synchronized void doCollision(SimpleCollisionItem c) {
        
        //System.out.println("doing collision between " + this.toString() + " and " + c.toString());
        
        if (!getCollidable())
            return;
        
        // we're going to fetch what he wants us to be
        final FloatVector hisMomentum = c.getCollisionMomentum((SimpleCollisionItem)this);
        
        // now we remove our mass from that
        hisMomentum.scale(1/getMass());
        
        // now we set our velocity
        setVelocity(hisMomentum);
        
        ///System.out.println("hell yeah, smack that bitch");
        
        // i'd say the collision is over now, that was messy
    }
    
    /** Computes the forces that are to work on the item
     *
     * It will identify finite forces and infinite forces and handle them individually.
     * First handling the finite forces, then applying all the infinite forces for as many
     * infinite forces as there are.  (to allow for "settling")
     *
     * @param forceItems a vector of ForceItem's to be processed
     */
    private void computeItemForces(Vector forceItems) {
        forceSum = new FloatVector();
        Vector equilibriumForces = new Vector();
        
        Iterator<ForceItem> i = getForceItems().iterator();
        
        while (i.hasNext()) {
            ForceItem forceItem = i.next();
            
            // lets pile up all the equilibrium forces
            if (forceItem.getForceMagnitude(this) < 0.0) {
                equilibriumForces.add(forceItem.getForce(this));
            } else {
                
                forceSum = forceCombination(forceItem.getForce(this));
                
            }
        }
        
        // now for the equilibrium forces.  we should probably work them over
        // a couple of times.
        for (int x = 0; x < equilibriumForces.size(); x++) {
            Iterator<FloatVector> j = equilibriumForces.iterator();
            
            while (j.hasNext()) {
                FloatVector dv = j.next();
                equalizeForce((FloatVector)dv.clone());
                equalizeVelocity((FloatVector)dv.clone());
            }
        }
        if (isDebug()) {
//            System.out.println(forceSum.toString());
        }
    }
    
    /*  I don't think this works quite right  it worked fine, but the way developed
     * in combineForces works considerably faster
    private void addItemForces(Vector itemForces) {
        DoubleVector forceSum = new DoubleVector();
     
        Iterator<DoubleVector> i = itemForces.iterator();
     
        while (i.hasNext()) {
            forceSum.add(i.next());
            //System.out.println("Forces  : " + forceSum.toString() + "X: " + Double.toString(forceSum.getX()));
        }
     
        // friction, bitch.
        this.forceSum = forceSum;
        System.out.println("Forcer  : " + forceSum.toString());
    }
     */
    
    /** compute the tickwise velocity
     *
     * @param deltaT measure of time to compute
     */
    private synchronized void computeVelocity(float deltaT) {
        
        // V = t*Fs + Vo
        // Velocity = deltaT * forceSum + Velocity(t-1);
        velocity = velocity.add(forceSum.scale(deltaT));
        if (isDebug()) {
//            System.out.println("Velocity: " + velocity.toString());
        }
    }
    
    public float distanceTo(PositionItem p) {
        FloatVector displacement = (FloatVector) p.getPosition().clone();
        displacement.subtract(getPosition());
        return (displacement.getMagnitude());
        
        /*
        int x1 = (int)getPosition().getX();
        int y1 = (int)getPosition().getY();
        int x2 = (int)p.getPosition().getX();
        int y2 = (int)p.getPosition().getY();
        double p1 = Math.pow(y2 - y1, 2);
        double p2 = Math.pow(x2 - x1, 2);
        return Math.sqrt(p1 + p2);
         */
    }
    
    /** Compute tickwise position
     * @param deltaT measure of time for computation
     */
    private synchronized void computePosition(float deltaT) {
        
/*
        System.out.println("before calcs");
        System.out.println(".5 * " + Double.toString(forceSum.getX()) + " * " + Double.toString(deltaT*deltaT) + " + "+ Double.toString(velocity.getX()));
        System.out.println(" * " + Double.toString(deltaT) + " + " + Double.toString(position.getX()) + " = ");
        System.out.println(Float.toString((float)((.5) * forceSum.getX()/getMass() * deltaT*deltaT + velocity.getX() * deltaT + position.getX())));
 */
        // this is the standard positional formula
        // dX = 1/2 * F(x) * t^2 + V(x)*t
        // X = dx + X(t-1)
        position.setX((float)((.5) * forceSum.getX()/getMass() * deltaT*deltaT + velocity.getX() * deltaT + position.getX()));
        position.setY((float)((.5) * forceSum.getY()/getMass() * deltaT*deltaT + velocity.getY() * deltaT + position.getY()));
    /*
        System.out.println("after calcs");
        System.out.println(".5 * " + Double.toString(forceSum.getX()) + " * " + Double.toString(deltaT*deltaT) + " + "+ Double.toString(velocity.getX()));
        System.out.println(" * " + Double.toString(deltaT) + " + " + Double.toString(position.getX()) + " = ");
        System.out.println(Double.toString((.5) * forceSum.getX() * deltaT*deltaT + velocity.getX() * deltaT + position.getX()));
     */
        
    }
    
    /** Advances the state of the machine one small notch in time.  the smaller the
     * increments, the more accurate the results.
     *
     * @param deltaT increment of time to compute.
     */
    public void tickForward(float deltaT) {
        // we need to get the forces & sum item forces
        //addItemForces(getItemForces());
        
        computeItemForces(forceItems);
        
        
        // determine current position
        computePosition(deltaT);
        
        // determine current velocity
        computeVelocity(deltaT);
        
        // all set for the next iteration
        forceSum = new FloatVector(0.0f,0.0f);
    }
    
    private FloatVector forceSum;
    private FloatVector velocity;
    
    private FloatVector position;
    
    private float mass;
    private Vector forceItems;
    
    private float velocityDecay;
    
    /**
     * Holds value of property debug.
     */
    private boolean debug = false;
    
    /**
     * Getter for property debug.
     * @return Value of property debug.
     */
    private boolean isDebug() {
        
        return this.debug;
    }
    
    /**
     * Setter for property debug.
     * @param debug New value of property debug.
     */
    private void setDebug(boolean debug) {
        
        this.debug = debug;
    }
    
    /**
     * Holds value of property size.
     */
    private int size;
    
    /**
     * Getter for property size.
     * @return Value of property size.
     */
    public int getSize() {
        
        return this.size;
    }
    
    public void addSize(int size) {
        this.size += size;
    }
    
    /**
     * Setter for property size.
     * @param size New value of property size.
     */
    public void setSize(int size) {
        
        this.size = size;
    }
    
    public Vector getForceItems() {
        return ((Vector)forceItems.clone());
    }
    
    /**
     * Setter for property velocity.
     * @param velocity New value of property velocity.
     */
    public void setVelocity(CmbMultiPhysics.FloatVector velocity) {
        if (!Float.isNaN(velocity.getX()) && !Float.isNaN(velocity.getY()))
            this.velocity = velocity;
    }
    
}
