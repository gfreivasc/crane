package com.gabrielfv.basicsample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.gabrielfv.basicsample.collapsibleflow.CollapsibleResult
import com.gabrielfv.crane.core.Crane
import com.gabrielfv.crane.core.Route
import com.gabrielfv.crane.ktx.params
import kotlinx.parcelize.Parcelize

@Parcelize
data class FirstRoute(val name: String) : Route

class FirstFragment : Fragment() {
  private val crane = Crane.getInstance()
  private val params: FirstRoute by params()
  private var count: Int = 0
  private var confirmedId: Int? = null
  private val greeting: String
    get() = "Hello, ${params.name}" + confirmedId?.let { id ->
      ", user id #$id"
    }.orEmpty()

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.first_fragment, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val greeting = view.findViewById<TextView>(R.id.greeting)
    val goToRegular = view.findViewById<Button>(R.id.goToRegular)
    greeting.text = this.greeting
    greeting.setOnClickListener {
      crane.push(SecondRoute(12334))
    }
    goToRegular.setOnClickListener {
      crane.push(RegularRoute(javaClass.simpleName, count))
    }
  }

  override fun onResume() {
    super.onResume()
    crane.fetchResult<RegularResult>()?.let { result ->
      count = result.count
    }
    confirmedId = crane.fetchResult<CollapsibleResult>()?.confirmedAge
    view?.findViewById<TextView>(R.id.greeting)?.text = greeting
  }
}
