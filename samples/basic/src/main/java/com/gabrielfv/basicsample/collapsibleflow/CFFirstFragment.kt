package com.gabrielfv.basicsample.collapsibleflow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.gabrielfv.basicsample.NavRegistry
import com.gabrielfv.basicsample.R
import com.gabrielfv.crane.core.affinity.AffinityRoute
import com.gabrielfv.crane.ktx.params
import kotlinx.parcelize.Parcelize

@Parcelize
data class CFFirstRoute(val age: Int) : AffinityRoute

class CFFirstFragment : Fragment() {
  private val crane by lazy { NavRegistry.crane }
  private val params: CFFirstRoute by params()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.cffirst_fragment, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val confirmText = view.findViewById<TextView>(R.id.confirmText)
    val button = view.findViewById<Button>(R.id.button)
    confirmText.text = "Are you really ${params.age}?"
    button.setOnClickListener {
      crane.push(CFSecondRoute(params.age))
    }
  }
}
