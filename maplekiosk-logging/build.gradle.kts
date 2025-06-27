plugins {
    id("java")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    // Log4j2 의존성만 관리
    implementation("org.springframework.boot:spring-boot-starter-log4j2")
}

tasks.withType<Test> {
    useJUnitPlatform()
}