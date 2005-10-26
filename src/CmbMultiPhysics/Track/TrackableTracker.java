/*
 * TrackableTracker.java
 *
 * Created on October 26, 2005, 3:04 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package CmbMultiPhysics.Track;

import CmbMultiPhysics.FloatVector;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.Vector;
import java.util.Iterator;

/**
 *
 * @author cbaron
 */
public class TrackableTracker extends Tracker implements Trackable {
    
    Shape ourShape;
    
    /** Creates a new instance of TrackableTracker */
    public TrackableTracker() {
    }
    
    /** This method finds all shapes within an arbitrary boundry specified by
     * the boundry parameter, and the container parameters. (which are boundry
     * comparison controllers: e.g. ContainerParameters.CONTAINS, etc.)
     * This method will also recurse into TrackableTrackers which may have been
     * inserted into it.
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
            
            // Recursion into contained TrackableTrackers
            if (TrackableTracker.class.isInstance(checkingTrackable)) {
                // check to see if the TrackableTracker we have is intersecting or contained by
                // the boundry we've handed.
                if (isWithinBoundryParameters(checkingTrackable.getShape(), boundry, Tracker.ContainerParameters.CONTAINSORINTERSECTS))
                    returnVector.addAll(((TrackableTracker)checkingTrackable).getObjectsFromBoundry(boundry, parameter, classtype));
            }
            
            if (isWithinBoundryParameters(checkingTrackable.getShape(), boundry, parameter)) {
                if (classtype.isInstance(checkingTrackable)) {
                    returnVector.add(checkingTrackable);
                }
            }
            
        }
        
        return (returnVector);
        
    }
    
    /**
     * @param position THIS IS NOT THE POSITION.  position is not settable.
     *
     * @deprecated DO NOT USE
     */
    public void setPosition(FloatVector position) {
        // do not use (SEE Trackable)
    }
    
    public FloatVector getPosition() {
        Rectangle2D r = getShape().getBounds2D();
        return (new FloatVector((float)r.getCenterX(), (float)r.getCenterY() ));
    }
    
    public Shape getShape() {
        return ourShape;
    }
    
    public void setShape(Shape s) {
        ourShape = s;
    }
    
}
