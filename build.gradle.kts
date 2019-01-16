val junitJupiterVersion = "5.3.2"
val spekVersion = "1.2.1"
val kluentVersion = "1.45"
val ktorVersion = "1.1.1"
val prometheusVersion = "0.6.0"
val orgJsonVersion = "20180813"
val slf4jVersion = "1.7.25"

group = "no.nav.helse"
version = 8

plugins {
   kotlin("jvm") version "1.3.11"
   id("com.github.johnrengelman.shadow") version "4.0.3"
}

buildscript {
   dependencies {
      classpath("org.junit.platform:junit-platform-gradle-plugin:1.2.0")
   }
}

dependencies {
   compile(kotlin("stdlib"))
   compile("io.ktor:ktor-server-netty:$ktorVersion")
   compile("io.prometheus:simpleclient_common:$prometheusVersion")
   compile("org.json:json:$orgJsonVersion")
   compile("org.slf4j:slf4j-simple:$slf4jVersion")

   testCompile("org.junit.jupiter:junit-jupiter-api:$junitJupiterVersion")
   testCompile("org.junit.jupiter:junit-jupiter-params:$junitJupiterVersion")
   testRuntime("org.junit.jupiter:junit-jupiter-engine:$junitJupiterVersion")
   testCompile("org.amshove.kluent:kluent:$kluentVersion")
   testCompile("org.jetbrains.spek:spek-api:$spekVersion") {
      exclude(group = "org.jetbrains.kotlin")
   }
   testRuntime("org.jetbrains.spek:spek-junit-platform-engine:$spekVersion") {
      exclude(group = "org.junit.platform")
      exclude(group = "org.jetbrains.kotlin")
   }
}

repositories {
   jcenter()
}

java {
   sourceCompatibility = JavaVersion.VERSION_11
   targetCompatibility = JavaVersion.VERSION_11
}

tasks.withType<Test> {
   useJUnitPlatform()
   testLogging {
      events("passed", "skipped", "failed")
   }
}

tasks.withType<Wrapper> {
   gradleVersion = "5.1.1"
}

