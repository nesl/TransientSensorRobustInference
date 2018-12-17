package org.md2k.mcerebrum.commons.ui.day;
/*
 * Copyright (c) 2016, The University of Memphis, MD2K Center
 * - Syed Monowar Hossain <monowar.hossain@gmail.com>
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

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.md2k.datakitapi.exception.DataKitException;
import org.md2k.datakitapi.time.DateTime;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class ControllerDay {
    private static final long DAYS_IN_MILLIS=24*60*60*1000;
    private Context context;
    private ViewDay viewDay;
    private ModelDay modelDay;
    private Subscription subscription;
    public ControllerDay(final Context context, ViewDay viewDay, final ModelDay modelDay) {
        this.context = context;
        this.viewDay = viewDay;
        this.modelDay = modelDay;
        this.viewDay.setCallbackDay(new CallbackDay() {
            @Override
            public void onReceive(String type) {
                try {
                    modelDay.insert(type);
                    stop();
                    start();
                } catch (DataKitException e) {
                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(DataKitException.class.getSimpleName()));
                }

            }
        });
    }
    public ViewDay getViewDay() {
        return viewDay;
    }

    public ModelDay getModelDay() {
        return modelDay;
    }

    public void start() {
        stop();
        Log.d("abc","DAY: ControllerDay -> start()");
        try {
            modelDay.set();
        } catch (DataKitException e) {
            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(DataKitException.class.getSimpleName()));
        }
        subscription = Observable.merge(Observable.just(-1), getObservableWakeupOffset(), getObservableWakeup(), getObservableSleepOffset(), getObservableSleep()).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<Integer, Integer>() {
                    @Override
                    public Integer call(Integer integer) {
                        Log.d("abc","Day: ControllerDay -> Observable from wakeup, sleep...received status="+integer);

                        switch(integer){
                            case -1:
                                break;
                            case 0:
                                break;
                            case 1:
                                break;
                            case 2:
                                break;
                            case 3:
                                try {
                                    if (modelDay.isActiveDay())
                                        modelDay.insert("END");
                                } catch (DataKitException e) {
                                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(DataKitException.class.getSimpleName()));
                                }
                                break;
                        }
                        return integer;
                    }
                }).doOnUnsubscribe(new Action0() {
                    @Override
                    public void call() {
                        try {
                            viewDay.removeNotify();
                        }catch (Exception e){}
                    }
                }).doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        try {
                            viewDay.removeNotify();
                        }catch (Exception e){}
                    }
                }).subscribe(new Observer<Integer>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        viewDay.removeNotify();
                        if(modelDay.isActiveDay()) {
                            Log.d("abc","DAY: ControllerDay ->onNext() -> isActiveDay = true");
                            viewDay.setStartButton(false, 0xff5cb85c, "Day started: " + DateTime.convertTimeStampToDateTime(modelDay.getDayStart(), "hh:mm aaa")+"\n");
                            viewDay.setEndButton(true, Color.WHITE, "Day End\n");
                        }
                        else if(modelDay.isEnableStart()) {
                            Log.d("abc","DAY: ControllerDay ->onNext() -> isEnableStart = true");
                            viewDay.setStartButton(true, 0xffffbb33, "Day Start\n");
                            viewDay.setEndButton(false, Color.WHITE, "Day End\nDay is not started yet");
                            if(modelDay.isNotify()) {
                                Log.d("abc","DAY: ControllerDay ->onNext() -> isNotify() = true");
                                viewDay.setNotify(null, 30 * 1000);
                            }else
                                Log.d("abc","DAY: ControllerDay ->onNext() -> isNotify() = false");
                        }
                        else {
                            viewDay.setStartButton(false, Color.WHITE, "Day will resume at\n" + DateTime.convertTimeStampToDateTime(DateTime.getTodayAtInMilliSecond("00:00:00")+modelDay.getWakeupTime(), "hh:mm aaa"));
                            viewDay.setEndButton(false, Color.WHITE, "Day End\nDay is not started yet");
                        }
                    }
                });
    }
    private Observable<Integer> getObservableWakeupOffset(){
        long now= DateTime.getDateTime();
        long today = DateTime.getTodayAtInMilliSecond("00:00:00");
        long w = today+modelDay.getWakeupTime()-modelDay.getWakeupTimeOffset();
        while(w<now) w+=DAYS_IN_MILLIS;
        Log.d("abc","DAY: ControllerDay -> getObservableWakeOffset() -> next: "+(w-now)/(1000*60)+" second");
        return Observable.interval(w-now,DAYS_IN_MILLIS, TimeUnit.MILLISECONDS).map(new Func1<Long, Integer>() {
            @Override
            public Integer call(Long aLong) {
                return 0;
            }
        });
    }
    private Observable<Integer> getObservableWakeup(){
        long now= DateTime.getDateTime();
        long today = DateTime.getTodayAtInMilliSecond("00:00:00");
        long w = today+modelDay.getWakeupTime();
        while(w<now) w+=DAYS_IN_MILLIS;
        Log.d("abc","DAY: ControllerDay -> getObservableWakeup() -> next: "+(w-now)/(1000*60)+" minute");
        return Observable.interval(w-now,DAYS_IN_MILLIS, TimeUnit.MILLISECONDS).map(new Func1<Long, Integer>() {
            @Override
            public Integer call(Long aLong) {
                return 1;
            }
        });
    }
    private Observable<Integer> getObservableSleepOffset(){
        long now= DateTime.getDateTime();
        long today = DateTime.getTodayAtInMilliSecond("00:00:00");
        long w = today+modelDay.getSleepTime()-modelDay.getSleepTimeOffset();
        while(w<now) w+=DAYS_IN_MILLIS;
        Log.d("abc","DAY: ControllerDay -> getObservablesleepOffset() -> next: "+(w-now)/(1000*60)+" minute");
        return Observable.interval(w-now,DAYS_IN_MILLIS, TimeUnit.MILLISECONDS).map(new Func1<Long, Integer>() {
            @Override
            public Integer call(Long aLong) {
                return 2;
            }
        });
    }
    private Observable<Integer> getObservableSleep(){
        long now= DateTime.getDateTime();
        long today = DateTime.getTodayAtInMilliSecond("00:00:00");
        long w = today+modelDay.getSleepTime();
        while(w<now) w+=DAYS_IN_MILLIS;
        Log.d("abc","DAY: ControllerDay -> getObservableSleep() -> next: "+(w-now)/(1000*60)+" minute");
        return Observable.interval(w-now,DAYS_IN_MILLIS, TimeUnit.MILLISECONDS).map(new Func1<Long, Integer>() {
            @Override
            public Integer call(Long aLong) {
                return 3;
            }
        });
    }

    public void stop() {
        Log.d("abc","DAY: ControllerDay -> stop()");
        if(subscription!=null && !subscription.isUnsubscribed())
            subscription.unsubscribe();
        subscription=null;
    }
}
