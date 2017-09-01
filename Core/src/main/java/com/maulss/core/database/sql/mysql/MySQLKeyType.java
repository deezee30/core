/*
 * Part of core.
 * Made on 05/08/2017
 */

package com.maulss.core.database.sql.mysql;

import com.maulss.core.database.sql.SQLKeyType;

import static org.apache.commons.lang3.StringUtils.countMatches;
import static org.apache.commons.lang3.Validate.*;

public enum MySQLKeyType implements SQLKeyType {

    // =: Numerical
    // Type     | Column Name     | Attr 1 | Attr 2 |
    TINYINT		("TINYINT(?)", 		127),
    SMALLINT	("SMALLINT(?)", 	255),
    MEDIUMINT	("MEDIUMINT(?)", 	255),
    INT			("INT(?)", 			255),
    BIGINT		("BIGINT(?)", 		255),
    FLOAT		("FLOAT(?,?)", 		16, 		6),
    DOUBLE		("DOUBLE(?,?)", 	16, 		6),

    // =: Text
    // Type     | Column Name     | Attr 1 |
    CHAR		("CHAR(?)", 		255),
    VARCHAR		("VARCHAR(?)", 		255),
    TEXT		("TEXT", 			255),
    MEDIUMTEXT	("MEDIUMTEXT", 		255),
    LONGTEXT	("LONGTEXT", 		255),

    // =: Dates
    // Type     | Column Name     | Attr 1 |
    TIMESTAMP	("TIMESTAMP", 		0),
    TIME		("TIME", 			0),
    DATE		("DATE", 			0),

    // =: Misc
    // Type     | Column Name     | Attr 1 | Attr 2 |
    BOOLEAN		("BOOLEAN"),
    CUSTOM		("?", 				0),
    CUSTOM_1	("?(?)", 			0),
    CUSTOM_2	("?(?,?)", 			0, 			0);

    private String   type;
    private Object[] attr;

    MySQLKeyType(final String type,
                 final Object... attr) {
        this.type = notNull(type);
        this.attr = notNull(attr);
    }

    /**
     * Provides optional attributes if the {@code DataType} contains specific variables
     * that can be used alongside them.  For example, {@link #VARCHAR} can hold an attribute
     * specifying the maximum capacity for that specific column.
     * <ping>
     * If the attribute(s) has/have not been set, the default options will be used (Not recommended).
     * </ping>
     *
     * @param   attr The attributes to set
     *
     * @return  This instance.
     */
    public MySQLKeyType setAttributes(final Object... attr) {
        notEmpty(attr);
        noNullElements(attr);

        this.attr = attr;
        return this;
    }

    /**
     * Used for custom {@code DataType}s. By default the {@link #CUSTOM} enum entries are unknown.
     * However, {@link #custom(String, Object...)} calls this method to set a proper title
     * (and attributes) for custom {@code DataType}s.
     *
     * @param   type The data name of the custom {@code DataType}
     *
     * @return  This instance.
     * @see     #custom(String, Object...)
     */
    private MySQLKeyType modifyData(final String type) {
        this.type = notEmpty(type);
        return this;
    }

    @Override
    public String get() {
        for (int x = 0; x < countMatches(type, "?"); ++x) {
            type = type.replaceAll("\\?", attr[x].toString());
        }

        return type;
    }

    /**
     * Generates custom {@code DataType}s that could be used for types of data that are not present
     * in the list of enum entries in this enum.
     *
     * @param   type The custom name of the data type
     * @param   attr The custom attributes for the custom data type (if any are needed).
     *               The length of the Object array defines which of the custom enums to
     *               use depending on the attributes needed
     *
     * @return  The instance of the custom data type.
     * @see     #CUSTOM
     * @see     #CUSTOM_1
     * @see     #CUSTOM_2
     */
    public static MySQLKeyType custom(final String type,
                                      final Object... attr) {
        int attrs = attr.length;
        return attrs == 0
                ? CUSTOM.modifyData(type).setAttributes(attr)
                : attrs == 1
                ? CUSTOM_1.modifyData(type).setAttributes(attr)
                : CUSTOM_2.modifyData(type).setAttributes(attr);
    }
}