plugins {
    kotlin("jvm")
    kotlin("kapt")
}

repositories {
    mavenCentral()
    jcenter()
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

sourceSets {
    create("testInput")
}

dependencies {
    "testInputImplementation"(kotlin("stdlib"))
    compileOnly(kotlin("compiler-embeddable"))
    compileOnly("com.google.auto.service:auto-service:1.0-rc4")
    kapt("com.google.auto.service:auto-service:1.0-rc4")
    implementation(kotlin("stdlib-jdk8"))
    testImplementation(kotlin("test-junit"))
    testImplementation(kotlin("compiler-embeddable"))
}

tasks {
    val test by getting(Test::class) {
        inputs.dir("src/testInput/kotlin")
        val testInputCompileClasspath by configurations.getting
        inputs.property("testInputCompileClasspath", testInputCompileClasspath)
        doFirst {
            environment("TEST_INPUT_COMPILE_CLASSPATH", testInputCompileClasspath.joinToString(":"))
        }
    }
}