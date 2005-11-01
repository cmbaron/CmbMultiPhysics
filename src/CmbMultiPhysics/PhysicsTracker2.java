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
import CmbMultiPhysics.Track.BTreeTracker;
import java.util.Iterator;
import java.util.Enumeration;
import java.util.Vector;
import CmbMultiPhysics.Collisions.*;
import java.awt.geom.Area;
import java.util.Hashtable;
import java.awt.geom.Rectangle2D;
import java.util.Calendar;

/**
 *
 * @author Administrator
 */
public class PhysicsTracker2 extends BTreeTracker implements Runnable  {
    
    static PhysicsTracker2 _instance = null;
    private int rateInMillis;
    private int averageRate;
    Vector collided;
    private Thread collisionEngineThread;
    private Thread treeBottomTickingThread;
    Hashtable collisionMaster;
    int tickerSignal = 0;
    
    public void signalTicker() {
        tickerSignal++;
    }
    
    public boolean tryGetTicker() {
        
        if (tickerSignal > 0) {
            tickerSignal--;
            return(true);
        }
        return(false);
    }
    
    
    /** Creates a new instance of PhysicsTracker2 */
    public PhysicsTracker2() {
        rateInMillis = 50;
        averageRate = rateInMillis;
        setRoot(this);
        setParent(this);
        setShape(new Rectangle2D.Float(-1000f,-1000f,2000f,2000f));
        startCollisionEngineThread();
        startTreeTicker();
        collisionMaster = new Hashtable();
    }
    
