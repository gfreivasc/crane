package com.gabrielfv.basicsample

import com.gabrielfv.basicsample.collapsibleflow.CFFirstFragment
import com.gabrielfv.basicsample.collapsibleflow.CFFirstRoute
import com.gabrielfv.basicsample.collapsibleflow.CFSecondFragment
import com.gabrielfv.basicsample.collapsibleflow.CFSecondRoute
import com.gabrielfv.basicsample.collapsibleflow.CFSuccessFragment
import com.gabrielfv.basicsample.collapsibleflow.CFSuccessRoute

val routeMap = mapOf(
  FirstRoute::class to FirstFragment::class,
  SecondRoute::class to SecondFragment::class,
  CFFirstRoute::class to CFFirstFragment::class,
  CFSecondRoute::class to CFSecondFragment::class,
  CFSuccessRoute::class to CFSuccessFragment::class,
  RegularRoute::class to RegularFragment::class,
)
