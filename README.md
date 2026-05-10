# Weather Widget for Android (Realme GT 6)

A beautiful weather widget for Android that displays real-time weather information from AccuWeather API.

## Features

- 🌤️ Real-time weather updates from AccuWeather
- 📍 Location search and selection
- 🌡️ Temperature and RealFeel temperature display
- 💨 Wind speed and humidity information
- 🔄 Automatic weather updates every 30 minutes
- 📱 Optimized for Realme GT 6 and other Android devices (Android 8.0+)
- 🎨 Modern Material Design 3 UI

## Architecture

### Project Structure
```
WeatherWidget/
├── data/
│   ├── api/
│   │   └── AccuWeatherService.kt      # Retrofit API interface
│   ├── model/
│   │   └── WeatherModels.kt            # Data models for API responses
│   └── repository/
│       └── WeatherRepository.kt         # Data access layer
├── widget/
│   ├── WeatherWidgetProvider.kt        # Widget lifecycle management
│   └── WeatherUpdateWorker.kt          # Background update worker
├── ui/
│   ├── MainActivity.kt                 # Main application activity
│   └── WidgetConfigActivity.kt         # Widget configuration screen
└── res/
    ├── layout/
    │   └── widget_layout.xml           # Widget UI layout
    ├── drawable/
    │   ├── widget_background.xml       # Widget background shape
    │   ├── ic_weather_sunny.xml        # Weather icons
    │   └── ic_dropdown.xml             # Dropdown indicator
    └── values/
        └── colors.xml                  # Color definitions
```

## Getting Started

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK 26+ (API Level 26)
- Kotlin 1.8+
- AccuWeather API Key

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/pravinl3893/WeatherWidget.git
   cd WeatherWidget
   ```

2. **Get AccuWeather API Key**
   - Visit [AccuWeather Developer Portal](https://developer.accuweather.com/)
   - Sign up for a free account
   - Create a new app to get your API key

3. **Configure API Key**
   - Open `src/main/java/com/weatherwidget/app/data/repository/WeatherRepository.kt`
   - Replace `YOUR_ACCUWEATHER_API_KEY` with your actual API key:
   ```kotlin
   private const val ACCUWEATHER_API_KEY = "your_api_key_here"
   ```

4. **Build and Run**
   ```bash
   ./gradlew build
   ./gradlew installDebug
   ```

## Usage

### Adding the Widget to Home Screen

1. Open your device home screen
2. Long press to open widget menu
3. Search for "Weather Widget"
4. Select the widget size (2x2 recommended)
5. Tap on the widget to configure location
6. Search and select your desired location
7. Widget will automatically update weather every 30 minutes

### Widget Configuration

The widget automatically opens the location selector when:
- First added to the home screen
- Tapped on the location area
- User manually updates settings

## API Reference

### AccuWeather Endpoints Used

#### 1. Location Search
```
GET /locations/v1/cities/autocomplete
Parameters:
  - apikey: Your API key
  - q: Search query (city name)
```

#### 2. Current Weather
```
GET /currentconditions/v1/{locationKey}
Parameters:
  - apikey: Your API key
  - details: true (to get additional weather details)
```

## Dependencies

### Core Android
- `androidx.core:core-ktx:1.12.0`
- `androidx.appcompat:appcompat:1.6.1`

### Networking
- `com.squareup.retrofit2:retrofit:2.10.0`
- `com.squareup.retrofit2:converter-gson:2.10.0`
- `com.squareup.okhttp3:okhttp:4.11.0`

### Jetpack Components
- `androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2`
- `androidx.lifecycle:lifecycle-livedata-ktx:2.6.2`
- `androidx.compose.*` (for Compose UI)

### Background Processing
- `androidx.work:work-runtime-ktx:2.8.1` (for periodic updates)

### Image Loading
- `com.github.bumptech.glide:glide:4.15.1`

## Widget Specifications

### Size
- **Minimum**: 280dp × 110dp (2×1)
- **Recommended**: 280dp × 280dp (2×2)
- **Resizable**: Yes (both horizontal and vertical)

### Update Interval
- Default: 30 minutes
- Configurable via WorkManager

### Permissions Required
- `INTERNET` - For API calls
- `ACCESS_FINE_LOCATION` - Optional, for location-based weather
- `ACCESS_COARSE_LOCATION` - Optional, for location-based weather

## Device Compatibility

- **Minimum Android Version**: Android 8.0 (API 26)
- **Target Android Version**: Android 14 (API 34)
- **Optimized for**: Realme GT 6 (1440×3168 resolution, Android 13+)

## Screenshot

The widget displays:
- **Weather Icon**: Large icon showing current conditions
- **Temperature**: Current temperature in °C or °F
- **RealFeel Temperature**: AccuWeather's RealFeel metric
- **Location Name**: Selected location
- **Condition**: Weather condition description
- **Dropdown**: Indicator for additional options

## Error Handling

The app handles various error scenarios:
- Network failures: Shows "--°" placeholder
- Invalid API key: Displays error notification
- Location not found: Shows search error message
- No location selected: Prompts user to configure

## Building APK for Realme GT 6

```bash
# Create release build
./gradlew bundleRelease

# For APK
./gradlew assembleRelease
```

The resulting APK can be installed directly on Realme GT 6 devices.

## Troubleshooting

### Widget not updating
1. Check if location is selected
2. Verify internet connection
3. Ensure API key is valid
4. Check WorkManager configuration

### API errors
1. Verify AccuWeather API key
2. Check API rate limits
3. Ensure API plan supports required endpoints

### Location search not working
1. Verify internet connectivity
2. Check API response format
3. Enable verbose logging

## Contributing

Contributions are welcome! Please feel free to submit pull requests or open issues.

## License

This project is licensed under the MIT License - see LICENSE file for details.

## Support

For issues and questions:
- Open GitHub Issues: [Issues](https://github.com/pravinl3893/WeatherWidget/issues)
- Check documentation
- Review example implementations

## Acknowledgments

- [AccuWeather API](https://developer.accuweather.com/) - Weather data provider
- [Retrofit](https://square.github.io/retrofit/) - HTTP client
- [Jetpack Compose](https://developer.android.com/jetpack/compose) - Modern Android UI
- [WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager) - Background processing

---

**Made with ❤️ for Weather Enthusiasts**
