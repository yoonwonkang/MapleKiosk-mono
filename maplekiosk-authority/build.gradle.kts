plugins {
	java
	id("org.springframework.boot") version "3.5.3"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "ca.yw"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
}

dependencies {
  implementation(project(":common"))
  implementation(project(":maplekiosk-logging"))

  implementation("org.springframework.boot:spring-boot-starter-web") {
    exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
    exclude(group = "org.apache.logging.log4j", module = "log4j-to-slf4j")
  }
  implementation("org.springframework.boot:spring-boot-starter-security")

  implementation("io.jsonwebtoken:jjwt-api:0.11.5")
  runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
  runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")

  implementation("org.springframework.boot:spring-boot-starter-validation")
  // implementation("org.springframework.boot:spring-boot-starter-data-jpa")

  //swagger
  implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0")

  // Lombok
  compileOnly("org.projectlombok:lombok:1.18.32")
  annotationProcessor("org.projectlombok:lombok:1.18.32")

  runtimeOnly("org.mariadb.jdbc:mariadb-java-client:3.3.2")
  testImplementation("org.springframework.boot:spring-boot-starter-test") {
    exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
    exclude(group = "org.apache.logging.log4j", module = "log4j-to-slf4j")
    exclude(group = "ch.qos.logback")
  }
}

configurations.all {
  exclude(group = "org.apache.logging.log4j", module = "log4j-to-slf4j")
  exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
  exclude(group = "ch.qos.logback")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

