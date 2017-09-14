package com.maulss.core.bukkit.displaybar.title;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.util.Map;
import java.util.Optional;

public final class TitleBuilder implements ConfigurationSerializable {

    static {
        ConfigurationSerialization.registerClass(TitleBuilder.class);
    }

    private Optional<TitleMessage> title = Optional.empty();
    private Optional<TitleMessage> subTitle = Optional.empty();
    private boolean clear = false;

    public TitleBuilder() {}

    public TitleBuilder(final TitleMessage title,
                        final TitleMessage subTitle) {
        withTitle(title).withTitle(subTitle);
    }

    public TitleBuilder withTitle(final TitleMessage title) {
        Validate.notNull(title);

        if (title.getType().equals(TitleMessage.Type.TITLE))
            this.title = Optional.of(title);
        else
            this.subTitle = Optional.of(title);

        return this;
    }

    public TitleBuilder clear(final boolean clear) {
        this.clear = clear;
        return this;
    }

    public Title build() {
        return new Title(title, subTitle, clear);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("title", title)
                .append("subTitle", subTitle)
                .append("clear", clear)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        TitleBuilder that = (TitleBuilder) o;

        return new EqualsBuilder()
                .append(clear, that.clear)
                .append(title, that.title)
                .append(subTitle, that.subTitle)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(title)
                .append(subTitle)
                .append(clear)
                .toHashCode();
    }

    @Override
    public Map<String, Object> serialize() {
        return build().serialize();
    }

    public static TitleBuilder deserialize(final Map<String, Object> data) {
        TitleBuilder builder = new TitleBuilder();

        if (data.containsKey("title"))
            builder.title = Optional.of((TitleMessage) data.get("title"));
        if (data.containsKey("subTitle"))
            builder.subTitle = Optional.of((TitleMessage) data.get("subTitle"));
        builder.clear = (boolean) data.get("clear");

        return builder;
    }
}
