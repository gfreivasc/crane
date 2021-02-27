package com.gabrielfv.crane.core.affinity

import android.os.Bundle
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
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

  @Test
  fun onSave() {
    val bundle: Bundle = mockk(relaxed = true)
    val subject = AffinityManager()
    subject.push("A")
    subject.push()

    subject.save(bundle)

    verify {
      bundle.putSerializable(
        eq(AffinityManager.AFFINITY_REGISTRY),
        eq(HashMap(mapOf("A" to 2)))
      )
    }
  }

  @Test
  fun onRestore() {
    val bundle: Bundle = mockk {
      every { getSerializable(eq(AffinityManager.AFFINITY_REGISTRY)) }
        .returns(HashMap(mapOf("A" to 2)))
    }
    val subject = AffinityManager()

    subject.restore(bundle)

    val (tag, offset) = subject.popAffinity()!!
    assertThat(tag).isEqualTo("A")
    assertThat(offset).isEqualTo(2)
  }

  @Test
  fun onRestore_noData() {
    val bundle: Bundle = mockk(relaxed = true)
    val subject = AffinityManager()

    subject.restore(bundle)

    val result = subject.popAffinity()
    assertThat(result).isNull()
  }
}
