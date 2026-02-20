# Implementation Plan

## CHUNK 1: Project Scaffold

- [x] 1. Set up project configuration files





- [ ] 1.1 Create settings.gradle.kts with project configuration
  - Configure plugin management repositories
  - Set root project name


  - _Requirements: 12.1_

- [ ] 1.2 Create root build.gradle.kts with plugin versions
  - Add Android Gradle Plugin 8.2.0
  - Add Kotlin 1.9.20


  - Add Hilt 2.48
  - Add KSP 1.9.20-1.0.14
  - _Requirements: 12.1_

- [ ] 1.3 Configure app/build.gradle.kts with all dependencies
  - Enable Compose with Material3
  - Add Room, DataStore, WorkManager dependencies


  - Add Navigation Compose
  - Add Hilt dependencies
  - Add test dependencies (JUnit, Turbine, MockK)
  - Configure compile options for Java 17


  - _Requirements: 12.1, 12.5_

- [x] 1.4 Create AndroidManifest.xml with required permissions


  - Add POST_NOTIFICATIONS permission
  - Add SCHEDULE_EXACT_ALARM permission
  - Declare MainActivity as launcher
  - _Requirements: 5.2_

- [ ] 1.5 Implement MainActivity with Compose setup
  - Create single-activity with setContent
  - Call AssignmentApp composable
  - _Requirements: 12.1_

- [ ] 1.6 Create AssignmentApp.kt with theme and navigation stub
  - Implement AssignmentTrackerTheme wrapper
  - Add NavHost stub pointing to list screen





  - _Requirements: 9.1, 12.1_

- [x]* 1.7 Create ExampleUnitTest.kt to verify test pipeline


  - Simple assertion test to confirm test infrastructure
  - _Requirements: 11.1_



- [ ]* 1.8 Run tests and build verification
  - Execute ./gradlew testDebugUnitTest
  - Execute ./gradlew assembleDebug
  - _Requirements: 11.5_




## CHUNK 2: Data Model + Room Database

- [ ] 2. Implement data layer entities and database
- [ ] 2.1 Create Priority and Status enums
  - Define Priority enum (LOW, MEDIUM, HIGH, URGENT)


  - Define Status enum (NOT_STARTED, IN_PROGRESS, DONE)
  - _Requirements: 1.1, 4.1_

- [ ] 2.2 Create Assignment entity with Room annotations
  - Add all fields (id, title, description, dueDate, dueTime, priority, status, timestamps)
  - Add @Entity, @PrimaryKey annotations
  - _Requirements: 1.1, 1.2, 6.1_

- [ ] 2.3 Implement Converters.kt for type conversions
  - Add converters for LocalDate, LocalTime, LocalDateTime
  - Add converters for Priority and Status enums
  - Add @TypeConverter annotations
  - _Requirements: 6.1_

- [ ] 2.4 Create AssignmentDao.kt with all query methods
  - Implement getAllAssignments() returning Flow
  - Implement getAssignmentById() suspend function
  - Implement searchAssignments() with LIKE query
  - Implement getAssignmentsByStatus() and getAssignmentsByPriority()





  - Implement insert, update, delete operations
  - Implement getOverdueAssignments() and getCompletedThisWeek()


  - _Requirements: 1.2, 2.1, 2.2, 6.2, 7.1, 7.2_

- [ ] 2.5 Create AppDatabase.kt with Room database configuration
  - Define database with Assignment entity


  - Add Converters with @TypeConverters
  - Set version to 1
  - Provide abstract dao method
  - _Requirements: 6.1, 6.3_



- [ ]* 2.6 Write DAO instrumentation tests
  - Test insertAssignment and getAssignmentById
  - Test getAllAssignments Flow emission
  - Test searchAssignments with query
  - Test filter by status and priority
  - Test overdue and completed queries
  - Use in-memory database


  - _Requirements: 11.3_

- [ ]* 2.7 Run database tests
  - Execute ./gradlew connectedAndroidTest


  - _Requirements: 11.5_




## CHUNK 3: Repository + Use Cases + DI

