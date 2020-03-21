plugins {
    kotlin("jvm") version "1.3.70" apply false
}

subprojects {
    tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class.java).all {
        kotlinOptions {
            freeCompilerArgs += listOf("-module-name", rootProject.name + project.path.replace(':', '.'))
        }
    }
}
