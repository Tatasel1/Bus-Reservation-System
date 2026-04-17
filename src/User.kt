abstract class User(
    open val name: String,
    open val email: String,
    open val passwordHash: String
)

/// data class se foloseste pentru o clasa care doar stocheaza date
data class Client(
    override val name: String,
    override val email: String,
    override val passwordHash: String
) : User(name, email, passwordHash)

data class Operator(
    override val name: String,
    override val email: String,
    override val passwordHash: String
) : User(name, email, passwordHash)