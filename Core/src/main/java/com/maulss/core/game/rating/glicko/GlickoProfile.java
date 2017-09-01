package com.maulss.core.game.rating.glicko;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class GlickoProfile {

    private static double c = 35;

    public static void setDefaultC(final double c) {
        GlickoProfile.c = c;
    }

    private final GlickoCompetitor competitor;
    private final Map<GlickoProfile, GlickoMatch.Outcome> matches = new HashMap<>();

    public GlickoProfile(final GlickoCompetitor competitor,
                         final int daysAbsent) {
        this(competitor);
        competitor.setRatingDeviation(Math.min(350,
                Math.sqrt(Math.pow(competitor.getRatingDeviation(), 2)
                + Math.pow(c, 2) * daysAbsent)));
    }

    public GlickoProfile(final GlickoCompetitor competitor) {
        this.competitor = Validate.notNull(competitor);
    }

    public GlickoCompetitor getCompetitor() {
        return competitor;
    }

    public void registerMatch(GlickoMatch match) {
        matches.put(match.getOpponent(this), match.getOutcome(this));
    }

    public Map<GlickoProfile, GlickoMatch.Outcome> getMatches() {
        return matches;
    }

    public Set<GlickoProfile> getOpponents() {
        return matches.keySet();
    }

    public void clear() {
        matches.clear();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("competitor", competitor.toString())
                .append("matches", matches)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        GlickoProfile that = (GlickoProfile) o;

        return new EqualsBuilder()
                .append(competitor, that.competitor)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(competitor)
                .toHashCode();
    }
}