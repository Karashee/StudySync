# Requirements Document

## Introduction

The Assignment App Tracker is an offline-first Android application designed to help students manage their academic assignments effectively. The system provides multiple views (list, calendar, board), deadline reminders, search and filtering capabilities, and basic analytics to track assignment completion. The application follows modern Android development practices using Kotlin, Jetpack Compose, MVVM architecture, and Room database for local storage.

## Glossary

- **Assignment_Tracker_System**: The complete Android application including UI, business logic, and data persistence layers
- **Assignment_Entity**: A data model representing a student assignment with properties like title, description, due date, priority, and status
- **Room_Database**: The local SQLite database abstraction layer used for offline data persistence
- **Assignment_Repository**: The data access layer that provides a clean API for data operations
- **List_View**: A scrollable view displaying all assignments with search and filter capabilities
- **Calendar_View**: A monthly calendar grid showing assignments on their respective due dates
- **Board_View**: A Kanban-style board with three columns representing assignment status (Not Started, In Progress, Done)
- **Reminder_Worker**: A background worker that schedules and triggers notifications 24 hours before assignment deadlines
- **DataStore_Preferences**: A key-value storage mechanism for user preferences and settings
- **Navigation_Component**: The Jetpack Navigation library managing screen transitions and navigation flow
- **ViewModel**: A lifecycle-aware component that holds and manages UI-related data
- **Use_Case**: A single-responsibility class that encapsulates a specific business operation

## Requirements

### Requirement 1

**User Story:** As a student, I want to create and edit assignments with detailed information, so that I can track all my academic work in one place

#### Acceptance Criteria

1. WHEN the user taps the add assignment button, THE Assignment_Tracker_System SHALL display a form with fields for title, description, due date, due time, priority, and status
2. WHEN the user submits a valid assignment form, THE Assignment_Tracker_System SHALL persist the assignment to the Room_Database within 500 milliseconds
3. WHEN the user taps an existing assignment, THE Assignment_Tracker_System SHALL display the assignment details in an editable form pre-filled with current values
4. WHEN the user updates an assignment and saves, THE Assignment_Tracker_System SHALL update the assignment in the Room_Database and reflect changes across all views
5. IF the user attempts to save an assignment without a title, THEN THE Assignment_Tracker_System SHALL display a validation error message and prevent submission

### Requirement 2

**User Story:** As a student, I want to view my assignments in a list with search and filter options, so that I can quickly find specific assignments

#### Acceptance Criteria

1. THE Assignment_Tracker_System SHALL display all assignments in a scrollable list ordered by due date ascending
2. WHEN the user enters text in the search field, THE Assignment_Tracker_System SHALL filter assignments where the title or description contains the search text (case-insensitive)
3. WHEN the user selects a priority filter, THE Assignment_Tracker_System SHALL display only assignments matching the selected priority level
4. WHEN the user selects a status filter, THE Assignment_Tracker_System SHALL display only assignments matching the selected status
5. WHEN the user applies multiple filters simultaneously, THE Assignment_Tracker_System SHALL display only assignments matching all active filter criteria

### Requirement 3

**User Story:** As a student, I want to view my assignments on a calendar, so that I can see my workload distribution across the month

#### Acceptance Criteria

1. THE Assignment_Tracker_System SHALL display a monthly calendar grid showing the current month with all days visible
2. WHEN an assignment has a due date falling on a calendar day, THE Assignment_Tracker_System SHALL display a visual marker on that day
3. WHEN the user taps a calendar day with assignments, THE Assignment_Tracker_System SHALL display a list of assignments due on that date
4. WHEN the user navigates to the previous or next month, THE Assignment_Tracker_System SHALL update the calendar grid to show the selected month within 200 milliseconds
5. THE Assignment_Tracker_System SHALL highlight the current day with a distinct visual indicator

### Requirement 4

**User Story:** As a student, I want to view my assignments in a Kanban board layout, so that I can visualize my workflow and progress

#### Acceptance Criteria

1. THE Assignment_Tracker_System SHALL display three columns labeled "Not Started", "In Progress", and "Done"
2. THE Assignment_Tracker_System SHALL place each assignment in the column corresponding to its current status
3. WHEN the user taps an assignment card in the Board_View, THE Assignment_Tracker_System SHALL move the assignment to the next status column and update the Room_Database
4. WHEN an assignment status changes in the Board_View, THE Assignment_Tracker_System SHALL reflect the updated status in the List_View and Calendar_View immediately
5. THE Assignment_Tracker_System SHALL display assignment cards with title, due date, and priority indicator

### Requirement 5

**User Story:** As a student, I want to receive notifications 24 hours before assignment deadlines, so that I have adequate time to complete my work

#### Acceptance Criteria

1. WHEN the user creates or updates an assignment with a due date, THE Assignment_Tracker_System SHALL schedule a notification to trigger 24 hours before the due date and time
2. WHEN the scheduled time arrives, THE Reminder_Worker SHALL display a system notification with the assignment title and due date
3. WHEN the user taps the notification, THE Assignment_Tracker_System SHALL open the application and navigate to the assignment detail screen
4. WHEN the user deletes an assignment, THE Assignment_Tracker_System SHALL cancel any scheduled notifications for that assignment
5. WHERE test mode is enabled, THE Assignment_Tracker_System SHALL schedule notifications 15 seconds in the future instead of 24 hours

