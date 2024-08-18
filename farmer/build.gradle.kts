group = "site.liangbai.farmer"

taboolib {
    subproject = true
}

tasks.named<Jar>("jar") {
    archiveBaseName.set("Farmer")
}