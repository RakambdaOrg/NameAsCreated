plugins {
    idea
    java
    `java-library`
    `maven-publish`
    id("com.github.ben-manes.versions") version ("0.38.0")
    id("io.freefair.lombok") version ("6.0.0-m2")
    id("com.github.breadmoirai.github-release") version ("2.2.12")
}

group = "fr.raksrinana"
description = "NameAsCreated"

dependencies {
    api(libs.slf4j)

    api(libs.unirest)
    api(libs.bundles.jackson)

    api(libs.metadataExtractor)
    api(libs.pointLocation)

    compileOnly(libs.jetbrainsAnnotations)
}

repositories {
    mavenCentral()
    jcenter()
}

tasks {
    processResources {
        expand(project.properties)
    }

    compileJava {
        val moduleName: String by project
        inputs.property("moduleName", moduleName)

        options.encoding = "UTF-8"
        options.isDeprecation = true

        doFirst {
            val compilerArgs = options.compilerArgs
            compilerArgs.add("--module-path")
            compilerArgs.add(classpath.asPath)
            classpath = files()
        }
    }

    wrapper {
        val wrapperVersion: String by project
        gradleVersion = wrapperVersion
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_16
    targetCompatibility = JavaVersion.VERSION_16
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/RakSrinaNa/NameAsCreated")
            credentials {
                username = project.findProperty("githubRepoUsername") as String?
                password = project.findProperty("githubRepoPassword") as String?
            }
        }
    }
    publications {
        create<MavenPublication>("gpr") {
            from(components["java"])
        }
    }
}

githubRelease {
    val githubRepoPassword: String by project
    val version: String by project

    owner("RakSrinaNa")
    repo("NameAsCreated")
    token(githubRepoPassword)
    tagName("v${version}")
    releaseName(version)
}
