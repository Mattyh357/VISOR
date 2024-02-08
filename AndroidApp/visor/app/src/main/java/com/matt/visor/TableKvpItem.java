package com.matt.visor;

public class TableKvpItem {

    private String key;

    private String key_readable;
    private String value;


    // TODO delete
    public TableKvpItem(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public TableKvpItem(String key, String key_readable, String value) {
        this.key = key;
        this.key_readable = key_readable;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getKeyReadable() {
        return key_readable;
    }

    public String getValue() {
        return value;
    }


    public void setValue(String value) {
        this.value = value;
    }
}
