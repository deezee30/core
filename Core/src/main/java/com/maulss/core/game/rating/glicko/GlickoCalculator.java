package com.maulss.core.game.rating.glicko;

import java.util.Collection;
import java.util.Map;

public final class GlickoCalculator {

    private static final int MAX_RATING = 2950;
    private static final int MIN_RATING = 50;
    private static final int MAX_DEVIATION = 350;
    private static final int MIN_DEVIATION = 50;
    private static final double q = Math.log(10) / 1200;

    // disable initialization
    private GlickoCalculator() {}

    public static int getNewRating(final GlickoProfile profile) {
        return Math.min(MAX_RATING, Math.max(MIN_RATING, getNewRating(
                profile.getCompetitor().getRating(),            // Current rating
                profile.getCompetitor().getRatingDeviation(),   // Current rating deviation
                profile.getMatches()                            // All past matches with outcomes
        )));
    }

    public static double getNewRatingDeviation(final GlickoProfile profile) {
        return Math.min(MAX_DEVIATION, Math.max(MIN_DEVIATION, getNewRatingDeviation(
                profile.getCompetitor().getRating(),            // Current rating
                profile.getCompetitor().getRatingDeviation(),   // Current rating deviation
                profile.getOpponents()                          // Opponents in current match
        )));
    }

    private static double dSquared(final int rating,
                                   final Collection<GlickoProfile> opponents) {
        double sum = 0;

        for (GlickoProfile opponent : opponents) {
            GlickoCompetitor competitor = opponent.getCompetitor();

            double g = g(competitor.getRatingDeviation());
            double e = E(rating, competitor.getRating(), competitor.getRatingDeviation());

            sum += g * g * e * (1 - e);
        }

        return 1 / (q * q * sum);
    }

    private static double E(final int playerRating,
                            final int opponentRating,
                            final double opponentRatingDeviation) {
        return 1 / (1 + Math.pow(10, g(opponentRatingDeviation)
                * (playerRating - opponentRating) / -400));
    }

    private static double g(final double ratingDeviation) {
        return 1 / (Math.sqrt(1 + (3 * q * q * ratingDeviation * ratingDeviation)
                / (Math.PI * Math.PI)));
    }

    private static double k(final int rating,
                            final double deviation,
                            final Collection<GlickoProfile> opponents) {
        return 1 / Math.pow(deviation, 2) + 1 / (dSquared(rating, opponents));
    }

    private static double getNewRatingDeviation(final int rating,
                                                final double ratingDeviation,
                                                final Collection<GlickoProfile> opponents) {
        return Math.sqrt(1 / k(rating, ratingDeviation, opponents));
    }

    private static int getNewRating(final int originalRating,
                                    final double ratingDeviation,
                                    final Map<GlickoProfile, GlickoMatch.Outcome> matches) {
        double sum = 0;

        for (Map.Entry<GlickoProfile, GlickoMatch.Outcome> entry : matches.entrySet()) {
            GlickoCompetitor competitor = entry.getKey().getCompetitor();
            sum += (g(competitor.getRatingDeviation()) * (entry.getValue().getScore() -
                    E(originalRating, competitor.getRating(), competitor.getRatingDeviation())));
        }

        double prefix = q / k(originalRating, ratingDeviation, matches.keySet());
        return originalRating + Math.round((float) (prefix * sum));
    }
}
