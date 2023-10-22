/*
 * Copyright (c) 2023.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
val javaVersion: String by System.getProperties()

plugins {
    `java-library`
    id("com.github.jk1.dependency-license-report") version "2.5"
}

repositories {
    mavenLocal()
    mavenCentral()
}

// extend api so that we can resolve it
val exported: Configuration by configurations.creating {
    extendsFrom(configurations.api.get())
}

dependencies {
    // https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind
    api("com.fasterxml.jackson.core:jackson-databind:2.15.3")

    // ------------ PROVIDED ------------

    // https://mvnrepository.com/artifact/org.apache.kafka/connect-api
    implementation("org.apache.kafka:connect-api:3.6.0")
    // https://mvnrepository.com/artifact/org.slf4j/slf4j-api
    implementation("org.slf4j:slf4j-api:2.0.9")

    // ------------ TEST ------------

    // https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter-api
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    // https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter-engine
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
    // https://mvnrepository.com/artifact/org.assertj/assertj-core
    testImplementation("org.assertj:assertj-core:3.24.2")
    // https://mvnrepository.com/artifact/commons-io/commons-io
    testImplementation("commons-io:commons-io:2.14.0")
}

val depFolder = "${layout.buildDirectory.get()}/dependencies"
val libsFolder = "${layout.buildDirectory.get()}/libs"
val docFolder = "${layout.projectDirectory}/doc"
val licensesDir = "${layout.buildDirectory.get()}/licenses"

val depConfig = exported

val listDependencies by tasks.register("listDependencies") {
    doLast {
        depConfig.forEach { dependency ->
            println("Dependency: ${dependency.nameWithoutExtension}.${dependency.extension}")
        }
    }
}

val deleteDependencies = task(
    "deleteDependencies",
    Delete::class
) {
    delete(depFolder)
}

val copyDependencies = task(
    "copyDependencies",
    Copy::class
) {
    from(depConfig).into(depFolder)
    dependsOn(
        deleteDependencies,
        listDependencies
    )
}

licenseReport {
    outputDir = licensesDir
    configurations = arrayOf("exported")
}

task(
    "connector",
    Zip::class
) {
    into("${project.name}-${project.version}") {
        into("lib") {
            from(
                depFolder,
                libsFolder
            )
            include("*.jar")
        }
        into("doc") {
            from(docFolder)
            include("**/*")
        }
        into("doc/licenses") {
            from(licensesDir)
            include("**/*")
        }

        from("${layout.projectDirectory}/src/main")
        include("manifest.json")
    }


    destinationDirectory = layout.buildDirectory.dir("connector")
    archiveFileName.set("${project.name}-${project.version}.zip")

    dependsOn(
        "build",
        copyDependencies,
        "generateLicenseReport"
    )
}



tasks {
    withType<JavaCompile> {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
        options.isIncremental = true
    }

    withType<Test> {
        useJUnitPlatform()
    }

    withType<Copy> {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(javaVersion))
    }
}