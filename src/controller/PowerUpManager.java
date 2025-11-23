
package controller;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class PowerUpManager {

    private int killCount = 0;
    private int lastPowerUpAt = 0;
    private static final int KILLS_PER_POWERUP = 5;

    // Power-up states
    private double fireRateMultiplier = 1.0; // 1.0 = normal, 0.4 = 60% reduction
    private double speedMultiplier = 1.0;
    private boolean hasDoubleShot = false;
    private int shieldCharges = 0;

    // Constants
    private static final double FIRE_RATE_REDUCTION = 0.12; // 12% reduction per power-up
    private static final double MIN_FIRE_RATE = 0.4; // Cap at 60% reduction
    private static final double SPEED_INCREASE = 0.18; // 18% increase per power-up
    private static final double MAX_SPEED_MULTIPLIER = 2.5; // Cap speed increase
    private static final int MAX_SHIELDS = 3; // Cap the amount of shields to 3

    private final Image shields = new Image("assets/shields.png");

    // Created shield images that will pop up everytime the power up is used
    public void drawShields(GraphicsContext gc, double canvasHeight) {
        double iconSize = 24;

        // Bottom-left corner
        double startX = 10;
        double startY = canvasHeight - iconSize - 10;

        for (int i = 0; i < shieldCharges; i++) {
            double x = startX + i * (iconSize + 5);
            gc.drawImage(shields, x, startY, iconSize, iconSize);
        }
    }

    public void registerKill() {
        killCount++;

        if (killCount - lastPowerUpAt >= KILLS_PER_POWERUP) {
            lastPowerUpAt = killCount;
            grantPowerUp();
        }
    }

    private void grantPowerUp() {
        // Grant power-ups in order: Double Shot -> Rapid Fire -> Speed -> Shield (cycle)
        if (!hasDoubleShot) {
            hasDoubleShot = true;
            System.out.println("Power-Up: Double Shot!");
        } else if (fireRateMultiplier > MIN_FIRE_RATE) {
            fireRateMultiplier = Math.max(MIN_FIRE_RATE, fireRateMultiplier - FIRE_RATE_REDUCTION);
            System.out.println("Power-Up: Rapid Fire! (Cooldown: " + String.format("%.0f%%", fireRateMultiplier * 100) + ")");
        } else if (speedMultiplier < MAX_SPEED_MULTIPLIER) {
            speedMultiplier = Math.min(MAX_SPEED_MULTIPLIER, speedMultiplier + SPEED_INCREASE);
            System.out.println("Power-Up: Speed Boost! (Speed: " + String.format("%.0f%%", speedMultiplier * 100) + ")");
        } else {
            // Changed to check for max shield usage
            if (shieldCharges < MAX_SHIELDS) {
                shieldCharges++;
                System.out.println("Power-Up: Shield! (Charges: " + shieldCharges + ")");
            }
        }
    }

    public boolean useShield() {
        if (shieldCharges > 0) {
            shieldCharges--;
            System.out.println("Shield used! Remaining: " + shieldCharges);
            return true;
        }
        return false;
    }

    public void reset() {
        killCount = 0;
        lastPowerUpAt = 0;
        fireRateMultiplier = 1.0;
        speedMultiplier = 1.0;
        hasDoubleShot = false;
        shieldCharges = 0;
    }

    // Getters
    public double getFireRateMultiplier() {
        return fireRateMultiplier;
    }

    public double getSpeedMultiplier() {
        return speedMultiplier;
    }

    public boolean hasDoubleShot() {
        return hasDoubleShot;
    }

    public int getShieldCharges() {
        return shieldCharges;
    }

    public int getKillCount() {
        return killCount;
    }
}
