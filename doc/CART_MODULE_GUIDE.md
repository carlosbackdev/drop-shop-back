# 🛒 Módulo de Carrito de Compras - Documentación

## 📋 Resumen

El módulo de carrito de compras permite a los usuarios autenticados gestionar sus productos seleccionados antes de realizar una compra.

---

## 🗄️ Estructura de la Base de Datos

### Tabla: `cart`

```sql
CREATE TABLE cart (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_user_product (user_id, product_id)
);
```

**Restricciones:**
- Un usuario NO puede tener el mismo producto duplicado en el carrito
- Si intenta agregar un producto que ya tiene, se actualiza la cantidad
- La cantidad mínima es 1

---

## 🔐 Seguridad

**Todas las rutas del carrito requieren autenticación JWT:**

```
Authorization: Bearer {token}
```

Cada usuario solo puede:
- ✅ Ver su propio carrito
- ✅ Agregar productos a su carrito
- ✅ Modificar items de su carrito
- ✅ Eliminar items de su carrito
- ❌ NO puede ver ni modificar carritos de otros usuarios

---

## 📡 Endpoints Disponibles

### 1. **GET /api/cart** - Obtener Carrito

Retorna todos los items del carrito del usuario autenticado.

**Request:**
```http
GET /api/cart
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "productId": 5,
    "userId": 1,
    "quantity": 2,
    "createdAt": "2024-01-15T10:30:00"
  }
]
```

---

### 2. **POST /api/cart** - Agregar al Carrito

Agrega un producto al carrito. Si ya existe, incrementa la cantidad.

**Request:**
```http
POST /api/cart
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json

{
  "productId": 5,
  "quantity": 2
}
```

**Response:** `201 CREATED`
```json
{
  "id": 1,
  "productId": 5,
  "userId": 1,
  "quantity": 2,
  "createdAt": "2024-01-15T10:30:00"
}
```

**Comportamiento:**
- Si el producto **NO existe** en el carrito → Crea un nuevo item
- Si el producto **YA existe** → Suma la cantidad nueva a la existente
  - Ejemplo: Tenías 2, agregas 3 → Ahora tienes 5

---

### 3. **PUT /api/cart/{cartItemId}?quantity={newQuantity}** - Actualizar Cantidad

Establece una nueva cantidad para un item específico (no suma, reemplaza).

**Request:**
```http
PUT /api/cart/1?quantity=5
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Response:** `200 OK`
```json
{
  "id": 1,
  "productId": 5,
  "userId": 1,
  "quantity": 5,
  "createdAt": "2024-01-15T10:30:00"
}
```

**Validaciones:**
- ✅ La cantidad debe ser >= 1
- ✅ El item debe pertenecer al usuario autenticado

---

### 4. **DELETE /api/cart/product/{productId}** - Eliminar por Producto

Elimina un producto específico del carrito usando el ID del producto.

**Request:**
```http
DELETE /api/cart/product/5
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Response:** `204 NO CONTENT`

---

### 5. **DELETE /api/cart/{cartItemId}** - Eliminar por ID

Elimina un item del carrito usando su ID.

**Request:**
```http
DELETE /api/cart/1
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Response:** `204 NO CONTENT`

**Validaciones:**
- ✅ El item debe existir
- ✅ El item debe pertenecer al usuario autenticado

---

### 6. **DELETE /api/cart** - Vaciar Carrito

Elimina TODOS los items del carrito del usuario.

**Request:**
```http
DELETE /api/cart
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Response:** `204 NO CONTENT`

---

### 7. **GET /api/cart/count** - Contar Items

Retorna la cantidad total de items en el carrito (suma de todas las cantidades).

**Request:**
```http
GET /api/cart/count
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Response:** `200 OK`
```json
7
```

**Ejemplo:**
- Item 1: producto A, cantidad 3
- Item 2: producto B, cantidad 4
- **Total:** 7 items

---

## 💻 Ejemplos de Uso desde el Frontend

### JavaScript/Fetch

```javascript
const API_URL = 'http://localhost:8080/api';
const token = localStorage.getItem('authToken');

