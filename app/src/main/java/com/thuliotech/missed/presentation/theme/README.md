# Design System - Missed App

## 🎨 Visão Geral

O Design System do Missed segue **Material Design 3** (Material You) com suporte a:
- ✅ Dynamic Color (cores do sistema no Android 12+)
- ✅ Tema claro e escuro
- ✅ Variações de contraste (medium/high) para acessibilidade
- ✅ Cores customizadas estendidas
- ✅ Sistema de espaçamento consistente
- ✅ Dimensões padronizadas
- ✅ Shapes arredondados

---

## 📁 Arquivos

```
theme/
├── Color.kt           # Paleta de cores (gerada pelo Material Theme Builder)
├── Theme.kt           # Tema principal e ColorSchemes
├── Type.kt            # Tipografia
├── Shape.kt           # Shapes (bordas arredondadas)
├── Spacing.kt         # Sistema de espaçamento
└── Dimensions.kt      # Dimensões de componentes
```

---

## 🎨 Cores

As cores foram geradas pelo **Material Theme Builder** e incluem:

### ColorScheme Padrão
- `primary`, `onPrimary`, `primaryContainer`, `onPrimaryContainer`
- `secondary`, `onSecondary`, `secondaryContainer`, `onSecondaryContainer`
- `tertiary`, `onTertiary`, `tertiaryContainer`, `onTertiaryContainer`
- `error`, `onError`, `errorContainer`, `onErrorContainer`
- `background`, `onBackground`
- `surface`, `onSurface`, `surfaceVariant`, `onSurfaceVariant`
- `outline`, `outlineVariant`
- E muito mais...

### Cores Customizadas
6 cores customizadas adicionais disponíveis via `ExtendedColorScheme`:
- `customColor1` a `customColor6`

### Uso
```kotlin
@Composable
fun MyScreen() {
    Surface(
        color = MaterialTheme.colorScheme.primary
    ) {
        Text(
            text = "Hello",
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}
```

---

## 📏 Espaçamento

Sistema baseado em múltiplos de **4dp**:

```kotlin
MaterialTheme.spacing.none         // 0dp
MaterialTheme.spacing.extraSmall   // 4dp
MaterialTheme.spacing.small        // 8dp
MaterialTheme.spacing.medium       // 16dp
MaterialTheme.spacing.large        // 24dp
MaterialTheme.spacing.extraLarge   // 32dp
MaterialTheme.spacing.huge         // 48dp
MaterialTheme.spacing.massive      // 64dp
```

### Uso
```kotlin
Column(
    modifier = Modifier.padding(MaterialTheme.spacing.medium)
) {
    Text("Title")
    Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
    Text("Subtitle")
}
```

---

## 📐 Dimensões

Tamanhos padronizados para componentes:

### Ícones
```kotlin
MaterialTheme.dimensions.iconSmall       // 16dp
MaterialTheme.dimensions.iconMedium      // 24dp
MaterialTheme.dimensions.iconLarge       // 32dp
MaterialTheme.dimensions.iconExtraLarge  // 48dp
```

### Botões
```kotlin
MaterialTheme.dimensions.buttonHeight       // 48dp
MaterialTheme.dimensions.buttonHeightSmall  // 36dp
MaterialTheme.dimensions.buttonHeightLarge  // 56dp
```

### Cards
```kotlin
MaterialTheme.dimensions.cardElevation      // 2dp
MaterialTheme.dimensions.cardCornerRadius   // 12dp
```

### Avatares
```kotlin
MaterialTheme.dimensions.avatarSmall   // 32dp
MaterialTheme.dimensions.avatarMedium  // 48dp
MaterialTheme.dimensions.avatarLarge   // 64dp
```

### Outros
```kotlin
MaterialTheme.dimensions.textFieldHeight        // 56dp
MaterialTheme.dimensions.topAppBarHeight        // 64dp
MaterialTheme.dimensions.bottomNavHeight        // 80dp
MaterialTheme.dimensions.mapMarkerSize          // 40dp
```

---

## 🔷 Shapes

Bordas arredondadas seguindo Material 3:

```kotlin
MaterialTheme.shapes.extraSmall  // 4dp  - Chips, small buttons
MaterialTheme.shapes.small       // 8dp  - Buttons, text fields
MaterialTheme.shapes.medium      // 12dp - Cards, dialogs
MaterialTheme.shapes.large       // 16dp - Bottom sheets
MaterialTheme.shapes.extraLarge  // 28dp - Full screen dialogs
```

