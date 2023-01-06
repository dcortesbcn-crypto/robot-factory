package de.tech26.robotfactory.adapter.out.repository

import de.tech26.functional.Either
import de.tech26.functional.Left
import de.tech26.functional.Right
import de.tech26.robotfactory.application.domain.*
import de.tech26.robotfactory.utils.componentWithArms
import de.tech26.robotfactory.utils.componentWithFace
import de.tech26.robotfactory.utils.componentWithMobility
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.rmi.UnexpectedException

internal class StockInMemoryRepositoryTest {

    private val stockComponents = listOf(
        componentWithArms,
        componentWithFace,
        componentWithMobility
    )

    @Nested
    inner class DisplayComponentsTest {

        private val repository = StockInMemoryRepository(stockComponents.map { it.copy() })

        @Test
        fun `given non code passed should return empty list`() {
            val expectedResult: Either<InvalidStockState, List<Component>> = Right(emptyList())

            assertThat(repository.displayComponents(emptyList())).isEqualTo(expectedResult)

        }

        @Test
        fun `given all codes can be found on stock should return list of components`() {
            val expectedResult = listOf(componentWithFace, componentWithArms)

            val result = repository.displayComponents(listOf("A", "B"))

            assertThat(result.isRight()).isTrue
            assertThat(result.getOrThrow { UnexpectedException("") }).containsAll(expectedResult)

        }

        @Test
        fun `given all codes can be found on stock and some are repeated should return list of components without duplicates`() {
            val expectedResult = listOf(componentWithFace, componentWithArms)

            val result = repository.displayComponents(listOf("A", "A", "B"))

            assertThat(result.isRight()).isTrue
            assertThat(result.getOrThrow { UnexpectedException("") }).containsAll(expectedResult)

        }

        @Test
        fun `given some codes can't be found on stock should return stock error with missing components`() {
            val expectedResult: List<PartNotFound> = listOf(PartNotFound("N"), PartNotFound("Z"))

            val result = repository.displayComponents(listOf("A", "B", "N", "Z"))

            assertThat(result.isLeft()).isTrue
            assertThat(result.getLeftOrThrow { UnexpectedException("") }.errors).containsAll(expectedResult)

        }
    }

    @Nested
    inner class SubtractComponentsTest {

        private val allStockCodes = stockComponents.map { it.part.code }

        @Test
        fun `given non code passed should return empty list without any modification of stock`() {
            val expectedResult: Either<InvalidStockState, List<Code>> = Right(emptyList())
            val repository = StockInMemoryRepository(stockComponents.map { it.copy() })

            val result = repository.subtractComponents(emptyList())

            assertThat(result).isEqualTo(expectedResult)
            assertThat(repository.displayComponents(allStockCodes).getOrThrow { UnexpectedException("") })
                .isEqualTo(stockComponents)

        }

        @Test
        fun `given some codes not found should return stock error with not available components`() {
            // TODO add
            val expectedResult: Either<InvalidStockState, List<Code>> = Left(InvalidStockState(errors = listOf(PartNotFound("Z"))))
            val repository = StockInMemoryRepository(stockComponents.map { it.copy() })

            val result = repository.subtractComponents(listOf("Z", "A"))

            assertThat(result).isEqualTo(expectedResult)
            assertThat(repository.displayComponents(allStockCodes).getOrThrow { UnexpectedException("") })
                .isEqualTo(stockComponents)

        }

        @Test
        fun `given some codes have not enough availability should return stock error with not available components`() {
            val expectedResult: Either<InvalidStockState, List<Code>> = Left(InvalidStockState(errors = listOf(PartNotAvailable("A"))))
            val repository = StockInMemoryRepository(stockComponents.map { it.copy() })

            val result = repository.subtractComponents(listOf("A", "A", "A", "A", "A", "A"))

            assertThat(result).isEqualTo(expectedResult)
            assertThat(repository.displayComponents(allStockCodes).getOrThrow { UnexpectedException("") })
                .isEqualTo(stockComponents)

        }

        @Test
        fun `given all codes have enough availability on stock should return list of codes and modify the stock`() {
            val codes = listOf("A", "D", "D", "D", "D", "D")
            val expectedResult: Either<InvalidStockState, List<Code>> = Right(codes)
            val repository = StockInMemoryRepository(stockComponents.map { it.copy() })
            val newStockState = listOf(
                componentWithArms,
                componentWithFace.copy(available = 4),
                componentWithMobility.copy(available = 0)
            )

            val result = repository.subtractComponents(codes)

            assertThat(result).isEqualTo(expectedResult)
            assertThat(repository.displayComponents(allStockCodes).getOrThrow { UnexpectedException("") })
                .isEqualTo(newStockState)
        }
    }
}