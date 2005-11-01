/*
 * Tracker.java
 *
 * Created on October 26, 2005, 11:17 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package CmbMultiPhysics.Track;

import CmbMultiPhysics.*;
import java.util.Vector;
import java.util.Iterator;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;


/**
 *  This class will implement an interface for tracking objects on a plane.
 *
 *  This will not allow the objects to move.  An extension of this class will
 *  be implemented to permit motion.
 *
 *  The notion of motion is important because a new B-Tree will need to be
 *  constructed after each motion.  That is the moving items will need to be
 *  revalidated.
 *
 *  You can get objects inside, outside, or intersecting arbitrary bounds, and
 * in instance of a certain class
 * Vector getContainingObjects(Shape bounds);
 * Vector getContainingObjects(Shape bounds, Class classType);
 *
 * Vector getIntersectingObjects(Shape bounds);
 * Vector getIntersectingObjects(Shape bounds, Class classType);
 *
 * Vector getOutsideObjects(Shape bounds);
 * Vector getOutsideObjects(Shabe bounds, Class classType);
 *
 * these are implemented very abstractly, and the key to the abstraction is a
 * static public method that can determine if one object
 * is contained within the bounds, intersects the bounds, or is outside the bounds.
 *
 * static public boolean isWithinBoundryParameters(Shape object, Shape boundry,
 * ContainerParamaters parameter);
 * Tracker.ContainerParameters.CONTAINS
 * Tracker.ContainerParameters.INTERSECTS
 * Tracker.ContainerParameters.OUTSIDE
 *
 * OUTSIDE is implemented as NOT CONTAINED and NOT INTERSECTS
 *
 * @author cbaron
 */
public class Tracker {
    
    
    public static enum ContainerParameters { CONTAINS, INTERSECTS, NOTCONTAINED, CONTAINSORINTERSECTS };
    
    Vector items;
    
    /** Creates a new instance of Tracker */
    public Tracker() {
        items = new Vector();
    }
    
    /** Add an item to this tracker This method is private to allow the overriding
     * of it in extentions of this class.  Possible extensions include a B-Tree
     * of Trackers, and as such the public method should call this method, in
     * addition to doing whatever else it needs to do to validate the b-tree.
     *
     * @param t A trackble to be added
     *
     */
    private void addItemToTracker(Trackable t) {
        items.add(t);
    }
    
    /** Adds a Trackable object to this Tracker
     *
     * @param t Trackable to be added
     */
    public void registerItem(Trackable t) {
        
        addItemToTracker(t);
        
    }
    
    /** Determines if an object meets an arbitrary bounds condition
     * BoundryConditions can be specified by using the ContainerParameters
     * More container parameters may be added and should be implemented in this
     * method.
     *
     * @param object Shape object to fit in boundry (will getBounds2D for comparison)
     * @param boundry Shape boundry to perform comparison on
     * @param parameter use Tracker.ContainerParameters to specify behavior
     *
     * @return if the object matches the boundry parameters compared to the specified boundry
     *
     */
    static public boolean isWithinBoundryParameters(Shape object, Shape boundry, ContainerParameters parameter) {
        
        boolean match = false;
        
        final Rectangle2D boundingBox = object.getBounds2D();
        
        
        switch (parameter) {
            case CONTAINS:
                if (boundry.contains(boundingBox)) {
                    match = true;
                }
                break;
            case INTERSECTS:
                if (boundry.intersects(boundingBox)) {
                    match = true;
                }
                break;
            case NOTCONTAINED:
                if (!boundry.contains(boundingBox) && !boundry.intersects(boundingBox)) {
                    match = true;
                }
                break;
            case CONTAINSORINTERSECTS:
                if (boundry.contains(boundingBox) || boundry.intersects(boundingBox)) {
                    match = true;
                }
                break;
            default:
                match = false;
                
        }
        
        return(match);
    }
    
    
    /** This method finds all shapes within an arbitrary boundry specified by
     * the boundry parameter, and the container parameters. (which are boundry
     * comparison controllers: e.g. ContainerParameters.CONTAINS, etc.)
     *
     * @param boundry boundry to search objects for
     * @param parameter boundry parameters ContainerParameters.CONTAINS, ...
     * @param classtype class type to search for (Object.class) for all
     *
     *
     * @return a vector of objects in this Tracker that meet the criteria.  They
     * will implement, at least @see Trackable.
     *
     */
    public Vector getObjectsFromBoundry(Shape boundry, ContainerParameters parameter, Class classtype) {
        
        Vector returnVector = new Vector();
        Iterator i = getItems().iterator();
        
        while (i.hasNext()) {
            Trackable checkingTrackable = (Trackable) i.next();
            
            if (isWithinBoundryParameters(checkingTrackable.getBounds(), boundry, parameter)) {
                if (classtype.isInstance(checkingTrackable)) {
                    returnVector.add(checkingTrackable);
                }
            }
            
        }
        
        return (returnVector);
        
    }
    
