import org.gradle.internal.os.OperatingSystem

plugins {
    id("java")
}

group = "com.hoangicloudvn"
version = "1.0.0"

repositories {
    mavenCentral()
}


dependencies {
    implementation("org.bytedeco:javacv:1.5.11")
    val os = OperatingSystem.current()

    when {
        os.isWindows -> implementation("org.bytedeco:ffmpeg:6.1.1-1.5.10:windows-x86_64")
        os.isLinux -> implementation("org.bytedeco:ffmpeg:6.1.1-1.5.10:linux-x86_64")
        os.isMacOsX -> implementation("org.bytedeco:ffmpeg:6.1.1-1.5.10:macosx-x86_64")
    }
}

tasks.jar {
    group = "build"
    description = "Build Ipcamera jar"
    val os = OperatingSystem.current()
    val classifier = when {
        os.isWindows -> "windows"
        os.isLinux -> "linux"
        os.isMacOsX -> "macosx"
        else -> "unknown"
    }
    archiveClassifier.set(classifier)
    destinationDirectory.set(layout.buildDirectory.dir("../").get().asFile)

    manifest {
        attributes("Main-Class" to "com.hoangicloudvn.Main")
    }
    from(
        sourceSets.main.get().output,
        configurations.runtimeClasspath.get().map {
            if (it.isDirectory) it else zipTree(it)
        }
    )
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
