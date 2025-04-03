package com.gabrielfv.crane.core

import android.os.Parcelable
import androidx.fragment.app.Fragment
import kotlin.reflect.KClass

abstract class Route(val destination: KClass<out Fragment>) : Parcelable
