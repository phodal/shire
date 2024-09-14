package com.phodal.shire.marketplace.model

data class ShirePackage(
    val title: String,
    val description: String,
    val link: String,
    val installCmd: String = "",
    val featured: Boolean = false
) {
    // for Jackson, do not remove
    constructor() : this("", "", "")
}