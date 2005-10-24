/*
 * Drivable.java
 *
 * Created on October 24, 2005, 6:49 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package CmbMultiPhysics;

/** Drivable Controls
 *
 * This controls represent the controls necessary to drive a "car-like" object
 * Boats, motorcycles, busses, cars.  E.g. anything that has a gas pedal, break
 * and steering wheel.
 *
 * @author Christopher Baron
 */
public interface Drivable {
    public void setAcceleration(float pct);
    public void setBrake(float pct);
    public void resetAcceleration();
    public void resetBrake();
    public float getAccelleration();
    public float getBrake();
    public FloatVector getVelocity();
    public void turn(FloatVector f);
}
