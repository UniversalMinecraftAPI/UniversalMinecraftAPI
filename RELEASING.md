# Releasing

1. Change the version number in `build.gradle`
1. Build all platforms using `gradlew clean dist`
1. Load the plugin into each platform and generate the documentation using `/uma cad <platform>.json`
1. Copy each `<platform>.json` into `docs/platforms`
1. Make sure there are no warnings when executing `gradlew generateDoc`
1. Commit to make sure all platforms JSON files are in the release
1. Tag the release via Git: `git tag -a v1.4 -m "v1.4"`
1. Generate the new docs: `gradlew generateDoc -PdocBaseDir=/v1.4/`
1. Upload the docs from `build/docs/universalminecraftapi` to `uma.koenv.com`
1. Add the version to `uma.koenv.com/index.html`
1. Update the version to the new `SNAPSHOT` and commit