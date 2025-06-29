// common/build.gradle.kts
plugins {
  id("java")
}

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(17))
  }
}

repositories {
  mavenCentral()
}

dependencies {
  // @TODO lombok issue 해결
  // Lombok 추가
  compileOnly("org.projectlombok:lombok:1.18.34")
  annotationProcessor("org.projectlombok:lombok:1.18.34")

  // Lombok이 잘 동작하도록 테스트에서도 포함
  testCompileOnly("org.projectlombok:lombok:1.18.34")
  testAnnotationProcessor("org.projectlombok:lombok:1.18.34")
}

tasks.withType<Test> {
  useJUnitPlatform()
}