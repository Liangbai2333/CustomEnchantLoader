group = "site.liangbai.farm"

taboolib {
    subproject = true
}

tasks.named<Jar>("jar") {
    archiveBaseName.set("Farm")
}