/*
 * Part of core.
 * 
 * Created on 12 June 2017 at 4:55 PM.
 */

package com.maulss.core.bukkit;

import org.apache.commons.lang3.Validate;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public abstract class Violation<Target> extends TimerTask {

    private Timer timer;
    private final Target target;
    private final int toleration;
    private volatile int violations = 0;

    protected Violation(final Target target,
                        final int toleration) {
        this.target = Validate.notNull(target);
        this.toleration = toleration;
    }

    @Override
    public final void run() {
        if (violations != 0) {
            violations--;
            onViolationDecrease(target);
        }
    }

    public abstract void onMaxViolations(final Target target);

    public abstract void onViolation(final Target target);

    public void onViolationDecrease(final Target target) {}

    public final void enableCooldown(final int time,
                                     final TimeUnit units) {
        if (timer != null) timer.cancel();

        long millis = units.toMillis(time);
        timer = new Timer();
        timer.scheduleAtFixedRate(this, millis, millis);
    }

    public final boolean addViolation() {
        boolean tolerated = ++violations != toleration;

        if (tolerated) {
            onViolation(target);
        } else {
            onMaxViolations(target);
            clear();
        }

        return tolerated;
    }

    public final int getRemainingViolations() {
        return toleration - violations;
    }

    public final void clear() {
        violations = 0;
    }

    public Target getTarget() {
        return target;
    }

    public int getToleration() {
        return toleration;
    }

    public int getViolations() {
        return violations;
    }
}