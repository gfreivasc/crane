package com.gabrielfv.basicsample.collapsibleflow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.gabrielfv.basicsample.R
import com.gabrielfv.crane.core.Crane
import com.gabrielfv.crane.core.Route
import com.gabrielfv.crane.ktx.params
import kotlinx.parcelize.Parcelize
import kotlin.random.Random

@Parcelize
data class CFSecondRoute(val id: Int) : Route

class CFSecondFragment : Fragment() {
  private val crane = Crane.getInstance()
  private val params: CFSecondRoute by params()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.cfsecond_fragment, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val finishText = view.findViewById<TextView>(R.id.finishText)
    val finishButton = view.findViewById<Button>(R.id.finishButton)
    finishText.text = "This is page 2 for user #${params.id}!"
    finishButton.setOnClickListener {
      crane.pushResult(CollapsibleResult(params.id + Random(params.id).nextInt()))
      crane.popAffinity()
      crane.push(CFSuccessRoute())
    }
  }
}
