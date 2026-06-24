import java.util.Properties

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.androidx.room)
}

val generatedLlmConfigDir = layout.buildDirectory.dir("generated/source/llmLocalConfig/commonMain/kotlin")

val generateLlmLocalConfig by tasks.registering {
    val localPropertiesFile = rootProject.layout.projectDirectory.file("local.properties")

    if (localPropertiesFile.asFile.isFile) {
        inputs.file(localPropertiesFile)
            .withPathSensitivity(PathSensitivity.RELATIVE)
    } else {
        inputs.property("missingLocalProperties", true)
    }
    outputs.dir(generatedLlmConfigDir)

    doLast {
        val properties = Properties()
        val file = localPropertiesFile.asFile
        if (file.isFile) {
            file.inputStream().use(properties::load)
        }

        fun property(name: String): String = properties.getProperty(name)?.trim().orEmpty()

        fun String.kotlinLiteral(): String = buildString {
            append('"')
            this@kotlinLiteral.forEach { char ->
                when (char) {
                    '\\' -> append("\\\\")
                    '"' -> append("\\\"")
                    '\n' -> append("\\n")
                    '\r' -> append("\\r")
                    '\t' -> append("\\t")
                    else -> append(char)
                }
            }
            append('"')
        }

        val outputFile = generatedLlmConfigDir.get()
            .file("com/zhangke/koog/basic/core/llm/model/LlmLocalConfig.kt")
            .asFile
        outputFile.parentFile.mkdirs()
        outputFile.writeText(
            """
            package com.zhangke.koog.basic.core.llm.model

            internal object LlmLocalConfig {
                const val QWEN_API_KEY: String = ${property("qwen.api.key").kotlinLiteral()}
                const val KIMI_API_KEY: String = ${property("kimi.api.key").kotlinLiteral()}
                const val OPENAI_API_KEY: String = ${property("openai.api.key").kotlinLiteral()}
            }
            """.trimIndent(),
        )
    }
}

kotlin {
    jvm("desktop") {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_16)
        }
    }

    sourceSets {
        val commonMain by getting {
            kotlin.srcDir(generateLlmLocalConfig)

            dependencies {
                implementation(project(":framework"))
                implementation(libs.androidx.room.runtime)
                implementation(libs.androidx.sqlite.bundled)
                implementation(libs.koin.core)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.client.serialization.kotlinx.json)
                implementation(libs.kotlin.coroutines.core)
                implementation(libs.kotlinx.serialization.json)
                api(libs.koog.agents)
                api(libs.koog.hashscope.client)
                api(libs.koog.openai.client)
            }
        }
        val desktopMain by getting {
            dependencies {
                implementation(libs.zip4j)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

dependencies {
    add("kspDesktop", libs.androidx.room.compiler)
}

room {
    schemaDirectory("$projectDir/schemas")
}
