package controller;

import entities.Bullet;
import entities.Entity;
import entities.Invader;
import entities.Player;
import javafx.animation.AnimationTimer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.ArrayList;

public class GameController {

    public static final int WIDTH = 440; // canvas width
    public static final int HEIGHT = 500; // canvas height

    private GraphicsContext gc; // GraphicsContext used for rendering
    private AnimationTimer gameLoop; // AnimationTimer used for main game loop
    private Player player; // the player
    private double invaderUpdateTimer = 0;
    private InvaderController invaderController;
    ArrayList<Bullet> bulletList = new ArrayList<>();
    private int currentLevel = 1;
    private boolean levelComplete = false;
    private double levelCompleteTimer = 0;
    private double LEVEL_COMPLETE_DELAY = 2.0;
    private boolean gameOver = false; // created gameOver field for when invaders get to the bottom of the screen

    private final Image background = new Image("assets/background.png"); // more backgrounds can be used or added just add to resource folder and change name

    @FXML
    public AnchorPane anchorPane;

    @FXML
    public Canvas canvas;

    /**
     * Setting up everything required for the game to function
     */
    public void initialize() {
        canvas.setFocusTraversable(true);
        setListeners();
        gc = canvas.getGraphicsContext2D();
        player = new Player(canvas.getWidth() / 2 - 16, canvas.getHeight() - 32, 32, 32); // player width centered and subtract half the size to center, player height - 45 to leave white space from bottom, player size 32 by 32\
        invaderController = new InvaderController();
        invaderController.spawnInvaders(4, 5);
        startGameLoop();
    }

    /**
     * Starts the main game loop using the AnimationTimer abstract class with a target rate of 60 fps
     * calls update and render method once per frame
     */
    private void startGameLoop() {
        gameLoop = new AnimationTimer() {
            long lastTime = System.nanoTime();
            @Override
            public void handle(long now) {
                if (lastTime > 0) {
                    double delta = (double) (now - lastTime) / 1000000000; // convert to seconds (should be 1 frame per second 60fps)
                    update(delta);   // update game logic
                    render();   // draw everything
                }
                lastTime = now;
            }
        };
        gameLoop.start();
    }

    /**
     * Update entity movement, check for collisions
     * @param delta time in seconds since the last update
     */
    private void update(double delta) {
        
        if (gameOver) {
            return; // Doesn't update after game is over
        }
        
        if (levelComplete) {
            levelCompleteTimer += delta;
            if (levelCompleteTimer >= LEVEL_COMPLETE_DELAY) {
                startNewLevel();
            }
            return;
        }

        player.update(delta);
        ArrayList<Bullet> bulletsToRemove = new ArrayList<>();
        for (Bullet bullet : bulletList) {
            if (!bullet.isAlive()) {
                bulletsToRemove.add(bullet);
            } else {
                bullet.update(delta);
            }
        }
        bulletList.removeAll(bulletsToRemove);
        invaderUpdateTimer += delta;

        double stepTimer = invaderController.calculateUpdateTime();
        if (invaderUpdateTimer > stepTimer) {
            invaderUpdateTimer -= stepTimer;
            invaderController.updateAll();
        }


        for (Bullet bullet : bulletList) {
            for (Invader[] invaderRow : invaderController.getInvaderList()) {
                for (Invader invader : invaderRow) {
                    if (bullet.intersects(invader) && invader.isAlive()) {
                        invader.setAlive(false);
                        bullet.setAlive(false);
                    }
                }
            }
        }
        
        // Added a check for if invaders have reached the players position
        if (checkGameOver()) {
            gameOver = true;
            gameLoop.stop();
            System.out.println("GAME OVER");
            return;
        }

        if (checkAllInvadersDead()) {
            levelComplete = true;
            levelCompleteTimer = 0;
        }
    }

    /**
     * Draws the game screen
     */
    private void render() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.drawImage(background, 0, 0, canvas.getWidth(), canvas.getHeight());
        player.draw(gc);
        for (Bullet bullet : bulletList) {
            bullet.draw(gc);
        }
        for (Invader[] invaderRow : invaderController.getInvaderList()) {
            for (Invader invader : invaderRow) {
                if (invader.isAlive()) {
                    invader.draw(gc);
                }
            }
        }

