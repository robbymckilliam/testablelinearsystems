import sounder.Sounder._
import scala.math.sin
import scala.math.min
import scala.math.max
import scala.math.floor
import scala.math.ceil
import scala.math.Pi

val fc = 100 //frequency of input signal
val xinput : Double => Double = t => sin(2*Pi*fc*t) //input signals
val Fs = 44100 //sample rate of sound card

// resistor values
val R1 = 820.0
val R2 = 6.8e3

/** Plays signals an obtains gain estimate Ahat given resistor R */
def ahat(R : Double) = {
print("Plug the " + R.toInt + "Ohm resistor in series and press ENTER")
System.in.read
val (x, y) = playRecord(xinput, 0, 2.0, Fs) //play signal
val L1 = min(x.length, y.length) //total number of samples recorded
val s = L1/4
val e = 3*L1/4
val xysum = (s to e).foldLeft(0.0) { (sum, i) => sum + x(i)*y(i) }
val xxsum = (s to e).foldLeft(0.0) { (sum, i) => sum + x(i)*x(i) }
val ahat = xysum/xxsum
println("Gain estimate = " + ahat)
ahat //return ahat 
}

// Get estimated gain values
val a1 = ahat(R1)
val a2 = ahat(R2)

// Compute estimated input and output resistances
val Ri = a1*a2*(R2-R1)/(a1-a2)
val Ro = Ri*(1-a1)/a1 - R1

println("Input and output resistor estimates:")
println("Ri = " + Ri)
println("Ro = " + Ro)

println("Scala finished")
