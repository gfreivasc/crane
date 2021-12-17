package com.gabrielfv.crane.core

import androidx.fragment.app.Fragment
import kotlin.reflect.KClass

typealias RouteMap = Map<KClass<out Route>, KClass<out Fragment>>
