/*
 * Collider.java
 *
 * Created on October 26, 2005, 8:56 AM
 *
 * Copyright (C) 2005 Statewide Software and Systems
 */

package CmbMultiPhysics.Collisions;

import CmbMultiPhysics.*;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author Christopher M. Baron
 */
public abstract class ComplexCollider implements ComplexCollisionItem {
    
    /** Creates a new instance of Collider */
    public ComplexCollider() {
    }
    
    abstract public Shape getShape();

    
    abstract public FloatVector getMomentum();
    abstract public FloatVector getCollisionMomentum(SimpleCollisionItem c);
    abstract public void doCollision(SimpleCollisionItem c);
    abstract public float getMass();
    abstract public void setCollidable(boolean b);
    abstract public boolean getCollidable();
    abstract public void correctPosition(ComplexCollisionItem c);
    abstract public FloatVector correctPositionAbout(ComplexCollisionItem c, FloatVector intention);
    
    public static FloatVector dist2Center(Shape shape1, Shape shape2) {

        
        FloatVector ourCenter = getCenterPoint(shape1);
        FloatVector theirCenter = getCenterPoint(shape2);
        //FloatVector dimension1 = new FloatVector((float)incomingRect.getWidth(), (float)incomingRect.getHeight());
        //FloatVector dimension2 = new FloatVector((float)nextRect.getWidth(), (float)nextRect.getHeight());
        
        //FloatVector incomingPosition = t.getPosition();
        //FloatVector collidingPosition = collidingTrackable.getPosition();
        
        return(((FloatVector) ourCenter.clone()).subtract(theirCenter));
    }
    
    public static FloatVector getCenterPoint(Shape s) {
        
        Rectangle2D r = s.getBounds2D();
        
        return(new FloatVector((float)r.getCenterX(), (float)r.getCenterY()));
 
    }
}
