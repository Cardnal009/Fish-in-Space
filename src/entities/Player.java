package entities;

import controller.GameController;
import controller.PowerUpManager;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Player extends Entity {


    private boolean isMovingLeft;
    private boolean isMovingRight;
    private double moveX;
    private long lastBullet;
    private PowerUpManager powerUpManager;

    /**
     * Constructor providing default player image
     * @param x
     * @param y
     * @param width
     * @param height
     * @param powerUpManager
     */
    public Player(double x, double y, int width, int height, PowerUpManager powerUpManager) {
        super(x, y, width, height, new Image("assets/invaders/invader.png"));
        this.powerUpManager = powerUpManager;
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.drawImage(getImage(), getX(), getY());
    }

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

    public boolean isMovingLeft() {
        return isMovingLeft;
    }

    public void setMovingLeft(boolean movingLeft) {
        isMovingLeft = movingLeft;
    }

    public boolean isMovingRight() {
        return isMovingRight;
    }

    public void setMovingRight(boolean movingRight) {
        isMovingRight = movingRight;
    }

    public void setMoveX(double moveX) {
        this.moveX = moveX;
    }

    public long getLastBullet() {
        return lastBullet;
    }

    public void setLastBullet(long bulletTimer) {
        this.lastBullet = bulletTimer;
    }

    public PowerUpManager getPowerUpManager() {
        return powerUpManager;
    }

    public boolean takeDamage() {
        // Returns false if shield absorbed the hit, true if player takes damage
        return !powerUpManager.useShield();
    }

}
