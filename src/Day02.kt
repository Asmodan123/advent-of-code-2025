fun main() {
    fun part1(input: String) {
        var sum = 0L
        input.split(",").forEach { idRange ->
            idRange.split("-").let { (min, max) ->
                for (id in min.toLong()..max.toLong()) {
                    if (isInvalidProductId1(id.toString())) {
                        sum += id
                        println("$idRange => $id")
                    }
                }
            }
        }
        println("InvalidIdSum: $sum")
    }

    fun part2(input: String) {
        var sum = 0L
        input.split(",").forEach { idRange ->
            idRange.split("-").let { (min, max) ->
                for (id in min.toLong()..max.toLong()) {
                    if (isInvalidProductId2b(id.toString())) {
                        sum += id
                        println("$idRange => $id")
                    }
                }
            }
        }
        println("InvalidIdSum: $sum")
    }

//    part1("11-22,95-115,998-1012,1188511880-1188511890,222220-222224,1698522-1698528,446443-446449,38593856-38593862,565653-565659,824824821-824824827,
//    2121212118-2121212124")

//    part1("26803-38596,161-351,37-56,9945663-10044587,350019-413817,5252508299-5252534634,38145069-38162596,1747127-1881019,609816-640411,207466-230638,18904-25781,131637-190261,438347308-438525264,5124157617-5124298820,68670991-68710448,8282798062-8282867198,2942-5251,659813-676399,57-99,5857600742-5857691960,9898925025-9899040061,745821-835116,2056-2782,686588904-686792094,5487438-5622255,325224-347154,352-630,244657-315699,459409-500499,639-918,78943-106934,3260856-3442902,3-20,887467-1022885,975-1863,5897-13354,43667065-43786338")

    part2("11-22,95-115,998-1012,1188511880-1188511890,222220-222224,1698522-1698528,446443-446449,38593856-38593862,565653-565659,824824821-824824827," +
            "2121212118-2121212124")

//    part2("26803-38596,161-351,37-56,9945663-10044587,350019-413817,5252508299-5252534634,38145069-38162596,1747127-1881019,609816-640411,207466-230638," +
//            "18904-25781,131637-190261,438347308-438525264,5124157617-5124298820,68670991-68710448,8282798062-8282867198,2942-5251,659813-676399,57-99,5857600742-5857691960,9898925025-9899040061,745821-835116,2056-2782,686588904-686792094,5487438-5622255,325224-347154,352-630,244657-315699,459409-500499,639-918,78943-106934,3260856-3442902,3-20,887467-1022885,975-1863,5897-13354,43667065-43786338")

}

fun isInvalidProductId1(productId: String): Boolean {
    if (productId.length % 2 != 0) return false
    val mid = productId.length / 2
    return productId.take(mid) == productId.substring(mid)
}

fun isInvalidProductId2(s: String): Boolean {

    for (parts in 2..s.length) {
        if (s.length % parts != 0) continue   // Teilung muss passen
        val size = s.length / parts
        val first = s.take(size)

        // Prüfen, ob alle Segmente identisch sind
        val allEqual = (1 until parts).all { i ->
            s.substring(i * size, (i + 1) * size) == first
        }

        if (allEqual) {
            return true
        }
    }

    return false
}

fun isInvalidProductId2b(s: String) =
    (2 .. s.length).any() {
        if (s.length % it == 0) {
            val size = s.length / it
            val first = s.take(size)
            (1 until it).all { i ->
                s.substring(i * size, (i + 1) * size) == first
            }
        } else {
            false
        }
    }
