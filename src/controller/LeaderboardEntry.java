package controller;


/**
 * Displays the leaderboard score, the users name, and takes
 * track of the highest score
 */
public class LeaderboardEntry {
    /**
     * The players name
     */
    private final String name;
    /**
     * The players score
     */
    private final int score;

    /**
     *
     * @param name takes track of the users name
     * @param score takes track of the players score
     */
    public  LeaderboardEntry(String name, int score){
        this.name = name;
        this.score = score;
    }

    /**
     *
     * @return returns the players name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return returns the players score
     */
    public int getScore() {
        return score;
    }
}
