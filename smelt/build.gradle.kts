group = "site.liangbai.smelt"

taboolib {
    subproject = true
}

tasks.named<Jar>("jar") {
    archiveBaseName.set("Smelt")
}