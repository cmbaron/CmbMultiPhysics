/*
 * ForceItem.java
 *
 * Created on October 1, 2005, 12:17 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package CmbMultiPhysics;

import java.lang.*;
import java.util.*;

/**
 *
 * @author Christopher Baron
 */
public interface ForceItem {
    
    public FloatVector getForce(MotionItem m);
    public float getForceMagnitude(MotionItem m);
    
}
