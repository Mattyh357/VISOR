/**
 * This class is part of the V.I.S.O.R app.
 * The TableKvpItem is a data construct to hold 1 item that will be displayed in a recycling view.
 * Although always a key-value pair, the value can have boolean value to be used in switch.
 *
 * TODO change to generic
 *
 * @version 1.0
 * @since 21/02/2024
 */

package com.matt.visor;

public class TableKvpItem {

    private final String _key;
    private final String key_readable;
    private String value;


    /**
     * Constructor for TableKvpItem with key, readable key, and string value.
     *
     * @param key The key identifier.
     * @param key_readable The human-readable form of the key.
     * @param value The value associated with the key.
     */
    public TableKvpItem(String key, String key_readable, String value) {
        this._key = key;
        this.key_readable = key_readable;
        this.value = value;
    }

    /**
     * Constructor for TableKvpItem with key, readable key, and boolean value.
     *
     * @param key The key identifier.
     * @param key_readable The human-readable form of the key.
     * @param value The boolean value, stored as null in this constructor.
     */
    public TableKvpItem(String key, String key_readable, boolean value) {
        this._key = key;
        this.key_readable = key_readable;
        this.value = null;
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
        return key_readable;
    }

    /**
     * Retrieves the value associated with the key.
     *
     * @return The value.
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value associated with the key.
     *
     * @param value The new value to set.
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Sets the value if the provided key matches the item's key.
     *
     * @param key The key to check.
     * @param value The value to set if the keys match.
     * @return True if the value was set, false otherwise.
     */
    public boolean setValueIfKey(String key, Object value) {
        if(key.equals(_key)){
            setValue(value.toString());
            return true;
        }
        return false;
    }



}
