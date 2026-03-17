# Gestión de Órdenes – Manual de Usuario

Aplicación web para administrar órdenes, inventario y perfiles de usuario. El backend está implementado en **Spring Boot (Java 17)** y el frontend es un **SPA de Vue 3 + Vite** ubicado en `frontend/`. Usa una base de datos PostgreSQL existente con las tablas `users`, `products`, `orders` y `order_items`.

---

## 1. Requisitos previos

| Herramienta                | Versión sugerida                            |
|---------------------------|---------------------------------------------|
| Git                       | >= 2.30                                      |
| Java                      | 17 (usar `openjdk@17` o similar)             |
| Node.js / npm             | Node >= 18 (se probó con 20.x)               |
| PostgreSQL                | >= 12                                        |
| cURL / Postman (opcional) | Para probar los endpoints                    |

> **Puertos utilizados:**  
> - Backend Spring Boot: `8081` (puede cambiarse en `application.properties`).  
> - Frontend Vite (modo dev): `5173`.  
> Asegúrate de que no estén ocupados por otros servicios (p. ej. Jenkins en 8080).

---

## 2. Clonado del proyecto

```bash
git clone https://github.com/Aleottau/gestion-ordenes-java.git
cd gestion-ordenes-java
```

---

## 3. Configuración del backend (Spring Boot)

1. **Java 17 y Maven Wrapper**  
   El repositorio incluye `mvnw`, por lo que no necesitas Maven instalado globalmente, solo Java 17 configurado:
   ```bash
   export JAVA_HOME="/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home"
   export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"
   ```

2. **Propiedades de la base de datos**  
   Edita `src/main/resources/application.properties` si tus credenciales cambian:
   ```properties
   server.port=8081
   spring.datasource.url=jdbc:postgresql://127.0.0.1:5432/gestionOrdenesDb
   spring.datasource.username=alejandro
   spring.datasource.password=alejandro123
   spring.jpa.hibernate.ddl-auto=none
   spring.jackson.property-naming-strategy=SNAKE_CASE
   ```
   - Asegúrate de que la base tenga las tablas y datos existentes (puedes reutilizar las migraciones del proyecto Laravel original).
   - Si usas otra base/puerto/usuario, ajusta las variables correspondientes.

3. **Arranque del servidor**
   ```bash
   ./mvnw spring-boot:run
   ```
   Verás en consola: `Tomcat started on port 8081 (http)` y `Started Application in ...`.

4. **Pruebas rápidas (opcional)**
   ```bash
   ./mvnw test
   ```

5. **Verificar conexión**
   ```bash
   curl http://localhost:8081/api/products
   ```
   Debería responder con un JSON paginado de productos.

---

## 4. Configuración del frontend (Vue 3 / Vite)

1. **Instala dependencias**
   ```bash
   cd frontend
   npm install
   ```

2. **Variables de entorno (opcional)**  
   Si necesitas apuntar a otro backend, crea `frontend/.env`:
   ```bash
   VITE_API_BASE_URL=http://localhost:8081
   ```
   Por defecto ya usa ese valor, pero puedes cambiarlo a una URL pública.

3. **Modo desarrollo**
   ```bash
   npm run dev -- --host=0.0.0.0
   ```
   Abre `http://localhost:5173/login`.

4. **Compilación para producción**
   ```bash
   npm run build
   npm run preview   # para revisar el build localmente
   ```

---

## 5. Flujo funcional

1. **Autenticación**  
   - Registro (`/register`): crea usuarios con nombre, email/username, teléfono y contraseña (mínimo 6 caracteres).  
   - Login (`/login`): valida credenciales y guarda el usuario en `localStorage` (`go:user`).

2. **Órdenes (`/orders/new`)**  
   - **Crear nueva orden:** seleccionar cliente, fecha, notas y productos (el precio se autocompleta desde inventario).  
   - **Listado / filtros:** filtrar por fechas, estado (`new`, `invoiced`, `cancelled`) y ordenar por nombre, total, número o fecha.  
   - **Gestión de una orden:** cambiar estado a cancelada/facturada. Al facturar se habilita descarga/imprenta de ticket (PDF generado desde el backend).

3. **Inventario**  
   - Ver todos los productos paginados.  
   - Buscar por código o nombre (buscador con sugerencias en los formularios de orden).  
   - Crear, editar y eliminar productos. El campo “creado por” se llena automáticamente con el usuario actual.

4. **Perfil**  
   - Visualiza y edita nombre/teléfono.  
   - Botón para cerrar sesión (elimina `localStorage` y vuelve al login).

---

## 6. Solución de problemas frecuentes

| Problema | Causa probable | Solución |
|----------|----------------|----------|
| `curl ... "Error interno del servidor"` | Excepción en el backend | Revisa la consola de `./mvnw spring-boot:run`; si ves `function lower(bytea)` o `column createdat`, asegúrate de tener el código actualizado y reinicia el servidor. |
| `SQLState: 08001` / `Connection failed` | PostgreSQL apagado o credenciales erróneas | Verifica con `pg_isready` y `psql`; ajusta `application.properties`. |
| `Port 8081 already in use` | Otro servicio (p. ej. Jenkins) ocupa el puerto | Cambia `server.port` en `application.properties` o detén el servicio conflictivo. |
| Frontend muestra pantalla de Vite/Laravel | Ingresaste a `localhost:5173` sin correr el backend original | Asegúrate de usar el nuevo frontend (`frontend/`) y que el backend Java esté corriendo en `8081`. |

---

## 7. Estructura del repositorio

```
gestion-ordenes-java/
├── src/                    # Código Java (Spring Boot)
├── frontend/               # SPA de Vue 3 + Tailwind + Vite
├── pom.xml                 # Configuración Maven
├── mvnw / mvnw.cmd         # Maven Wrapper
└── README.md               # Este manual
```

---

## 8. Próximos pasos / personalización

- **Despliegue**: puedes construir el backend (`./mvnw package`) y servir el `frontend/dist` con cualquier hosting estático. Configura `VITE_API_BASE_URL` con la URL pública del backend.
- **Seguridad**: actualmente las rutas API están abiertas (solo autenticación por frontend). Para un entorno productivo, considera JWT o Spring Security con sesiones.
- **Migraciones**: si necesitas recrear la base, reutiliza las migraciones originales de Laravel o crea scripts DDL equivalentes en este repositorio.

¡Listo! Con estos pasos cualquier persona puede clonar el proyecto, conectar la base de datos existente y ejecutar tanto el backend como el frontend de forma local. Si encuentras un problema, revisa la sección de solución de problemas o abre un issue en el repositorio. 
