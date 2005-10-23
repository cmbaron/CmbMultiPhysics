/*
 * Point.java
 *
 * Created on October 2, 2005, 10:21 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package CmbMultiPhysics;

/**
 *
 * @author Christopher Baron
 */
public class Point implements PositionItem {
    
    /** Creates a new instance of Point */
    public Point(FloatVector dv) {
        point = dv;
    }
    
    public Point(float x, float y) {
        point = new FloatVector(x,y);
    }
    
    public Point() {
        point = new FloatVector(0,0);
    }
    
    public FloatVector getPosition() {
        return point;
    }
    
    public void setPosition(FloatVector dv) {
        point = dv;
    }
    
    FloatVector point;
}