### Requirement 6

**User Story:** As a student, I want all my data stored locally on my device, so that I can access my assignments without an internet connection

#### Acceptance Criteria

1. THE Assignment_Tracker_System SHALL store all assignment data in the Room_Database on the device
2. THE Assignment_Tracker_System SHALL perform all create, read, update, and delete operations against the local Room_Database
3. WHEN the device has no internet connectivity, THE Assignment_Tracker_System SHALL continue to function with full feature availability
4. THE Assignment_Tracker_System SHALL persist all data changes immediately to the Room_Database without requiring network synchronization
5. WHEN the application restarts, THE Assignment_Tracker_System SHALL load all assignments from the Room_Database within 1 second

### Requirement 7

**User Story:** As a student, I want to see analytics about my assignment completion, so that I can track my productivity and identify overdue work

#### Acceptance Criteria

1. THE Assignment_Tracker_System SHALL calculate and display the count of assignments completed in the current week
2. THE Assignment_Tracker_System SHALL calculate and display the count of assignments that are overdue (due date in the past and status not "Done")
3. THE Assignment_Tracker_System SHALL display a simple bar chart showing completed assignments per week for the last 4 weeks
4. WHEN the user marks an assignment as done, THE Assignment_Tracker_System SHALL update the analytics display within 500 milliseconds
5. THE Assignment_Tracker_System SHALL recalculate analytics data whenever the user navigates to the analytics screen

### Requirement 8

**User Story:** As a student, I want to customize my profile and preferences, so that I can personalize the application to my needs

#### Acceptance Criteria

1. THE Assignment_Tracker_System SHALL provide a settings screen where the user can enter their name, course, and academic year
2. WHEN the user updates preferences, THE Assignment_Tracker_System SHALL persist the values to DataStore_Preferences within 300 milliseconds
3. WHEN the user first launches the application, THE Assignment_Tracker_System SHALL display an onboarding flow to collect profile information
4. THE Assignment_Tracker_System SHALL load user preferences from DataStore_Preferences on application startup
5. THE Assignment_Tracker_System SHALL display the user's name on the main screen when available

### Requirement 9

**User Story:** As a student, I want to switch between light and dark themes, so that I can use the app comfortably in different lighting conditions

#### Acceptance Criteria

1. THE Assignment_Tracker_System SHALL support both light and dark Material3 color schemes
2. THE Assignment_Tracker_System SHALL follow the system theme setting by default
3. WHERE the user has selected a theme preference in settings, THE Assignment_Tracker_System SHALL apply the selected theme regardless of system settings
4. WHEN the system theme changes, THE Assignment_Tracker_System SHALL update the UI theme within 200 milliseconds
5. THE Assignment_Tracker_System SHALL persist theme preference to DataStore_Preferences

### Requirement 10

**User Story:** As a student, I want to delete assignments I no longer need, so that I can keep my assignment list clean and relevant

#### Acceptance Criteria

1. WHEN the user performs a delete action on an assignment, THE Assignment_Tracker_System SHALL display a confirmation dialog
2. WHEN the user confirms deletion, THE Assignment_Tracker_System SHALL remove the assignment from the Room_Database within 300 milliseconds
3. WHEN an assignment is deleted, THE Assignment_Tracker_System SHALL remove it from all views (List_View, Calendar_View, Board_View)
4. WHEN an assignment is deleted, THE Assignment_Tracker_System SHALL cancel any scheduled notifications for that assignment
5. THE Assignment_Tracker_System SHALL display a success message after successful deletion

### Requirement 11

**User Story:** As a developer, I want comprehensive unit and instrumentation tests, so that I can ensure code quality and prevent regressions

#### Acceptance Criteria

1. THE Assignment_Tracker_System SHALL include unit tests for all Use_Case classes with minimum 80% code coverage
2. THE Assignment_Tracker_System SHALL include unit tests for all ViewModel classes using test coroutines and Turbine for Flow testing
3. THE Assignment_Tracker_System SHALL include instrumentation tests for the Room_Database using in-memory database instances
4. THE Assignment_Tracker_System SHALL include UI tests for all major screens using Compose testing framework
5. WHEN the test suite executes, THE Assignment_Tracker_System SHALL complete all tests within 5 minutes

### Requirement 12

**User Story:** As a developer, I want the project to follow clean architecture principles, so that the codebase is maintainable and testable

#### Acceptance Criteria

1. THE Assignment_Tracker_System SHALL implement MVVM architecture with clear separation between UI, ViewModel, and data layers
2. THE Assignment_Tracker_System SHALL use the Assignment_Repository as the single source of truth for data operations
3. THE Assignment_Tracker_System SHALL implement Use_Case classes for each business operation to encapsulate business logic
4. THE Assignment_Tracker_System SHALL use dependency injection to provide dependencies to ViewModels and repositories
5. THE Assignment_Tracker_System SHALL use Kotlin Coroutines and Flow for asynchronous operations and data streams
