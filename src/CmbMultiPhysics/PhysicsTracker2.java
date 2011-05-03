/*
 * PhysicsTracker2.java
 *
 * Created on October 26, 2005, 8:01 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package CmbMultiPhysics;
import CmbMultiPhysics.Track.Tracker;
import java.util.Iterator;
import java.util.Vector;
import CmbMultiPhysics.Collisions.*;
import java.awt.geom.Area;

/**
 *
 * @author Administrator
 */
public class PhysicsTracker2 extends Tracker implements Runnable  {
    
    static PhysicsTracker2 _instance = null;
    private int rateInMillis;
    Vector collided;
    
    /** Creates a new instance of PhysicsTracker2 */
    public PhysicsTracker2() {
        rateInMillis = 5;
    }
    
    static public PhysicsTracker2 getInstance() {
        if (_instance == null) {
            _instance = new PhysicsTracker2();
        }
        
        return(_instance);
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
    
    /** Iterates over all registered Trackables
     *  @see registerTick()
     *  @param deltaT the time duration to tick all physics items
     */
    public synchronized void tickForward(float deltaT) {
        
        Iterator i = getItems().iterator();
        
        while (i.hasNext()) {
            try {
                PhysicsTrackable item = (PhysicsTrackable) i.next();
                item.tickForward(deltaT);
                
                // this will return objects inside our bounding box
                // it is a rough estimation
                
                Vector objectsInCollision = super.getIntersectingObjects(item.getShape());
                doCollisions(item, objectsInCollision);
                //System.out.println("tick");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        ///System.out.println("TICK");
    }
    
    public void doCollisions(PhysicsTrackable inbound, Vector hitting) {
        Iterator i = hitting.iterator();
        
        if (!ComplexCollisionItem.class.isInstance(inbound)) {
            return;
        }

        ComplexCollisionItem inboundCollider = (ComplexCollisionItem) inbound;
        
        while (i.hasNext()) {
            
            PhysicsTrackable collidingWith = (PhysicsTrackable) i.next();
            
            if (!ComplexCollisionItem.class.isInstance(collidingWith)) {
                continue;
            }
            
            ComplexCollisionItem colliding = (ComplexCollisionItem) collidingWith;
            // we have to make sure we don't collide with ourselves
            if (!inboundCollider.equals(colliding)) {
                if (inboundCollider.isColliding(colliding)) {

                        inboundCollider.doCollision(colliding);
                        inboundCollider.correctPosition(colliding);
                        
                }
            }
        }
        
    }
}
