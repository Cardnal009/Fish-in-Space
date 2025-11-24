
package controller;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 * Manages the power-up system for the player in the game.
 * Tracks kills and grants progressive power-ups including:
 * - Double Shot: Fires two bullets at once
 * - Rapid Fire: Reduces shooting cooldown
 * - Speed Boost: Increases player movement speed
 * - Shield: Provides damage protection
 * 
 * Power-ups are granted after every 5 kills in sequential order.
 * @author Trevor Fish
 */
public class PowerUpManager {

    // Kill tracking
    private int killCount = 0; // Total number of enemies killed
    private int lastPowerUpAt = 0; // Kill count when last power-up was granted
    private static final int KILLS_PER_POWERUP = 5; // Number of kills required for each power-up

    // Power-up states
    private double fireRateMultiplier = 1.0; // 1.0 = normal rate, 0.4 = 60% faster (minimum)
    private double speedMultiplier = 1.0; // 1.0 = normal speed, increases with power-ups
    private boolean hasDoubleShot = false; // Whether player can fire two bullets simultaneously
    private int shieldCharges = 0; // Number of shield charges available (absorbs one hit each)

    // Constants
    private static final double FIRE_RATE_REDUCTION = 0.12; // 12% reduction per power-up
    private static final double MIN_FIRE_RATE = 0.4; // Cap at 60% reduction
    private static final double SPEED_INCREASE = 0.18; // 18% increase per power-up
    private static final double MAX_SPEED_MULTIPLIER = 2.5; // Cap speed increase
    private static final int MAX_SHIELDS = 3; // Cap the amount of shields to 3

    private final Image shields = new Image("assets/shields.png"); // Shield icon image

    /**
     * Renders shield icons on the canvas to display the number of shield charges.
     * Shields are drawn in the bottom-left corner of the screen.
     * 
     * @param gc The GraphicsContext used for drawing
     * @param canvasHeight The height of the canvas for positioning
     */
    public void drawShields(GraphicsContext gc, double canvasHeight) {
        double iconSize = 24; // Size of each shield icon in pixels

        // Position shields in bottom-left corner with padding
        double startX = 10; // 10px from left edge
        double startY = canvasHeight - iconSize - 10; // 10px from bottom edge

        // Draw each shield charge as an icon with spacing
        for (int i = 0; i < shieldCharges; i++) {
            double x = startX + i * (iconSize + 5); // 5px spacing between icons
            gc.drawImage(shields, x, startY, iconSize, iconSize);
        }
    }

    /**
     * Registers an enemy kill and checks if a power-up should be granted.
     * A power-up is granted every 5 kills.
     */
    public void registerKill() {
        killCount++; // Increment total kill counter

        // Check if enough kills have occurred since last power-up
        if (killCount - lastPowerUpAt >= KILLS_PER_POWERUP) {
            lastPowerUpAt = killCount; // Update last power-up milestone
            grantPowerUp(); // Award the next power-up in sequence
        }
    }

    /**
     * Grants the next power-up to the player in sequential order:
     * 1. Double Shot (one-time)
     * 2. Rapid Fire (progressive until max)
     * 3. Speed Boost (progressive until max)
     * 4. Shield (up to 3 charges, then cycles)
     * 
     * Each power-up type has caps to maintain game balance.
     */
    private void grantPowerUp() {
        // Grant power-ups in order: Double Shot -> Rapid Fire -> Speed -> Shield (cycle)
        
        // First power-up: Enable double shot (one-time upgrade)
        if (!hasDoubleShot) {
            hasDoubleShot = true;
            System.out.println("Power-Up: Double Shot!");
        } 
        // Second power-up type: Reduce fire rate cooldown (progressive)
        else if (fireRateMultiplier > MIN_FIRE_RATE) {
            fireRateMultiplier = Math.max(MIN_FIRE_RATE, fireRateMultiplier - FIRE_RATE_REDUCTION);
            System.out.println("Power-Up: Rapid Fire! (Cooldown: " + String.format("%.0f%%", fireRateMultiplier * 100) + ")");
        } 
        // Third power-up type: Increase movement speed (progressive)
        else if (speedMultiplier < MAX_SPEED_MULTIPLIER) {
            speedMultiplier = Math.min(MAX_SPEED_MULTIPLIER, speedMultiplier + SPEED_INCREASE);
            System.out.println("Power-Up: Speed Boost! (Speed: " + String.format("%.0f%%", speedMultiplier * 100) + ")");
        } 
        // Final power-up type: Grant shield charges (capped at max)
        else {
            if (shieldCharges < MAX_SHIELDS) {
                shieldCharges++;
                System.out.println("Power-Up: Shield! (Charges: " + shieldCharges + ")");
            }
        }
    }

    /**
     * Attempts to use a shield charge to absorb damage.
     * 
     * @return true if a shield was available and used, false if no shields remain
     */
    public boolean useShield() {
        if (shieldCharges > 0) {
            shieldCharges--; // Consume one shield charge
            System.out.println("Shield used! Remaining: " + shieldCharges);
            return true; // Shield successfully absorbed damage
        }
        return false; // No shields available
    }

    /**
     * Resets all power-up states and kill tracking to initial values.
     * Called when starting a new game or after game over.
     */
    public void reset() {
        killCount = 0; // Reset kill counter
        lastPowerUpAt = 0; // Reset power-up milestone
        fireRateMultiplier = 1.0; // Reset fire rate to normal
        speedMultiplier = 1.0; // Reset speed to normal
        hasDoubleShot = false; // Remove double shot ability
        shieldCharges = 0; // Remove all shields
    }

    // ========== Getter Methods ==========
    
    /**
     * Gets the current fire rate multiplier.
     * 
     * @return The fire rate multiplier (1.0 = normal, lower = faster)
     */
    public double getFireRateMultiplier() {
        return fireRateMultiplier;
    }

    /**
     * Gets the current speed multiplier.
     * 
     * @return The speed multiplier (1.0 = normal, higher = faster)
     */
    public double getSpeedMultiplier() {
        return speedMultiplier;
    }

    /**
     * Checks if the player has the double shot ability.
     * 
     * @return true if double shot is enabled, false otherwise
     */
    public boolean hasDoubleShot() {
        return hasDoubleShot;
    }

    /**
     * Gets the current number of shield charges.
     * 
     * @return The number of available shield charges
     */
    public int getShieldCharges() {
        return shieldCharges;
    }

    /**
     * Gets the total number of kills registered.
     * 
     * @return The total kill count
     */
    public int getKillCount() {
        return killCount;
    }
}
