@file:Suppress("UNNECESSARY_SAFE_CALL")

package taxipark

/*
 * Task #1. Find all the drivers who performed no trips.
 */
fun TaxiPark.findFakeDrivers(): Set<Driver> =
    allDrivers.filter { driver: Driver -> trips.none { trip -> trip.driver == driver } }.toSet()
//we could also use allDrivers - trips.map {it.driver} //does only one iteration while the other does two iterations and one transformation

/*
 * Task #2. Find all the clients who completed at least the given number of trips.
 */
fun TaxiPark.findFaithfulPassengers(minTrips: Int): Set<Passenger> =
    allPassengers.filter { passenger -> trips.count { trip -> passenger in trip.passengers } >= minTrips }.toSet()

/*
 * Task #3. Find all the passengers, who were taken by a given driver more than once.
 */
fun TaxiPark.findFrequentPassengers2(driver: Driver): Set<Passenger> {
    val tripsMadeByTheDriver = trips.filter { trip -> trip.driver == driver }
    //println("trips made by the driver: $tripsMadeByTheDriver")
    val passengersTravelDriver = tripsMadeByTheDriver.map { it.passengers }
    //println("passengers that traveled with this driver: $passengersTravelDriver")
    val groupingByPassenger =
        trips.filter { trip -> trip.driver == driver }.flatMap { trip -> trip.passengers }.groupingBy { it }.eachCount()
    //println("grouping by: $groupingByPassenger")
    return trips.filter { trip -> trip.driver == driver }.flatMap { trip -> trip.passengers }
        .groupingBy { passenger -> passenger }.eachCount().filter { (_, count) -> count > 1 }
        .map { (passenger, _) -> passenger }.toSet()
}

//we could also use something like this
fun TaxiPark.findFrequentPassengers(driver: Driver): Set<Passenger> =
    allPassengers.filter { passenger -> trips.count { trip -> passenger in trip.passengers && trip.driver == driver } > 1 }
        .toSet()

/*
 * Task #4. Find the passengers who had a discount for majority of their trips.
 */
fun TaxiPark.findSmartPassengers0(): Set<Passenger> {
    val (tripsWithDiscount, tripsWithoutDiscount) = trips.partition { it.discount != null }
    return allPassengers.filter { passenger ->
        tripsWithDiscount.count { trip -> passenger in trip.passengers } > tripsWithoutDiscount.count { trip -> passenger in trip.passengers }
    }.toSet()
}

fun TaxiPark.findSmartPassengers(): Set<Passenger> {
    return allPassengers.filter { passenger ->
        val tripsWithDiscount = trips.count { trip -> trip.discount != null && passenger in trip.passengers }
        val tripsWithoutDiscount = trips.count { trip -> trip.discount == null && passenger in trip.passengers }
        tripsWithDiscount > tripsWithoutDiscount
    }.toSet()
}

fun TaxiPark.findSmartPassengers3(): Set<Passenger> {
    val countTripsPerPassenger =
        allPassengers.associateWith { passenger -> trips.count { trip -> trip.passengers.contains(passenger) } }
    val passengersHadDiscounts =
        trips.filter { trip -> (trip.discount ?: 0.0) > 0.0 }.flatMap { trip -> trip.passengers }.toSet()
    val tripsWithDiscounts = trips.filter { (it.discount ?: 0.0) > 0.0 }
    val countTripsWithDiscountPerPassenger = passengersHadDiscounts.associateWith { passenger ->
        tripsWithDiscounts.count { trip ->
            trip.passengers.contains(
                passenger
            )
        }
    }

    fun hasMoreTripsWithDiscount(
        passenger: Passenger
    ) = (countTripsWithDiscountPerPassenger[passenger] ?: 0).toDouble().div(countTripsPerPassenger[passenger]!!) > 0.5

    return allPassengers.filter { passenger ->
        hasMoreTripsWithDiscount(passenger)
    }.toSet()
}

fun TaxiPark.findSmartPassengers2(): Set<Passenger> {
    val tripsWithDiscounts = trips.filter { (it.discount ?: 0.0) > 0.0 }
    val countTripsWithDiscountPerPassenger = tripsWithDiscounts.flatMap { it.passengers }.groupingBy { it }.eachCount()

    return allPassengers.filter { passenger ->
        countTripsWithDiscountPerPassenger.getOrDefault(passenger, 0) * 2 > trips.count {
            it.passengers.contains(
                passenger
            )
        }
    }.toSet()
}

