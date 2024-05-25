package com.tasnimulhasan.ui

import android.content.Context
import android.widget.ArrayAdapter

fun <E> createDropDownAdapter(
    context: Context, dataList: List<E>
): ArrayAdapter<E> {
    return ArrayAdapter(
        context,
        R.layout.item_spninner,
        dataList
    )
}