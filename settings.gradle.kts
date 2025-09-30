pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://maven.pkg.github.com/spotify/android-auth")
            credentials {
                // IMPORTANT: Replace YOUR_GITHUB_USERNAME and YOUR_PERSONAL_ACCESS_TOKEN
                // You need a GitHub PAT with 'read:packages' permission.
                username = System.getenv("GITHUB_ACTOR") ?: "daedaevibn"
                password = System.getenv("GITHUB_TOKEN") ?: "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"
            }
        }
    }
}

rootProject.name = "桜の雨"
include(":app")