- [ ] 3. Implement repository pattern and dependency injection
- [ ] 3.1 Create AssignmentRepository interface
  - Define all repository methods returning Flow or suspend functions
  - _Requirements: 12.2, 12.3_

- [ ] 3.2 Implement AssignmentRepositoryImpl
  - Inject AssignmentDao
  - Implement all methods delegating to DAO
  - Add error handling with try-catch
  - _Requirements: 6.2, 12.2_

- [ ] 3.3 Create NotificationScheduler utility class
  - Implement scheduleReminder() using WorkManager
  - Implement cancelReminder() for work cancellation
  - Calculate 24-hour delay (or 15 seconds for test mode)
  - _Requirements: 5.1, 5.4_

- [ ] 3.4 Implement all Use Case classes
  - CreateAssignmentUseCase with notification scheduling
  - UpdateAssignmentUseCase with notification rescheduling
  - DeleteAssignmentUseCase with notification cancellation
  - GetAssignmentUseCase for single assignment retrieval
  - GetAllAssignmentsUseCase returning Flow





  - SearchAssignmentsUseCase with query and filters
  - GetAnalyticsUseCase combining completed and overdue data
  - _Requirements: 1.2, 1.3, 2.2, 5.1, 7.1, 7.2, 10.2, 12.3_

- [ ] 3.5 Create Hilt modules (DatabaseModule, RepositoryModule, UseCaseModule)
  - DatabaseModule: provide AppDatabase, AssignmentDao, WorkManager, NotificationScheduler
  - RepositoryModule: provide AssignmentRepository implementation


  - UseCaseModule: provide all use case instances
  - _Requirements: 12.4_

- [ ] 3.6 Create AssignmentTrackerApplication class
  - Annotate with @HiltAndroidApp
  - Initialize notification channel
  - _Requirements: 5.2, 12.4_



- [ ] 3.7 Create manual DI fallback (DependencyContainer object)
  - Implement initialize() method
  - Provide factory methods for all dependencies
  - Use if Hilt configuration fails


  - _Requirements: 12.4_

- [ ]* 3.8 Write repository unit tests
  - Mock AssignmentDao
  - Test all repository methods
  - Test error handling
  - Verify Flow emissions using Turbine
  - _Requirements: 11.1_

- [ ]* 3.9 Write use case unit tests
  - Mock repository and NotificationScheduler
  - Test CreateAssignmentUseCase schedules notification
  - Test UpdateAssignmentUseCase reschedules notification
  - Test DeleteAssignmentUseCase cancels notification
  - Test SearchAssignmentsUseCase filtering logic
  - Test GetAnalyticsUseCase data combination
  - _Requirements: 11.1_

- [ ]* 3.10 Run unit tests
  - Execute ./gradlew testDebugUnitTest
  - _Requirements: 11.5_







## CHUNK 4: ViewModels

- [ ] 4. Implement ViewModels with state management
- [x] 4.1 Create AssignmentListViewModel


  - Inject GetAllAssignmentsUseCase, SearchAssignmentsUseCase, DeleteAssignmentUseCase
  - Implement search query, status filter, priority filter state
  - Combine filters and expose uiState as StateFlow
  - Implement updateSearchQuery(), updateStatusFilter(), updatePriorityFilter()
  - Implement deleteAssignment() method
  - Define AssignmentListUiState sealed interface (Loading, Success, Error)
  - _Requirements: 2.1, 2.2, 2.3, 2.4, 2.5, 10.1, 12.1_



- [ ] 4.2 Create AssignmentDetailViewModel
  - Inject GetAssignmentUseCase, CreateAssignmentUseCase, UpdateAssignmentUseCase
  - Load assignment by ID from SavedStateHandle
  - Implement form state (title, description, dueDate, dueTime, priority, status)
  - Implement update methods for each field
  - Implement saveAssignment() with validation
  - Define AssignmentDetailUiState data class
  - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.5, 12.1_

- [x] 4.3 Create BoardViewModel


  - Inject GetAllAssignmentsUseCase, UpdateAssignmentUseCase
  - Transform assignments Flow into BoardUiState with three lists
  - Implement moveToNextStatus() to cycle through statuses
  - Define BoardUiState sealed interface
  - _Requirements: 4.1, 4.2, 4.3, 4.4, 12.1_



