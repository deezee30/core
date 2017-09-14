/*
 * Part of core.
 * 
 * Created on 28 June 2017 at 8:17 PM.
 */

package com.maulss.core.bukkit.displaybar.actionbar;

import com.google.common.collect.ImmutableMap;
import com.maulss.core.bukkit.Core;
import com.maulss.core.bukkit.player.CorePlayer;
import com.maulss.core.util.MathUtil;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.util.Map;

public class ActionBar implements ConfigurationSerializable {

    private String text;
    private final int durationSecs;
    private final long updateRateTicks;

    private transient ActionBarUpdate updater;

    public ActionBar(final String text,
                     final int durationSecs) {
        this(text, durationSecs, ActionBarHelper.UPDATE_RATE_TICKS);
    }

    public ActionBar(final String text,
                     final int durationSecs,
                     final long updateRateTicks) {
        this.text = Validate.notNull(text);
        this.durationSecs = durationSecs < 1 ? 1 : durationSecs;
        this.updateRateTicks = updateRateTicks;
    }

    public void update(final String text) {
        if (updater == null)
            throw new IllegalStateException("Can't update a task that hasn't started yet!");
        this.text = text;
        updater.scheduleUpdate();
    }

    public void cancel() {
        if (updater == null)
            throw new IllegalStateException("Can't call cancel on a task that hasn't started yet!");
        updater.scheduleCancel();
    }

    public String getText() {
        return text;
    }

    public long getDurationSeconds() {
        return durationSecs;
    }

    public long getUpdateRateTicks() {
        return updateRateTicks;
    }

    public void send(CorePlayer... players) {
        double times = (double) durationSecs * (20D / (double) updateRateTicks);
        updater = new ActionBarUpdate(this, MathUtil.round(times), players);
        updater.runTaskTimerAsynchronously(Core.get(), 0, updateRateTicks);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("text", text)
                .append("duration", durationSecs)
                .append("update", updateRateTicks)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ActionBar actionBar = (ActionBar) o;

        return new EqualsBuilder()
                .append(durationSecs, actionBar.durationSecs)
                .append(text, actionBar.text)
                .append(updateRateTicks, actionBar.updateRateTicks)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(text)
                .append(durationSecs)
                .append(updateRateTicks)
                .toHashCode();
    }

    @Override
    public Map<String, Object> serialize() {
        return new ImmutableMap.Builder<String, Object>()
                .put("text", text)
                .put("duration", durationSecs)
                .put("update", updateRateTicks)
                .build();
    }

    public static ActionBar deserialize(final Map<String, Object> map) {
        return new ActionBar(
                (String) map.get("text"),
                (int) map.get("duration"),
                (long) map.get("update")
        );
    }
}