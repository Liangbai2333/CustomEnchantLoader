group = "site.liangbai.moisturize"

taboolib {
    subproject = true
}

tasks.named<Jar>("jar") {
    archiveBaseName.set("Moisturize")
}