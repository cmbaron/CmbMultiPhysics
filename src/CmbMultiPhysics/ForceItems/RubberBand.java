/*
 * RubberBand.java
 *
 * Created on October 1, 2005, 5:55 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package CmbMultiPhysics.ForceItems;
import CmbMultiPhysics.*;

/** Represents a RubberBand force from a MotionItem to a Vertex.
 *   The RubberBand only produces a force at displacements > equilibrium.
 *   The RubberBand is a dynamic force dependant on the Position of MotionItem
 *   The position of a MotionItem depends on the forces acted on by the RubberBand
 *   The model will only function for sufficiently small increments of time.
 *
 *
 * @author Christopher Baron
 */
public class RubberBand implements ForceItem {
    
    /** Creates a new instance of RubberBand
     *
     * @param vertex the point at which the rubber band is tied.
     * @param resistivity The number of Force Units per Length Unit past equilibrium
     * @param equilibrium The number of Length Units from the Vertex at which the band starts exhibiting force
     * @param maxLength The number of equilibriums until the elastic breaks
     */
    public RubberBand(PositionItem vertex, float resistivity, float equilibrium, float maxLength) {
        this.vertex = vertex;
        this.resistivity = resistivity;
        this.equilibrium = equilibrium;
        setBreakForceScalar(maxLength);
        setBroken(false);
    }
    
    /** Creates a new instance of a RubberBand
     *
     * @param vertex the point at which the rubber band is tied
     * @param resistivity The number of Force Units per Length Unit past equilibrium
     * @param equilibrium the number of length units from the vertex at which teh band starts exhibiting forces
     * @param maxLength The number of equilibriums until the elastic breaks
     */
    public RubberBand(FloatVector vertex, float resistivity, float equilibrium, float maxLength) {
        this.vertex = new Point(vertex);
        this.resistivity = resistivity;
        this.equilibrium = equilibrium;
        setBreakForceScalar(maxLength);
        setBroken(false);
    }
    
    /** Computes the instantaneous elastic force for MotionItem m.
     *
     * @param m MotionItem which is calling this getForce
     * @return the instantaneous force vector
     */
    public FloatVector getForce(MotionItem m) {
        // the spring force is linear, determined by: Fs = k * x
        // where k is the rigity constant.
        // this is a rubberband, but we'll model it the same, except not below equilibrium
        
        FloatVector displacement = (FloatVector) vertex.getPosition().clone();
        displacement.subtract(m.getPosition());
        final float breakingForce = getBreakingForce();
        
        length = displacement;
        
        // this means:
        // Fb = Uv(displacement) * ( (||displacement|| - equilibrium) * resistivity )
        if (isDebug()) {
            System.out.println("displacement - equilibrium" + Double.toString(displacement.getMagnitude() - equilibrium));
        }
        float distance = (displacement.getMagnitude() - equilibrium);
        
        if ((distance*resistivity < breakingForce || breakingForce == 0) && !getBroken()) {
            
            FloatVector dv = displacement.unitVector().scale(Math.abs(distance)*resistivity);
            if (isDebug()) {
                System.out.println("displacement UV: " + displacement.unitVector().toString() + " * " + Double.toString((displacement.getMagnitude() - equilibrium)*resistivity));
                System.out.println("force vector   : " + dv.toString());
            }
            if ((displacement.getMagnitude() - equilibrium) > 0) {
                if (breakingForce > 0) {
                    percentOfThreshold = distance*resistivity/breakingForce * 100;
                    ///System.out.println(Double.toString(percentOfThreshold) + "% of tolerance");
                } else {
                    percentOfThreshold = 0;
                }
                return(dv);
            }
        } else {
            System.out.println("we're borked");
            setBroken(true);
        }
        
        return(new FloatVector(0,0));
    }
    
    /**
     * the instantaneous magnitude of the force
     * @param m the motionitem we're looking at
     * @return the magnitude of the force
     * @deprecated Don't use this unless you fix it first.  It currently always
     * returns 1 to fool the infinite force machine inside MotionItem
     */
    public float getForceMagnitude(MotionItem m) {
        return(1);
    }
    
    /** broken boolean
     * @param b says if the elastic is broken
     */
    public void setBroken(boolean b) {
        broken = b;
    }
    
    /** get boolean
     *
     * @return status of the elastic
     */
    public boolean getBroken() {
        return broken;
    }
    
    /** Sets the breaking force scalar
     * @param bfs the number of equlibriums until the elastic breaks
     */
    public void setBreakForceScalar(float bfs) {
        breakingForceScalar = bfs;
    }
    
    /** get the breaking force scalar
     *
     * @return the number of equlibriums until the elastic breaks
     */
    public float getBreakForceScalar() {
        return breakingForceScalar;
    }
    
    private float getBreakingForce() {
        return getBreakForceScalar()*resistivity*equilibrium;
    }
    
    
    
    /** get the percentage of the threshold of the elastic
     * @return a percentage of the elastic threshold
     */
    public float getBreakForcePercentage() {
        return percentOfThreshold;
    }
    
    
    
    /**
     * Holds value of property debug.
     */
    private boolean debug;
    
    /**
     * Getter for property debug.
     * @return Value of property debug.
     */
    public boolean isDebug() {
        
        return this.debug;
    }
    
    /**
     * Setter for property debug.
     * @param debug New value of property debug.
     */
    public void setDebug(boolean debug) {
        
        this.debug = debug;
    }
    
    
    PositionItem vertex;
    float equilibrium;
    float resistivity;
    FloatVector length;
    float breakingForceScalar;
    boolean broken;
    float percentOfThreshold;
    
}
