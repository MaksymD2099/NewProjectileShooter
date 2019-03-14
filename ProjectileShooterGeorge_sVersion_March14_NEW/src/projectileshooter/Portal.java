/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectileshooter;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author blues
 */
public class Portal extends GameObject{
    
    Bounds bound;    
            
    public Portal(Vector position, double width, double height)
    {
        super(position, width, height);
        {
            bound = rectangle.getBoundsInParent();
        }
    }
    public Bounds getBounds(){return bound;}
    
}
