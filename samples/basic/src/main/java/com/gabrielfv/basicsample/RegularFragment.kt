package com.gabrielfv.basicsample

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.gabrielfv.crane.core.Route
import com.gabrielfv.crane.ktx.params
import kotlinx.parcelize.Parcelize

@Parcelize
data class RegularRoute(val from: String, val count: Int) : Route

@Parcelize
data class RegularResult(val count: Int) : Parcelable

class RegularFragment : Fragment() {
  private val params: RegularRoute by params()
  private val crane by lazy { NavRegistry.crane }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.regular_fragment, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val count = params.count + 1
    view.findViewById<TextView>(R.id.broughtBy).apply {
      text = "Brought to you by ${params.from} #$count"
      setOnClickListener {
        crane.popAffinity()
      }
    }
    crane.pushResult(RegularResult(count))
  }
}
