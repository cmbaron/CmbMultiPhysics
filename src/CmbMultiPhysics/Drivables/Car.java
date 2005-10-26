/*
 * Car.java
 *
 * Created on October 24, 2005, 7:12 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package CmbMultiPhysics.Drivables;

import CmbMultiPhysics.*;
import CmbMultiPhysics.ForceItems.*;

/**
 *
 * @author Administrator
 */
public class Car extends TrackedMotionItem implements Drivable {
    
    float brake;
    float accel;
    FloatVector direction;
    
    /** Creates a new instance of Car */
    public Car() {
        super();
        addItem(new EngineForce());

        initCar();
    }
    
    public Car(FloatVector position, float mass) {
        super(position, mass);
        addItem(new EngineForce());

        initCar();
    }
    
    public Car(FloatVector position, float mass, EngineForce e) {
        super(position, mass);
        addItem(e);
        initCar();
    }
    
    public Car(EngineForce e) {
        super();
        addItem(e);
        initCar();
    }
    
    private void initCar() {
        setDirection(new FloatVector());
        addItem(new BrakeForce());
        setCollidable(false);
        resetBrake();
        resetAcceleration();
    }
    
    public void setDirection(FloatVector direction) {
        
        ///System.out.println("setting direction: " + direction.toString() + direction.unitVector().toString() + Float.toString(getVelocity().getMagnitude()));
        
        this.direction = direction;
        
        // now we'll cheat and fix our velocity this way
        if (getVelocity().getMagnitude() > 0.0f) {
            setVelocity(direction.unitVector().scale(getVelocity().getMagnitude()));
        }
    }
    
    public FloatVector getDirection() {
        return((FloatVector)direction.clone());
    }
    
    public float getBrake() {
        return brake;
    }
    
    public void setBrake(float pct) {
        if (pct <= 100 && pct >= 0) {
            brake = pct;
        }
    }
    
    public void setAcceleration(float pct) {
        if (pct <= 100 && pct >= 0) {
            accel = pct;
        }
    }
    
    public float getAcceleration() {
        return (accel);
    }
    
    public void resetAcceleration() {
        accel = 0;
    }
    
    public void resetBrake() {
        brake = 0;
    }
    
}
