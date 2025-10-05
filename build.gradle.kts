// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.spotless) apply false
    alias(libs.plugins.detekt) apply false
}

// Configure ktlint for all projects
subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
        version.set("0.50.0")
        android.set(true)
        outputToConsole.set(true)
        outputColorName.set("RED")
        ignoreFailures.set(false)
        enableExperimentalRules.set(true)
        filter {
            exclude("**/generated/**")
            exclude("**/build/**")
        }
    }

    // Configure Spotless for code formatting
    apply(plugin = "com.diffplug.spotless")

    configure<com.diffplug.gradle.spotless.SpotlessExtension> {
        kotlin {
            target("**/*.kt")
            ktlint("0.50.0")
            trimTrailingWhitespace()
            indentWithSpaces()
            endWithNewline()
        }
        kotlinGradle {
            target("**/*.gradle.kts")
            ktlint("0.50.0")
            trimTrailingWhitespace()
            indentWithSpaces()
            endWithNewline()
        }
    }

    // Configure Detekt for static code analysis
    apply(plugin = "io.gitlab.arturbosch.detekt")
}

// Add convenient linting tasks
tasks.register("lintAll") {
    group = "verification"
    description = "Run all linting tools (ktlint, detekt, android lint)"
    dependsOn(":app:ktlintCheck", ":app:detekt", ":app:lint")
}

tasks.register("formatAll") {
    group = "formatting"
    description = "Format code with ktlint and spotless"
    dependsOn(":app:ktlintFormat", ":app:spotlessApply")
}

tasks.register("checkQuality") {
    group = "verification"
    description = "Run all code quality checks"
    dependsOn("lintAll")
}
