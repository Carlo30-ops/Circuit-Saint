# Guía de Publicación en Tiendas Oficiales

## Circuit Saint - Tienda Virtual

### Índice
1. [Preparación para Google Play Store](#google-play-store)
2. [Preparación para Apple App Store](#apple-app-store)
3. [Optimizaciones de Batería y Memoria](#optimizaciones)
4. [Checklist Pre-Publicación](#checklist)

---

## Google Play Store

### Paso 1: Preparar la Aplicación

#### 1.1 Generar Keystore
```bash
keytool -genkey -v -keystore circuit-saint-release.keystore -alias circuit_saint -keyalg RSA -keysize 2048 -validity 10000
```

#### 1.2 Configurar build.gradle para Release
```kotlin
android {
    signingConfigs {
        release {
            storeFile file('circuit-saint-release.keystore')
            storePassword 'TU_PASSWORD'
            keyAlias 'circuit_saint'
            keyPassword 'TU_PASSWORD'
        }
    }
    
    buildTypes {
        release {
            signingConfig signingConfigs.release
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}
```

#### 1.3 Generar APK/AAB
- **APK**: `./gradlew assembleRelease`
- **AAB (Recomendado)**: `./gradlew bundleRelease`

### Paso 2: Crear Cuenta de Desarrollador

1. Visitar: https://play.google.com/console
2. Pagar tarifa única de $25 USD
3. Completar información de cuenta

### Paso 3: Crear la Aplicación en Play Console

1. **Información de la App**:
   - Nombre: Circuit Saint
   - Idioma predeterminado: Español
   - Tipo de app: Aplicación
   - Gratis o de pago: Gratis

2. **Contenido de la app**:
   - Categoría: Compras
   - Etiquetas: Tienda, E-commerce, Compras

3. **Privacidad**:
   - Política de privacidad (URL requerida)
   - Declaración de permisos

### Paso 4: Preparar Assets

#### 4.1 Icono de la Aplicación
- **Tamaño**: 512x512 px (PNG, sin transparencia)
- **Formato**: PNG
- **Fondo**: Color sólido

#### 4.2 Capturas de Pantalla
- **Mínimo**: 2 capturas
- **Recomendado**: 4-8 capturas
- **Tamaños requeridos**:
  - Teléfono: 1080x1920 px o superior
  - Tableta (opcional): 1200x1920 px o superior

#### 4.3 Imagen Promocional
- **Tamaño**: 1024x500 px
- **Formato**: JPG o PNG 24 bits

#### 4.4 Video Promocional (Opcional)
- **Duración**: 30 segundos máximo
- **Formato**: YouTube URL

### Paso 5: Configurar Versión

1. **Versión de la app**:
   - Version Code: 1 (incrementar en cada actualización)
   - Version Name: "1.0"

2. **Versión de Android**:
   - MinSdk: 24 (Android 7.0)
   - TargetSdk: 34 (Android 14)

### Paso 6: Política de Privacidad

Crear y alojar política de privacidad que incluya:
- Qué datos se recopilan
- Cómo se usan los datos
- Permisos solicitados y su uso
- Información de contacto

**Ejemplo de URL**: `https://circuitsaint.com/privacy-policy`

### Paso 7: Probar la App

1. **Internal Testing**: Probar con equipo interno
2. **Closed Testing**: Beta con usuarios seleccionados
3. **Open Testing**: Beta pública (opcional)

### Paso 8: Enviar para Revisión

1. Completar todos los campos requeridos
2. Subir AAB firmado
3. Configurar contenido para diferentes países
4. Establecer precio (gratis o de pago)
5. Enviar para revisión

**Tiempo de revisión**: 1-3 días hábiles

---

## Apple App Store

### Paso 1: Requisitos Previos

1. **Cuenta de Desarrollador Apple**:
   - Costo: $99 USD/año
   - Visitar: https://developer.apple.com

2. **Mac con Xcode**:
   - Versión mínima: Xcode 14+
   - macOS 12.0 o superior

### Paso 2: Convertir Proyecto Android a iOS

**Opción A: Usar Kotlin Multiplatform Mobile (KMM)**
- Compartir lógica de negocio
- UI nativa para cada plataforma

**Opción B: Recrear en Swift/SwiftUI**
- Reimplementar toda la aplicación
- Mejor rendimiento nativo

### Paso 3: Preparar para App Store Connect

1. **Crear App ID**:
   - Identificador: `com.circuitsaint.app`
   - Servicios habilitados: Push Notifications (si aplica)

2. **Certificados y Provisioning Profiles**:
   - Certificado de distribución
   - Provisioning profile para App Store

3. **Archivar la App**:
   - En Xcode: Product → Archive
   - Validar el archivo
   - Subir a App Store Connect

### Paso 4: App Store Connect

1. **Crear Nueva App**:
   - Nombre: Circuit Saint
   - Idioma principal: Español
   - SKU: `circuitsaint-001`
   - Bundle ID: `com.circuitsaint.app`

2. **Información de la App**:
   - Categoría: Shopping
   - Subcategoría: E-commerce
   - Precio: Gratis

3. **Assets Requeridos**:
   - Icono: 1024x1024 px
   - Capturas: Mínimo 3.5" y 5.5" iPhone
   - Descripción: Hasta 4000 caracteres
   - Palabras clave: Hasta 100 caracteres

### Paso 5: Revisión de Apple

- **Tiempo**: 1-7 días
- **Criterios**: Guidelines de App Store
- **Rechazos comunes**: Funcionalidad incompleta, políticas de privacidad

---

## Optimizaciones de Batería y Memoria

### Implementadas en el Proyecto

#### 1. **Optimización de Batería**

```kotlin
// En PerformanceOptimizer.kt
- Detección de batería baja
- Liberación de recursos en onPause()
- Uso eficiente de WakeLocks
- Reducción de animaciones en batería baja
```

#### 2. **Optimización de Memoria**

```kotlin
// Gestión de memoria
- ViewBinding para evitar memory leaks
- Lifecycle-aware components
- Limpieza automática de recursos
- Optimización de imágenes
- Uso de ProGuard/R8 para minificación
```

#### 3. **Mejores Prácticas Implementadas**

- ✅ **Lazy Loading**: RecyclerView con paginación
- ✅ **Image Optimization**: Compresión de imágenes
- ✅ **Background Tasks**: Coroutines con scope limitado
- ✅ **Database**: Room con índices optimizados
- ✅ **Network**: Caché de datos cuando sea posible

### Configuración de ProGuard

Archivo: `proguard-rules.pro`

```proguard
# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Gson (si se usa)
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }

# Google Maps
-keep class com.google.android.gms.maps.** { *; }
-keep interface com.google.android.gms.maps.** { *; }
```

---

## Checklist Pre-Publicación

### Funcionalidad
- [ ] Todas las pantallas funcionan correctamente
- [ ] Carrito de compras funcional
- [ ] Escáner QR operativo
- [ ] Mapa muestra ubicación correcta
- [ ] Formulario envía datos correctamente
- [ ] Sin crashes en pruebas

### Diseño
- [ ] Icono de app creado (512x512)
- [ ] Capturas de pantalla preparadas
- [ ] Imagen promocional lista
- [ ] Diseño responsive

### Legal
- [ ] Política de privacidad publicada
- [ ] Términos y condiciones (si aplica)
- [ ] Permisos justificados

### Técnico
- [ ] APK/AAB firmado correctamente
- [ ] Version code incrementado
- [ ] ProGuard configurado
- [ ] Sin warnings críticos
- [ ] Pruebas en diferentes dispositivos

### Contenido
- [ ] Descripción de la app completa
- [ ] Screenshots descriptivos
- [ ] Categoría correcta
- [ ] Palabras clave relevantes

### Testing
- [ ] Pruebas internas completadas
- [ ] Beta testing realizado
- [ ] Feedback de usuarios incorporado

---

## Recursos Adicionales

### Documentación Oficial
- [Google Play Console](https://support.google.com/googleplay/android-developer)
- [Apple Developer](https://developer.apple.com/documentation)
- [Android Developer](https://developer.android.com)

### Herramientas
- [App Bundle Tool](https://developer.android.com/studio/command-line/bundletool)
- [Play Console App](https://play.google.com/store/apps/details?id=com.google.android.apps.playconsole)
- [TestFlight](https://developer.apple.com/testflight/) (iOS)

### Comunidades
- [Stack Overflow - Android](https://stackoverflow.com/questions/tagged/android)
- [Reddit r/androiddev](https://www.reddit.com/r/androiddev/)
- [Android Developers Community](https://www.reddit.com/r/androiddev/)

---

## Contacto y Soporte

Para dudas sobre la publicación:
- Email: soporte@circuitsaint.com
- Documentación: https://docs.circuitsaint.com

---

**Versión del Documento**: 1.0  
**Última Actualización**: 14/03/2025  
**Autor**: Andrés Ernesto Díaz Ortega

