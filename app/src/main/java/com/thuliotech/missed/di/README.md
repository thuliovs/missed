# Dependency Injection (Hilt)

## 💉 Responsabilidade

Prover todas as dependências do aplicativo usando **Hilt** (Dagger).

## 📁 Estrutura

```
di/
├── AppModule.kt           # Dependências gerais
├── DatabaseModule.kt      # Room database
├── NetworkModule.kt       # Firebase, APIs
└── RepositoryModule.kt    # Bindings de repositórios
```

## 🎯 Princípios

- **Constructor Injection**: Preferir injeção via construtor
- **Scopes**: Usar scopes apropriados (@Singleton, @ViewModelScoped)
- **Interfaces**: Prover implementações via interfaces

## 📝 Exemplos

### AppModule
```kotlin
// di/AppModule.kt
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideContext(
        @ApplicationContext context: Context
    ): Context = context
    
    @Provides
    @Singleton
    fun provideDispatcherProvider(): DispatcherProvider {
        return DefaultDispatcherProvider()
    }
}
```

### DatabaseModule
```kotlin
// di/DatabaseModule.kt
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): MissedDatabase {
        return Room.databaseBuilder(
            context,
            MissedDatabase::class.java,
            "missed_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
    
    @Provides
    fun provideAlarmDao(database: MissedDatabase): AlarmDao {
        return database.alarmDao()
    }
}
```

### NetworkModule
```kotlin
// di/NetworkModule.kt
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }
    
    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }
    
    @Provides
    @Singleton
    fun provideFirebaseAnalytics(
        @ApplicationContext context: Context
    ): FirebaseAnalytics {
        return FirebaseAnalytics.getInstance(context)
    }
}
```

### RepositoryModule
```kotlin
// di/RepositoryModule.kt
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    @Singleton
    abstract fun bindAlarmRepository(
        impl: AlarmRepositoryImpl
    ): AlarmRepository
    
    @Binds
    @Singleton
    abstract fun bindUserRepository(
        impl: UserRepositoryImpl
    ): UserRepository
    
    @Binds
    @Singleton
    abstract fun bindLocationRepository(
        impl: LocationRepositoryImpl
    ): LocationRepository
}
```

## 🔍 Scopes

- **@Singleton**: Vive durante toda a aplicação
- **@ViewModelScoped**: Vive durante o ciclo de vida do ViewModel
- **@ActivityRetainedScoped**: Sobrevive a mudanças de configuração

## 📝 Uso nos ViewModels

```kotlin
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAlarmsUseCase: GetAlarmsUseCase,
    private val deleteAlarmUseCase: DeleteAlarmUseCase
) : ViewModel() {
    // ViewModel code
}
```

## 📝 Uso nas Activities

```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Activity code
    }
}
```

## 📝 Uso na Application

```kotlin
@HiltAndroidApp
class MissedApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Application code
    }
}
```

