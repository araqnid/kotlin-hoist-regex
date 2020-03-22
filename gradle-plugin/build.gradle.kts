plugins {
    `java-gradle-plugin`
    kotlin("jvm")
    kotlin("kapt")
}

the<BasePluginConvention>().archivesBaseName = "kotlin-host-regex-gradle-plugin"

kapt.includeCompileClasspath = false

repositories {
    mavenCentral()
    jcenter()
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    compileOnly("com.google.auto.service:auto-service:1.0-rc4")
    kapt("com.google.auto.service:auto-service:1.0-rc4")
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("gradle-plugin-api"))
    testImplementation(kotlin("test-junit"))
}

gradlePlugin {
    plugins {
        create("simplePlugin") {
            id = "org.araqnid.kotlin.hoist-regex"
            implementationClass = "org.araqnid.kotlin.hoistregex.gradle.HoistRegexPlugin"
        }
    }
}
