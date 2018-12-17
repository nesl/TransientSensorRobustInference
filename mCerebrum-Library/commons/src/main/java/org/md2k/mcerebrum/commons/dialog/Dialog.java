/*
 * Copyright (c) 2018, The University of Memphis, MD2K Center of Excellence
 *
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

package org.md2k.mcerebrum.commons.dialog;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.md2k.datakitapi.DataKitAPI;
import org.md2k.datakitapi.datatype.DataTypeLong;
import org.md2k.datakitapi.exception.DataKitException;
import org.md2k.datakitapi.time.DateTime;
import org.md2k.mcerebrum.commons.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import es.dmoral.toasty.Toasty;

import static android.R.id.input;

/**
 * Provides methods for creating dialogs and capturing user intaction with them.
 */
public class Dialog {
    /**
     * Creates a single choice dialog.
     * @param activity Activity needing a dialog.
     * @param title Dialog title.
     * @param items Array of dialog items.
     * @param selected Selected items.
     * @param dialogCallback Dialog callback interface.
     * @return A builder for a <code>MaterialDialog</code>.
     */
    public static MaterialDialog.Builder singleChoice(Activity activity, String title, String[] items,
                                                      int selected, final DialogCallback dialogCallback) {
        ArrayList<String> tempItems = new ArrayList<>();
        Collections.addAll(tempItems, items);
        return new MaterialDialog.Builder(activity)
                .title(title)
                .items(items)
                .itemsCallbackSingleChoice(selected, new MaterialDialog.ListCallbackSingleChoice() {
                    /**
                     * Passes the selected text to the dialog callback interface.
                     * @param dialog Dialog receiving input.
                     * @param view A builder object for a <code>MaterialDialog</code>.
                     * @param which Dialog action that was clicked.
                     * @param text Button text.
                     * @return Always returns true.
                     */
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        dialogCallback.onSelected(text.toString());
                        return true;
                    }
                })
                .autoDismiss(true)
                .cancelable(true);
    }

    /**
     * Creates a single choice confirmation dialog.
     * @param activity Activity needing a dialog.
     * @param title Dialog title.
     * @param items Array of items for the dialog.
     * @param selected Selected item.
     * @param dialogCallback Dialog callback interface.
     * @return A builder for a <code>MaterialDialog</code>.
     */
    public static MaterialDialog.Builder singleChoiceConfirm(final Activity activity, String title,
                                                             final String[] items, int selected,
                                                             final DialogCallback dialogCallback) {
        return new MaterialDialog.Builder(activity)
                .title(title)
                .items(items)
                .positiveText("Select")
                .negativeText("Cancel")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    /**
                     * Passes the positive button press to the dialog callback.
                     * @param dialog Dialog that was clicked.
                     * @param which Dialog action that was clicked.
                     */
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if(dialog.getSelectedIndex() == -1){
                            Toasty.error(activity, "File is not selected", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        dialogCallback.onSelected(items[dialog.getSelectedIndex()]);
                        dialog.dismiss();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    /**
                     * Passes the negative button press to the dialog callback.
                     * @param dialog Dialog that was clicked.
                     * @param which Dialog action that was clicked.
                     */
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialogCallback.onSelected(null);
                        dialog.dismiss();
                    }
                })
                .itemsCallbackSingleChoice(selected, new MaterialDialog.ListCallbackSingleChoice() {
                    /**
                     * Always returns false on single choice selection.
                     * @param dialog Dialog receiving input.
                     * @param view The dialog view
                     * @param which Dialog action that was clicked.
                     * @param text
                     * @return Always returns false.
                     */
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        return false;
                    }
                })
                .autoDismiss(false)
                .cancelable(false);
    }

    /**
     * Creates a dialog that allows the user to change a text field.
     * @param activity Activity needing a dialog.
     * @param title Dialog title.
     * @param content Dialog content.
     * @param input User input.
     * @param dialogCallback Dialog callback interface.
     * @return A builder for a <code>MaterialDialog</code>.
     */
    public static MaterialDialog.Builder editboxText(Activity activity, String title, String content,
                                                     String input, final DialogCallback dialogCallback){
        return new MaterialDialog.Builder(activity)
                .title(title)
                .content(content)
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input("", input, new MaterialDialog.InputCallback() {
                    /**
                     * Passes the input to the dialog callback interface as a string.
                     * @param dialog Dialog receiving input.
                     * @param input Input to be parsed.
                     */
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        dialogCallback.onSelected(input.toString());
                    }
                });
    }

    /**
     * Creates a dialog that allows the user to change a string value.
     * @param activity Activity needing a dialog.
     * @param title Dialog title.
     * @param content Dialog content.
     * @param dialogCallback Dialog callback interface.
     * @return A builder for a <code>MaterialDialog</code>.
     */
    public static MaterialDialog.Builder editbox(Activity activity, String title, String content,
                                                 final DialogCallback dialogCallback){
        return new MaterialDialog.Builder(activity)
                .title(title)
                .content(content)
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input("abc", "", new MaterialDialog.InputCallback() {
                    /**
                     * Passes the input to the dialog callback interface as a string.
                     * @param dialog Dialog receiving input.
                     * @param input Input to be parsed.
                     */
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        dialogCallback.onSelected(input.toString());
                    }
                });
    }


    /**
     * Creates a dialog that allows the user to change a numeric value.
     * @param activity Activity needing a dialog.
     * @param title Dialog title.
     * @param content Dialog content.
     * @param selectedValue Value to edit.
     * @param dialogCallback Dialog callback interface.
     * @return A builder for a <code>MaterialDialog</code>.
     */
    public static MaterialDialog.Builder editbox_numeric(Activity activity, String title, String content,
                                                         String selectedValue, final DialogCallback dialogCallback){
        return new MaterialDialog.Builder(activity)
                .title(title)
                .content(content)
                .inputType(InputType.TYPE_CLASS_NUMBER)
                .input(null, selectedValue, new MaterialDialog.InputCallback() {
                    /**
                     * Passes the input to the dialog callback interface as a string.
                     * @param dialog Dialog receiving input.
                     * @param input Input to be parsed.
                     */
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        dialogCallback.onSelected(input.toString());
                    }
                });
    }

    /**
     * Contructs a simple positive/negative reponse dialog.
     * @param activity Activity needing a dialog.
     * @param title Dialog title.
     * @param content Dialog content.
     * @param buttonPositive Button denoting a positive response.
     * @param buttonNegative Button denoting a negative response.
     * @param dialogCallback Dialog callback interface.
     * @return A builder for a <code>MaterialDialog</code>.
     */
    public static MaterialDialog.Builder simple(Activity activity, String title, String content,
                                                final String buttonPositive, final String buttonNegative,
                                                final DialogCallback dialogCallback) {
        return new MaterialDialog.Builder(activity)
                .title(title)
                .content(content)
                .positiveText(buttonPositive)
                .negativeText(buttonNegative)
                .cancelable(false)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    /**
                     * Passes the positive button press to the dialog callback.
                     * @param dialog Dialog that was clicked.
                     * @param which Dialog action that was clicked.
                     */
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialogCallback.onSelected(buttonPositive);
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    /**
                     * Passes the negative button press to the dialog callback.
                     * @param dialog Dialog that was clicked.
                     * @param which Dialog action that was clicked.
                     */
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialogCallback.onSelected(buttonNegative);
                    }
                });
    }

    /**
     * Creates a dialog with a progress bar.
     * @param activity Activity needing a dialog.
     * @param content Dialog content.
     * @return A builder object for a <code>MaterialDialog</code>.
     */
    public static MaterialDialog.Builder progressWithBar(Activity activity, String content){
        return new MaterialDialog.Builder(activity)
                .content(content)
                .progress(false, 100, true)
                .cancelable(false)
                .autoDismiss(false);
    }

    /**
     * Creates an indeterminate progress dialog.
     * @param activity Activity needing a dialog.
     * @param content Dialog content.
     * @return A builder object for a <code>MaterialDialog</code>.
     */
    public static MaterialDialog.Builder progressIndeterminate(Activity activity, String content){
        return new MaterialDialog.Builder(activity)
                .content(content)
                .progress(true, 100)
                .cancelable(false)
                .autoDismiss(false);

    }

    /**
     * Creates a new dialog for picking the date.
     * @param activity Activity needing a dialog.
     * @param year Initial year.
     * @param month Initial month (0 - 11)
     * @param day Initial day (1 - 31)
     * @param dialogCallback Dialog callback interface.
     * @return A constructed <code>DataPickerDialog</code>.
     */
    public static DatePickerDialog dateTimePicker(Activity activity, int year, int month, int day,
                                                  final DialogCallback dialogCallback) {
        return new DatePickerDialog(activity, new DatePickerDialog.OnDateSetListener() {
            /**
             * When the user picks a date, the calendar is set, and the time in milliseconds is passed
             * to the dialog callback.
             * @param view The Date picker being shown.
             * @param year Year the user chose.
             * @param month Month the user chose.
             * @param dayOfMonth Day the user chose.
             */
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar c = Calendar.getInstance();
                c.set(year, month, dayOfMonth, 0,0, 0);
                c.set(Calendar.MILLISECOND, 0);
                long time = c.getTimeInMillis();
                dialogCallback.onSelected(String.valueOf(time));
            }
        },year, month, day);
    }

    /**
     * Creates a new dialog for picking the time.
     * @param activity Activity needing a dialog.
     * @param hourOfDay Initial hour.
     * @param minute Initial minute.
     * @param dialogCallback Dialog callback interface.
     * @return A constructed <code>TimePickerDialog</code>.
     */
    public static TimePickerDialog timePicker(Activity activity, int hourOfDay, int minute,
                                              final DialogCallback dialogCallback) {
        return new TimePickerDialog(activity,
                new TimePickerDialog.OnTimeSetListener() {

                    /**
                     * When the user sets the time, it is converted to milliseconds and passed to the
                     * dialog callback interface.
                     * @param view Time picker being shown.
                     * @param hourOfDay Hour the user chose.
                     * @param minute Minute value the user chose.
                     */
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        long time = hourOfDay * 60 * 60 * 1000 + minute * 60 * 1000;
                        dialogCallback.onSelected(String.valueOf(time));
                    }
                }, hourOfDay, minute, false);
    }
}
