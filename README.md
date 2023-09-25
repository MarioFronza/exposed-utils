# Exposed Utils 🦑

The Exposed Utils Library is a collection of useful functions and utilities designed to enhance the functionality of the
Exposed framework when used with Kotlin. This library simplifies database operations and provides convenient pagination
support.

## Key Features

- Simplified database CRUD operations
- Efficient pagination support

## Installation

You can add the Exposed Utils Library to your project by including the following dependency:

```kotlin
dependencies {
    implementation("com.github:exposed-utils:1.0.0")
}
```

## Usage

```kotlin
// Define your domain model
data class User(val id: Int, val name: String)

// Define your database entity class
object UserEntity : EntityClass<Int, User>(UserTable)

// Define your database table
object UserTable : IntIdTable() {
    val name = varchar("name", 255)
}

// Define our custom repository (not required)
interface UserRepository : Repository<User, Int> {
    suspend fun findByEmail(email: Email): User?
}

// Your DAO class extending DefaultDAO
class ExposedUserRepository : UserRepository, DefaultDAO<User, Int, UserEntity>(UserEntity) {
    override fun UserEntity.toDomain() = User(id.value, name)

    // Create a new User
    suspend fun create(user: User): User {
        val newUserEntity = query {
            UserEntity.new {
                name = user.name
            }
        }
        return newUserEntity.toDomain()
    }

    // Update an existing User
    suspend fun update(user: User): User {
        val existingUserEntity = query {
            UserEntity[user.id]
        } ?: throw EntityNotFoundException(user.id, UserEntity)

        existingUserEntity.apply {
            name = user.name
        }

        return existingUserEntity.toDomain()
    }

    // The custom findByEmail function
    override suspend fun findByEmail(email: Email) = query {
        UserEntity.find { UserTable.email eq email.value }.firstOrNull()?.toUser()
    }
}

fun main() {
    // Create a new User
    val user = UserDao().create(User(1, "John Doe"))

    // Retrieve a User by ID
    val retrievedUser = UserDao().findById(1)

    // Retrieve all Users with pagination
    val pagination = Pagination(page = 1, itemsPerPage = 10, sort = Order.ASC, search = null)
    val usersPage = UserDao().findAll(pagination)
}
```

## Documentation and Setup Guide

TODO

## Contributing

We welcome contributions from the community! To build and test the library, follow these steps:

1. Clone the repository.
2. Build the project using `./gradlew build`.
3. Run tests using `./gradlew test`.