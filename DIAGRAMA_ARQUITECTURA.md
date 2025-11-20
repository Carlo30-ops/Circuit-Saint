# Diagrama de Arquitectura - Circuit Saint

## Arquitectura MVVM con Clean Architecture

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              PRESENTATION LAYER                              │
│                                                                               │
│  ┌──────────────┐    ┌──────────────┐    ┌──────────────┐                  │
│  │MainActivity  │    │HomeFragment  │    │ProductDetail │                  │
│  │              │    │              │    │  Activity    │                  │
│  │- Navigation  │    │- RecyclerView│    │- Product Info│                  │
│  │- BottomNav   │    │- Paging3     │    │- Add to Cart │                  │
│  └──────┬───────┘    └──────┬───────┘    └──────┬───────┘                  │
│         │                    │                    │                          │
│  ┌──────▼───────┐    ┌──────▼───────┐    ┌──────▼───────┐                  │
│  │CartActivity  │    │MapFragment   │    │QrScannerFrag │                  │
│  │              │    │              │    │              │                  │
│  │- Cart Items  │    │- Google Maps │    │- QR Scanner  │                  │
│  │- Checkout    │    │- Location    │    │- Permissions │                  │
│  └──────────────┘    └──────────────┘    └──────────────┘                  │
│                                                                               │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │                        ViewBinding                                    │   │
│  │  - activity_main.xml                                                 │   │
│  │  - fragment_home.xml                                                 │   │
│  │  - activity_product_detail.xml                                       │   │
│  │  - activity_cart.xml                                                  │   │
│  └──────────────────────────────────────────────────────────────────────┘   │
└───────────────────────────────────┬─────────────────────────────────────────┘
                                    │ Observa (LiveData/StateFlow)
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                            VIEWMODEL LAYER                                   │
│                                                                               │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │                        StoreViewModel                                 │   │
│  │  ┌────────────────────────────────────────────────────────────────┐  │   │
│  │  │  Products:                                                     │  │   │
│  │  │  - getProductsPaginated(): Flow<PagingData<Product>>          │  │   │
│  │  │  - searchProducts(query: String)                              │  │   │
│  │  │  - getProductById(id: Long): LiveData<Product?>               │  │   │
│  │  └────────────────────────────────────────────────────────────────┘  │   │
│  │  ┌────────────────────────────────────────────────────────────────┐  │   │
│  │  │  Cart:                                                         │  │   │
│  │  │  - cartItems: LiveData<List<CartItemWithProduct>>             │  │   │
│  │  │  - totalPrice: LiveData<Double?>                               │  │   │
│  │  │  - addToCart(productId, quantity)                             │  │   │
│  │  │  - updateCartItemQuantity(id, quantity)                        │  │   │
│  │  │  - removeCartItem(id)                                          │  │   │
│  │  └────────────────────────────────────────────────────────────────┘  │   │
│  │  ┌────────────────────────────────────────────────────────────────┐  │   │
│  │  │  Checkout:                                                     │  │   │
│  │  │  - checkoutState: StateFlow<Result<Order>?>                    │  │   │
│  │  │  - checkout(nombre, email, telefono)                           │  │   │
│  │  └────────────────────────────────────────────────────────────────┘  │   │
│  └──────────────────────────────────────────────────────────────────────┘   │
└───────────────────────────────────┬─────────────────────────────────────────┘
                                    │ Usa
                                    ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                              DOMAIN LAYER                                     │
│                                                                               │
│  ┌──────────────────────┐    ┌──────────────────────┐                      │
│  │GetProductsPaginated  │    │CheckoutUseCase       │                      │
│  │UseCase               │    │                      │                      │
│  │                      │    │- Validates data       │                      │
│  │- Encapsulates        │    │- Calls repository    │                      │
│  │  business logic      │    │- Returns Result<T>   │                      │
│  └──────────────────────┘    └──────────────────────┘                      │
│                                                                               │
│  ┌──────────────────────┐    ┌──────────────────────┐                      │
│  │AddToCartUseCase      │    │SearchProductsUseCase │                      │
│  │                      │    │                      │                      │
│  │- Validates quantity  │    │- Encapsulates search │                      │
│  │- Checks stock        │    │  logic              │                      │
│  │- Returns Result      │    │- Returns paginated   │                      │
│  └──────────────────────┘    └──────────────────────┘                      │
│                                                                               │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │                        Result<T> (Sealed Class)                       │   │
│  │  - Success<T>(val data: T)                                            │   │
│  │  - Error(val exception: Throwable, val message: String?)              │   │
│  │  - Loading                                                             │   │
│  └──────────────────────────────────────────────────────────────────────┘   │
└───────────────────────────────────┬─────────────────────────────────────────┘
                                    │ Usa
                                    ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                               DATA LAYER                                      │
