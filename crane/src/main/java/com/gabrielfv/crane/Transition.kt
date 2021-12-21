package com.gabrielfv.crane

/**
 * Bit mask that is set for all enter transitions.
 */
internal const val ENTER_MASK = 0x1000

/**
 * Bit mask that is set for all exit transitions.
 */
internal const val EXIT_MASK = 0x2000

enum class Transition(val value: Int) {
  UNSET(-1),
  NONE(0),
  FRAGMENT_OPEN(1 or ENTER_MASK),
  FRAGMENT_CLOSE(2 or EXIT_MASK),
  FRAGMENT_FADE(3 or ENTER_MASK),
  FRAGMENT_MATCH_ACTIVITY_OPEN(4 or ENTER_MASK),
  FRAGMENT_MATCH_ACTIVITY_CLOSE(5 or EXIT_MASK),
}
