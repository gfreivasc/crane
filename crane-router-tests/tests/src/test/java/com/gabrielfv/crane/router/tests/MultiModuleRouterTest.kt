package com.gabrielfv.crane.router.tests

import com.dummy.a.MultiModuleFragment
import com.dummy.a.MultiModuleRoute
import com.dummy.b.MMRootRoute
import com.dummy.b.MultiModRootFragment
import com.gabrielfv.crane.core.RouteMap
import org.assertj.core.api.Assertions
import org.junit.Test

class MultiModuleRouterTest {

  @Test
  fun multiModuleRouteMap_compiledCorrectly() {
    val routeMap: RouteMap = com.dummy.b.getRoutes()

    Assertions.assertThat(routeMap).containsKeys(MultiModuleRoute::class, MMRootRoute::class)
    Assertions.assertThat(routeMap[MultiModuleRoute::class]).isEqualTo(MultiModuleFragment::class)
    Assertions.assertThat(routeMap[MMRootRoute::class]).isEqualTo(MultiModRootFragment::class)
  }
}
