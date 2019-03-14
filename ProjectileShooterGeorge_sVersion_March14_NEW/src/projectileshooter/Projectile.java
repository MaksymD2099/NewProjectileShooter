/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectileshooter;

import javafx.scene.shape.Circle;

/**
 *
 * @author cstuser
 */
public class Projectile extends GameObject {    
    
    public Projectile(Vector position, Vector velocity, Vector acceleration, double radius, String type)
    {
        super(position, velocity, acceleration, radius, type);
        
        //Checking what kind of projectile it is to assign the correct image        
        
        if(type.equalsIgnoreCase("fire")) {
            circle.setFill(AssetManager.getBallFire());
        }
        
        if(type.equalsIgnoreCase("ice")) {
            circle.setFill(AssetManager.getBallIce());
        }
        
        if(type.equalsIgnoreCase("antigravity")) {
            circle.setFill(AssetManager.getBallAntiGravity());            
        }
        
        if(type.equalsIgnoreCase("portalin")){
            circle.setFill(AssetManager.getBallPortalIn());
        }
        
        if(type.equalsIgnoreCase("portalout")){
            circle.setFill(AssetManager.getBallPortalOut());
        }
    }
}
