package controller;

import entities.Boss;
import entities.Bullet;
import entities.Entity;
import entities.Invader;
import entities.Player;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.ArrayList;

/**
 * Main controller for the Fish in Space game.
 * Handles initialization, game loop, updating logic, rendering, and input.
 */
public class GameController {

    public static final int WIDTH = 440; // canvas width
    public static final int HEIGHT = 500; // canvas height

    private GraphicsContext gc; // GraphicsContext used for rendering
    private AnimationTimer gameLoop; // AnimationTimer used for main game loop
    private Player player; // the player
    private double invaderUpdateTimer = 0; // time used to update invaders movement
    private InvaderController invaderController; // placeholder for InvaderController
    ArrayList<Bullet> bulletList = new ArrayList<>(); // list of bullets
    private int currentLevel = 1;
    private boolean levelComplete = false; // placeholder to check if level is complete
    private double levelCompleteTimer = 0;
    private double LEVEL_COMPLETE_DELAY = 2.0;
    private boolean gameOver = false; // created gameOver field for when invaders get to the bottom of the screen
    private PowerUpManager powerUpManager;
    private boolean startScreen = true;   // created startScreen field
    private boolean paused = false; // added field for a pause and unpause feature
    private int score = 0;
    private MediaPlayer backgroundMusic;
    private LeaderboardManager leaderboardManager = new LeaderboardManager();
    private boolean nameEntered = false;
    private Boss boss = null;
    private boolean isBossLevel = false;
    private boolean gameWon = false; // Track if player won by defeating boss


    private final Image background = new Image("assets/background.png"); // background image for game

    @FXML
    public AnchorPane anchorPane; // anchorpane used to hold canvas and keep size

    @FXML
    public Canvas canvas; // game area - sits on anchorpane

    /**
     * Initializes the game controller.
     * Sets up the canvas, listeners, player, invaders, music, and game loop.
     */
    public void initialize() {
        canvas.setFocusTraversable(true); // allows listeners to work and move objects/shapes
        setListeners();
        gc = canvas.getGraphicsContext2D(); // initialize graphics
        powerUpManager = new PowerUpManager(); //initialize powerups
        player = new Player(canvas.getWidth() / 2 - 16, canvas.getHeight() - 32, 32, 32, powerUpManager); // player width centered and subtract half the size to center, player height - 45 to leave white space from bottom, player size 32 by 32\
        invaderController = new InvaderController(); // initialize InvaderController
        invaderController.spawnInvaders(4, 5); //spawns set number of invaders rows/cols
        
        // Initialize background music
        try {
            Media media = new Media(getClass().getResource("/assets/music/Space Invaders - Space Invaders.mp3").toString());
            backgroundMusic = new MediaPlayer(media);
            backgroundMusic.setCycleCount(MediaPlayer.INDEFINITE); // Loop indefinitely
            backgroundMusic.setVolume(0.5); // Set volume to 50%
            backgroundMusic.play();
        } catch (Exception e) {
            System.out.println("Could not load background music: " + e.getMessage());
        }
        
        startGameLoop(); // start game
    }

    /**
     * Starts the main game loop using the AnimationTimer abstract class with a target rate of 60 fps
     * calls update and render method once per frame
     */
    private void startGameLoop() {
        gameLoop = new AnimationTimer() { // initialize gameLoop
            long lastTime = System.nanoTime(); // initialize last time as current time in nanoseconds
            @Override
            public void handle(long now) {
                double delta = (double) (now - lastTime) / 1000000000; // delta time converted to seconds (AnimationTimer target is 60 frames per second)
                update(delta);   // update game logic
                render();   // draw everything
                lastTime = now; // set last time to current time
            }
        };
        gameLoop.start(); // starts gameloop
    }

