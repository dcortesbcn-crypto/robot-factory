package de.tech26.robotfactory.adapter.out.repository

import de.tech26.functional.Either
import de.tech26.functional.Right
import de.tech26.robotfactory.application.domain.DomainError
import de.tech26.robotfactory.application.domain.ManufacturedOrder
import de.tech26.robotfactory.application.domain.OrderId
import de.tech26.robotfactory.application.domain.Robot
import de.tech26.robotfactory.application.port.out.ManufactureRepository

class ManufactureInMemoryRepository(initialState: List<ManufacturedOrder>, private val codeGenerator: () -> String) : ManufactureRepository {

    private var currentOrders: MutableList<ManufacturedOrder> = initialState.map { it.copy() }.toMutableList()

    override fun fetchOrders(): Either<DomainError, List<ManufacturedOrder>> = Right(currentOrders.map { it.copy() })

    override fun addOrder(robot: Robot): Either<DomainError, OrderId> {
        val orderId = codeGenerator()
        currentOrders.add(ManufacturedOrder(orderId, robot))
        return Right(orderId)
    }

    override fun cancelOrder(orderId: OrderId): Either<DomainError, OrderId> {
        currentOrders.removeIf { it.id == orderId }
        return Right(orderId)
    }


}