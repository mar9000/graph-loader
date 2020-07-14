plugins {
    `java-library`
    `maven`
    id("me.champeau.gradle.jmh") version "0.5.0"
}

group = "com.github.mar9000"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {

    testImplementation("org.junit.jupiter:junit-jupiter:5.6.2")
    testImplementation("org.openjdk.jmh:jmh-core:1.23")
    testImplementation("org.openjdk.jmh:jmh-generator-annprocess:1.23")
    testImplementation("com.graphql-java:java-dataloader:2.2.3")
    testImplementation("com.graphql-java:graphql-java:15.0")

    jmh("org.openjdk.jmh:jmh-core:1.23")
    jmh("org.openjdk.jmh:jmh-generator-annprocess:1.23")
    jmh("com.graphql-java:java-dataloader:2.2.3")
    jmh("com.graphql-java:graphql-java:15.0")
}

jmh {
    timeUnit = "us"
    profilers = mutableListOf("gc", "stack")
    fork = 1
    warmupIterations = 3
    warmup = "10s"
    iterations = 3
    timeOnIteration = "10s"
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

java {
    withSourcesJar()
}
