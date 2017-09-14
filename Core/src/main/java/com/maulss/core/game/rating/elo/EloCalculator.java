/*
 * Part of core.
 * 
 * Created on 02 July 2017 at 11:17 PM.
 */

package com.maulss.core.game.rating.elo;

import com.maulss.core.game.rating.KFactor;
import com.maulss.core.game.rating.StaticKFactor;

import static org.apache.commons.lang3.Validate.notNull;

/**
 * This class handles the player Elo rating system calculations.
 * See <a href='http://en.wikipedia.org/wiki/Elo_rating_system'>Wikipedia</a> for more info.
 */
public final class EloCalculator {

    private static final int MAX_ELO = 2900;
    private static final int MIN_ELO = 100;

    private EloCalculator() {}

    /**
     * Returns the new rating for the player after winning
     * or losing to the opponent(s) with the given rating.
     *
     * <p>Min and max rating boundaries are applied, between
     * {@code 100} and {@code 2900}</p>
     *
     * @param   competitor
     *          The player who holds the Elo rating
     * @param   opponent
     *          The competitor's opponent
     * @param   outcome
     *          The outcome of the game (WIN = 1.0, DRAW = 0.5, LOSS = 0.0)
     * @return  The competitor's new rating
     */
    public static int newRating(final EloCompetitor competitor,
                                final EloCompetitor opponent,
                                final EloMatchOutcome outcome) {
        notNull(competitor);
        notNull(opponent);
        notNull(outcome);

        int rating = competitor.getRating();
        return Math.min(MAX_ELO, Math.max(MIN_ELO, calculateNewRating(
                rating, outcome,
                calculateExpectedScore(rating, opponent.getRating()),
                competitor.getKFactor()
        )));
    }

    /**
     * Returns the new rating for the player after winning
     * or losing to the opponent(s) with the given rating.
     *
     * <p>Min and max rating boundaries are applied, between
     * {@code 100} and {@code 2900}</p>
     *
     * @param   rating
     *          Player's old rating
     * @param   opponentRating
     *          The rating of the opposing player(s)
     * @param   outcome
     *          The outcome of the game (WIN = 1.0, DRAW = 0.5, LOSS = 0.0)
     * @param   provisional
     *          Whether or not the player is new
     * @return  The new rating
     * @see     #newRating(int, int, EloMatchOutcome, boolean)
     */
    public static int newRating(final int rating,
                                final int opponentRating,
                                final EloMatchOutcome outcome,
                                final boolean provisional) {
        return Math.min(MAX_ELO, Math.max(MIN_ELO, calculateNewRating(
                rating,
                notNull(outcome),
                calculateExpectedScore(rating, opponentRating),
                StaticKFactor.getKFactor(rating, provisional)
        )));
    }

    /**
     * Calculates the expected score based on two players.
     *
     * <p>In a 2v2 game opponent rating will be an average
     * of the opposing players rating.</p>
     *
     * @param   rating
     *          Player rating
     * @param   opponentRating
     *          The rating of the opposing player(s)
     * @return  Expected score
     */
    public static double calculateExpectedScore(final int rating,
                                                final int opponentRating) {
        return 1.0 / (1.0 + Math.pow(10.0, (opponentRating - rating) / 400.0));
    }

    /**
     * Calculates the new rating for the player.
     *
     * <p>The new rating is based on the old rating, the game
     * score, the expected game score and the k-factor.</p>
     *
     * @param   rating
     *          Player's old rating
     * @param   score
     *          The outcome of the game (WIN = 1.0, DRAW = 0.5, LOSS = 0.0)
     * @param   expectedScore
     *          Expected game score (based on participant ratings)
     * @param   kFactor
     *          K-factor
     * @return  Player's new rating
     */
    private static int calculateNewRating(final int rating,
                                          final EloMatchOutcome score,
                                          final double expectedScore,
                                          final KFactor kFactor) {
        return rating + (int) Math.round(kFactor.getKFactor() * (score.getScore() - expectedScore));
    }
}