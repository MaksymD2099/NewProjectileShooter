/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectileshooter;

import java.awt.Point;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author cstuser
 */
public class FXMLDocumentController implements Initializable {

    private double lastFrameTime = 0.0;

    @FXML
    private AnchorPane pane;

    @FXML
    private Label coordinates;

    @FXML
    private ImageView gunImage;

    @FXML
    private ImageView character;

    @FXML
    private RadioButton radioButtonFireGun;
    @FXML
    private RadioButton radioButtonIceGun;
    @FXML
    private RadioButton radioButtonAGGun;
    @FXML
    private RadioButton radioButtonPortalGun;

    @FXML
    private Button buttonClosePortals;
    @FXML
    private Button buttonCloseAntiGravity;
    
    //@FXML
    //private Button buttonRemoveProjectiles;

    //Local Variables
    ArrayList<Double> values = new ArrayList<Double>();
    public ArrayList<GameObject> objectList = new ArrayList<>();
    private ArrayList<Projectile> arrayListProjectiles = new ArrayList<>();

    private int counterProjectiles;
    private int counterBounces;

    //rectangles for detecting collisions with the edges of the game environment
    private Edge edgeRoof;
    private Edge edgeFloor;
    private Edge edgeLeftWall;
    private Edge edgeRightWall;

    private AntiGravity agLeftSide;
    private AntiGravity agRightSide;

    Bounds boundPortalIn;
    Bounds boundPortalOut;

    //Boolean to check if the projectile is within the bounds of an antigravity region
    private boolean isWithinAntiGravity = false;

    private Portal portalIn;
    private Portal portalOut;

    public boolean portalInIsActive = false;
    public boolean portalOutIsActive = false;

    public boolean portalInIsRoof = false;
    public boolean portalOutIsRoof = false;

    public boolean portalInIsFloor = false;
    public boolean portalOutIsFloor = false;

    public boolean portalInIsLeftWall = false;
    public boolean portalOutIsLeftWall = false;

    public boolean portalInIsRightWall = false;
    public boolean portalOutIsRightWall = false;

    public boolean antiGravityIsActive = false;

    public Point mouseAim;
    public Point gunPivot;

    //used in the calculation of the angle of the gun and the projectile initial velocity
    double theta;

    //Variables for the mouse's position within the game environment
    private double mouseX;
    private double mouseY;

    private Vector acceleration;
    private Projectile projectile;

    //Final variables
    private final double PROJECTILE_RADIUS = 10; // Radius of the projectiles
    private final double PROJECTILE_VELOCITY = 500; //Magnitude of the projectile's velocity
    private final double GRAVITY = 50; //Magnitude of gravity

    private final int MAX_NUMBER_OF_BOUNCES = 4;

    //Values used by portals and antigravity
    private final double PORTAL_WIDTH = 30;
    private final double PORTAL_HEIGHT = 100;
    private final double AG_BORDERTHICKNESS = 40;
    private final double AG_DELTA_X = 150;

    private final double PORTAL_DELTA = 50;

    //Values that are used for corrections/compensations
    private final double ROTATION_PORTAL_ROOF = 270;
    private final double CORRECTION_PORTAL = -40;

    private final double ROTATION_PORTAL_LEFT = 180;

    private final double ROTATION_PORTAL_FLOOR = 90;

    //Values used for the location  of the character's shoulder for shooting location
    private final double SHOULDER_X = 100;
    private final double SHOULDER_Y = 750;
    
    private final double GUN_PIVOT_X = 100;
    private final double GUN_PIVOT_Y = 100;

    public void addToPane(Node node) {
        pane.getChildren().add(node);
    }

    public void removeFromPane(Node node) {
        pane.getChildren().remove(node);
    }

    @FXML
    public void mouseMoved(MouseEvent event) {
        mouseX = event.getSceneX();
        mouseY = (pane.getHeight() - event.getSceneY());

        mouseAim = new Point((int) mouseX, (int) mouseY);
        gunRotationAngle(mouseAim, gunPivot);

        coordinates.setText("MouseX: " + mouseX + "MouseY: " + mouseY);
    }

