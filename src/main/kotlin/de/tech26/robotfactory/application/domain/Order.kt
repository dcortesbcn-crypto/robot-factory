package de.tech26.robotfactory.application.domain

data class OrderRequest(val codes: List<Code>)

typealias OrderId = String
data class ManufacturedOrder(val id: OrderId, val robot: Robot)
data class Order(val id: OrderId, val price: Double)

