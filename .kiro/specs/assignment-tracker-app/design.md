# Design Document

## Overview

The Assignment Tracker is a single-activity Android application built with Jetpack Compose following clean MVVM architecture. The app uses offline-first design with Room database as the single source of truth, WorkManager for background notifications, and DataStore for preferences. The architecture emphasizes testability, separation of concerns, and reactive data flow using Kotlin Coroutines and Flow.

### Technology Stack

- **Language**: Kotlin 1.9+
- **UI Framework**: Jetpack Compose with Material3
- **Architecture**: MVVM with Repository pattern
- **Database**: Room 2.6+
- **Preferences**: DataStore Preferences
- **Background Work**: WorkManager 2.9+
- **Navigation**: Navigation Compose 2.7+
- **Dependency Injection**: Hilt (with manual DI fallback)
- **Async**: Coroutines + Flow
- **Testing**: JUnit, Turbine, Compose Test, Room in-memory

### Key Design Principles

1. **Offline-First**: All data operations target local Room database
2. **Single Source of Truth**: Repository layer owns all data access
3. **Unidirectional Data Flow**: UI observes ViewModels via StateFlow/Flow
4. **Separation of Concerns**: Clear boundaries between layers
5. **Testability**: Dependency injection and interface-based design

## Architecture

### Layer Structure

```
┌─────────────────────────────────────────┐
│         UI Layer (Compose)              │
│  - Screens, Components, Navigation      │
└──────────────┬──────────────────────────┘
               │ observes StateFlow/Flow
┌──────────────▼──────────────────────────┐
│         ViewModel Layer                  │
│  - State management, UI logic           │
└──────────────┬──────────────────────────┘
               │ calls
┌──────────────▼──────────────────────────┐
│         Domain Layer                     │
│  - Use Cases, Business Logic            │
└──────────────┬──────────────────────────┘
               │ uses
┌──────────────▼──────────────────────────┐
│         Data Layer                       │
│  - Repository, Room, DataStore          │
└─────────────────────────────────────────┘
```


### Package Structure

```
com.example.assignmenttracker/
├── data/
│   ├── local/
│   │   ├── entity/
│   │   │   └── Assignment.kt
│   │   ├── dao/
│   │   │   └── AssignmentDao.kt
│   │   ├── database/
│   │   │   ├── AppDatabase.kt
│   │   │   └── Converters.kt
│   │   └── preferences/
│   │       └── UserPreferences.kt
│   └── repository/
│       └── AssignmentRepositoryImpl.kt
├── domain/
│   ├── model/
│   │   ├── Priority.kt
│   │   └── Status.kt
│   ├── repository/
│   │   └── AssignmentRepository.kt (interface)
│   └── usecase/
│       ├── CreateAssignmentUseCase.kt
│       ├── UpdateAssignmentUseCase.kt
│       ├── DeleteAssignmentUseCase.kt
│       ├── GetAssignmentUseCase.kt
│       ├── GetAllAssignmentsUseCase.kt
│       ├── SearchAssignmentsUseCase.kt
│       └── GetAnalyticsUseCase.kt
├── presentation/
│   ├── list/
│   │   ├── AssignmentListScreen.kt
│   │   └── AssignmentListViewModel.kt
│   ├── detail/
│   │   ├── AssignmentDetailScreen.kt
│   │   └── AssignmentDetailViewModel.kt
│   ├── calendar/
│   │   ├── CalendarScreen.kt
│   │   └── CalendarViewModel.kt
│   ├── board/
│   │   ├── BoardScreen.kt
│   │   └── BoardViewModel.kt
│   ├── analytics/
│   │   ├── AnalyticsScreen.kt
│   │   └── AnalyticsViewModel.kt
│   ├── settings/
│   │   ├── SettingsScreen.kt
│   │   └── SettingsViewModel.kt
│   ├── onboarding/
│   │   └── OnboardingScreen.kt
│   ├── navigation/
│   │   └── NavGraph.kt
│   └── theme/
│       ├── Theme.kt
│       ├── Color.kt
│       └── Type.kt
├── worker/
│   └── ReminderWorker.kt
├── di/
│   ├── DatabaseModule.kt
│   ├── RepositoryModule.kt
│   └── UseCaseModule.kt
├── util/
│   ├── DateUtils.kt
│   └── NotificationUtils.kt
├── AssignmentTrackerApplication.kt
└── MainActivity.kt
```

## Components and Interfaces

### Data Layer

#### Assignment Entity

```kotlin
@Entity(tableName = "assignments")
data class Assignment(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val dueDate: LocalDate,
    val dueTime: LocalTime,
    val priority: Priority,
    val status: Status,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
```


#### Enums

```kotlin
enum class Priority {
    LOW, MEDIUM, HIGH, URGENT
}

enum class Status {
    NOT_STARTED, IN_PROGRESS, DONE
}
```

#### AssignmentDao

```kotlin
@Dao
interface AssignmentDao {
    @Query("SELECT * FROM assignments ORDER BY dueDate ASC, dueTime ASC")
    fun getAllAssignments(): Flow<List<Assignment>>
    
    @Query("SELECT * FROM assignments WHERE id = :id")
    suspend fun getAssignmentById(id: Long): Assignment?
    
    @Query("SELECT * FROM assignments WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'")
    fun searchAssignments(query: String): Flow<List<Assignment>>
    
    @Query("SELECT * FROM assignments WHERE status = :status")
    fun getAssignmentsByStatus(status: Status): Flow<List<Assignment>>
    
    @Query("SELECT * FROM assignments WHERE priority = :priority")
    fun getAssignmentsByPriority(priority: Priority): Flow<List<Assignment>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssignment(assignment: Assignment): Long
    
    @Update
    suspend fun updateAssignment(assignment: Assignment)
    
    @Delete
    suspend fun deleteAssignment(assignment: Assignment)
    
    @Query("SELECT * FROM assignments WHERE dueDate < :today AND status != 'DONE'")
    fun getOverdueAssignments(today: LocalDate): Flow<List<Assignment>>
    
    @Query("SELECT * FROM assignments WHERE status = 'DONE' AND updatedAt >= :startOfWeek")
    fun getCompletedThisWeek(startOfWeek: LocalDateTime): Flow<List<Assignment>>
}
```

