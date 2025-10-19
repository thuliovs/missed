# Data Layer

## 📦 Responsabilidade

Gerenciar todas as fontes de dados (local e remota) e implementar os repositórios definidos na camada de domínio.

## 📁 Estrutura

```
data/
├── local/              # Fontes de dados locais
│   ├── dao/           # Room DAOs (Data Access Objects)
│   ├── entity/        # Room Entities (tabelas do banco)
│   └── datastore/     # DataStore (preferências)
├── remote/             # Fontes de dados remotas
│   ├── firebase/      # Firebase Auth, Firestore
│   └── dto/           # Data Transfer Objects
└── repository/         # Implementações dos repositórios
```

## 🎯 Princípios

- **Single Source of Truth**: Room é a fonte única de verdade
- **Offline First**: Dados locais primeiro, sincronização depois
- **Mapeamento**: DTOs/Entities → Domain Models

## 📝 Exemplo

```kotlin
// repository/AlarmRepositoryImpl.kt
@Singleton
class AlarmRepositoryImpl @Inject constructor(
    private val alarmDao: AlarmDao,
    private val firestore: FirebaseFirestore
) : AlarmRepository {
    
    override fun getAlarms(): Flow<List<Alarm>> {
        return alarmDao.getAllAlarms()
            .map { entities -> entities.map { it.toDomainModel() } }
    }
    
    override suspend fun createAlarm(alarm: Alarm): Result<Unit> {
        return try {
            alarmDao.insert(alarm.toEntity())
            firestore.collection("alarms").add(alarm.toDto())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

