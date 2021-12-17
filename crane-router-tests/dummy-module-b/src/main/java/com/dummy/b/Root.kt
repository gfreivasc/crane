package com.dummy.b

import com.gabrielfv.crane.annotations.CraneRoot

@CraneRoot
class Root

fun getRoutes() = Router.get()
