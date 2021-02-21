package com.gabrielfv.basicsample.collapsibleflow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gabrielfv.basicsample.R
import com.gabrielfv.crane.core.Route
import kotlinx.parcelize.Parcelize

@Parcelize
class CFSuccessRoute : Route

class CFSuccessFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.cfsuccess_fragment, container, false)
    }
}