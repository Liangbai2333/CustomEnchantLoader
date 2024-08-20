group = "site.liangbai.eternal"

taboolib {
    subproject = true
}

tasks.named<Jar>("jar") {
    archiveBaseName.set("Eternal")
}