/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectileshooter;

public class Vector {
      private double x;
      private double y;
      
       public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }
       
    // Accessors and Mutators
    public double getX(){ return x; }
    public double getY(){ return y; }
    
    public void  set(Vector other) { x = other.x; y=other.y;}
    public void  setX(double value){ x=value; }
    public void  setY(double value){ y=value; }
    
    // Operations
    public Vector add(Vector other) {
        return new Vector(x + other.x, y + other.y);
    }

    public Vector sub(Vector other) {
        return new Vector(x - other.x, y - other.y);
    }

    public Vector mult(double multiplier) {
        return new Vector(x * multiplier, y * multiplier);
    }
    
    public double magnitude()
    {
        return Math.sqrt(this.dot(this));
    }
    
    public double dot(Vector other){
        return x*other.x + y*other.y;
    }
    
    public void normalize()
    {
        this.set(this.mult(1.0/this.magnitude()));
    }
}

