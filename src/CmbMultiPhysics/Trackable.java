/*
 * PhysicsTrackable.java
 *
 * Created on October 26, 2005, 4:37 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package CmbMultiPhysics;

/**  This is a really stupid hack to get the physics modeling working while
 *  the physics tracker transitions to the generalized tracker.
 *
 * @author cbaron
 */
public interface Trackable extends CmbMultiPhysics.Track.Trackable {
    public void tickForward(float deltaT);
}
