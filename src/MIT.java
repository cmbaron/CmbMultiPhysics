/*
 * MIT.java
 *
 * Created on October 1, 2005, 6:34 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

import CmbMultiPhysics.*;
import CmbMultiPhysics.ForceItems.*;

import java.io.*;

/** Motion Item Test
 *
 *  This is an example class of how to use a Motion Item and Force Items
 *
 * @author Christopher Baron
 */
public class MIT {
    
    /** Creates a new instance of MIT */
    public MIT() {
        BufferedWriter position;
        BufferedWriter velocity;
        BufferedWriter accelleration;
        
        // instantaniate a motionitem
        MotionItem m = new MotionItem();
        // some status vars
        double t = 0;
        double maxX = 0.0;
        double maxY = 0.0;
        
        // actually add things to the motion item
        //                            vertex             string length
        //m.addItem(new StringForce(new DoubleVector(5,0), 10));
        //m.addItem(new StringForce(new DoubleVector(-5,0), 10));
        //                               vector and magnitude of the acceleration of gravity
        m.addItem(new Gravity(new FloatVector(0,-10.0f)));
        //                              vertex        resistivity    equilibrium
        Point dynamicRubberBandPoint = new Point(5.0f,0.0f);
        //m.addItem(new RubberBand(dynamicRubberBandPoint, 15.0, 5.0));
        //m.addItem(new RubberBand(new Point(-5.0,0.0), 15.0, 5.0));
        //                        coefficient of friction  
        m.addItem(new VelocityFriction(0.15f));
        m.setMass(4.0f);
        m.setPosition(new FloatVector(0.0f, 1.0f));
        
        try {
            position = new BufferedWriter(new FileWriter("position.csv"));
            velocity = new BufferedWriter(new FileWriter("velocity.csv"));
            accelleration = new BufferedWriter(new FileWriter("accelleration.csv"));
            
            
            while (t < 100.0) {
                m.tickForward(0.001f);
                t += 0.01;
                System.out.println("Position: " + m.getPosition().toString());
                try {
                    position.write(Double.toString(t) + "," + m.getPosition().toNiceString() + "\n");
                    velocity.write(Double.toString(t) + "," + m.getVelocity().toNiceString() + "\n");
                    accelleration.write(Double.toString(t) + "," + m.getForces().toNiceString() + "\n");
                } catch (IOException e) {
                    
                }
                
                if (maxX < m.getPosition().getX())
                    maxX = m.getPosition().getX();
                if (maxY < m.getPosition().getY()) 
                    maxY = m.getPosition().getY();
            }
        } catch (IOException e) {

        }
        System.out.println(Double.toString(maxX) + " " + Double.toString(maxY));
    }
    
    public static void main(String[] args) {
        new MIT();
    }
    
}
