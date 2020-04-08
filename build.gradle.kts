import com.github.spotbugs.snom.SpotBugsTask

plugins {
    `java-library`
    signing
    `maven-publish`
    `project-report`
    `build-dashboard`
    pmd
    checkstyle
    jacoco
    id("com.github.spotbugs")
    id("org.danilopianini.git-sensitive-semantic-versioning")
    id("org.danilopianini.javadoc.io-linker")
    id("org.danilopianini.publish-on-central")
    id("org.jlleitschuh.gradle.ktlint")
}

gitSemVer {
    version = computeGitSemVer()
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("commons-io:commons-io:_")
    testImplementation("com.google.guava:guava:_")
    testImplementation("junit:junit:_")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

spotbugs {
    ignoreFailures.set(true)
    setEffort("max")
    setReportLevel("low")
    showProgress.set(true)
    val excludeFile = File("${project.rootProject.projectDir}/config/spotbugs/excludes.xml")
    if (excludeFile.exists()) {
        excludeFilter.set(excludeFile)
    }
}

tasks.withType<SpotBugsTask> {
    reports {
        create("html") {
            enabled = true
        }
    }
}

pmd {
    ruleSets = listOf()
    ruleSetConfig = resources.text.fromFile("${project.rootProject.projectDir}/config/pmd/pmd.xml")
}

group = "org.danilopianini"
publishOnCentral {
    projectDescription.set(extra["projectDescription"].toString())
    projectUrl.set("https://travis-ci.org/DanySK/Thread-Inheritable-Resource-Loader-for-Java")
    projectLongName.set("Thread Inheritable Resource Loader for Java")
    scmConnection.set("git:git@github.com:DanySK/Thread-Inheritable-Resource-Loader-for-Java")
}

if (System.getenv("CI") == true.toString()) {
    signing {
        val signingKey: String? by project
        val signingPassword: String? by project
        useInMemoryPgpKeys(signingKey, signingPassword)
    }
}

publishing {
    publications {
        withType<MavenPublication> {
            pom {
                developers {
                    developer {
                        name.set("Matteo Magnani")
                    }
                    developer {
                        name.set("Danilo Pianini")
                        email.set("danilo.pianini@gmail.com")
                        url.set("http://www.danilopianini.org/")
                    }
                }
            }
        }
    }
}
