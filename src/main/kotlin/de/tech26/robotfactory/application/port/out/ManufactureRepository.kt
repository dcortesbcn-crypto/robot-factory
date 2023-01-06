package de.tech26.robotfactory.application.port.out

import de.tech26.functional.Either
import de.tech26.robotfactory.application.domain.DomainError
import de.tech26.robotfactory.application.domain.ManufacturedOrder
import de.tech26.robotfactory.application.domain.OrderId
import de.tech26.robotfactory.application.domain.Robot

interface ManufactureRepository {
    fun addOrder(robot:Robot): Either<DomainError, OrderId>
    fun cancelOrder(orderId: OrderId): Either<DomainError, OrderId>
    fun fetchOrders(): Either<DomainError, List<ManufacturedOrder>>
}