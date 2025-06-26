plugins {
    id("java")
}

dependencies {
  implementation("com.fasterxml.jackson.core:jackson-databind:2.17.0")
}

java {
  sourceCompatibility = JavaVersion.VERSION_17
  targetCompatibility = JavaVersion.VERSION_17
}