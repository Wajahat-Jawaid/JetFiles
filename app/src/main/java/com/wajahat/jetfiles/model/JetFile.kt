package com.wajahat.jetfiles.model

data class JetFile(
    val name: String,
    val modifiedAt: Long = 0,
    val size: Long = 0,
    val path: String = ""
)