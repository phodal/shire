package com.phodal.shirelang.javascript.variable

interface Framework {
    val presentation: String
    val packageName: String
}

enum class JsWebFrameworks(override val presentation: String, override val packageName: String) : Framework {
    React("React", "react"),
    Vue("Vue", "vue"),
    Angular("Angular", "@angular/core"),
    AngularJS("AngularJS", "angular"),
    Svelte("Svelte", "svelte"),
    Astro("Astro", "astro"),
    Lit("Lit", "lit"),
    Solid("Solid", "solid-js"),
    Preact("Preact", "preact"),
    Next("Next", "next"),
    Nuxt("Nuxt", "nuxt"),
}

enum class JsTestFrameworks(override val presentation: String, override val packageName: String) : Framework {
    Jest("Jest", "jest"),
    Mocha("Mocha", "mocha"),
    Jasmine("Jasmine", "jasmine"),
    Karma("Karma", "karma"),
    Ava("Ava", "ava"),
    Tape("Tape", "tape"),
    Qunit("Qunit", "qunit"),
    Tap("Tap", "tap"),
    Cypress("Cypress", "cypress"),
    Protractor("Protractor", "protractor"),
    Nightwatch("Nightwatch", "nightwatch"),
    Vitest("Vitest", "vitest")
}

private const val TYPESCRIPT_PACKAGE = "typescript"

val MOST_POPULAR_PACKAGES = setOf(
    "lodash",
    "request",
    "commander",
    "react",
    "express",
    "async",
    "moment",
    "prop-types",
    "react-dom",
    "bluebird",
    "underscore",
    "vue",
    "axios",
    "tslib",
    "glob",
    "yargs",
    "colors",
    "webpack",
    "uuid",
    "classnames",
    "minimist",
    "body-parser",
    "rxjs",
    "babel-runtime",
    "jquery",
    "babel-core",
    "core-js",
    "babel-loader",
    "cheerio",
    "rimraf",
    "eslint",
    "dotenv",
    TYPESCRIPT_PACKAGE,
    "@types/node",
    "@angular/core",
    "@angular/common",
    "redux",
    "gulp",
    "node-fetch",
    "@angular/platform-browser",
    "@babel/runtime",
    "handlebars",
    "@angular/compiler",
    "aws-sdk",
    "@angular/forms",
    "webpack-dev-server",
    "@angular/platform-browser-dynamic",
    "mocha",
    "socket.io",
    "ws",
    "node-sass",
    "@angular/router",
    "ramda",
    "react-redux",
    "@babel/core",
    "@angular/http",
    "ejs",
    "coffee-script",
    "mongodb",
    "chai",
    "mongoose",
    "xml2js",
    "bootstrap",
    "jest",
    "redis",
    "vue-router",
    "optimist",
    "promise",
    "@angular/animations",
    "postcss",
    "morgan",
    "less",
    "immutable"
)
