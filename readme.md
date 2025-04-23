# 📦 Sistema de Control de Inventario - Technix System

Sistema backend de gestión de inventario desarrollado con **Spring Boot 3** y **Java 17**, que permite administrar productos, categorías, proveedores, usuarios y movimientos de inventario.  
Incluye autenticación con JWT, alertas de stock bajo, y documentación de API con Swagger.

---

## 🚀 Características principales

- ✅ Gestión de productos, categorías y proveedores
- 🔐 Autenticación y autorización con JWT
- 📉 Alertas automáticas por bajo stock (configurable por producto)
- 📦 Registro de movimientos de inventario (entradas, salidas, ajustes)
- 🧑‍💼 Gestión de usuarios y roles
- 📄 Documentación interactiva con Swagger 3
- 💾 Persistencia con MySQL y MongoDB (según entidad)
- ☁️ Arquitectura RESTful con DTOs y validaciones

---

## 📂 Estructura del proyecto

src/main/java/pe/com/lacunza/technix/ <br>
├── api/ # Controladores REST y endpoints <br>
├── config/ # Configuraciones generales (CORS, Mongo, Swagger, etc.) <br>
├── domain/ # Entidades y modelos de dominio <br>
├── dto/ # Clases DTO para entrada/salida <br>
├── services/ # Lógica de negocio y servicios de la aplicación <br>
├── util/ # Clases utilitarias y herramientas comunes <br>
└── TecnixSystemApplication.java # Punto de entrada principal de la aplicación

---

## 🧪 Controladores disponibles

| Controlador           | Descripción |
|-----------------------|-------------|
| `UserAuthController`  | Registro, login, refresh token |
| `UserController`      | Gestión de usuarios (admin) |
| `ProductController`   | CRUD de productos |
| `CategoryController`  | CRUD de categorías de productos |
| `SupplierController`  | CRUD de proveedores |
| `InventoryController` | Registro de movimientos de inventario |
| `AlertConfigurationController` | Configuración del stock mínimo por producto |

---

## 🔐 Autenticación

El sistema utiliza **JWT (JSON Web Tokens)** para autenticación.  
Una vez logueado, deberás enviar el token en el header:
Authorization: **Bearer <tu-token>**

---

## 📄 Documentación de API

Swagger UI está disponible en:
http://localhost:8080/technix-system/swagger-ui/index.html

Desde ahí puedes probar todos los endpoints protegidos y públicos.

---

## 🛠️ Tecnologías usadas

- ☕ Java 17
- 🌱 Spring Boot 3
- 🛡️ Spring Security + JWT
- 📦 Maven
- 🐬 MySQL (JPA)
- 🍃 MongoDB (para logs o modelos específicos)
- 📚 Swagger OpenAPI 3 (`springdoc`)
- 🔐 Lombok

---

## ⚙️ Requisitos

- Java 17
- Maven 3+
- MySQL 8+
- MongoDB (si usas modelos en Mongo)
- Postman o Swagger para testear API

---

## ▶️ Cómo ejecutar el proyecto

1. Clona el repositorio:

```bash
git clone https://github.com/Megex04/technix-system.git
```

2. Configura tu archivo application.properties:
```
# Puerto y contexto
server.port=8080
server.servlet.context-path=/technix-system

# Base de datos MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/inventory_db
spring.datasource.username=root
spring.datasource.password=tu_password

# MongoDB
spring.data.mongodb.uri=mongodb://localhost:27017/inventory_db

# JWT
app.jwt.secret=clave-super-secreta
app.jwt.expiration=900000
app.jwt.refresh-expiration=3600000

# To send emails in AlertConfigurations
app.technix.valid-emails=example@mail.com,example2@mail.com
app.tecnix.passowrd.tmp.smtp.gmail=xxxx yyyy zzzz
```

3. Ejecuta la app:
```
mvn spring-boot:run
```

---

## 🧠 Notas adicionales

- 🔐 **Autenticación segura**: Las contraseñas se encriptan usando el algoritmo **BCrypt**.
- 📧 **Notificaciones por correo**: Se pueden enviar alertas de stock bajo a una lista de correos configurada en las propiedades del sistema.
- 🔄 **JWT Refresh Token**: Implementado para permitir sesiones prolongadas sin necesidad de re-login.
- 📋 **Validaciones**: Se usan anotaciones como `@Valid`, `@NotNull`, `@Email`, etc., para asegurar integridad de los datos en las peticiones.
- 🧱 **Arquitectura limpia**: Separación clara por capas:
    - `controller` → expone la API
    - `service` → contiene la lógica de negocio
    - `repository` → acceso a datos (JPA o Mongo)
    - `dto` / `models` → objetos para entrada/salida de datos

---

## ✨ Autor

👤 Desarrollado por **[Miguel La Cunza Alfaro]**  
📧 Contacto: [lacunzamiguel04@gmail.com](mailto:lacunzamiguel04@gmail.com)  
🔗 GitHub: [https://github.com/Megex04](https://github.com/Megex04)


---

Gracias por visitar este repositorio ❤️