### Uso
```kotlin
Card(
    shape = MaterialTheme.shapes.medium
) {
    // Content
}
```

---

## 🔤 Tipografia

Usa a tipografia padrão do Material 3:

```kotlin
MaterialTheme.typography.displayLarge
MaterialTheme.typography.displayMedium
MaterialTheme.typography.displaySmall
MaterialTheme.typography.headlineLarge
MaterialTheme.typography.headlineMedium
MaterialTheme.typography.headlineSmall
MaterialTheme.typography.titleLarge
MaterialTheme.typography.titleMedium
MaterialTheme.typography.titleSmall
MaterialTheme.typography.bodyLarge
MaterialTheme.typography.bodyMedium
MaterialTheme.typography.bodySmall
MaterialTheme.typography.labelLarge
MaterialTheme.typography.labelMedium
MaterialTheme.typography.labelSmall
```

---

## 🧩 Componentes Reutilizáveis

### MissedButton
```kotlin
MissedButton(
    text = "Continuar",
    onClick = { /* action */ }
)

MissedOutlinedButton(
    text = "Voltar",
    onClick = { /* action */ }
)

MissedTextButton(
    text = "Pular",
    onClick = { /* action */ }
)
```

### MissedTextField
```kotlin
MissedTextField(
    value = email,
    onValueChange = { email = it },
    label = "Email",
    placeholder = "Digite seu email",
    isError = emailError,
    errorMessage = "Email inválido"
)
```

### LoadingIndicator
```kotlin
// Tela cheia
LoadingIndicator()

// Inline
SmallLoadingIndicator()
```

---

## 🎯 Uso do Tema

### No MainActivity
```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MissedTheme {
                // Seu app aqui
            }
        }
    }
}
```

### Em Previews
```kotlin
@Preview(showBackground = true)
@Composable
fun MyScreenPreview() {
    MissedTheme {
        MyScreen()
    }
}
```

### Forçar tema escuro/claro
```kotlin
MissedTheme(
    darkTheme = true,  // Força tema escuro
    dynamicColor = false  // Desabilita cores dinâmicas
) {
    // Content
}
```

---

## 🌈 Dynamic Color (Material You)

No Android 12+, o app automaticamente usa as cores do wallpaper do usuário.

Para desabilitar:
```kotlin
MissedTheme(dynamicColor = false) {
    // Usa as cores definidas no Color.kt
}
```

---

## ♿ Acessibilidade

O tema inclui variações de contraste:
- `mediumContrastLightColorScheme`
- `highContrastLightColorScheme`
- `mediumContrastDarkColorScheme`
- `highContrastDarkColorScheme`

Atualmente não estão sendo usadas, mas podem ser ativadas no futuro baseado nas preferências do sistema.

---

## 📝 Boas Práticas

1. **Sempre use o tema**: Nunca use cores hardcoded
   ```kotlin
   // ❌ Errado
   Text(text = "Hello", color = Color.Blue)
   
   // ✅ Correto
   Text(text = "Hello", color = MaterialTheme.colorScheme.primary)
   ```

2. **Use o sistema de espaçamento**:
   ```kotlin
   // ❌ Errado
   Modifier.padding(16.dp)
   
   // ✅ Correto
   Modifier.padding(MaterialTheme.spacing.medium)
   ```

3. **Use componentes reutilizáveis**:
   ```kotlin
   // ❌ Evite criar botões do zero
   Button(onClick = {}) { Text("Click") }
   
   // ✅ Use os componentes do Design System
   MissedButton(text = "Click", onClick = {})
   ```

4. **Respeite as dimensões**:
   ```kotlin
   // ✅ Correto
   Icon(
       imageVector = Icons.Default.Add,
       contentDescription = null,
       modifier = Modifier.size(MaterialTheme.dimensions.iconMedium)
   )
   ```

---

## 🔄 Atualizando o Tema

Se precisar atualizar as cores:

1. Acesse [Material Theme Builder](https://material-foundation.github.io/material-theme-builder/)
2. Crie/edite seu tema
3. Exporte como **Theme.kt**
4. Substitua o conteúdo de `Color.kt` e `Theme.kt`
5. Mantenha a função `MissedTheme` e os sistemas customizados (Spacing, Dimensions)