- [ ] 4.4 Create CalendarViewModel
  - Inject GetAllAssignmentsUseCase
  - Maintain selectedMonth state as StateFlow
  - Combine month and assignments into CalendarUiState
  - Group assignments by date
  - Implement navigateToPreviousMonth() and navigateToNextMonth()
  - Define CalendarUiState sealed interface
  - _Requirements: 3.1, 3.2, 3.3, 3.4, 12.1_

- [ ]* 4.5 Write ViewModel unit tests
  - Test AssignmentListViewModel search and filter logic
  - Test AssignmentDetailViewModel form validation
  - Test BoardViewModel status transitions
  - Test CalendarViewModel month navigation
  - Use Turbine for Flow testing
  - Mock use cases with MockK
  - Use MainDispatcherRule for coroutine testing
  - _Requirements: 11.2_







- [ ]* 4.6 Run ViewModel tests
  - Execute ./gradlew testDebugUnitTest
  - _Requirements: 11.5_





## CHUNK 5: UI — List + Add/Edit Screens

- [ ] 5. Implement list and detail screens with navigation
- [x] 5.1 Create reusable UI components

  - AssignmentCard composable with title, description, priority, status, due date
  - PriorityChip and StatusChip composables


  - LoadingIndicator composable
  - ErrorMessage composable
  - _Requirements: 2.1, 4.5_


- [ ] 5.2 Implement AssignmentListScreen
  - Inject AssignmentListViewModel with hiltViewModel()



  - Collect uiState with collectAsStateWithLifecycle()
  - Display search TextField
  - Display FilterChipGroup for status and priority
  - Display LazyColumn of AssignmentCard items
  - Add FloatingActionButton for adding new assignment
  - Handle navigation callbacks
  - _Requirements: 2.1, 2.2, 2.3, 2.4, 2.5, 12.1_

- [ ] 5.3 Implement AssignmentDetailScreen
  - Inject AssignmentDetailViewModel with hiltViewModel()


  - Create form with TextField for title and description


  - Add DatePicker for due date
  - Add TimePicker for due time
  - Add dropdown/chips for priority selection
  - Add dropdown/chips for status selection


  - Add Save button calling viewModel.saveAssignment()
  - Display validation errors
  - Navigate back on successful save
  - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.5, 12.1_



- [ ] 5.4 Create NavGraph.kt with all routes
  - Define "list" route for AssignmentListScreen
  - Define "detail/{assignmentId}" route for AssignmentDetailScreen


  - Define "detail/new" route for adding new assignment
  - Add navigation arguments for assignmentId
  - Wire up navigation callbacks
  - _Requirements: 12.1_

- [x] 5.5 Update AssignmentApp.kt with complete navigation


  - Create NavController
  - Set up NavHost with NavGraph
  - _Requirements: 12.1_

- [ ]* 5.6 Write UI tests for list and detail screens
  - Test AssignmentListScreen displays assignments
  - Test search functionality
  - Test filter chips
  - Test navigation to detail screen
  - Test AssignmentDetailScreen form input
  - Test validation error display
  - Use Compose testing framework
  - _Requirements: 11.4_

- [x]* 5.7 Run UI tests and build


  - Execute ./gradlew connectedAndroidTest


  - Execute ./gradlew assembleDebug
  - _Requirements: 11.5_


## CHUNK 6: Calendar View

- [x] 6. Implement calendar view with assignment markers


- [ ] 6.1 Create CalendarDay composable
  - Display day number
  - Show marker if assignments exist on that date
  - Highlight current day
  - Handle click events
  - _Requirements: 3.2, 3.5_



- [ ] 6.2 Create CalendarGrid composable
  - Display month/year header
  - Display day-of-week labels


  - Create 7-column grid using LazyVerticalGrid
  - Calculate first day offset
  - Render CalendarDay for each date in month
  - _Requirements: 3.1, 3.2_

