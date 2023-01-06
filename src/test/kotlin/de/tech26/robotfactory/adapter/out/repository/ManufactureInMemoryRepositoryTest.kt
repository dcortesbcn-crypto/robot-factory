package de.tech26.robotfactory.adapter.out.repository

import de.tech26.functional.Either
import de.tech26.functional.Right
import de.tech26.robotfactory.application.domain.DomainError
import de.tech26.robotfactory.application.domain.ManufacturedOrder
import de.tech26.robotfactory.application.domain.OrderId
import de.tech26.robotfactory.utils.robot
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.rmi.UnexpectedException

internal class ManufactureInMemoryRepositoryTest{

    @Nested
    inner class FetchOrdersTest {

        @Test
        fun `given not manufactured orders when we fetch orders should return empty list`(){
            val expectedResult: Either<DomainError, List<ManufacturedOrder>> = Right(emptyList())
            val repository = ManufactureInMemoryRepository(emptyList()) {  "new-order" }

            Assertions.assertThat(repository.fetchOrders()).isEqualTo(expectedResult)
        }

        @Test
        fun `given manufactured orders when we fetch orders should return all orders`(){
            val manufacturedOrders = listOf(
                ManufacturedOrder("first-order", robot),
                ManufacturedOrder("second-order", robot),
            )
            val repository = ManufactureInMemoryRepository(manufacturedOrders) {  "new-order" }

            val result = repository.fetchOrders()

            Assertions.assertThat(result.isRight()).isTrue
            Assertions.assertThat(result.getOrThrow { UnexpectedException("") }.size).isEqualTo(2)
            Assertions.assertThat(result.getOrThrow { UnexpectedException("") }).containsAll(manufacturedOrders)
        }

    }

    @Nested
    inner class AddOrderTest {

        @Test
        fun `given a new order is generated an order id should be retrieved`(){
            val expectedResult: Either<DomainError, OrderId> = Right("new-order")
            val expectedOrders = listOf(ManufacturedOrder("new-order", robot))

            val repository = ManufactureInMemoryRepository(emptyList()) {  "new-order" }

            Assertions.assertThat(repository.addOrder(robot)).isEqualTo(expectedResult)
            val currentOrders = repository.fetchOrders()
            Assertions.assertThat(currentOrders.getOrThrow { UnexpectedException("") }.size).isEqualTo(1)
            Assertions.assertThat(currentOrders.getOrThrow { UnexpectedException("") }).containsAll(expectedOrders)

        }
    }

    @Nested
    inner class CancelOrderTest {

        @Test
        fun `given a non existent order is canceled an order id should be retrieved`(){
            val expectedResult: Either<DomainError, OrderId> = Right("new-order")

            val repository = ManufactureInMemoryRepository(emptyList()) {  "some-order" }

            Assertions.assertThat(repository.cancelOrder("new-order")).isEqualTo(expectedResult)
            val currentOrders = repository.fetchOrders()
            Assertions.assertThat(currentOrders.getOrThrow { UnexpectedException("") }.size).isEqualTo(0)
        }

        @Test
        fun `given an existent order is canceled an order id should be retrieved`(){
            val expectedResult: Either<DomainError, OrderId> = Right("some-order")
            val initialState = listOf(ManufacturedOrder("some-order", robot), ManufacturedOrder("second-order", robot))
            val endState = listOf(ManufacturedOrder("second-order", robot))

            val repository = ManufactureInMemoryRepository(initialState) {  "some-order" }

            Assertions.assertThat(repository.cancelOrder("some-order")).isEqualTo(expectedResult)
            val currentOrders = repository.fetchOrders()
            Assertions.assertThat(currentOrders.getOrThrow { UnexpectedException("") }.size).isEqualTo(1)
            Assertions.assertThat(currentOrders.getOrThrow { UnexpectedException("") }).containsAll(endState)
        }
    }
}