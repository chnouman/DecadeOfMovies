package com.noumanch.decadeofmovies.utils

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.noumanch.decadeofmovies.R

fun Fragment.showAlertDialog(
    context: Context,
    @DrawableRes drawableId: Int,
    @StringRes titleStringId: Int,
    messageStringId: String,
    positiveButtonOnClick: DialogInterface.OnClickListener?
) {
    AlertDialog.Builder(context)
        .setIcon(drawableId)
        .setTitle(titleStringId)
        .setMessage(messageStringId)
        .setPositiveButton(R.string.alert_dialog_ok_button_text, positiveButtonOnClick)
        .setCancelable(false)
        .create()
        .show()
}