package com.gabrielfv.crane.core.affinity

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class AffinityManagerTest {

    @Test
    fun onPopAffinity_noAffinity() {
        val subject = AffinityManager()

        val result = subject.popAffinity()

        assertThat(result).isNull()
    }

    @Test
    fun onPopAffinity_withAffinity() {
        val subject = AffinityManager()
        subject.push("A")

        val result = subject.popAffinity()

        assertThat(result).isNotNull
        val (affinity, offset) = result!!
        assertThat(affinity).isEqualTo("A")
        assertThat(offset).isEqualTo(1)
    }

    @Test
    fun onPopAffinity_offsetAffinity() {
        val subject = AffinityManager()
        subject.push("A")
        subject.push()

        val (affinity, offset) = subject.popAffinity()!!

        assertThat(affinity).isEqualTo("A")
        assertThat(offset).isEqualTo(2)
    }

    @Test
    fun onPopAffinity_pushedPoppedBackAffinity() {
        val subject = AffinityManager()
        subject.push("A")
        subject.push()
        subject.popRegular()

        val (affinity, offset) = subject.popAffinity()!!

        assertThat(affinity).isEqualTo("A")
        assertThat(offset).isEqualTo(1)
    }

    @Test
    fun onPopAffinity_multipleOffsetAffinities() {
        val subject = AffinityManager()
        subject.push("A")
        subject.push()
        subject.push("B")
        subject.push()
        subject.push()

        val (affinityTop, offsetTop) = subject.popAffinity()!!
        val (affinityBottom, offsetBottom) = subject.popAffinity()!!

        assertThat(affinityTop).isEqualTo("B")
        assertThat(offsetTop).isEqualTo(3)
        assertThat(affinityBottom).isEqualTo("A")
        assertThat(offsetBottom).isEqualTo(2)
    }
}