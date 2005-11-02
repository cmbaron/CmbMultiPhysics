/*
 * BTreeTracker.java
 *
 * Created on October 26, 2005, 3:01 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package CmbMultiPhysics.Track;

import java.awt.event.ContainerAdapter;
import java.util.Vector;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.Hashtable;

/**
 *
 * @author cbaron
 */
public class BTreeTracker extends TrackableTracker implements SyncTickable {
    
    boolean leaf;
    BTreeTracker root = null;
    BTreeTracker parent = null;
    boolean smallest;
    int lastticked = -1;
    
    /** Creates a new instance of BTreeTracker */
    public BTreeTracker() {
        leaf = true;
        smallest = false;
    }
    
    
    
    public boolean isSmallest() {
        return smallest;
    }
    
    public void setSmallest(boolean b) {
        smallest = b;
    }
    
    public void setRoot(BTreeTracker btt) {
        root = btt;
    }
    
    public BTreeTracker getRoot() {
        return(root);
    }
    
    public void setParent(BTreeTracker btt) {
        parent = btt;
    }
    
    public BTreeTracker getParent() {
        return(parent);
    }
    
    public void passToParent(Trackable t) {
        if (getParent().equals(this)) {
            // well, i don't quite know why we're here, so lets dump to the screen
            //System.out.println("trying to pass to parent, which is myself. shit.");
            
        }
        getParent().addItem2(t);
    }
    
    public void upwardResorting() {
        Object a[];
        //synchronized(items) {
            //Vector theseItems = getItems();
            //Vector theseItems = items;
            a = ((Vector)getItems().clone()).toArray();
            //Iterator i = theseItems.iterator();
        //}
        for (int x = 0; x < a.length; x++) {
            Object obj = a[x];
            if (!BTreeTracker.class.isInstance(obj)) {
                Trackable t = (Trackable) obj;
                if (!isLeaf()) {
                    //synchronized(items) {
                        items.remove(obj);
                    //}
                    passToParent(t);
                    
                } else {
                    Shape thisShape = getBounds();
                    //if (!thisShape.intersects(t.getBounds()) && !thisShape.contains(t.getBounds())) {
                     if (!isWithinBoundryParameters(thisShape, getBounds(), Tracker.ContainerParameters.CONTAINSORINTERSECTS)) {
                    /*try {
                        throw new Exception();
                    } catch (Exception e) {
                      e.printStackTrace();
                    }*/
                        //System.out.println("hey, this object isn't in us anymore" + thisShape.getBounds2D().toString());
                        //synchronized(items) {
                            items.remove(obj);
                        //}
                        passToParent(t);
                    }
                }
            }
        }
        
    }
    
    public void syncRunOnTrackables(BTreeTrackableRunnable b, int n) {
        //Vector items;
        //items = getItems();
        Object a[];
        //synchronized(items) {
            a = getItems().toArray();
        //}
        //Iterator i = items.iterator();
        
        for (int x = 0; x < a.length; x++) {
            Object obj = a[x];
            if (BTreeTracker.class.isInstance(obj))
                ((BTreeTracker) obj).syncRunOnTrackables(b, n);
            else if (Trackable.class.isInstance(obj)) {
                b.run((Trackable) obj, this, n);
            }
        }
        
    }
    
    private void processTick(int n) {
        Object a[];
        //synchronized(items) {
            a = getItems().toArray();
        //}
        //Iterator i = items.iterator();
        
        for (int x = 0; x < a.length; x++) {
            Object obj = a[x];
            if (Trackable.class.isInstance(obj)) {
                Trackable t = (Trackable) obj;
                // ticket if we can
                if (SyncTickable.class.isInstance(t))
                    ((SyncTickable)t).syncTick(n);
                //System.out.println("syncticked");
                
                // this is where we should be doing collision detection
                
                
                
            }  else {
                BTreeTracker t = (BTreeTracker) a[x];
                t.syncTick(n);
            }
        }
        
        upwardResorting();
        
        
        
    }
    
