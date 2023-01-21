jackson-module-kogera
====
`jackson-module-kogera` is an experimental project to develop `jackson-module-kotlin`.  
This project has the following features compared to `jackson-module-kotlin`.

- Lightweight
- high-performance
  - Fast deserialization
  - Smaller memory consumption
- More `Kotlin` friendly behavior

This project is experimental, but passes all the tests implemented in `jackson-module-kotlin` except for the intentional incompatibility.

# Features of `jackson-module-kogera`
The main feature of `jackson-module-kogera` is that it replaces `kotlin-reflect` with `kotlinx.metadata.jvm`.  
As of `1.7.21`, `kotlin-reflect` is a huge library(3MB), and replacing it with `kotlinx.metadata.jvm`(1MB) makes it lightweight.

Several performance improvements have also been made.
First, by implementing the equivalent of https://github.com/FasterXML/jackson-module-kotlin/pull/439, deserialization is now up to three times faster, depending on the use case.  
The cache has also been reorganized based on [benchmark results](https://github.com/ProjectMapK/kogera-benchmark) to achieve smaller memory consumption.  
The performance degradation when the `strictNullChecks` option is enabled is also [greatly reduced](https://github.com/ProjectMapK/jackson-module-kogera/pull/44).

In addition, several property-related behaviors have been improved.

[Here](./docs/FixedIssues.md) is a list of issues that are not resolved in `jackson-module-kotlin` but are or will be resolved in `kogera`.

## About intentional destructive changes
This project makes several disruptive changes to achieve more `Kotlin-like` behavior.  
In particular, the following points should be noted

- Non `Kotlin` properties are `Ignore` during serialization/deserialization.
  - i.e. functions with names like `getFoo` are not serialized.
- Property names in `Kotlin` are used during serialization/deserialization.
  - i.e. no manipulation of serialization results using `JvmName`.
- Old options and codes have been removed and some codes have been made private.

# Compatibility
- `Java 8+`
- `jackson 2.14.1`
- `Kotlin 1.7.21+`(This version will be smaller than `1.6.x.`)

# Installation
The package is temporarily published in `JitPack`.  
Please refer to `jitpack.io` for the released version.

[ProjectMapK / jackson\-module\-kogera](https://jitpack.io/#ProjectMapK/jackson-module-kogera)

```kotlin
repositories {
    // ...

    maven { setUrl("https://jitpack.io") }
}

dependencies {
  // ...

  implementation("com.github.ProjectMapK:jackson-module-kogera:${version}")
}
```

## Migration in existing projects
**The following descriptions are for the `alpha` version.**  
**I plan to change the package and module names when I move to the `beta` version(see https://github.com/FasterXML/jackson-module-kotlin/issues/450#issuecomment-1384788717).**

When replacing `jackson-module-kotlin` in an existing project, please replace the dependencies of `jackson-module-kotlin` with `jackson-module-kogera`.  
Since the package/module name of `jackson-module-kogera` is the same as that of `jackson-module-kotlin`, it is basically possible to migrate by simply replacing dependencies (although there is a possibility of compile errors due to some destructive changes).

`. /gradlew dependencies` and if `jackson-module-kotlin` does not appear, you have successfully migrated.  
If `jackson-module-kotlin` is still there, please `exclude` it from the dependencies.  
At least for the `Spring` project at hand, I have confirmed that this method works fine up to the `Jackson2ObjectMapperBuilder` auto-configuration.

If you find any problems, it would be appreciated if you could share them in an `issue`.

# About the future
Currently this project is in `alpha`.  
After the following features are implemented, this project will be moved to the `beta` version if there is enough demand for it.

- Deserialization support for `value class
  - Partial support is achieved at [#40](https://github.com/ProjectMapK/jackson-module-kogera/pull/40).
- Support for less than `Kotlin 1.6.x`(including grid test building with `CI`)
- Rename module and package(see https://github.com/FasterXML/jackson-module-kotlin/issues/450#issuecomment-1384788717).

# About license
This project is based on `jackson-module-kotlin`, so the license follows `jackson-module-kotlin`.  
The current license is `Apache License 2.0`.

[jackson\-module\-kotlin/LICENSE at 2\.14 · FasterXML/jackson\-module\-kotlin](https://github.com/FasterXML/jackson-module-kotlin/blob/2.14/LICENSE)

# About `Kogera`
`Kogera` is the Japanese name for `Japanese pygmy woodpecker`.  
This bird is the smallest woodpecker in Japan.  
