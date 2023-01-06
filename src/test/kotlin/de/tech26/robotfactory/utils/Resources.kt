package de.tech26.robotfactory.utils

import de.tech26.robotfactory.application.domain.*

val partFace = FacePart(code = "A")
val partArms = ArmsPart(code = "B")
val partMaterial = MaterialPart(code = "C")
val partMobility = MobilityPart(code = "D")

val componentWithFace = Component(price = 12.4, available = 5, partFace)
val componentWithArms = Component(price = 24.1, available = 5, partArms)
val componentWithMaterial = Component(price = 5.1, available = 5, partMaterial)
val componentWithMobility = Component(price = 100.25, available = 5, partMobility)

val robot = Robot(partFace, partMaterial, partArms, partMobility)