    private void splitUp() {
        
        //System.out.println("splitting up");
        
        Rectangle2D t1;
        Rectangle2D t2;
        
        
        Rectangle2D r = getBounds();
        
        
        final float centerX,centerY;
        centerX = (float)r.getCenterX();
        centerY = (float)r.getCenterY();
        final float minX = (float)r.getMinX();
        final float minY = (float)r.getMinY();
        final float maxX = (float)r.getMaxX();
        final float maxY = (float)r.getMaxY();
        final float height = (float)r.getHeight();
        final float width = (float)r.getWidth();
        
        if (r.getWidth() < r.getHeight()) {
            
            // split rectangle about centerY
            t1 = new Rectangle2D.Float(minX,minY,width,height/2);
            t2 = new Rectangle2D.Float(minX,centerY,width,height/2);
            System.out.println("xsplit: " + t1 + " " + t2);
            
        } else {
            t1 = new Rectangle2D.Float(minX, minY, width/2, height);
            t2 = new Rectangle2D.Float(centerX, minY, width/2, height);
             System.out.println("ysplit:" + t1 + " " + t2);
        }
        
        
        
        //System.out.println("split1: " + t1.toString());
        //System.out.println("split2: " + t2.toString());
        
        // now we need to create two trackers.
        BTreeTracker b1 = new BTreeTracker();
        BTreeTracker b2 = new BTreeTracker();
        b1.setShape(t1);
        b2.setShape(t2);
        b1.setParent(this);
        b2.setParent(this);
        b1.setRoot(getRoot());
        b2.setRoot(getRoot());
        
        if (t1.getWidth() < 50 || t1.getHeight() < 50) {
            b1.setSmallest(true);
            b2.setSmallest(true);
        }
        
        // we are NO LONGER A LEAF
        setLeaf(false);
        
        synchronized(items) {
            items.add(b1);
            items.add(b2);
        }
        
        /*
        synchronized(items) {
            Vector oldItems = (Vector) getItems().clone();
            Vector newItems = new Vector();
            newItems.add(b1);
            newItems.add(b2);
            setItems(newItems);
         
            Object a[] = oldItems.toArray();
         
            for (int x = 0; x < a.length; x++) {
                Object obj = a[x];
                addItemToTracker((Trackable)obj);
            }
         
        }
         */
        /*  this doesn't make sense here, lets defer this until after.
        // now we need to add all our current items into the children
        Vector items;
        Iterator i;
         
        synchronized(items) {
         
            i = items.iterator();
         
            // we can use recursion to do it once we replace our items with
            // the new trackers
         
            Vector newItems = new Vector();
            newItems.add(b1);
            newItems.add(b2);
         
            setItems(newItems);
        }
         
        while (i.hasNext()) {
            Trackable item = (Trackable) i.next();
            //System.out.println("rejibbing: " + item.toString());
            rootUnregisterTracker(item, this);
            addItemToTracker(item);
        }
         */
        
    }
    
    public boolean isLeaf() {
        return leaf;
    }
    
    public void setLeaf(final boolean b) {
        leaf = b;
    }
    
    public synchronized void registerItem(final Trackable t) {
        addItem2(t);
    }
    
