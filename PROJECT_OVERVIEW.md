# Missed — Visão Geral do Projeto e Contexto Técnico

Este documento explica de forma detalhada o que o projeto faz, o porquê das decisões, como está organizado e como evoluir. A ideia é que qualquer pessoa nova no repositório consiga entender rapidamente o estado atual e os próximos passos.


## TL;DR
- App Android nativo em Kotlin com Jetpack Compose (Material 3), arquitetura MVVM + Clean Architecture, DI com Hilt e navegação type-safe.
- Objetivo: criar um app de “alarme por destino” (geofencing). O usuário define um ponto no mapa e o app o alerta quando estiver chegando à área.
- Status atual (principal):
  - Onboarding minimalista 100% Material 3
  - Navegação com rotas tipadas
  - DataStore para estado inicial (primeiro acesso) e login
  - Login por Google adiado (UI presente, fluxo desativado)
  - Home com FAB “+” (Material) levando à tela de Mapa
  - Tela de Mapa com SearchBar, botão de centralizar câmera, e menu inferior (Material). Google watermark visível por padrão.


## Stack Técnica
- Linguagem: Kotlin 2.0.21
- UI: Jetpack Compose + Material 3 (Compose BOM 2024.12.01)
- Arquitetura: MVVM + Clean Architecture
- Navegação: Navigation Compose com rotas tipadas via Kotlinx Serialization
- DI: Hilt
- Persistência leve: DataStore Preferences
- Localização e Mapas: Google Play Services (Location/Maps) + Google Maps Compose
- Firebase (parcial): Auth/Firestore/Analytics via Firebase BOM (ativado no Gradle; login Google postergado)
- Min/Target/Compile SDK: 26 / 36 / 36


## Estrutura de Pastas (alto nível)
```
app/src/main/java/com/thuliotech/missed/
│
├── data/                    # Data Layer
│   └── local/datastore/     # DataStore (preferências)
│
├── domain/                  # Domain Layer (contratos/uses cases)
│   └── repository/          # Interfaces de repositórios
│
├── presentation/            # Presentation Layer (UI)
│   ├── onboarding/          # Telas do onboarding
│   ├── home/                # Home (FAB ➜ Mapa)
│   ├── map/                 # Tela do mapa
│   └── navigation/          # Rotas e NavGraph
│
├── di/                      # Módulos Hilt
└── util/                    # Úteis/extensões (futuro)
```
Arquivos de topo úteis:
- ARCHITECTURE.md: visão de arquitetura (Clean Architecture)
- SETUP.md: passos de configuração local
- PROJECT_OVERVIEW.md (este arquivo): visão geral + contexto


## Configuração e Credenciais
- Firebase: `app/google-services.json` (o plugin google-services processa esse arquivo). A autenticação Google será retomada mais à frente.
- Google Maps/Places:
  - ÚNICA credencial commitada por decisão do projeto: `app/src/main/res/values/google_maps_api.xml`
  - Recurso string: `google_maps_key`
  - Valor atual: `AIzaSyCTgXTvtyxnSqODHsv4ZLUWkisvHNYrYes`
  - Manifest referencia a key via meta-data:
    ```xml
    <meta-data
        android:name="com.google.android.geo.API_KEY"
        android:value="@string/google_maps_key" />
    ```
  - Recomendação: restringir a chave no Google Cloud (por package `com.thuliotech.missed` e SHA-1) e habilitar apenas as APIs necessárias (Maps SDK for Android, Places API, etc.).

Observação importante sobre o watermark “Google”: o SDK posiciona automaticamente a marca d’água e não devemos ocultá-la ou reposicioná-la. A UI respeita margens/paddings para não cobrir a marca.


## Fluxo de Navegação (type-safe)
Rotas estão em `presentation/navigation/Screen.kt` como um sealed interface tipado com `@Serializable`. Exemplos: `Screen.Welcome`, `Screen.Home`, `Screen.Map`.

- `NavGraph.kt` resolve a rota inicial a partir do `NavigationViewModel`, que lê o DataStore para saber se é o primeiro acesso.
- Onboarding: Welcome ➜ Explanation ➜ Benefits ➜ Login (com opção “Pular”)
- Após concluir onboarding (login adiado), navega para `Screen.Home`.
- Na Home, o FAB “+” navega para `Screen.Map`.


## Estado do Onboarding e Login
- DataStore (`PreferencesManager`) guarda:
  - `isFirstLaunch` (default true)
  - `isLoggedIn` (default false)
- `NavigationViewModel` determina a tela inicial com base nesses valores.
- Login Google: fluxo adiado, UI presente; quando retomarmos, integraremos Credential Manager e Places (se aplicável).


## Home
- `HomeScreen` usa `Scaffold` com **FloatingActionButton** (Material 3 padrão). A ação do FAB navega para o Mapa.
- Mantemos a tela Home simples por enquanto; listas de alarmes virão na próxima fase.


