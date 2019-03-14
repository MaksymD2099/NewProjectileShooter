/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectileshooter;

/**
 *
 * @author cstuser
 */
public class Enemy extends GameObject{    
    public Enemy(Vector position, Vector velocity, Vector acceleration, double height, double width)
    {
        super(position, velocity, acceleration, height, width);        
    }
}
