# Presentation Layer

## 🎨 Responsabilidade

Gerenciar a interface do usuário (UI) com **Jetpack Compose** e **Material Design 3**.

## 📁 Estrutura

```
presentation/
├── onboarding/         # Fluxo de onboarding (4 telas)
├── auth/               # Autenticação
│   ├── login/         # Tela de login
│   └── register/      # Tela de registro
├── home/               # Tela principal (lista de alarmes)
├── map/                # Seleção de localização no mapa
├── alarm/              # Tela de alarme ativo
├── settings/           # Configurações do app
├── components/         # Componentes reutilizáveis
├── navigation/         # Configuração de navegação
└── theme/              # Material 3 theme
```

## 🎯 Princípios

- **MVVM**: Model-View-ViewModel
- **Unidirectional Data Flow**: Estado flui em uma direção
- **Stateless Composables**: Composables sem estado interno
- **Single Source of Truth**: ViewModel é a fonte de verdade

## 📝 Estrutura de uma Feature

Cada feature segue este padrão:

```
feature/
├── FeatureScreen.kt        # Composable da tela
├── FeatureViewModel.kt     # ViewModel
├── FeatureState.kt         # Estado da UI
└── FeatureEvent.kt         # Eventos do usuário
```

## 📝 Exemplo Completo

### State
```kotlin
// home/HomeState.kt
data class HomeState(
    val alarms: List<Alarm> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
```

### Event
```kotlin
// home/HomeEvent.kt
sealed interface HomeEvent {
    data object LoadAlarms : HomeEvent
    data class DeleteAlarm(val id: String) : HomeEvent
    data class ToggleAlarm(val id: String) : HomeEvent
}
```

### ViewModel
```kotlin
// home/HomeViewModel.kt
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAlarmsUseCase: GetAlarmsUseCase,
    private val deleteAlarmUseCase: DeleteAlarmUseCase
) : ViewModel() {
    
    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()
    
    init {
        loadAlarms()
    }
    
    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.LoadAlarms -> loadAlarms()
            is HomeEvent.DeleteAlarm -> deleteAlarm(event.id)
            is HomeEvent.ToggleAlarm -> toggleAlarm(event.id)
        }
    }
    
    private fun loadAlarms() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getAlarmsUseCase()
                .catch { e ->
                    _state.update { 
                        it.copy(isLoading = false, error = e.message) 
                    }
                }
                .collect { alarms ->
                    _state.update { 
                        it.copy(alarms = alarms, isLoading = false) 
                    }
                }
        }
    }
    
    private fun deleteAlarm(id: String) {
        viewModelScope.launch {
            deleteAlarmUseCase(id)
        }
    }
}
```

### Screen
```kotlin
// home/HomeScreen.kt
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToMap: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    HomeContent(
        state = state,
        onEvent = viewModel::onEvent,
        onNavigateToMap = onNavigateToMap
    )
}

@Composable
private fun HomeContent(
    state: HomeState,
    onEvent: (HomeEvent) -> Unit,
    onNavigateToMap: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Meus Alarmes") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToMap) {
                Icon(Icons.Default.Add, contentDescription = "Novo Alarme")
            }
        }
    ) { padding ->
        when {
            state.isLoading -> LoadingIndicator()
            state.error != null -> ErrorMessage(state.error)
            state.alarms.isEmpty() -> EmptyState()
            else -> AlarmList(
                alarms = state.alarms,
                onDelete = { onEvent(HomeEvent.DeleteAlarm(it)) }
            )
        }
    }
}
```

## 🎨 Material Design 3

Todos os componentes seguem Material 3:

```kotlin
// theme/Theme.kt
@Composable
fun MissedTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> darkColorScheme()
        else -> lightColorScheme()
    }
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
```

## 🧭 Navegação

```kotlin
// navigation/NavGraph.kt
@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Onboarding
    ) {
        composable<Screen.Onboarding> {
            OnboardingScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home) {
                        popUpTo(Screen.Onboarding) { inclusive = true }
                    }
                }
            )
        }
        
        composable<Screen.Home> {
            HomeScreen(
                onNavigateToMap = {
                    navController.navigate(Screen.Map)
                }
            )
        }
    }
}
```

