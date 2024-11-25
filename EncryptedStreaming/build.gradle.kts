plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {

    implementation("javax.websocket:javax.websocket-api:1.1")
    implementation("org.glassfish.tyrus.bundles:tyrus-standalone-client:2.1.1")
    implementation("org.glassfish.tyrus:tyrus-server:2.1.1")
    implementation("org.glassfish.tyrus:tyrus-container-grizzly-server:2.1.1")
}
