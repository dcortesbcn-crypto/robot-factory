package de.tech26.robotfactory.adapter.`in`.api

import de.tech26.robotfactory.adapter.out.repository.ManufactureInMemoryRepository
import de.tech26.robotfactory.adapter.out.repository.StockInMemoryRepository
import de.tech26.robotfactory.application.domain.*
import de.tech26.robotfactory.application.port.out.ManufactureRepository
import de.tech26.robotfactory.application.port.out.StockRepository
import de.tech26.robotfactory.application.usecase.order.OrderService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import kotlin.random.Random


@Configuration
@Import(StockConfig::class, ManufactureConfig::class)
class OrderConfig {

    @Bean
    fun orderService(manufactureRepository: ManufactureRepository, stockRepository: StockRepository) =
        OrderService(manufactureRepository, stockRepository)

}


@Configuration
class StockConfig {

    private val initialStockState = listOf(
        Component(price = 10.28, available = 9, part = FacePart(code = "A")),
        Component(price = 24.07, available = 7, part = FacePart(code = "B")),
        Component(price = 13.30, available = 0, part = FacePart(code = "C")),
        Component(price = 28.94, available = 1, part = ArmsPart(code = "D")),
        Component(price = 12.39, available = 3, part = ArmsPart(code = "E")),
        Component(price = 30.77, available = 2, part = MobilityPart(code = "F")),
        Component(price = 55.13, available = 15, part = MobilityPart(code = "G")),
        Component(price = 50.00, available = 7, part = MobilityPart(code = "H")),
        Component(price = 90.12, available = 92, part = MaterialPart(code = "I")),
        Component(price = 82.31, available = 15, part = MaterialPart(code = "J")),
    )

    @Bean
    fun stockRepository() = StockInMemoryRepository(initialStockState)
}

@Configuration
class ManufactureConfig {

    fun randomStringGenerator(): String = (1..10)
        .map { Random.nextInt(0, 9)  }
        .joinToString("")

    @Bean
    fun manufactureRepository() = ManufactureInMemoryRepository(emptyList()) { randomStringGenerator() }
}