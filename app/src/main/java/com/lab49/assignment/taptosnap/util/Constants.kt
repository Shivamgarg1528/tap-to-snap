package com.lab49.assignment.taptosnap.util

import java.util.concurrent.TimeUnit

object Constants {

    val MAX_TIMER_IN_SECONDS = TimeUnit.MINUTES.toSeconds(2)

    object STATE {
        const val NOT_STARTED = 0
        const val RUNNING = 1
        const val FAILED = 2
        const val SUCCESS = 3
    }
}