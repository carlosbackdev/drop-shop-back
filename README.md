# 🛒 DropShop Backend

API REST para una plataforma de e-commerce de dropshipping con integración de scraping automatizado de productos desde AliExpress y procesamiento mediante IA.

## 🆕 Cambios Recientes - Sistema de Usuarios y Autenticación JWT

### ✨ Nueva Funcionalidad Implementada (v2.0)

Se ha implementado un **sistema completo de autenticación y usuarios** con las siguientes características:

#### 🎯 Características Principales

- ✅ **Registro de usuarios** con validación de email y contraseña
- ✅ **Login tradicional** con email y contraseña
- ✅ **Autenticación JWT stateless** (sin sesiones en servidor)
- ✅ **Endpoint `/users/me`** para obtener información del usuario autenticado
- ✅ **Roles de usuario** (USER, ADMIN)
- ✅ **Encriptación BCrypt** para contraseñas
- ✅ **Integración con Swagger** (Bearer Authentication)
- ✅ **Preparado para Firebase/Google Authentication**
- ✅ **Nuevo flujo de Checkout protegido** (crear, listar, actualizar y eliminar órdenes del usuario autenticado)
- ✅ **Sistema de Órdenes completo** (crear órdenes, actualizar estados, cancelar, filtrar por estado)

#### 📦 Nuevos Módulos

- **`users/`**: Paquete completo con entidad User, servicios y controladores
- **`users/components/`**: JwtTokenProvider, JwtAuthenticationFilter, CustomUserDetailsService
- **`checkout/`**: Entidad, DTOs, repositorio, servicio y controlador REST protegidos con JWT
- **`order/`**: Sistema completo de órdenes con estados (PENDING, PAID, PROCESSING, SHIPPED, DELIVERED, CANCELLED)
- **DTOs de autenticación**: RegisterRequest, LoginRequest, AuthResponse, UserMeResponse

#### 🔧 Configuración Actualizada

- **SecurityConfig**: Ahora con soporte JWT y sesiones stateless
- **OpenApiConfig**: Documentación Swagger con autenticación Bearer
- **Nuevas propiedades**: `jwt.secret` y `jwt.expiration`

#### 🎨 Nuevas Dependencias

- **jjwt (0.12.5)**: Librería JWT de alta seguridad
- Configuración lista para Firebase Admin SDK (opcional)

---

## 📋 Descripción

DropShop es una aplicación backend desarrollada en Spring Boot que proporciona servicios para la gestión de productos y categorías de un e-commerce. Incluye funcionalidades avanzadas como:

- 👤 **Sistema de usuarios y autenticación JWT** (registro, login, gestión de perfiles)
- 🔑 **Autenticación stateless** con JSON Web Tokens
- 🔥 **Compatible con Firebase/Google Authentication**
- 🔄 Importación automatizada de productos desde AliExpress mediante scraping
- 🤖 Procesamiento de información de productos con IA
- 📦 Gestión de catálogo de productos y categorías
- 🔒 Rutas administrativas protegidas (acceso solo desde localhost)
- 📚 Documentación interactiva con Swagger/OpenAPI
- ⚡ Actualizaciones asíncronas de inventario

## 🚀 Tecnologías

- **Java 21**
- **Spring Boot 3.5.7**
  - Spring Data JPA
  - Spring Security
  - Spring Web
- **MySQL 8**
- **JWT (JSON Web Tokens)** - Autenticación stateless
- **Lombok**
- **SpringDoc OpenAPI (Swagger)**
- **Docker** (Docker Compose para base de datos)

## 📦 Requisitos Previos

- Java 21 o superior
- Maven 3.6+
- MySQL 8 o Docker/Docker Compose
- Variables de entorno configuradas

## ⚙️ Configuración

### Variables de Entorno

Configura las siguientes variables de entorno antes de ejecutar la aplicación:

