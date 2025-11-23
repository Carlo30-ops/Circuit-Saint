Circuit Saint - Tienda Virtual
Aplicación móvil tipo Tienda Virtual desarrollada en Android Studio con Kotlin, siguiendo arquitectura MVVM.

📱 Características
✅ Lista de Productos: Visualización de productos con RecyclerView
✅ Detalle de Productos: Pantalla detallada con información completa
✅ Carrito de Compras: Agregar, modificar y eliminar productos
✅ Simulación de Compra: Proceso completo de checkout
✅ Escanear QR: Vinculación a formulario mediante código QR
✅ Ubicación de Tienda: Mapa interactivo con Google Maps
✅ Formulario de Contacto: Formulario vinculado desde QR
✅ Optimizaciones: Reducción de consumo de batería y memoria
🏗️ Arquitectura
El proyecto sigue la arquitectura MVVM (Model-View-ViewModel):

app/
├── data/
│   ├── model/          # Entidades de datos (Product, CartItem, StoreLocation)
│   ├── db/             # Room Database (DAOs, AppDatabase)
│   └── repository/     # StoreRepository (capa de datos)
├── viewmodel/          # StoreViewModel (lógica de negocio)
├── ui/                 # Activities y Fragments (vista)
└── util/               # Utilidades (PerformanceOptimizer)
🛠️ Tecnologías Utilizadas
Kotlin: Lenguaje de programación
Room Database: Persistencia de datos local
LiveData: Observables reactivos
Coroutines: Programación asíncrona
ViewBinding: Binding de vistas
Google Maps: Visualización de ubicación
CameraX + ML Kit: Escáner de códigos QR
Material Design: Componentes UI modernos
📦 Dependencias Principales
// Room Database
implementation("androidx.room:room-runtime:2.6.1")
implementation("androidx.room:room-ktx:2.6.1")

// Google Maps
implementation("com.google.android.gms:play-services-maps:18.2.0")
implementation("com.google.android.gms:play-services-location:21.0.1")

// QR Scanner
implementation("androidx.camera:camera-camera2:1.3.0")
implementation("androidx.camera:camera-lifecycle:1.3.0")
implementation("androidx.camera:camera-view:1.3.0")
implementation("com.google.mlkit:barcode-scanning:17.2.0")

// Glide
implementation("com.github.bumptech.glide:glide:4.16.0")

// Navigation
implementation("androidx.navigation:navigation-fragment-ktx:2.7.6")
🚀 Configuración del Proyecto
Requisitos Previos
Android Studio Hedgehog o superior
JDK 17
Android SDK 24+ (Android 7.0)
Google Maps API Key (para funcionalidad de mapas)
Pasos de Instalación
Clonar el repositorio

git clone [url-del-repositorio]
cd "Circuit Saint"
Configurar Google Maps API Key

Obtener API Key en Google Cloud Console
Reemplazar YOUR_GOOGLE_MAPS_API_KEY en AndroidManifest.xml
Sincronizar dependencias

./gradlew build
Ejecutar la aplicación

Conectar dispositivo Android o iniciar emulador
Ejecutar desde Android Studio
📱 Estructura de Pantallas
MainActivity
Contiene BottomNavigationView para navegación
Gestiona los fragments principales
HomeFragment
Lista de productos con RecyclerView
Navegación a detalle de producto
ProductDetailActivity
Información completa del producto
Selector de cantidad
Agregar al carrito
CartActivity
Resumen del carrito
Modificar cantidades
Eliminar productos
Finalizar compra
MapFragment
Mapa interactivo con Google Maps
Ubicación de la tienda
Información de contacto
QrScannerFragment
Escáner de códigos QR
Vinculación automática a formulario
FormActivity
Formulario de contacto
Validación de campos
Envío de datos
🔋 Optimizaciones de Batería y Memoria
Implementadas
PerformanceOptimizer.kt

Detección de batería baja
Liberación automática de recursos
Gestión de memoria optimizada
ViewBinding

Previene memory leaks
Limpieza automática en onDestroyView()
Lifecycle-aware Components

Observadores del ciclo de vida
Liberación de recursos en pausa
ProGuard/R8

Minificación de código
Reducción de tamaño APK
Ofuscación de código
Mejores Prácticas
✅ Uso de Coroutines con scope limitado
✅ Lazy loading en RecyclerView
✅ Optimización de imágenes
✅ Caché de datos cuando sea posible
✅ Liberación de recursos en onPause()
📊 Base de Datos
Esquema
Product

