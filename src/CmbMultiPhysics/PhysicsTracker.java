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

/**
 *
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
    
    static public PhysicsTracker getInstance() {
        if (instance == null) {
            instance = new PhysicsTracker();
        } 
        return(instance);
    }
    
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
    
    public synchronized void registerItem(Trackable t) {
        System.out.print("Register!");
        trackableItems.add(t);
    }
    
    
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
                if (CollisionItem.class.isInstance(t) && CollisionItem.class.isInstance(collidingTrackable)) {
                    ((CollisionItem) t).doCollision((CollisionItem)collidingTrackable);
                         
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
