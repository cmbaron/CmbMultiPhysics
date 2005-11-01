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
import CmbMultiPhysics.Track.BTreeTrackableRunnable;
import CmbMultiPhysics.Track.BTreeTracker;
import CmbMultiPhysics.Track.Trackable;
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
    volatile static private int rateInMillis;
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

    public void treeTickerRunner(int tickcounter) {
        syncTick(tickcounter);
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
                        treeTickerRunner(tickcounter++);
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
    
    public void collisionEngineRunner(BTreeTrackableRunnable bttr) {
        
        runOnTrackables(bttr);
        
    }
    
    public void startCollisionEngineThread() {
        Runnable r = new Runnable() {
            public void run() {
                long rate;
                
                BTreeTrackableRunnable bttr1 = getCollisionDetectionBTTR();
                BTreeTrackableRunnable bttr2 = getClearCollisionsBTTR();
                
                while (true) {
                    try {
                        Thread.sleep(rateInMillis/20);
                        //System.out.println("getting here");
                        //runCollisionDetection();
                        rate = Calendar.getInstance().getTimeInMillis();
                        collisionEngineRunner(bttr1);
                        runOnTrackables(bttr2);
                        rate = Calendar.getInstance().getTimeInMillis() - rate;
                        ///System.out.println(rate);
                        //collisionMaster = new Hashtable();
                        ///System.out.println("new cm");
                        if (tryGetTicker()) {
                            //tickerSignal = 0;
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
        
        BTreeTrackableRunnable bttr = getTickForwardBTTR();
        
        while (true) {
            try {
                
                
                
                Thread.sleep(sleepRate);
                rate = Calendar.getInstance().getTimeInMillis();
                //tickForward((float)rateInMillis/1000);
                runOnTrackables(bttr);
                rate = Calendar.getInstance().getTimeInMillis() - rate;
                if (rate > rateInMillis) {
                    
                    //sleepRate = 0;
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
    
    public BTreeTrackableRunnable getClearCollisionsBTTR() {
        BTreeTrackableRunnable bttr = new BTreeTrackableRunnable() {
            public void run(Trackable t, BTreeTracker btt) {
                if (PhysicsTrackable.class.isInstance(t)) {
                    ///System.out.println("actually doing a tf " + (float)PhysicsTracker2.rateInMillis/1000);
                    ((PhysicsTrackable) t).clearCollisions();
                }
            }
        };
        return(bttr);
    }
    
    public BTreeTrackableRunnable getTickForwardBTTR() {
        BTreeTrackableRunnable bttr = new BTreeTrackableRunnable() {
            public void run(Trackable t, BTreeTracker btt) {
                ///System.out.println("called to tf");
                if (PhysicsTrackable.class.isInstance(t)) {
                    ///System.out.println("actually doing a tf " + (float)PhysicsTracker2.rateInMillis/1000);
                    ((PhysicsTrackable) t).tickForward((float)PhysicsTracker2.rateInMillis/1000);
                }
            }
        };
        
        return(bttr);
    }
    
    public BTreeTrackableRunnable getCollisionDetectionBTTR() {
        
        // define the dynamic block of code to be run
        BTreeTrackableRunnable bttr = new BTreeTrackableRunnable() {
            public void run(Trackable t, BTreeTracker btt) {
                if (!PhysicsTrackable.class.isInstance(t))
                    return;
                
                Vector v = btt.getItems();
                Object a[] = v.toArray();
                
                System.out.println(a.length);
                
                for (int x = 0; x < a.length; x++)
                    if (PhysicsTrackable.class.isInstance(a[x]))
                        newDoCollisions((PhysicsTrackable) t, (PhysicsTrackable) a[x]);
            }
            
            public void newDoCollisions(PhysicsTrackable inbound1, PhysicsTrackable inbound2) {
                
                
                if (inbound1 != inbound2) {
                    //Area inboundArea = new Area(inbound1.getShape());
                    //Area collidingArea = new Area(inbound2.getShape());
                    //inboundArea.intersect(collidingArea);
                    if (true) {
                        //if (!inboundArea.isEmpty()) {
                        // high precision collision check successful.
                        if (ComplexCollisionItem.class.isInstance(inbound1) && ComplexCollisionItem.class.isInstance(inbound2)) {
                            
                            ((ComplexCollisionItem) inbound1).correctPosition((ComplexCollisionItem)inbound2);
                            ((ComplexCollisionItem) inbound1).doCollision((ComplexCollisionItem)inbound2);
                            
                        }
                        
                    }
                }
            }
        };
        
        return (bttr);
        
    }
    /*
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
     */
    /*
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
     */
    
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
