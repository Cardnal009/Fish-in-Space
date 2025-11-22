package entities;

import controller.GameController;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Invader extends Entity {

    private boolean invertImage = true;

    public Invader(double x, double y, int width, int height, Image image) {
        super(x, y, width, height, image);
    }

    public void draw(GraphicsContext gc) {
        if (invertImage) {
            gc.save(); // save current state
            gc.scale(-1,1); // scale to invert
            gc.drawImage(getImage(), -getX() - getWidth(), getY(), getWidth(), getHeight()); // draw at corrected inverted position
            gc.restore(); // restore to previously saved state so next image is not wrongly inverted/uninverted
        } else {
            gc.drawImage(getImage(), getX(), getY(), getWidth(), getHeight()); // regular render
        }
    }

    @Override
    public void update(double delta) {

        if (getX() <= 0) {
            setX(0);
        }
        if (getX() > GameController.WIDTH - 32) {
            setX(GameController.WIDTH - 32);
        }
    }

    public void setInvertImage(boolean invertImage) {
        this.invertImage = invertImage;
    }


}
