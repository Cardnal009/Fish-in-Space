package controller;

import entities.Invader;
import javafx.scene.image.Image;


public class InvaderController {

    private Invader[][] invaderList;
    private int rowsNum;
    private int colsNum;
    private Direction direction = Direction.LEFT;
    private static final int XSTEP = 20;

    private enum Direction {
        LEFT, RIGHT
    }

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

    private int getLeftmostColumn() {
        for (int column = 0; column < colsNum; column++) {
            for (int row = 0; row < rowsNum; row++) {
                if (invaderList[row][column].isAlive())
                    return column;
            }
        }
        return -1; // if - 1 game is won
    }

    private int getRightMostColumn() {
        for (int column = colsNum-1; column >= 0; column--) {
            for (int row = 0; row < rowsNum; row++) {
                if (invaderList[row][column].isAlive())
                    return column;
            }
        }
        return -1; // if - 1 game is won
    }

    public void updateAll() {
        if (getLeftmostColumn() == -1 || getRightMostColumn() == -1) {
            return; // TODO: add game ending logic
        }

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

    public int getTotalInvaders() {
        return rowsNum * colsNum;
    }

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
