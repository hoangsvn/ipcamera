plugins {
    id("java")
}

group = "com.hoangicloudvn"
version = "1.0.0"

repositories {
    mavenCentral()
}

configurations {
    create("osWindows")
    create("osLinux")
}
dependencies {
    implementation("org.bytedeco:javacv:1.5.11")
    implementation("org.bytedeco:ffmpeg:6.1.2-1.5.11:windows-x86_64")
}


tasks.register<Jar>("jarLinux") {
    group = "build"
    description = "Build fat JAR cho Linux"
    archiveClassifier.set("linux")
    destinationDirectory.set(layout.buildDirectory.dir("../").get().asFile)
    dependencies {
        implementation("org.bytedeco:ffmpeg:6.1.2-1.5.11:linux-x86_64")
    }
    manifest {
        attributes("Main-Class" to "com.hoangicloudvn.Main")
    }
    from(sourceSets.main.get().output)
    from({
        configurations.runtimeClasspath.get().plus(
            configurations.getByName("osLinux")
        ).map {
            if (it.isDirectory) it else zipTree(it)
        }
    })

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
tasks.jar {
    group = "build"
    description = "Build fat JAR cho Windows"
    archiveClassifier.set("windows")
    destinationDirectory.set(layout.buildDirectory.dir("../").get().asFile)
    dependencies {
        implementation("org.bytedeco:ffmpeg:6.1.2-1.5.11:windows-x86_64")
    }
    manifest {
        attributes("Main-Class" to "com.hoangicloudvn.Main")
    }
    from(sourceSets.main.get().output)
    from({
        configurations.runtimeClasspath.get().plus(
            configurations.getByName("osWindows")
        ).map {
            if (it.isDirectory) it else zipTree(it)
        }
    })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
