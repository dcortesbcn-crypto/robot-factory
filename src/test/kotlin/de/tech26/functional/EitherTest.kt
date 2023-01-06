package de.tech26.functional


import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.rmi.UnexpectedException

internal class EitherTest {

    @Test
    fun `given a right when called isRight should return true`() {
        val data: Either<Exception, Int> = Right(12)

        assertThat(data.isRight()).isTrue
    }

    @Test
    fun `given a left when called isRight should return false`() {
        val data: Either<String, Int> = Left("12")

        assertThat(data.isRight()).isFalse
    }

    @Test
    fun `given a right when called isLeft should return false`() {
        val data: Either<Exception, Int> = Right(12)

        assertThat(data.isLeft()).isFalse
    }

    @Test
    fun `given a left when called isLeft should return true`() {
        val data: Either<String, Int> = Left("12")

        assertThat(data.isLeft()).isTrue
    }

    @Test
    fun `given a right when called map should apply function`() {
        val data: Either<Exception, Int> = Right(12)
        val expectedResult: Either<Exception, Int> = Right(14)

        val result = data.map { it + 2 }

        assertThat(result).isEqualTo(expectedResult)
    }

    @Test
    fun `given a left when called map should ignore function`() {
        val data: Either<String, Int> = Left("some error")
        val onRightFunction = mockk<MockFunctionsClass<Int>>()

        val result = data.map { onRightFunction.identity(it) }

        assertThat(result).isEqualTo(data)
        verify(exactly = 0) { onRightFunction.identity(any()) }
    }

    @Test
    fun `given a right when called flat map should switch container if required`() {
        val data: Either<String, Int> = Right(12)
        val expectedResult: Either<String, Int> = Left("error")

        val result = data.flatMap { expectedResult }

        assertThat(result).isEqualTo(expectedResult)
    }

    @Test
    fun `given a left when called flatmap should not apply function`() {
        val data: Either<String, Int> = Left("some error")
        val onRightFunction = mockk<MockFunctionsClass<Int>>()

        val result = data.flatMap { Right(onRightFunction.identity(it)) }

        assertThat(result).isEqualTo(data)
        verify(exactly = 0) { onRightFunction.identity(any()) }
    }

    @Test
    fun `given a right when called fold should return expected calculated value`() {
        val data: Either<String, Int> = Right(12)
        val onLeftFunction = mockk<MockFunctionsClass<String>>()

        val result = data.fold(leftFunction = { onLeftFunction.identity(it) }, rightFunction = { it * 2 })

        assertThat(result).isEqualTo(24)
        verify(exactly = 0) { onLeftFunction.identity(any()) }
    }

    @Test
    fun `given a left when called fold should return expected calculated error`() {
        val data: Either<String, Int> = Left("some error")
        val onRightFunction = mockk<MockFunctionsClass<Int>>()

        val result = data.fold(leftFunction = { "$it appended" }, rightFunction = { onRightFunction.identity(it) } )

        assertThat(result).isEqualTo("some error appended")
        verify(exactly = 0) { onRightFunction.identity(any()) }
    }

    @Test
    fun `given a right when called getOrThrow should return expected value`() {
        val data: Either<String, Int> = Right(12)

        assertThat(data.getOrThrow { UnexpectedException("") }).isEqualTo(12)
    }

    @Test
    fun `given a left when called getOrThrow should raise an exception`() {
        val data: Either<String, Int> = Left("some error")

        assertThatThrownBy { data.getOrThrow { UnexpectedException("") } }
    }

    @Test
    fun `given a right when called getLeftOrThrow should raise Error`() {
        val data: Either<String, Int> = Right(12)

        assertThatThrownBy { data.getLeftOrThrow { UnexpectedException("") } }
    }

    @Test
    fun `given a left when called getLeftOrThrow should return value`() {
        val data: Either<String, Int> = Left("some error")

        assertThat(data.getLeftOrThrow { UnexpectedException("") }).isEqualTo("some error")
    }

    @Test
    fun `given a right when called onLeft should do nothing`() {
        val data: Either<String, Int> = Right(12)
        val onLeftClass = mockk<MockFunctionsClass<String>>()

        assertThat(data.onLeft { it + "12" }).isEqualTo(data)
        verify(exactly = 0) { onLeftClass.doNothing(any()) }
    }

    @Test
    fun `given a left when called onLeft should apply function and return same value`() {
        val data: Either<String, Int> = Left("some error")
        val onLeftClass = mockk<MockFunctionsClass<String>>()

        every { onLeftClass.doNothing("some error") } returns Unit

        assertThat(data.onLeft { onLeftClass.doNothing(it) }).isEqualTo(data)
        verify(exactly = 1) { onLeftClass.doNothing("some error") }
    }

    private class MockFunctionsClass<T> {
        fun doNothing(value: T): Unit = Unit
        fun identity(value: T) :T = value
    }
}