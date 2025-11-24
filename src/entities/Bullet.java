
package entities;

import controller.GameController;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 * Represents a projectile fired by either the player or an enemy.
 */
public class Bullet extends Entity {

    private double moveY; // used to move Y value of bullet in update
    private double moveX = 0; // horizontal movement for spread patterns
    private Image bulletImage;
    private Entity owner;

    /**
     * Creates a bullet entity.
     *
     * @param x      starting x-position
     * @param y      starting y-position
     * @param width  bullet width
     * @param height bullet height
     * @param image  bullet sprite 
     * @param owner  entity that fired the bullet
     */
    public Bullet(double x, double y, int width, int height, Image image, Entity owner) {
        super(x, y, width, height, image);
        this.bulletImage = image;
        this.owner = owner;
    }


    /**
     * Draws the bullet on the screen
     *
     * @param gc graphics context used to draw
     */
    @Override
    public void draw(GraphicsContext gc) {
        if (isAlive()) {
            if (bulletImage == null) {
                gc.setFill(Color.WHITE);
                gc.fillRect(getX(), getY(), getWidth(), getHeight());
            } else {
                gc.drawImage(bulletImage, getX(), getY(), getWidth(), getHeight());
            }

        }
    }

    /**
     * Updates bullet movement and marks it dead if out of bounds
     *
     * @param delta time in seconds since last update
     */
    @Override
    public void update(double delta) {
        setY(getY() + (moveY * delta));
        setX(getX() + (moveX * delta));
        if (getY() <= 0) {
            setAlive(false);
        }
        if (getY() > GameController.HEIGHT) {
            setAlive(false);
        }
    }

    //getters and setters
    /**
     * @return vertical movement speed
     */
    public double getMoveY() {
        return moveY;
    }

    /**
     * @param moveY vertical movement speed
     */
    public void setMoveY(double moveY) {
        this.moveY = moveY;
    }

    /**
     * @return horizontal movement speed
     */
    public double getMoveX() {
        return moveX;
    }

    /**
     * @param moveX horizontal movement speed
     */
    public void setMoveX(double moveX) {
        this.moveX = moveX;
    }

    /**
     * @return entity that fired this bullet
     */
    public Entity getOwner() {
        return owner;
    }
}
