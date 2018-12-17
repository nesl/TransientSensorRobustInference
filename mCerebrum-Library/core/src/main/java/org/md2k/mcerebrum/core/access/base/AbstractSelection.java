/*
 * Copyright (c) 2018, The University of Memphis, MD2K Center of Excellence
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.md2k.mcerebrum.core.access.base;

// @formatter:off
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;

/**
 * Abstract class for creating SQLite selection strings.
 * @param <T> Generic class.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class AbstractSelection<T extends AbstractSelection<?>> {
    private static final String EQ = "=?";
    private static final String PAREN_OPEN = "(";
    private static final String PAREN_CLOSE = ")";
    private static final String AND = " AND ";
    private static final String OR = " OR ";
    private static final String IS_NULL = " IS NULL";
    private static final String IS_NOT_NULL = " IS NOT NULL";
    private static final String IN = " IN (";
    private static final String NOT_IN = " NOT IN (";
    private static final String COMMA = ",";
    private static final String GT = ">?";
    private static final String LT = "<?";
    private static final String GT_EQ = ">=?";
    private static final String LT_EQ = "<=?";
    private static final String NOT_EQ = "<>?";
    private static final String LIKE = " LIKE ?";
    private static final String CONTAINS = " LIKE '%' || ? || '%'";
    private static final String STARTS = " LIKE ? || '%'";
    private static final String ENDS = " LIKE '%' || ?";
    private static final String COUNT = "COUNT(*)";
    public static final String DESC = " DESC";

    private final StringBuilder mSelection = new StringBuilder();
    private final List<String> mSelectionArgs = new ArrayList<>(5);

    private final StringBuilder mOrderBy = new StringBuilder();

    private Boolean mNotify;
    private String mGroupBy;
    private String mHaving;
    private Integer mLimit;

    /**
     * Creates a selection string that selects attributes with the given values.
     * @param column Attribute to look for.
     * @param value Values to match.
     */
    protected void addEquals(String column, Object[] value) {
        mSelection.append(column);

        if (value == null) {
            // Single null value
            mSelection.append(IS_NULL);
        } else if (value.length > 1) {
            // Multiple values ('in' clause)
            mSelection.append(IN);
            for (int i = 0; i < value.length; i++) {
                mSelection.append("?");
                if (i < value.length - 1) {
                    mSelection.append(COMMA);
                }
                mSelectionArgs.add(valueOf(value[i]));
            }
            mSelection.append(PAREN_CLOSE);
        } else {
            // Single value
            if (value[0] == null) {
                // Single null value
                mSelection.append(IS_NULL);
            } else {
                // Single not null value
                mSelection.append(EQ);
                mSelectionArgs.add(valueOf(value[0]));
            }
        }
    }

    /**
     * Creates a selection string that selects attributes without the given values.
     * @param column Attribute to look for.
     * @param value Values to exclude.
     */
    protected void addNotEquals(String column, Object[] value) {
        mSelection.append(column);

        if (value == null) {
            // Single null value
            mSelection.append(IS_NOT_NULL);
        } else if (value.length > 1) {
            // Multiple values ('in' clause)
            mSelection.append(NOT_IN);
            for (int i = 0; i < value.length; i++) {
                mSelection.append("?");
                if (i < value.length - 1) {
                    mSelection.append(COMMA);
                }
                mSelectionArgs.add(valueOf(value[i]));
            }
            mSelection.append(PAREN_CLOSE);
        } else {
            // Single value
            if (value[0] == null) {
                // Single null value
                mSelection.append(IS_NOT_NULL);
            } else {
                // Single not null value
                mSelection.append(NOT_EQ);
                mSelectionArgs.add(valueOf(value[0]));
            }
        }
    }

    /**
     * Creates a selection string that selects attributes similar to the given values.
     * @param column Attribute to look for.
     * @param values Values to match.
     */
    protected void addLike(String column, String[] values) {
        mSelection.append(PAREN_OPEN);
        for (int i = 0; i < values.length; i++) {
            mSelection.append(column);
            mSelection.append(LIKE);
            mSelectionArgs.add(values[i]);
            if (i < values.length - 1) {
                mSelection.append(OR);
            }
        }
        mSelection.append(PAREN_CLOSE);
    }

    /**
     * Creates a selection string that selects attributes that contain the given values.
     * @param column Attribute to look for.
     * @param values Values to match.
     */
    protected void addContains(String column, String[] values) {
        mSelection.append(PAREN_OPEN);
        for (int i = 0; i < values.length; i++) {
            mSelection.append(column);
            mSelection.append(CONTAINS);
            mSelectionArgs.add(values[i]);
            if (i < values.length - 1) {
                mSelection.append(OR);
            }
        }
        mSelection.append(PAREN_CLOSE);
    }

    /**
     * Creates a selection string that selects attributes that start with the given values.
     * @param column Attribute to look for.
     * @param values Values to match.
     */
    protected void addStartsWith(String column, String[] values) {
        mSelection.append(PAREN_OPEN);
        for (int i = 0; i < values.length; i++) {
            mSelection.append(column);
            mSelection.append(STARTS);
            mSelectionArgs.add(values[i]);
            if (i < values.length - 1) {
                mSelection.append(OR);
            }
        }
        mSelection.append(PAREN_CLOSE);
    }

    /**
     * Creates a selection string that selects attributes that start with the given values.
     * @param column Attribute to look for.
     * @param values Values to match.
     */
    protected void addEndsWith(String column, String[] values) {
        mSelection.append(PAREN_OPEN);
        for (int i = 0; i < values.length; i++) {
            mSelection.append(column);
            mSelection.append(ENDS);
            mSelectionArgs.add(values[i]);
            if (i < values.length - 1) {
                mSelection.append(OR);
            }
        }
        mSelection.append(PAREN_CLOSE);
    }

    /**
     * Creates a selection string that selects attributes greater than the given value.
     * @param column Attribute to look for.
     * @param value Value to check against.
     */
    protected void addGreaterThan(String column, Object value) {
        mSelection.append(column);
        mSelection.append(GT);
        mSelectionArgs.add(valueOf(value));
    }

    /**
     * Creates a selection string that selects attributes greater than or equal to the given value.
     * @param column Attribute to look for.
     * @param value Value to check against.
     */
    protected void addGreaterThanOrEquals(String column, Object value) {
        mSelection.append(column);
        mSelection.append(GT_EQ);
        mSelectionArgs.add(valueOf(value));
    }

    /**
     * Creates a selection string that selects attributes less than the given value.
     * @param column Attribute to look for.
     * @param value Value to check against.
     */
    protected void addLessThan(String column, Object value) {
        mSelection.append(column);
        mSelection.append(LT);
        mSelectionArgs.add(valueOf(value));
    }

    /**
     * Creates a selection string that selects attributes less than or equal to the given value.
     * @param column Attribute to look for.
     * @param value Value to check against.
     */
    protected void addLessThanOrEquals(String column, Object value) {
        mSelection.append(column);
        mSelection.append(LT_EQ);
        mSelectionArgs.add(valueOf(value));
    }

    /**
     * Appends the value of one or more raw objects to the selection string.
     * @param raw Name of raw object to add to the selection string.
     * @param args Object(s) to add to the selection string.
     * @return The updated selection string.
     */
    @SuppressWarnings("unchecked")
    public T addRaw(String raw, Object... args) {
        mSelection.append(" ");
        mSelection.append(raw);
        mSelection.append(" ");
        for (Object arg : args) {
            mSelectionArgs.add(valueOf(arg));
        }
        return (T) this;
    }

    /**
     * Returns the value of the object as a string.
     * @param obj Object to get the value of.
     * @return The value of the object as a string.
     */
    private String valueOf(Object obj) {
        if (obj instanceof Date) {
            return String.valueOf(((Date) obj).getTime());
        } else if (obj instanceof Boolean) {
            return (Boolean) obj ? "1" : "0";
        } else if (obj instanceof Enum) {
            return String.valueOf(((Enum<?>) obj).ordinal());
        }
        return String.valueOf(obj);
    }

    /**
     * Adds an opening parenthesis to the selection string.
     * @return The updated selection string.
     */
    @SuppressWarnings("unchecked")
    public T openParen() {
        mSelection.append(PAREN_OPEN);
        return (T) this;
    }

    /**
     * Adds a closing parenthesis to the selection string.
     * @return The updated selection string.
     */
    @SuppressWarnings("unchecked")
    public T closeParen() {
        mSelection.append(PAREN_CLOSE);
        return (T) this;
    }

    /**
     * Adds " AND " to the selection string.
     * @return The updated selection string.
     */
    @SuppressWarnings("unchecked")
    public T and() {
        mSelection.append(AND);
        return (T) this;
    }

    /**
     * Adds " OR " to the selection string.
     * @return The updated selection string.
     */
    @SuppressWarnings("unchecked")
    public T or() {
        mSelection.append(OR);
        return (T) this;
    }


    /**
     * Converts an integer array into an Object array.
     * @param array Array to convert.
     * @return The new Object array.
     */
    protected Object[] toObjectArray(int... array) {
        Object[] res = new Object[array.length];
        for (int i = 0; i < array.length; i++) {
            res[i] = array[i];
        }
        return res;
    }

    /**
     * Converts a long array into an Object array.
     * @param array Array to convert.
     * @return The new Object array.
     */
    protected Object[] toObjectArray(long... array) {
        Object[] res = new Object[array.length];
        for (int i = 0; i < array.length; i++) {
            res[i] = array[i];
        }
        return res;
    }

    /**
     * Converts a float array into an Object array.
     * @param array Array to convert.
     * @return The new Object array.
     */
    protected Object[] toObjectArray(float... array) {
        Object[] res = new Object[array.length];
        for (int i = 0; i < array.length; i++) {
            res[i] = array[i];
        }
        return res;
    }

    /**
     * Converts a double array into an Object array.
     * @param array Array to convert.
     * @return The new Object array.
     */
    protected Object[] toObjectArray(double... array) {
        Object[] res = new Object[array.length];
        for (int i = 0; i < array.length; i++) {
            res[i] = array[i];
        }
        return res;
    }

    /**
     * Converts a boolean value into an Object array.
     * @param value Value to convert.
     * @return The new Object array.
     */
    protected Object[] toObjectArray(Boolean value) {
        return new Object[] {value};
    }


    /**
     * Returns the selection produced by this object.
     */
    public String sel() {
        return mSelection.toString();
    }

    /**
     * Returns the selection arguments produced by this object.
     */
    public String[] args() {
        int size = mSelectionArgs.size();
        if (size == 0) return null;
        return mSelectionArgs.toArray(new String[size]);
    }

    /**
     * Returns the order string produced by this object.
     */
    public String order() {
        return mOrderBy.length() > 0 ? mOrderBy.toString() : null;
    }

    /**
     * Returns the <code>uri</code> argument to pass to the <code>ContentResolver</code> methods.
     */
    public Uri uri() {
        Uri uri = baseUri();
        if (mNotify != null) uri = BaseContentProvider.notify(uri, mNotify);
        if (mGroupBy != null) uri = BaseContentProvider.groupBy(uri, mGroupBy);
        if (mHaving != null) uri = BaseContentProvider.having(uri, mHaving);
        if (mLimit != null) uri = BaseContentProvider.limit(uri, String.valueOf(mLimit));
        return uri;
    }

    protected abstract Uri baseUri();

    /**
     * Deletes row(s) specified by this selection.
     *
     * @param contentResolver The content resolver to use.
     * @return The number of rows deleted.
     */
    public int delete(ContentResolver contentResolver) {
        return contentResolver.delete(uri(), sel(), args());
    }

    /**
     * Deletes row(s) specified by this selection.
     *
     * @param context The context to use.
     * @return The number of rows deleted.
     */
    public int delete(Context context) {
        return context.getContentResolver().delete(uri(), sel(), args());
    }

    /**
     * Sets the notify variable for the selection string.
     * @param notify Whether to notify or not.
     * @return This class instance.
     */
    @SuppressWarnings("unchecked")
    public T notify(boolean notify) {
        mNotify = notify;
        return (T) this;
    }

    /**
     * Sets the value of <code>mGroupBy</code>.
     * @param groupBy String defining the group by clause.
     * @return This class instance.
     */
    @SuppressWarnings("unchecked")
    public T groupBy(String groupBy) {
        mGroupBy = groupBy;
        return (T) this;
    }

    /**
     * Sets the value of the having clause.
     * @param having String defining the having clause.
     * @return This class instance.
     */
    @SuppressWarnings("unchecked")
    public T having(String having) {
        mHaving = having;
        return (T) this;
    }

    /**
     * Sets the maximum number of rows to select.
     * @param limit Maximum number of rows to select.
     * @return This class instance.
     */
    @SuppressWarnings("unchecked")
    public T limit(int limit) {
        mLimit = limit;
        return (T) this;
    }

    /**
     * Appends values to the order by clause.
     * @param order What to order the selection by.
     * @param desc Whether to sort in descending order.
     * @return This class instance.
     */
    @SuppressWarnings("unchecked")
    public T orderBy(String order, boolean desc) {
        if (mOrderBy.length() > 0) mOrderBy.append(COMMA);
        mOrderBy.append(order);
        if (desc) mOrderBy.append(DESC);
        return (T) this;
    }

    /**
     * Appends values to the order by clause.
     * @param order What to order the selection by.
     * @return This class instance.
     */
    public T orderBy(String order) {
        return orderBy(order, false);
    }

    /**
     * Appends values to the order by clause.
     * @param orders What to order the selection by.
     * @return This class instance.
     */
    @SuppressWarnings("unchecked")
    public T orderBy(String... orders) {
        for (String order : orders) {
            orderBy(order, false);
        }
        return (T) this;
    }

    /**
     * Returns the number of rows selected by this object.
     *
     * @param resolver The content resolver to use.
     * @return The number of rows selected by this object.
     */
    public int count(ContentResolver resolver) {
        Cursor cursor = resolver.query(uri(), new String[] {COUNT}, sel(), args(), null);
        if (cursor == null) return 0;
        try {
            return cursor.moveToFirst() ? cursor.getInt(0) : 0;
        } finally {
            cursor.close();
        }
    }

    /**
     * Returns the number of rows selected by this object.
     *
     * @param context The context to use.
     * @return The number of rows selected by this object.
     */
    public int count(Context context) {
        return count(context.getContentResolver());
    }

    /**
     * Returns a <code>CursorLoader</code> based on this selection.
     *
     * @param context The context to use.
     * @param projection The projection to use.
     * @return The CursorLoader.
     */
    public abstract CursorLoader getCursorLoader(Context context, String[] projection);

    /**
     * Returns a <code>CursorLoader</code> based on this selection, with a <code>null</code> (all columns) selection.
     *
     * @param context The context to use.
     * @return The CursorLoader.
     */
    public CursorLoader getCursorLoader(Context context) {
        return getCursorLoader(context, null);
    }
}
