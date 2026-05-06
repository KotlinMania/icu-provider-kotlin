# icu-provider-kotlin in Kotlin

[![GitHub link](https://img.shields.io/badge/GitHub-KotlinMania%2Ficu--provider--kotlin-blue.svg)](https://github.com/KotlinMania/icu-provider-kotlin)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.kotlinmania/icu-provider-kotlin)](https://central.sonatype.com/artifact/io.github.kotlinmania/icu-provider-kotlin)
[![Build status](https://img.shields.io/github/actions/workflow/status/KotlinMania/icu-provider-kotlin/ci.yml?branch=main)](https://github.com/KotlinMania/icu-provider-kotlin/actions)

This is a Kotlin Multiplatform line-by-line transliteration port of [`unicode-org/icu4x`](https://github.com/unicode-org/icu4x).

**Original Project:** This port is based on [`unicode-org/icu4x`](https://github.com/unicode-org/icu4x). All design credit and project intent belong to the upstream authors; this repository is a faithful port to Kotlin Multiplatform with no behavioural changes intended.

### Porting status

This is an **in-progress port**. The goal is feature parity with the upstream Rust crate while providing a native Kotlin Multiplatform API. Every Kotlin file carries a `// port-lint: source <path>` header naming its upstream Rust counterpart so the AST-distance tool can track provenance.

---

## Upstream README — `unicode-org/icu4x`

> The text below is reproduced and lightly edited from [`https://github.com/unicode-org/icu4x`](https://github.com/unicode-org/icu4x). It is the upstream project's own description and remains under the upstream authors' authorship; links have been rewritten to absolute upstream URLs so they continue to resolve from this repository.

<p align="center">
<a href="https://icu4x.unicode.org">
<img src="https://icu4x.unicode.org/icon_glow.png" alt="Logo" width="150" height="150">
</a>
</p>

## Welcome to ICU4X

[![crates.io](https://img.shields.io/crates/v/icu)](https://crates.io/crates/icu)
[![npm](https://img.shields.io/npm/v/icu)](https://www.npmjs.com/package/icu)
[![pub.dev](https://img.shields.io/pub/v/icu4x?include_prereleases)](https://pub.dev/packages/icu4x)


[![Build Status](https://github.com/unicode-org/icu4x/actions/workflows/build-test.yml/badge.svg)](https://github.com/unicode-org/icu4x/actions)
[![Coverage Status (Coveralls)](https://coveralls.io/repos/github/unicode-org/icu4x/badge.svg?branch=main)](https://coveralls.io/github/unicode-org/icu4x?branch=main)
[![Coverage Status (Codecov)](https://codecov.io/gh/unicode-org/icu4x/branch/main/graph/badge.svg)](https://app.codecov.io/gh/unicode-org/icu4x/tree/main)

ICU4X provides components enabling wide range of software internationalization.
It draws deeply from the experience of [ICU4C/J](https://github.com/unicode-org/icu), [ECMA-402](https://github.com/tc39/ecma402/) and relies on data from the [CLDR](https://cldr.unicode.org/) project.

ICU4X is fully implemented in the [Rust programming language](https://rust-lang.org).

The design goals of ICU4X are:

* Small and modular code
* Pluggable locale data
* Availability and ease of use in multiple programming languages
* Written by internationalization experts to encourage best practices

***Stay informed!*** Join our public, low-traffic mailing list: [icu4x-announce@unicode.org](https://groups.google.com/a/unicode.org/g/icu4x-announce).  *Note: After subscribing, check your spam folder for a confirmation.*

For more information, please visit [our website](https://icu4x.unicode.org/).

## Quick Start

An example ICU4X powered application in Rust may look like this:

`Cargo.toml`:

```toml
[dependencies]
icu = "2.0.0"
```

`src/main.rs`:

```rust
use icu::calendar::Date;
use icu::datetime::{DateTimeFormatter, fieldsets::YMD};
use icu::locale::locale;

let dtf = DateTimeFormatter::try_new(
    locale!("es").into(),
    YMD::long()
)
.expect("locale should be present in compiled data");

let date = Date::try_new_iso(2020, 9, 12).expect("date should be valid");

let formatted_date = dtf.format(&date).to_string();
assert_eq!(
    formatted_date,
    "12 de septiembre de 2020"
);
```

## Development

ICU4X is developed by the ICU4X Technical Committee (ICU4X-TC) in the Unicode Consortium. The ICU4X-TC leads strategy and development of internationalization solutions for modern platforms and ecosystems, including client-side and resource-constrained environments. See [unicode.org](https://www.unicode.org/consortium/techchairs.html) for more information on our governance.

ICU4X-TC convenes approximately once per quarter in advance of ICU4X releases. Most work in the interim takes place in the ICU4X Working Group (ICU4X WG), which makes technical recommendations, lands them in the repository, and records them in CHANGELOG.md. The recommendations of ICU4X WG are subject to approval by the ICU4X-TC.

Please subscribe to this repository to participate in discussions.  If you want to contribute, see our [contributing.md](https://github.com/unicode-org/icu4x/blob/HEAD/CONTRIBUTING.md).

## Charter

*For the full charter, including answers to frequently asked questions, see [charter.md](https://github.com/unicode-org/icu4x/blob/HEAD/documents/process/charter.md).*

ICU4X is a new project whose objective is to solve the needs of clients who wish to provide client-side internationalization for their products in resource-constrained environments.

ICU4X, or "ICU for X", will be built from the start with several key design constraints:

1. Small and modular code.
2. Pluggable locale data.
3. Availability and ease of use in multiple programming languages.
4. Written by internationalization experts to encourage best practices.

ICU4X will provide an ECMA-402-compatible API surface in the target client-side platforms, including the web platform, iOS, Android, WearOS, WatchOS, Flutter, and Fuchsia, supported in programming languages including Rust, JavaScript, Objective-C, Java, Dart, and C++.

## Licensing and Copyright

Copyright © 2020-2024 Unicode, Inc. Unicode and the Unicode Logo are registered trademarks of Unicode, Inc. in the United States and other countries.

The project is released under [LICENSE](https://github.com/unicode-org/icu4x/blob/HEAD/LICENSE), the free and open-source [Unicode License](https://www.unicode.org/license.txt), which is based on the well-known MIT license, with the primary difference being that the Unicode License expressly covers data and data files, as well as code. For further information please see [The Unicode Consortium Intellectual Property, Licensing, and Technical Contribution Policies](https://www.unicode.org/policies/licensing_policy.html).

A CLA is required to contribute to this project - please refer to the [CONTRIBUTING.md](https://github.com/unicode-org/icu4x/blob/HEAD/CONTRIBUTING.md) file (or start a Pull Request) for more information.

The contents of this repository are governed by the Unicode [Terms of Use](https://www.unicode.org/copyright.html).

---

## About this Kotlin port

### Installation

```kotlin
dependencies {
    implementation("io.github.kotlinmania:icu-provider-kotlin:0.1.0-SNAPSHOT")
}
```

### Building

```bash
./gradlew build
./gradlew test
```

### Targets

- macOS arm64
- Linux x64
- Windows mingw-x64
- iOS arm64 / simulator-arm64 (Swift export + XCFramework)
- JS (browser + Node.js)
- Wasm-JS (browser + Node.js)
- Android (API 24+)

### Porting guidelines

See [AGENTS.md](AGENTS.md) and [CLAUDE.md](CLAUDE.md) for translator discipline, port-lint header convention, and Rust → Kotlin idiom mapping.

### License

This Kotlin port is distributed under the same Unicode-3.0 license as the upstream [`unicode-org/icu4x`](https://github.com/unicode-org/icu4x). See [LICENSE](LICENSE) (and any sibling `LICENSE-*` / `NOTICE` files mirrored from upstream) for the full text.

Original work copyrighted by the icu4x authors.  
Kotlin port: Copyright (c) 2026 Sydney Renee and The Solace Project.

### Acknowledgments

Thanks to the [`unicode-org/icu4x`](https://github.com/unicode-org/icu4x) maintainers and contributors for the original Rust implementation. This port reproduces their work in Kotlin Multiplatform; bug reports about upstream design or behavior should go to the upstream repository.
