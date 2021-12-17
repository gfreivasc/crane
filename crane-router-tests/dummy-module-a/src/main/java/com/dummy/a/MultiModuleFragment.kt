package com.dummy.a

import android.os.Parcel
import androidx.fragment.app.Fragment
import com.gabrielfv.crane.annotations.RoutedBy
import com.gabrielfv.crane.core.Route

@Suppress("ParcelCreator")
object MultiModuleRoute : Route {
  override fun describeContents(): Int = 0
  override fun writeToParcel(dest: Parcel?, flags: Int) { }
}

@RoutedBy(MultiModuleRoute::class)
class MultiModuleFragment : Fragment()
