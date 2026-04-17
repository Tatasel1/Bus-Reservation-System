/// Folosesc object deoarece am nevoie doar de o instanta a clasei security si object in kt chiar asta face
object SecurityUtils {
    fun hashPassword(password: String): String {
        val sb = StringBuilder()
        for (char in password) {
            sb.append((char.code + 3).toString())
        }
        return sb.toString()
    }

    /// Generics din java :)))
    fun <T : CharSequence> verifyPasswordStrength(inputPassword: T): String {
        val password = inputPassword.toString()

        if (password.length < 8) {
            throw PasswordWeakException()
        }

        var hasUpper = false
        var hasLower = false
        var hasDigit = false
        var hasSpecial = false

        for (c in password) {
            when {
                c.isUpperCase() -> hasUpper = true
                c.isLowerCase() -> hasLower = true
                c.isDigit() -> hasDigit = true
                !c.isLetterOrDigit() -> hasSpecial = true
            }
        }

        var score = 0
        if (hasUpper) score++
        if (hasLower) score++
        if (hasDigit) score++
        if (hasSpecial) score++

        return when {
            score == 4 -> "good"
            score >= 2 -> "ok"
            else -> "weak"
        }
    }

    fun isEmailValid(email: String): Boolean {
        return email.contains("@") && email.contains(".") &&
                email.indexOf("@") < email.lastIndexOf(".")
    }

    fun isCityValid(city: String): Boolean {
        val regex = "^[a-zA-Z\\s-]+$".toRegex()
        return city.length >= 3 && city.matches(regex)
    }
}