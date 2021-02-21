package com.gabrielfv.crane.core.result

import android.os.Bundle
import com.gabrielfv.crane.Result
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class ResultRegistryTest {
    private val saveBundle = mockk<Bundle>(relaxed = true)

    @Test
    fun onFetch_withoutResult() {
        val subject = ResultRegistry()

        val result = subject.fetch(Result::class)

        assertThat(result).isNull()
    }

    @Test
    fun onFetch_existingResult() {
        val subject = ResultRegistry()
        subject.push(Result(2))

        val result = subject.fetch(Result::class)

        assertThat(result).isNotNull
        assertThat(result!!.i).isEqualTo(2)
    }

    @Test
    fun onFetch_multipleResults() {
        val subject = ResultRegistry()
        subject.push(Result(3))
        subject.push(Result(4))

        val result = subject.fetch(Result::class)

        assertThat(result).isNotNull
        assertThat(result!!.i).isEqualTo(4)
    }

    @Test
    fun onFetch_multipleTimes() {
        val subject = ResultRegistry()
        subject.push(Result(5))
        subject.fetch(Result::class)

        val result = subject.fetch(Result::class)

        // This is a requirement that will probably change eventually
        assertThat(result).isNull()
    }

    @Test
    fun onSave_withoutResults() {
        val subject = ResultRegistry()

        subject.save(saveBundle)

        verify(exactly = 0) {
            saveBundle.putParcelableArray(eq(ResultRegistry.RESULT_REGISTRY), any())
        }
    }

    @Test
    fun onSave_withResults() {
        val subject = ResultRegistry()
        subject.push(Result(6))

        subject.save(saveBundle)

        verify {
            saveBundle.putParcelableArray(
                eq(ResultRegistry.RESULT_REGISTRY),
                eq(arrayOf(Result(6)))
            )
        }
    }

    @Test
    fun onRestore_emptyState() {
        val subject = ResultRegistry()

        subject.restore(saveBundle)
        val result = subject.fetch(Result::class)

        assertThat(result).isNull()
    }

    @Test
    fun onRestore_savedState() {
        val subject = ResultRegistry()
        every {
            saveBundle.getParcelableArray(eq(ResultRegistry.RESULT_REGISTRY))
        } returns arrayOf(
            Result(7)
        )

        subject.restore(saveBundle)
        val result = subject.fetch(Result::class)

        assertThat(result).isNotNull
        assertThat(result!!.i).isEqualTo(7)
    }
}
