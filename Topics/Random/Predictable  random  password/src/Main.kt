import kotlin.random.Random

fun generatePredictablePassword(seed: Int): String {
    var randomPassword = ""
    val random = Random(seed)
    repeat(10) {
        randomPassword += random.nextInt(33, 127).toChar()
    }
    // write your code here
	return randomPassword
}