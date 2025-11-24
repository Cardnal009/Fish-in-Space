
package entities;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 * Base class for all game objects with position, size, image, and collision logic
 */
public abstract class Entity {

    private double x;
    private double y;
    private int width;
    private int height;
    private Image image;
    private boolean alive = true;

    /**
     * Creates a basic entity.
     *
     * @param x      starting x-position
     * @param y      starting y-position
     * @param width  entity width
     * @param height entity height
     * @param image  sprite image
     */
    public Entity(double x, double y, int width, int height, Image image) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.image = image;
    }

    /**
     * Draws the entity
     *
     * @param gc graphics context for drawing
     */
    public abstract void draw(GraphicsContext gc);


    /**
     * Updates entity logic
     *
     * @param delta time since last update in seconds
     */
    public abstract void update(double delta);

    /**
     * Checks if this entity intersects another
     *
     * @param other entity to compare with
     * @return true if bounding boxes overlap
     */
    public boolean intersects(Entity other) {
        return x < other.x + other.width &&
                x + width > other.x &&
                y < other.y + other.height &&
                y + height > other.y && isAlive() && other.isAlive();
    }



    // getters and setters

    /**
     * @return x-position of the entity
     */
    public double getX() {
        return x;
    }

    /**
     * @param x new x-position
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * @return y-position of the entity
     */
    public double getY() {
        return y;
    }

    /**
     * @param y new y-position
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * @return entity width
     */
    public int getWidth() {
        return width;
    }

    /**
     * @param width new width
     */
    public void setWidth(int width) {
        this.width = width;
    }

     /**
     * @return entity height
     */
    public int getHeight() {
        return height;
    }

    /**
     * @param height new height
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * @return image used to draw the entity
     */
    public Image getImage() {
        return image;
    }

    /**
     * @param image new sprite image
     */
    public void setImage(Image image) {
        this.image = image;
    }

    /**
     * @return true if the entity is alive
     */
    public boolean isAlive() {
        return alive;
    }

    /**
     * @param alive whether the entity is alive
     */
    public void setAlive(boolean alive) {
        this.alive = alive;
    }
}
