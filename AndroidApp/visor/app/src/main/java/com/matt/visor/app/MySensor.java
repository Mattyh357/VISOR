package com.matt.visor.app;


import com.matt.visor.R;

import java.util.HashMap;
import java.util.Map;

public class MySensor {

    private String _name;
    private Status _status;
    private String _address;
    private int _type;

    protected SensorStatusListener _sensorStatusListener;
    protected SensorValueListener _sensorValueListener;


    public static final int TYPE_NONE = 0;
    public static final int TYPE_HR = 6;
    public static final int TYPE_CAD = 7;
    public static final int TYPE_PWR = 8;


    public enum Status {
        NotFound,
        Searching,
        Connecting,
        Connected,
        NotAssigned,
    }

    public MySensor(String name, String address, int type) {
        _name = name;
        _address = address;
        _status = Status.Connecting;
        _type = type;
    }

    public void activate() {
    }

    public void setStatusChangeListener(SensorStatusListener listener) {
        _sensorStatusListener = listener;
    }

    public void setValueChangedListener(SensorValueListener listener) {
        this._sensorValueListener = listener;
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        _name = name;
    }

    public Status getStatus() {
        return _status;
    }

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

        return "I dunno...";

    }


    public void setStatus(Status status) {
        _status = status;
        if(_sensorStatusListener != null)
            _sensorStatusListener.onChange();
    }
//
//    public void setValue() {
//        if(_sensorValueListener != null)
//            _sensorValueListener.onSensorValueChange();
//    }

    public void setAddress(String address) {
        _address = address;
    }

    public int getType() {
        return _type;
    }

    public boolean hasAddress() {
        return _address != null;
    }


    public Map<String, Object> getValues() {
        Map<String, Object> values = new HashMap<>();

        values.put("heartRate", 123);

        return values;
    }

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
