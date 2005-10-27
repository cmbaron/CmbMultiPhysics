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
        
        while (i.hasNext()) {
            
            PhysicsTrackable collidingWith = (PhysicsTrackable) i.next();
            
            // we have to make sure we don't collide with ourselves
            if (inbound != collidingWith) {
                if (ComplexCollisionItem.class.isInstance(inbound) && ComplexCollisionItem.class.isInstance(collidingWith)) {
                    ((ComplexCollisionItem) inbound).doCollision((ComplexCollisionItem)collidingWith);
                    
                    ((ComplexCollisionItem) inbound).correctPosition((ComplexCollisionItem)collidingWith);
                }
            }
        }
        
    }
}
