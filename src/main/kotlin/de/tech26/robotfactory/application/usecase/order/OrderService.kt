package de.tech26.robotfactory.application.usecase.order

import de.tech26.functional.Either
import de.tech26.robotfactory.application.domain.*
import de.tech26.robotfactory.application.port.`in`.OrderPort
import de.tech26.robotfactory.application.port.out.ManufactureRepository
import de.tech26.robotfactory.application.port.out.StockRepository
import java.math.BigDecimal

class OrderService(private val manufactureService: ManufactureRepository, private val stockService: StockRepository) : OrderPort {

    override fun create(orderRequest: OrderRequest): Either<DomainError, Order> =
        stockService
            .displayComponents(orderRequest.codes)
            .flatMap { generateEvaluatedOrder(it) }
            .flatMap { generateProcessedOrder(it, orderRequest.codes) }

    private fun generateEvaluatedOrder(components: List<Component>): Either<DomainError, EvaluatedOrder> = Robot
        .fromParts(components.map { it.part })
        .map { EvaluatedOrder(it, calculatePrice(components)) }

    private fun generateProcessedOrder(evaluatedOrder: EvaluatedOrder, codes: List<Code>): Either<DomainError, Order> =
        manufactureService
            .addOrder(evaluatedOrder.robot)
            .map { Order(it, evaluatedOrder.price) }
            .flatMap { order ->
                stockService
                    .subtractComponents(codes)
                    .onLeft { manufactureService.cancelOrder(order.id) }
                    .map { order }
            }

    private fun calculatePrice(components: List<Component>): Double = components.map { BigDecimal(it.price) }.sumOf { it }.toDouble()

    private data class EvaluatedOrder(val robot: Robot, val price: Double)

}