
package entities;

import controller.GameController;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class Bullet extends Entity {

    private double moveY; // used to move Y value of bullet in update
    private Image bulletImage;
    private Entity owner;

    public Bullet(double x, double y, int width, int height, Image image, Entity owner) {
        super(x, y, width, height, image);
        this.bulletImage = image;
        this.owner = owner;
    }


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

    @Override
    public void update(double delta) {
        setY(getY() + (moveY * delta));
        if (getY() <= 0) {
            setAlive(false);
        }
        if (getY() > GameController.HEIGHT) {
            setAlive(false);
        }
    }

    //getters and setters

    public double getMoveY() {
        return moveY;
    }

    public void setMoveY(double moveY) {
        this.moveY = moveY;
    }
}