- [ ] 6.3 Implement CalendarScreen
  - Inject CalendarViewModel with hiltViewModel()
  - Display month navigation buttons
  - Render CalendarGrid with assignments data
  - Show assignment list dialog when day is clicked
  - Handle navigation to assignment detail
  - _Requirements: 3.1, 3.2, 3.3, 3.4, 3.5_

- [ ] 6.4 Add calendar route to NavGraph
  - Define "calendar" route
  - Wire up navigation from list screen
  - _Requirements: 12.1_

- [ ]* 6.5 Write calendar UI tests
  - Test calendar grid renders correctly


  - Test month navigation


  - Test day click shows assignments
  - Test current day highlighting
  - _Requirements: 11.4_



- [ ]* 6.6 Run calendar tests
  - Execute ./gradlew connectedAndroidTest
  - _Requirements: 11.5_




## CHUNK 7: Board (Kanban) View

- [ ] 7. Implement Kanban board view with status columns
- [x] 7.1 Create BoardCard composable


  - Display assignment title, due date, priority
  - Add tap handler for moving to next status
  - Add click handler for viewing details
  - _Requirements: 4.3, 4.5_

- [ ] 7.2 Create BoardColumn composable
  - Display column title with count
  - Render LazyColumn of BoardCard items
  - Apply column-specific background color
  - _Requirements: 4.1, 4.2_

- [ ] 7.3 Create BoardView composable
  - Display three BoardColumn components (Not Started, In Progress, Done)
  - Enable horizontal scrolling
  - Pass assignments to respective columns
  - _Requirements: 4.1, 4.2_





- [ ] 7.4 Implement BoardScreen
  - Inject BoardViewModel with hiltViewModel()
  - Collect uiState with board data
  - Render BoardView with three lists
  - Handle moveToNextStatus callback
  - Handle navigation to detail screen
  - _Requirements: 4.1, 4.2, 4.3, 4.4_



- [ ] 7.5 Add board route to NavGraph
  - Define "board" route
  - Wire up navigation from list screen
  - _Requirements: 12.1_



- [ ]* 7.6 Write board UI tests
  - Test three columns render with correct assignments


  - Test tap to move assignment to next status
  - Test status updates persist
  - Test navigation to detail
  - _Requirements: 11.4_

- [x]* 7.7 Run board tests


  - Execute ./gradlew connectedAndroidTest
  - _Requirements: 11.5_


## CHUNK 8: Notifications & WorkManager

- [ ] 8. Implement background reminders with WorkManager
- [ ] 8.1 Create ReminderWorker
  - Extend CoroutineWorker
  - Extract assignment data from inputData
  - Create notification with title and due date
  - Create PendingIntent to open assignment detail
  - Show notification using NotificationManager
  - Return Result.success()


  - _Requirements: 5.2, 5.3_



- [ ] 8.2 Update NotificationScheduler implementation
  - Calculate delay as 24 hours before due date/time


  - Create WorkRequest with assignment data
  - Use enqueueUniqueWork with REPLACE policy
  - Tag work with assignment ID
  - Implement test mode with 15-second delay


  - _Requirements: 5.1, 5.5_

- [ ] 8.3 Create notification channel in Application class
  - Create channel with ID "assignment_reminders"
  - Set importance to HIGH


  - Register channel with NotificationManager
  - _Requirements: 5.2_

- [ ] 8.4 Update MainActivity to handle notification intents
  - Extract assignment_id from intent extras


  - Navigate to detail screen if ID present
  - _Requirements: 5.3_

- [ ]* 8.5 Write Worker tests
  - Test ReminderWorker executes successfully


  - Test notification is created with correct data
  - Use TestListenableWorkerBuilder
  - _Requirements: 11.3_



- [ ]* 8.6 Test notification scheduling manually
  - Create test assignment with near-future due date
  - Verify notification appears at scheduled time


  - Verify tapping notification opens correct assignment
  - _Requirements: 5.1, 5.2, 5.3_

- [ ]* 8.7 Run worker tests
  - Execute ./gradlew testDebugUnitTest
  - _Requirements: 11.5_


## CHUNK 9: Search & Filter Improvements