#### AppDatabase

```kotlin
@Database(
    entities = [Assignment::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun assignmentDao(): AssignmentDao
}
```

#### Converters

```kotlin
class Converters {
    @TypeConverter
    fun fromLocalDate(value: LocalDate?): String? = value?.toString()
    
    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? = value?.let { LocalDate.parse(it) }
    
    @TypeConverter
    fun fromLocalTime(value: LocalTime?): String? = value?.toString()
    
    @TypeConverter
    fun toLocalTime(value: String?): LocalTime? = value?.let { LocalTime.parse(it) }
    
    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime?): String? = value?.toString()
    
    @TypeConverter
    fun toLocalDateTime(value: String?): LocalDateTime? = value?.let { LocalDateTime.parse(it) }
    
    @TypeConverter
    fun fromPriority(value: Priority): String = value.name
    
    @TypeConverter
    fun toPriority(value: String): Priority = Priority.valueOf(value)
    
    @TypeConverter
    fun fromStatus(value: Status): String = value.name
    
    @TypeConverter
    fun toStatus(value: String): Status = Status.valueOf(value)
}
```


#### AssignmentRepository Interface

```kotlin
interface AssignmentRepository {
    fun getAllAssignments(): Flow<List<Assignment>>
    suspend fun getAssignmentById(id: Long): Assignment?
    fun searchAssignments(query: String): Flow<List<Assignment>>
    fun getAssignmentsByStatus(status: Status): Flow<List<Assignment>>
    fun getAssignmentsByPriority(priority: Priority): Flow<List<Assignment>>
    suspend fun insertAssignment(assignment: Assignment): Long
    suspend fun updateAssignment(assignment: Assignment)
    suspend fun deleteAssignment(assignment: Assignment)
    fun getOverdueAssignments(): Flow<List<Assignment>>
    fun getCompletedThisWeek(): Flow<List<Assignment>>
}
```

#### AssignmentRepositoryImpl

```kotlin
class AssignmentRepositoryImpl(
    private val dao: AssignmentDao
) : AssignmentRepository {
    override fun getAllAssignments(): Flow<List<Assignment>> = dao.getAllAssignments()
    
    override suspend fun getAssignmentById(id: Long): Assignment? = dao.getAssignmentById(id)
    
    override fun searchAssignments(query: String): Flow<List<Assignment>> = 
        dao.searchAssignments(query)
    
    override fun getAssignmentsByStatus(status: Status): Flow<List<Assignment>> = 
        dao.getAssignmentsByStatus(status)
    
    override fun getAssignmentsByPriority(priority: Priority): Flow<List<Assignment>> = 
        dao.getAssignmentsByPriority(priority)
    
    override suspend fun insertAssignment(assignment: Assignment): Long = 
        dao.insertAssignment(assignment)
    
    override suspend fun updateAssignment(assignment: Assignment) = 
        dao.updateAssignment(assignment)
    
    override suspend fun deleteAssignment(assignment: Assignment) = 
        dao.deleteAssignment(assignment)
    
    override fun getOverdueAssignments(): Flow<List<Assignment>> = 
        dao.getOverdueAssignments(LocalDate.now())
    
    override fun getCompletedThisWeek(): Flow<List<Assignment>> {
        val startOfWeek = LocalDateTime.now().with(DayOfWeek.MONDAY).toLocalDate().atStartOfDay()
        return dao.getCompletedThisWeek(startOfWeek)
    }
}
```

#### UserPreferences (DataStore)

```kotlin
data class UserPreferences(
    val userName: String = "",
    val course: String = "",
    val academicYear: String = "",
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val hasCompletedOnboarding: Boolean = false
)

enum class ThemeMode {
    LIGHT, DARK, SYSTEM
}

class UserPreferencesRepository(private val context: Context) {
    private val Context.dataStore by preferencesDataStore(name = "user_preferences")
    
    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data.map { prefs ->
        UserPreferences(
            userName = prefs[USER_NAME] ?: "",
            course = prefs[COURSE] ?: "",
            academicYear = prefs[ACADEMIC_YEAR] ?: "",
            themeMode = ThemeMode.valueOf(prefs[THEME_MODE] ?: ThemeMode.SYSTEM.name),
            hasCompletedOnboarding = prefs[HAS_COMPLETED_ONBOARDING] ?: false
        )
    }
    
    suspend fun updateUserPreferences(preferences: UserPreferences) {
        context.dataStore.edit { prefs ->
            prefs[USER_NAME] = preferences.userName
            prefs[COURSE] = preferences.course
            prefs[ACADEMIC_YEAR] = preferences.academicYear
            prefs[THEME_MODE] = preferences.themeMode.name
            prefs[HAS_COMPLETED_ONBOARDING] = preferences.hasCompletedOnboarding
        }
    }
    
    companion object {
        private val USER_NAME = stringPreferencesKey("user_name")
        private val COURSE = stringPreferencesKey("course")
        private val ACADEMIC_YEAR = stringPreferencesKey("academic_year")
        private val THEME_MODE = stringPreferencesKey("theme_mode")
        private val HAS_COMPLETED_ONBOARDING = booleanPreferencesKey("has_completed_onboarding")
    }
}
```


### Domain Layer

#### Use Cases

Each use case encapsulates a single business operation:

