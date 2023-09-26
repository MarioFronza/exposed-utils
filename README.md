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
repositories {
    maven { url = URI("https://jitpack.io") }
    mavenCentral()
}

dependencies {
    implementation("com.github.MarioFronza:exposed-utils:1.0.3")
}
```

## Usage

```kotlin
import com.github.eu.DefaultDAO
import com.github.eu.Repository
import com.github.eu.query.pagination.Pagination
import com.github.eu.util.query
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

// Define your domain model
data class User(
    val id: Int = -1,
    val name: String,
    val email: String
)

// Define your database table
object UserTable : IntIdTable() {
    val name = varchar("name", 255)
    val email = varchar("email", 50).index(isUnique = true)
}

// Define your database entity class
class UserEntity(id: EntityID<Int>) : IntEntity(id) {
    var name by UserTable.name
    var email by UserTable.email

    companion object : IntEntityClass<UserEntity>(UserTable)

    fun toUser() = User(id.value, name, email)
}

// Define our custom repository (not required)
interface UserRepository : Repository<User, Int> {
    suspend fun findByEmail(email: String): User?
}

// Your DAO class extending DefaultDAO
class ExposedUserRepository : UserRepository, DefaultDAO<User, Int, UserEntity>(UserEntity) {
    override fun UserEntity.toDomain() = User(id.value, name, email)

    // Create a new User
    override suspend fun create(entity: User) = query {
        val newUser = query {
            UserEntity.new {
                name = entity.name
                email = entity.email
            }
        }
        newUser.toDomain()
    }

    // Update an existing User
    override suspend fun update(entity: User) = query {
        val updatedUser = UserEntity[entity.id].apply {
            name = entity.name
            email = entity.email
        }

        updatedUser.toDomain()
    }

    // The custom findByEmail function
    override suspend fun findByEmail(email: String) = query {
        UserEntity.find { UserTable.name eq email }.firstOrNull()?.toUser()
    }
}

fun main() = runBlocking {
    Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver", user = "root", password = "")
    newSuspendedTransaction {
        SchemaUtils.create(UserTable)
        // Create a new User
        val userRepository = ExposedUserRepository()
        val user = userRepository.create(User(1, "John Doe", "john@test.com"))

        // Retrieve a User by ID
        val retrievedUser = userRepository.findById(1)

        // Retrieve all Users with pagination
        val pagination = Pagination(
            page = 0,
            itemsPerPage = 10
        )
        val usersPage = userRepository.findAll(pagination)
    }
}
```

## Documentation

TODO

## Contributing

We welcome contributions from the community! To build and test the library, follow these steps:

1. Clone the repository.
2. Build the project using `./gradlew build`.
3. Run tests using `./gradlew test`.