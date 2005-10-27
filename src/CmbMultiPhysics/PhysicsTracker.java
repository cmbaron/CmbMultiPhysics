/*
 * PhysicsTracker.java
 *
 * Created on October 23, 2005, 9:02 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package CmbMultiPhysics;

import java.util.Vector;
import java.util.Enumeration;
import java.awt.Shape;
import java.lang.Class;
import java.util.Hashtable;
import java.util.Iterator;
import java.awt.geom.Rectangle2D;
import CmbMultiPhysics.Collisions.SimpleCollisionItem;
//import CmbMultiPhysics.Track.Trackable;

/**
 * This class is an alternative way of implementing moving-object physics. The 
 * MotionItem is implemented to receive 'ticks' and compute its position and 
 * velocity information by numerical integration of the forces acting on the 
 * free body. 
 * In the tradition MotionItem, the ticks must be handled by the application 
 * using the library. PhysicsTracker generates its own ticks, and tells all 
 * Trackable's to tickForward.
 * 
 * Thread t = new Thread(PhysicsTracker.getInstance());
 * t.start();
 * 
 * PhysicsTracker implements register(Trackable t), and 
 * registerTick(Trackable t). This allows the tracker to maintain a list of all 
 * objects to be tracked, and a per-tick update of positions. 
 * register(Trackable t) puts the Trackable in a Vector, over which ticks are 
 * iterated.
 * 
 * The key to collisions is the registerTick method. When a Trackable gets a 
 * tick it should do its processing then call 
 * PhysicsTracker.getInstance().registerTick(self). The tracker gets the shape 
 * of the caller, checks to see if keys (Shape) in the Hashtable of 
 * perTickTrackables intersects that shape, if it does then it checks to see 
 * if the Trackables are also CollisionItems. If they are both CollisionItems 
 * a collision is forced, positions are corrected*, and the Trackable is thrown 
 * into a hash keyed by its Shape.
 *
 * @implements Runnable
 * @author Chris Baron
 */
public class PhysicsTracker implements Runnable {
    
    static private PhysicsTracker instance = null;
    private Hashtable perTickItems;
    private Vector trackableItems;
    private int rateInMillis;
    
    /** Creates a new instance of PhysicsTracker */
    public PhysicsTracker() {
        trackableItems = new Vector();
        rateInMillis = 1;
    }
    
    /** Gets the singleton instance
     *
     * @return instance of the physicstracker
     */
    static public PhysicsTracker getInstance() {
        if (instance == null) {
            instance = new PhysicsTracker();
        } 
        return(instance);
    }
    
    /** 
     * Runs the thread
     */
    public void run() {
        
        while (true) {
            try {
                //System.out.println("RUNN");
                Thread.sleep(rateInMillis);
                tickForward((float)rateInMillis/1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    /** Iterates over all registered Trackables
     *  @see registerTick()
     *  @param deltaT the time duration to tick all physics items
     */
    public synchronized void tickForward(float deltaT) {

        perTickItems = new Hashtable();
        
        Iterator<Trackable> i = trackableItems.iterator();
    
        while (i.hasNext()) {
            try {
                Trackable item = i.next();
                item.tickForward(deltaT);
                ///System.out.println("tick");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        ///System.out.println("TICK");
    }
    
    /** Registers an item to be ticked 
     * 
     * @param Trackable
     */
    public synchronized void registerItem(Trackable t) {
        System.out.println("Register!");
        trackableItems.add(t);
    }
    
    /** Called by a ticked Tracker to register a new position
     *
     * The tracker gets the shapeof the caller, checks to see if keys (Shape) in 
     * the Hashtable of perTickTrackables intersects that shape, if it does then 
     * it checks to see if the Trackables are also CollisionItems. If they are 
     * both CollisionItems* a collision is forced, positions are corrected*, and 
     * the Trackable is thrown into a hash keyed by its Shape.
     *
     * @param t the Trackable that got ticked.
     *
     */
    public synchronized void registerTick(Trackable t) {
        Shape incomingShape = t.getShape();
        
        Enumeration<Shape> enumeration = perTickItems.keys();
    
        
        while (enumeration.hasMoreElements()) {
            Shape nextShape = enumeration.nextElement();
            ///System.out.println("incoming: " + incomingShape.getBounds2D().toString());
            ///System.out.println("next: " + nextShape.getBounds2D().toString());
            
            final Rectangle2D incomingRect = incomingShape.getBounds2D();
            final Rectangle2D nextRect = nextShape.getBounds2D(); 
            
            if (nextShape.intersects(incomingRect)) {
                System.out.println("We should be colliding now");
                // we collided with something, so lets pull out the trackable
                Trackable collidingTrackable = (Trackable) perTickItems.get(nextShape);
                
                ///System.out.println(Boolean.toString((CollisionItem.class.isInstance(t))));
                ///System.out.println(Boolean.toString((CollisionItem.class.isInstance(collidingTrackable))));
                
                // check to see if we've got TWO collision items
                if (SimpleCollisionItem.class.isInstance(t) && SimpleCollisionItem.class.isInstance(collidingTrackable)) {
                    ((SimpleCollisionItem) t).doCollision((SimpleCollisionItem)collidingTrackable);
                         
                    FloatVector center1 = new FloatVector((float)incomingRect.getCenterX(), (float)incomingRect.getCenterY());
                    FloatVector center2 = new FloatVector((float)nextRect.getCenterX(), (float)nextRect.getCenterY());
                    //FloatVector dimension1 = new FloatVector((float)incomingRect.getWidth(), (float)incomingRect.getHeight());
                    //FloatVector dimension2 = new FloatVector((float)nextRect.getWidth(), (float)nextRect.getHeight());
                    
                    //FloatVector incomingPosition = t.getPosition();
                    //FloatVector collidingPosition = collidingTrackable.getPosition();
                    
                    final FloatVector dist2Center = ((FloatVector) center1.clone()).subtract(center2);
                    
                    final float x = dist2Center.getX();
                    final float y = dist2Center.getY();
                    
                    // its left
                    if (x > 0) {
                        center1.add(new FloatVector(x/2, 0f));
                        center2.subtract(new FloatVector(x/2, 0f));
                    } else if (x < 0) {  // right
                        center1.subtract(new FloatVector(x/2, 0f));
                        center2.add(new FloatVector( x/2, 0f));
                    }
                    
                    // its below
                    if (y > 0) {
                        center1.add(new FloatVector(0f, y/2));
                        center2.subtract(new FloatVector(0f, y/2));
                    } else if (y < 0) { // above
                        center1.subtract(new FloatVector(0f, y/2));
                        center2.add(new FloatVector(0f, y/2));
                    }
                  
                    t.setPosition(center1);
                    collidingTrackable.setPosition(center2);
                    


                    
                }
            }
        }
        
        perTickItems.put(incomingShape, t);
        
    }
    
}