```kotlin
class CreateAssignmentUseCase(
    private val repository: AssignmentRepository,
    private val notificationScheduler: NotificationScheduler
) {
    suspend operator fun invoke(assignment: Assignment): Long {
        val id = repository.insertAssignment(assignment)
        notificationScheduler.scheduleReminder(assignment.copy(id = id))
        return id
    }
}

class UpdateAssignmentUseCase(
    private val repository: AssignmentRepository,
    private val notificationScheduler: NotificationScheduler
) {
    suspend operator fun invoke(assignment: Assignment) {
        repository.updateAssignment(assignment)
        notificationScheduler.cancelReminder(assignment.id)
        notificationScheduler.scheduleReminder(assignment)
    }
}

class DeleteAssignmentUseCase(
    private val repository: AssignmentRepository,
    private val notificationScheduler: NotificationScheduler
) {
    suspend operator fun invoke(assignment: Assignment) {
        repository.deleteAssignment(assignment)
        notificationScheduler.cancelReminder(assignment.id)
    }
}

class GetAssignmentUseCase(private val repository: AssignmentRepository) {
    suspend operator fun invoke(id: Long): Assignment? = repository.getAssignmentById(id)
}

class GetAllAssignmentsUseCase(private val repository: AssignmentRepository) {
    operator fun invoke(): Flow<List<Assignment>> = repository.getAllAssignments()
}

class SearchAssignmentsUseCase(private val repository: AssignmentRepository) {
    operator fun invoke(
        query: String,
        statusFilter: Status? = null,
        priorityFilter: Priority? = null
    ): Flow<List<Assignment>> {
        return if (query.isBlank() && statusFilter == null && priorityFilter == null) {
            repository.getAllAssignments()
        } else {
            repository.searchAssignments(query).map { assignments ->
                assignments.filter { assignment ->
                    (statusFilter == null || assignment.status == statusFilter) &&
                    (priorityFilter == null || assignment.priority == priorityFilter)
                }
            }
        }
    }
}

class GetAnalyticsUseCase(private val repository: AssignmentRepository) {
    operator fun invoke(): Flow<AnalyticsData> {
        return combine(
            repository.getCompletedThisWeek(),
            repository.getOverdueAssignments()
        ) { completed, overdue ->
            AnalyticsData(
                completedThisWeek = completed.size,
                overdueCount = overdue.size
            )
        }
    }
}

data class AnalyticsData(
    val completedThisWeek: Int,
    val overdueCount: Int
)
```


### Presentation Layer

#### ViewModels

**AssignmentListViewModel**

```kotlin
@HiltViewModel
class AssignmentListViewModel @Inject constructor(
    private val getAllAssignmentsUseCase: GetAllAssignmentsUseCase,
    private val searchAssignmentsUseCase: SearchAssignmentsUseCase,
    private val deleteAssignmentUseCase: DeleteAssignmentUseCase
) : ViewModel() {
    
    private val _searchQuery = MutableStateFlow("")
    private val _statusFilter = MutableStateFlow<Status?>(null)
    private val _priorityFilter = MutableStateFlow<Priority?>(null)
    
    val uiState: StateFlow<AssignmentListUiState> = combine(
        _searchQuery,
        _statusFilter,
        _priorityFilter
    ) { query, status, priority ->
        Triple(query, status, priority)
    }.flatMapLatest { (query, status, priority) ->
        searchAssignmentsUseCase(query, status, priority).map { assignments ->
            AssignmentListUiState.Success(assignments)
        }
    }.catch { error ->
        emit(AssignmentListUiState.Error(error.message ?: "Unknown error"))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AssignmentListUiState.Loading
    )
    
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
    
    fun updateStatusFilter(status: Status?) {
        _statusFilter.value = status
    }
    
    fun updatePriorityFilter(priority: Priority?) {
        _priorityFilter.value = priority
    }
    
    fun deleteAssignment(assignment: Assignment) {
        viewModelScope.launch {
            deleteAssignmentUseCase(assignment)
        }
    }
}

sealed interface AssignmentListUiState {
    object Loading : AssignmentListUiState
    data class Success(val assignments: List<Assignment>) : AssignmentListUiState
    data class Error(val message: String) : AssignmentListUiState
}
```

**AssignmentDetailViewModel**

```kotlin
@HiltViewModel
class AssignmentDetailViewModel @Inject constructor(
    private val getAssignmentUseCase: GetAssignmentUseCase,
    private val createAssignmentUseCase: CreateAssignmentUseCase,
    private val updateAssignmentUseCase: UpdateAssignmentUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val assignmentId: Long? = savedStateHandle["assignmentId"]
    
    private val _uiState = MutableStateFlow(AssignmentDetailUiState())
    val uiState: StateFlow<AssignmentDetailUiState> = _uiState.asStateFlow()
    
    init {
        assignmentId?.let { loadAssignment(it) }
    }
    
    private fun loadAssignment(id: Long) {
        viewModelScope.launch {
            val assignment = getAssignmentUseCase(id)
            assignment?.let {
                _uiState.update { state ->
                    state.copy(
                        title = it.title,
                        description = it.description,
                        dueDate = it.dueDate,
                        dueTime = it.dueTime,
                        priority = it.priority,
                        status = it.status
                    )
                }
            }
        }
    }
    
    fun updateTitle(title: String) {
        _uiState.update { it.copy(title = title, titleError = null) }
    }
    
    fun updateDescription(description: String) {
        _uiState.update { it.copy(description = description) }
    }
    
    fun updateDueDate(date: LocalDate) {
        _uiState.update { it.copy(dueDate = date) }
    }
    
    fun updateDueTime(time: LocalTime) {
        _uiState.update { it.copy(dueTime = time) }
    }
    
    fun updatePriority(priority: Priority) {
        _uiState.update { it.copy(priority = priority) }
    }
    
    fun updateStatus(status: Status) {
        _uiState.update { it.copy(status = status) }
    }
    
    fun saveAssignment() {
        val state = _uiState.value
        
        if (state.title.isBlank()) {
            _uiState.update { it.copy(titleError = "Title is required") }
            return
        }
        
        viewModelScope.launch {
            val assignment = Assignment(
                id = assignmentId ?: 0,
                title = state.title,
                description = state.description,
                dueDate = state.dueDate,
                dueTime = state.dueTime,
                priority = state.priority,
                status = state.status,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
            
            if (assignmentId == null) {
                createAssignmentUseCase(assignment)
            } else {
                updateAssignmentUseCase(assignment)
            }
            
            _uiState.update { it.copy(isSaved = true) }
        }
    }
}

data class AssignmentDetailUiState(
    val title: String = "",
    val description: String = "",
    val dueDate: LocalDate = LocalDate.now(),
    val dueTime: LocalTime = LocalTime.now(),
    val priority: Priority = Priority.MEDIUM,
    val status: Status = Status.NOT_STARTED,
    val titleError: String? = null,
    val isSaved: Boolean = false
)
```


