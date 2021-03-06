/*
 * CollisionItem.java
 *
 * Created on October 11, 2005, 3:19 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package CmbMultiPhysics.Collisions;

import CmbMultiPhysics.*;

/**
 *
 * @author cbaron
 */
public interface SimpleCollisionItem {

    // the idea is to call doCollision(c)
    // doCollision calls c.getCollisionMomentum(c)
    // getCollisionMomentum calls c.getMomentum(c);
    // getMomentum returns the CURRENT momentum
    // getCollisionMomentum returns the NEW momentum (and sets it locally!)
    // doCollision sets the momentum given by the doCollisionMomentum call.
    public FloatVector getMomentum();
    public FloatVector getCollisionMomentum(SimpleCollisionItem c);
    public void doCollision(SimpleCollisionItem c);
    public float getMass();
    public void setCollidable(boolean b);
    public boolean getCollidable();
}
