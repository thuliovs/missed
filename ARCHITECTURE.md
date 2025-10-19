# Missed App - Arquitetura

## 🏗️ Clean Architecture

O projeto segue os princípios de **Clean Architecture** com separação clara de responsabilidades em camadas.

```
┌─────────────────────────────────────────────────────────┐
│                    Presentation Layer                    │
│  (UI, ViewModels, Compose Screens, Navigation)          │
└────────────────────┬────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────┐
│                     Domain Layer                         │
│  (Use Cases, Business Logic, Domain Models, Interfaces) │
└────────────────────┬────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────┐
│                      Data Layer                          │
│  (Repositories, Data Sources, Room, Firebase, APIs)     │
└─────────────────────────────────────────────────────────┘
```

## 📁 Estrutura de Pastas

```
app/src/main/java/com/thuliotech/missed/
│
├── 📂 data/                    # Data Layer
│   ├── local/                  # Local data sources
│   │   ├── dao/               # Room DAOs
│   │   ├── entity/            # Room Entities
│   │   └── datastore/         # DataStore preferences
│   ├── remote/                 # Remote data sources
│   │   └── firebase/          # Firebase services
│   └── repository/             # Repository implementations
│
├── 📂 domain/                  # Domain Layer (Business Logic)
│   ├── model/                  # Domain models (pure Kotlin)
│   ├── repository/             # Repository interfaces
│   └── usecase/                # Use cases (business logic)
│
├── 📂 presentation/            # Presentation Layer (UI)
│   ├── onboarding/            # Onboarding flow screens
│   ├── auth/                  # Authentication screens
│   ├── home/                  # Home/Main screen
│   ├── map/                   # Map selection screen
│   ├── alarm/                 # Alarm screen
│   ├── components/            # Reusable UI components
│   ├── navigation/            # Navigation setup
│   └── theme/                 # Material 3 theme
│
├── 📂 di/                      # Dependency Injection (Hilt modules)
│
└── 📂 util/                    # Utilities, extensions, helpers
```

## 🔄 Fluxo de Dados

### Unidirecional Data Flow (UDF)

```
┌──────────┐
│   User   │
└────┬─────┘
     │ (1) User Action
     ▼
┌──────────────┐
│  UI (Screen) │
└────┬─────────┘
     │ (2) Event
     ▼
┌──────────────┐
│  ViewModel   │
└────┬─────────┘
     │ (3) Call Use Case
     ▼
┌──────────────┐
│   Use Case   │
└────┬─────────┘
     │ (4) Request Data
     ▼
┌──────────────┐
│  Repository  │
└────┬─────────┘
     │ (5) Fetch from Data Source
     ▼
┌──────────────┐
│ Data Source  │ (Room, Firebase, etc.)
└────┬─────────┘
     │ (6) Return Data
     ▼
┌──────────────┐
│  Repository  │
└────┬─────────┘
     │ (7) Map to Domain Model
     ▼
┌──────────────┐
│   Use Case   │
└────┬─────────┘
     │ (8) Return Result
     ▼
┌──────────────┐
│  ViewModel   │
└────┬─────────┘
     │ (9) Update State
     ▼
┌──────────────┐
│  UI (Screen) │ ← (10) Observe State & Recompose
└──────────────┘
```

## 📦 Camadas Detalhadas

### 1. Data Layer

**Responsabilidades:**
- Acesso a dados (local e remoto)
- Implementação de repositórios
- Cache e sincronização
- Mapeamento de DTOs para Domain Models

**Componentes:**
- **DAOs**: Interfaces Room para acesso ao banco de dados
- **Entities**: Tabelas do banco de dados (Room)
- **DataStore**: Preferências do usuário
- **Firebase Services**: Autenticação, Firestore
- **Repositories**: Implementações concretas

**Exemplo:**
```kotlin
// data/repository/AlarmRepositoryImpl.kt
class AlarmRepositoryImpl @Inject constructor(
    private val alarmDao: AlarmDao,
    private val firestore: FirebaseFirestore
) : AlarmRepository {
    override fun getAlarms(): Flow<List<Alarm>> {
        return alarmDao.getAllAlarms()
            .map { entities -> entities.map { it.toDomain() } }
    }
}
```

### 2. Domain Layer

**Responsabilidades:**
- Lógica de negócio pura (sem dependências Android)
- Definição de modelos de domínio
- Interfaces de repositórios
- Use Cases (casos de uso)

**Componentes:**
- **Models**: Classes de dados do domínio
- **Repository Interfaces**: Contratos para repositórios
- **Use Cases**: Operações de negócio específicas

**Exemplo:**
```kotlin
// domain/usecase/CreateAlarmUseCase.kt
class CreateAlarmUseCase @Inject constructor(
    private val repository: AlarmRepository
) {
    suspend operator fun invoke(
        latitude: Double,
        longitude: Double,
        radius: Int
    ): Result<Alarm> {
        // Business logic here
        return repository.createAlarm(latitude, longitude, radius)
    }
}
```

### 3. Presentation Layer

**Responsabilidades:**
- UI (Jetpack Compose)
- ViewModels (gerenciamento de estado)
- Navegação
- Tema e componentes visuais

**Componentes:**
- **Screens**: Telas Compose
- **ViewModels**: Gerenciamento de estado e lógica de apresentação
- **Components**: Componentes reutilizáveis
- **Navigation**: Configuração de rotas
- **Theme**: Material 3 theme

**Exemplo:**
```kotlin
// presentation/home/HomeViewModel.kt
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAlarmsUseCase: GetAlarmsUseCase
) : ViewModel() {
    
    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()
    
    init {
        loadAlarms()
    }
    
    private fun loadAlarms() {
        viewModelScope.launch {
            getAlarmsUseCase().collect { alarms ->
                _state.update { it.copy(alarms = alarms) }
            }
        }
    }
}
```

## 🔌 Dependency Injection (Hilt)

Todos os módulos Hilt ficam em `di/`:

- **AppModule**: Dependências gerais
- **DatabaseModule**: Room database
- **NetworkModule**: Firebase, APIs
- **RepositoryModule**: Bindings de repositórios
- **UseCaseModule**: Use cases (se necessário)

## 🎨 Material Design 3

O tema segue **Material You** com:
- Dynamic Color (cores do sistema)
- Typography scale
- Elevation e shadows
- Motion e animations

## 🧪 Testabilidade

A arquitetura facilita testes:

- **Domain Layer**: 100% testável (pure Kotlin)
- **ViewModels**: Testáveis com coroutines test
- **Repositories**: Mockáveis via interfaces
- **Use Cases**: Testáveis isoladamente

## 📱 Features Principais

1. **Onboarding**: Fluxo de boas-vindas
2. **Authentication**: Login/Register com Firebase
3. **Home**: Lista de alarmes salvos
4. **Map**: Seleção de ponto e raio
5. **Geofencing**: Monitoramento de localização
6. **Alarm**: Tela de alarme quando ativado
7. **Premium**: Features pagas
8. **Ads**: Monetização com AdMob

## 🔐 Segurança

- API Keys em `local.properties` (não commitadas)
- Firebase Security Rules
- Proguard/R8 para ofuscação
- Validação de dados no backend

## 🚀 Performance

- Geofencing API (baixo consumo de bateria)
- Room com Flow (reativo)
- Coil para cache de imagens
- WorkManager para tarefas em background
- Configuration cache do Gradle

---

**Próximos passos:** Implementar Design System com Material 3

