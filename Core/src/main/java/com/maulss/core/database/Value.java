/*
 * Part of core.
 * Made on 31/07/2017
 */

package com.maulss.core.database;

import com.maulss.core.math.MathUtil;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public final class Value<O> implements Cloneable, Serializable {

    private static final long serialVersionUID = 35271921742643526L;

    private final O value;
    private final ValueType type;

    public Value(final O value) {
        this(value, ValueType.SET);
    }

    public Value(final O value,
                 final ValueType type) {
        this.value = value;
        this.type = type;
    }

    public boolean isInteger() {
        return MathUtil.isInteger(String.valueOf(value));
    }

    public ValueType getType() {
        return type;
    }

    public O getValue() {
        return value;
    }

    public int appendTo(final int append) {
        if (!isInteger()) throw new IllegalStateException("Value " + value + " is not an integer");

        return type.equals(ValueType.SET) ? append
                : Integer.valueOf(value.toString())
                + (type.equals(ValueType.GIVE) ? append : -append);
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public Value<O> clone() {
        return new Value<>(value, type);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Value<?> value1 = (Value<?>) o;

        return new EqualsBuilder()
                .append(value, value1.value)
                .append(type, value1.type)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(value)
                .append(type)
                .toHashCode();
    }
}