```bash
# Base de datos
DB_URL=jdbc:mysql://localhost:3306/dropshop
DB_USER=tu_usuario
DB_PASS=tu_contraseña

# API de scraping
SCRAPING_API_URL=https://tu-api-de-scraping.com

# JWT (Opcional - valores por defecto incluidos)
jwt.secret=${JWT_SECRET:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}
jwt.expiration=${JWT_EXPIRATION:86400000}  # 24 horas en milisegundos
# Google Sign-In (habilita la validación opcional del token en backend)
google.client.id=tu-client-id-aqui.apps.googleusercontent.com

# Stripe (para pagos)
STRIPE_API_KEY=sk_test_4eC39HqLyjWDarjtT1zdp7dc
STRIPE_WEBHOOK_SECRET=whsec_...
STRIPE_SUCCESS_URL=https://tu-front.com/payment/success
STRIPE_CANCEL_URL=https://tu-front.com/payment/cancel
```

> **¿Para qué sirven estas propiedades?**
> - `jwt.secret` y `jwt.expiration` configuran la firma y vigencia de los tokens que emite `JwtTokenProvider`.
> - `google.client.id` es utilizado por `GoogleTokenValidator` para verificar los tokens entregados por Firebase/Google cuando quieras validar el `idToken` recibido desde el frontend.
> - `STRIPE_API_KEY`, `STRIPE_WEBHOOK_SECRET`, `STRIPE_SUCCESS_URL` y `STRIPE_CANCEL_URL` son utilizados para configurar la integración con Stripe.

> **Nota**: Si no configuras `JWT_SECRET`, se usará una clave por defecto (NO recomendado en producción). Para generar una clave segura en Base64, puedes usar: `openssl rand -base64 64`

### Base de Datos con Docker

El proyecto incluye un archivo `docker-compose.yml` para levantar MySQL fácilmente:

```bash
docker-compose up -d
```

## 🏃‍♂️ Ejecutar la Aplicación

### Con Maven

```bash
# Compilar el proyecto
mvn clean install

# Ejecutar la aplicación
mvn spring-boot:run
```

### Con Maven Wrapper (Windows)

```bash
.\mvnw.cmd spring-boot:run
```

### Con Maven Wrapper (Linux/Mac)

```bash
./mvnw spring-boot:run
```

La aplicación estará disponible en: `http://localhost:8080`

## 📖 Documentación de la API

Una vez que la aplicación esté ejecutándose, puedes acceder a la documentación interactiva de Swagger en:

**Swagger UI**: `http://localhost:8080/swagger-ui.html`

**OpenAPI JSON**: `http://localhost:8080/api-docs`

### 🔐 Usar JWT en Swagger

1. Primero, registra un usuario o haz login usando los endpoints `/api/auth/register` o `/api/auth/login`
2. Copia el token JWT de la respuesta
3. En Swagger, haz clic en el botón **"Authorize"** 🔓 (arriba a la derecha)
4. Pega el token en el campo (sin el prefijo "Bearer")
5. Haz clic en **"Authorize"** y luego **"Close"**
6. Ahora puedes probar endpoints protegidos como `/api/users/me`

## 🔐 Seguridad y Autenticación

La aplicación implementa **autenticación stateless con JWT (JSON Web Tokens)**.

### 🔓 Rutas Públicas (Sin autenticación)
- ✅ `POST /api/auth/register` - Registro de nuevos usuarios
- ✅ `POST /api/auth/login` - Inicio de sesión
- ✅ `GET /api/products/**` - Consulta de productos para clientes
- ✅ `GET /api/categories/**` - Consulta de categorías
- ✅ `/swagger-ui/**` - Documentación de la API
- ✅ `/api-docs/**` - Especificación OpenAPI

### 🔒 Rutas Protegidas (Requieren JWT)
- 🔐 `GET /api/users/me` - Obtener información del usuario actual
- 🔐 `POST /api/checkout` - Crear un nuevo checkout
- 🔐 `GET /api/checkout` - Listar checkouts del usuario
- 🔐 `GET /api/checkout/{id}` - Obtener un checkout específico
- 🔐 `PUT /api/checkout/{id}` - Actualizar un checkout
- 🔐 `DELETE /api/checkout/{id}` - Eliminar un checkout
- 🔐 `POST /api/orders` - Crear una nueva orden
- 🔐 `GET /api/orders` - Listar todas mis órdenes
- 🔐 `GET /api/orders/{id}` - Obtener una orden específica
- 🔐 `GET /api/orders/status/{status}` - Listar órdenes por estado
- 🔐 `PATCH /api/orders/{id}/status` - Actualizar estado de una orden
- 🔐 `PATCH /api/orders/{id}/cancel` - Cancelar una orden
- 🔐 `DELETE /api/orders/{id}` - Eliminar una orden cancelada

