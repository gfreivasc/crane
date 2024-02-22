package com.gabrielfv.crane

import android.os.Parcel
import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.gabrielfv.crane.core.Route
import com.gabrielfv.crane.core.affinity.AffinityRoute
import com.gabrielfv.crane.ktx.params

data class A(val i: Int) : Route {
  override fun describeContents(): Int = 0
  override fun writeToParcel(dest: Parcel, flags: Int) {}
}

data class Unregistered(val i: Int) : Route {
  override fun describeContents(): Int = 0
  override fun writeToParcel(dest: Parcel, flags: Int) {}
}

data class Result(val i: Int) : Parcelable {
  override fun describeContents(): Int = 0
  override fun writeToParcel(dest: Parcel, flags: Int) {}
}

data class Affinity(val i: Int) : AffinityRoute {
  override fun describeContents(): Int = 0
  override fun writeToParcel(dest: Parcel, flags: Int) {}
}

class AFragment : Fragment() {
  val params: A by params()
}

class TestFragmentFactory(private val fragment: AFragment) : FragmentFactory() {

  override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
    return fragment
  }
}

fun testFragmentFactory(fragment: AFragment) = TestFragmentFactory(fragment)