        if (levelComplete) {
            gc.setFill(Color.YELLOW);
            gc.setFont(new Font("Arial", 40));
            gc.fillText("Level Complete", canvas.getWidth() / 2 - 140, canvas.getHeight() / 2);
        }
        
        // Added a display for when the game is over
        if (gameOver) {
            gc.setFill(Color.RED);
            gc.setFont(new Font("Arial", 40));
            gc.fillText("GAME OVER", canvas.getWidth() / 2 - 120, canvas.getHeight() / 2);
        }
    }

    /**
     * Scales and centers the game when the application is resized
     */
    public void scale() {
            double rootW = anchorPane.getWidth();
            double rootH = anchorPane.getHeight();

            double scaleX = rootW / canvas.getWidth();
            double scaleY = rootH / canvas.getHeight();
            double scale = Math.min(scaleX, scaleY);

            canvas.setScaleX(scale);
            canvas.setScaleY(scale);

            double scaledW = canvas.getWidth() * scale;
            double scaledH = canvas.getHeight() * scale;

            double canvasX = (scaledW - canvas.getWidth()) / 2;
            double canvasY = (scaledH - canvas.getHeight()) / 2;

            canvas.setLayoutX(canvasX + ((rootW - scaledW)) / 2);
            canvas.setLayoutY(canvasY + ((rootH - scaledH)) / 2);
    }

    private void fireProjectile(Entity entity) {
        Bullet bullet = null;
        if (entity instanceof Player) {
            if (System.currentTimeMillis() - player.getLastBullet() < 800) { // player can only fire ever .8 seconds
                return;
            }
            player.setLastBullet(System.currentTimeMillis());
            bullet = new Bullet(player.getX() + ((double) player.getWidth() / 2), player.getY(), 3, 8, null, entity);
            bullet.setMoveY(-120);
        }
        if (entity instanceof Invader) {
            bullet = new Bullet(entity.getX() + ((double) player.getWidth() / 2), entity.getY(), 3, 8, null, entity);
            bullet.setMoveY(+120);
        }

        bulletList.add(bullet);
    }

    /**
     * Set up listeners for scaling and key events
     */
    private void setListeners() {
        anchorPane.heightProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldNumber, Number newNumber) {
                scale();
            }
        });

        anchorPane.widthProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldNumber, Number newNumber) {
                scale();
            }
        });


        canvas.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                switch (keyEvent.getCode()) {
                    case LEFT:
                        player.setMoveX(-150);
                        player.setMovingLeft(true);
                        break;

                    case RIGHT:
                        player.setMoveX(150);
                        player.setMovingRight(true);
                        break;

                    case SPACE:
                        fireProjectile(player);
                        break;

                    case ESCAPE:
                        break;
                }
            }
        });

        canvas.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                switch (keyEvent.getCode()) {
                    case LEFT:
                        if (!player.isMovingRight()) {
                            player.setMoveX(0);
                        }
                        player.setMovingLeft(false);
                        break;

                    case RIGHT:
                        if (!player.isMovingLeft()) {
                            player.setMoveX(0);
                        }
                        player.setMovingRight(false);
                        break;

                    case SPACE:
                        break;

                    case ESCAPE:
                        break;
                }
            }
        });

    }

    // Added a method to check if the game is over
    private boolean checkGameOver() {
        // Returns true for game over if invaders get to players Y position
        for (Invader[] invaderRow : invaderController.getInvaderList()) {
            for (Invader invader : invaderRow) {
                if (invader.isAlive()) {
                    // Checks the position of invaders if they are alive
                    if (invader.getY() + invader.getHeight() >= player.getY()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean checkAllInvadersDead() {
        for (Invader[] invaderRow : invaderController.getInvaderList()) {
            for (Invader invader : invaderRow) {
                if (invader.isAlive()) {
                    return false;
                }
            }
        }
        return true;
    }

    private void startNewLevel() {
        currentLevel++;
        levelComplete = false;
        levelCompleteTimer = 0;
        bulletList.clear();
        
        int rows = 4 + (currentLevel - 1);
        int cols = 5 + (currentLevel - 1);
        invaderController.spawnInvaders(rows, cols);
    }

}
