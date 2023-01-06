package de.tech26.robotfactory.adapter.`in`.api.controller

import de.tech26.robotfactory.application.domain.*
import de.tech26.robotfactory.application.port.`in`.OrderPort
import org.springframework.http.HttpStatus.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/orders")
class OrderController(val orderService: OrderPort) {

    @PostMapping
    fun orderRobotManufacturing(@RequestBody payload: OrderPayload): ResponseEntity<out Any> =
        orderService.create(payload.asNewOrder()).fold(
            leftFunction = { asBadResponse(it) },
            rightFunction = { asGoodResponse(it) }
        )

    fun asBadResponse(error: DomainError): ResponseEntity<String> = when (error) {
        is InvalidRobotState -> ResponseEntity.status(UNPROCESSABLE_ENTITY).build()
        is InvalidStockState -> ResponseEntity.status(UNPROCESSABLE_ENTITY).build()
        is UnableToProceedOrder -> ResponseEntity.status(INTERNAL_SERVER_ERROR).build()
    }

    fun asGoodResponse(order: Order): ResponseEntity<OrderResponse> = ResponseEntity.status(CREATED).body(OrderResponse.from(order))


    data class OrderPayload(val components: List<String>) {
        fun asNewOrder() = OrderRequest(components)
    }

    data class OrderResponse(val orderId: String, val total: Double) {
        companion object {
            fun from(order: Order): OrderResponse {
                return OrderResponse(order.id, order.price)
            }
        }
    }
}







