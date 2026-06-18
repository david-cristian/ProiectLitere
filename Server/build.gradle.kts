plugins {
    id("java")
    id("application")
    id("org.springframework.boot") version "3.2.9"
    id("io.spring.dependency-management") version "1.1.5"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

application {
    mainClass.set("org.example.StartServer")
}

springBoot {
    mainClass.set("org.example.StartServer")
}


dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation("org.apache.logging.log4j:log4j-api:2.23.1")
    implementation("org.apache.logging.log4j:log4j-core:2.23.1")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.20.0")
    implementation("jakarta.persistence:jakarta.persistence-api:3.1.0")

    implementation("org.springframework.boot:spring-boot-starter-tomcat:3.2.9")
    implementation("org.springframework.boot:spring-boot-starter-web:3.2.9")

    implementation("org.springframework.boot:spring-boot-starter-actuator")

    implementation("org.hibernate.orm:hibernate-core:6.4.4.Final")
    implementation("org.hibernate.orm:hibernate-community-dialects:6.4.4.Final")

    implementation("com.fasterxml.jackson.core:jackson-annotations:2.13.1")
    implementation(project(":Model"))
    implementation(project(":Persistence"))
    implementation(project(":Networking"))
    implementation(project(":Services"))

}

tasks.test {
    useJUnitPlatform()
}