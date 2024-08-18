group = "site.liangbai.elytra"

taboolib {
    subproject = true
}

tasks.named<Jar>("jar") {
    archiveBaseName.set("Elytra")
}