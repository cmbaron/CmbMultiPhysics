/*
 * RectanglePoint.java
 *
 * Created on October 24, 2005, 8:03 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package CmbMultiPhysics;

import java.awt.geom.Rectangle2D;

/**
 *
 * @author Administrator
 */
public class RectanglePoint extends Rectangle2D.Float {
       
    /** Creates a new instance of RectanglePoint 
     *
     * @param f CENTER point of this rectangle!
     */
    public RectanglePoint(final FloatVector f, final float size) {
        super(f.getX()-(float)size/2, f.getY()-(float)size/2, size, size);
    }

   public RectanglePoint(final FloatVector f, final float sizex, final float sizey) {
        super(f.getX()-(float)sizex/2, f.getY()-(float)sizey/2, sizex, sizey);
    }
}
