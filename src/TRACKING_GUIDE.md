# Sistema de Tracking de Envíos

## Descripción

Sistema que permite obtener y almacenar información de seguimiento de envíos desde una API externa de scraping.

## Estructura

### Entidad: `Tracking`

Almacena la información completa del tracking en la base de datos:

- `trackingNumber`: Número de seguimiento único
- `orderId`: ID de la orden asociada (opcional)
- `status`: Estado actual del envío (ej: "In transit")
- `statusDescription`: Descripción detallada del estado
- `origin`: País/región de origen
- `destination`: País/región de destino
- `weight`: Peso del paquete
- `daysOnRoute`: Días que lleva en ruta
- `sourceUrl`: URL de la fuente del tracking
- `couriers`: Lista de couriers en formato JSON string
- `timeline`: Historial de eventos en formato JSON string

### DTO: `TrackingResponseDto`

Estructura que representa la respuesta de la API de scraping:

```json
{
  "success": true,
  "data": {
    "trackingNumber": "280320000056634969",
    "status": "In transit",
    "statusDescription": "Import customs clearance complete China",
    "origin": "Mainland China",
    "destination": "Spain",
    "weight": null,
    "couriers": ["Aliexpress Standard Shipping", "Correos Express"],
    "daysOnRoute": 4,
    "timeline": [
      {
        "date": "Nov 24, 2025 5:01 AM",
        "courier": "Aliexpress Standard Shipping",
        "title": "Import customs clearance complete",
        "location": null,
        "isActive": true
      }
    ],
    "sourceUrl": "https://pkge.net/parcel/280320000056634969"
  }
}
```

## API Endpoints

### Scrape Tracking

Obtiene la información de tracking desde la API externa y la guarda en base de datos.

```
POST /api/tracking/scrape?trackingNumber={numero}
```

**Parámetros:**
- `trackingNumber`: Número de seguimiento del envío

**Respuesta:**
```json
{
  "id": 1,
  "trackingNumber": "280320000056634969",
  "orderId": null,
  "status": "In transit",
  "statusDescription": "Import customs clearance complete China",
  "origin": "Mainland China",
  "destination": "Spain",
  "weight": null,
  "daysOnRoute": 4,
  "sourceUrl": "https://pkge.net/parcel/280320000056634969",
  "couriers": "[\"Aliexpress Standard Shipping\",\"Correos Express\"]",
  "timeline": "[{\"date\":\"Nov 24, 2025 5:01 AM\",\"courier\":\"Aliexpress Standard Shipping\",\"title\":\"Import customs clearance complete\",\"location\":null,\"isActive\":true}]",
  "createdAt": "2025-11-24T10:30:00",
  "updatedAt": "2025-11-24T10:30:00"
}
```

## Base de Datos

### Migración: `V1__create_trackings_table.sql`

Crea la tabla `shop.trackings` con:
- Columnas para todos los campos del tracking
- Índices para optimizar búsquedas
- Foreign key con la tabla de órdenes
- Comentarios de documentación

### Índices creados:
- `idx_trackings_tracking_number`: Búsqueda por número de tracking
- `idx_trackings_order_id`: Búsqueda por orden
- `idx_trackings_status`: Filtrado por estado
- `idx_trackings_created_at`: Ordenamiento por fecha

## Configuración

Asegúrate de tener configurada la URL de la API de scraping en `application.properties`:

```properties
scraping.api.url=http://localhost:3000
```

## Uso desde el Frontend

### Ejemplo con Fetch:

```javascript
async function trackShipment(trackingNumber) {
  try {
    const response = await fetch(
      `/api/tracking/scrape?trackingNumber=${trackingNumber}`,
      { method: 'POST' }
    );
    
    const tracking = await response.json();
    
    // Parsear los JSON strings
    const couriers = JSON.parse(tracking.couriers);
    const timeline = JSON.parse(tracking.timeline);
    
    console.log('Couriers:', couriers);
    console.log('Timeline:', timeline);
    
    return tracking;
  } catch (error) {
    console.error('Error al obtener tracking:', error);
    throw error;
  }
}
```

## Notas Importantes

1. **Actualización automática**: Si ya existe un tracking con el mismo número, se actualizará con la nueva información
2. **Formato JSON**: Los campos `couriers` y `timeline` se guardan como strings JSON para facilitar la búsqueda y visualización
3. **Relación con órdenes**: El campo `orderId` es opcional y puede ser `null`
4. **Timestamps**: Los campos `createdAt` y `updatedAt` se manejan automáticamente

## Próximos pasos sugeridos

1. Agregar endpoints para consultar trackings existentes
2. Implementar sistema de caché para reducir llamadas a la API
3. Agregar notificaciones cuando cambie el estado de un envío
4. Crear un job programado para actualizar trackings activos

