import java.util.Scanner

fun main() {
    val app = BusApplication()
    app.loadData()

    val scanner = Scanner(System.`in`)
    var option = -1

    while (option != 0) {
        println("\n====================================")
        println("   SISTEM REZERVARI AUTOBUZ (Kotlin)")
        println("====================================")
        println("1. Login OPERATOR")
        println("2. Login CLIENT")
        println("3. Creeaza Cont CLIENT")
        println("0. Iesire")
        print("Alegeti optiunea: ")

        option = scanner.nextLine().toIntOrNull() ?: -1

        when (option) {
            1 -> {
                if (app.login("OPERATOR")) {
                    var opOption = -1
                    while (opOption != 0) {
                        println("\n--- Meniu OPERATOR ---")
                        println("1. Adauga Calatorie")
                        println("2. Sterge Calatorie")
                        println("0. Logout")
                        print("Optiune: ")
                        opOption = scanner.nextLine().toIntOrNull() ?: -1
                        when (opOption) {
                            1 -> app.addTrip()
                            2 -> app.removeTrip()
                            0 -> app.logout()
                            else -> println("Optiune invalida.")
                        }
                    }
                }
            }
            2 -> {
                if (app.login("CLIENT")) {
                    var clOption = -1
                    while (clOption != 0) {
                        println("\n--- Meniu CLIENT ---")
                        println("1. Cauta Calatorii")
                        println("2. Rezerva Calatorie")
                        println("0. Logout")
                        print("Optiune: ")
                        clOption = scanner.nextLine().toIntOrNull() ?: -1
                        when (clOption) {
                            1 -> app.searchTrips()
                            2 -> app.bookTrip()
                            0 -> app.logout()
                            else -> println("Optiune invalida.")
                        }
                    }
                }
            }
            3 -> app.createAccount()
            0 -> println("La revedere!")
            else -> println("Optiune invalida.")
        }
    }
}