    /**
     * Update entity movement, check for collisions
     * @param delta time in seconds since the last update
     */
    private void update(double delta) {

        if (startScreen) { // Doesn't update when on start screen
            return;
        }
        if (paused) { // Doesn't update when paused
            return;
        }
        if (gameOver || gameWon) {
            return; // Doesn't update after game is over or won
        }

        if (levelComplete) {
            levelCompleteTimer += delta; // timer adding roughly .016 per game tick (60 fps)
            if (levelCompleteTimer >= LEVEL_COMPLETE_DELAY) {
                startNewLevel();
            }
            return;
        }

        // Boss level logic
        if (isBossLevel && boss != null) {
            boss.update(delta);
            
            // Boss shooting
            if (boss.canShoot()) {
                // Fire 3 bullets in a spread pattern
                fireBossProjectile(boss, -20);
                fireBossProjectile(boss, 0);
                fireBossProjectile(boss, 20);
            }
            
            // Update bullets and check player collisions
            player.update(delta); // allows player to move smoothly (60fps)
            ArrayList<Bullet> bulletsToRemove = new ArrayList<>(); // placeholder to get rid of fired bullets
            for (Bullet bullet : bulletList) { // loop through bullets
                if (!bullet.isAlive()) { // check if not alive
                    bulletsToRemove.add(bullet); // add bullets to remove to list
                } else {
                    bullet.update(delta); // update bullet with game speed
                    
                    // Check boss bullet hitting player
                    if (bullet.getOwner() instanceof Boss && bullet.intersects(player)) {
                        if (player.takeDamage()) {
                            gameOver = true; // end game
                            gameLoop.stop(); // stop game loop
                            if (!nameEntered){
                                nameEntered = true;
                                /*
                                prompt for player name and add to score
                                 */
                                Platform.runLater(() ->{
                                    TextInputDialog dialog = new TextInputDialog("Player");
                                    dialog.setTitle("Leaderboard");
                                    dialog.setHeaderText("GAME OVER - FINAL SCORE:" + score);
                                    dialog.setContentText("Enter Your Name:");
                                    String name = dialog.showAndWait().orElse("Player");
                                    leaderboardManager.addScore(name, score);
                                });
                            }
                            return;
                        } else {
                            System.out.println("Shield absorbed boss bullet! Shields remaining: " + powerUpManager.getShieldCharges()); // print remaining shields
                        }
                        bullet.setAlive(false); // destroy bullet
                    }
                    
                    // Check player bullet hitting boss
                    if (bullet.getOwner() instanceof Player && bullet.intersects(boss)) {
                        boss.takeDamage(1);
                        bullet.setAlive(false);
                        score += 10;
                    }
                }
            }
            bulletList.removeAll(bulletsToRemove); // remove bullets to remove from list
            
            // Check if boss defeated
            if (boss.isDefeated()) {
                gameWon = true;
                gameLoop.stop();
                score += 500;
                System.out.println("BOSS DEFEATED! YOU WIN! +500 bonus points");
                
                if (!nameEntered){
                    nameEntered = true;
                    Platform.runLater(() ->{
                        TextInputDialog dialog = new TextInputDialog("Player");
                        dialog.setTitle("Victory!");
                        dialog.setHeaderText("YOU WIN - FINAL SCORE: " + score);
                        dialog.setContentText("Enter Your Name:");
                        String name = dialog.showAndWait().orElse("Player");
                        leaderboardManager.addScore(name, score);
                    });
                }
            }
            
            return;
        }

        player.update(delta); // update player with each tick of the game - allows for smooth movement
        ArrayList<Bullet> bulletsToRemove = new ArrayList<>(); // temp bullet list
        for (Bullet bullet : bulletList) { // loop through list
            if (!bullet.isAlive()) { // if not alive
                bulletsToRemove.add(bullet); // add the bullet to the list
            } else {
                bullet.update(delta); //update bullet with speed
            }
        }
        bulletList.removeAll(bulletsToRemove); // remove all the dead bullets from the list
        invaderUpdateTimer += delta; // timer adding roughly .016 per game tick (60 fps)

        double stepTimer = invaderController.calculateUpdateTime(); // calculator time for invader to "step" left or right
        if (invaderUpdateTimer > stepTimer) {
            invaderUpdateTimer -= stepTimer;
            invaderController.updateAll(); // moves the invaders once the appropriate time has passed
        }

         // Bullets hitting invaders
        for (Bullet bullet : bulletList) {
            for (Invader[] invaderRow : invaderController.getInvaderList()) {
                for (Invader invader : invaderRow) {
                    if (bullet.intersects(invader) && invader.isAlive()) {
                        invader.setAlive(false);
                        bullet.setAlive(false);
                        powerUpManager.registerKill();
                        score += 5;
                    }
                }
            }
        }

        // Kill invaders that have gone off the bottom of the screen
        for (Invader[] invaderRow : invaderController.getInvaderList()) {
            for (Invader invader : invaderRow) {
                if (invader.isAlive() && invader.getY() > HEIGHT) {
                    invader.setAlive(false);
                    System.out.println("Invader escaped off screen!");
                }
            }
        }

        // Check each invader individually for passing the player
        for (Invader[] invaderRow : invaderController.getInvaderList()) {
            for (Invader invader : invaderRow) {
                if (invader.isAlive() && !invader.hasPassedPlayer()) {
                    if (invader.getY() + invader.getHeight() >= player.getY()) {
                        invader.setHasPassedPlayer(true);
                        // Try to use a shield
                        if (player.takeDamage()) {
                            // No shield available - game over
                            gameOver = true;
                            gameLoop.stop();

                            if (!nameEntered){
                                nameEntered = true;
                                Platform.runLater(() ->{
                                    TextInputDialog dialog = new TextInputDialog("Player");
                                    dialog.setTitle("Leaderboard");
                                    dialog.setHeaderText("GAME OVER - FINAL SCORE:" + score);
                                    dialog.setContentText("Enter Your Name:");
                                    String name = dialog.showAndWait().orElse("Player");
                                    leaderboardManager.addScore(name, score);
                                });
                            }
                            return;
                        } else {
                            System.out.println("Shield absorbed hit from invader! Shields remaining: " + powerUpManager.getShieldCharges());
                        }
                    }
                }
            }
        }

        // Kill invaders that have gone off the bottom of the screen
        for (Invader[] invaderRow : invaderController.getInvaderList()) {
            for (Invader invader : invaderRow) {
                if (invader.isAlive() && invader.getY() > HEIGHT) {
                    invader.setAlive(false);
                    System.out.println("Invader escaped off screen!");
                }
            }
        }

        if (checkAllInvadersDead()) {
            levelComplete = true;
            levelCompleteTimer = 0;
            score += 100;
        }
    }

