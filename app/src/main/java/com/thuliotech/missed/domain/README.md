# Domain Layer

## 🎯 Responsabilidade

Contém a **lógica de negócio pura** do aplicativo. Esta camada não deve ter dependências Android.

## 📁 Estrutura

```
domain/
├── model/              # Modelos de domínio (pure Kotlin)
├── repository/         # Interfaces dos repositórios
└── usecase/            # Casos de uso (business logic)
    ├── alarm/         # Use cases de alarmes
    ├── auth/          # Use cases de autenticação
    ├── location/      # Use cases de localização
    └── user/          # Use cases de usuário
```

## 🎯 Princípios

- **Pure Kotlin**: Sem dependências Android
- **Single Responsibility**: Cada use case faz uma coisa
- **Testável**: 100% testável com unit tests
- **Dependency Inversion**: Depende de abstrações (interfaces)

## 📝 Exemplos

### Model
```kotlin
// model/Alarm.kt
data class Alarm(
    val id: String,
    val latitude: Double,
    val longitude: Double,
    val radius: Int,
    val isActive: Boolean,
    val createdAt: Long,
    val name: String? = null
)
```

### Repository Interface
```kotlin
// repository/AlarmRepository.kt
interface AlarmRepository {
    fun getAlarms(): Flow<List<Alarm>>
    suspend fun createAlarm(alarm: Alarm): Result<Unit>
    suspend fun deleteAlarm(id: String): Result<Unit>
    suspend fun updateAlarm(alarm: Alarm): Result<Unit>
}
```

### Use Case
```kotlin
// usecase/alarm/CreateAlarmUseCase.kt
class CreateAlarmUseCase @Inject constructor(
    private val repository: AlarmRepository
) {
    suspend operator fun invoke(
        latitude: Double,
        longitude: Double,
        radius: Int,
        name: String? = null
    ): Result<Alarm> {
        // Validação de negócio
        if (radius < 50 || radius > 5000) {
            return Result.failure(InvalidRadiusException())
        }
        
        val alarm = Alarm(
            id = UUID.randomUUID().toString(),
            latitude = latitude,
            longitude = longitude,
            radius = radius,
            isActive = true,
            createdAt = System.currentTimeMillis(),
            name = name
        )
        
        return repository.createAlarm(alarm)
            .map { alarm }
    }
}
```

## 🧪 Testabilidade

```kotlin
class CreateAlarmUseCaseTest {
    
    @Test
    fun `should fail when radius is too small`() = runTest {
        val useCase = CreateAlarmUseCase(mockRepository)
        
        val result = useCase(0.0, 0.0, radius = 10)
        
        assertTrue(result.isFailure)
    }
}
```

