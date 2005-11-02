/*
 * PhysicsSyncTrackable.java
 *
 * Created on November 2, 2005, 2:30 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package CmbMultiPhysics;

/**
 *
 * @author cbaron
 */
public interface PhysicsSyncTrackable extends CmbMultiPhysics.PhysicsTrackable {
    public void tickForward(float deltaT, int n);
}