    public void startTreeTicker() {
        Runnable r = new Runnable() {
            public void run() {
                
                float avgRate = rateInMillis;
                long sleepRate = rateInMillis;
                long rate = rateInMillis;
                
                int scalar = 5;
                
                // self tuning.  this is the highest latency thread, we need
                // to synchronize the other ones at the rate of this, at least
                // they would be wasting cycles grinding away unnecessarily.
                
                int tickcounter = 0;
                
                while (true) {
                    try {
                        Thread.sleep(rateInMillis/scalar);
                        rate = Calendar.getInstance().getTimeInMillis();
                        syncTick(tickcounter++);
                        //tickTheBottom();
                        signalTicker();
                        rate = Calendar.getInstance().getTimeInMillis() - rate;
                        /*
                        if (rate > avgRate) {
                            
                            sleepRate = 0;
                            avgRate = (float) (avgRate*0.999 + rate*0.001);
                            rateInMillis = (int)avgRate*scalar;
                            
                            //System.out.println("new expected rate: " + rateInMillis);
                        } else {
                            sleepRate = rateInMillis - rate;
                            if (avgRate > rateInMillis/scalar) {
                                avgRate = (float) (avgRate*0.999 + rate*0.001);
                                
                                rateInMillis = (int)avgRate*scalar;
                            }
                        
                        }*/
                        
                        //System.out.println("getting here");
                        //tickTheBottom();
                        
                        
                        
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    
                }
            }
        };
        
        treeBottomTickingThread = new Thread(r);
        treeBottomTickingThread.start();
    }
    
    public void startCollisionEngineThread() {
        Runnable r = new Runnable() {
            public void run() {
                
                
                
                
                while (true) {
                    try {
                        Thread.sleep(rateInMillis/20);
                        //System.out.println("getting here");
                        if (tryGetTicker()) {
                            //tickerSignal = 0;
                            
                            runCollisionDetection();
                            
                            collisionMaster = new Hashtable();
                            ///System.out.println("new cm");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    
                    
                }
            }
        };
        
        collisionEngineThread = new Thread(r);
        collisionEngineThread.start();
    }
    
    static public PhysicsTracker2 getInstance() {
        if (_instance == null) {
            _instance = new PhysicsTracker2();
        }
        
        return(_instance);
    }
    
    public void run() {
        Calendar cal = Calendar.getInstance();
        
        
        long mills = cal.getTimeInMillis();
        long rate;
        long sleepRate = rateInMillis;
        float runRate = (float)rateInMillis/1000;
        long runRateMillis = rateInMillis;
        long calc;
        
        
        while (true) {
            try {
                
                
                
                Thread.sleep(sleepRate);
                rate = Calendar.getInstance().getTimeInMillis();
                tickForward((float)rateInMillis/1000);
                rate = Calendar.getInstance().getTimeInMillis() - rate;
                if (rate > rateInMillis) {
                    
                    sleepRate = 0;
                    //runRate = (float) (runRateMillis*0.9 + rate*0.1);
                    //runRateMillis = (long)runRate;
                    //rateInMillis = (int)runRateMillis;
                    //System.out.println("new expected rate: " + rateInMillis);
                } else {
                    sleepRate = rateInMillis - rate;
                    //if (runRateMillis > 30) {
                    //  runRate = (float) (runRateMillis*0.9 + rate*0.1);
                    //   runRateMillis = (long)runRate;
                    //   rateInMillis = (int)runRateMillis;
                    //}
                }
                
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    /** Iterates over all registered Trackables
     *  @see registerTick()
     *  @param deltaT the time duration to tick all physics items
     */
    public void tickForward(float deltaT) {
        
        //Iterator i = getItems().iterator();
        /*
        while (i.hasNext()) {
            try {
                PhysicsTrackable item = (PhysicsTrackable) i.next();
                item.tickForward(deltaT);
                // now correct the btree
                BTreeTracker trackerForItem = rootGetTracker((Trackable)item);
                trackerForItem.tick();
                // this will return objects inside our bounding box
                // it is a rough estimation
         
                Vector objectsInCollision = super.getIntersectingObjects(item.getShape());
                doCollisions(item, objectsInCollision);
                //System.out.println("tick");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
         */
        
        Hashtable reg = getRegistry();
        
        // strobe all objects to move.
        
        // this all needs to be rewritten to support RECURSION
        // not this bullshit of crawling up the bottom.. too inefficient!
        
        Vector runVector = new Vector();
        
        //System.out.println(reg.size());
        
        for (Enumeration e = reg.keys(); e.hasMoreElements(); ) {
            PhysicsTrackable t = (PhysicsTrackable) e.nextElement();
            t.tickForward(deltaT);
            /*
            if (runVector.size() < 15) {
                runVector.add(t);
            } else {
                Runnable r = new ForwardTickThread((Vector)runVector.clone(),deltaT);
                Thread thread = new Thread(r);
                thread.start();
                runVector = new Vector();
            } */
            
        }
        /*
        Runnable r = new ForwardTickThread(runVector,deltaT);
            Thread thread = new Thread(r);
            thread.start(); */
        
        
        // correct the b-tree
        //tick();
        //tickTheBottom();
        //signalTicker();
        
        // process collisions?
        
        
        ///System.out.println("TICK");
    }
    
    public void runCollisionDetection() {
        Hashtable reg = getRegistry();
        //Hashtable collided = new Hashtable();
        
        for (Enumeration e = reg.keys(); e.hasMoreElements(); ) {
            PhysicsTrackable t = (PhysicsTrackable) e.nextElement();
            
            // uh, this is still checking everything.
            // create a holder for the collisions
            Vector objectsInCollision = new Vector();
            
            // get the list of squares this object exists in
            Vector v = rootGetTracker(t);
            Iterator i = v.iterator();
            // iterate over those squares to find collisions
            Hashtable collided = (Hashtable)collisionMaster;
            Vector actuallyCollideWith = new Vector();
            
            
            
            objectsInCollision.addAll(getIntersectingObjects(t.getShape(), PhysicsTrackable.class));
            
            
            
            
            Iterator i2 = ((Vector)objectsInCollision.clone()).iterator();
            
            
            
            while (i2.hasNext()) {
                Object obj = i2.next();
                // this is the other way we multiply collide (its listed
                // from two btree nodes
                if (!actuallyCollideWith.contains(obj) && !obj.equals(t)) {
                    actuallyCollideWith.add(obj);
                }
                if (collided.containsKey(obj)) {
                    
                    Vector v1 = (Vector)collided.get(obj);
                    if (v1.contains(t))
                        actuallyCollideWith.remove(obj);
                }
            }
            collisionMaster.put(t, actuallyCollideWith);
            
            
            doCollisions(t, actuallyCollideWith);
        }
    }
    
    public void tickTheBottom() {
        Hashtable reg = getRegistry();
        
        int tickcounter = 0;
        
        for (Enumeration e = reg.keys(); e.hasMoreElements(); ) {
            PhysicsTrackable t = (PhysicsTrackable) e.nextElement();
            
            // get the list of squares this object exists in
            Vector v = rootGetTracker(t);
            Iterator i = v.iterator();
            // iterate over those squares to find collisions
            while (i.hasNext()) {
                BTreeTracker btt = (BTreeTracker) i.next();
                btt.syncTick(tickcounter--);
            }
        }
    }
    
    
    
    public void doCollisions(PhysicsTrackable inbound, Vector hitting) {
        Iterator i = hitting.iterator();
        
        while (i.hasNext()) {
            
            PhysicsTrackable collidingWith = (PhysicsTrackable) i.next();
            
            // we have to make sure we don't collide with ourselves
            if (inbound != collidingWith) {
                
                // this will do a much more refined check of collisions
                Area inboundArea = new Area(inbound.getShape());
                Area collidingArea = new Area(collidingWith.getShape());
                inboundArea.intersect(collidingArea);
                
                if (!inboundArea.isEmpty()) {
                    // high precision collision check successful.
                    if (ComplexCollisionItem.class.isInstance(inbound) && ComplexCollisionItem.class.isInstance(collidingWith)) {
                        
                        ((ComplexCollisionItem) inbound).correctPosition((ComplexCollisionItem)collidingWith);
                        ((ComplexCollisionItem) inbound).doCollision((ComplexCollisionItem)collidingWith);
                        
                        
                    }
                }
            }
        }
        
    }
    
    private class ForwardTickThread implements Runnable {
        
        Vector items;
        float deltaT;
        
        public ForwardTickThread(Vector r, float deltaT) {
            items = r;
            this.deltaT = deltaT;
        }
        
        public void run() {
            Iterator i = items.iterator();
            while(i.hasNext()) {
                ((PhysicsTrackable) i.next()).tickForward(deltaT);
            }
        }
    }
    
}
