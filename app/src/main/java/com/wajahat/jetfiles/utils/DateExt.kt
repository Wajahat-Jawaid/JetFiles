package com.wajahat.jetfiles.utils

import java.text.SimpleDateFormat
import java.util.*

fun Long.formattedDate(): String {
    val sdf = SimpleDateFormat("dd MMM, yyyy HH:mm", Locale.getDefault())
    return sdf.format(this)
}