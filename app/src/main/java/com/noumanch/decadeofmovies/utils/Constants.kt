package com.noumanch.decadeofmovies.utils

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color


object Constants {

     fun getMatColor(context: Context,typeColor: String): Int {
        var returnColor: Int = Color.BLACK
        val arrayId: Int = context.resources.getIdentifier(
            "mdcolor_$typeColor",
            "array",
            context.packageName
        )
        if (arrayId != 0) {
            val colors: TypedArray = context.resources.obtainTypedArray(arrayId)
            val index = (Math.random() * colors.length()).toInt()
            returnColor = colors.getColor(index, Color.BLACK)
            colors.recycle()
        }
        return returnColor
    }
}