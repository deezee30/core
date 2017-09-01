package com.maulss.core.game.rating.glicko;

import org.apache.commons.lang3.Validate;

public final class GlickoMatch {

    private final GlickoProfile firstPlayer;
    private final GlickoProfile secondPlayer;
    private final Outcome       outcome;

    public GlickoMatch(final GlickoProfile firstPlayer,
                       final GlickoProfile secondPlayer,
                       final Outcome outcome) {
        this.firstPlayer    = Validate.notNull(firstPlayer);
        this.secondPlayer   = Validate.notNull(secondPlayer);
        this.outcome        = Validate.notNull(outcome);
    }

    public GlickoProfile getFirstPlayer() {
        return firstPlayer;
    }

    public GlickoProfile getSecondPlayer() {
        return secondPlayer;
    }

    public Outcome getOutcome(final GlickoProfile competitor) {
        Validate.notNull(competitor);

        if (competitor.equals(firstPlayer)) return outcome;
        if (competitor.equals(secondPlayer)) return outcome.getOpposite();

        throw new IllegalStateException("Competitor " + competitor.toString()
                + " wasn't found in match " + toString());
    }

    public GlickoProfile getOpponent(final GlickoProfile competitor) {
        Validate.notNull(competitor);

        if (competitor.equals(firstPlayer)) return secondPlayer;
        if (competitor.equals(secondPlayer)) return firstPlayer;

        throw new IllegalStateException("Competitor " + competitor.toString()
                + " wasn't found in match " + toString());
    }

    public void register() {
        firstPlayer.registerMatch(this);
        secondPlayer.registerMatch(this);
    }

    public enum Outcome {

        WIN(1d),
        LOSS(0d),
        DRAW(0.5);

        private final double value;

        Outcome(double value) {
            this.value = value;
        }

        public double getScore() {
            return value;
        }

        public Outcome getOpposite() {
            switch (this) {
                case WIN:   return LOSS;
                case LOSS:  return WIN;
                default:    return DRAW;
            }
        }
    }
}
