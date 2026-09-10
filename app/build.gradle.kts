import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.file.RelativePath
import org.gradle.api.provider.Property
import java.security.MessageDigest
import java.util.Properties

plugins {
    id("org.autojs.build.utils")
    id("org.autojs.build.versions")
    id("org.autojs.build.signs")
    id("org.autojs.build.jvm-convention")
    id("com.android.application")
}

val globalApplicationId = "io.github.supermonster003.autojs6.plugin.htmlpreviewer"

val buildTypeDebug = "debug"
val buildTypeRelease = "release"

val explorerActionCompatibilityFile =
    rootProject.file("gradle/explorer-action-compatibility.properties")
val explorerActionCompatibility = Properties().apply {
    explorerActionCompatibilityFile.inputStream().use(::load)
}

fun explorerActionCompatibilityProperty(name: String): String =
    explorerActionCompatibility.getProperty(name)
        ?.trim()
        ?.takeIf(String::isNotEmpty)
        ?: error("Missing Explorer Action compatibility property: $name")

val explorerActionProtocolVersion =
    explorerActionCompatibilityProperty("declaredProtocolVersion").toInt()
val explorerActionMinimumHostVersionCode =
    explorerActionCompatibilityProperty("minimumHostVersionCode").toLong()
val explorerActionMaximumAuditedHostVersionCode =
    explorerActionCompatibilityProperty("maximumAuditedHostVersionCode").toLong()
val explorerActionMaximumAuditedHostProtocolVersion =
    explorerActionCompatibilityProperty("maximumAuditedHostProtocolVersion").toInt()
val explorerActionApiSha256 =
    explorerActionCompatibilityProperty("explorerActionApiSha256").uppercase()

android {
    namespace = globalApplicationId
    compileSdk = versions.sdkVersionCompile

    defaultConfig {
        applicationId = globalApplicationId
        minSdk = versions.sdkVersionMin
        targetSdk = versions.sdkVersionTarget
        versionCode = versions.appVersionCode
        versionName = versions.appVersionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("int", "EXPLORER_ACTION_PROTOCOL_VERSION", explorerActionProtocolVersion.toString())
        buildConfigField(
            "long",
            "EXPLORER_ACTION_MINIMUM_HOST_VERSION_CODE",
            "${explorerActionMinimumHostVersionCode}L",
        )
        buildConfigField(
            "long",
            "EXPLORER_ACTION_MAXIMUM_AUDITED_HOST_VERSION_CODE",
            "${explorerActionMaximumAuditedHostVersionCode}L",
        )
        buildConfigField(
            "int",
            "EXPLORER_ACTION_MAXIMUM_AUDITED_HOST_PROTOCOL_VERSION",
            explorerActionMaximumAuditedHostProtocolVersion.toString(),
        )

        resValue("string", "plugin_author", "SuperMonster003")
        resValue("string", "plugin_version_date", utils.getDateString("MMM d, yyyy", "GMT+08:00"))
    }

    lint {
        abortOnError = false
    }

    signingConfigs {
        if (signs.isValid) {
            create(buildTypeRelease) {
                storeFile = signs.properties["storeFile"]?.let { file(it as String) }
                keyPassword = signs.properties["keyPassword"] as String
                keyAlias = signs.properties["keyAlias"] as String
                storePassword = signs.properties["storePassword"] as String
            }
        }
    }

    buildTypes {
        val proguardFiles = arrayOf<Any>(
            getDefaultProguardFile("proguard-android-optimize.txt"),
            "proguard-rules.pro",
        )
        val niceSigningConfig = takeIf { signs.isValid }?.let {
            signingConfigs.getByName(buildTypeRelease)
        }
        debug {
            isMinifyEnabled = false
            proguardFiles(*proguardFiles)
            niceSigningConfig?.let { signingConfig = it }
        }
        release {
            isMinifyEnabled = true
            proguardFiles(*proguardFiles)
            niceSigningConfig?.let { signingConfig = it }
        }
    }

    buildFeatures {
        aidl = true
        buildConfig = true
        resValues = true
        viewBinding = true
    }

    sourceSets.named("main") {
        kotlin.directories += "src/main/java"
    }

    packaging {
        resources.pickFirsts.addAll(
            listOf(
                "META-INF/DEPENDENCIES",
                "META-INF/LICENSE",
                "META-INF/LICENSE.*",
                "META-INF/NOTICE",
                "META-INF/NOTICE.*",
                "META-INF/*.kotlin_module",
            ),
        )
    }

    bundle {
        language.enableSplit = false
        density.enableSplit = false
        abi.enableSplit = false
    }
}

