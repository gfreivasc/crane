package com.gabrielfv.crane.routersample

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.gabrielfv.crane.core.Route
import com.gabrielfv.crane.router.RoutedBy
import com.gabrielfv.crane.routersample.databinding.BFragmentBinding
import kotlinx.parcelize.Parcelize

@Parcelize
class BRoute : Route

@RoutedBy(BRoute::class)
class BFragment : Fragment(R.layout.b_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        BFragmentBinding.bind(view).run {
            textView.setOnClickListener {
                NavReg.crane.pop()
            }
        }
    }
}