**BoardViewModel**

```kotlin
@HiltViewModel
class BoardViewModel @Inject constructor(
    private val getAllAssignmentsUseCase: GetAllAssignmentsUseCase,
    private val updateAssignmentUseCase: UpdateAssignmentUseCase
) : ViewModel() {
    
    val uiState: StateFlow<BoardUiState> = getAllAssignmentsUseCase()
        .map { assignments ->
            BoardUiState.Success(
                notStarted = assignments.filter { it.status == Status.NOT_STARTED },
                inProgress = assignments.filter { it.status == Status.IN_PROGRESS },
                done = assignments.filter { it.status == Status.DONE }
            )
        }
        .catch { error ->
            emit(BoardUiState.Error(error.message ?: "Unknown error"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = BoardUiState.Loading
        )
    
    fun moveToNextStatus(assignment: Assignment) {
        viewModelScope.launch {
            val newStatus = when (assignment.status) {
                Status.NOT_STARTED -> Status.IN_PROGRESS
                Status.IN_PROGRESS -> Status.DONE
                Status.DONE -> Status.NOT_STARTED
            }
            updateAssignmentUseCase(assignment.copy(status = newStatus))
        }
    }
}

sealed interface BoardUiState {
    object Loading : BoardUiState
    data class Success(
        val notStarted: List<Assignment>,
        val inProgress: List<Assignment>,
        val done: List<Assignment>
    ) : BoardUiState
    data class Error(val message: String) : BoardUiState
}
```

**CalendarViewModel**

```kotlin
@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val getAllAssignmentsUseCase: GetAllAssignmentsUseCase
) : ViewModel() {
    
    private val _selectedMonth = MutableStateFlow(YearMonth.now())
    val selectedMonth: StateFlow<YearMonth> = _selectedMonth.asStateFlow()
    
    val uiState: StateFlow<CalendarUiState> = combine(
        _selectedMonth,
        getAllAssignmentsUseCase()
    ) { month, assignments ->
        val assignmentsByDate = assignments.groupBy { it.dueDate }
        CalendarUiState.Success(
            month = month,
            assignmentsByDate = assignmentsByDate
        )
    }.catch { error ->
        emit(CalendarUiState.Error(error.message ?: "Unknown error"))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CalendarUiState.Loading
    )
    
    fun navigateToPreviousMonth() {
        _selectedMonth.value = _selectedMonth.value.minusMonths(1)
    }
    
    fun navigateToNextMonth() {
        _selectedMonth.value = _selectedMonth.value.plusMonths(1)
    }
}

sealed interface CalendarUiState {
    object Loading : CalendarUiState
    data class Success(
        val month: YearMonth,
        val assignmentsByDate: Map<LocalDate, List<Assignment>>
    ) : CalendarUiState
    data class Error(val message: String) : CalendarUiState
}
```


### Background Work Layer

#### ReminderWorker

```kotlin
class ReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    
    override suspend fun doWork(): Result {
        val assignmentId = inputData.getLong("assignment_id", -1)
        val title = inputData.getString("assignment_title") ?: return Result.failure()
        val dueDate = inputData.getString("due_date") ?: return Result.failure()
        
        showNotification(assignmentId, title, dueDate)
        
        return Result.success()
    }
    
    private fun showNotification(assignmentId: Long, title: String, dueDate: String) {
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            putExtra("assignment_id", assignmentId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            assignmentId.toInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Assignment Due Soon")
            .setContentText("$title is due on $dueDate")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        
        val notificationManager = NotificationManagerCompat.from(applicationContext)
        notificationManager.notify(assignmentId.toInt(), notification)
    }
    
    companion object {
        const val CHANNEL_ID = "assignment_reminders"
    }
}
```

#### NotificationScheduler

```kotlin
class NotificationScheduler(
    private val context: Context,
    private val workManager: WorkManager
) {
    fun scheduleReminder(assignment: Assignment) {
        val dueDateTime = LocalDateTime.of(assignment.dueDate, assignment.dueTime)
        val reminderTime = dueDateTime.minusHours(24)
        val delay = Duration.between(LocalDateTime.now(), reminderTime)
        
        if (delay.isNegative) return // Don't schedule past reminders
        
        val inputData = workDataOf(
            "assignment_id" to assignment.id,
            "assignment_title" to assignment.title,
            "due_date" to assignment.dueDate.toString()
        )
        
        val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delay.toMillis(), TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .addTag("reminder_${assignment.id}")
            .build()
        
        workManager.enqueueUniqueWork(
            "reminder_${assignment.id}",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }
    
    fun cancelReminder(assignmentId: Long) {
        workManager.cancelUniqueWork("reminder_$assignmentId")
    }
}
```


### Navigation

#### NavGraph

