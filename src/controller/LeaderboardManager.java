package controller;
import  java.io.*;
import java.util.ArrayList;
import java.util.Collections;

public class LeaderboardManager {
    private static final String FILE_NAME = "leaderboard.txt";
    private static final int MAX_ENTRIES = 5;

    private final ArrayList<LeaderboardEntry> entries = new ArrayList<>();
    public LeaderboardManager(){
        load();
    }
    private void load(){
        entries.clear();
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] split = line.split(",");
                if (split.length == 2) {
                    String name = split[0];
                    int score = Integer.parseInt(split[1]);
                    entries.add(new LeaderboardEntry(name, score));
                }
            }
        } catch (Exception ignored) {}
    }

    private void save() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (LeaderboardEntry entry : entries) {
                pw.println(entry.getName() + "," + entry.getScore());
            }
        } catch (Exception ignored) {}
    }

    public void addScore(String name, int score) {
        entries.add(new LeaderboardEntry(name, score));
        entries.sort((a, b) -> b.getScore() - a.getScore());
        while (entries.size() > MAX_ENTRIES)
            entries.remove(entries.size() - 1);
        save();
    }

    public ArrayList<LeaderboardEntry> getEntries() {
        return entries;
    }
}