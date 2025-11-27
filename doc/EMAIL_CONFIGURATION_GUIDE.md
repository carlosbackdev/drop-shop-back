# Guía de Configuración de Emails - Drop Shop

## ✅ Implementación Completada

Se ha creado un sistema completo de emails profesionales con plantilla HTML y tono muy formal para tu tienda ecommerce.

---

## 📋 Archivos Creados/Modificados

### 1. **FormatterEmail.java** ✅
**Ubicación:** `src/main/java/com/motogear/dropshopback/common/messages/event/FormatterEmail.java`

**Funcionalidad:**
- Genera emails HTML profesionales con diseño responsive
- Calcula automáticamente subtotales y costos de envío
- Formatea precios en formato español (€)
- Incluye tabla de productos con variantes
- Dirección de envío completa
- Sección de soporte al cliente
- Prevención de XSS con escape de HTML

**Características:**
- ✅ Tono muy formal de ecommerce profesional
- ✅ Diseño limpio con cabecera oscura
- ✅ Tabla de productos con colores alternados
- ✅ Totales destacados en verde (#28a745)
- ✅ Sección de soporte destacada en amarillo
- ✅ Footer corporativo con copyright

---

## ⚙️ Configuración en application.properties

### Propiedades Añadidas:

```properties
# Store Configuration
app.store.name=Drop Shop
app.store.url=https://drop-shop.com
app.support.email=soporte@drop-shop.com
app.support.phone=+34 900 000 000
```

### Personalización:

1. **Nombre de la tienda:** Cambia `app.store.name` por el nombre de tu tienda
2. **URL de la tienda:** Cambia `app.store.url` por tu dominio
3. **Email de soporte:** Cambia `app.support.email` por tu email real
4. **Teléfono:** Cambia `app.support.phone` por tu teléfono de atención

---

## 📧 Configuración de Email (Gmail/Outlook)

### Para Gmail:

```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=tu-email@gmail.com
spring.mail.password=tu-app-password
```

**⚠️ Importante para Gmail:**
- NO uses tu contraseña normal de Gmail
- Debes crear una **"Contraseña de aplicación"**
- Ve a: Cuenta de Google → Seguridad → Verificación en 2 pasos → Contraseñas de aplicaciones
- Genera una contraseña nueva para "Correo"
- Usa esa contraseña en `spring.mail.password`

### Para Outlook/Hotmail:

```properties
spring.mail.host=smtp-mail.outlook.com
spring.mail.port=587
spring.mail.username=tu-email@outlook.com
spring.mail.password=tu-contraseña
```

**⚠️ Importante para Outlook:**
- Asegúrate de que tu cuenta NO tenga restricciones
- Ve a: Configuración → Ver toda la configuración → Correo → Sincronización de correo
- Habilita "Permitir que dispositivos y aplicaciones usen POP"

### Para Otros Proveedores:

| Proveedor | Host | Puerto |
|-----------|------|--------|
| Yahoo | smtp.mail.yahoo.com | 587 |
| Zoho | smtp.zoho.com | 587 |
| Protonmail | smtp.protonmail.ch | 587 |

---

## 🎨 Diseño del Email

### Estructura:

1. **Header negro** con el nombre de la tienda
2. **Caja de información del pedido** (verde a la izquierda)
3. **Saludo formal:** "Estimado/a [Nombre]"
4. **Tabla de productos** con:
   - Producto
   - Variante (talla/color)
   - Cantidad
   - Precio unitario
   - Total línea
5. **Sección de totales:**
   - Subtotal
   - Gastos de envío
   - **TOTAL PAGADO** (destacado en verde)
6. **Información de envío:**
   - Destinatario
   - Email
   - Dirección completa
   - Teléfono
7. **Próximos pasos:** Información sobre el proceso
8. **Sección de soporte** (fondo amarillo claro)
9. **Footer corporativo** con copyright

### Colores:

- **Header:** #1a1a1a (negro)
- **Fondo:** #f4f4f4 (gris claro)
- **Total:** #28a745 (verde)
- **Soporte:** #fff9e6 (amarillo claro)
- **Bordes:** #e0e0e0 (gris suave)

---

## 🔄 Flujo de Envío

```java
@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
public void onOrderPaidEvent(OrderEvent event) {
    // 1. Obtener datos del pedido
    Order order = dataMailService.getOrder(orderId);
    User user = dataMailService.getUser(order.getUserId());
    Checkout checkout = dataMailService.getCheckout(order);
    Map<CartShaded, Product> cartProductMap = dataMailService.getCartShadedAndProduct(order);

    // 2. Generar HTML del email
    String body = formatEmailService.buildBodyMail(user, order, checkout, cartProductMap);
    
    // 3. Enviar emails
    mailService.sendEmail(fromEmail, "Confirmación de pago – Pedido #" + orderId, body);
    mailService.sendEmail(user.getEmail(), "Confirmación de pago – Pedido #" + orderId, body);
}
```

---

## 🧪 Probar el Email

### 1. Configurar variables de entorno:

```bash
# Windows PowerShell
$env:MAIL_HOST="smtp.gmail.com"
$env:MAIL_PORT="587"
$env:MAIL_USERNAME="tu-email@gmail.com"
$env:MAIL_PASSWORD="tu-app-password"
```

### 2. Realizar un pago de prueba con Stripe

### 3. Ver el email enviado

El email se enviará a:
- ✅ Tu email configurado (`spring.mail.username`) - copia para la tienda
- ✅ Email del cliente (`user.getEmail()`) - confirmación para el cliente

---

## 🛠️ Personalizar el Email

### Cambiar el tono (más cercano):

Edita el método `getBaseTemplate()` en `FormatterEmail.java`:

```java
// Cambiar de:
"Estimado/a <strong>{{customerName}}</strong>,"

// A:
"Hola <strong>{{customerName}}</strong>,"
```

### Cambiar colores:

```css
/* Header */
.header {
  background-color: #tu-color-hex;
}

/* Total */
.totals .total-row .value {
  color: #tu-color-hex;
}
```

### Añadir logo:

En el header, añade:

```html
<div class="header">
  <img src="https://tu-dominio.com/logo.png" alt="Logo" style="max-width: 200px;">
  <h1>{{storeName}}</h1>
</div>
```

---

## 🔒 Seguridad

### Variables de entorno recomendadas:

```properties
# NO incluir en git
spring.mail.username=${MAIL_USERNAME}
spring.mail.password=${MAIL_PASSWORD}
app.support.email=${SUPPORT_EMAIL}
```

### Prevención de XSS:

El método `escapeHtml()` convierte automáticamente:
- `&` → `&amp;`
- `<` → `&lt;`
- `>` → `&gt;`
- `"` → `&quot;`
- `'` → `&#x27;`

---

## 📊 Datos Incluidos en el Email

### Del Usuario (User):
- ✅ Nombre completo (`fullName`)
- ✅ Email (`email`)

### De la Orden (Order):
- ✅ ID del pedido
- ✅ Fecha de creación
- ✅ Total pagado

### Del Checkout:
- ✅ Dirección completa
- ✅ Código postal
- ✅ Ciudad
- ✅ País
- ✅ Teléfono

### De los Productos:
- ✅ Nombre del producto
- ✅ Variante (talla/color)
- ✅ Cantidad
- ✅ Precio unitario
- ✅ Precio total línea
- ✅ Costo de envío por producto

---

## ❓ Troubleshooting

### ⚠️ El email muestra el código HTML literal (texto plano):

**Problema:** El email se recibe mostrando todo el código HTML (`<!DOCTYPE html>...`) en lugar del diseño.

**Causa:** El `MailServiceImpl` usa `SimpleMailMessage` que solo envía texto plano.

**Solución:** ✅ Ya está aplicada. El `MailServiceImpl` ahora usa `MimeMessage` con `MimeMessageHelper`:

```java
@Override
public void sendEmail(String to, String subject, String body) {
    try {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body, true); // true = HTML content
        
        mailSender.send(message);
    } catch (MessagingException e) {
        throw new RuntimeException("Error al enviar el email a: " + to, e);
    }
}
```

**Nota:** El segundo parámetro `true` en `helper.setText(body, true)` indica que el contenido es HTML.

---

### Email no se envía:

1. Verifica las credenciales en variables de entorno
2. Para Gmail: usa contraseña de aplicación
3. Verifica que el puerto sea 587 (TLS)
4. Comprueba los logs: `ERROR ... MailService`

### Email llega a spam:

1. Configura SPF/DKIM en tu dominio
2. Usa un email del mismo dominio (no Gmail para producción)
3. Añade un email de respuesta válido

### Email sin formato (después de la corrección):

Si aún ves problemas con el formato:
1. Verifica que el navegador/cliente de email soporte HTML
2. Algunos clientes corporativos bloquean HTML por seguridad
3. Prueba con Gmail web, Outlook web o otro cliente

---

## 📝 Ejemplo de Email Generado

**Asunto:** Confirmación de pago – Pedido #17

**Cuerpo:**
```
┌─────────────────────────────────────┐
│         Drop Shop                   │
└─────────────────────────────────────┘

┌─────────────────────────────────────┐
│ Pedido confirmado: #17              │
│ Fecha: 24/11/2025 01:16             │
│ Estado: Pago recibido correctamente │
└─────────────────────────────────────┘

Estimado/a Carlos,

Le informamos que hemos recibido correctamente 
el pago correspondiente a su pedido #17...

[Tabla de productos]
[Totales]
[Información de envío]
[Soporte]
```

---

## ✅ Checklist de Implementación

- [x] Clase `FormatterEmail` creada
- [x] Plantilla HTML profesional implementada
- [x] Configuración en `application.properties`
- [x] Listener de eventos configurado
- [x] Cálculo de totales automático
- [x] Formato de precios en español
- [x] Prevención de XSS
- [x] Diseño responsive
- [ ] Configurar variables de entorno de email
- [ ] Probar con un pago real
- [ ] Personalizar colores/logo (opcional)

---

## 🎯 Próximos Pasos

1. **Configurar tu email real** en variables de entorno
2. **Personalizar** nombre de tienda, URL y contacto
3. **Probar** con un pago de prueba
4. **Opcional:** Añadir logo y ajustar colores corporativos
5. **Opcional:** Crear más plantillas (envío realizado, cancelación, etc.)

---

¡Email profesional listo para producción! 🚀

