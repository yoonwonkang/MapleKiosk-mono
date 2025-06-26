plugins {
  id("org.springframework.boot") version "3.5.3" apply false
  id("io.spring.dependency-management") version "1.1.7" apply false
}

subprojects {
  group = "ca.yw.maplekiosk"
  version = "1.0.0"

  repositories {
    mavenCentral()
  }
}