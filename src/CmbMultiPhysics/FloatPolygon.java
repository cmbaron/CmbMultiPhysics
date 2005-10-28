/******************************************************************
 * @(#) Polygon.java     1.0
 *
 * Copyright (c) 2002 John Miller
 * All Right Reserved
 *-----------------------------------------------------------------
 * Permission to use, copy, modify and distribute this software and
 * its documentation without fee is hereby granted provided that
 * this copyright notice appears in all copies.
 * WE MAKE NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY
 * OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. WE SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY ANY USER AS A RESULT OF USING,
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 *-----------------------------------------------------------------
 *
 * @version     1.0, 6 Mar 2002
 * @author      John Miller
 */

package CmbMultiPhysics;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;



/******************************************************************
 * The Polygon class uses GeneralPath to implement general polygons.
 */
public class FloatPolygon implements Shape
{
    private Point2D []   vertex;
    private int          nVertices;
    private GeneralPath  path;
    

    /**************************************************************
     * Construct a polygon.
     * @param  _vertex  Corner points of polygon
     */
    public FloatPolygon (Point2D [] _vertex)
    {
        vertex = _vertex;
        nVertices = vertex.length;
        path = new GeneralPath ();
        path.moveTo ((float) vertex [0].getX (), (float) vertex [0].getY ());
        for (int i = 1; i < nVertices; i++) {
            path.lineTo ((float) vertex [i].getX (), (float) vertex [i].getY ());
        }; // for
        path.closePath ();
        
        

    } // Polygon
    


    /**************************************************************
     * Tests if the specified coordinates are inside the boundary of the Shape.
     */
    public boolean contains (double x, double y) 
    {
        return path.contains (x, y);

    } // contains


    /**************************************************************
     * Tests if the interior of the Shape entirely contains the specified
     * rectangular area.
     */
    public boolean contains (double x, double y, double w, double h) 
    {
        return path.contains (x, y, w, h);

    } // contains


    /**************************************************************
     * Tests if a specified Point2D is inside the boundary of the Shape.
     */
    public boolean contains (Point2D p) 
    {
        return path.contains (p);

    } // contains


    /**************************************************************
     * Tests if the interior of the Shape entirely contains the specified
     * specified Rectangle2D.
     */
    public boolean contains (Rectangle2D r) 
    {
        return path.contains (r);

    } // contains


    /**************************************************************
     * Return an integer bounding box of the polygon.
     * @return  Rectangle  Object that bounds the current path
     */
    public Rectangle getBounds ()
    {
        return path.getBounds ();

    } // getBounds


    /**************************************************************
     * Return the bounding box of the polygon. 
     * @return  Rectangle2D  Object that bounds the current path 
     */  
    public Rectangle2D getBounds2D () 
    { 
        return path.getBounds2D (); 
 
    } // getBounds2D


    /**************************************************************
     * Returns an iterator object that iterates along the Shape boundary
     * and provides access to the geometry of the Shape outline.
     */
    public PathIterator getPathIterator (AffineTransform at)
    {
        return path.getPathIterator (at);

    } // getPathIterator


    /**************************************************************
     * Returns an iterator object that iterates along the Shape boundary and
     * provides access to a flattened view of the Shape outline geometry.
     */
    public PathIterator getPathIterator (AffineTransform at, double flatness) 
    {
        return path.getPathIterator (at, flatness);

    } // getPathIterator


    /**************************************************************
     * Tests if the interior of the Shape intersects the interior of a
     * specified rectangular area.
     */
    public boolean intersects (double x, double y, double w, double h) 
    {
        return path.intersects (x, y, w, h);

    } // intersects


    /**************************************************************
     * Tests if the interior of the Shape intersects the interior of a
     * specified Rectangle2D.
     */
    public  boolean intersects (Rectangle2D r) 
    {
        return path.intersects (r);

    } // intersects


    /**************************************************************
     * Print the vertices of the polygon.
     */
    public void print ()
    {
        System.out.print ("Vertices = { ");
        for (int i = 0; i < nVertices; i++) {
            if (i > 0) {
                System.out.print ("\t");
            }; // if
            System.out.print ("\t" + vertex [i]);
            if (i < nVertices - 1) {
                System.out.println (",");
            }; // if
        }; // for
        System.out.println (" }");

    } // print



} // Polygon class
