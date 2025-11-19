# Diagrama de Arquitectura - Circuit Saint

## Arquitectura MVVM

```
┌─────────────────────────────────────────────────────────────┐
│                        UI LAYER                              │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │  Activities  │  │   Fragments  │  │   Adapters   │      │
│  │              │  │              │  │              │      │
│  │ MainActivity │  │ HomeFragment │  │ProductoAdapter│      │
│  │ProductDetail │  │  MapFragment │  │ CartAdapter  │      │
│  │   Activity   │  │QrScannerFrag. │  │              │      │
│  │ CartActivity │  │              │  │              │      │
│  │ FormActivity │  │              │  │              │      │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘      │
│         │                 │                 │               │
│         └─────────────────┼─────────────────┘               │
│                           │                                 │
│                           ▼                                 │
│                  ┌─────────────────┐                        │
│                  │  ViewBinding    │                        │
│                  │  (UI Binding)   │                        │
│                  └─────────────────┘                        │
└───────────────────────────┬─────────────────────────────────┘
                            │
                            │ Observa LiveData
                            │
┌───────────────────────────▼─────────────────────────────────┐
│                    VIEWMODEL LAYER                          │
│  ┌──────────────────────────────────────────────────────┐  │
│  │              StoreViewModel                          │  │
│  │  - allProducts: LiveData<List<Product>>             │  │
│  │  - cartItems: LiveData<List<CartItemWithProduct>>    │  │
│  │  - cartItemCount: LiveData<Int>                     │  │
│  │  - totalPrice: LiveData<Double?>                      │  │
│  │                                                       │  │
│  │  Métodos:                                            │  │
│  │  - insertProduct()                                   │  │
│  │  - addToCart()                                       │  │
│  │  - updateCartItemQuantity()                         │  │
│  │  - removeFromCart()                                  │  │
│  │  - clearCart()                                       │  │
│  └───────────────────┬─────────────────────────────────┘  │
└────────────────────────┼─────────────────────────────────────┘
                         │
                         │ Usa Coroutines
                         │
┌────────────────────────▼─────────────────────────────────────┐
│                    REPOSITORY LAYER                           │
│  ┌────────────────────────────────────────────────────────┐  │
│  │              StoreRepository                           │  │
│  │  - Abstrae acceso a datos                             │  │
│  │  - Maneja lógica de negocio de datos                  │  │
│  │  - Proporciona datos a ViewModel                      │  │
│  └───────────────────┬──────────────────────────────────┘  │
└────────────────────────┼─────────────────────────────────────┘
                         │
                         │ Accede a
                         │
┌────────────────────────▼─────────────────────────────────────┐
│                      DATA LAYER                                │
│  ┌──────────────────┐         ┌──────────────────┐        │
│  │   Room Database   │         │   Data Models     │        │
│  │                   │         │                   │        │
│  │  AppDatabase      │         │  Product          │        │
│  │  ├─ ProductDao    │         │  CartItem         │        │
│  │  └─ CartDao       │         │  StoreLocation    │        │
│  │                   │         │  CartItemWithProd │        │
│  └──────────────────┘         └──────────────────┘        │
└──────────────────────────────────────────────────────────────┘
```

## Flujo de Datos

### 1. Cargar Productos

```
User Action (HomeFragment)
    │
    ▼
StoreViewModel.getAllProducts()
    │
    ▼
StoreRepository.getAllProducts()
    │
    ▼
ProductDao.getAllProducts() → LiveData<List<Product>>
    │
    ▼
Room Database Query
    │
    ▼
LiveData actualiza UI automáticamente
```

### 2. Agregar al Carrito

```
User Action (ProductDetailActivity)
    │
    ▼
StoreViewModel.addToCart(productId, quantity)
    │
    ▼
Coroutine Scope (viewModelScope)
    │
    ▼
StoreRepository.addToCart()
    │
    ├─ Verifica si existe en carrito
    ├─ Si existe: actualiza cantidad
    └─ Si no existe: inserta nuevo
    │
    ▼
CartDao.insertCartItem() / updateCartItem()
    │
    ▼
Room Database Transaction
    │
    ▼
LiveData se actualiza automáticamente
    │
    ▼
UI se actualiza (CartActivity, HomeFragment)
```

## Componentes Principales

### UI Components

```
MainActivity
├── BottomNavigationView
│   ├── Home (HomeFragment)
│   ├── Map (MapFragment)
│   ├── QR Scanner (QrScannerFragment)
│   └── Cart (CartActivity)
│
HomeFragment
├── RecyclerView
│   └── ProductoAdapter
│       └── ProductViewHolder
└── Observa: StoreViewModel.allProducts

ProductDetailActivity
├── Muestra detalles del producto
├── Selector de cantidad
└── Botón "Agregar al Carrito"

CartActivity
├── RecyclerView
│   └── CartAdapter
│       └── CartViewHolder
├── Total del carrito
└── Botón "Finalizar Compra"

MapFragment
├── Google Maps
├── Marcador de tienda
└── Información de contacto

QrScannerFragment
├── Cámara (ZXing)
├── Detección de QR
└── Navegación a FormActivity

FormActivity
├── Formulario de contacto
└── Validación de campos
```

### Data Flow

```
┌─────────────┐
│   Product   │
│  (Entity)   │
└──────┬──────┘
       │
       │ @Entity
       ▼
┌─────────────┐
│ ProductDao  │
│  (DAO)      │
└──────┬──────┘
       │
       │ Queries
       ▼
┌─────────────┐
│ AppDatabase │
│  (Room)     │
└─────────────┘
```

## Optimizaciones Implementadas

### Batería
- Lifecycle-aware components
- Liberación de recursos en onPause()
- Detección de batería baja
- Uso eficiente de WakeLocks

### Memoria
- ViewBinding (previene leaks)
- Limpieza automática de recursos
- Optimización de imágenes
- ProGuard/R8 minificación

### Rendimiento
- Coroutines para operaciones asíncronas
- LiveData para actualizaciones reactivas
- Índices en base de datos
- Lazy loading en RecyclerView

## Dependencias entre Capas

```
UI Layer
    │ depende de
    ▼
ViewModel Layer
    │ depende de
    ▼
Repository Layer
    │ depende de
    ▼
Data Layer (Room)
```

**Regla**: Las capas superiores solo conocen las inferiores, nunca al revés.

## Comunicación entre Componentes

### ViewModel ↔ Repository
- Llamadas directas a métodos del repositorio
- Uso de coroutines para operaciones asíncronas

### Repository ↔ Database
- Acceso a DAOs
- Transacciones cuando sea necesario

### View ↔ ViewModel
- Observación de LiveData
- Llamadas a métodos del ViewModel

## Patrones Utilizados

1. **Repository Pattern**: Abstracción de fuente de datos
2. **Observer Pattern**: LiveData observables
3. **Singleton Pattern**: AppDatabase, PerformanceOptimizer
4. **Factory Pattern**: StoreViewModelFactory
5. **Adapter Pattern**: RecyclerView Adapters

---

**Versión**: 1.0  
**Fecha**: 14/03/2025

