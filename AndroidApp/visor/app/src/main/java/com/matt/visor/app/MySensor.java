/**
 * This class is part of the V.I.S.O.R app.
 * The MySensor class is a base class to be used for all sensors. 
 *
 * @version 1.0
 * @since 21/02/2024
 */

package com.matt.visor.app;

import com.matt.visor.R;

import java.util.HashMap;
import java.util.Map;

public class MySensor {

    private String _name;
    private Status _status;
    private String _address;
    private final int _type;

    protected SensorStatusListener _sensorStatusListener;
    protected SensorValueListener _sensorValueListener;

    public static final int TYPE_NONE = 0;
    public static final int TYPE_HR = 6;
    public static final int TYPE_CAD = 7;
    public static final int TYPE_PWR = 8;

    /**
     * Enum for sensor status
     */
    public enum Status {
        NotFound,
        Searching,
        Connecting,
        Connected,
        NotAssigned,
    }

    /**
     * Initializes MySensor class
     * @param name Name of the sensor
     * @param address BLE address of the sensor
     * @param type Type of the sensor
     */
    public MySensor(String name, String address, int type) {
        _name = name;
        _address = address;
        _status = Status.Connecting;
        _type = type;
    }

    /**
     * Activates the sensor - in case sensor is sleeping / off
     */
    public void activate() {
    }

    /**
     * Set Status listener - used when sensor changes its status
     * @param listener StatusSensor listener
     */
    public void setStatusChangeListener(SensorStatusListener listener) {
        _sensorStatusListener = listener;
    }

    /**
     * Set values listener - used when the sensor changes its value
     * @param listener SensorValue listener
     */
    public void setValueChangedListener(SensorValueListener listener) {
        this._sensorValueListener = listener;
    }

    /** Gets the name of the sensor
     * @return Name of the sensor
     */
    public String getName() {
        return _name;
    }

    /**
     * Sets the name of the sensor
     * @param name Name of the sensor
     */
    public void setName(String name) {
        _name = name;
    }

    /**
     * Gets the status of sensor
     * @return Status of the sensor
     */
    public Status getStatus() {
        return _status;
    }

    /**
     * Gets the status of the sensor as a formatted String
     * Probably should NOT be hardcoded in English :)
     * @return Sensor status as a String
     */
    public String getStatusString() {

        if(_address == null)
            return "EMPTY!!!";

        switch (_status) {

            case NotFound:
                return "Not found";
            case Searching:
                return "Searching...";
            case Connecting:
                return "Connecting...";
            case Connected:
                return "Connected";
            case NotAssigned:
                break;
        }

        return "Error...";

    }

    /**
     * Sets the status of the sensor and calls the status Listener
     * @param status Status of the sensor
     */
    public void setStatus(Status status) {
        _status = status;
        if(_sensorStatusListener != null)
            _sensorStatusListener.onChange();
    }

    /**
     * Sets the values of the sensor and calls the value Listener
     */
    public void setValue() {
        if(_sensorValueListener != null)
            _sensorValueListener.onSensorValueChange();
    }

    /**
     * Sets the address of the sensor
     * @param address Address of the sensor
     */
    public void setAddress(String address) {
        _address = address;
    }

    /**
     * Gets the type of the sensor
     * @return Type of the sensor
     */
    public int getType() {
        return _type;
    }

    /**
     * Returns indication whether or not the sensor has assigned address
     * @return True if address has been assigned
     */
    public boolean hasAddress() {
        return _address != null;
    }

    /**
     * Gets values of the sensors as a map
     * @return Map representation of the values
     */
    public Map<String, Object> getValues() {
        Map<String, Object> values = new HashMap<>();

        values.put("heartRate", 123);

        return values;
    }

    /**
     * Gets drawable image for the sensor based on its type
     * @return Image for the sensor
     */
    public int getImage() {
        switch (_type) {
            case TYPE_HR:
                return R.drawable.icon_hr;
            case TYPE_CAD:
                return R.drawable.icon_cad;
            case TYPE_PWR:
                return R.drawable.icon_pwr;
            default:
                return R.drawable.splash_screen;
        }
    }


}
