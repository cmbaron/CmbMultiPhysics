/*
 * CollisionItem.java
 *
 * Created on October 11, 2005, 3:19 PM
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
public interface CollisionItem {

    public FloatVector getMomentum(CollisionItem c);
    public FloatVector getCollisionMomentum(CollisionItem c);
    public void doCollision(CollisionItem c);
    
}
