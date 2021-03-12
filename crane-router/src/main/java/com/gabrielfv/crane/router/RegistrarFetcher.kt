package com.gabrielfv.crane.router

import androidx.room.compiler.processing.XProcessingEnv
import androidx.room.compiler.processing.XTypeElement

internal interface RegistrarFetcher {

  fun fetch(env: XProcessingEnv, fromRound: Set<XTypeElement>): Set<XTypeElement>
}
