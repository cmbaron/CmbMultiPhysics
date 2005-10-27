/*
 * ComplexCollisionItem.java
 *
 * Created on October 26, 2005, 8:36 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package CmbMultiPhysics.Collisions;
import java.awt.Shape;
import CmbMultiPhysics.*;

/**
 *
 * @author Administrator
 */
public interface ComplexCollisionItem extends SimpleCollisionItem,Trackable {
    public void correctPosition(ComplexCollisionItem c);
    public FloatVector correctPositionAbout(ComplexCollisionItem c, FloatVector intention);
    public Shape getShape();
}
