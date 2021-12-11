package com.gabrielfv.crane.core

import android.os.Parcel

interface Route {
  fun describeContents(): Int
  fun writeToParcel(dest: Parcel?, flags: Int)
}
