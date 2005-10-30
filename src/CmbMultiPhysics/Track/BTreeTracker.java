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

import java.util.Vector;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.Hashtable;

/**
 *
 * @author cbaron
 */
public class BTreeTracker extends TrackableTracker implements Tickable {
    
    boolean leaf;
    BTreeTracker root = null;
    BTreeTracker parent = null;
    Hashtable registry = null;
    boolean smallest;
    
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
        if (root.equals(this))
            setRegistry(new Hashtable());
    }
    
    public Hashtable getRegistry() {
        if (getRoot().equals(this))
            return ((Hashtable)registry.clone());
        else
            return (getRoot().getRegistry());
    }
    
    public void setRegistry(Hashtable h) {
        if (getRoot().equals(this))
            registry = h;
        else
            getRoot().setRegistry(h);
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
        getParent().addItemToTracker(t);
    }
    
    private void processTick() {
        Vector items;
        items = getItems();
        Iterator i = items.iterator();
        
        
        while (i.hasNext()) {
            Object obj = i.next();
            if (Trackable.class.isInstance(obj)) {
                Trackable t = (Trackable) obj;
                // ticket if we can
                if (Tickable.class.isInstance(t))
                    ((Tickable)t).tick();
                Shape thisShape = t.getShape();
                if (thisShape == null) {
                    //System.out.println("their shape is null");
                    
                }
                if (getShape() == null) {
                    //System.out.println("our shape is null");
                }
                if (!thisShape.intersects(getShape().getBounds2D()) && !thisShape.contains(getShape().getBounds2D())) {
                    /*try {
                        throw new Exception();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/
                    //System.out.println("hey, this object isn't in us anymore" + thisShape.getBounds2D().toString());
                    Vector newItems = getItems();
                    newItems.remove(t);
                    setItems(newItems);
                    rootUnregisterTracker(t, this);
                    // lets throw it at the root.
                    getRoot().passToParent(t);
                }
            }  else {
                BTreeTracker t = (BTreeTracker) i.next();
                t.tick();
            }
        }
        
    }
    
    private void splitUp() {
        
        //System.out.println("splitting up");
        
        Rectangle2D t1;
        Rectangle2D t2;
        
        Shape s = getShape();
        Rectangle2D r = s.getBounds2D();
        
        
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
            
        } else {
            t1 = new Rectangle2D.Float(minX, minY, width/2, height);
            t2 = new Rectangle2D.Float(centerX, minY, width/2, height);
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
        
        if (t1.getWidth() < 10 && t1.getHeight() < 10) {
            b1.setSmallest(true);
            b2.setSmallest(true);
        }
        
        // we are NO LONGER A LEAF
        setLeaf(false);
        
        // now we need to add all our current items into the children
        Vector items = getItems();
        Iterator i = items.iterator();
        
        // we can use recursion to do it once we replace our items with
        // the new trackers
        
        Vector newItems = new Vector();
        newItems.add(b1);
        newItems.add(b2);
        
        setItems(newItems);
        
        while (i.hasNext()) {
            Trackable item = (Trackable) i.next();
            //System.out.println("rejibbing: " + item.toString());
            rootUnregisterTracker(item, this);
            addItemToTracker(item);
        }
        
    }
    
    public boolean isLeaf() {
        return leaf;
    }
    
    public void setLeaf(boolean b) {
        leaf = b;
    }
    
    public void registerItem(Trackable t) {
        addItemToTracker(t);
    }
    
    private void addItemToTracker(Trackable t) {
        
        Vector items;
        
        items = getItems();
        if (!items.contains(t)) {
            
            if (isLeaf()) {
                items = getItems();
                if (isSmallest() || items.size() < 2) {
                    super.registerItem(t);
                    // tell root we own this object.
                    //System.out.println("Adding in an object to this guy" + this.toString());
                    rootRegisterTracker(t, this);
                    return;
                } else {
                    // when we come out of this call we're guaranteed to
                    // be a tree node
                    splitUp();
                }
            }
            
            // we need to check this again, incase we turned into a tree node
            items = getItems();
            boolean placed = false;
            
            Iterator i = items.iterator();
            while (i.hasNext()) {
                Object obj = i.next();
                
                if (BTreeTracker.class.isInstance(obj)) {
                    BTreeTracker btreenode = (BTreeTracker) obj;
                    if (btreenode.getShape().intersects(t.getShape().getBounds2D()) || btreenode.getShape().contains(t.getShape().getBounds2D())) {
                        //System.out.println("putting 'er in");
                        
                        btreenode.addItemToTracker(t);
                        placed = true;
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
                    System.out.println("UNABLE TO PLACE OBJECT: " + t.getShape().getBounds2D().toString() + "in: " + getShape().toString());
                    // this means we have a busted item.  we'll register him here, and violate the b-tree
                    // next time around he'll get in the right place (we hope)
                    super.registerItem(t);
                    rootRegisterTracker(t, this);
                    
                    
                } else {
                    //rootUnregisterTracker(t, this);
                    getParent().addItemToTracker(t);
                }
                
            }
        }
        
    }
    
    
    public synchronized void rootUnregisterTracker(Trackable t, BTreeTracker btt) {
        if (getRoot().equals(this)) {
            
            //System.out.println("removing registry size: " + Integer.toString(registry.size()));
            Vector v = rootGetTracker(t);
            
            v.remove(btt);
            rootSetTracker(t, v);
            //System.out.println("post remove registry size: " + Integer.toString(registry.size()));
            
        } else {
            getRoot().rootUnregisterTracker(t, btt);
        }
    }
    
    // register the tracker that trackable t is in.
    public synchronized void rootRegisterTracker(Trackable t, BTreeTracker btt) {
        if (getRoot().equals(this)) {
            
            Vector r = rootGetTracker(t);
            // add our tracker
            r.add(btt);
            rootSetTracker(t, r);
        } else {
            getRoot().rootRegisterTracker(t, btt);
        }
    }
    
    public synchronized Vector rootGetTracker(Trackable t) {
        if (getRoot().equals(this)) {
            Vector v = ((Vector)getRegistry().get(t));
            if (v == null) {
                v = new Vector();
            }
            return((Vector)v.clone());
        } else {
            return(getRoot().rootGetTracker(t));
        }
    }
    
    public synchronized void rootSetTracker(Trackable t, Vector v) {
        if (getRoot().equals(this)) {
            Hashtable registry = getRegistry();
            registry.remove(t);
            registry.put(t, v);
            setRegistry(registry);
        } else {
            getRoot().rootSetTracker(t, v);
        }
    }
    
    public void tick() {
        processTick();
    }
    
}