    @FXML
    public void mouseClicked(MouseEvent event) {

        acceleration = new Vector(0, GRAVITY);

        //Checks if the projectile is within the bounds of an antigravity region and inverts the gravity if it is. ---------------PROBABLY REMOVE---------------------
        if (isWithinAntiGravity) {
            acceleration = new Vector(0, -GRAVITY);
        }

        //Supplies the method with values for the mouse's "x" and "y" coordinates
        mouseX = event.getSceneX();
        mouseY = (pane.getHeight() - event.getSceneY());

        //---------Changes the type of projectile depending on the gun--------
        if (radioButtonFireGun.isSelected()) {
            projectile = new Projectile(new Vector(SHOULDER_X, SHOULDER_Y), new Vector(Math.cos(theta) * PROJECTILE_VELOCITY, -Math.sin(theta) * PROJECTILE_VELOCITY), acceleration, PROJECTILE_RADIUS, "fire");
        }

        if (radioButtonIceGun.isSelected()) {
            projectile = new Projectile(new Vector(SHOULDER_X, SHOULDER_Y), new Vector(Math.cos(theta) * PROJECTILE_VELOCITY, -Math.sin(theta) * PROJECTILE_VELOCITY), acceleration, PROJECTILE_RADIUS, "ice");
        }

        if (radioButtonAGGun.isSelected()) {
            if (antiGravityIsActive == true) {
                closeAntiGravity();
                projectile = new Projectile(new Vector(SHOULDER_X, SHOULDER_Y), new Vector(Math.cos(theta) * PROJECTILE_VELOCITY, -Math.sin(theta) * PROJECTILE_VELOCITY), acceleration, PROJECTILE_RADIUS, "antigravity");
            } else {
                projectile = new Projectile(new Vector(SHOULDER_X, SHOULDER_Y), new Vector(Math.cos(theta) * PROJECTILE_VELOCITY, -Math.sin(theta) * PROJECTILE_VELOCITY), acceleration, PROJECTILE_RADIUS, "antigravity");
            }

        }

        if (radioButtonPortalGun.isSelected()) {

            if (portalInIsActive && portalOutIsActive) {
                closePortal();
                projectile = new Projectile(new Vector(SHOULDER_X, SHOULDER_Y), new Vector(Math.cos(theta) * PROJECTILE_VELOCITY, -Math.sin(theta) * PROJECTILE_VELOCITY), acceleration, PROJECTILE_RADIUS, "portalIn");
            }
            if (portalInIsActive == false) {
                projectile = new Projectile(new Vector(SHOULDER_X, SHOULDER_Y), new Vector(Math.cos(theta) * PROJECTILE_VELOCITY, -Math.sin(theta) * PROJECTILE_VELOCITY), acceleration, PROJECTILE_RADIUS, "portalIn");
            } else {
                projectile = new Projectile(new Vector(SHOULDER_X, SHOULDER_Y), new Vector(Math.cos(theta) * PROJECTILE_VELOCITY, -Math.sin(theta) * PROJECTILE_VELOCITY), acceleration, PROJECTILE_RADIUS, "portalOut");
            }
        }

        //This ensures that only one projectile can exist at one time and if it can exist, it adds it to the scene
        if (arrayListProjectiles.isEmpty()) {
            addToPane(projectile.getCircle());
            objectList.add(projectile);
            arrayListProjectiles.add(projectile);
        }
    }

    public void gunRotationAngle(Point mouseAim, Point gunPivot) {
        theta = Math.atan2(mouseAim.y - gunPivot.y, mouseAim.x - gunPivot.x);

        double angle = Math.toDegrees(theta);
        gunImage.setRotate(-angle);
    }

    //----------PORTAL AND ANTIGRAVITY BEHAVIOR METHODS------------
    //PORTALS
    public void openPortal() {
        if (portalInIsActive == false) {            
            portalIn = new Portal(new Vector(projectile.getPosition().getX(), projectile.getPosition().getY() + CORRECTION_PORTAL), PORTAL_WIDTH, PORTAL_HEIGHT);            
            boundPortalIn = portalIn.getBounds();
            objectList.add(portalIn);
            addToPane(portalIn.getRectangle());
            portalIn.getRectangle().setFill(AssetManager.getPortalIn());
            portalInIsActive = true;
        } else {
            buttonClosePortals.setDisable(false);
            portalOut = new Portal(new Vector(projectile.getPosition().getX(), projectile.getPosition().getY() + CORRECTION_PORTAL), PORTAL_WIDTH, PORTAL_HEIGHT);            
            boundPortalOut = portalOut.getBounds();
            /*
            if(boundPortalOut.intersects(boundPortalIn))
            {
                if(portalInIsFloor)
                {
                    if(portalIn.getRectangle().getX() + PORTAL_HEIGHT >= edgeRightWall.getRectangle().getX())
                    {
                        
                    }
                }
            } */
            objectList.add(portalOut);
            addToPane(portalOut.getRectangle());
            portalOut.getRectangle().setFill(AssetManager.getPortalOut());
            portalOutIsActive = true;
        }
    }

