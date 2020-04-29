plugins {
    `kotlin-dsl`
}

gradlePlugin {
    plugins {
        register("ktlint-plugin") {
            id = "ktlint"
            implementationClass = "KtlintPlugin"
            description = "Format Kotlin files with ktlint"
        }

        register("project-defaults-plugin") {
            id = "project-defaults"
            implementationClass = "ProjectDefaultsPlugin"
            description = "Applies common project settings"
        }
    }
}

repositories {
    jcenter()
}

dependencies {
    implementation(gradleApi())
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.72")
}