    /**
     * Draws the game screen
     */
    private void render() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.drawImage(background, 0, 0, canvas.getWidth(), canvas.getHeight());

        // Displays start screen
        if (startScreen) {
            gc.setFill(Color.WHITE);

            // Title (wasn't sure what we were naming it tbh feel free to change)
            gc.setFont(new Font("Arial", 48));
            gc.fillText("FISH IN SPACE", canvas.getWidth() / 2 - 170, canvas.getHeight() / 2 - 60);

            // Press "ENTER" to start game
            gc.setFont(new Font("Arial", 24));
            gc.fillText("Press ENTER to Start", canvas.getWidth() / 2 - 125, canvas.getHeight() / 2 - 10);

            // Instructions
            gc.setFont(new Font("Arial", 10));
            gc.fillText("Arrows: Move  |  SPACE: Shoot  |  P: Pause", canvas.getWidth() / 2 - 105, canvas.getHeight() / 2 + 25);
            return;
        }

        player.draw(gc);
        for (Bullet bullet : bulletList) {
            bullet.draw(gc);
        }
        
        // Draw boss or regular invaders
        if (isBossLevel && boss != null) {
            boss.draw(gc);
        } else {
            for (Invader[] invaderRow : invaderController.getInvaderList()) {
                for (Invader invader : invaderRow) {
                    if (invader.isAlive()) {
                        invader.draw(gc);
                    }
                }
            }
        }

        // Display score in top right corner
        gc.setFill(Color.WHITE);
        gc.setFont(new Font("Arial", 20));
        gc.fillText("Score: " + score, canvas.getWidth() - 120, 25);
        
        // Boss level indicator
        if (isBossLevel) {
            gc.setFill(Color.RED);
            gc.setFont(new Font("Arial", 24));
            gc.fillText("BOSS FIGHT!", canvas.getWidth() / 2 - 70, 30);
        }

        // Draw shield icons in bottom left corner
        powerUpManager.drawShields(gc, canvas.getHeight());

        // Level complete display
        if (levelComplete) {
            gc.setFill(Color.YELLOW);
            gc.setFont(new Font("Arial", 40));
            gc.fillText("Level Complete", canvas.getWidth() / 2 - 140, canvas.getHeight() / 2);
        }