```kotlin
@Composable
fun AssignmentNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "list",
        modifier = modifier
    ) {
        composable("list") {
            AssignmentListScreen(
                onNavigateToDetail = { id ->
                    navController.navigate("detail/$id")
                },
                onNavigateToAdd = {
                    navController.navigate("detail/new")
                },
                onNavigateToCalendar = {
                    navController.navigate("calendar")
                },
                onNavigateToBoard = {
                    navController.navigate("board")
                },
                onNavigateToAnalytics = {
                    navController.navigate("analytics")
                },
                onNavigateToSettings = {
                    navController.navigate("settings")
                }
            )
        }
        
        composable(
            route = "detail/{assignmentId}",
            arguments = listOf(navArgument("assignmentId") { type = NavType.StringType })
        ) {
            AssignmentDetailScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable("calendar") {
            CalendarScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDetail = { id ->
                    navController.navigate("detail/$id")
                }
            )
        }
        
        composable("board") {
            BoardScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDetail = { id ->
                    navController.navigate("detail/$id")
                }
            )
        }
        
        composable("analytics") {
            AnalyticsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable("settings") {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable("onboarding") {
            OnboardingScreen(
                onComplete = {
                    navController.navigate("list") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }
    }
}
```


### Dependency Injection

#### DatabaseModule

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "assignment_tracker_db"
        ).build()
    }
    
    @Provides
    fun provideAssignmentDao(database: AppDatabase): AssignmentDao {
        return database.assignmentDao()
    }
    
    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }
    
    @Provides
    @Singleton
    fun provideNotificationScheduler(
        @ApplicationContext context: Context,
        workManager: WorkManager
    ): NotificationScheduler {
        return NotificationScheduler(context, workManager)
    }
}
```

#### RepositoryModule

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    
    @Provides
    @Singleton
    fun provideAssignmentRepository(dao: AssignmentDao): AssignmentRepository {
        return AssignmentRepositoryImpl(dao)
    }
    
    @Provides
    @Singleton
    fun provideUserPreferencesRepository(
        @ApplicationContext context: Context
    ): UserPreferencesRepository {
        return UserPreferencesRepository(context)
    }
}
```

#### UseCaseModule

```kotlin
@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {
    
    @Provides
    fun provideCreateAssignmentUseCase(
        repository: AssignmentRepository,
        scheduler: NotificationScheduler
    ): CreateAssignmentUseCase {
        return CreateAssignmentUseCase(repository, scheduler)
    }
    
    @Provides
    fun provideUpdateAssignmentUseCase(
        repository: AssignmentRepository,
        scheduler: NotificationScheduler
    ): UpdateAssignmentUseCase {
        return UpdateAssignmentUseCase(repository, scheduler)
    }
    
    @Provides
    fun provideDeleteAssignmentUseCase(
        repository: AssignmentRepository,
        scheduler: NotificationScheduler
    ): DeleteAssignmentUseCase {
        return DeleteAssignmentUseCase(repository, scheduler)
    }
    
    @Provides
    fun provideGetAssignmentUseCase(
        repository: AssignmentRepository
    ): GetAssignmentUseCase {
        return GetAssignmentUseCase(repository)
    }
    
    @Provides
    fun provideGetAllAssignmentsUseCase(
        repository: AssignmentRepository
    ): GetAllAssignmentsUseCase {
        return GetAllAssignmentsUseCase(repository)
    }
    
    @Provides
    fun provideSearchAssignmentsUseCase(
        repository: AssignmentRepository
    ): SearchAssignmentsUseCase {
        return SearchAssignmentsUseCase(repository)
    }
    
    @Provides
    fun provideGetAnalyticsUseCase(
        repository: AssignmentRepository
    ): GetAnalyticsUseCase {
        return GetAnalyticsUseCase(repository)
    }
}
```

#### Manual DI Fallback (if Hilt fails)

```kotlin
object DependencyContainer {
    private lateinit var database: AppDatabase
    private lateinit var workManager: WorkManager
    private lateinit var notificationScheduler: NotificationScheduler
    private lateinit var assignmentRepository: AssignmentRepository
    private lateinit var userPreferencesRepository: UserPreferencesRepository
    
    fun initialize(context: Context) {
        database = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "assignment_tracker_db"
        ).build()
        
        workManager = WorkManager.getInstance(context)
        notificationScheduler = NotificationScheduler(context, workManager)
        assignmentRepository = AssignmentRepositoryImpl(database.assignmentDao())
        userPreferencesRepository = UserPreferencesRepository(context)
    }
    
    fun provideCreateAssignmentUseCase() = 
        CreateAssignmentUseCase(assignmentRepository, notificationScheduler)
    
    fun provideUpdateAssignmentUseCase() = 
        UpdateAssignmentUseCase(assignmentRepository, notificationScheduler)
    
    fun provideDeleteAssignmentUseCase() = 
        DeleteAssignmentUseCase(assignmentRepository, notificationScheduler)
    
    fun provideGetAssignmentUseCase() = 
        GetAssignmentUseCase(assignmentRepository)
    
    fun provideGetAllAssignmentsUseCase() = 
        GetAllAssignmentsUseCase(assignmentRepository)
    
    fun provideSearchAssignmentsUseCase() = 
        SearchAssignmentsUseCase(assignmentRepository)
    
    fun provideGetAnalyticsUseCase() = 
        GetAnalyticsUseCase(assignmentRepository)
    
    fun provideUserPreferencesRepository() = userPreferencesRepository
}
```


## Data Models

### Assignment Entity Details

| Field | Type | Description | Constraints |
|-------|------|-------------|-------------|
| id | Long | Primary key | Auto-generated |
| title | String | Assignment title | Required, non-blank |
| description | String | Detailed description | Optional |
| dueDate | LocalDate | Due date | Required |
| dueTime | LocalTime | Due time | Required |
| priority | Priority | Priority level | Enum: LOW, MEDIUM, HIGH, URGENT |
| status | Status | Current status | Enum: NOT_STARTED, IN_PROGRESS, DONE |
| createdAt | LocalDateTime | Creation timestamp | Auto-set |
| updatedAt | LocalDateTime | Last update timestamp | Auto-updated |

### User Preferences Model

| Field | Type | Description | Default |
|-------|------|-------------|---------|
| userName | String | Student name | "" |
| course | String | Course name | "" |
| academicYear | String | Academic year | "" |
| themeMode | ThemeMode | Theme preference | SYSTEM |
| hasCompletedOnboarding | Boolean | Onboarding status | false |