/*
 * Task #5. Find the most frequent trip duration among minute periods 0..9, 10..19, 20..29, and so on.
 * Return any period if many are the most frequent, return `null` if there're no trips.
 */
fun TaxiPark.findTheMostFrequentTripDurationPeriod(): IntRange? {
    if (trips.isEmpty()) {
        return null
    }
    val effectiveMaxTripDuration = trips.maxOf { it.duration }
    val maxTripRange = effectiveMaxTripDuration + effectiveMaxTripDuration % 10
    val timeKeysSet = buildTimeKeysSet(maxTripRange)
    val countTripsPerTimeKeys =
        timeKeysSet.associateWith { time -> trips.count { trip -> trip.duration in time.first..time.last } }
    return countTripsPerTimeKeys.maxBy { it.value }?.key
}

private fun buildTimeKeysSet(maxDuration: Int): Set<IntRange> {
    val timeKeys = mutableSetOf<IntRange>()
    var previousStep = 0
    (9..maxDuration step 10).forEach { currentStep ->
        timeKeys += IntRange(previousStep, currentStep)
        previousStep = currentStep + 1
    }
    return timeKeys.toSet()
}

/*
 * Task #6.
 * Check whether 20% of the drivers contribute 80% of the income.
 */
fun TaxiPark.checkParetoPrinciple(): Boolean {
    if (trips.isEmpty()) {
        return false
    }
    val income80 = trips.sumOf(Trip::cost) * 0.8
    val incomePerDriver = allDrivers.associateWith { driver ->
        trips.filter { trip -> trip.driver == driver }.sumOf(Trip::cost)
    }
    val listIncomes = incomePerDriver.map { it.value }
    val topNumber = (listIncomes.size * 0.2).toInt()
    val topIncomes = listIncomes.sortedDescending().take(topNumber).sum()
    return topIncomes >= income80
}

fun TaxiPark.checkParetoPrinciple2(): Boolean {
    if (trips.isEmpty()) return false

    val totalIncome = trips.sumOf(Trip::cost)
    val sortedDriversIncome: List<Double> = trips
        .groupBy(Trip::driver)
        .map { (_, tripsByDriver) -> tripsByDriver.sumOf(Trip::cost) }
        .sortedDescending()

    val numberOfTopDrivers = (0.2 * allDrivers.size).toInt()
    val incomeByTopDrivers = sortedDriversIncome
        .take(numberOfTopDrivers)
        .sum()

    return incomeByTopDrivers >= 0.8 * totalIncome
}

fun main() {
    //build 10 minute periods
    println(buildTimeKeysSet(100))
    println("${35 + 35 % 10}")
    val passengers = setOf(1, 2, 3)

    data class Trip(val name: String, val passengers: Set<Int>, val duration: Int = 0)

    val trips = listOf(Trip("t1", setOf(1, 2)), Trip("t2", setOf(1, 3)), Trip("t3", setOf(3)))
    val allTripsDoneByPassenger = passengers.groupBy({ passenger -> passenger },
        { passenger -> trips.filter { trip -> passenger in trip.passengers } })
    println(allTripsDoneByPassenger)
    val countTripsDoneByPassenger = passengers.groupBy({ passenger -> passenger },
        { passenger -> trips.count { trip -> passenger in trip.passengers } })
    println(countTripsDoneByPassenger)
    val countTripsDoneByPassenger2 =
        passengers.associateWith { passenger -> trips.count { trip -> passenger in trip.passengers } }
    println(countTripsDoneByPassenger2)
    val countTripsDoneByPassenger3 =
        passengers.associate { passenger -> passenger to trips.count { trip -> passenger in trip.passengers } }
    println(countTripsDoneByPassenger3)

    val trips2 = listOf(Trip("t1", setOf(1, 2), 3), Trip("t2", setOf(1, 3), 15), Trip("t3", setOf(3), 4))
    val trips2GroupByRange = trips2.groupBy {
        val start = it.duration / 10 * 10
        val end = start + 9
        start..end
    }
    println(trips2GroupByRange)
    val trips2Ranges = trips2.map {
        val start = it.duration / 10 * 10
        val end = start + 9
        "$start..$end"
    }
    println(trips2Ranges)
}