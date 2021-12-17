package com.dummy.b

import android.os.Parcel
import androidx.fragment.app.Fragment
import com.gabrielfv.crane.annotations.RoutedBy
import com.gabrielfv.crane.core.Route

@Suppress("ParcelCreator")
data class MMRootRoute(val i: Int) : Route {
  override fun describeContents(): Int = 0
  override fun writeToParcel(dest: Parcel?, flags: Int) { }
}

@RoutedBy(MMRootRoute::class)
class MultiModRootFragment : Fragment()