    //Method that closes portals and should only be called when two portals are active
    public void closePortal() {
        buttonClosePortals.setDisable(true);
        portalInIsActive = false;
        portalIn.getRectangle().setFill(Color.TRANSPARENT);
        objectList.remove(portalIn);
        removeFromPane(portalIn.getRectangle());

        portalOutIsActive = false;
        portalOut.getRectangle().setFill(Color.TRANSPARENT);
        objectList.remove(portalOut);
        removeFromPane(portalOut.getRectangle());

        //Resetting booleans for which side the portals are on        
        portalInIsFloor = false;
        portalOutIsFloor = false;

        portalInIsRoof = false;
        portalOutIsRoof = false;

        portalInIsLeftWall = false;
        portalOutIsLeftWall = false;

        portalInIsRightWall = false;
        portalOutIsRightWall = false;
    }

    //Method that teleports projectile from portalIn to portalOut
    public void teleportInToOut(Projectile proj) {
        proj.setPosition(new Vector(portalOut.getPosition().getX(), portalOut.getPosition().getY()));
        //-----portalIn is on the Roof-----

        //portalOut = roof (portalIn is roof)
        if (portalInIsRoof && portalOutIsRoof) {
            proj.setVelocity(new Vector(proj.getVelocity().getX(), -proj.getVelocity().getY()));
        }

        //portalOut = floor (portalIn is roof)
        if (portalInIsRoof && portalOutIsFloor) {
            //just set the position                                    
        }

        //portalOut = LeftWall (portalIn is roof)        
        if (portalInIsRoof && portalOutIsLeftWall) {
            if (proj.getVelocity().getX() < 0) {
                proj.setVelocity(new Vector(-proj.getVelocity().getX(), proj.getVelocity().getY()));
            }
        }

        //portalOut = RightWall (portalIn is roof)
        if (portalInIsRoof && portalOutIsRightWall) {
            if (proj.getVelocity().getX() > 0) {
                proj.setVelocity(new Vector(-proj.getVelocity().getX(), proj.getVelocity().getY()));
            }
        }

        //-----portalIn is on the Floor-----
        //portalOut = roof (portalIn is floor)
        if (portalInIsFloor && portalOutIsRoof) {
            //just set the position
        }

        //portalOut = floor (portalIn is floor)
        if (portalInIsFloor && portalOutIsFloor) {
            proj.setVelocity(new Vector(proj.getVelocity().getX(), -proj.getVelocity().getY()));
        }

        //portalOut = leftWall (portalIn is floor)
        if (portalInIsFloor && portalOutIsLeftWall) {
            if (proj.getVelocity().getX() < 0) {
                proj.setVelocity(new Vector(-proj.getVelocity().getX(), proj.getVelocity().getY()));
            }
        }

        //portalOut = rightWall (portalIn is floor)
        if (portalInIsFloor && portalOutIsRightWall) {
            if (proj.getVelocity().getX() > 0) {
                proj.setVelocity(new Vector(-proj.getVelocity().getX(), proj.getVelocity().getY()));
            }
        }

        //--------portalIn is on Left Wall-------
        //portalOut = roof (portalIn is leftWall)
        if (portalInIsLeftWall && portalOutIsRoof) {
            if (proj.getVelocity().getY() < 0) {
                proj.setVelocity(new Vector(proj.getVelocity().getX(), -proj.getVelocity().getY()));
            }
        }

        //portalOut = floor (portalIn is leftWall)
        if (portalInIsLeftWall && portalOutIsFloor) {
            if (proj.getVelocity().getY() > 0) {
                proj.setVelocity(new Vector(proj.getVelocity().getX(), -proj.getVelocity().getY()));
            }
        }

        //portalOut = leftWall (portalIn is leftWall)
        if (portalInIsLeftWall && portalOutIsLeftWall) {
            proj.setVelocity(new Vector(-proj.getVelocity().getX(), proj.getVelocity().getY()));
        }

        //portalOut = rightWall (portalIn is leftWall)
        if (portalInIsLeftWall && portalOutIsRightWall) {
            //Just set the position
        }

        //--------portalIn is on Right Wall-------
        //portalOut = roof (portalIn is rightWall)
        if (portalInIsRightWall && portalOutIsRoof) {
            if (proj.getVelocity().getY() < 0) {
                proj.setVelocity(new Vector(proj.getVelocity().getX(), -proj.getVelocity().getY()));
            }
        }
        //portalOut = floor (portalIn is rightWall)
        if (portalInIsRightWall && portalOutIsFloor) {
            if (proj.getVelocity().getY() > 0) {
                proj.setVelocity(new Vector(proj.getVelocity().getX(), -proj.getVelocity().getY()));
            }
        }
        //portalOut = leftWall (portalIn is rightWall)
        if (portalInIsRightWall && portalOutIsLeftWall) {
            //just set the position
        }

        //portalOut = rightWall (portalIn is rightWall)
        if (portalInIsRightWall && portalOutIsRightWall) {
            proj.setVelocity(new Vector(-proj.getVelocity().getX(), proj.getVelocity().getY()));
        }
    }// public void teleport(Projectile proj)
    
    
    /*
    //Method that teleports a projectile from portalOut to portalIn
    public void teleportOutToIn(Projectile proj) {
        proj.setPosition(portalIn.getPosition());
        //-----portalIn is on the Roof-----

        //portalOut = roof (portalIn is roof)
        if (portalInIsRoof && portalOutIsRoof) {
            proj.setVelocity(new Vector(proj.getVelocity().getX(), -proj.getVelocity().getY()));
        }

        //portalOut = floor (portalIn is roof)
        if (portalInIsRoof && portalOutIsFloor) {
            //just set the position                                    
        }

        //portalOut = LeftWall (portalIn is roof)        
        if (portalInIsRoof && portalOutIsLeftWall) {
            if (proj.getVelocity().getX() < 0) {
                proj.setVelocity(new Vector(-proj.getVelocity().getX(), proj.getVelocity().getY()));
            }
        }

        //portalOut = RightWall (portalIn is roof)
        if (portalInIsRoof && portalOutIsRightWall) {
            if (proj.getVelocity().getX() > 0) {
                proj.setVelocity(new Vector(-proj.getVelocity().getX(), proj.getVelocity().getY()));
            }
        }

        //-----portalIn is on the Floor-----
        //portalOut = roof (portalIn is floor)
        if (portalInIsFloor && portalOutIsRoof) {
            //just set the position
        }

        //portalOut = floor (portalIn is floor)
        if (portalInIsFloor && portalOutIsFloor) {
            proj.setVelocity(new Vector(proj.getVelocity().getX(), -proj.getVelocity().getY()));
        }

        //portalOut = leftWall (portalIn is floor)
        if (portalInIsFloor && portalOutIsLeftWall) {
            if (proj.getVelocity().getX() < 0) {
                proj.setVelocity(new Vector(-proj.getVelocity().getX(), proj.getVelocity().getY()));
            }
        }

        //portalOut = rightWall (portalIn is floor)
        if (portalInIsFloor && portalOutIsRightWall) {
            if (proj.getVelocity().getX() > 0) {
                proj.setVelocity(new Vector(-proj.getVelocity().getX(), proj.getVelocity().getY()));
            }
        }

        //--------portalIn is on Left Wall-------
        //portalOut = roof (portalIn is leftWall)
        if (portalInIsLeftWall && portalOutIsRoof) {
            if (proj.getVelocity().getY() < 0) {
                proj.setVelocity(new Vector(proj.getVelocity().getX(), -proj.getVelocity().getY()));
            }
        }

        //portalOut = floor (portalIn is leftWall)
        if (portalInIsLeftWall && portalOutIsFloor) {
            if (proj.getVelocity().getY() > 0) {
                proj.setVelocity(new Vector(proj.getVelocity().getX(), -proj.getVelocity().getY()));
            }
        }

        //portalOut = leftWall (portalIn is leftWall)
        if (portalInIsLeftWall && portalOutIsLeftWall) {
            proj.setVelocity(new Vector(-proj.getVelocity().getX(), proj.getVelocity().getY()));
        }

        //portalOut = rightWall (portalIn is leftWall)
        if (portalInIsLeftWall && portalOutIsRightWall) {
            //Just set the position
        }

        //--------portalIn is on Right Wall-------
        //portalOut = roof (portalIn is rightWall)
        if (portalInIsRightWall && portalOutIsRoof) {
            if (proj.getVelocity().getY() < 0) {
                proj.setVelocity(new Vector(proj.getVelocity().getX(), -proj.getVelocity().getY()));
            }
        }
        //portalOut = floor (portalIn is rightWall)
        if (portalInIsRightWall && portalOutIsFloor) {
            if (proj.getVelocity().getY() > 0) {
                proj.setVelocity(new Vector(proj.getVelocity().getX(), -proj.getVelocity().getY()));
            }
        }
        //portalOut = leftWall (portalIn is rightWall)
        if (portalInIsRightWall && portalOutIsLeftWall) {
            //just set the position
        }

        //portalOut = rightWall (portalIn is rightWall)
        if (portalInIsRightWall && portalOutIsRightWall) {
            proj.setVelocity(new Vector(-proj.getVelocity().getX(), proj.getVelocity().getY()));
        }
    }// public void teleportOutToIn(Projectile proj)
    
    */

