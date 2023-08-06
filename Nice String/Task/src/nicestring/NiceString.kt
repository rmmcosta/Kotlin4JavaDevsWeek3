package nicestring

import java.util.*

fun String.isNice(): Boolean {
    val condition1 = if (notContainsBUBABE) 1 else 0
    val condition2 = if (containsAtLeastThreeVowels) 1 else 0
    val condition3 = if (containsDoubleLetter) 1 else 0
    val sum = condition1 + condition2 + condition3
    //println("$sum = $condition1 + $condition2 + $condition3")
    return sum > 1
}

val String.notContainsBUBABE: Boolean
    get() = setOf("ba","be","bu").none { this.contains(it) }

val String.containsAtLeastThreeVowels: Boolean
    get() = this.toCharArray().count { vowelsArray.contains(it) } >= 3

val String.containsDoubleLetter: Boolean
    get() = this.zipWithNext().any { it.first == it.second }

val vowelsArray get() = setOf('a', 'e', 'i', 'o', 'u')

fun main() {
    "aei".isNice() eq true
    "nn".isNice() eq true
    "bac".isNice() eq false
    "aza".isNice() eq false
    "baaa".containsAtLeastThreeVowels eq true
}

private infix fun Boolean.eq(b: Boolean) {
    if (this == b) {
        println("ok")
    } else {
        println("not ok")
    }
}
