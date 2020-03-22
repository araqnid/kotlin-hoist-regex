plugins {
    kotlin("jvm")
    kotlin("kapt")
}

the<BasePluginConvention>().archivesBaseName = "kotlin-host-regex-kotlin-plugin"

kapt.includeCompileClasspath = false

repositories {
    mavenCentral()
    jcenter()
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

val asmifier by configurations.creating

val testInput by sourceSets.creating
val testInputImplementation = configurations[testInput.implementationConfigurationName]
val testInputJsImplementation by configurations.creating

dependencies {
    testInputImplementation(kotlin("stdlib"))
    testInputJsImplementation(kotlin("stdlib-js"))
    compileOnly(kotlin("compiler-embeddable"))
    compileOnly("com.google.auto.service:auto-service:1.0-rc4")
    kapt("com.google.auto.service:auto-service:1.0-rc4")
    implementation(kotlin("stdlib-jdk8"))
    testImplementation(kotlin("test-junit"))
    testImplementation(kotlin("compiler-embeddable"))
    asmifier("org.ow2.asm:asm-util:7.3.1")
}

tasks {
    val test by getting(Test::class) {
        inputs.dir("src/testInput/kotlin")
        inputs.property("testInputCompileClasspath", testInput.compileClasspath)
        doFirst {
            environment("TEST_INPUT_COMPILE_CLASSPATH", testInput.compileClasspath.joinToString(":"))
            environment("TEST_INPUT_JS_COMPILE_CLASSPATH", testInputJsImplementation.joinToString(":"))
        }
    }

    val asmifyTestInputs by creating(JavaExec::class) {
        group = "development"
        description = "Prints the ASM visitor calls to produce the test example class"
        dependsOn("compileTestInputKotlin")
        inputs.file("build/classes/kotlin/testInput/testInput/Example.class")
        classpath = files(asmifier)
        main = "org.objectweb.asm.util.ASMifier"
        args = listOf(file("build/classes/kotlin/testInput/testInput/Example.class").toString())
    }
}