### 🛡️ Rutas Administrativas (Solo Localhost + Opcional JWT)
- 🔒 `POST /api/categories/admin/save` - Crear categoría
- 🔒 `DELETE /api/categories/admin/delete/{id}` - Eliminar categoría
- 🔒 `GET /api/products/admin/**` - Administración completa de productos
- 🔒 `POST /api/products/admin/import` - Importar desde AliExpress
- 🔒 `POST /api/products/admin/scripting-update` - Actualización por scraping

## 📡 Endpoints Principales

### 👤 Autenticación (Público)

#### Registro de Usuario

```http
POST /api/auth/register
Content-Type: application/json

{
  "email": "usuario@example.com",
  "password": "password123",
  "fullName": "Juan Pérez"
}
```

**Respuesta exitosa:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "userId": 1,
  "email": "usuario@example.com",
  "fullName": "Juan Pérez",
  "role": "USER"
}
```

#### Inicio de Sesión

```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "usuario@example.com",
  "password": "password123"
}
```

**Respuesta exitosa:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "userId": 1,
  "email": "usuario@example.com",
  "fullName": "Juan Pérez",
  "role": "USER",
  "photoUrl": null
}
```

#### 🔥 Login/Registro con Firebase/Google (IMPLEMENTADO)

```http
POST /api/auth/firebase-login
Content-Type: application/json

{
  "email": "usuario@gmail.com",
  "fullName": "Juan Pérez",
  "firebaseToken": "eyJhbGciOiJSUzI1NiIsImtpZCI6...",
  "firebaseUid": "firebase-uid-123456",
  "photoUrl": "https://lh3.googleusercontent.com/a/..."
}
```

**Respuesta exitosa:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "userId": 1,
  "email": "usuario@gmail.com",
  "fullName": "Juan Pérez",
  "role": "USER",
  "photoUrl": "https://lh3.googleusercontent.com/a/..."
}
```

**Comportamiento:**
- ✅ Si el email YA existe → Actualiza la información (foto, nombre, firebaseUid) y devuelve JWT
- ✅ Si el email NO existe → Crea un nuevo usuario y devuelve JWT
- ✅ No requiere validación del token de Firebase (puedes activarla si quieres más seguridad)
- ✅ El usuario puede luego hacer login tradicional si quiere establecer una contraseña

### 🔐 Usuario Actual (Requiere JWT)

#### Obtener Información del Usuario Autenticado

```http
GET /api/users/me
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Respuesta:**
```json
{
  "id": 1,
  "email": "usuario@example.com",
  "fullName": "Juan Pérez",
  "role": "USER"
}
```

### 🧾 Checkout (Protegido con JWT)

```http
GET /api/checkout                # Lista todos los checkouts del usuario
GET /api/checkout/{id}           # Obtiene un checkout propio
POST /api/checkout               # Crea un checkout para el usuario autenticado
PUT /api/checkout/{id}           # Actualiza un checkout existente
DELETE /api/checkout/{id}        # Elimina un checkout
```

**Ejemplo de payload (`POST /api/checkout`):**
```json
{
  "customerName": "Juan Pérez",
  "customerEmail": "juan@example.com",
  "address": "Av. Siempre Viva 742",
  "city": "Madrid",
  "country": "España",
  "postalCode": "28001",
  "phoneNumber": "+34 600 123 456"
}
```

Cada operación utiliza el usuario autenticado (`/users/me`) gracias al `UserService`, por lo que no necesitas enviar `userId` en el payload.

### 📦 Órdenes (Protegido con JWT)

