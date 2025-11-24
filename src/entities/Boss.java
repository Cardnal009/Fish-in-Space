package entities;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class Boss extends Entity {
    
    private int health;
    private int maxHealth;
    private double shootCooldown = 0;
    private static final double SHOOT_INTERVAL = 1.5; // Shoots every 1.5 seconds
    private int direction = 1; // 1 = right, -1 = left
    private double moveSpeed = 50;
    
    public Boss(double x, double y, int width, int height, int health) {
        super(x, y, width, height, new Image("assets/invaders/fishvader.png"));
        this.health = health;
        this.maxHealth = health;
    }
    
    @Override
    public void update(double delta) {
        // Move horizontally
        double newX = getX() + (moveSpeed * direction * delta);
        
        // Bounce off walls
        if (newX <= 0) {
            newX = 0;
            direction = 1;
        } else if (newX + getWidth() >= 440) {
            newX = 440 - getWidth();
            direction = -1;
        }
        
        setX(newX);
        
        // Update shoot cooldown
        shootCooldown -= delta;
    }
    
    public void takeDamage(int damage) {
        health -= damage;
        if (health < 0) health = 0;
    }
    
    public boolean canShoot() {
        if (shootCooldown <= 0) {
            shootCooldown = SHOOT_INTERVAL;
            return true;
        }
        return false;
    }
    
    @Override
    public void draw(GraphicsContext gc) {
        // Save the current graphics state
        gc.save();
        
        // Translate to the center of the boss
        double centerX = getX() + getWidth() / 2;
        double centerY = getY() + getHeight() / 2;
        
        // Rotate 90 degrees clockwise (pointing down)
        gc.translate(centerX, centerY);
        gc.rotate(90);
        gc.translate(-centerX, -centerY);
        
        // Draw boss rotated
        gc.drawImage(getImage(), getX(), getY(), getWidth(), getHeight());
        
        // Restore the graphics state (unrotate for health bar)
        gc.restore();
        
        // Draw health bar above boss (not rotated)
        double barWidth = getWidth();
        double barHeight = 8;
        double barX = getX();
        double barY = getY() - 15;
        
        // Background (red)
        gc.setFill(Color.RED);
        gc.fillRect(barX, barY, barWidth, barHeight);
        
        // Health (green)
        gc.setFill(Color.LIME);
        double healthPercent = (double) health / maxHealth;
        gc.fillRect(barX, barY, barWidth * healthPercent, barHeight);
        
        // Border
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(1);
        gc.strokeRect(barX, barY, barWidth, barHeight);
    }
    
    public boolean isDefeated() {
        return health <= 0;
    }
    
    public int getHealth() {
        return health;
    }
    
    public int getMaxHealth() {
        return maxHealth;
    }
}
