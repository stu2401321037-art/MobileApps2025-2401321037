
# NumisTracker

A native **offline-first numismatic collection manager** for Android, developed in Java. NumisTracker allows users to manage personal coin and banknote collections, record item details, capture photographs and store information locally using SQLite.

> **Project status:** The application builds and runs successfully. CRUD operations, SQLite persistence, Material Design 3 theming, light/dark mode and camera functionality have been implemented and manually tested.

> **Offline-first:** NumisTracker does not require an account, internet connection or remote server for its core features.

## 1. Idea

Numismatic collectors often need a convenient way to organize their coins and banknotes, preserve details about individual items and document their collections with photographs.

NumisTracker provides an Android application that combines these activities in one place. Users can maintain a personal catalog, manage item details, attach images and access their collection without an internet connection.

The main goals are simplicity, reliable local storage, intuitive navigation and consistent visual design.

## 2. How it works

NumisTracker opens with a main screen providing access to Coins and Banknotes. Each section displays saved items with their details and photographs.

Users can add records using the "+" button, edit existing items or delete them after confirmation. Each record contains a country, year, denomination, currency, condition, quantity and estimated value. The item name is generated automatically.

The application supports complete Create, Read, Update and Delete (CRUD) operations.

Users can capture photographs through Android Camera Intent. Images are stored in private application storage and linked to the corresponding SQLite records.

Collection data remains available after restarting the application. The interface uses Material Design 3 with light and dark themes. Theme preferences are saved using SharedPreferences.

No account or internet connection is required.

## 3. Technologies / Architecture

| Part | Technology |
|---|---|
| Language | Java 11 |
| Development environment | Android Studio |
| SDK | `minSdk=24`, `compileSdk=37`, `targetSdk=37` |
| UI | XML layouts + Material Design 3 |
| Navigation | Activities + Intent |
| Architecture | Activity-based architecture with SQLite data access |
| Persistence | SQLite |
| Database management | SQLiteOpenHelper |
| Background operations | ExecutorService |
| Theme persistence | SharedPreferences |
| Camera | ActivityResultContracts.TakePicture |
| Image access | Android FileProvider |
| Build system | Gradle |
| Version control | Git + GitHub |

### Project structure

```text
app/
└── src/
    └── main/
        ├── java/
        │   └── com/example/numistracker/
        │       ├── MainScreen.java
        │       ├── ItemsListActivity.java
        │       ├── CoinsActivity.java
        │       ├── banknotes.java
        │       ├── PhotoCaptureHelper.java
        │       └── data/
        │           └── DatabaseHelper.java
        │
        ├── res/
        │   ├── layout/
        │   ├── drawable/
        │   ├── values/
        │   ├── values-night/
        │   └── xml/
        │
        └── AndroidManifest.xml
```

### Main components

| Component | Responsibility |
|---|---|
| MainScreen | Main navigation and theme selection |
| ItemsListActivity | Displays coins and banknotes with Edit/Delete actions |
| CoinsActivity | Coin creation, editing and validation |
| banknotes | Banknote creation, editing and validation |
| PhotoCaptureHelper | Camera integration and image handling |
| DatabaseHelper | SQLite schema and CRUD operations |

### Architecture

The application currently uses an Activity-based architecture.

Activities handle user interaction, navigation and presentation logic. DatabaseHelper provides the SQLite persistence layer, while ExecutorService performs database operations on background threads.

A Repository layer is being introduced to separate data access from presentation logic.

Full MVVM and Dependency Injection are planned improvements and are not currently claimed as completed functionality.

## 4. User flow

1. Launch NumisTracker.
2. Select Coins or Banknotes from the main screen.
3. View the existing collection.
4. Press "+" to create a new record.
5. Enter the item's country, year, denomination, currency, condition, quantity and estimated value.
6. Optionally capture photographs using the device camera.
7. Press Save Coin or Save Banknote.
8. Return to the collection list and view the saved item.
9. Use Edit to update its information.
10. Use Delete to remove an item after confirmation.
11. Switch between light and dark themes from the main screen.

All saved records remain available after restarting the application.

## 5. How to run

### Requirements

