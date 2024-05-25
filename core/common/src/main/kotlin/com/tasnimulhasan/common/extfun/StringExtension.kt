package com.tasnimulhasan.common.extfun

import android.util.Base64
import java.text.DecimalFormat
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow


fun String.decode(): String =
    Base64.decode(this.replace(" ", "/"), Base64.DEFAULT).toString(charset("UTF-8"))


fun String.encode(): String =
    Base64.encodeToString(this.toByteArray(charset("UTF-8")), Base64.DEFAULT).replace("/", " ")

fun String.replaceParenthesis(): String = this.replace("[","").replace("]","")



fun List<String>.convertSeatNoToDisplayFormat(): String {
    val displayFormat = StringBuilder()
    this.forEachIndexed { index, s ->
        if (index == (this.size - 1))
            displayFormat.append("[${s}]")
        else displayFormat.append("[${s}],")
    }

    return displayFormat.toString()
}

fun List<Int>.farePerSeat(): String {
    val displayFormat = StringBuilder()
    val uniqueItem = this.distinctBy { it }
    uniqueItem.forEachIndexed { index, s ->
        if (index == (uniqueItem.size - 1))
            displayFormat.append(s)
        else displayFormat.append("$s/")
    }

    return displayFormat.toString()
}


fun Int.amountShortNotation(): String{
    val notation = arrayOf(' ', 'K', 'M', 'B', 'T', 'P', 'E')
    val value = floor(log10(this.toDouble())).toInt()
    val  base = value / 3
    return if (value >= 3 && base < notation.size) {
        DecimalFormat("#0.0").format(this/ 10.0.pow((base * 3).toDouble())) + notation[base]
    } else {
        DecimalFormat("#,##0").format(this)
    }
}

fun Int.commaSeparator(): String {
    return toString()
        .reversed()
        .chunked(3)
        .joinToString(",")
        .reversed()
}

fun Double.commaSeparator() : String{
    val dec = DecimalFormat(doubleAmountFormat(this.roundOfTwoDecimalPlaces().toString()))
    val finalOutput = dec.format(this)
    return finalOutput
}

fun doubleAmountFormat(amount: String): String {
    return when (amount.length) {
        5 -> "##,###.##"
        6 -> "#,##,###.##"
        7 -> "##,##,###.##"
        8 -> "#,##,##,###.##"
        9 -> "##,##,##,###.##"
        10 -> "#,##,##,##,###.##"
        11 -> "##,##,##,##,###.##"
        4 -> "#,###.##"
        else -> "#,###.##"
    }
}

fun String.commaSeparator(): String {
    return this
        .reversed()
        .chunked(3)
        .joinToString(",")
        .reversed()
}