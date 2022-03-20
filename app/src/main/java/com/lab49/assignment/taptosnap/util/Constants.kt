package com.lab49.assignment.taptosnap.util

import java.util.concurrent.TimeUnit

object Constants {

    const val SWW = "Something went wrong!"
    const val FTG = "Failed to get image!"

    val MAX_TIMER_IN_SECONDS = TimeUnit.MINUTES.toSeconds(2)

    object STATE {
        const val NOT_STARTED = 0
        const val RUNNING = 1
        const val FAILED = 2
        const val SUCCESS = 3
    }

    object KEY {
        const val ITEMS = "key_items"
    }
}