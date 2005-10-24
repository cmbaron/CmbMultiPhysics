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
        rateInMillis = 50;
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
            System.out.println("incoming: " + incomingShape.getBounds2D().toString());
            System.out.println("next: " + nextShape.getBounds2D().toString());
            if (nextShape.intersects(incomingShape.getBounds2D())) {
                System.out.println("We should be colliding now");
                // we collided with something, so lets pull out the trackable
                Trackable collidingTrackable = (Trackable) perTickItems.get(nextShape);
                
                ///System.out.println(Boolean.toString((CollisionItem.class.isInstance(t))));
                ///System.out.println(Boolean.toString((CollisionItem.class.isInstance(collidingTrackable))));
                
                // check to see if we've got TWO collision items
                if (CollisionItem.class.isInstance(t) && CollisionItem.class.isInstance(collidingTrackable)) {
                    ((CollisionItem) t).doCollision((CollisionItem)collidingTrackable);
                }
            }
        }
        
        perTickItems.put(incomingShape, t);
        
    }
    
}