// Obtener carrito
const getCart = async () => {
  const response = await fetch(`${API_URL}/cart`, {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  return response.json();
};

// Agregar al carrito
const addToCart = async (productId, quantity = 1) => {
  const response = await fetch(`${API_URL}/cart`, {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({ productId, quantity })
  });
  return response.json();
};

// Actualizar cantidad
const updateQuantity = async (cartItemId, quantity) => {
  const response = await fetch(`${API_URL}/cart/${cartItemId}?quantity=${quantity}`, {
    method: 'PUT',
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  return response.json();
};

// Eliminar item
const removeItem = async (cartItemId) => {
  await fetch(`${API_URL}/cart/${cartItemId}`, {
    method: 'DELETE',
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
};

// Vaciar carrito
const clearCart = async () => {
  await fetch(`${API_URL}/cart`, {
    method: 'DELETE',
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
};

// Contar items
const getCartCount = async () => {
  const response = await fetch(`${API_URL}/cart/count`, {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  return response.json();
};
```

### Axios

```javascript
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Authorization': `Bearer ${localStorage.getItem('authToken')}`
  }
});

// Obtener carrito
const cart = await api.get('/cart');

// Agregar al carrito
const item = await api.post('/cart', { productId: 5, quantity: 2 });

// Actualizar cantidad
const updated = await api.put(`/cart/1?quantity=3`);

// Eliminar item
await api.delete('/cart/1');

// Vaciar carrito
await api.delete('/cart');

// Contar items
const count = await api.get('/cart/count');
```

---

## 🔄 Flujos Comunes

### Flujo 1: Agregar Producto al Carrito

```
Usuario hace clic en "Agregar al carrito"
    ↓
Frontend envía: POST /api/cart { productId: 5, quantity: 1 }
    ↓
Backend verifica JWT → Obtiene userId
    ↓
Backend busca si existe cart item con (userId, productId)
    ↓
    ├─ Si existe → Suma cantidad existente + nueva
    └─ Si NO existe → Crea nuevo item
    ↓
Retorna el item creado/actualizado
    ↓
Frontend actualiza badge del carrito
```

### Flujo 2: Ver Carrito

```
Usuario navega a página de carrito
    ↓
Frontend: GET /api/cart
    ↓
Backend retorna lista de items del usuario
    ↓
Frontend obtiene detalles de productos desde /api/products/{id}
    ↓
Frontend muestra carrito con imágenes, nombres y precios
```

### Flujo 3: Actualizar Cantidad

```
Usuario cambia cantidad en el carrito
    ↓
Frontend: PUT /api/cart/{id}?quantity=5
    ↓
Backend valida que item pertenece al usuario
    ↓
Actualiza cantidad
    ↓
Frontend refresca carrito
```

---

## ⚠️ Consideraciones Importantes

1. **Relaciones:**
   - `productId` es una clave foránea conceptual (no hay FK en BD por ahora)
   - `userId` es una clave foránea conceptual
   - La validación de que el producto existe debe hacerse en el frontend o añadir lógica en el backend

2. **Sincronización:**
   - El carrito se guarda en la base de datos (no en localStorage)
   - Si el usuario cierra sesión y vuelve a entrar, su carrito persiste
   - Si el usuario se loguea desde otro dispositivo, ve el mismo carrito

3. **Mejoras Futuras:**
   - Añadir campo `updatedAt` para tracking
   - Añadir precio snapshot (guardar precio al momento de agregar)
   - Añadir validación de stock antes de agregar
   - Implementar carrito anónimo con migración al login

4. **Testing:**
   - Todas las operaciones requieren JWT válido
   - Un usuario NO puede modificar items de otro usuario
   - La constraint UNIQUE evita duplicados en BD

---

## 🎯 Resumen de Métodos del Servicio

```java
CartService:
├─ getCartItems()                              → Lista items del usuario
├─ addToCart(productId, quantity)              → Agregar/Incrementar
├─ updateCartItem(cartItemId, quantity)        → Actualizar cantidad
├─ removeFromCart(productId)                   → Eliminar por productId
├─ removeCartItem(cartItemId)                  → Eliminar por cartItemId
├─ clearCart()                                 → Vaciar carrito
└─ getCartItemCount()                          → Contar total items
```

---

## 📚 Referencias

- **Entity:** `Cart.java` - Modelo de datos
- **Repository:** `CartRepository.java` - Operaciones de BD
- **Service:** `CartService.java` - Lógica de negocio
- **Controller:** `CartController.java` - Endpoints REST
- **DTOs:** `AddToCartRequest.java`, `CartItemResponse.java`

---

✅ **El módulo está completo y listo para usar!**

