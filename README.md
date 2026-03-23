# 🚀 Product Management Backend (Ktor + PostgreSQL)

A backend service built using **Ktor framework in Kotlin** for managing products with image uploads.

This backend powers the **Product Management Android App**, providing secure APIs for product creation, editing, deletion, and image management.

---

# ✨ Features

* 🔐 JWT Authentication
* 👤 User registration & login
* 📦 Product CRUD operations
* 🖼 Multiple product image uploads
* ❌ Remove product images
* 🗑 Delete product with associated images
* 📂 File storage handling
* 🗄 PostgreSQL database integration
* ⚡ Asynchronous request handling using coroutines

---

# 🛠 Tech Stack

**Language**

* Kotlin

**Framework**

* Ktor

**Database**

* PostgreSQL

**ORM / DB Layer**

* Exposed (or your DB access layer)

**Authentication**

* JWT

**File Handling**

* Server side file storage
* URL based image management

**Build Tool**

* Gradle Kotlin DSL

---

# 📂 Project Structure

```id="m8k6q1"
src
 ┣ routes
 ┃ ┣ authRoutes.kt
 ┃ ┗ productRoutes.kt
 ┣ repository
 ┃ ┣ AuthRepository.kt
 ┃ ┗ ProductRepository.kt
 ┣ models
 ┃ ┣ requests
 ┃ ┗ responses
 ┣ services
 ┃ ┗ FileHandler.kt
 ┣ database
 ┃ ┗ DatabaseFactory.kt
 ┗ Application.kt
```

---

# 🔐 Authentication

The API uses **JWT authentication**.

Protected routes require:

```id="d9g7q0"
Authorization: Bearer <token>
```

Token is issued after successful login.

---

# 📦 Product APIs

### Create Product

```id="a2t91f"
POST /create-product
```

Supports **multipart image upload**.

---

### Get Products

```id="n47g2q"
GET /products
```

Returns all products belonging to the authenticated user.

---

### Update Product

```id="bx1po3"
PUT /update-product/{productId}
```

Updates product details.

---

### Remove Product Image

```id="0y4krn"
PUT /remove-product-image
```

Deletes a specific product image.

---

### Delete Product

```id="quh2pi"
DELETE /delete-product/{productId}
```

Deletes the product and all associated images.

---

# 🖼 Image Handling

Images are stored on the server file system.

Each image:

* is uploaded via multipart request
* stored on disk
* saved in database as URL

When a product is deleted:

1. All image URLs are retrieved
2. Files are removed from storage
3. Database records are deleted

---

# ⚙️ Environment Configuration

Environment variables used:

```id="s7d9xm"
DB_URL
DB_USER
DB_PASSWORD
JWT_SECRET
JWT_ISSUER
JWT_AUDIENCE
```

---

# 🧑‍💻 Author

**Sankalp Tiwari**

Android Developer working with:

* Jetpack Compose
* Ktor backend
* Clean architecture
* Full stack Kotlin development

---

# 🎯 Purpose

This backend was created as a **practice and portfolio project** to explore building scalable backend APIs using **Ktor and PostgreSQL**.