│                                                                               │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │                        StoreRepository                               │   │
│  │  ┌────────────────────────────────────────────────────────────────┐  │   │
│  │  │  Products:                                                     │  │   │
│  │  │  - getProductsPaginated(): Flow<PagingData<Product>>          │  │   │
│  │  │  - searchProductsPaginated(query): Flow<PagingData<Product>>  │  │   │
│  │  │  - getProductById(id): LiveData<Product?>                     │  │   │
│  │  └────────────────────────────────────────────────────────────────┘  │   │
│  │  ┌────────────────────────────────────────────────────────────────┐  │   │
│  │  │  Cart:                                                         │  │   │
│  │  │  - getCartItemsWithProducts(): LiveData<List<CartItemWith...>> │  │   │
│  │  │  - addToCart(productId, quantity)                              │  │   │
│  │  │  - updateCartItemQuantity(id, quantity)                        │  │   │
│  │  │  - removeCartItem(id)                                          │  │   │
│  │  │  - clearCart()                                                  │  │   │
│  │  └────────────────────────────────────────────────────────────────┘  │   │
│  │  ┌────────────────────────────────────────────────────────────────┐  │   │
│  │  │  Checkout (Atomic Transaction):                                │  │   │
│  │  │  - checkout(nombre, email, telefono): Order?                   │  │   │
│  │  │    • Validates stock with conditional UPDATE                   │  │   │
│  │  │    • Creates order atomically                                  │  │   │
│  │  │    • Updates stock                                             │  │   │
│  │  │    • Clears cart                                               │  │   │
│  │  └────────────────────────────────────────────────────────────────┘  │   │
│  └───────────────────────────────────┬────────────────────────────────────┘   │
│                                      │                                        │
│  ┌───────────────────────────────────▼────────────────────────────────────┐   │
│  │                    Room Database (SQLite)                              │   │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐                 │   │
│  │  │ ProductDao  │  │  CartDao     │  │  OrderDao    │                 │   │
│  │  │             │  │              │  │              │                 │   │
│  │  │- getAll     │  │- getCart     │  │- insertOrder │                 │   │
│  │  │  Products   │  │  Items      │  │- getOrders   │                 │   │
│  │  │  Paged()    │  │- addToCart  │  │- update      │                 │   │
│  │  │- search     │  │- update     │  │  Estado      │                 │   │
│  │  │  Products() │  │  Quantity   │  │              │                 │   │
│  │  │- getById()  │  │- remove()   │  │              │                 │   │
│  │  │- update     │  │- clear()    │  │              │                 │   │
│  │  │  Stock()    │  │              │  │              │                 │   │
│  │  └──────────────┘  └──────────────┘  └──────────────┘                 │   │
│  │                                                                         │   │
│  │  ┌─────────────────────────────────────────────────────────────────┐   │   │
│  │  │  Entities:                                                      │   │   │
│  │  │  - Product                                                       │   │   │
│  │  │  - CartItem                                                      │   │   │
│  │  │  - Order                                                         │   │   │
│  │  │  - OrderItem                                                     │   │   │
│  │  │  - Contact                                                       │   │   │
│  │  └─────────────────────────────────────────────────────────────────┘   │   │
│  └─────────────────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│                      DEPENDENCY INJECTION (Hilt)                            │
│                                                                               │
│  ┌──────────────────────┐    ┌──────────────────────┐                      │
│  │  DatabaseModule      │    │RepositoryModule      │                      │
│  │                      │    │                      │                      │
│  │  @Provides           │    │  @Provides           │                      │
│  │  @Singleton          │    │  @Singleton          │                      │
│  │  - AppDatabase       │    │  - StoreRepository   │                      │
│  │  - ProductDao        │    │                      │                      │
│  │  - CartDao           │    │                      │                      │
│  │  - OrderDao          │    │                      │                      │
│  └──────────────────────┘    └──────────────────────┘                      │
│                                                                               │
│  ┌──────────────────────┐    ┌──────────────────────┐                      │
│  │  WorkerModule        │    │  UseCase Modules     │                      │
│  │                      │    │  (Auto-generated)    │                      │
│  │  @Provides           │    │                      │                      │
│  │  - HiltWorkerFactory │    │  - GetProducts...    │                      │
│  │                      │    │  - CheckoutUseCase   │                      │
│  │                      │    │  - AddToCartUseCase  │                      │
│  └──────────────────────┘    └──────────────────────┘                      │
└─────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│                         BACKGROUND SERVICES                                 │
│                                                                               │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │                    WorkManager + Hilt                                │   │
│  │  ┌────────────────────────────────────────────────────────────────┐  │   │
│  │  │  DatabaseSeederWorker                                           │  │   │
│  │  │  - Seeds database with initial products                         │  │   │
│  │  │  - Runs in background                                          │  │   │
│  │  │  - Uses Hilt for dependency injection                          │  │   │
│  │  └────────────────────────────────────────────────────────────────┘  │   │
│  └──────────────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│                         EXTERNAL SERVICES                                   │
│                                                                               │
│  ┌──────────────────────┐    ┌──────────────────────┐                      │
│  │  Google Maps API     │    │  ZXing QR Scanner    │                      │
│  │                      │    │                      │                      │
│  │  - MapFragment       │    │  - QrScannerFragment │                      │
│  │  - Location display  │    │  - QR code reading   │                      │
│  │  - Marker placement  │    │  - Permission handle │                      │
│  └──────────────────────┘    └──────────────────────┘                      │
│                                                                               │
│  ┌──────────────────────┐    ┌──────────────────────┐                      │
│  │  Glide               │    │  Timber (Logging)     │                      │
│  │                      │    │                      │                      │
│  │  - Image loading     │    │  - Debug logging     │                      │
│  │  - Image caching     │    │  - Error tracking    │                      │
│  │  - Memory efficient  │    │  - Performance logs  │                      │
│  └──────────────────────┘    └──────────────────────┘                      │
└─────────────────────────────────────────────────────────────────────────────┘
```

## Flujo de Datos: Checkout Process

```
User clicks "Finalizar Compra"
         │
         ▼
