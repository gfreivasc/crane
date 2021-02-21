package com.gabrielfv.crane

import androidx.annotation.AnimRes

data class FragmentAnimation(
    @AnimRes val enter: Int = android.R.anim.slide_in_left,
    @AnimRes val exit: Int = android.R.anim.fade_out,
    @AnimRes val popEnter: Int = android.R.anim.fade_in,
    @AnimRes val popExit: Int = android.R.anim.slide_out_right
)