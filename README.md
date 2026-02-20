# Assignment Tracker

![Android CI](https://github.com/YOUR_USERNAME/assignment-tracker/workflows/Android%20CI/badge.svg)

A modern Android application for tracking academic assignments with deadline reminders, built with Jetpack Compose and Material 3.

## Features

### Core Functionality
- ✅ **Assignment Management** - Create, edit, and delete assignments with titles, descriptions, due dates, and times
- 📅 **Calendar View** - Visual calendar showing assignments by date with markers
- 📊 **Kanban Board** - Organize assignments by status (Not Started, In Progress, Done)
- 🔔 **Smart Notifications** - 24-hour advance reminders for upcoming deadlines
- 🔍 **Search & Filter** - Search assignments and filter by status and priority
- 📈 **Analytics** - Track completed assignments and view weekly completion trends

### User Experience
- 🎨 **Material 3 Design** - Modern UI with dynamic color theming
- 🌓 **Theme Support** - Light, Dark, and System theme modes
- 👤 **User Profiles** - Personalized experience with name, course, and academic year
- 🚀 **Onboarding** - Smooth first-time user experience
- ♿ **Accessibility** - Full TalkBack support with content descriptions

## Tech Stack

### Architecture
- **MVVM** - Model-View-ViewModel architecture pattern
- **Clean Architecture** - Separation of concerns with data, domain, and presentation layers
- **Repository Pattern** - Abstraction layer for data sources

### Libraries & Frameworks
- **Jetpack Compose** - Modern declarative UI toolkit
- **Material 3** - Latest Material Design components
- **Room Database** - Local data persistence
- **Hilt** - Dependency injection
- **Kotlin Coroutines & Flow** - Asynchronous programming
- **DataStore** - User preferences storage
- **WorkManager** - Background task scheduling for notifications
- **Navigation Compose** - Type-safe navigation

## Setup Instructions

### Prerequisites
- Android Studio Hedgehog or later
- JDK 17
- Android SDK with minimum API 24 (Android 7.0)

### Installation

1. Clone the repository
```bash
git clone https://github.com/YOUR_USERNAME/assignment-tracker.git
cd assignment-tracker
```

2. Open the project in Android Studio

3. Sync Gradle files
```bash
./gradlew build
```

4. Run the app on an emulator or physical device

### Building

Build debug APK:
```bash
./gradlew assembleDebug
```

Build release APK:
```bash
./gradlew assembleRelease
```

Run tests:
```bash
./gradlew testDebugUnitTest
```

## Project Structure

```
app/
├── data/
│   ├── local/          # Room database, DAOs, entities
│   ├── preferences/    # DataStore preferences
│   └── repository/     # Repository implementations
├── domain/
│   ├── model/          # Domain models
│   ├── repository/     # Repository interfaces
│   └── usecase/        # Business logic use cases
├── presentation/
│   ├── analytics/      # Analytics screen
│   ├── board/          # Kanban board view
│   ├── calendar/       # Calendar view
│   ├── components/     # Reusable UI components
│   ├── detail/         # Assignment detail/edit screen
│   ├── list/           # Assignment list screen
│   ├── navigation/     # Navigation graph
│   ├── onboarding/     # First-time user flow
│   ├── settings/       # Settings screen
│   └── theme/          # Material 3 theming
├── di/                 # Dependency injection modules
├── util/               # Utility classes
└── worker/             # Background workers
```

## Features in Detail

### Assignment Management
- Create assignments with title, description, due date, time, priority, and status
- Edit existing assignments
- Delete assignments with confirmation
- Automatic notification scheduling

### Views
- **List View** - Default view with search and filters
- **Calendar View** - Monthly calendar with assignment markers
- **Board View** - Kanban-style status columns
- **Analytics View** - Completion statistics and trends

### Notifications
- 24-hour advance reminders
- Tap notification to open assignment details
- Automatic rescheduling on assignment updates
- Test mode available for development

### Preferences
- User profile (name, course, academic year)
- Theme selection (Light/Dark/System)
- Persistent across app restarts

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- Built with Jetpack Compose
- Material Design 3 guidelines
- Android Architecture Components
