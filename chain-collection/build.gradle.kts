group = "site.liangbai.chaincollection"

taboolib {
    subproject = true
}

tasks.named<Jar>("jar") {
    archiveBaseName.set("ChainCollection")
}