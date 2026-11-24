# NumisTracker

**NumisTracker** is an offline-first Android application for managing personal coin and banknote collections. Developed in Java, the application allows users to organize their collections, manage item details, capture photographs, and store information locally using SQLite.

The application uses an MVVM-based architecture with a Repository layer and manual Dependency Injection.

## 1. Idea

Numismatic collectors need a convenient way to organize their coins and banknotes, preserve identifying information, and document their collections with photographs.

NumisTracker provides a simple Android application that combines these activities in one place.

The main goals of the project are:

- Easy management of personal coin and banknote collections.
- Reliable local data storage.
- Full CRUD functionality.
- Camera integration for photographing collectibles.
- Intuitive navigation and a modern user interface.
- Offline access without user registration.

## 2. How it works

NumisTracker opens with a main screen providing access to Coins and Banknotes. Each section displays saved items and their photographs.

Users can add new items using the "+" button, edit existing records, or delete them after confirmation. Each item includes a country, year, denomination, currency, condition, quantity, and optional estimated value.

The application supports complete Create, Read, Update, and Delete operations.

Users can capture photographs using the device camera. Images are stored in private application storage and linked to SQLite records.

Collection data persists after restarting the application. The interface supports light and dark themes, and the selected theme is remembered using SharedPreferences.

No account or internet connection is required for normal application usage.

## 3. Technologies and Architecture

| Component | Technology |
|---|---|
| Platform | Android |
| Programming language | Java 11 |
| Development environment | Android Studio |
| Minimum SDK | API 24 (Android 7.0) |
| Compile SDK | API 37 |
| Target SDK | API 37 |
| UI | XML layouts and Material Design 3 |
| Navigation | Activities and Intents |
| Architecture | MVVM |
| Data access | Repository Pattern |
| Dependency Injection | Manual DI with AppContainer |
| State management | ViewModel and LiveData |
| Database | SQLite |
| Database management | SQLiteOpenHelper |
| Background operations | Executor-based asynchronous operations |
| Camera | ActivityResultContracts.TakePicture |
| Image access | FileProvider |
| Theme preferences | SharedPreferences |
| Unit testing | JUnit and Mockito |
| UI testing | Espresso |
| Build system | Gradle |
| Version control | Git and GitHub |

### Architecture Overview

The application follows an MVVM-based architecture with separation between the presentation, business logic, and data access layers.

```text
             Activities + XML Views
                       |
                       v
             ViewModel Layer
        +------------------------+
        | ItemsViewModel         |
        | FormViewModel          |
        +------------------------+
                       |
                       v
             Repository Layer
                NumisRepository
                       |
                       v
              DatabaseHelper
                       |
                       v
                SQLite Database
                  numistracker.db
```

### Dependency Injection

The application uses manual Dependency Injection through `AppContainer`.

`AppContainer` provides shared instances of `DatabaseHelper` and `NumisRepository`.

This reduces direct dependencies between UI components and the database layer.

### Main Components

| Component | Responsibility |
|---|---|
| MainScreen | Main navigation and theme selection |
| ItemsListActivity | Displays coins and banknotes with Edit/Delete actions |
| CoinsActivity | Coin creation, editing, and input validation |
| banknotes | Banknote creation, editing, and input validation |
| ItemsViewModel | Collection loading, deletion, and UI state |
| FormViewModel | Form data loading, insertion, updating, and saving |
| NumisRepository | Data access and database operations |
| DatabaseHelper | SQLite schema and CRUD implementation |
| AppContainer | Manual Dependency Injection |
| CollectionItem | Model representing collection items |
| PhotoCaptureHelper | Camera integration and image preview |

### Project Structure

