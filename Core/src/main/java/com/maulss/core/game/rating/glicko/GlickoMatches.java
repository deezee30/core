package com.maulss.core.game.rating.glicko;

import org.apache.commons.lang3.Validate;

import java.util.*;

public final class GlickoMatches implements Iterable<GlickoProfile> {

    private final List<GlickoMatch> matches = new ArrayList<>();

    public void addMatch(final GlickoMatch match) {
        Validate.notNull(match);
        matches.add(match);
        match.register();
    }

    private List<GlickoProfile> getCompetitors() {
        List<GlickoProfile> competitors = new ArrayList<>(matches.size() * 2);
        for (GlickoMatch match : matches) {
            competitors.add(match.getFirstPlayer());
            competitors.add(match.getSecondPlayer());
        }

        return competitors;
    }

    public void applyNewData() {
        List<GlickoProfile> profiles = getCompetitors();

        Map<GlickoProfile, Integer> ratingData = new HashMap<>(profiles.size());
        Map<GlickoProfile, Double> deviationData = new HashMap<>(profiles.size());

        for (GlickoProfile profile : this) {
            ratingData.put(profile, GlickoCalculator.getNewRating(profile));
            deviationData.put(profile, GlickoCalculator.getNewRatingDeviation(profile));
        }

        for (GlickoProfile profile : this) {
            GlickoCompetitor competitor = profile.getCompetitor();
            competitor.setRating(ratingData.get(profile));
            competitor.setRatingDeviation(deviationData.get(profile));
            profile.clear();
        }
    }

    @Override
    public Iterator<GlickoProfile> iterator() {
        return getCompetitors().iterator();
    }
}
