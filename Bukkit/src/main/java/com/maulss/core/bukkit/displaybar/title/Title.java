package com.maulss.core.bukkit.displaybar.title;

import com.maulss.core.bukkit.player.CorePlayer;
import com.maulss.core.collect.EnhancedMap;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Optional;

public final class Title implements ConfigurationSerializable {

    private static final transient TitleHandler handler = new TitleHandler();

    private final boolean clear;
    private Optional<TitleMessage> title = Optional.empty();
    private Optional<TitleMessage> subTitle = Optional.empty();

    Title(Optional<TitleMessage> title,
          Optional<TitleMessage> subTitle,
          boolean clear) {
        this.title = Validate.notNull(title);
        this.subTitle = Validate.notNull(subTitle);
        this.clear = clear;
    }

    public void send(final Player... players) {
        for (Player player : players) {
            send(CorePlayer.createIfAbsent(player));
        }
    }

    public void send(final CorePlayer... players) {
        if (clear) {
            handler.send(handler.buildClearPacket(), players);
            return;
        }

        title.ifPresent(titleMessage -> handler.handleTitleSendPacket(titleMessage, players));
        subTitle.ifPresent(titleMessage -> handler.handleTitleSendPacket(titleMessage, players));
    }

    public boolean isClear() {
        return clear;
    }

    public Optional<TitleMessage> getTitle() {
        return title;
    }

    public Optional<TitleMessage> getSubTitle() {
        return subTitle;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("clear", clear)
                .append("title", title)
                .append("subTitle", subTitle)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Title title1 = (Title) o;

        return new EqualsBuilder()
                .append(clear, title1.clear)
                .append(title, title1.title)
                .append(subTitle, title1.subTitle)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(clear)
                .append(title)
                .append(subTitle)
                .toHashCode();
    }

    @Override
    public Map<String, Object> serialize() {
        EnhancedMap<String, Object> map = new EnhancedMap<>();

        map.put("clear", clear);
        map.putIf(title.isPresent(), "title", title.get());
        map.putIf(subTitle.isPresent(), "subTitle", subTitle.get());

        return map;
    }

    public static Title deserialize(final Map<String, Object> data) {
        Optional<TitleMessage> title = Optional.empty();
        Optional<TitleMessage> subTitle = Optional.empty();
        boolean clear;

        if (data.containsKey("title"))
            title = Optional.of((TitleMessage) data.get("title"));
        if (data.containsKey("subTitle"))
            subTitle = Optional.of((TitleMessage) data.get("subTitle"));
        clear = (boolean) data.get("clear");

        return new Title(title, subTitle, clear);
    }

    public static TitleBuilder builder() {
        return new TitleBuilder();
    }
}
