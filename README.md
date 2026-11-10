# Starlit Reader — Stage 1

This is a real, buildable Android Studio project — not a mockup. It is the
first of the 10 build stages from the original spec:

- Bookshelf screen (Jetpack Compose + Material 3)
- Import a PDF from your phone (Storage Access Framework, persisted access)
- Room database that remembers your books and reading position
- A working PDF reader (Android's built-in `PdfRenderer`) with tap-left /
  tap-right page turns

**Not in this build yet:** paper textures, the PDF paper-transformation
engine, 3D page curl, custom wallpapers/themes, music and ambient audio,
EPUB/TXT support. Those are stages 2–8 from the original plan and get added
once this pipeline is proven to build and run on your phone.

## Why this can't be compiled directly in chat

Turning Kotlin + Gradle + the Android SDK into an `.apk` needs the Android
SDK, Gradle, and the Kotlin compiler, plus network access to Google's and
Gradle's servers to download them. The sandbox this conversation runs code
in has none of that installed and can't reach those servers. GitHub's own
build servers have all of it, which is why the plan below routes the actual
compiling through GitHub Actions — it's not a workaround, it's genuinely how
this APK gets built.

## Getting a real .apk from this project

1. Create a new **empty** repository on github.com (no README, no
   .gitignore — this project already has one).
2. Upload every file in this folder to it, keeping the folder structure
   (the `.github/workflows/android-build.yml` file especially — GitHub
   looks for workflows there). The simplest way with no git experience:
   open the new repo page → "Add file" → "Upload files" → drag the whole
   unzipped folder in → commit directly to `main`.
3. Open the repo's **Actions** tab. A run called "Build Starlit Reader APK"
   starts automatically (or click "Run workflow" if it doesn't). It takes
   roughly 3–6 minutes.
4. When it finishes, open the run, scroll to **Artifacts**, and download
   `starlit-reader-debug-apk`. Unzip it — that's your `.apk`.
5. Copy the `.apk` to your phone (USB, Google Drive, email to yourself —
   whatever's easiest), tap it, allow "install unknown apps" for that
   source when asked, and install.

This produces a **debug-signed** APK. It installs and runs exactly like a
release build — debug signing only matters if you later publish to the
Play Store, which needs its own signing key (that's stage 10 in the
original plan, and worth doing once the app is further along).

## If the first build fails

I wrote all of this without being able to compile it myself, so a
dependency-version mismatch on the first run is realistically possible —
Gradle, Android Gradle Plugin, Kotlin, and Compose version numbers move
fast and I can't check the live registry from here. If Actions shows a red
X, open the failed step and paste me the error text. These are normally a
one-line version-number fix.