        // Added pause display
        if (paused) {
            gc.setFill(Color.YELLOW);
            gc.setFont(new Font("Arial", 40));
            gc.fillText("PAUSED", canvas.getWidth() / 2 - 90, canvas.getHeight() / 2);
        }

        // Added a display for when the game is over
        if (gameOver) {
            gc.setFill(Color.RED);
            gc.setFont(new Font("Arial", 40));
            gc.fillText("GAME OVER", canvas.getWidth() / 2 - 120, 60);

            // Options for user to either continue or quit the game
            gc.setFill(Color.WHITE);
            gc.setFont(new Font("Arial", 16));
            gc.fillText("Press ENTER to Continue", canvas.getWidth() / 2 - 100, 100);

            // Display current score centered
            gc.setFill(Color.WHITE);
            gc.setFont(new Font("Arial", 32));
            String scoreText = "SCORE: " + score;
            gc.fillText(scoreText, canvas.getWidth() / 2 - 85, 145);

            // Leaderboard section
            gc.setFont(new Font("Arial", 22));
            gc.fillText("LEADERBOARD:", canvas.getWidth() / 2 - 90, 190);

            // List leaderboard entries
            gc.setFont(new Font("Arial", 18));
            int yoffset = 220;
            int index = 1;

            for (LeaderboardEntry entry : leaderboardManager.getEntries()){
                String line = index + ". " + entry.getName() + " - " + entry.getScore();
                gc.fillText(line, canvas.getWidth() / 2 - 100, yoffset);
                yoffset += 30;
                index++;
            }

            // Quit option at bottom
            gc.setFont(new Font("Arial", 16));
            gc.fillText("Press ESC to Quit", canvas.getWidth() / 2 - 80, canvas.getHeight() - 30);
        }
        