```http
GET /api/orders                       # Lista todas las órdenes del usuario
GET /api/orders/{id}                  # Obtiene una orden específica
GET /api/orders/status/{status}       # Lista órdenes por estado (PENDING, PAID, etc.)
POST /api/orders                      # Crea una nueva orden
PATCH /api/orders/{id}/status         # Actualiza el estado de una orden
PATCH /api/orders/{id}/cancel         # Cancela una orden
DELETE /api/orders/{id}               # Elimina una orden (solo canceladas)
```

**Ejemplo de payload para crear orden (`POST /api/orders`):**
```json
{
  "checkoutId": 1,
  "cartItemIds": [10, 11, 12],
  "total": 159.99,
  "notes": "Entregar por la mañana"
}
```

**Ejemplo para actualizar estado (`PATCH /api/orders/{id}/status`):**
```json
{
  "status": "PAID",
  "notes": "Pago confirmado vía PayPal"
}
```

**Estados disponibles:**
- `PENDING` - Pendiente de pago
- `PAID` - Pagada
- `PROCESSING` - En procesamiento
- `SHIPPED` - Enviada
- `DELIVERED` - Entregada
- `CANCELLED` - Cancelada

**Reglas de negocio:**
- Solo se pueden cancelar órdenes en estado `PENDING` o `PAID`
- Solo se pueden eliminar órdenes en estado `CANCELLED`
- Cada orden está asociada a un usuario (obtenido del JWT)
- Una orden incluye referencia a checkout y lista de items del carrito

### 🛍️ Productos (Público)

```http
GET /api/products/page?page=0&size=10
GET /api/products/{id}
GET /api/products/category/{categoryId}?page=0&size=10
```

### Categorías (Público)

```http
GET /api/categories/get/all
GET /api/categories/get/{id}
```

### Administración de Productos (Solo Localhost)

```http
GET /api/products/admin/all
GET /api/products/admin/{id}
POST /api/products/admin/save
PUT /api/products/admin/update
DELETE /api/products/admin/delete/{id}
```

#### Importar Producto desde AliExpress

```http
POST /api/products/admin/import
Content-Type: application/json

{
  "url": "https://es.aliexpress.com/item/1005006982763663.html",
  "categoryId": "5"
}
```

#### Actualización Asíncrona por Scraping

```http
POST /api/products/admin/scripting-update
```

### Administración de Categorías (Solo Localhost)

```http
POST /api/categories/admin/save
DELETE /api/categories/admin/delete/{id}
```

## 💳 Pagos con Stripe

La API integra Stripe Checkout para manejar pagos de órdenes.

### Configuración

Añade estas variables de entorno:
```bash
STRIPE_API_KEY=sk_live_...
STRIPE_WEBHOOK_SECRET=whsec_...
STRIPE_SUCCESS_URL=https://tu-front.com/payment/success
STRIPE_CANCEL_URL=https://tu-front.com/payment/cancel
```

### Endpoints

| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| POST | `/api/payments/stripe/session` | Crea la sesión de Stripe Checkout para una orden (`orderId`) | JWT |
| POST | `/api/payments/stripe/webhook` | Webhook público de Stripe para confirmar pagos | Público |

### Flujo resumido
1. Frontend crea una orden y obtiene su `orderId`.
2. Llama a `/api/payments/stripe/session` con el JWT del usuario.
3. Backend responde con `url` de Stripe → redirigir al usuario.
4. Stripe ejecuta el webhook al completar/expirar el pago.
5. Backend marca la orden como `PAID` (o `PENDING` si falló).

> Consulta `ORDER_FLOW_GUIDE.md` para ver el flujo completo carrito → checkout → orden → pago.

## 🏗️ Estructura del Proyecto

