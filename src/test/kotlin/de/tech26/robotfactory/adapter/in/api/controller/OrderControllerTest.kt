package de.tech26.robotfactory.adapter.`in`.api.controller

import com.ninjasquad.springmockk.MockkBean
import de.tech26.functional.Left
import de.tech26.functional.Right
import de.tech26.robotfactory.application.domain.*
import de.tech26.robotfactory.application.port.`in`.OrderPort
import io.mockk.every
import io.mockk.verify
import io.restassured.RestAssured
import io.restassured.http.ContentType
import net.javacrumbs.jsonunit.assertj.assertThatJson
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.system.OutputCaptureExtension
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus.*

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ExtendWith(OutputCaptureExtension::class)
internal class OrderControllerTest{

    @MockkBean
    private lateinit var orderService: OrderPort

    @LocalServerPort
    val springBootPort: Int = 0

    @Test
    fun `given a malformed new order should return error`(){

        postOrder("""
                    {
                        "components": 12
                    }
                """)
            .then()
            .assertThat()
            .statusCode(BAD_REQUEST.value())
            .extract()
            .asString()

        verify(exactly = 0) { orderService.create(any()) }

    }

    @Test
    fun `given a new order that raise an invalid robot to proceed order should return an unprocessable entity`(){
        val orderRequest = OrderRequest(listOf("A", "C", "I", "D"))
        every { orderService.create(orderRequest) } returns Left(InvalidRobotState(emptyList()))

        postOrder("""
                    {
                        "components": ["A", "C", "I", "D"]
                    }
                """)
            .then()
            .assertThat()
            .statusCode(UNPROCESSABLE_ENTITY.value())

        verify(exactly = 1) { orderService.create(any()) }

    }

    @Test
    fun `given a new order that raise an invalid stock to proceed order should return an unprocessable entity`(){
        val orderRequest = OrderRequest(listOf("A", "C", "I", "D"))
        every { orderService.create(orderRequest) } returns Left(InvalidStockState(emptyList()))

        postOrder("""
                    {
                        "components": ["A", "C", "I", "D"]
                    }
                """)
            .then()
            .assertThat()
            .statusCode(UNPROCESSABLE_ENTITY.value())

        verify(exactly = 1) { orderService.create(any()) }

    }

    @Test
    fun `given a new order that raise an unexpected unable to proceed order should return an internal error`(){
        val orderRequest = OrderRequest(listOf("A", "C", "I", "D"))
        every { orderService.create(orderRequest) } returns Left(UnableToProceedOrder("bad order"))

        postOrder("""
                    {
                        "components": ["A", "C", "I", "D"]
                    }
                """)
            .then()
            .assertThat()
            .statusCode(INTERNAL_SERVER_ERROR.value())

        verify(exactly = 1) { orderService.create(any()) }

    }

    @Test
    fun `given a correct new order should be proceeded and returned with price`(){
        val orderRequest = OrderRequest(listOf("A", "C", "I", "D"))
        every { orderService.create(orderRequest) } returns Right(Order("12D", 14.88))

        val result = postOrder("""
                    {
                        "components": ["A", "C", "I", "D"]
                    }
                """)
            .then()
            .assertThat()
            .statusCode(CREATED.value())
            .extract()
            .asString()

        assertThatJson(result).isEqualTo(""" { "order_id": "12D", "total": 14.88 } """)
        verify(exactly = 1) { orderService.create(any()) }

    }

    private fun postOrder(body: String) = RestAssured
        .given()
        .body(body)
        .contentType(ContentType.JSON)
        .`when`()
        .port(springBootPort)
        .post("/orders")
}