
package controller;

import entities.Invader;
import javafx.scene.image.Image;

/**
 * Controls spawning, positioning, and movement of all invaders in the game
 */
public class InvaderController {

    private Invader[][] invaderList;
    private int rowsNum;
    private int colsNum;
    private Direction direction = Direction.LEFT;
    private static final int XSTEP = 20;

    private enum Direction {
        LEFT, RIGHT
    }
    
    /**
     * Creates and positions invaders in rows and columns
     *
     * @param rows number of rows of invaders
     * @param cols number of columns of invaders
     */
    public void spawnInvaders(int rows, int cols) {
        this.rowsNum = rows;
        this.colsNum = cols;
        invaderList = new Invader[rowsNum][colsNum];
        double offset = (double) (GameController.WIDTH - (40 * invaderList[0].length)) / 2;
        for (int row = 0; row < rowsNum; row++) {
            for (int column = 0; column < invaderList[row].length; column++) {
                invaderList[row][column] = new Invader(offset + column * 40, row * 40, 32, 32, new Image("assets/invaders/fishvader.png"));
            }
        }
    }
    
    /**
     * Gets the index of the leftmost column that still has a living invader
     *
     * @return column index or -1 if none alive
     */
    private int getLeftmostColumn() {
        for (int column = 0; column < colsNum; column++) {
            for (int row = 0; row < rowsNum; row++) {
                if (invaderList[row][column].isAlive())
                    return column;
            }
        }
        return -1; // if - 1 game is won
    }

    /**
     * Gets the index of the rightmost column that still has a living invader
     *
     * @return column index or -1 if none alive
     */
    private int getRightMostColumn() {
        for (int column = colsNum-1; column >= 0; column--) {
            for (int row = 0; row < rowsNum; row++) {
                if (invaderList[row][column].isAlive())
                    return column;
            }
        }
        return -1; // if - 1 game is won
    }

    /**
     * Updates all invaders, moves them left or right and down when hitting borders of game
     */
    public void updateAll() {

        int leftMostColumn = getLeftmostColumn();
        int rightMostColumn = getRightMostColumn();
        boolean hitLeftWall = false;
        boolean hitRightWall = false;


        if (direction == Direction.LEFT) {
            for (int row = 0; row < rowsNum; row++) {
                if (invaderList[row][leftMostColumn].getX() - XSTEP < 0) {
                    hitLeftWall = true;
                    break;
                }
            }
        }

        if (direction == Direction.RIGHT) {
            for (int row = 0; row < rowsNum; row++) {
                if (invaderList[row][rightMostColumn].getX() + XSTEP > GameController.WIDTH - invaderList[row][rightMostColumn].getWidth()) {
                    hitRightWall = true;
                    break;
                }
            }
        }

        if (hitLeftWall || hitRightWall) {
            direction = direction == Direction.LEFT ? Direction.RIGHT : Direction.LEFT;
            for (Invader[] invaderRow : invaderList) {
                for (Invader invader: invaderRow) {
                    invader.setY(invader.getY() + 30); // move invaders down once they hit the wall
                }
            }
            return;
        }

        int xChange = direction == Direction.LEFT ? -XSTEP : XSTEP;

        for (Invader[] invaderRow : invaderList) {
            for (Invader invader: invaderRow) {
                invader.setX(invader.getX() + xChange);
                invader.setInvertImage(direction == Direction.LEFT);
            }
        }
    }
    
    /**
     * @return total number of invaders including dead ones
     */
    public int getTotalInvaders() {
        return rowsNum * colsNum;
    }
    
    /**
     * Counts how many invaders are still alive
     *
     * @return number of alive invaders
     */
    public int countAlive() {
        int alive = 0;
        for (Invader[] invaderRow : invaderList) {
            for (Invader invader : invaderRow) {
                if (invader.isAlive())
                    alive++;
            }
        }
        return alive;
    }
    
     /**
     * Calculates how fast invaders should update,
     * speeding up as fewer invaders remain
     *
     * @return update interval in seconds
     */
    public double calculateUpdateTime() {
        int invadersAlive = countAlive();
        int totalInvaders = getTotalInvaders();
        double baseRate = .6;
        double updateRate = baseRate * ((double) invadersAlive / totalInvaders);

        return Math.max(updateRate, 0.1);
    }

    // getters and setters

    public Invader[][] getInvaderList() {
        return invaderList;
    }

    public void setInvaderList(Invader[][] invaderList) {
        this.invaderList = invaderList;
    }

}