    //ANTIGRAVITY
    public void openAntiGravity() {
        buttonCloseAntiGravity.setDisable(false);
        antiGravityIsActive = true;
        agLeftSide = new AntiGravity(new Vector(projectile.getCircle().getCenterX() - AG_DELTA_X, 0), AG_BORDERTHICKNESS, pane.getHeight());
        agLeftSide.getRectangle().setFill(AssetManager.getAntiGravitySide());
        objectList.add(agLeftSide);
        addToPane(agLeftSide.getRectangle());

        agRightSide = new AntiGravity(new Vector(projectile.getCircle().getCenterX() + AG_DELTA_X, 0), AG_BORDERTHICKNESS, pane.getHeight());
        agRightSide.getRectangle().setFill(AssetManager.getAntiGravitySide());
        objectList.add(agRightSide);
        addToPane(agRightSide.getRectangle());
    }

    public void closeAntiGravity() {
        buttonCloseAntiGravity.setDisable(true);
        antiGravityIsActive = false;
        objectList.remove(agLeftSide);
        removeFromPane(agLeftSide.getRectangle());

        objectList.remove(agRightSide);
        removeFromPane(agRightSide.getRectangle());
    }
    
    /*
    @FXML
    public void removeProjectiles() {
        arrayListProjectiles.stream().map((proj) -> {
            removeFromPane(proj.getRectangle());
            return proj;           
        }).map((Projectile proj) -> {
            objectList.remove(proj);
            return proj;
        }).forEachOrdered((proj) -> {
            arrayListProjectiles.remove(proj);
        });
    }
*/

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lastFrameTime = 0.0f;
        long initialTime = System.nanoTime();

