package com.gabrielfv.crane.util

import android.os.Bundle

interface Savable {

  fun save(outState: Bundle)

  fun restore(savedState: Bundle)
}