- [ ] 9. Enhance search and filter functionality
- [ ] 9.1 Verify DAO LIKE queries work correctly
  - Test case-insensitive search
  - Test partial matches in title and description
  - _Requirements: 2.2_





- [ ] 9.2 Create FilterChipGroup composable
  - Display status filter chips
  - Display priority filter chips


  - Handle selection/deselection
  - Support clearing filters
  - _Requirements: 2.3, 2.4_



- [ ] 9.3 Update AssignmentListScreen with filter UI
  - Add FilterChipGroup below search field
  - Wire up filter callbacks to ViewModel


  - Display active filter count
  - Add "Clear Filters" button
  - _Requirements: 2.3, 2.4, 2.5_



- [ ] 9.4 Implement combined search and filter logic
  - Ensure SearchAssignmentsUseCase applies all filters
  - Test multiple filters simultaneously

  - _Requirements: 2.5_

- [ ]* 9.5 Write search and filter tests
  - Test search query filters results
  - Test status filter works
  - Test priority filter works
  - Test combined filters work together
  - Test clearing filters shows all assignments
  - _Requirements: 11.1_

- [ ]* 9.6 Run search tests
  - Execute ./gradlew testDebugUnitTest
  - _Requirements: 11.5_


## CHUNK 10: Analytics

- [ ] 10. Implement analytics screen with charts
- [x] 10.1 Create AnalyticsViewModel


  - Inject GetAnalyticsUseCase


  - Expose analytics data as StateFlow
  - Calculate completed this week count
  - Calculate overdue count
  - Calculate weekly completion data for last 4 weeks
  - Define AnalyticsUiState sealed interface
  - _Requirements: 7.1, 7.2, 7.3, 7.4_



- [ ] 10.2 Create WeeklyCompletionChart composable
  - Use Canvas to draw bar chart
  - Display bars for each week
  - Label weeks on x-axis
  - Scale bars based on max value

  - _Requirements: 7.3_

- [ ] 10.3 Create AnalyticsCard composable
  - Display metric title and value
  - Use Material3 Card styling

  - _Requirements: 7.1, 7.2_

- [ ] 10.4 Implement AnalyticsScreen
  - Inject AnalyticsViewModel with hiltViewModel()
  - Display completed this week count in AnalyticsCard
  - Display overdue count in AnalyticsCard
  - Display WeeklyCompletionChart
  - Add refresh functionality
  - _Requirements: 7.1, 7.2, 7.3, 7.4, 7.5_

- [ ] 10.5 Add analytics route to NavGraph
  - Define "analytics" route
  - Wire up navigation from list screen
  - _Requirements: 12.1_

- [ ]* 10.6 Write analytics tests
  - Test GetAnalyticsUseCase calculates correct counts
  - Test AnalyticsViewModel exposes correct data
  - Test analytics screen displays metrics
  - _Requirements: 11.1, 11.4_

- [ ]* 10.7 Run analytics tests
  - Execute ./gradlew testDebugUnitTest
  - _Requirements: 11.5_


## CHUNK 11: Preferences & Onboarding

- [ ] 11. Implement user preferences and onboarding flow
- [ ] 11.1 Create UserPreferences data class and ThemeMode enum
  - Define fields: userName, course, academicYear, themeMode, hasCompletedOnboarding
  - Define ThemeMode enum (LIGHT, DARK, SYSTEM)
  - _Requirements: 8.1, 9.1_

- [ ] 11.2 Create UserPreferencesRepository with DataStore
  - Implement userPreferencesFlow exposing Flow<UserPreferences>
  - Implement updateUserPreferences() suspend function
  - Define preference keys
  - _Requirements: 8.2, 8.4_

- [ ] 11.3 Create SettingsViewModel
  - Inject UserPreferencesRepository
  - Expose preferences as StateFlow
  - Implement updateUserName(), updateCourse(), updateAcademicYear()
  - Implement updateThemeMode()
  - _Requirements: 8.1, 8.2, 9.3_

- [ ] 11.4 Implement SettingsScreen
  - Inject SettingsViewModel with hiltViewModel()
  - Display TextField for name, course, academic year
  - Display theme mode selector (Light/Dark/System)
  - Save changes on text field value changes
  - _Requirements: 8.1, 8.2, 8.5, 9.3_