- Android Studio
- Android SDK 37
- Java 11 language compatibility
- Android emulator or physical Android device
- Android 7.0 or newer (API 24+)

### Installation

1. Clone the repository:

   ```bash
   git clone https://github.com/RadoR7/MobileApps2026-2401321026.git
   ```

2. Switch to the development branch:

   ```bash
   git checkout develop
   ```

3. Open the NumisTracker project directory in Android Studio.

4. Wait for Gradle synchronization to complete.

5. Select an Android emulator or connect a physical device.

6. Click **Run** to build and launch the application.

### Building

Build the project using Android Studio:

`Build -> Make Project`

Generate a debug APK:

`Build -> Build APK(s)`

The debug APK is normally generated at:

`app/build/outputs/apk/debug/app-debug.apk`

An internet connection may be necessary during the initial Gradle dependency setup. Normal application usage does not require internet access.

## 6. Database and CRUD

NumisTracker uses a local SQLite database:

`numistracker.db`

DatabaseHelper extends SQLiteOpenHelper and manages the database schema, relationships and operations.

### Database tables

| Table | Description |
|---|---|
| coins | Stores collected coin information |
| banknotes | Stores collected banknote information |
| coin_images | Stores image references associated with coins |
| banknote_images | Stores image references associated with banknotes |

### CRUD functionality

| Operation | Coins | Banknotes |
|---|---|---|
| Create | Add a new coin | Add a new banknote |
| Read | View saved coins | View saved banknotes |
| Update | Edit an existing coin | Edit an existing banknote |
| Delete | Delete a coin | Delete a banknote |

### Data validation

Before saving a record, the application validates user input:

- Required fields must not be empty.
- Year must be a positive integer.
- Denomination must be a positive number.
- Quantity must be at least 1.
- Estimated value cannot be negative.
- A valid condition must be selected.

### Image storage

Photographs are stored in the application's private file directory.

The SQLite image tables store references linking image files to their corresponding coins or banknotes.

### Persistence

All records remain stored locally after the application is closed and restarted.

The application does not depend on an external database or cloud service.

## 7. Tests / Quality

### Manual tests

| Test | Expected result |
|---|---|
| Application launch | Main screen opens successfully |
| Coins navigation | Coins list opens |
| Banknotes navigation | Banknotes list opens |
| Create coin | New coin appears in the list |
| Create banknote | New banknote appears in the list |
| Edit coin | Existing coin is updated |
| Edit banknote | Existing banknote is updated |
| Delete coin | Coin is deleted after confirmation |
| Delete banknote | Banknote is deleted after confirmation |
| Application restart | Saved records remain available |
| Theme switching | Light/dark mode changes correctly |
| Theme persistence | Selected theme remains after restart |
| Camera launch | Device camera opens |
| Photo capture | Captured image is previewed |
| Image persistence | Saved image is associated with the item |
| Image display | Image appears in the collection list |

### Automated tests

The project includes dependencies for JUnit and AndroidX Espresso.

Automated unit test coverage and UI test execution have not yet been verified.

Planned quality improvements:

- JUnit tests for input validation.
- Unit tests for data processing.
- Repository tests.
- At least one Espresso UI test.
- Measurement of at least 15% unit test coverage.

Manual tests are not a substitute for automated test coverage.

## 8. Test accounts

NumisTracker does not implement user authentication.

No test accounts, usernames or passwords are required.

All available functionality can be accessed immediately after installation.

## 9. APK

**Required submission path:** `/apk/app-release.apk`

### Download

[Download NumisTracker APK](apk/app-release.apk)

The download link becomes available after the signed release APK is generated and uploaded to the repository.

### Generate a signed release APK

1. Open the project in Android Studio.
2. Select **Build -> Generate Signed App Bundle or APK**.
3. Choose **APK**.
4. Select or create a signing key.
5. Select the release build variant.
6. Complete the build process.
7. Locate the generated APK.
8. Copy it into the repository's `/apk` directory.
9. Rename it to `app-release.apk` if necessary.
10. Commit and push the APK to GitHub.

Do not commit private keystores or signing passwords.

**Submission status:** The APK must be generated, installed and verified before final submission.
---