        // You Win screen
        if (gameWon) {
            gc.setFill(Color.GOLD);
            gc.setFont(new Font("Arial", 48));
            gc.fillText("YOU WIN!", canvas.getWidth() / 2 - 120, 80);

            // Display final score centered
            gc.setFill(Color.WHITE);
            gc.setFont(new Font("Arial", 32));
            String scoreText = "FINAL SCORE: " + score;
            gc.fillText(scoreText, canvas.getWidth() / 2 - 130, 130);

            // Leaderboard section
            gc.setFont(new Font("Arial", 22));
            gc.fillText("LEADERBOARD:", canvas.getWidth() / 2 - 90, 180);

            // List leaderboard entries
            gc.setFont(new Font("Arial", 18));
            int yoffset = 210;
            int index = 1;

            for (LeaderboardEntry entry : leaderboardManager.getEntries()){
                String line = index + ". " + entry.getName() + " - " + entry.getScore();
                gc.fillText(line, canvas.getWidth() / 2 - 100, yoffset);
                yoffset += 30;
                index++;
            }

            // Play again option
            gc.setFill(Color.WHITE);
            gc.setFont(new Font("Arial", 16));
            gc.fillText("Press ENTER to Play Again", canvas.getWidth() / 2 - 110, canvas.getHeight() - 60);
            gc.fillText("Press ESC to Quit", canvas.getWidth() / 2 - 80, canvas.getHeight() - 30);
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

    /**
     * Fires a projectile from the entity
     * Handles both player bullets and invader bullets.
     *
     * @param entity firing bullets
     */
    private void fireProjectile(Entity entity) {
        if (entity instanceof Player) {
            double cooldown = 800 * powerUpManager.getFireRateMultiplier();
            if (System.currentTimeMillis() - player.getLastBullet() < cooldown) {
                return;
            }
            player.setLastBullet(System.currentTimeMillis());

            if (powerUpManager.hasDoubleShot()) {
                // Fire two bullets side-by-side
                Bullet bullet1 = new Bullet(player.getX() + ((double) player.getWidth() / 2) - 8, player.getY(), 3, 8, null, entity);
                bullet1.setMoveY(-120);
                bulletList.add(bullet1);

                Bullet bullet2 = new Bullet(player.getX() + ((double) player.getWidth() / 2) + 5, player.getY(), 3, 8, null, entity);
                bullet2.setMoveY(-120);
                bulletList.add(bullet2);
            } else {
                // Fire single bullet
                Bullet bullet = new Bullet(player.getX() + ((double) player.getWidth() / 2), player.getY(), 3, 8, null, entity);
                bullet.setMoveY(-120);
                bulletList.add(bullet);
            }
        }
        if (entity instanceof Invader) {
            Bullet bullet = new Bullet(entity.getX() + ((double) player.getWidth() / 2), entity.getY(), 3, 8, null, entity);
            bullet.setMoveY(+120);
            bulletList.add(bullet);
        }
    }
    
    /**
     * Fires a single boss projectile with a horizontal offset
     *
     * @param boss the boss firing the projectile
     * @param angleOffset horizontal offset for the bullet path
     */
    private void fireBossProjectile(Boss boss, double angleOffset) {
        Bullet bullet = new Bullet(
            boss.getX() + boss.getWidth() / 2 - 1.5,
            boss.getY() + boss.getHeight(),
            3, 8, null, boss
        );
        bullet.setMoveY(150);
        bullet.setMoveX(angleOffset * 2);
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

                    // Added "ENTER" key to start the game
                    case ENTER:
                        if (startScreen) {
                            startScreen = false; // leaves start screen
                            break;
                        }
                        if (gameOver || gameWon) {
                            restartGame(); // "ENTER" restarts game at gameover or victory
                            break;
                        }
                        break;

                    // Added "P" key for pause/unpause
                    case P:
                        if (startScreen) { // Can't pause on start screen
                            break;
                        }
                        paused = !paused;
                        break;

                    case LEFT:
                        player.setMoveX(-150 * powerUpManager.getSpeedMultiplier());
                        player.setMovingLeft(true);
                        break;

                    case RIGHT:
                        player.setMoveX(150 * powerUpManager.getSpeedMultiplier());
                        player.setMovingRight(true);
                        break;

                    case SPACE:
                        fireProjectile(player);
                        break;

                    case ESCAPE:
                        if (gameOver || gameWon) {
                            System.exit(0);
                        }
                        break;
                }
            }
        });

        canvas.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                // To ignore releases on start menu
                if (startScreen) {
                    return;
                }
                // To ignore releases while paused
                if (paused) {
                    return;
                }

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
    
    /**
     * Checks whether all invaders are dead.
     *
     * @return true if no invader is alive and false otherwise
     */
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

    // Method for if user chooses to continue in game over screen
    private void restartGame() {

        // Reinitialize game to start-of-game state
        gameOver = false;
        gameWon = false;
        paused = false;
        startScreen = false;
        currentLevel = 1;
        levelComplete = false;
        levelCompleteTimer = 0;
        score = 0;
        bulletList.clear();
        invaderUpdateTimer = 0;
        isBossLevel = false;
        boss = null;

        // Reset player and powerups
        powerUpManager = new PowerUpManager();
        player = new Player(canvas.getWidth() / 2 - 16, canvas.getHeight() - 32, 32, 32, powerUpManager);

        // Reset invaders to first level rows and columns
        invaderController = new InvaderController();
        invaderController.spawnInvaders(4, 5);

        // Resume game from beginning
        gameLoop.start();
    }
    
    /**
     * Starts a new level after the level complete delay
     * Handles both regular levels and boss level setup
     */
    private void startNewLevel() {
        currentLevel++;
        levelComplete = false;
        levelCompleteTimer = 0;
        bulletList.clear();

        // Boss fight on level 6
        if (currentLevel == 6) {
            isBossLevel = true;
            int bossHealth = 50; // Boss health
            boss = new Boss(canvas.getWidth() / 2 - 40, 50, 80, 80, bossHealth);
            System.out.println("BOSS LEVEL " + currentLevel + "! Boss Health: " + bossHealth);
        } else {
            isBossLevel = false;
            boss = null;
            int rows = 4 + (currentLevel - 1);
            int cols = 5 + (currentLevel - 1);
            invaderController.spawnInvaders(rows, cols);
        }

        // Keep power-ups between levels
        System.out.println("Level " + currentLevel + " - Score: " + score + ", Kills: " + powerUpManager.getKillCount() + ", Shields: " + powerUpManager.getShieldCharges()
                + ", Speed: " + String.format("%.0f%%", powerUpManager.getSpeedMultiplier() * 100)
                + ", Fire Rate: " + String.format("%.0f%%", powerUpManager.getFireRateMultiplier() * 100));
    }

}