```
src/main/java/com/motogear/dropshopback/
├── catalog/
│   ├── components/     # Mappers y componentes auxiliares
│   ├── domain/         # Entidades JPA (Product, Category, ProductImage)
│   ├── repository/     # Repositorios Spring Data
│   ├── service/        # Lógica de negocio
│   └── web/            # Controladores REST
├── users/              # ⭐ NUEVO: Módulo de usuarios y autenticación
│   ├── components/     # JwtTokenProvider, JwtAuthenticationFilter, UserDetailsService
│   ├── domain/         # Entidad User, enum UserRole
│   ├── repository/     # UserRepository
│   ├── service/        # UserService - lógica de usuarios
│   └── web/            # AuthController, UserController
├── checkout/           # ⭐ NUEVO: Módulo de checkout (direcciones de envío)
│   ├── domain/         # Entidad Checkout
│   ├── dto/            # DTOs de checkout
│   ├── repository/     # CheckoutRepository
│   ├── service/        # CheckoutService - lógica de checkouts
│   └── web/            # CheckoutController
├── order/              # ⭐ NUEVO: Módulo de órdenes de compra
│   ├── domain/         # Entidad Order, enum OrderStatus
│   ├── dto/            # CreateOrderRequest, OrderResponse, UpdateOrderStatusRequest
│   ├── repository/     # OrderRepository
│   ├── service/        # OrderService - lógica de órdenes
│   └── web/            # OrderController
├── common/
│   ├── dtd/
│   │   ├── catalog/    # DTOs de catálogo
│   │   └── user/       # ⭐ NUEVO: DTOs de usuarios (RegisterRequest, LoginRequest, AuthResponse, UserMeResponse)
│   └── util/           # Utilidades
├── config/
│   ├── SecurityConfig.java    # Configuración de seguridad con JWT
│   ├── OpenApiConfig.java     # Configuración de Swagger con Bearer Auth
│   └── AppConfig.java         # Configuración general
└── scraping/
    ├── ScrapingService.java   # Servicio de scraping
    └── dto/                   # DTOs relacionados con scraping
```

## 📦 Flujo Completo de Compra

✅ **YA IMPLEMENTADO** - Sistema completo de carrito → checkout → órdenes con seguimiento de estados.

> 📖 **[Ver guía completa del Flujo de Compra →](doc/ORDER_FLOW_GUIDE.md)**  
> Esta guía explica paso a paso cómo implementar el flujo completo desde agregar productos al carrito hasta confirmar una orden, incluyendo ejemplos de código frontend y mejores prácticas.

**Resumen del flujo:**
1. Usuario agrega productos al carrito (`POST /api/cart`)
2. Usuario configura dirección de envío (`POST /api/checkout`)
3. Usuario crea orden con items del carrito (`POST /api/orders`)
4. Sistema procesa pago (integración externa)
5. Frontend actualiza estado a `PAID` (`PATCH /api/orders/{id}/status`)
6. Seguimiento de estados: `PENDING` → `PAID` → `PROCESSING` → `SHIPPED` → `DELIVERED`

## 🔥 Integración con Firebase/Google Authentication

✅ **YA IMPLEMENTADO** - Tu backend ahora soporta nativamente login/registro con Firebase/Google.

> 📖 **[Ver guía completa de Google Authentication →](doc/GOOGLE_AUTH_GUIDE.md)**  
> Esta guía explica en detalle cómo obtener el email del usuario desde Google y sincronizarlo con tu backend (el email llega en el token de Google y es persistido en la entidad `User`).

### 📋 Estrategia Implementada: Login Híbrido

#### ✨ Endpoint `/api/auth/firebase-login` (YA DISPONIBLE)

1. **En el Frontend**: El usuario se autentica con Firebase/Google
2. **Obtener datos del usuario**: Firebase te da el email, nombre, UID y foto
3. **Enviar a tu Backend**: Usar el endpoint `/api/auth/firebase-login`
4. **Backend responde**: Con un JWT propio que usarás en todas las peticiones

#### 🔧 Implementación Backend (✅ COMPLETADA)

El endpoint está implementado en `AuthController`:

**El endpoint hace automáticamente:**

1. ✅ Busca si existe un usuario con ese email
2. ✅ Si existe → Actualiza sus datos (foto, nombre, firebaseUid)
3. ✅ Si NO existe → Crea un nuevo usuario con esos datos
4. ✅ Genera un JWT de tu backend
5. ✅ Retorna el token + datos del usuario

**Campos del DTO `FirebaseLoginRequest`:**