## Error Handling

### Error Handling Strategy

1. **Database Errors**: Caught at repository level, logged, and propagated as domain exceptions
2. **Validation Errors**: Handled in ViewModels with user-friendly error messages
3. **Worker Failures**: Logged and retried with exponential backoff
4. **UI Errors**: Displayed using Snackbar or error states in UI

### Error Types

```kotlin
sealed class AssignmentError : Exception() {
    data class DatabaseError(override val message: String) : AssignmentError()
    data class ValidationError(val field: String, override val message: String) : AssignmentError()
    data class NotFoundError(val id: Long) : AssignmentError()
}
```

### Error Handling in Repository

```kotlin
class AssignmentRepositoryImpl(private val dao: AssignmentDao) : AssignmentRepository {
    override suspend fun insertAssignment(assignment: Assignment): Long {
        return try {
            dao.insertAssignment(assignment)
        } catch (e: Exception) {
            throw AssignmentError.DatabaseError("Failed to insert assignment: ${e.message}")
        }
    }
    
    // Similar error handling for other operations
}
```

### Error Handling in ViewModels

```kotlin
val uiState: StateFlow<UiState> = repository.getAllAssignments()
    .map { assignments -> UiState.Success(assignments) }
    .catch { error ->
        emit(UiState.Error(error.message ?: "Unknown error"))
    }
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UiState.Loading
    )
```


## Testing Strategy

### Unit Tests

#### Repository Tests
- Test all CRUD operations
- Test search and filter queries
- Test error handling
- Use in-memory Room database
- Verify Flow emissions

```kotlin
@RunWith(AndroidJUnit4::class)
class AssignmentRepositoryTest {
    private lateinit var database: AppDatabase
    private lateinit var dao: AssignmentDao
    private lateinit var repository: AssignmentRepository
    
    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        
        dao = database.assignmentDao()
        repository = AssignmentRepositoryImpl(dao)
    }
    
    @After
    fun teardown() {
        database.close()
    }
    
    @Test
    fun insertAssignment_returnsId() = runTest {
        val assignment = createTestAssignment()
        val id = repository.insertAssignment(assignment)
        assertTrue(id > 0)
    }
    
    @Test
    fun getAllAssignments_emitsAllAssignments() = runTest {
        // Test implementation
    }
}
```

#### ViewModel Tests
- Test state transformations
- Test user actions
- Test error scenarios
- Use Turbine for Flow testing
- Mock use cases

```kotlin
@ExperimentalCoroutinesApi
class AssignmentListViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    
    private lateinit var getAllAssignmentsUseCase: GetAllAssignmentsUseCase
    private lateinit var searchAssignmentsUseCase: SearchAssignmentsUseCase
    private lateinit var deleteAssignmentUseCase: DeleteAssignmentUseCase
    private lateinit var viewModel: AssignmentListViewModel
    
    @Before
    fun setup() {
        getAllAssignmentsUseCase = mockk()
        searchAssignmentsUseCase = mockk()
        deleteAssignmentUseCase = mockk(relaxed = true)
        
        every { searchAssignmentsUseCase(any(), any(), any()) } returns 
            flowOf(emptyList())
        
        viewModel = AssignmentListViewModel(
            getAllAssignmentsUseCase,
            searchAssignmentsUseCase,
            deleteAssignmentUseCase
        )
    }
    
    @Test
    fun `updateSearchQuery updates state`() = runTest {
        viewModel.uiState.test {
            viewModel.updateSearchQuery("test")
            // Verify state changes
        }
    }
}
```

#### Use Case Tests
- Test business logic
- Test integration with repository
- Test notification scheduling
- Mock dependencies

```kotlin
class CreateAssignmentUseCaseTest {
    private lateinit var repository: AssignmentRepository
    private lateinit var notificationScheduler: NotificationScheduler
    private lateinit var useCase: CreateAssignmentUseCase
    
    @Before
    fun setup() {
        repository = mockk()
        notificationScheduler = mockk(relaxed = true)
        useCase = CreateAssignmentUseCase(repository, notificationScheduler)
    }
    
    @Test
    fun `invoke creates assignment and schedules notification`() = runTest {
        val assignment = createTestAssignment()
        coEvery { repository.insertAssignment(any()) } returns 1L
        
        val id = useCase(assignment)
        
        assertEquals(1L, id)
        coVerify { repository.insertAssignment(assignment) }
        verify { notificationScheduler.scheduleReminder(any()) }
    }
}
```


### Instrumentation Tests

#### Database Tests
- Test DAO operations with real database
- Test type converters
- Test migrations (future)

```kotlin
@RunWith(AndroidJUnit4::class)
class AssignmentDaoTest {
    private lateinit var database: AppDatabase
    private lateinit var dao: AssignmentDao
    
    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()
        dao = database.assignmentDao()
    }
    
    @After
    fun teardown() {
        database.close()
    }
    
    @Test
    fun insertAndGetAssignment() = runTest {
        val assignment = createTestAssignment()
        val id = dao.insertAssignment(assignment)
        
        val retrieved = dao.getAssignmentById(id)
        assertNotNull(retrieved)
        assertEquals(assignment.title, retrieved?.title)
    }
}
```

#### UI Tests
- Test screen rendering
- Test user interactions
- Test navigation
- Use Compose testing framework

```kotlin
@RunWith(AndroidJUnit4::class)
class AssignmentListScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @Test
    fun displaysList_whenAssignmentsAvailable() {
        val assignments = listOf(createTestAssignment())
        
        composeTestRule.setContent {
            AssignmentListScreen(
                uiState = AssignmentListUiState.Success(assignments),
                onNavigateToDetail = {},
                onNavigateToAdd = {},
                onDeleteAssignment = {}
            )
        }
        
        composeTestRule.onNodeWithText(assignments[0].title).assertIsDisplayed()
    }
    
    @Test
    fun clickAddButton_navigatesToDetail() {
        var navigatedToAdd = false
        
        composeTestRule.setContent {
            AssignmentListScreen(
                uiState = AssignmentListUiState.Success(emptyList()),
                onNavigateToDetail = {},
                onNavigateToAdd = { navigatedToAdd = true },
                onDeleteAssignment = {}
            )
        }
        
        composeTestRule.onNodeWithContentDescription("Add assignment").performClick()
        assertTrue(navigatedToAdd)
    }
}
```