androidComponents {
    onVariants { variant ->
        variant.outputs.forEach { output ->
            val outputFileNameProperty = output.javaClass.methods.firstOrNull {
                it.name == "getOutputFileName" && it.parameterTypes.isEmpty()
            }?.invoke(output) as? Property<*>

            @Suppress("UNCHECKED_CAST")
            (outputFileNameProperty as? Property<String>)?.set(
                output.versionName.map { versionName ->
                    val version = versionName.replace("\\s".toRegex(), "-")
                    "${rootProject.name}-v$version.${utils.FILE_EXTENSION_APK}".lowercase()
                },
            )
        }
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.2.21")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")

    implementation(files("$rootDir/libs/common-plugin-api.aar"))
    implementation(files("$rootDir/libs/explorer-action-api.aar"))

    implementation(libs.activity.ktx)
    implementation(libs.appcompat)
    implementation(libs.core.ktx)
    implementation(libs.jsoup)
    implementation(libs.material)
    implementation(libs.webkit)
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.4")

    testImplementation(libs.junit)
    androidTestImplementation(libs.test.ext.junit)
    androidTestImplementation(libs.test.runner)
}

tasks {
    val verifyExplorerActionApiCompatibility = register("verifyExplorerActionApiCompatibility") {
        description = "Verifies the audited Explorer Action v1 AAR digest"
        group = "verification"
        val apiAar = rootProject.file("libs/explorer-action-api.aar")
        inputs.file(apiAar)
        inputs.file(explorerActionCompatibilityFile)

        doLast {
            val digest = MessageDigest.getInstance("SHA-256")
            apiAar.inputStream().use { input ->
                val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                while (true) {
                    val count = input.read(buffer)
                    if (count < 0) break
                    digest.update(buffer, 0, count)
                }
            }
            val actual = digest.digest().joinToString("") { byte ->
                "%02X".format(byte.toInt() and 0xFF)
            }
            check(actual == explorerActionApiSha256) {
                "Explorer Action API AAR digest changed: expected $explorerActionApiSha256, actual $actual. " +
                    "Audit the protocol and update the compatibility matrix before accepting a new AAR."
            }
            println("Explorer Action API compatibility OK: protocol v$explorerActionProtocolVersion, SHA-256 $actual")
        }
    }

    named("preBuild").configure {
        dependsOn(verifyExplorerActionApiCompatibility)
    }

    withType(JavaCompile::class.java) {
        options.encoding = "UTF-8"
    }

    register<Copy>("appendDigestToReleasedFiles") {
        description = "Appends CRC32 digest to released APK files"
        dependsOn("assembleRelease")

        val ext = utils.FILE_EXTENSION_APK
        val src = layout.buildDirectory.dir("outputs/apk/$buildTypeRelease")
        val dst = file("${buildTypeRelease}s")

        from(src)
        into(dst)
        include("*.$ext")
        includeEmptyDirs = false
        duplicatesStrategy = DuplicatesStrategy.FAIL

        eachFile {
            val digest = utils.digestCRC32(file)
            relativePath = RelativePath(true, "${name.removeSuffix(".$ext")}-$digest.$ext")
        }

        doLast { println("Destination: $dst") }
    }
}

extra {
    versions.handleIfNeeded(project, "", listOf(buildTypeDebug, buildTypeRelease))
}
