import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

group = "site.liangbai.customenchantloader"
version = "1.0.0"

dependencies {
    compileOnly(fileTree(rootDir.resolve("libs")))
}