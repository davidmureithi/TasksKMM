# KMM Task List App

This is a Kotlin Multiplatform Mobile (KMM) application that runs on both Android and iOS platforms.

## Setup Instructions

### Prerequisites

- Android Studio Arctic Fox or later
- Xcode 13.0 or later (for iOS development)
- JDK 11 or later
- Kotlin Multiplatform Mobile plugin installed in Android Studio
- CocoaPods (for iOS dependencies)

### Clone the Project

```bash
git clone 
cd TestKMM
```

### Android Setup

1. Open the project in Android Studio
2. Wait for the Gradle sync to complete
3. Run the app using the Android configuration:
   - Select 'composeApp' configuration
   - Choose your Android device/emulator
   - Click 'Run'

### iOS Setup

1. Install CocoaPods if you haven't already:
```bash
sudo gem install cocoapods
```

2. Navigate to the iOS project directory:
```bash
cd iosMain
```

3. Install Pod dependencies:
```bash
pod install
```

4. Open the `.xcworkspace` file in Xcode:
```bash
open iosApp.xcworkspace
```

5. Build and run the project in Xcode:
   - Select your target device/simulator
   - Click the Run button or press ⌘R

## Project Structure

- `/composeApp` - Contains the shared Kotlin code and Android-specific code
- `/iosApp` - Contains iOS-specific code
- Shared code includes:
  - Data models
  - Business logic
  - Database operations (SQLDelight)
  - UI components (Compose Multiplatform)

## Development

### Android Development
- Use Android Studio for development
- Run configurations are already set up
- Hot reload is supported for Compose UI

### iOS Development
- Use Xcode for iOS-specific development
- Kotlin changes need to be rebuilt
- iOS UI changes support live preview

## Troubleshooting

If you encounter any issues:

1. Clean and Rebuild the project:
   - In Android Studio: Build > Clean Project
   - In Xcode: Product > Clean Build Folder

2. Verify Gradle sync:
   - File > Sync Project with Gradle Files

3. For iOS issues:
   - Delete the `Pods` folder
   - Run `pod install` again
   - Clean and rebuild the project

## Dependencies

The project uses:
- Kotlin Multiplatform Mobile
- Compose Multiplatform for UI
- SQLDelight for database
- Koin for dependency injection
- Kotlin Coroutines for async operations
