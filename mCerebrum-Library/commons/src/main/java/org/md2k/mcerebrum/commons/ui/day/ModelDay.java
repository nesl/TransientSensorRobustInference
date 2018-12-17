package org.md2k.mcerebrum.commons.ui.day;

import android.content.Context;
import android.util.Log;

import org.md2k.datakitapi.DataKitAPI;
import org.md2k.datakitapi.datatype.DataType;
import org.md2k.datakitapi.datatype.DataTypeLong;
import org.md2k.datakitapi.exception.DataKitException;
import org.md2k.datakitapi.source.datasource.DataSourceBuilder;
import org.md2k.datakitapi.source.datasource.DataSourceClient;
import org.md2k.datakitapi.source.datasource.DataSourceType;
import org.md2k.datakitapi.time.DateTime;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Copyright (c) 2015, The University of Memphis, MD2K Center
 * - Syed Monowar Hossain <monowar.hossain@gmail.com>
 * All rights reserved.
 * <p/>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * <p/>
 * * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * <p/>
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * <p/>
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
public class ModelDay {
    private long dayStart;
    private long dayEnd;
    private long wakeupTime;
    private long sleepTime;
    private Context context;
    private long wakeupTimeOffset;
    private long sleepTimeOffset;

    public ModelDay(Context context, long wakeupTimeOffset, long sleepTimeOffset) {
        this.context = context;
        wakeupTime = -1;
        sleepTime = -1;
        dayStart = -1;
        dayEnd = -1;
        this.wakeupTimeOffset = wakeupTimeOffset;
        this.sleepTimeOffset = sleepTimeOffset;
    }

    public long getDayStart() {
        return dayStart;
    }

    public long getDayEnd() {
        return dayEnd;
    }

    public long getWakeupTime() {
        return wakeupTime;
    }

    public long getSleepTime() {
        return sleepTime;
    }

    public long getWakeupTimeOffset() {
        return wakeupTimeOffset;
    }

    public long getSleepTimeOffset() {
        return sleepTimeOffset;
    }

    public boolean isActiveDay() {
        long now = DateTime.getDateTime();
        if (dayStart == -1) return false;
        if (now < dayStart) return false;
        if (dayStart < DateTime.getTodayAtInMilliSecond("00:00:00")) return false;
        if (dayStart < dayEnd) return false;
        return true;
    }

    private boolean isDayEnded() {
        long now = DateTime.getDateTime();
        if (dayStart == -1) return false;
        if (now < dayStart) return false;
        if (dayStart < DateTime.getTodayAtInMilliSecond("00:00:00")) return false;
        if (dayStart < dayEnd) return true;
        return false;
    }

    private long readDay(Context context, DataSourceBuilder dataSourceBuilder) throws DataKitException {
        DataKitAPI dataKitAPI = DataKitAPI.getInstance(context);
        ArrayList<DataSourceClient> dataSourceClients = dataKitAPI.find(dataSourceBuilder);
        if (dataSourceClients.size() > 0) {
            ArrayList<DataType> dataTypes = dataKitAPI.query(dataSourceClients.get(0), 1);
            if (dataTypes.size() != 0) {
                DataTypeLong d = (DataTypeLong) dataTypes.get(0);
                return d.getSample();
            }
        }
        return -1;
    }

    private long readWS(Context context, DataSourceBuilder dataSourceBuilder) throws DataKitException {
            DataKitAPI dataKitAPI = DataKitAPI.getInstance(context);
            ArrayList<DataSourceClient> dataSourceClients = dataKitAPI.find(dataSourceBuilder);
            if (dataSourceClients.size() > 0) {
                ArrayList<DataType> dataTypes = dataKitAPI.query(dataSourceClients.get(0), 1);
                if (dataTypes.size() != 0) {
                        DataTypeLong d = (DataTypeLong) dataTypes.get(0);
                        return d.getSample();

                }
            }
        return -1;
    }

    public void insert(String type) throws DataKitException {
        Log.d("abc", "Day: DataKitInsert -> (Day, " + type + ")");
        long curTime = DateTime.getDateTime();
        DataKitAPI dataKitAPI = DataKitAPI.getInstance(context);
        DataSourceClient ds = dataKitAPI.register(new DataSourceBuilder().setType("DAY").setId(type));
        dataKitAPI.insert(ds, new DataTypeLong(curTime, curTime));
        if (type.equals("START"))
            dayStart = curTime;
        else dayEnd = curTime;
    }

    boolean isEnableStart() {
        long now = DateTime.getDateTime();
        long today = DateTime.getTodayAtInMilliSecond("00:00:00");
        if (isActiveDay()) return false;
        if (isDayEnded()) return false;
        long wx = today + wakeupTime - wakeupTimeOffset;
        long s = today + sleepTime;
        if (s < wx) s += 24 * 60 * 60 * 1000;
        if (now < wx) return false;
        if (s < now) return false;
        return true;
    }

    boolean isNotify() {
        long now = DateTime.getDateTime();
        long today = DateTime.getTodayAtInMilliSecond("00:00:00");
        long wx = today + wakeupTime;
        long s = today + sleepTime;
        if (s < wx) s += 24 * 60 * 60 * 1000;
        Log.d("abc", "DAY: ModelDay -> isNotify ->isActiveDay=" + isActiveDay() + " wx-now=" + (wx - now) + " now-s=" + (now - s));
        if (isActiveDay()) return false;
        if (now < wx) return false;
        if (s < now) return false;
        return true;
    }

    public boolean isEnableEnd() {
        long now = DateTime.getDateTime();
        long today = DateTime.getTodayAtInMilliSecond("00:00:00");
        if (isActiveDay()) return false;
        long wx = today + wakeupTime - wakeupTimeOffset;
        long s = today + sleepTime;
        if (s < wx) s += 24 * 60 * 60 * 1000;
        if (now < wx) return false;
        if (s < now) return false;
        return true;

    }

    void set() throws DataKitException {
//        wakeupTime = DateTime.getTimeInMillis("05:33:00");
        wakeupTime = readWS(context, new DataSourceBuilder().setType(DataSourceType.WAKEUP));
        Log.d("abc", "Day: ModelDay: set() -> wakeup=" + DateTime.convertTimestampToTimeStr(wakeupTime));
//        sleepTime = DateTime.getTimeInMillis("05:33:30");

        sleepTime = readWS(context, new DataSourceBuilder().setType(DataSourceType.SLEEP));
        Log.d("abc", "Day: ModelDay: set() -> sleeptime=" + DateTime.convertTimestampToTimeStr(sleepTime));
        dayStart = readDay(context, new DataSourceBuilder().setType("DAY").setId("START"));
        Log.d("abc", "Day: ModelDay: set() -> dayStart=" + DateTime.convertTimeStampToDateTime(dayStart));
        dayEnd = readDay(context, new DataSourceBuilder().setType("DAY").setId("END"));
        Log.d("abc", "Day: ModelDay: set() -> dayEnd=" + DateTime.convertTimeStampToDateTime(dayEnd));
    }
}
