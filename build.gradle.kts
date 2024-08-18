import io.izzel.taboolib.gradle.BUKKIT
import io.izzel.taboolib.gradle.BUKKIT_ALL
import io.izzel.taboolib.gradle.UNIVERSAL
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    id("io.izzel.taboolib") version "2.0.12"
    id("org.jetbrains.kotlin.jvm") version "1.8.22"
}

subprojects {
    apply<JavaPlugin>()
    apply(plugin = "io.izzel.taboolib")
    apply(plugin = "org.jetbrains.kotlin.jvm")

    taboolib {
        description {
            name(rootProject.name)
            contributors {
                name("Liangbai")
            }
        }
        env {
            // 安装模块
            install(UNIVERSAL, BUKKIT_ALL)
        }
        version { taboolib = "6.1.2-beta10" }
    }

    repositories {
        mavenCentral()
    }

    dependencies {
        if (name != "loader") {
            compileOnly(project(":loader"))
        }
        compileOnly("ink.ptms.core:v11200:11200")
        compileOnly(kotlin("stdlib"))
    }

    tasks.named<Jar>("jar") {
        if (project.name == "loader") {
            archiveBaseName.set("CustomEnchantLoader")
        } else {
            archiveVersion.set("")
        }
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
            freeCompilerArgs = listOf("-Xjvm-default=all")
        }
    }

    configure<JavaPluginConvention> {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

tasks.register<Copy>("copyJarsToDirectory") {
    // 设置目的地目录
    into(file("plugins"))

    // 遍历所有子项目，添加它们的jar文件到复制任务
    subprojects.forEach { subproject ->
        from(subproject.tasks.named("jar"))
    }
}

tasks.named<Jar>("jar") {
    finalizedBy("copyJarsToDirectory")
}