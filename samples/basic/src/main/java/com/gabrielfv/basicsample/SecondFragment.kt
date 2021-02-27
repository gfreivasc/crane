package com.gabrielfv.basicsample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.gabrielfv.basicsample.collapsibleflow.CFFirstRoute
import com.gabrielfv.crane.core.Route
import com.gabrielfv.crane.ktx.params
import kotlinx.parcelize.Parcelize

@Parcelize
data class SecondRoute(val number: Int) : Route

class SecondFragment : Fragment() {
  private val crane by lazy { NavRegistry.crane }
  private val params: SecondRoute by params()

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.second_fragment, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    view.findViewById<TextView>(R.id.number).apply {
      text = "Has ${params.number} years old"
      setOnClickListener {
        crane.push(CFFirstRoute(params.number))
      }
    }
  }
}
