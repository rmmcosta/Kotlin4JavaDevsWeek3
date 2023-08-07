package taxipark

/*
 * Task #1. Find all the drivers who performed no trips.
 */
fun TaxiPark.findFakeDrivers(): Set<Driver> =
    allDrivers.filter { driver: Driver -> trips.none { trip -> trip.driver == driver } }.toSet()

/*
 * Task #2. Find all the clients who completed at least the given number of trips.
 */
fun TaxiPark.findFaithfulPassengers(minTrips: Int): Set<Passenger> =
    allPassengers.filter { passenger -> trips.count { trip -> passenger in trip.passengers } >= minTrips }.toSet()

/*
 * Task #3. Find all the passengers, who were taken by a given driver more than once.
 */
fun TaxiPark.findFrequentPassengers(driver: Driver): Set<Passenger> {
    val tripsMadeByTheDriver = trips.filter { trip -> trip.driver == driver }
    println("trips made by the driver: $tripsMadeByTheDriver")
    val passengersTravelDriver = tripsMadeByTheDriver.map { it.passengers }
    println("passengers that traveled with this driver: $passengersTravelDriver")
    val groupingByPassenger =
        trips.filter { trip -> trip.driver == driver }.flatMap { trip -> trip.passengers }.groupingBy { it }.eachCount()
    println("grouping by: $groupingByPassenger")
    return trips.filter { trip -> trip.driver == driver }.flatMap { trip -> trip.passengers }
        .groupingBy { passenger -> passenger }.eachCount().filter { (_, count) -> count > 1 }
        .map { (passenger, _) -> passenger }.toSet()
}

/*
 * Task #4. Find the passengers who had a discount for majority of their trips.
 */
fun TaxiPark.findSmartPassengers(): Set<Passenger> {
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
    return countTripsPerTimeKeys.maxBy { it.value }.key
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
    val income80 = trips.sumOf { it.cost } * 0.8
    val incomePerDriver =
        allDrivers.associateWith { driver ->
            trips.filter { trip -> trip.driver == driver }.sumOf { trip -> trip.cost }
        }
    val listIncomes = incomePerDriver.map { it.value }
    val topNumber = (listIncomes.size * 0.2).toInt()
    val topIncomes = listIncomes.sortedDescending().take(topNumber).sum()
    return topIncomes >= income80
}

fun main() {
    //build 10 minute periods
    println(buildTimeKeysSet(100))
    println("${35 + 35 % 10}")
}