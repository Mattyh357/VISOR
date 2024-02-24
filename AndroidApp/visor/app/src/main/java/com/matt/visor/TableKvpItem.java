/**
 * This class is part of the V.I.S.O.R app.
 * The TableKvpItem is a data construct to hold 1 item that will be displayed in a recycling view.
 * Using generics, it can take key as a string and value of anything it wants.
 *
 * @version 1.0
 * @since 21/02/2024
 */

package com.matt.visor;

public class TableKvpItem<T> {

    private final String _key;
    private final String _key_readable;
    private T _value;

    /**
     * Constructor for TableKvpItem with key, readable key, and value of type T.
     *
     * @param key The key identifier.
     * @param key_readable The human-readable form of the key.
     * @param value The value associated with the key, of type T.
     */
    public TableKvpItem(String key, String key_readable, T value) {
        _key = key;
        _key_readable = key_readable;
        _value = value;
    }

    /**
     * Retrieves the key identifier.
     *
     * @return The key.
     */
    public String getKey() {
        return _key;
    }

    /**
     * Retrieves the human-readable form of the key.
     *
     * @return The readable key.
     */
    public String getKeyReadable() {
        return _key_readable;
    }

    /**
     * Retrieves the value associated with the key.
     *
     * @return The value of type T.
     */
    public T getValue() {
        return _value;
    }

    /**
     * Assigns a new value to the field if it matches the expected type.
     *
     * @param value The new value to assign.
     */

    public void setValue(Object value) {

        if (value != null && _value != null && _value.getClass().isAssignableFrom(value.getClass())) {
            _value = (T) value;
        }
    }

    /**
     * Sets the field value if the specified key matches the current key.
     *
     * @param key The key to check.
     * @param value The value to set if keys match.
     * @return True if the value was set, false otherwise.
     */
    public boolean setValueIfKey(String key, Object value) {
        if(key.equals(_key)){
            setValue(value);
            return true;
        }
        return false;
    }



}