```java
{
  "email": "usuario@gmail.com",           // ✅ Requerido
  "fullName": "Juan Pérez",               // ✅ Requerido
  "firebaseToken": "eyJ...",              // ⚪ Opcional (para validar)
  "firebaseUid": "firebase-uid-123",      // ⚪ Opcional (se guarda en BD)
  "photoUrl": "https://..."               // ⚪ Opcional (foto de perfil)
}
```

### 🎯 Flujo de Autenticación Completo

```mermaid
sequenceDiagram
    Frontend->>Firebase: 1. Login con Google
    Firebase-->>Frontend: 2. Token Firebase + Datos usuario
    Frontend->>Backend: 3. POST /api/auth/firebase-login
    Note right of Frontend: { email, fullName, firebaseToken }
    Backend->>Backend: 4. Buscar o crear usuario por email
    Backend->>Backend: 5. Generar JWT propio
    Backend-->>Frontend: 6. JWT del backend + datos usuario
    Frontend->>Backend: 7. Usar JWT en todas las peticiones
    Note right of Frontend: Header: Authorization Bearer {JWT}
```

### 🔑 Ventajas de Esta Estrategia

1. ✅ **Flexibilidad**: Soportas login tradicional (email/password) Y login social (Google/Firebase)
2. ✅ **Simplicidad**: Solo necesitas el email para sincronizar usuarios
3. ✅ **Seguridad**: Tu backend mantiene el control con su propio JWT
4. ✅ **Desacoplamiento**: No dependes directamente de Firebase en el backend
5. ✅ **Base de datos unificada**: Todos los usuarios en tu BD, sin importar el método de login

### 🔐 Validación de Token Firebase (Opcional pero Recomendada)

Para mayor seguridad, puedes verificar el token de Firebase en el backend:


## 📝 Notas Importantes

1. **🔐 JWT y Seguridad**: 
   - Los tokens JWT tienen una duración de 24 horas por defecto (configurable)
   - La autenticación es **stateless** (no se guardan sesiones en servidor)
   - En producción, SIEMPRE usa `JWT_SECRET` personalizado y seguro
   - El token se envía en el header: `Authorization: Bearer {token}`

2. **👤 Gestión de Usuarios**:
   - Las contraseñas se encriptan con BCrypt (10 rounds)
   - Los emails deben ser únicos en el sistema
   - Por defecto, los usuarios registrados tienen rol `USER`
   - Para crear un admin, modifica directamente en la BD o crea un endpoint dedicado

3. **🔥 Firebase/Google Integration**:
   - El endpoint `/api/auth/firebase-login` debe ser implementado según tus necesidades
   - Recomendado: Validar el token de Firebase en el backend para mayor seguridad
   - El email es la clave única para sincronizar usuarios entre Firebase y tu BD

4. **🛡️ Acceso Administrativo**: 
   - Las rutas `/admin/**` solo son accesibles desde localhost (127.0.0.1 o ::1)
   - Cualquier intento de acceso desde otra IP será rechazado con error 403
   - Opcionalmente, puedes añadir validación de rol ADMIN en estas rutas

5. **🔄 Scraping**: 
   - El servicio de scraping requiere una API externa configurada mediante `SCRAPING_API_URL`

6. **⚡ Procesamiento Asíncrono**: 
   - Las actualizaciones masivas de productos se ejecutan de forma asíncrona para no bloquear la aplicación

7. **💾 Base de Datos**: 
   - La aplicación usa Hibernate con `ddl-auto=update`, lo que crea/actualiza el esquema automáticamente
   - La tabla de usuarios se creará automáticamente en el primer arranque

8. **📚 Swagger con JWT**:
   - Usa el botón "Authorize" en Swagger para probar endpoints protegidos
   - Solo pega el token (sin el prefijo "Bearer")


## 📄 Licencia

Este proyecto está bajo la Licencia Apache 2.0.

## 👥 Autores

**MotoGear Team** - [support@motogear.com](mailto:support@motogear.com)

## 🐛 Problemas

Si encuentras algún bug o tienes alguna sugerencia, por favor abre un issue en el repositorio.

---

Desarrollado con ❤️ por MotoGear Team