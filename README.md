# BirdZone

BirdZone is an Android application designed for bird enthusiasts to discover bird hotspots, record observations, and manage their birding activities.

## Features

- **Bird Hotspot Map**: View bird hotspots on an interactive satellite map powered by Google Maps and the eBird API
- **Bird Observations**: Record bird sightings with species, location, date, time, and notes
- **Photo Capture**: Take photos of birds directly from the app and save them to your gallery
- **Location Services**: Automatically capture GPS coordinates for your observations
- **Saved Observations**: View and manage your saved bird observations
- **User Authentication**: Secure login and registration powered by Firebase

## Requirements

- Minimum SDK: Android 29 (Android 10)
- Target SDK: Android 34 (Android 14)
- Google Maps API key
- Firebase project for authentication and Firestore

## Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/Siya-S10-ai/BirdZone.git
   ```

2. Open the project in Android Studio

3. Configure your Google Maps API key in `local.properties`:
   ```properties
   MAPS_API_KEY=your_google_maps_api_key
   ```

4. Set up Firebase:
   - Create a Firebase project
   - Add your Android app to the Firebase project
   - Download `google-services.json` and place it in the `app/` directory
   - Enable Authentication and Firestore in the Firebase console

5. Build and run the project

## Tech Stack

- **Language**: Kotlin
- **UI**: Android Views with ViewBinding
- **Maps**: Google Maps SDK
- **Location**: Google Play Services Location
- **Backend**: Firebase (Authentication, Firestore, Storage)
- **Networking**: OkHttp
- **Bird Data**: eBird API

## Project Structure

```
app/src/main/java/com/example/birdzone/
├── MainActivity.kt          # Splash screen
├── Login.kt                  # User login
├── Register.kt               # User registration
├── MapActivity.kt            # Bird hotspot map
├── ObservationsActivity.kt   # Record new observations
├── SavedObservationsActivity.kt # View saved observations
├── SettingsActivity.kt       # App settings
├── Model/
│   ├── Observation.kt        # Observation data model
│   └── User.kt               # User data model
└── adapter/                  # RecyclerView adapters
```

## Permissions

The app requires the following permissions:
- Camera - for taking bird photos
- Location - for GPS coordinates of observations
- Internet - for map data and API calls
- Storage - for saving photos (Android 12 and below)

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Author

Copyright (c) 2025 Siyabonga Nhlapo
