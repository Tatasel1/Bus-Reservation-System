import java.io.File
import java.io.FileWriter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Scanner

class BusApplication {
    private val users = mutableListOf<User>()
    private val trips = mutableListOf<Trip>()
    private val reservations = mutableListOf<Reservation>()

    private var currentUser: User? = null
    private val scanner = Scanner(System.`in`)
    private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy-HH:mm")

    // --- Incarcare Date ---
    fun loadData() {
        try {
            loadOperators()
            loadClients()
            loadTrips()
            loadReservations()
            println("Date incarcate cu succes.")
        } catch (e: Exception) {
            println("Nota: Nu s-au putut incarca toate datele (fisiere lipsa sau format incorect).")
        }
    }

    private fun loadOperators() {
        val file = File("operator.csv")
        if (!file.exists()) return

        /// Functie lambda, de asta am nevoie de @forEachLine, return ar iesii de tot din functie, ceea ce nu vreau
        file.forEachLine { line ->
            if (line.startsWith("name")) return@forEachLine /// Echivalentul continue
            val parts = line.split(",")
            if (parts.size >= 3) {
                users.add(Operator(parts[0].trim(), parts[1].trim(), parts[2].trim()))
            }
        }
    }

    private fun loadClients() {
        val file = File("users.csv")
        if (!file.exists()) return
        file.forEachLine { line ->
            if (line.startsWith("name")) return@forEachLine
            val parts = line.split(",")
            if (parts.size >= 3) {
                users.add(Client(parts[0].trim(), parts[1].trim(), parts[2].trim()))
            }
        }
    }

    private fun loadTrips() {
        val file = File("calatorii.csv")
        if (!file.exists()) return
        file.forEachLine { line ->
            if (line.startsWith("id")) return@forEachLine
            val parts = line.split(",")
            if (parts.size >= 5) {
                trips.add(Trip(parts[0].toInt(), parts[1], parts[2], parts[3], parts[4].toInt()))
            }
        }
    }

    private fun loadReservations() {
        val file = File("rezervari.csv")
        if (!file.exists()) return
        file.forEachLine { line ->
            if (line.startsWith("idCalatorie") || line.isBlank()) return@forEachLine
            val parts = line.split(",")
            if (parts.size >= 3) {
                reservations.add(Reservation(parts[0].toInt(), parts[1], parts[2]))
            }
        }
    }

    // --- Salvare Date ---
    private fun saveClients() {
        try {
            FileWriter("users.csv").use { writer ->
                writer.write("name,email,password\n")
                users.filterIsInstance<Client>().forEach { u ->
                    writer.write("${u.name},${u.email},${u.passwordHash}\n")
                }
            }
        } catch (e: Exception) { println("Eroare la salvare users: ${e.message}") }
    }

    private fun saveTrips() {
        try {
            FileWriter("calatorii.csv").use { writer ->
                writer.write("id,start,stop,date,seats\n")
                trips.forEach { t ->
                    writer.write("${t.id},${t.startCity},${t.stopCity},${t.date},${t.seats}\n")
                }
            }
        } catch (e: Exception) { println("Eroare la salvare calatorii: ${e.message}") }
    }

    private fun saveReservations() {
        try {
            FileWriter("rezervari.csv", true).use { writer ->
                val last = reservations.lastOrNull()
                if (last != null) {
                    writer.write("${last.tripId},${last.clientName},${last.bookingDate}\n")
                }
            }
        } catch (e: Exception) { println("Eroare la salvare rezervari: ${e.message}") }
    }

    // --- Login & Cont ---
    fun login(requiredType: String): Boolean {
        print("Email: ")
        val email = scanner.nextLine().trim()
        print("Parola: ")
        val password = scanner.nextLine().trim()

        try {
            val user = users.find { it.email == email } ?: throw UserNotFoundException()

            if (requiredType == "OPERATOR" && user !is Operator) throw UserNotFoundException()
            if (requiredType == "CLIENT" && user !is Client) throw UserNotFoundException()

            val inputHash = SecurityUtils.hashPassword(password)
            if (user.passwordHash != inputHash) {
                throw InvalidCredentialsException()
            }

            currentUser = user
            println("Autentificare reusita! Bun venit, ${user.name}.")
            return true

        } catch (e: Exception) {
            println(e.message)
            return false
        }
    }

