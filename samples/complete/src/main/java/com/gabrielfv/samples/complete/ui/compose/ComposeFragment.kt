package com.gabrielfv.samples.complete.ui.compose

import androidx.fragment.app.Fragment
import com.gabrielfv.crane.annotations.RoutedBy
import com.gabrielfv.crane.core.Route
import com.gabrielfv.samples.complete.R
import kotlinx.parcelize.Parcelize

@Parcelize
data class Compose(val noteId: Long?) : Route

@RoutedBy(Compose::class)
class ComposeFragment : Fragment(R.layout.compose_fragment)