## Tela de Mapa (UI e Comportamento)
Arquivo: `presentation/map/MapScreen.kt`.

Layout e decisões (100% Material):
1. Plano de fundo: `GoogleMap` (Maps Compose) com `rememberCameraPositionState`.
2. Barra de pesquisa no topo: `SearchBar` (Material 3). Hoje está “local” (ainda sem Places Autocomplete) — placeholder “Para onde vamos?”.
3. Botão de recentralizar: `SmallFloatingActionButton` com ícone `MyLocation` alinhado no topo/direita. Usa `rememberCoroutineScope` + `cameraPositionState.animate(...)`.
4. Menu inferior: `Surface` com `shape = MaterialTheme.shapes.extraLarge`, texto de instrução e botão “Cancelar”.
5. Content padding do mapa: aumentamos o `contentPadding` para que o menu inferior não sobreponha controles do mapa nem o watermark “Google”.

Próximas melhorias planejadas:
- Integrar Places Autocomplete (campo de busca real com sugestões e seleção).
- Adicionar marcador central/preview para “ponto selecionado” e um botão de confirmação (Material) para salvar o destino.
- Pedir permissões de localização com Accompanist e exibir posição do usuário quando autorizada.


## Dependências e Catálogo de Versões
- Catálogo: `gradle/libs.versions.toml` centraliza versões.
- Principais libs em uso:
  - `androidx.compose.material3:material3`
  - `androidx.navigation:navigation-compose`
  - Hilt: `com.google.dagger:hilt-android`, `androidx.hilt:hilt-navigation-compose`
  - Maps: `com.google.android.gms:play-services-maps`, `com.google.maps.android:maps-compose`
  - Location: `com.google.android.gms:play-services-location`
  - Firebase (via BOM): Auth, Firestore, Analytics
  - DataStore: `androidx.datastore:datastore-preferences`

Esse modelo (BOM + catálogo) mantém versões consistentes e atualizações seguras.


## Build e Execução
- Gradle Plugin Android: 8.13.0
- Kotlin: 2.0.21
- JDK alvo: 11 (configurado em `compileOptions`/`kotlinOptions`)
- Comandos comuns:
  - Build debug: `./gradlew :app:assembleDebug`
  - Testes (futuros): `./gradlew test` / `connectedAndroidTest`

Problemas de build já tratados:
- Conflito de versão do Firebase BOM corrigido para usar o catálogo (`implementation(platform(libs.firebase.bom)))`).
- Referência segura ao `default_web_client_id` (quando o google-services ainda não está configurado) feita via lookup dinâmico para evitar erros de compilação.


## Padrões de Código e UI
- Material 3 “puro”: somente componentes do Material Theme (sem UI custom fora do Material). Foi uma exigência explícita do projeto.
- Navegação type-safe (sem strings mágicas), ViewModels com `StateFlow`, camadas separadas (Clean Architecture).
- Evitar singletons manuais: usar Hilt para dependências.
- Sempre que possível, previews em Compose (tema claro/escuro).


## Roadmap (alto nível)
1. Onboarding (feito)
2. Navegação type-safe (feito)
3. Tema e design system Material 3 (feito)
4. Home com FAB (feito)
5. Tela de Mapa (UI inicial) (feito)
6. Permissões de localização e “minha localização” (próximo)
7. Places Autocomplete no SearchBar (próximo)
8. Seleção de ponto/raio + confirmação (próximo)
9. Geofencing (entrada/saída de região) e alarmes (pendente)
10. Persistência Room (alarmes salvos) (pendente)
11. Autenticação (retomar Google Sign-In e estados) (pendente)
12. Ads e Premium (pendente)


## Como contribuir/estender
- Novas telas: criar em `presentation/<feature>` e adicionar uma entrada em `Screen` + `NavGraph`.
- Novo estado persistente: DataStore via `PreferencesManager` ou Room (quando apropriado).
- Dependência nova: adicionar no `libs.versions.toml` (versões) e usar as aliases em `build.gradle.kts`.
- Testes: priorizar unit tests em domain e ViewModels; para UI, usar testes de Compose.


## Considerações de Segurança
- A única credencial commitada é a key do Google Maps/Places em `values/google_maps_api.xml`, por decisão explícita de projeto. Essa key deve estar restrita no Google Cloud (SHA-1/256 da app, package name) e somente para as APIs necessárias.
- Firebase: `google-services.json` deve ser obtido diretamente do Console do Firebase para cada ambiente.
- ProGuard/R8 ativos no release; evitar logs sensíveis em produção.

---

Se algo aqui não estiver claro ou se precisar de exemplos mais específicos, abra uma issue ou peça um tour guiado do código. 😉

