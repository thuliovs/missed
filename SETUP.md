# Missed App - Setup Instructions

## 🔧 Configuração Inicial

### 1. Firebase Setup

1. Acesse o [Firebase Console](https://console.firebase.google.com/)
2. Crie um novo projeto ou use um existente
3. Adicione um app Android com o package name: `com.thuliotech.missed`
4. Baixe o arquivo `google-services.json`
5. Substitua o arquivo `app/google-services.json` pelo arquivo baixado
6. No Firebase Console, ative:
   - **Authentication** (Email/Password e Google Sign-In)
   - **Cloud Firestore**
   - **Analytics**

### 2. Google Maps API Key

1. Acesse o [Google Cloud Console](https://console.cloud.google.com/)
2. Ative as seguintes APIs:
   - Maps SDK for Android
   - Places API
   - Geolocation API
3. Crie uma API Key
4. Restrinja a chave para Android apps com o package name: `com.thuliotech.missed`
5. Substitua `YOUR_GOOGLE_MAPS_API_KEY` no `AndroidManifest.xml` pela sua chave

### 3. Sync do Projeto

1. Abra o projeto no Android Studio
2. Clique em "Sync Project with Gradle Files"
3. Aguarde o download de todas as dependências

## 📦 Dependências Principais

- **Jetpack Compose** - UI moderna
- **Material 3** - Design System
- **Hilt** - Dependency Injection
- **Room** - Database local
- **Navigation Compose** - Navegação
- **Firebase** - Auth, Firestore, Analytics
- **Google Maps Compose** - Mapas
- **Geofencing API** - Monitoramento de localização eficiente
- **Accompanist** - Permissions
- **Coil** - Carregamento de imagens
- **DataStore** - Preferências

## 🏗️ Arquitetura

O projeto segue **Clean Architecture** com as seguintes camadas:

```
app/
├── data/           # Data sources, repositories
├── domain/         # Use cases, models
├── presentation/   # UI (Compose), ViewModels
└── di/            # Dependency Injection modules
```

## 🚀 Próximos Passos

1. Configurar Firebase e Google Maps (instruções acima)
2. Implementar estrutura de pastas (Clean Architecture)
3. Criar Design System com Material 3
4. Implementar fluxo de Onboarding
5. Implementar autenticação
6. Desenvolver features principais

## 📝 Notas

- minSdk: 26 (Android 8.0)
- targetSdk: 35 (Android 14)
- Linguagem: Kotlin
- UI: Jetpack Compose

