import numbers.finite.Complex
import numbers.finite.PolarComplex
import numbers.finite.RectComplex
import scala.math.sin
import scala.math.min
import scala.math.max
import scala.math.floor
import scala.math.ceil
import scala.math.Pi

println("Generating data for butterworth filters")

def beta(k : Int, m : Int) = new PolarComplex(1, Pi/2*(1+(2.0*k-1.0)/m))
def lambda(m: Int, f : Double) = Complex.one / (1 to m).foldLeft(Complex.one){ (prod, k) => prod*(new RectComplex(0,f) - beta(k,m)) }
def mod2pi(x : Double) = x - floor(x/2/Pi)*2*Pi

val fmin = 0.00001
val fmax=3.1

/// Format Doubles string to a reasonable number of decimal places
def fmt(x : Double) = "%f" format x

for(m <- 1 to 4) {
val filetfun = new java.io.FileWriter("data" + m + ".csv")
(fmin to fmax by 0.05) foreach { f =>
   filetfun.write(fmt(f) + "\t" + fmt(mod2pi(lambda(m,f).angle)) + "\n")
}
filetfun.close
}

println("Scala finished")
