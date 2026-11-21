# LINKO Android Starter

This is a starter Android project with a dark LINKO interface and a GitHub Actions workflow that builds a debug APK.

## Upload to GitHub
1. Extract this ZIP on your phone or computer.
2. Upload **all contents inside the extracted folder** to the root of your repository.
3. Make sure the hidden `.github/workflows/android.yml` file is included.
4. Commit the changes to the `main` branch.

## Download the APK
Open the repository's **Actions** tab, open the latest successful **Build LINKO APK** run, then download the `LINKO-debug-APK` artifact.

Note: This starter provides the app shell and build pipeline. Nearby discovery, QR generation/pairing, encrypted messaging, and media transfer still need implementation and device testing; this ZIP is not itself a finished offline messenger APK.
