import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.7.7"
    id("io.spring.dependency-management") version "1.1.0"
    kotlin("jvm") version "1.8.0"
    kotlin("plugin.spring") version "1.8.0"
}

group = "de.tech26.robotfactory"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.14.1")

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation("org.assertj:assertj-core:3.23.1")
    testImplementation("io.mockk:mockk:1.13.3")
    testImplementation("com.ninja-squad:springmockk:3.0.1")
    testImplementation("io.rest-assured:rest-assured:4.5.1") {
        exclude(group = "com.sun.xml.bind", module = "jaxb-osgi")
    }
    testImplementation("net.javacrumbs.json-unit:json-unit-assertj:2.36.0")
}

tasks.apply {
    test {
        enableAssertions = true
        useJUnitPlatform {}
    }
}

// TODO check to increase jvm version to 17
tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

springBoot{
    mainClass.set("de.tech26.robotfactory.adapter.in.api.RobotFactoryApplication")
}