        AssetManager.preloadAllAssets();

        //pane.setBackground(AssetManager.getBackground());
        
        //character.setTranslateX(0);
        //character.setTranslateY(pane.getPrefHeight() - (character.getY() + character.getFitHeight()));
        System.out.println("p.getPrefHeight()" + pane.getPrefHeight() + "c.getFitHeight()" + character.getFitHeight());
        //character.setX(10);
        //character.setY(pane.getHeight() - character.getY());        
        
        ToggleGroup group = new ToggleGroup();
        radioButtonFireGun.setToggleGroup(group);
        radioButtonIceGun.setToggleGroup(group);
        radioButtonAGGun.setToggleGroup(group);
        radioButtonPortalGun.setToggleGroup(group);

        buttonCloseAntiGravity.setDisable(true);
        buttonClosePortals.setDisable(true);
        
        //buttonRemoveProjectiles.setDisable(true);
        

        //Image of MainCharacter
        character.setImage(AssetManager.getCharacterImage());

        //Image Gun
        //if(selectedGunType == "fire")
        gunImage.setImage(AssetManager.getGunFire_Img());
        radioButtonPortalGun.setSelected(true);

        gunPivot = new Point();
        gunPivot.setLocation(GUN_PIVOT_X, GUN_PIVOT_Y);

