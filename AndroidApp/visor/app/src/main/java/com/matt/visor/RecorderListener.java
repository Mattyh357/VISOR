package com.matt.visor;

import com.matt.visor.app.RecordDataPoint;

public interface RecorderListener {

    void onNewData(RecordDataPoint recordDataPoint, int elapsedTime);

}
