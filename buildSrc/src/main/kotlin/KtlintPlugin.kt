import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.JavaExec
import org.gradle.kotlin.dsl.*

/**
 * Provides tasks for checking and, where possible, fixing formatting and style errors using
 * [ktlint](https://ktlint.github.io/).
 */
class KtlintPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.run {
            val ktlint: Configuration by project.configurations.creating

            dependencies {
                ktlint("com.pinterest", "ktlint", "0.36.0")
            }

            tasks {
                register<JavaExec>("ktlintApplyToIdeaProject") {
                    description = "REPLACE IntelliJ style with ktlint for this project"
                    main = "com.pinterest.ktlint.Main"
                    classpath = ktlint
                    args = listOf("--apply-to-idea-project", "--experimental", "-y")
                }
            }

            afterEvaluate {
                withConvention(JavaPluginConvention::class) {
                    tasks {
                        for (sourceSet in sourceSets) {
                            val sourceSetName =
                                if (sourceSet.name == "main") ""
                                else sourceSet.name.capitalize()

                            val baseArgs = listOf("--experimental")

                            val files =
                                sourceSet.allSource.sourceDirectories.files
                                    .map { it.path + "/**/*.kt" }

                            val checkTaskName = "ktlintCheck$sourceSetName"

                            register<JavaExec>(checkTaskName) {
                                group = "verification"
                                description = "Check Kotlin code style"
                                main = "com.pinterest.ktlint.Main"
                                classpath = ktlint
                                args = baseArgs + files
                            }

                            named("check") {
                                dependsOn(checkTaskName)
                            }

                            val formatTaskName = "ktlintFormat$sourceSetName"

                            register<JavaExec>("ktlintFormat$sourceSetName") {
                                group = "verification"
                                description = "Fix Kotlin code style deviations"
                                main = "com.pinterest.ktlint.Main"
                                classpath = ktlint
                                args = baseArgs + listOf("-F") + files
                            }

                            named("compile${sourceSetName}Kotlin") {
                                dependsOn(formatTaskName)
                            }
                        }
                    }
                }
            }
        }
    }
}