#### Worker Tests
- Test notification scheduling
- Test work execution
- Use TestListenableWorkerBuilder

```kotlin
@RunWith(AndroidJUnit4::class)
class ReminderWorkerTest {
    private lateinit var context: Context
    
    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
    }
    
    @Test
    fun reminderWorker_showsNotification() = runTest {
        val inputData = workDataOf(
            "assignment_id" to 1L,
            "assignment_title" to "Test Assignment",
            "due_date" to "2024-12-31"
        )
        
        val worker = TestListenableWorkerBuilder<ReminderWorker>(context)
            .setInputData(inputData)
            .build()
        
        val result = worker.doWork()
        
        assertEquals(Result.success(), result)
    }
}
```

### Test Coverage Goals

- **Repository Layer**: 90%+ coverage
- **ViewModel Layer**: 85%+ coverage
- **Use Case Layer**: 90%+ coverage
- **UI Layer**: 70%+ coverage (focus on critical paths)
- **Overall**: 80%+ coverage

### Testing Tools

- **JUnit 4**: Unit test framework
- **MockK**: Mocking library for Kotlin
- **Turbine**: Flow testing library
- **Compose Test**: UI testing for Compose
- **Room Testing**: In-memory database for tests
- **WorkManager Testing**: TestListenableWorkerBuilder
- **Coroutines Test**: TestDispatcher and runTest


## UI Design Patterns

### Screen Composition Pattern

Each screen follows this pattern:

```kotlin
@Composable
fun AssignmentListScreen(
    viewModel: AssignmentListViewModel = hiltViewModel(),
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToAdd: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    AssignmentListContent(
        uiState = uiState,
        onSearchQueryChange = viewModel::updateSearchQuery,
        onNavigateToDetail = onNavigateToDetail,
        onNavigateToAdd = onNavigateToAdd
    )
}

@Composable
private fun AssignmentListContent(
    uiState: AssignmentListUiState,
    onSearchQueryChange: (String) -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToAdd: () -> Unit
) {
    when (uiState) {
        is AssignmentListUiState.Loading -> LoadingIndicator()
        is AssignmentListUiState.Success -> AssignmentList(...)
        is AssignmentListUiState.Error -> ErrorMessage(...)
    }
}
```

### Reusable Components

#### AssignmentCard

```kotlin
@Composable
fun AssignmentCard(
    assignment: Assignment,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = assignment.title,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = assignment.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                PriorityChip(priority = assignment.priority)
                StatusChip(status = assignment.status)
                Text(
                    text = formatDate(assignment.dueDate),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
```

#### FilterChip

```kotlin
@Composable
fun FilterChipGroup(
    selectedStatus: Status?,
    selectedPriority: Priority?,
    onStatusSelected: (Status?) -> Unit,
    onPrioritySelected: (Priority?) -> Unit
) {
    Column {
        Text("Status", style = MaterialTheme.typography.labelMedium)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(Status.values()) { status ->
                FilterChip(
                    selected = selectedStatus == status,
                    onClick = { 
                        onStatusSelected(if (selectedStatus == status) null else status)
                    },
                    label = { Text(status.name) }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text("Priority", style = MaterialTheme.typography.labelMedium)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(Priority.values()) { priority ->
                FilterChip(
                    selected = selectedPriority == priority,
                    onClick = { 
                        onPrioritySelected(if (selectedPriority == priority) null else priority)
                    },
                    label = { Text(priority.name) }
                )
            }
        }
    }
}
```

### Calendar Grid Component

```kotlin
@Composable
fun CalendarGrid(
    month: YearMonth,
    assignmentsByDate: Map<LocalDate, List<Assignment>>,
    onDateClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val firstDayOfMonth = month.atDay(1)
    val lastDayOfMonth = month.atEndOfMonth()
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7
    val daysInMonth = month.lengthOfMonth()
    
    Column(modifier = modifier) {
        // Month header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = month.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                style = MaterialTheme.typography.titleLarge
            )
        }
        
        // Day of week headers
        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
        
        // Calendar grid
        val totalCells = ((firstDayOfWeek + daysInMonth) / 7 + 1) * 7
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(totalCells) { index ->
                val dayNumber = index - firstDayOfWeek + 1
                if (dayNumber in 1..daysInMonth) {
                    val date = month.atDay(dayNumber)
                    CalendarDay(
                        date = date,
                        hasAssignments = assignmentsByDate.containsKey(date),
                        isToday = date == LocalDate.now(),
                        onClick = { onDateClick(date) }
                    )
                } else {
                    Spacer(modifier = Modifier.aspectRatio(1f))
                }
            }
        }
    }
}
```


### Board View Component

```kotlin
@Composable
fun BoardView(
    notStarted: List<Assignment>,
    inProgress: List<Assignment>,
    done: List<Assignment>,
    onAssignmentClick: (Assignment) -> Unit,
    onMoveToNextStatus: (Assignment) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        BoardColumn(
            title = "Not Started",
            assignments = notStarted,
            color = MaterialTheme.colorScheme.errorContainer,
            onAssignmentClick = onAssignmentClick,
            onMoveNext = onMoveToNextStatus
        )
        
        BoardColumn(
            title = "In Progress",
            assignments = inProgress,
            color = MaterialTheme.colorScheme.primaryContainer,
            onAssignmentClick = onAssignmentClick,
            onMoveNext = onMoveToNextStatus
        )
        
        BoardColumn(
            title = "Done",
            assignments = done,
            color = MaterialTheme.colorScheme.tertiaryContainer,
            onAssignmentClick = onAssignmentClick,
            onMoveNext = onMoveToNextStatus
        )
    }
}

@Composable
private fun BoardColumn(
    title: String,
    assignments: List<Assignment>,
    color: Color,
    onAssignmentClick: (Assignment) -> Unit,
    onMoveNext: (Assignment) -> Unit
) {
    Card(
        modifier = Modifier
            .width(280.dp)
            .fillMaxHeight(),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "$title (${assignments.size})",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(assignments) { assignment ->
                    BoardCard(
                        assignment = assignment,
                        onClick = { onAssignmentClick(assignment) },
                        onMoveNext = { onMoveNext(assignment) }
                    )
                }
            }
        }
    }
}
```

