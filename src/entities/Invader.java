
package entities;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Invader extends Entity {

    private boolean invertImage = true; // checks if invader image needs to be inverted
    private boolean hasPassedPlayer = false; // check for if the invader has went below the player

    /**
     * Constructor with all arguments
     * @param x invader x
     * @param y invader y
     * @param width invader width
     * @param height invader height
     * @param image image of invader
     */
    public Invader(double x, double y, int width, int height, Image image) {
        super(x, y, width, height, image);
    }

    /**
     * Draws the invader
     * @param gc GraphicsContext
     */
    @Override
    public void draw(GraphicsContext gc) {
        if (invertImage) { // if invader needs to be inverted
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
    }

    /**
     *
     * @param invertImage
     */
    public void setInvertImage(boolean invertImage) {
        this.invertImage = invertImage;
    }

    /**
     *
     * @return boolean
     */
    public boolean hasPassedPlayer() {
        return hasPassedPlayer;
    }

    /**
     *
     * @param hasPassedPlayer
     */
    public void setHasPassedPlayer(boolean hasPassedPlayer) {
        this.hasPassedPlayer = hasPassedPlayer;
    }

}
