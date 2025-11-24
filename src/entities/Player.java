
package entities;

import controller.GameController;
import controller.PowerUpManager;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Player extends Entity {


    private boolean isMovingLeft; // checks if player is moving left
    private boolean isMovingRight; // checks if player is moving rigght
    private double moveX; // player x movement
    private long lastBullet; // cooldown for when player can fire again
    private PowerUpManager powerUpManager; // placeholder for powerup manager

    /**
     * Constructor providing default player image
     * @param x x position
     * @param y y position
     * @param width player width
     * @param height player height
     * @param powerUpManager the powerup manager
     */
    public Player(double x, double y, int width, int height, PowerUpManager powerUpManager) {
        super(x, y, width, height, new Image("assets/invaders/invader.png"));
        this.powerUpManager = powerUpManager;
    }

    /**
     * Draws the player
     * @param gc GraphicsContext
     */
    @Override
    public void draw(GraphicsContext gc) {
        gc.drawImage(getImage(), getX(), getY());
    }

    /**
     * Updates player movement
     * @param delta time in seconds since the last update
     */
    @Override
    public void update(double delta) {
        setX(getX() + (moveX * delta));
        if (getX() <= 0) {
            setX(0);
        }
        if (getX() > GameController.WIDTH - getWidth()) {
            setX(GameController.WIDTH - getWidth());
        }
    }

    /**
     *
     * @return boolean
     */
    public boolean isMovingLeft() {
        return isMovingLeft;
    }

    /**
     *
     * @param movingLeft
     */
    public void setMovingLeft(boolean movingLeft) {
        isMovingLeft = movingLeft;
    }

    /**
     *
     * @return boolean
     */
    public boolean isMovingRight() {
        return isMovingRight;
    }

    /**
     *
     * @param movingRight
     */
    public void setMovingRight(boolean movingRight) {
        isMovingRight = movingRight;
    }

    /**
     *
     * @param moveX
     */
    public void setMoveX(double moveX) {
        this.moveX = moveX;
    }

    /**
     *
     * @return lastBullet
     */
    public long getLastBullet() {
        return lastBullet;
    }

    /**
     *
     * @param bulletTimer
     */
    public void setLastBullet(long bulletTimer) {
        this.lastBullet = bulletTimer;
    }

    /**
     *
     * @return PowerUpManager
     */
    public PowerUpManager getPowerUpManager() {
        return powerUpManager;
    }

    /**
     *
     * @return false if shield absorbs the hit, true if player takes damage
     */
    public boolean takeDamage() {
        // Returns false if shield absorbed the hit, true if player takes damage
        return !powerUpManager.useShield();
    }

}
