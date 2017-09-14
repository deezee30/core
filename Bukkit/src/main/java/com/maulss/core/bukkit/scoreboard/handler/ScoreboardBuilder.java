/*
 * Part of core.
 * 
 * Created on 13 June 2017 at 2:49 PM.
 */

package com.maulss.core.bukkit.scoreboard.handler;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.maulss.core.bukkit.scoreboard.Scoreboards;
import com.maulss.core.collect.EnhancedList;
import com.maulss.core.collect.EnhancedMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.AbstractMap;
import java.util.Iterator;
import java.util.Map;

public final class ScoreboardBuilder {

    private Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
    private EnhancedList<Team> teams = new EnhancedList<>();

    private final EnhancedMap<String, Integer> scores = new EnhancedMap<>();
    private final String title;

    public ScoreboardBuilder(final String title,
                             final Map<String, Integer> scores) {
        this(title, scores, Scoreboards.MAX_WIDTH);
    }

    public ScoreboardBuilder(final String title,
                             final Map<String, Integer> scores,
                             final int maxWidth) {
        this.title = shorten(title, maxWidth);

        for (Map.Entry<String, Integer> entry : scores.entrySet()) {
            String text = entry.getKey();
            while (scores.containsKey(text)) text += ChatColor.RESET;
            this.scores.put(shorten(text, maxWidth), entry.getValue());
        }
    }

    public Scoreboard build() {
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

        Objective obj = scoreboard.registerNewObjective((title.length() > 16 ? title.substring(0, 15) : title), "dummy");
        obj.setDisplayName(ChatColor.translateAlternateColorCodes('&', title));
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        int index = scores.size();

        for (Map.Entry<String, Integer> text : scores.entrySet()) {
            Map.Entry<Team, String> team = createTeam(text.getKey());
            Integer score = text.getValue() != null ? text.getValue() : index;
            String row = team.getValue();
            if (!Strings.isNullOrEmpty(row) && team.getKey() != null) {
                team.getKey().addEntry(ChatColor.translateAlternateColorCodes('&', row));
            }
            obj.getScore(ChatColor.translateAlternateColorCodes('&', row)).setScore(score);
            --index;
        }

        return scoreboard;
    }

    public ScoreboardBuilder destroy() {
        scoreboard.getObjectives().forEach(Objective::unregister);
        teams.forEach(Team::unregister);
        teams.clear();
        return this;
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    private Map.Entry<Team, String> createTeam(final String text) {
        String result;
        if (text.length() <= 16) return new AbstractMap.SimpleEntry<>(null, text);
        Team team = scoreboard.registerNewTeam("text-" + scoreboard.getTeams().size());
        Iterator<String> iterator = Splitter.fixedLength(16).split(text).iterator();
        team.setPrefix(iterator.next());
        result = iterator.next();
        if (text.length() > 32) team.setSuffix(iterator.next());
        teams.add(team);
        return new AbstractMap.SimpleEntry<>(team, result);
    }

    private String shorten(final String text,
                           final int limit) {
        return text.length() > limit ? text.substring(0, limit - 1) : text;
    }
}