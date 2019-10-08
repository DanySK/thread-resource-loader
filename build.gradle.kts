import com.github.spotbugs.SpotBugsTask

plugins {
	`java-library`
	signing
	`maven-publish`
	`project-report`
	`build-dashboard`
	pmd
	checkstyle
	jacoco
	id("com.github.spotbugs") version Versions.com_github_spotbugs_gradle_plugin
	id("de.fayard.buildSrcVersions") version Versions.de_fayard_buildsrcversions_gradle_plugin
	id("org.danilopianini.git-sensitive-semantic-versioning") version Versions.org_danilopianini_git_sensitive_semantic_versioning_gradle_plugin
	id("org.danilopianini.javadoc.io-linker") version Versions.org_danilopianini_javadoc_io_linker_gradle_plugin
	id("org.danilopianini.publish-on-central") version Versions.org_danilopianini_publish_on_central_gradle_plugin
}

gitSemVer {
    version = computeGitSemVer()
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(Libs.commons_io)
    testImplementation(Libs.guava)
    testImplementation(Libs.junit)
}

java {
	sourceCompatibility = JavaVersion.VERSION_1_8
	targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<SpotBugsTask> {
	reports {
		xml.setEnabled(false)
		html.setEnabled(true)
	}
	ignoreFailures = false
	effort = "max"
	reportLevel = "low"
	File("${project.rootProject.projectDir}/findbugsExcludes.xml")
		.takeIf { it.exists() }
		?.also { excludeFilterConfig = project.resources.text.fromFile(it) }
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

signing {
	val signingKey: String? by project
	val signingPassword: String? by project
	useInMemoryPgpKeys(signingKey, signingPassword)
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