    /** Gets all objects in this Tracker contained by the boundry specified
     *
     * @param boundry set the boundry in which objects should be given
     *
     * @return the contained Trackables
     */
    public Vector getContainedObjects(Shape boundry) {
        return (getContainedObjects(boundry, Object.class));
    }
    
    /** Gets all the objects in this Tracker contained by the boundry specified
     * and is of the classtype specified.  The class type will always match
     * Trackable and Object.
     *
     * @param boundry set the boundry in which objects should be given
     * @param classtype the class an object must be an instance of to be returned
     *
     * @return a vector of, at least, Trackables that are inside the boundry
     * and are an instance of classtype
     */
    public Vector getContainedObjects(Shape boundry, Class classtype) {
        return(getObjectsFromBoundry(boundry, ContainerParameters.CONTAINS, classtype));
    }
    
    /** Gets all objects in this Tracker intersecting by the boundry specified
     *
     * @param boundry set the boundry in which objects should be given
     *
     * @return the intersecting Trackables
     */
    public Vector getIntersectingObjects(Shape boundry) {
        
        return(getIntersectingObjects(boundry, Object.class));
    }
    
    /** Gets all the objects in this Tracker intersected by the boundry specified
     * and is of the classtype specified.  The class type will always match
     * Trackable and Object.
     *
     * @param boundry set the boundry in which objects should be given
     * @param classtype the class an object must be an instance of to be returned
     *
     * @return a vector of, at least, Trackables that are intersecting the boundry
     * and are an instance of classtype
     */
    public Vector getIntersectingObjects(Shape boundry, Class classtype)  {
        return(getObjectsFromBoundry(boundry, ContainerParameters.INTERSECTS, classtype));
    }
    
    /** Gets all objects in this Tracker intersecting by the boundry specified
     *
     * @param boundry set the boundry in which objects should be given
     *
     * @return the intersecting Trackables
     */
    public Vector getOutsideObjects(Shape boundry) {
        return(getOutsideObjects(boundry, Object.class));
    }
    
    /** Gets all the objects in this Tracker intersected by the boundry specified
     * and is of the classtype specified.  The class type will always match
     * Trackable and Object.
     *
     * @param boundry set the boundry in which objects should be given
     * @param classtype the class an object must be an instance of to be returned
     *
     * @return a vector of, at least, Trackables that are intersecting the boundry
     * and are an instance of classtype
     */
    public Vector getOutsideObjects(Shape boundry, Class classtype) {
        return(getObjectsFromBoundry(boundry, ContainerParameters.NOTCONTAINED, classtype));
    }
    
    /** Gets all items in the tracker
     *
     * @return Vector of all items
     */
    public Vector getItems() {
        return((Vector)items.clone());
    }
    
    /** set the vector of items
     *
     * @param v vector of things to add.  it had better contain TRACKABLES!!!
     */
    public void setItems(Vector v) {
        items = v;
    }
    
}