id (Primary Key)
name
description
price
imageUrl
stock
CartItem

id (Primary Key)
productId (Foreign Key)
quantity
Operaciones
CRUD completo de productos
Gestión de carrito
Queries optimizadas con índices
Transacciones para operaciones complejas
Scripts externos
database/mysql_seed.sql: script oficial con los 6 productos base y las URLs originales de Unsplash para cuando la API REST/MySQL esté disponible.
🔐 Permisos Requeridos
<!-- Ubicación -->
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />

<!-- Cámara (QR Scanner) -->
<uses-permission android:name="android.permission.CAMERA" />

<!-- Internet -->
<uses-permission android:name="android.permission.INTERNET" />
📝 Uso del Escáner QR
Navegar a la pestaña "Escanear QR"
Apuntar la cámara al código QR
El sistema detecta automáticamente el código
Se abre el formulario vinculado
Formatos soportados:

URLs: http:// o https://
Códigos personalizados: FORM:codigo123
Texto plano (se usa como código)
🗺️ Configuración de Google Maps
Crear proyecto en Google Cloud Console
Habilitar "Maps SDK for Android"
Generar API Key
Agregar restricciones (recomendado)
Reemplazar en AndroidManifest.xml:
<meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="TU_API_KEY_AQUI" />
📦 Generar APK/AAB para Producción
APK
./gradlew assembleRelease
Output: app/build/outputs/apk/release/app-release.apk

AAB (Recomendado para Play Store)
./gradlew bundleRelease
Output: app/build/outputs/bundle/release/app-release.aab

Firmar la Aplicación
Generar keystore:

keytool -genkey -v -keystore circuit-saint-release.keystore -alias circuit_saint -keyalg RSA -keysize 2048 -validity 10000
Configurar en build.gradle.kts (ver GUIA_PUBLICACION.md)

📚 Documentación Adicional
DOCUMENTACION_PROYECTO.md: Documentación completa del proyecto académico
DIAGRAMA_ARQUITECTURA.md: Diagramas detallados de arquitectura y flujos
CHANGELOG.md: Historial de cambios y mejoras
INFORME_ESTADO_PROYECTO.md: Estado actual y mejoras implementadas
Código comentado: Todas las clases principales tienen documentación
🧪 Testing
Pruebas Manuales
Lista de productos: Verificar carga y navegación
Detalle de producto: Validar información y agregar al carrito
Carrito: Probar agregar, modificar y eliminar
Compra: Simular proceso completo
QR Scanner: Escanear diferentes códigos
Mapa: Verificar ubicación y marcadores
Formulario: Validar campos y envío
Dispositivos de Prueba
Mínimo: Android 7.0 (API 24)
Recomendado: Android 10+ (API 29+)
Probar en diferentes tamaños de pantalla
🐛 Solución de Problemas
Error: "Google Maps no se muestra"
Verificar API Key en AndroidManifest.xml
Comprobar que Maps SDK está habilitado en Google Cloud
Revisar permisos de ubicación
Error: "Cámara no funciona"
Verificar permisos de cámara
Comprobar que el dispositivo tiene cámara
Revisar configuración de permisos en tiempo de ejecución
Error: "Room Database"
Verificar que kapt está configurado
Limpiar y reconstruir proyecto: ./gradlew clean build
🖼️ Imágenes, logo y atribuciones
Las imágenes de productos reutilizan exactamente las mismas URLs que la PWA Circuit Saint (Unsplash). Se cargan dinámicamente con Glide (placeholder_product.xml se usa como fallback).
El logo “CIRCUIT SAINT” se renderiza con la fuente Anton y un gradiente dinámico (ver MainActivity.kt → applyLogoGradient()).
Los iconos del sistema provienen de Material Icons (ic_store, ic_map_pin, ic_qr_code, ic_phone, ic_shopping_cart, etc.) generados como Vector Assets.
Créditos: “Las imágenes de productos son proporcionadas por Unsplash – https://unsplash.com/license”.
📄 Licencia
Este proyecto es parte del trabajo académico "Construcción de una Tienda Virtual en Android Studio con Kotlin" por Andrés Ernesto Díaz Ortega.

👤 Autor
Andrés Ernesto Díaz Ortega

Carlos José Betts Gómez

Fecha de Elaboración: 14/03/2025
Versión: 01
🙏 Agradecimientos
Android Developer Community
Google Maps Platform
ZXing Project
Material Design Team
