/*
 * TickForwardable.java
 *
 * Created on October 26, 2005, 3:06 PM
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
public interface TickForwardable {
   /** Sets the tick time, and issues a tick for the object to process.
     *
     * Implementations of this method should CALL PhysicsTracker.getInstance().registerTick(self)
     * after the tick is processed!
     *
     * @param deltaT time difference over which numerical integration will occur
     *
     */
    public void tickForward(float deltaT);
}
