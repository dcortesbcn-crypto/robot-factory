package de.tech26.robotfactory.application.usecase.order

import de.tech26.functional.Either
import de.tech26.functional.Left
import de.tech26.functional.Right
import de.tech26.robotfactory.application.domain.*
import de.tech26.robotfactory.application.domain.PartCategory.*
import de.tech26.robotfactory.application.port.out.ManufactureRepository
import de.tech26.robotfactory.application.port.out.StockRepository
import de.tech26.robotfactory.utils.*
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.rmi.UnexpectedException

internal class OrderServiceTest {

    private val manufactureService: ManufactureRepository = mockk()
    private val stockService: StockRepository = mockk()
    private val service = OrderService(manufactureService, stockService)

    private val correctCodes = listOf("A", "B", "C", "D")
    private val correctComponents = listOf(
        componentWithFace,
        componentWithArms,
        componentWithMaterial,
        componentWithMobility
    )

    @Test
    fun `given an order request when problem displaying components from stock should return an stock error`() {
        val orderRequest = OrderRequest(codes = correctCodes)
        val expectedResult: Either<DomainError, List<Component>> =
            Left(InvalidStockState(listOf(
                PartNotFound("A"),
                PartNotFound("B"),
            )))

        every { stockService.displayComponents(correctCodes) } returns expectedResult
        val result = service.create(orderRequest)

        Assertions.assertThat(result).isEqualTo(expectedResult)
        verify(exactly = 1) { stockService.displayComponents(correctCodes) }
        verify(exactly = 0) { manufactureService.addOrder(any()) }
        verify(exactly = 0) { stockService.subtractComponents(any()) }
        verify(exactly = 0) { manufactureService.cancelOrder(any()) }
    }

    @Test
    fun `given an order request when bad parts configuration for robot should return a robot error`() {
        val codes = listOf("A", "A", "C")
        val orderRequest = OrderRequest(codes = codes)
        val expectedResult: Either<DomainError, Order> =
            Left(InvalidRobotState(listOf(
                TooManyOptionsForPart(FACE),
                MissingMandatoryPart(MATERIAL),
                MissingMandatoryPart(MOBILITY))
            ))

        every { stockService.displayComponents(codes) } returns Right(listOf(
            componentWithFace,
            componentWithFace,
            componentWithArms
        ))
        val result = service.create(orderRequest)

        Assertions.assertThat(result).isEqualTo(expectedResult)
        verify(exactly = 1) { stockService.displayComponents(codes) }
        verify(exactly = 0) { manufactureService.addOrder(any()) }
        verify(exactly = 0) { stockService.subtractComponents(any()) }
        verify(exactly = 0) { manufactureService.cancelOrder(any()) }
    }

    @Test
    fun `given an order request when not possible to add the order should return the manufacture error`() {
        val orderRequest = OrderRequest(codes = correctCodes)
        val error = UnableToProceedOrder("some unexpected error")

        every { stockService.displayComponents(correctCodes) } returns Right(correctComponents)
        every { manufactureService.addOrder(robot) } returns Left(error)
        val result = service.create(orderRequest)

        Assertions.assertThat(result.getLeftOrThrow { UnexpectedException("") }).isEqualTo(error)
        verify(exactly = 1) { stockService.displayComponents(correctCodes) }
        verify(exactly = 1) { manufactureService.addOrder(robot) }
        verify(exactly = 0) { stockService.subtractComponents(any()) }
        verify(exactly = 0) { manufactureService.cancelOrder(any()) }
    }

    @Test
    fun `given an order request when valid robot and all components in stock should return the confirmed order`() {
        val orderRequest = OrderRequest(codes = correctCodes)
        val order = Order(id = "new-order", price = 141.85)
        val expectedResult: Either<DomainError, Order> = Right(order)

        every { stockService.displayComponents(correctCodes) } returns Right(correctComponents)
        every { manufactureService.addOrder(robot) } returns Right(order.id)
        every { stockService.subtractComponents(correctCodes) } returns Right(correctCodes)

        val result = service.create(orderRequest)

        Assertions.assertThat(result).isEqualTo(expectedResult)
        verify(exactly = 1) { stockService.displayComponents(correctCodes) }
        verify(exactly = 1) { manufactureService.addOrder(robot) }
        verify(exactly = 1) { stockService.subtractComponents(correctCodes) }
        verify(exactly = 0) { manufactureService.cancelOrder(any()) }
    }

    @Test
    fun `given an order request when missing components in stock should return the stock error and cancel the order`() {
        val orderRequest = OrderRequest(codes = correctCodes)
        val order = Order(id = "new-order", price = 141.85)
        val error = InvalidStockState(listOf(PartNotAvailable("A")))
        val expectedResult: Either<DomainError, Order> = Left(error)

        every { stockService.displayComponents(correctCodes) } returns Right(correctComponents)
        every { manufactureService.addOrder(robot) } returns Right(order.id)
        every { stockService.subtractComponents(correctCodes) } returns Left(error)
        every { manufactureService.cancelOrder(order.id) } returns Right(order.id)

        val result = service.create(orderRequest)

        Assertions.assertThat(result).isEqualTo(expectedResult)
        verify(exactly = 1) { stockService.displayComponents(correctCodes) }
        verify(exactly = 1) { manufactureService.addOrder(robot) }
        verify(exactly = 1) { stockService.subtractComponents(correctCodes) }
        verify(exactly = 1) { manufactureService.cancelOrder(order.id) }
    }

}