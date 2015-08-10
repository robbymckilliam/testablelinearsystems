import sounder.Sounder._
import sounder.Util._
import scala.math.sin
import scala.math.min
import scala.math.exp
import scala.math.max
import scala.math.floor
import scala.math.ceil
import scala.math.Pi
import scala.math.log
import scala.math.abs

print("Plug in the active RC circuit and press ENTER")
System.in.read

println("Playing for 2 seconds and recording input")

val f1 = 500
val f2 = 1333
val Fs = 44100
val xtrue : Double => Double = t => sin(2*Pi*f1*t)/3 + sin(2*Pi*f2*t)/3
val (right,left) = playRecord(xtrue, 0, 2.0, Fs)
//playSamples(left)
//playSamples(right)

//total number of samples recorded
val L = min(left.length, right.length)

//value at which we truncate sinc function (makes things faster)
val sinc_truncate = 500

//reconstructed input signal x
def x(t : Double) : Double = {
  val mini = max(0, floor(Fs*t - sinc_truncate).toInt)
  val maxi = min(L-1, ceil(Fs*t + sinc_truncate).toInt)
  (mini to maxi).foldLeft(0.0){ (sum, i) => sum + left(i)*sinc(Fs*t - i) } 
}

//reconstructed output signal y
def y(t : Double) : Double = {
  val mini = max(0, floor(Fs*t - sinc_truncate).toInt)
  val maxi = min(L-1, ceil(Fs*t + sinc_truncate).toInt)
  (mini to maxi).foldLeft(0.0){ (sum, i) => sum + right(i)*sinc(Fs*t - i) } 
}

/** 
 * Approximates the integral of f from a to b using the trapezoidal 
 * rule with N intervals
 */
def trapezoidal(func : Double => Double, a : Double, b : Double, N : Int) : Double = {
  val del = (b - a)/N
  val inner = (1 to N-1).foldLeft(0.0)( (s,n) => s+2*func(a + n*del) )
  return del/2 * ( inner + func(a) + func(b) )
}

//hypothesised input signal
val R1 = 27e3
val R2 = 27e3
val C1 = 10e-9
val C2 = 0
val RC = 27e-5 //R*C 
val r = 1/RC

//The impulse response.
def h(t : Double) = if(t >= 0) -r*exp(-r*t) else 0.0

val K = -log(1e-4)*RC //log(1e-4) should get around 1e-4 error in the trapezoidal sum
val N = ceil(K*10*Fs).toInt //number of intervals in the trapezoidal sum, 10 points per sample.

//approximation of the function g from the tests.  Performs numerical integration
def g(t : Double) : Double = {
 def hsinc(tau : Double) = h(tau) * sinc(t - Fs*tau) //h multiplied by sinc function.
 return trapezoidal(hsinc,0,K,N)
}

def Hx(t : Double) : Double = {
  val mini = max(0, floor(Fs*t - sinc_truncate).toInt)
  val maxi = min(L-1, ceil(Fs*t + 2*sinc_truncate).toInt)
  (mini to maxi).foldLeft(0.0){ (sum, i) => sum + left(i)*g(Fs*t - i) }
}

val integralstarttime = (new java.util.Date).getTime
println("Writing data to file data.csv")
val tmin = 0.9999
val tmax = 1.0041
val filetfun = new java.io.FileWriter("data.csv")
(tmin to tmax by 0.00002) foreach { t => //writing time in milliseconds
   filetfun.write((1000*t).toString.replace('E', 'e') + "\t" + x(t).toString.replace('E', 'e')  + "\t" + y(t).toString.replace('E', 'e') + "\t" + Hx(t).toString.replace('E', 'e') +  "\n")
}
filetfun.close
val integralendtime = (new java.util.Date).getTime - integralstarttime
println("Required " + (integralendtime/1000.0) + " seconds.")

println("Scala finished")
