/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectileshooter;

import javafx.scene.shape.Rectangle;

/**
 *
 * @author cstuser
 */
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import projectileshooter.Vector;

public class GameObject {
    public  Rectangle rectangle;
    public Circle circle;
    public Vector position;
    public Vector velocity;
    public Vector acceleration;
    public double height;
    public double width;   
    public double radius;
    public String type;
    
    //Circle constructor    
    public GameObject(Vector position, Vector velocity, Vector acceleration, double radius, String type)
    {
        this.position = position;
        this.velocity = velocity;
        this.acceleration = acceleration; 
        this.radius = radius;     
        this.type = type;
        
        circle = new Circle(radius);       
        circle.setCenterX(position.getX());
        circle.setCenterY(position.getY());    
    }   
    
    
    //Rectangle constructor
    public GameObject(Vector position, Vector velocity, Vector acceleration, double height, double width)
    {
        this.position = position;
        this.velocity = velocity;
        this.acceleration = acceleration; 
        this.height = height;
        this.width = width;
              
        rectangle = new Rectangle(height, width);
        rectangle.setX(position.getX());
        rectangle.setY(position.getY());
        //rectangle.setLayoutX(-width*0.5f);
        //rectangle.setLayoutY(height*0.5f);
    }
    
    //Constructor for Barrier and Gun (rectangle without velocity or acceleration)
    public GameObject(Vector position, double height, double width, String type)
    {
        this.position = position;
        this.height = height;
        this.width = width;
        
        rectangle = new Rectangle(height, width);
        rectangle.setX(position.getX());
        rectangle.setY(position.getY());
    }
    
    //Constructor for wall
    public GameObject(Vector position, double height, double width)
    {
        this.position = position;
        this.height = height;
        this.width = width;
        
        rectangle = new Rectangle(height, width);
        rectangle.setX(position.getX());
        rectangle.setY(position.getY());
    }
    
    public Rectangle getRectangle()
    {
        return rectangle;
    }
    
    public Circle getCircle()
    {
        return circle;
    }
    
    
    public void updateCircle(double dt) throws Exception
    {
        try{
        // Euler Integration
        // Update velocity
        Vector frameAcceleration = getAcceleration().mult(dt);
        velocity = getVelocity().add(frameAcceleration);

        // Update position
        position = getPosition().add(getVelocity().mult(dt));
        /*rectangle.setX(getPosition().getX());
        rectangle.setY(getPosition().getY());
        */
        circle.setCenterX(getPosition().getX());
        circle.setCenterY(getPosition().getY());
        }
        catch (Exception e){}
    }
    
    public void updateRectangle(double dt) throws Exception
    {
        try{
        // Euler Integration
        // Update velocity
        Vector frameAcceleration = getAcceleration().mult(dt);
        velocity = getVelocity().add(frameAcceleration);

        // Update position
        position = getPosition().add(getVelocity().mult(dt));
        rectangle.setX(getPosition().getX());
        rectangle.setY(getPosition().getY());
        
        
        }
        catch (Exception e){}
    }
    
    
    
    //Getters and Setters for Class Variables
    
    //Position
    public Vector getPosition() {
        return position;
    }

    public void setPosition(Vector position) {
        this.position = position;
    }
    //Velocity
    public Vector getVelocity() {
        return velocity;
    }
    
    public void setVelocity(Vector velocity){
        this.velocity = velocity;
    }
    
    //Acceleration
    public Vector getAcceleration() {
        return acceleration;
    }    
    
    public void setAcceleration(Vector acceleration) {
        this.acceleration = acceleration;
    }
    
    //ProjectileType
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    
    //Get rectangle Height
    public double getRectangleHeight()
    {
        height = rectangle.getHeight();      
        return height;
    }
    
    //Get rectangle width
    public double getRectangleWidth()
    {
          width = rectangle.getWidth();          
          return width;
    }
    
    //Get circle radius
    public double getRadius(){
        radius = circle.getRadius();
        return radius;
    }    
    
    public Rectangle getRectangleCooridinates()
    {
        return new Rectangle(position.getX(), position.getY(), getRectangleHeight() , getRectangleWidth());
    }
    
    public Circle getCircleCooridinates()
    {
        return new Circle(circle.getCenterX(), circle.getCenterY(), circle.getRadius());
    }
}
