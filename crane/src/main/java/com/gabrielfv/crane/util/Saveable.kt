package com.gabrielfv.crane.util

import android.os.Bundle

interface Saveable {

    fun save(outState: Bundle)

    fun restore(savedState: Bundle)
}