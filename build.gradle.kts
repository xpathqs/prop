import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

version = "0.2.1"

plugins {
    kotlin("jvm") version "1.5.30"
    id("org.jetbrains.dokka") version "1.4.32"
    `java-library`
    jacoco
    maven
    `maven-publish`
    signing
    kotlin("plugin.serialization") version "1.5.30"
    id("io.codearte.nexus-staging") version "0.30.0"
    id("io.gitlab.arturbosch.detekt").version("1.18.0-RC2")
    id("info.solidsoft.pitest").version("1.7.0")
    id("org.jetbrains.kotlinx.kover") version "0.4.1"
}

java {
    withJavadocJar()
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

jacoco {
    toolVersion = "0.8.7"
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.5.30")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.2")

    implementation("org.yaml:snakeyaml:1.28")
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("net.oneandone.reflections8:reflections8:0.11.7")

    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.0")
    testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.23.1")
}

publishing {
    publications {
        beforeEvaluate {
            signing.sign(this@publications)
        }
        create<MavenPublication>("mavenJava") {
            pom {
                name.set("XpathQS I18N")
                description.set("A prop library")
                url.set("https://xpathqs.org/")
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("http://www.opensource.org/licenses/mit-license.php")
                    }
                }
                developers {
                    developer {
                        id.set("nachg")
                        name.set("Nikita A. Chegodaev")
                        email.set("nikchgd@gmail.com")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/xpathqs/prop.git")
                    developerConnection.set("scm:git:ssh://github.com/xpathqs/prop.git")
                    url.set("https://xpathqs.org/")
                }
            }
            groupId = "org.xpathqs"
            artifactId = "prop"

            from(components["java"])
        }
    }
    repositories {
        maven {
            val releasesRepoUrl = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            val snapshotsRepoUrl = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
            credentials {
                username = project.property("ossrhUsername").toString()
                password = project.property("ossrhPassword").toString()
            }
        }
    }
}

signing {
    sign(publishing.publications["mavenJava"])
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

tasks.jar {
    manifest {
        attributes(
            mapOf(
                "Implementation-Title" to project.name,
                "Implementation-Version" to project.version
            )
        )
    }
}

tasks.jacocoTestReport {
    reports {
        xml.isEnabled = false
        csv.isEnabled = true
    }
}

tasks.withType<org.jetbrains.dokka.gradle.DokkaTask>().configureEach {
    dokkaSourceSets {
        configureEach {
            samples.from("src/test/kotlin/org/xpathqs/prop", "src/main/kotlin/org/xpathqs/prop")
        }
    }
}