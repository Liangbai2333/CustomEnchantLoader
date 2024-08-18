group = "site.liangbai.damageprotection"

taboolib {
    subproject = true
}

tasks.named<Jar>("jar") {
    archiveBaseName.set("DamageProtection")
}