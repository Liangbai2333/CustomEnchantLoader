group = "site.liangbai.golddigger"

taboolib {
    subproject = true
}

tasks.named<Jar>("jar") {
    archiveBaseName.set("GoldDigger")
}