- [ ] 11.5 Create OnboardingScreen
  - Display welcome message
  - Collect user name, course, academic year
  - Save preferences on completion
  - Navigate to list screen
  - _Requirements: 8.3_

- [ ] 11.6 Update AssignmentApp to check onboarding status
  - Read hasCompletedOnboarding from preferences
  - Set start destination to "onboarding" if not completed
  - Set start destination to "list" if completed
  - _Requirements: 8.3_

- [ ] 11.7 Update AssignmentTrackerTheme to use preference
  - Read themeMode from preferences
  - Apply theme based on preference
  - _Requirements: 9.1, 9.3, 9.4, 9.5_

- [ ] 11.8 Add settings and onboarding routes to NavGraph
  - Define "settings" route
  - Define "onboarding" route
  - Wire up navigation from list screen to settings
  - _Requirements: 12.1_

- [ ]* 11.9 Write DataStore tests
  - Test saving and reading preferences
  - Test default values
  - Test theme mode changes
  - _Requirements: 11.3_

- [ ]* 11.10 Run preferences tests
  - Execute ./gradlew testDebugUnitTest
  - _Requirements: 11.5_


## CHUNK 12: Theme, Assets, Icons

- [ ] 12. Finalize theming and visual assets
- [ ] 12.1 Create Color.kt with Material3 color schemes
  - Define LightColorScheme with primary, secondary, background, surface colors
  - Define DarkColorScheme with appropriate dark theme colors
  - _Requirements: 9.1, 9.2_

- [ ] 12.2 Create Type.kt with typography definitions
  - Define Material3 Typography with custom font sizes
  - Set up title, body, label text styles
  - _Requirements: 9.1_

- [ ] 12.3 Update Theme.kt with dynamic color support
  - Support Android 12+ dynamic colors
  - Fall back to custom color schemes on older versions
  - Apply theme based on darkTheme parameter
  - _Requirements: 9.1, 9.2, 9.4_

- [ ] 12.4 Add app icon and notification icon
  - Create ic_launcher.xml (adaptive icon)
  - Create ic_notification.xml (monochrome icon for notifications)
  - _Requirements: 5.2_

- [ ] 12.5 Add content descriptions for accessibility
  - Add contentDescription to all Icon composables
  - Add semantics to interactive elements
  - Ensure TalkBack support
  - _Requirements: 12.1_

- [ ] 12.6 Test theme switching
  - Verify light theme displays correctly
  - Verify dark theme displays correctly
  - Verify system theme following works
  - Test theme persistence across app restarts
  - _Requirements: 9.1, 9.2, 9.4, 9.5_

- [ ]* 12.7 Run integration smoke tests
  - Test complete user flow: onboarding → add assignment → view in list/calendar/board
  - Test theme switching during usage
  - Test notification flow end-to-end
  - _Requirements: 11.4_

- [ ]* 12.8 Final build verification
  - Execute ./gradlew assembleDebug
  - Execute ./gradlew testDebugUnitTest
  - Execute ./gradlew connectedAndroidTest
  - _Requirements: 11.5_


## CHUNK 13: CI/CD

- [ ] 13. Set up continuous integration pipeline
- [ ] 13.1 Create .github/workflows/android.yml
  - Set up workflow triggered on push and pull request
  - Configure Java 17 environment
  - Add Gradle caching
  - Run ./gradlew assembleDebug
  - Run ./gradlew testDebugUnitTest
  - Upload test reports as artifacts
  - _Requirements: 11.5_

- [ ] 13.2 Add build status badge to README
  - Create README.md with project description
  - Add GitHub Actions badge
  - Document app features
  - Add setup instructions
  - _Requirements: 12.1_

- [ ] 13.3 Verify CI pipeline execution
  - Push changes to trigger workflow
  - Verify all jobs pass successfully
  - Check test reports
  - _Requirements: 11.5_

- [ ] 13.4 Final verification and cleanup
  - Review all code for consistency
  - Remove unused imports and dead code
  - Verify all requirements are met
  - Test app on physical device
  - _Requirements: 12.1_