### Analytics Chart Component

```kotlin
@Composable
fun WeeklyCompletionChart(
    weeklyData: List<WeekData>,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.fillMaxWidth().height(200.dp)) {
        val barWidth = size.width / (weeklyData.size * 2)
        val maxValue = weeklyData.maxOfOrNull { it.count } ?: 1
        val scale = size.height / maxValue
        
        weeklyData.forEachIndexed { index, data ->
            val barHeight = data.count * scale
            val x = index * barWidth * 2 + barWidth / 2
            
            drawRect(
                color = Color.Blue,
                topLeft = Offset(x, size.height - barHeight),
                size = Size(barWidth, barHeight)
            )
            
            drawContext.canvas.nativeCanvas.drawText(
                data.weekLabel,
                x + barWidth / 2,
                size.height + 20f,
                android.graphics.Paint().apply {
                    textSize = 24f
                    textAlign = android.graphics.Paint.Align.CENTER
                }
            )
        }
    }
}

data class WeekData(
    val weekLabel: String,
    val count: Int
)
```

## Theme Configuration

### Material3 Theme

```kotlin
@Composable
fun AssignmentTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF6200EE),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFBB86FC),
    secondary = Color(0xFF03DAC6),
    onSecondary = Color.Black,
    error = Color(0xFFB00020),
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFBB86FC),
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF3700B3),
    secondary = Color(0xFF03DAC6),
    onSecondary = Color.Black,
    error = Color(0xFFCF6679),
    background = Color(0xFF121212),
    surface = Color(0xFF121212)
)
```


## Build Configuration

### Root build.gradle.kts

```kotlin
plugins {
    id("com.android.application") version "8.2.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.20" apply false
    id("com.google.dagger.hilt.android") version "2.48" apply false
    id("com.google.devtools.ksp") version "1.9.20-1.0.14" apply false
}
```

### app/build.gradle.kts

```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.example.assignmenttracker"
    compileSdk = 34
    
    defaultConfig {
        applicationId = "com.example.assignmenttracker"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }
    
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    
    kotlinOptions {
        jvmTarget = "17"
    }
    
    buildFeatures {
        compose = true
    }
    
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.4"
    }
    
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Compose
    implementation(platform("androidx.compose:compose-bom:2023.10.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.activity:activity-compose:1.8.1")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.2")
    
    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.5")
    
    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")
    
    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    
    // WorkManager
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    
    // Hilt
    implementation("com.google.dagger:hilt-android:2.48")
    ksp("com.google.dagger:hilt-compiler:2.48")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
    implementation("androidx.hilt:hilt-work:1.1.0")
    ksp("androidx.hilt:hilt-compiler:1.1.0")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    
    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("app.cash.turbine:turbine:1.0.0")
    testImplementation("io.mockk:mockk:1.13.8")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.10.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("androidx.room:room-testing:2.6.1")
    androidTestImplementation("androidx.work:work-testing:2.9.0")
    
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
```

## Performance Considerations

### Database Optimization
- Use indices on frequently queried columns (dueDate, status, priority)
- Implement pagination for large lists using Paging 3 (future enhancement)
- Use transactions for batch operations

### UI Performance
- Use `remember` and `derivedStateOf` to minimize recompositions
- Implement lazy loading for lists
- Use `key` parameter in LazyColumn for stable item identity
- Avoid heavy computations in composables

### Memory Management
- Use Flow instead of LiveData for better memory efficiency
- Cancel coroutines properly in ViewModels
- Use `stateIn` with `WhileSubscribed` to stop upstream flows when not needed

### Background Work
- Use WorkManager constraints to optimize battery usage
- Implement exponential backoff for failed work
- Use OneTimeWorkRequest for reminders (not periodic)

## Security Considerations

### Data Security
- All data stored locally in encrypted Room database (future: enable encryption)
- No network communication required
- No sensitive data collection

### Permissions
- POST_NOTIFICATIONS permission for Android 13+
- SCHEDULE_EXACT_ALARM permission for precise reminders

### Input Validation
- Sanitize all user inputs
- Validate date/time ranges
- Prevent SQL injection through parameterized queries (Room handles this)

## Accessibility

### Compose Accessibility
- Provide content descriptions for all interactive elements
- Support TalkBack screen reader
- Ensure sufficient color contrast ratios
- Support dynamic text sizing
- Provide semantic labels for custom components

```kotlin
@Composable
fun AccessibleButton(
    text: String,
    onClick: () -> Unit,
    contentDescription: String? = null
) {
    Button(
        onClick = onClick,
        modifier = Modifier.semantics {
            this.contentDescription = contentDescription ?: text
        }
    ) {
        Text(text)
    }
}
```

## Future Enhancements

1. **Cloud Sync**: Optional Firebase sync for multi-device support
2. **Attachments**: Support for file attachments to assignments
3. **Collaboration**: Share assignments with classmates
4. **Advanced Analytics**: More detailed charts and insights
5. **Widgets**: Home screen widgets for quick access
6. **Export**: Export assignments to PDF or CSV
7. **Recurring Assignments**: Support for recurring tasks
8. **Tags**: Custom tags for better organization
9. **Dark Mode Scheduling**: Auto-switch based on time
10. **Backup/Restore**: Local backup and restore functionality