    fun createAccount() {
        try {
            print("Nume: ")
            val name = scanner.nextLine().trim()
            print("Email: ")
            val email = scanner.nextLine().trim()

            if (!SecurityUtils.isEmailValid(email)) throw InvalidEmailException()
            if (users.any { it.email == email }) throw UserAlreadyExistsException()

            print("Parola: ")
            val pass = scanner.nextLine().trim()

            val strength = SecurityUtils.verifyPasswordStrength(pass)
            println("Putere parola: $strength")

            if (strength == "weak") throw PasswordWeakException()

            print("Confirma Parola: ")
            val passConf = scanner.nextLine().trim()
            if (pass != passConf) throw PasswordMismatchException()

            val hashed = SecurityUtils.hashPassword(pass)
            val newClient = Client(name, email, hashed)
            users.add(newClient)
            saveClients()
            println("Cont creat cu succes!")

        } catch (e: Exception) {
            println(e.message)
        }
    }

    fun logout() {
        currentUser = null
        println("Delogare efectuata.")
    }

    // --- Operator ---
    fun addTrip() {
        if (currentUser !is Operator) return
        try {
            print("Oras Plecare: ")
            val start = scanner.nextLine().trim()
            print("Oras Sosire: ")
            val stop = scanner.nextLine().trim()

            if (!SecurityUtils.isCityValid(start) || !SecurityUtils.isCityValid(stop)) {
                throw WrongDetailsException("Numele oraselor contin caractere invalide sau sunt prea scurte.")
            }

            print("Data (dd/MM/yyyy-HH:mm): ")
            val dateStr = scanner.nextLine().trim()

            val tripDate = LocalDateTime.parse(dateStr, dateFormatter)
            if (tripDate.isBefore(LocalDateTime.now())) {
                throw WrongDetailsException("Data calatoriei este in trecut.")
            }

            print("Numar Locuri: ")
            val seatsStr = scanner.nextLine().trim()
            val seats = seatsStr.toIntOrNull() ?: throw WrongDetailsException("Numar locuri invalid.")

            val newId = (trips.maxOfOrNull { it.id } ?: 0) + 1
            trips.add(Trip(newId, start, stop, dateStr, seats))
            saveTrips()
            println("Cursa adaugata cu succes!")

        } catch (e: DateTimeParseException) {
            println("Eroare: Format data gresit. Folositi dd/MM/yyyy-HH:mm")
        } catch (e: Exception) {
            println(e.message)
        }
    }

    fun removeTrip() {
        if (currentUser !is Operator) return

        println("\n--- Stergere Cursa ---")
        if (trips.isEmpty()) {
            println("Nu exista curse de sters.")
            return
        }
        trips.forEach { println(it) }

        print("\nIntroduceti ID-ul cursei pe care doriti sa o stergeti: ")
        val input = scanner.nextLine().trim()
        val idToDelete = input.toIntOrNull()

        if (idToDelete == null) {
            println("ID invalid.")
            return
        }

        val trip = trips.find { it.id == idToDelete }

        if (trip != null) {
            trips.remove(trip)

            val removedCount = reservations.count { it.tripId == idToDelete }
            reservations.removeIf { it.tripId == idToDelete }

            saveTrips()
            saveReservations()

            println("Cursa cu ID $idToDelete a fost stearsa.")
            if (removedCount > 0) {
                println("Au fost anulate automat si $removedCount rezervari asociate.")
            }
        } else {
            println("Eroare: Cursa cu ID-ul $idToDelete nu a fost gasita.")
        }
    }

    // --- Client ---
    fun searchTrips() {
        if (trips.isEmpty()) {
            println("Nu exista curse disponibile.")
            return
        }
        println("\n--- Lista Curse ---")
        trips.forEach { println(it) }
    }

    fun bookTrip() {
        if (currentUser !is Client) return
        try {
            print("Oras Plecare dorit: ")
            val start = scanner.nextLine().trim()
            print("Oras Sosire dorit: ")
            val stop = scanner.nextLine().trim()

            val foundTrips = trips.filter {
                it.startCity.equals(start, ignoreCase = true) &&
                        it.stopCity.equals(stop, ignoreCase = true)
            }

            if (foundTrips.isEmpty()) throw WrongDetailsException("Nu exista curse pe aceasta ruta.")

            foundTrips.forEach { println(it) }

            print("Introduceti ID-ul cursei: ")
            val idInput = scanner.nextLine().toIntOrNull() ?: throw WrongDetailsException("ID invalid.")

            val selectedTrip = foundTrips.find { it.id == idInput }
                ?: throw WrongDetailsException("Cursa cu acest ID nu a fost gasita in rezultatele cautarii.")

            if (selectedTrip.seats <= 0) {
                println("Ne pare rau, nu mai sunt locuri disponibile.")
                return
            }

            selectedTrip.seats -= 1
            val nowStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))

            reservations.add(Reservation(selectedTrip.id, currentUser!!.name, nowStr))

            saveTrips()
            saveReservations()

            println("Rezervare efectuata cu succes la ora $nowStr!")

        } catch (e: Exception) {
            println(e.message)
        }
    }
}