┌────────────────────┐
│  CartActivity      │
│  - Shows dialog    │
│  - Collects data  │
└─────────┬──────────┘
          │
          ▼
┌────────────────────┐
│  StoreViewModel    │
│  - checkout()      │
│  - Sets Loading    │
└─────────┬──────────┘
          │
          ▼
┌────────────────────┐
│  CheckoutUseCase   │
│  - Validates data  │
│  - Calls repo      │
└─────────┬──────────┘
          │
          ▼
┌────────────────────┐
│  StoreRepository   │
│  - checkout()      │
│  - withTransaction │
└─────────┬──────────┘
          │
          ▼
┌─────────────────────────────────────────┐
│  Room Database (Atomic Transaction)     │
│  1. Validate stock (conditional UPDATE) │
│  2. Create Order                        │
│  3. Create OrderItems                   │
│  4. Update product stock                │
│  5. Clear cart                          │
└─────────┬───────────────────────────────┘
          │
          ▼
┌────────────────────┐
│  Returns Order     │
│  (or null if fail) │
└─────────┬──────────┘
          │
          ▼
┌────────────────────┐
│  CheckoutUseCase   │
│  - Wraps in Result │
└─────────┬──────────┘
          │
          ▼
┌────────────────────┐
│  StoreViewModel    │
│  - Updates state   │
│  - Result.Success  │
└─────────┬──────────┘
          │
          ▼
┌────────────────────┐
│  CartActivity      │
│  - Shows success   │
│  - Closes activity │
└────────────────────┘
```

## Flujo de Datos: Product List

```
HomeFragment created
         │
         ▼
┌────────────────────┐
│  HomeFragment      │
│  - setupRecycler   │
│  - observeViewModel│
└─────────┬──────────┘
          │
          ▼
┌────────────────────┐
│  StoreViewModel    │
│  - getProducts     │
│    Paginated()     │
└─────────┬──────────┘
          │
          ▼
┌────────────────────┐
│  GetProducts       │
│  PaginatedUseCase  │
└─────────┬──────────┘
          │
          ▼
┌────────────────────┐
│  StoreRepository   │
│  - getProducts     │
│    Paginated()     │
└─────────┬──────────┘
          │
          ▼
┌────────────────────┐
│  Pager (Paging3)   │
│  - Creates Pager   │
│  - Returns Flow    │
└─────────┬──────────┘
          │
          ▼
┌────────────────────┐
│  ProductDao        │
│  - getAllProducts  │
│    Paged()         │
│  - Returns         │
│    PagingSource    │
└─────────┬──────────┘
          │
          ▼
┌────────────────────┐
│  Room Database     │
│  - Queries products│
│  - Returns page    │
└─────────┬──────────┘
          │
          ▼
┌────────────────────┐
│  Flow<PagingData>  │
│  - Emits pages     │
│  - Auto-loads more │
└─────────┬──────────┘
          │
          ▼
┌────────────────────┐
│  ProductoAdapter   │
│  - submitData()    │
│  - Updates UI      │
└────────────────────┘
```

## Componentes y Responsabilidades

### Presentation Layer
- **Activities/Fragments:** Manejan UI y eventos del usuario
- **Adapters:** Conectan datos con RecyclerView
- **ViewBinding:** Acceso seguro a vistas

### ViewModel Layer
- **StoreViewModel:** Expone datos y estados para UI
- **LiveData/StateFlow:** Observables reactivos
- **viewModelScope:** Manejo de coroutines

### Domain Layer
- **UseCases:** Encapsulan lógica de negocio
- **Result<T>:** Manejo explícito de estados
- **Validaciones:** Reglas de negocio

### Data Layer
- **Repository:** Única fuente de verdad
- **DAOs:** Acceso a base de datos
- **Entities:** Modelos de datos
- **Transacciones:** Operaciones atómicas

### Infrastructure
- **Hilt:** Inyección de dependencias
- **WorkManager:** Tareas en background
- **Room:** Base de datos local
- **Paging3:** Paginación eficiente
