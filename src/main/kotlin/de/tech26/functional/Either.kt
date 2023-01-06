package de.tech26.functional


sealed class Either<out L, out R> {

    abstract fun isRight(): Boolean

    abstract fun isLeft(): Boolean

    abstract fun <NR> map(f: (R) -> NR): Either<L, NR>

    abstract fun <NR> flatMap(f: (R) -> Either<@UnsafeVariance L, NR>): Either<L, NR>

    abstract fun <C> fold(leftFunction: (L) -> C, rightFunction: (R) -> C): C

    abstract fun getOrThrow(exceptionFunction: () -> Exception): R

    abstract fun getLeftOrThrow(exceptionFunction: () -> Exception): L

    abstract fun onLeft(action: (L) -> Unit): Either<L, R>

}

data class Left<out L, out R>(private val value: L) : Either<L, R>() {

    override fun isRight(): Boolean = false

    override fun isLeft(): Boolean = true

    override fun <NR> map(f: (R) -> NR) = Left<L, NR>(value)

    override fun <NR> flatMap(f: (R) -> Either<@UnsafeVariance L, NR>) = Left<L, NR>(value)

    override fun <C> fold(leftFunction: (L) -> C, rightFunction: (R) -> C) = leftFunction(value)

    override fun getOrThrow(exceptionFunction: () -> Exception): R = throw exceptionFunction()

    override fun getLeftOrThrow(exceptionFunction: () -> Exception): L = value

    override fun onLeft(action: (L) -> Unit): Either<L, R> {
        action(value)
        return this
    }

}

data class Right<out L, out R>(private val value: R) : Either<L, R>() {

    override fun isRight(): Boolean = true

    override fun isLeft(): Boolean = false

    override fun <NR> map(f: (R) -> NR) = Right<L, NR>(f(value))

    override fun <NR> flatMap(f: (R) -> Either<@UnsafeVariance L, NR>) = f(value)

    override fun <C> fold(leftFunction: (L) -> C, rightFunction: (R) -> C) = rightFunction(value)

    override fun getOrThrow(exceptionFunction: () -> Exception): R = value

    override fun getLeftOrThrow(exceptionFunction: () -> Exception): L = throw exceptionFunction()

    override fun onLeft(action: (L) -> Unit): Either<L, R> = this

}

