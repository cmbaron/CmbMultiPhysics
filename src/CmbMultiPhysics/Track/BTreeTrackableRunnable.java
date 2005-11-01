/*
 * BTreeRunnableOnSyncTick.java
 *
 * Created on October 31, 2005, 10:17 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package CmbMultiPhysics.Track;

/**
 *
 * @author Administrator
 */
public interface BTreeTrackableRunnable {
    public void run(Trackable t, BTreeTracker btt);
}
