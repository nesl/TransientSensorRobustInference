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
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;

public class PhoneTone{
    private Context context;
    private MediaPlayer mPlayer;

    public PhoneTone(Context context) {
        this.context = context;
    }

    public Observable<Boolean> getObservable(String format, long interval) {
        Log.d("abc", "phoneTone Observable...interval=" + interval);
        load(format);
        return Observable.interval(0,interval, TimeUnit.MILLISECONDS)
                .map(new Func1<Long, Boolean>() {
                    @Override
                    public Boolean call(Long aLong) {
                        play();
                        return false;
                    }
                }).doOnUnsubscribe(new Action0() {
                    @Override
                    public void call() {
                        stop();
                    }
                }).doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        stop();
                    }
                });
    }

    private void stop() {
        try {
            if (mPlayer != null) {
                mPlayer.stop();
                mPlayer.reset();
                mPlayer.release();
            }
            mPlayer = null;
        } catch (Exception ignored) {
            Log.e("abc", "PhoneTone..stop()...failed" + "exception=" + ignored.toString());
        }
    }

    private void load(String filename) {
        try {
            mPlayer = new MediaPlayer();
            Uri myUri = Uri.parse(filename);
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mPlayer.setDataSource(context, myUri);
            mPlayer.prepare();
        } catch (Exception e1) {
            Log.e("abc", "PhoneTone..play()..fileLoad()..failed");
            try {
                AssetFileDescriptor afd = context.getAssets().openFd("tone.mp3");
                mPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                mPlayer.prepare();
            } catch (Exception ignored) {
                Log.e("abc", "PhoneTone..play()..assetload()..failed");
            }
        }
    }

    private void play() {
        try {
            Log.d("abc", "phonetone play...");
            mPlayer.start();
        } catch (Exception e) {
            Log.e("abc", "PhoneTone..play()..start()..failed");
        }
    }
}
