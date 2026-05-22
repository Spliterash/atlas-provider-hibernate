import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    `java-library`
    `java-gradle-plugin`
    signing
    id("com.gradle.plugin-publish") version "1.1.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    kotlin("jvm")
}

group = "io.github.spliterash"
version = System.getenv("PROVIDER_VERSION") ?: "0.0.1"

// Pinned upstream hibernate-provider artifact bundled into the shadow jar.
// Bumped manually when syncing the fork with upstream.
val hibernateProviderVersion = "3.9.0"

repositories {
    mavenCentral()
    maven {
        name = "localPluginRepository"
        url = uri("../.local-plugin-repository")
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_11
    }
}

tasks.withType<Jar> {
    archiveBaseName = "hibernate-provider-gradle-plugin"
}

tasks.named<ShadowJar>("shadowJar") {
    archiveClassifier = null
    archiveBaseName = "hibernate-provider-gradle-plugin"
}

val publishToProduction = project.hasProperty("production")

gradlePlugin {
    plugins {
        create("io.github.spliterash.hibernate-provider") {
            website = "https://github.com/Spliterash/atlas-provider-hibernate"
            vcsUrl = "https://github.com/Spliterash/atlas-provider-hibernate.git"
            description = "Atlas plugin, used as a database schema provider to Atlas. Fork of io.atlasgo.hibernate-provider-gradle-plugin with Windows long-path fix."
            displayName = "Atlas Hibernate Provider (Spliterash fork)"
            tags = listOf("database", "hibernate", "atlas", "migrations", "schema")
            id = "io.github.spliterash.hibernate-provider"
            implementationClass = "io.atlasgo.gradle.HibernateProvider"
        }
    }
}

publishing {
    repositories {
        maven {
            name = "localPluginRepository"
            url = uri("../.local-plugin-repository")
        }
    }
}

dependencies {
    compileOnly("org.hibernate.orm:hibernate-core:6.1.7.Final")
    implementation("com.github.ajalt.clikt:clikt:4.2.1")
    implementation(gradleApi())
    implementation("io.atlasgo:hibernate-provider:$hibernateProviderVersion")
    runtimeOnly(kotlin("stdlib"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<AbstractPublishToMaven>().configureEach {
    val signingTasks = tasks.withType<Sign>()
    mustRunAfter(signingTasks)
}

signing {
    isRequired = publishToProduction
    if (publishToProduction) {
        sign(publishing.publications)
    }
}