        //Creating Edge objects
        edgeFloor = new Edge(new Vector(0, pane.getPrefHeight() + 50), pane.getPrefWidth() + 50, 1);
        edgeRoof = new Edge(new Vector(0, 0), pane.getPrefWidth() + 50, 1);
        edgeLeftWall = new Edge(new Vector(0, 0), 1, pane.getPrefHeight() + 50);
        edgeRightWall = new Edge(new Vector(pane.getPrefWidth() + 50, 0), 1, pane.getPrefHeight() + 50);

        //Adding Edges to the pane so that collisions can be detected with the edge
        addToPane(edgeFloor.getRectangle());
        addToPane(edgeRoof.getRectangle());
        addToPane(edgeLeftWall.getRectangle());
        addToPane(edgeRightWall.getRectangle());

        //Adding edges to the objectList so that their existance within the program can be monitored
        objectList.add(edgeFloor);
        objectList.add(edgeRoof);
        objectList.add(edgeLeftWall);
        objectList.add(edgeRightWall);

        new AnimationTimer() {
            @Override
            public void handle(long now) {
                try {
                    double currentTime = (now - initialTime) / 1000000000.0;
                    double frameDeltaTime = currentTime - lastFrameTime;
                    lastFrameTime = currentTime;

                    for (GameObject obj : objectList) {
                        if (obj != null) {
                            obj.updateRectangle(frameDeltaTime);
                            obj.updateCircle(frameDeltaTime);
                        }
                    }
                } catch (Exception e) {}
                
                

                
                
                if (group.getSelectedToggle() == radioButtonFireGun) {
                    gunImage.setImage(AssetManager.getGunFire_Img());
                }

                if (group.getSelectedToggle() == radioButtonIceGun) {
                    gunImage.setImage(AssetManager.getGunIce_Img());
                }

                if (group.getSelectedToggle() == radioButtonAGGun) {
                    gunImage.setImage(AssetManager.getGunAntiGravity_Img());
                }

                if (group.getSelectedToggle() == radioButtonPortalGun) {
                    gunImage.setImage(AssetManager.getGunPortalIn_Img());
                }

                for (int i = 0; i < arrayListProjectiles.size(); i++) {
                    //AudioClip tempBounce = AssetManager.getBounce();  //---------------------SOUND IS BROKEN---------------
                    
                    /*if(arrayListProjectiles.isEmpty() == false)
                    {
                        buttonRemoveProjectiles.setDisable(false);
                    }
                    */
                    
                    
                    Projectile tempProjectile = arrayListProjectiles.get(i);

                    coordinates.setText("Projectile X: " + projectile.getCircle().getCenterX() + "Projectile Y: " + projectile.getCircle().getCenterY());

                    Circle projectileCircle = tempProjectile.getCircle();
                    Bounds boundProjectileCircle = projectileCircle.getBoundsInParent();

                    //SETTING BOUNDS
                    //Bound Floor
                    Rectangle rectangleEdgeFloor = edgeFloor.getRectangle();
                    Bounds boundRectangleEdgeFloor = rectangleEdgeFloor.getBoundsInParent();

                    //Bound Roof
                    Rectangle rectangleEdgeRoof = edgeRoof.getRectangle();
                    Bounds boundRectangleEdgeRoof = rectangleEdgeRoof.getBoundsInParent();

                    //Bound LeftWall
                    Rectangle rectangleEdgeLeftWall = edgeLeftWall.getRectangle();
                    Bounds boundRectangleEdgeLeftWall = rectangleEdgeLeftWall.getBoundsInParent();

                    //Bound RightWall
                    Rectangle rectangleEdgeRightWall = edgeRightWall.getRectangle();
                    Bounds boundRectangleEdgeRightWall = rectangleEdgeRightWall.getBoundsInParent();

                    //----------COLLISIONS--------
                    //FLOOR
                    if (boundProjectileCircle.intersects(boundRectangleEdgeFloor)) {

                        //Check for fire and ice (floor)
                        if (tempProjectile.getType().equalsIgnoreCase("fire") || tempProjectile.getType().equalsIgnoreCase("ice")) {
                            ++counterBounces;
                            //Moves projectile away from edge by 1 radius
                            tempProjectile.setPosition(new Vector(tempProjectile.getPosition().getX(), tempProjectile.getPosition().getY() - PROJECTILE_RADIUS));

                            //Inverts the projectile's velocity
                            tempProjectile.setVelocity(new Vector(tempProjectile.getVelocity().getX(), -tempProjectile.getVelocity().getY()));
                        }

                        //Check for antigravity (floor)
                        if (tempProjectile.getType().equalsIgnoreCase("antigravity")) {
                            openAntiGravity();

                            tempProjectile.setVelocity(new Vector(0, 0));
                            objectList.remove(tempProjectile);
                            removeFromPane(tempProjectile.getCircle());
                            arrayListProjectiles.remove(0);
                            //------TODO-------
                        }

                        //Check for portalIn (floor)
                        if (tempProjectile.getType().equalsIgnoreCase("portalIn")) {
                            openPortal();
                            
                            portalInIsFloor = true;

                            portalIn.getRectangle().setRotate(ROTATION_PORTAL_FLOOR);

                            tempProjectile.setVelocity(new Vector(0, 0));
                            objectList.remove(tempProjectile);
                            removeFromPane(tempProjectile.getCircle());
                            arrayListProjectiles.remove(0);
                        }

                        //Check for PortalOut (floor)
                        if (tempProjectile.getType().equalsIgnoreCase("portalOut")) {
                            openPortal();
                            portalOutIsFloor = true;

                            portalOut.getRectangle().setRotate(ROTATION_PORTAL_FLOOR);

                            tempProjectile.setVelocity(new Vector(0, 0));
                            objectList.remove(tempProjectile);
                            removeFromPane(tempProjectile.getCircle());
                            arrayListProjectiles.remove(0);
                        }
                    }

                    //ROOF
                    if (boundProjectileCircle.intersects(boundRectangleEdgeRoof)) {

                        //Check fire and ice (roof)
                        if (tempProjectile.getType().equalsIgnoreCase("fire") || tempProjectile.getType().equalsIgnoreCase("ice")) {
                            ++counterBounces;
                            //Moves the projectile away from edge by 1 radius
                            tempProjectile.setPosition(new Vector(tempProjectile.getPosition().getX(), tempProjectile.getPosition().getY() + PROJECTILE_RADIUS));

                            //Inverts the projectile's velocity
                            tempProjectile.setVelocity(new Vector(tempProjectile.getVelocity().getX(), -tempProjectile.getVelocity().getY()));
                        }

                        //Check for antigravity (roof)
                        if (tempProjectile.getType().equalsIgnoreCase("antigravity")) {
                            openAntiGravity();

                            tempProjectile.setVelocity(new Vector(0, 0));
                            objectList.remove(tempProjectile);
                            removeFromPane(tempProjectile.getCircle());
                            arrayListProjectiles.remove(0);
                            //------TODO-------
                        }

                        //Check for portalIn (roof)
                        if (tempProjectile.getType().equalsIgnoreCase("portalIn")) {
                            openPortal();
                            portalInIsRoof = true;

                            portalIn.getRectangle().setRotate(ROTATION_PORTAL_ROOF);

                            tempProjectile.setVelocity(new Vector(0, 0));
                            objectList.remove(tempProjectile);
                            removeFromPane(tempProjectile.getCircle());
                            arrayListProjectiles.remove(0);
                        }

                        //Check for PortalOut (roof)
                        if (tempProjectile.getType().equalsIgnoreCase("portalOut")) {
                            openPortal();
                            portalOutIsRoof = true;

                            portalOut.getRectangle().setRotate(ROTATION_PORTAL_ROOF);

                            tempProjectile.setVelocity(new Vector(0, 0));
                            objectList.remove(tempProjectile);
                            removeFromPane(tempProjectile.getCircle());
                            arrayListProjectiles.remove(0);
                        }
                    }

                    //LEFT WALL
                    if (boundProjectileCircle.intersects(boundRectangleEdgeLeftWall)) {

                        //Check for fire, ice and antigravity (left wall)
                        if (tempProjectile.getType().equalsIgnoreCase("fire") || tempProjectile.getType().equalsIgnoreCase("ice") || tempProjectile.getType().equalsIgnoreCase("antigravity")) {
                            ++counterBounces;
                            //Moves the projectile away from edge by 1 radius
                            tempProjectile.setPosition(new Vector(tempProjectile.getPosition().getX() + PROJECTILE_RADIUS, tempProjectile.getPosition().getY()));

                            //Inverts the projectile's velocity
                            tempProjectile.setVelocity(new Vector(-tempProjectile.getVelocity().getX(), tempProjectile.getVelocity().getY()));
                        }

                        //Check for portalIn (left wall)
                        if (tempProjectile.getType().equalsIgnoreCase("portalIn")) {
                            openPortal();
                            portalInIsLeftWall = true;

                            portalIn.getRectangle().setRotate(ROTATION_PORTAL_LEFT);

                            tempProjectile.setVelocity(new Vector(0, 0));
                            objectList.remove(tempProjectile);
                            removeFromPane(tempProjectile.getCircle());
                            arrayListProjectiles.remove(0);
                        }

                        //Check for PortalOut (left wall)
                        if (tempProjectile.getType().equalsIgnoreCase("portalOut")) {
                            openPortal();
                            portalOutIsLeftWall = true;

                            portalOut.getRectangle().setRotate(ROTATION_PORTAL_LEFT);

                            tempProjectile.setVelocity(new Vector(0, 0));
                            objectList.remove(tempProjectile);
                            removeFromPane(tempProjectile.getCircle());
                            arrayListProjectiles.remove(0);
                        }
                    }
                    //RIGHT WALL
                    if (boundProjectileCircle.intersects(boundRectangleEdgeRightWall)) {

                        //check for fire, ice and antigravity (right wall)
                        if (tempProjectile.getType().equalsIgnoreCase("fire") || tempProjectile.getType().equalsIgnoreCase("ice") || tempProjectile.getType().equalsIgnoreCase("antigravity")) {
                            ++counterBounces;
                            //Moves the projectile away from edge by 1 radius
                            tempProjectile.setPosition(new Vector(tempProjectile.getPosition().getX() - PROJECTILE_RADIUS, tempProjectile.getPosition().getY()));

                            //Inverts the projectile's velocity
                            tempProjectile.setVelocity(new Vector(-tempProjectile.getVelocity().getX(), tempProjectile.getVelocity().getY()));
                        }

                        //Check for portalIn (right wall)
                        if (tempProjectile.getType().equalsIgnoreCase("portalIn")) {
                            openPortal();
                            portalIn.setPosition(new Vector(portalIn.getPosition().getX() - 2 * PORTAL_WIDTH, portalIn.getPosition().getY()));
                            portalInIsRightWall = true;

                            tempProjectile.setVelocity(new Vector(0, 0));
                            objectList.remove(tempProjectile);
                            removeFromPane(tempProjectile.getCircle());
                            arrayListProjectiles.remove(0);
                        }

                        //Check for PortalOut (right wall)
                        if (tempProjectile.getType().equalsIgnoreCase("portalOut")) {
                            openPortal();
                            portalOut.setPosition(new Vector(portalOut.getPosition().getX() - 2 * PORTAL_WIDTH, portalOut.getPosition().getY()));
                            portalOutIsRightWall = true;

                            tempProjectile.setVelocity(new Vector(0, 0));
                            objectList.remove(tempProjectile);
                            removeFromPane(tempProjectile.getCircle());
                            arrayListProjectiles.remove(0);
                        }

                    }
                    //-----------TELEPORTATION-----------

                    if (portalInIsActive && portalOutIsActive) {
                        if (boundProjectileCircle.intersects(boundPortalIn)) {
                            teleportInToOut(tempProjectile);
                        }

                        if (boundProjectileCircle.intersects(boundPortalOut)) {
                            //teleportOutToIn(tempProjectile);
                        }
                    }

                    //-------------ANTIGRAVITY------------
                    if (antiGravityIsActive) {
                        if (projectileCircle.getCenterX() > agLeftSide.getRectangle().getX() && projectileCircle.getCenterX() < agRightSide.getRectangle().getX()) {
                            projectile.setAcceleration(new Vector(0, -10 * GRAVITY));
                        } else {
                            projectile.setAcceleration(new Vector(0, GRAVITY));
                        }
                    }

                    //------------Case for when the ball bounces the max number of times allowed-------
                    if (counterBounces == MAX_NUMBER_OF_BOUNCES) {
                        counterBounces = 0;
                        objectList.remove(arrayListProjectiles.get(0));
                        arrayListProjectiles.remove(0);
                        removeFromPane(tempProjectile.getCircle());
                    }
                    //tempBounce.play(); --------------THE SOUND DOESNT WORK
                    //Collision with Sides

                    projectile = tempProjectile;

                }//for (int i = 0; i < arrayListProjectiles.size(); i++)

            }//public void handle(long now)

        }.start();

    }

}
