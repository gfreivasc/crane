package com.gabrielfv.crane.router

import androidx.room.compiler.processing.XProcessingEnv
import androidx.room.compiler.processing.XTypeElement

/**
 * It only returns the registrars available within the compiling module.
 *
 * This exists while KSP does not offer a way of fetching registrars from
 * dependency modules and also while room processing does not offer a way
 * to use said APIs from the given backends. Currently we also can't
 * offload that task from room to the actual API and then back.
 */
class ConfinedRegistrarFetcher : RegistrarFetcher {

  override fun fetch(env: XProcessingEnv, fromRound: Set<XTypeElement>): Set<XTypeElement> {
    return fromRound
  }
}
