/*
 * Wall.java
 *
 * Created on October 11, 2005, 3:21 PM
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
public class Wall implements CollisionItem {
    
    public static final FloatVector TOPWALL = new FloatVector(0, 1);
    public static final FloatVector BOTTOMWALL = new FloatVector(0,-1);
    public static final FloatVector LEFTWALL = new FloatVector(-1,0);
    public static final FloatVector RIGHTWALL = new FloatVector(1,0);
    
    private boolean collidable;
    
    float lossyness = 0;
    FloatVector direction;
    
    /** Creates a new instance of Wall */
    public Wall(float lossyness, FloatVector direction) {
        this.lossyness = lossyness;
        this.direction = direction;
    }
    
    public void setDirection(FloatVector direction) {
        this.direction = direction;
    }
    
    public FloatVector getDirection () {
        return (FloatVector) direction.clone();
    }
    
    // this will hand back EXACTLY what the caller's momentum should be
    // following the collision
    public FloatVector getCollisionMomentum(CollisionItem c) {
        // return  null;
        FloatVector hisMomentum = c.getMomentum();
        FloatVector proj = getDirection().project(hisMomentum);
        FloatVector directionalMomentum = getDirection().scale(proj.getMagnitude());
        
        // get a vector of the momentum in the direction of the wall
        //hisMomentum.project(direction);
        //System.out.println(directionalMomentum.toString());
        
        // reverse it, double itish
        directionalMomentum.scale(-1.0f * lossyness);
        hisMomentum.subtract(proj).add(directionalMomentum);
        return(hisMomentum);
    }
    
    /**  Walls have NO momentum, stupid.
     *
     */
    public FloatVector getMomentum() {
        return new FloatVector(0,0);
    }
    
    public void doCollision(CollisionItem c) {

        // wow, you can collide with this object, but it makes no sense for this
        // to do anything since it's a WALL.
        // i guess the standard should be to doCollision on BOTH items
        // synchronously, but there is nothing to do here.
    }
    
    public boolean getCollidable () {
        return collidable;
    }
    
    // whoa, wicked broken
    public float getMass() {
        return (1);
    }
    
    public void setCollidable(boolean s) {
        collidable = s;
    }
}
