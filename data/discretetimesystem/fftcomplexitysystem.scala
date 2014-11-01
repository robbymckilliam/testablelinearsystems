import numbers.finite.Complex
import numbers.finite.PolarComplex
import numbers.finite.RectComplex
import scala.math.sin
import scala.math.min
import scala.math.max
import scala.math.floor
import scala.math.round
import scala.math.ceil
import scala.math.Pi

println("Generating data for discrete time system modeling the complexity of the Cooley Tukey FFT in Chapter 6")

val P = 0.5 //period of the discrete time system
def Lambda(alpha : Complex, f : Double) = {
  val e2pif = PolarComplex(1,2*Pi*P*f)
  e2pif / (e2pif  - alpha)
}
def mod2pi(x : Double) = x - round(x/2/Pi)*2*Pi

val fmin = -5.0
val fmax= 5.0

/// Format Doubles string to a reasonable number of decimal places
def fmt(x : Double) = "%f" format x

val ds = List(2,4,10) //reciprocal of alphas
for( d <- ds ){
  val alpha = RectComplex(1.0/d,0)
  val filetfunabs = new java.io.FileWriter("datafftcomplexityabs" + d + ".csv")
  val filetfunangle = new java.io.FileWriter("datafftcomplexityangle" + d + ".csv")
  (fmin to fmax by 0.01) foreach { f =>
    filetfunabs.write(fmt(f) + "\t" + fmt(Lambda(alpha,f).magnitude) + "\n")
    filetfunangle.write(fmt(f) + "\t" + fmt(mod2pi(Lambda(alpha,f).angle)) + "\n")
  }
  filetfunabs.close
  filetfunangle.close
}
//println("Scala finished")
