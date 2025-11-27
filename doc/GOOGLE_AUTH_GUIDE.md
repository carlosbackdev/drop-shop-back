# 🔥 Guía Completa: Autenticación con Google/Firebase

## 📋 Resumen

Esta guía explica cómo obtener el **email del usuario** cuando se autentica con Google y cómo sincronizarlo con tu backend.

---

## 🎯 Flujo Completo de Autenticación

```
┌─────────┐      ┌──────────┐      ┌─────────────┐      ┌─────────┐
│ Usuario │─────▶│ Frontend │─────▶│   Google    │─────▶│ Backend │
└─────────┘      └──────────┘      └─────────────┘      └─────────┘
    │                 │                    │                   │
    │ 1. Click       │                    │                   │
    │  "Login"       │                    │                   │
    ├───────────────▶│                    │                   │
    │                │ 2. Popup Google   │                   │
    │                ├──────────────────▶│                   │
    │                │                    │                   │
    │                │ 3. Email verificado│                   │
    │                │◀───────────────────┤                   │
    │                │   user.email       │                   │
    │                │   user.displayName │                   │
    │                │   user.photoURL    │                   │
    │                │                    │                   │
    │                │ 4. POST /firebase-login                │
    │                │    { email, name } │                   │
    │                ├───────────────────────────────────────▶│
    │                │                    │                   │
    │                │                    │  5. Guardar en BD │
    │                │                    │     o actualizar  │
    │                │                    │                   │
    │                │ 6. JWT del backend │                   │
    │                │◀───────────────────────────────────────┤
    │                │   { token, user }  │                   │
    │ 7. Autenticado │                    │                   │
    │◀───────────────┤                    │                   │
```



### ¿Por qué puedes confiar en este email?

1. ✅ **Google verifica** que el usuario es dueño del email
2. ✅ **Firebase valida** la autenticación con Google
3. ✅ El email viene en el **token JWT de Firebase** (no es editable)

---

## 💻 Implementación en el Frontend

### Opción 1: Con Firebase (Recomendado)



### Opción 2: Con Google Sign-In (Sin Firebase)

```html
<!-- Incluir la librería de Google en tu HTML -->
<script src="https://accounts.google.com/gsi/client" async defer></script>
```

---

## 🔧 Implementación en el Backend (YA HECHA)

### Endpoint: `POST /api/auth/firebase-login`


## 🛡️ Validación Adicional del Token (Opcional)

Para **mayor seguridad**, puedes validar el token de Google en el backend:


### 2. Configurar el Client ID de Google:

```properties
# application.properties
google.client.id=TU_CLIENT_ID.apps.googleusercontent.com
```

### 3. Usar el `GoogleTokenValidator` (ya creado):

```java
@PostMapping("/firebase-login")
public ResponseEntity<AuthResponse> firebaseLogin(@Valid @RequestBody FirebaseLoginRequest request) {
    // Validar el token de Google
    GoogleUserInfo googleInfo = googleTokenValidator.validateToken(request.getFirebaseToken());
    
    // Verificar que el email coincida
    if (!googleInfo.getEmail().equals(request.getEmail())) {
        throw new BadCredentialsException("El email no coincide con el token de Google");
    }
    
    // Continuar con el registro/login...
}
```

---

## 📊 Tabla de Usuarios en la Base de Datos

```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,     -- ⬅️ EMAIL de Google
    password VARCHAR(255) NOT NULL,          -- Password random si es Google
    full_name VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    firebase_uid VARCHAR(255),               -- UID de Firebase (opcional)
    photo_url VARCHAR(500),                  -- Foto de perfil de Google
    auth_provider VARCHAR(50),               -- "GOOGLE", "LOCAL", "FIREBASE"
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

---

## ✅ Resumen

1. **Frontend**: Firebase/Google te da el email en `user.email`
2. **Frontend**: Envías ese email a `/api/auth/firebase-login`
3. **Backend**: Busca o crea usuario con ese email
4. **Backend**: Retorna JWT propio
5. **Frontend**: Usa ese JWT en todas las peticiones

El email está **garantizado y verificado por Google**, por eso puedes confiar en él directamente.

