import numbers.finite.Complex
import numbers.finite.PolarComplex
import numbers.finite.RectComplex
import scala.math.sin
import scala.math.min
import scala.math.max
import scala.math.floor
import scala.math.ceil
import scala.math.Pi

println("Generating data for discrete time system modeling the complexity of the Cooley Tukey FFT in Chapter 6")

def lambda(f : Double) = {
  val e2pif = PolarComplex(1,2*Pi*f)
  e2pif / (e2pif  - 2)
}
def mod2pi(x : Double) = x - floor(x/2/Pi)*2*Pi

val fmin = 0.00001
val fmax=3.1

/// Format Doubles string to a reasonable number of decimal places
def fmt(x : Double) = "%f" format x

val filetfun = new java.io.FileWriter("datafftcomplexity.csv")
(fmin to fmax by 0.05) foreach { f =>
   filetfun.write(fmt(f) + "\t" + fmt(mod2pi(lambda(f).angle)) + "\n")
}
filetfun.close

println("Scala finished")
