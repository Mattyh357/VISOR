package com.matt.visor;

import java.util.Map;

public interface RecorderListener {

    void onNewData(Map<String, Object> data);

}
