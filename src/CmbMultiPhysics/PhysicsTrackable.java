/*
 * PhysicsTrackable.java
 *
 * Created on October 26, 2005, 8:07 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package CmbMultiPhysics;

/**
 *
 * @author Administrator
 */
public interface PhysicsTrackable extends CmbMultiPhysics.Track.Trackable {
    public void tickForward(float deltaT);
    
}