```text
NumisTracker/
├── app/
│   └── src/
│       ├── main/
│       │   ├── java/
│       │   │   └── com/example/numistracker/
│       │   │       ├── MainScreen.java
│       │   │       ├── ItemsListActivity.java
│       │   │       ├── CoinsActivity.java
│       │   │       ├── banknotes.java
│       │   │       ├── PhotoCaptureHelper.java
│       │   │       ├── data/
│       │   │       │   ├── DatabaseHelper.java
│       │   │       │   └── NumisRepository.java
│       │   │       ├── di/
│       │   │       │   └── AppContainer.java
│       │   │       ├── model/
│       │   │       │   └── CollectionItem.java
│       │   │       └── viewmodel/
│       │   │           ├── ItemsViewModel.java
│       │   │           └── FormViewModel.java
│       │   ├── res/
│       │   └── AndroidManifest.xml
│       ├── test/
│       │   └── java/
│       │       └── com/example/numistracker/
│       │           └── viewmodel/
│       └── androidTest/
│           └── java/
│               └── com/example/numistracker/
│                   └── CollectionNavigationTest.java
├── apk/
│   └── app-release.apk
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

## 4. User Flow

1. Launch NumisTracker.
2. Select Coins or Banknotes from the main screen.
3. View the existing collection.
4. Press "+" to create a new record.
5. Enter the item's details:
   - Country
   - Year
   - Denomination
   - Currency
   - Condition
   - Quantity
   - Estimated value
6. Optionally capture a photograph using the device camera.
7. Press Save Coin or Save Banknote.
8. Return to the collection list and view the saved item.
9. Use Edit to update an existing record.
10. Use Delete to remove an item after confirmation.
11. Switch between light and dark themes from the main screen.

All saved records remain available after restarting the application.

## 5. How to Run

### Requirements

- Android Studio
- Android SDK 37
- Java 11 source compatibility
- Android emulator or physical Android device
- Android 7.0 (API 24) or newer

### Installation

1. Clone the GitHub repository:

```bash
git clone https://github.com/RadoR7/MobileApps2026-2401321026.git
```

2. Switch to the development branch:

```bash
cd MobileApps2026-2401321026
git checkout develop
```

3. Open the NumisTracker project directory in Android Studio.

4. Wait for Gradle synchronization to complete.

5. Select an Android emulator or connect a physical Android device.

6. Click Run to build and launch the application.

### Building

To compile the Debug version, execute:

```powershell
.\gradlew.bat :app:assembleDebug
```

The generated Debug APK is normally located at:

```text
app/build/outputs/apk/debug/app-debug.apk
```

The application works offline. Internet access may be required only for the initial Gradle dependency download.

## 6. Database and CRUD

NumisTracker uses a local SQLite database:

```text
numistracker.db
```

The database is managed through `DatabaseHelper` and accessed through `NumisRepository`.

### Database Tables

| Table | Description |
|---|---|
| coins | Stores coin information |
| banknotes | Stores banknote information |
| coin_images | Stores references to coin photographs |
| banknote_images | Stores references to banknote photographs |

### CRUD Functionality

| Operation | Coins | Banknotes |
|---|---|---|
| Create | Add a new coin | Add a new banknote |
| Read | View saved coins | View saved banknotes |
| Update | Edit an existing coin | Edit an existing banknote |
| Delete | Delete a coin | Delete a banknote |

### Data Validation

The application validates user input before saving records.

Validation rules include:

- Required fields cannot be empty.
- Year must be a positive integer.
- Denomination must be a positive number.
- Quantity must be at least 1.
- Estimated value cannot be negative.
- A condition must be selected.

### Image Storage

Photographs are captured using the Android camera.

Images are stored in the application's private file storage.

SQLite stores the file paths linking photographs to the corresponding coins or banknotes.

### Data Persistence

All collection records remain stored locally after closing and restarting the application.

No external database or remote server is required.

## 7. Tests and Quality

### Manual Testing

The following functionality has been manually verified:

| Test | Result |
|---|---|
| Application launch | Passed |
| Coins navigation | Passed |
| Banknotes navigation | Passed |
| Create coin | Passed |
| Create banknote | Passed |
| Edit coin | Passed |
| Edit banknote | Passed |
| Delete coin | Passed |
| Delete banknote | Passed |
| SQLite persistence | Passed |
| Theme switching | Passed |
| Theme persistence | Passed |
| Camera launch | Passed |
| Photo capture | Passed |
| Image storage | Passed |
| Image display | Passed |

### Unit Tests

The project includes automated Unit tests using JUnit and Mockito.

The tests verify operations involving the ViewModel and Repository layers, including:

- Loading collection data.
- Inserting records.
- Updating existing records.
- Deleting records.
- Handling unsuccessful operations.
- Handling data access errors.

The Unit tests have been executed successfully.

Run Unit tests using:

```powershell
.\gradlew.bat :app:testDebugUnitTest
```

Unit test results can be found at:

```text
app/build/reports/tests/testDebugUnitTest/index.html
```

### Espresso UI Tests

The project also includes automated Espresso UI tests.

`CollectionNavigationTest.java` checks:

- Navigation from the Coins list to the coin creation form.
- Navigation from the Banknotes list to the banknote creation form.

The Espresso tests have been executed successfully on an Android API 37 emulator.

Run UI tests using:

```powershell
.\gradlew.bat :app:connectedDebugAndroidTest
```

UI test results can be found at:

```text
app/build/reports/androidTests/connected/debug/index.html
```

### Code Coverage

Code coverage has been measured using Android Studio.

The last confirmed overall result was:

| Metric | Coverage |
|---|---|
| Class Coverage | 23% |
| Method Coverage | 10% |
| Line Coverage | 5% |
| Branch Coverage | 5% |

Additional tests have been prepared to improve coverage, but a newer overall coverage measurement has not yet been confirmed.

The project's course target is at least 15% automated test coverage.

## 8. Test Accounts

NumisTracker does not require authentication.

There is no login or registration system.

No test usernames or passwords are necessary.

All functionality is available immediately after installing the application.

## 9. Screenshots

Screenshots of the application can be added to the `screenshots/` directory.

Recommended screenshots include:

- Main screen
- Coins collection
- Banknotes collection
- Add/Edit coin form
- Add/Edit banknote form
- Camera and photo preview
- Light mode
- Dark mode

## 10. Release APK

A signed Release APK has been generated for NumisTracker.

### Download APK

**[Download NumisTracker APK](apk/app-release.apk)**

APK location in the repository:

```text
apk/app-release.apk
```

### Generate a Signed APK

1. Open NumisTracker in Android Studio.
2. Select Build → Generate Signed App Bundle or APK.
3. Choose APK.
4. Select or create a signing key.
5. Select the release build variant.
6. Complete the signing process.
7. Locate the generated APK.

### Security

The private signing key and passwords must not be uploaded to GitHub.

The keystore should be stored securely outside the repository.

## 11. GitHub Repository and Submission

**Repository:**

[MobileApps2026-2401321026](https://github.com/RadoR7/MobileApps2026-2401321026)

**Development branch:**

[develop](https://github.com/RadoR7/MobileApps2026-2401321026/tree/develop)

**APK path:**

```text
apk/app-release.apk
```

The submitted project includes:

- Android application source code
- XML layouts and application resources
- SQLite database implementation
- MVVM architecture
- Repository and Dependency Injection
- Camera functionality
- Unit tests
- Espresso UI tests
- Signed Release APK
- README documentation

## 12. Known Limitations and Future Improvements

NumisTracker currently focuses on offline collection management.

The application does not include cloud synchronization or user accounts.

Potential future improvements include:

- Collection search and filtering.
- Exporting and importing collections.
- Cloud backup and synchronization.
- Additional photo management features.
- More automated tests and improved code coverage.

## Conclusion

NumisTracker is a functional Android application for managing personal coin and banknote collections.

It combines SQLite persistence, complete CRUD operations, camera integration, Material Design 3, light/dark themes, and an MVVM-based architecture.

The project also includes automated JUnit and Espresso tests and a signed Release APK for installation.
