# atlas-provider-hibernate

Fork with window's long path support

To use


settings.gradle.kts
```kotlin
pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "io.github.spliterash.hibernate-provider") {
                useModule("com.github.Spliterash.atlas-provider-hibernate:hibernate-provider-gradle-plugin:${requested.version}")
            }
        }
    }
}
```

build.gradle.kts
```kotlin
    id("io.github.spliterash.hibernate-provider") version "0.0.1"
```