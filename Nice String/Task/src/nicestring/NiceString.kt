package nicestring

fun String.isNice(): Boolean {
    return listOf(notContainsBUBABE, containsAtLeastThreeVowels, containsDoubleLetter).count { it } >= 2
}

val String.notContainsBUBABE: Boolean
    get() = setOf("ba", "be", "bu").none { this.contains(it) }

val String.containsAtLeastThreeVowels: Boolean
    get() = this.count { it in vowelsSet } >= 3

val String.containsDoubleLetter: Boolean
    get() = this.zipWithNext().any { it.first == it.second } //could also use windowed(2) instead of zipWithNext

val vowelsSet get() = setOf('a', 'e', 'i', 'o', 'u')

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
