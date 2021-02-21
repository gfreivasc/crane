package com.gabrielfv.crane.core.affinity

import com.gabrielfv.crane.core.Route

interface AffinityRoute : Route {
    val tag: String get() = hashCode().toString()
}