    private synchronized boolean addItem2(final Trackable t) {
        final Rectangle2D bounds = t.getBounds();
        if (isWithinBoundryParameters(bounds, getBounds(), Tracker.ContainerParameters.CONTAINSORINTERSECTS)) {
            if (isLeaf()) {
                if (!items.contains(t)) {
                    if (isSmallest() || items.size() < 5) {
                        items.add(t);
                        System.out.println("adding " + t.getBounds() + " to: " + getBounds());
                        return true;
                    } else {
                        splitUp();
                        addItem2(t);
                    }
                } else {
                    // contains
                    
                    // this shouldn't happen
                    System.out.println("adding a contained object???? ");
                    //System.out.println(isWithinBoundryParameters(bounds, getBounds(), Tracker.ContainerParameters.CONTAINSORINTERSECTS));
                    //System.out.println(getBounds() + " contains: " + t.getBounds());
                    return true;
                }
            } else {
                boolean placed = false;
                Object a[] = ((Vector)items.clone()).toArray();
                for (int x = 0; x < a.length; x++) 
                    if (BTreeTracker.class.isInstance(a[x]))
                        if (((BTreeTracker) a[x]).addItem2(t))
                            placed = true;
                if (placed)
                    return true;
            }
        } else {
            
            return (false);
        }
        
        // if we made it here that means there was no legitimate reason to bomb out
        // but we didn't place it anywhere.
        items.add(t);
        System.out.println("had nothing else to do, so i ate it." + t.getBounds());
        return(true);
        
    }
    /*
    private synchronized void addItemToTracker(final Trackable t) {
        
        boolean contained = false;
        //Vector items;
        synchronized(items) {
            //items = getItems();
            if (!items.contains(t)) {
                
                if (isLeaf()) {
                    //items = getItems();
                    if (isSmallest() || items.size() < 5) {
                        items.add(t);
                        // tell root we own this object.
                        //System.out.println("Adding in an object to this guy" + this.toString());
                        //rootRegisterTracker(t, this);
                        return;
                    } else {
                        // when we come out of this call we're guaranteed to
                        // be a tree node
                        splitUp();
                        //return;
                    }
                }
            } else {
                contained = true;
            }
        }
        // we need to check this again, incase we turned into a tree node
        boolean placed = false;
        final Rectangle2D bounds = t.getBounds();
        if (isWithinBoundryParameters(bounds, getBounds(), Tracker.ContainerParameters.CONTAINSORINTERSECTS)) {
            //items = getItems();
            
            synchronized(items) {
                Object a[] = getItems().toArray();
                
                //Iterator i = items.iterator();
                
                for (int x = 0; x < a.length; x++) {
                    final Object obj = a[x];
                    //final Object obj = i.next();
                    
                    if (BTreeTracker.class.isInstance(obj)) {
                        final BTreeTracker btreenode = (BTreeTracker) obj;
                        final Rectangle2D btreeShape = btreenode.getBounds();
                        if (isWithinBoundryParameters(bounds, btreeShape, Tracker.ContainerParameters.CONTAINSORINTERSECTS)) {  // note removed contains clause here
                            //System.out.println("putting 'er in");
                            
                            if (contained)
                                items.remove(t);
                            
                            btreenode.addItemToTracker(t);
                            
                            placed = true;
                        }
                    }
                }
                
            }
        }
        if (!placed) {
            //System.out.println("not quite sure why this guy is here, bouncing up");
            // we need to make sure we're not the root if we're going to pass it up
            // because we could get into a loop
            if (getParent().equals(this)) {
                // we've got an object outside of our trackability, so we need
                // some mechanism to revalidate the whole world above us.
                //System.out.println("UNABLE TO PLACE OBJECT: " + t.getShape().getBounds2D().toString() + "in: " + getShape().toString());
                // this means we have a busted item.  we'll register him here, and violate the b-tree
                // next time around he'll get in the right place (we hope)
                //super.registerItem(t);
                synchronized(items) {
                    items.add(t);
                }
                //rootRegisterTracker(t, this);
                
                
            } else {
                //rootUnregisterTracker(t, this);
                synchronized(items) {
                    if (contained)
                        items.remove(t);
                }
                getParent().addItemToTracker(t);
            }
            
        }
    }*/
    
    public void syncTick(int n) {
        /*
        //if (!isLeaf()) {
         
            Runnable r = new Runnable() {
                public void run() {processTick();}
            };
            Thread t = new Thread(r);
            t.start();
        //} else {*/
        
        if (n == lastticked)
            return;
        
        lastticked = n;
        
        processTick(n);
        //}
        
        
        
    }
    
}
