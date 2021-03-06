/**
 * Copyright (C) 2011  JTalks.org Team
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.jtalks.poulpe.util.databasebackup.persistence;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.Validate;

/**
 * Table Data Utility class performs different operations for preparing SQL statement strings.
 * 
 * @author Evgeny Surovtsev
 * 
 */
public final class TableDataUtil {
    /**
     * Private constructor to disable creating instances of the Utility class.
     */
    private TableDataUtil() {
    }

    /**
     * The method just quotes a given value, so it is available to use in the SQL statements.
     * 
     * @param value
     *            A value which will be quoted.
     * @return Already quoted value.
     * @throws NullPointerException
     *             If value is null.
     */
    public static String getSqlValueQuotedString(String value) {
        Validate.notNull(value, "value must not be null");
        return getSqlQuotedString(value, VALUE_QUOTE_SIGN);
    }

    /**
     * The method just quotes a given column or table name, so it is available to use in the SQL statements.
     * 
     * @param value
     *            A value which will be quoted.
     * @return Already quoted value.
     * @throws NullPointerException
     *             If value is null.
     */
    public static String getSqlColumnQuotedString(String value) {
        Validate.notNull(value, "value must not be null");
        return getSqlQuotedString(value, FIELD_QUOTE_SIGN);
    }

    /**
     * The method just quotes a given string, so it is available to use in the SQL statements. Method is used by
     * {@link #getSqlColumnQuotedString(String)} and {@link #getSqlValueQuotedString(String)} .
     * 
     * @param value
     *            A value which will be quoted.
     * @param quote
     *            A quote symbol which will be used for quoting process.
     * @return Already quoted value.
     */
    private static String getSqlQuotedString(String value, String quote) {
        assert value != null;
        assert quote != null;
        return quote + StringEscapeUtils.escapeSql(value).replace("\\", "\\\\") + quote;
    }

    private static final String VALUE_QUOTE_SIGN = "'";
    private static final String FIELD_QUOTE_SIGN = "`";
}
