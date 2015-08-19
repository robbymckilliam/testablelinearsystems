import sounder.Sounder._
import sounder.Util._
import scala.math.sin
import scala.math.sqrt
import scala.math.min
import scala.math.exp
import scala.math.max
import scala.math.floor
import scala.math.ceil
import scala.math.Pi
import scala.math.log

print("Plug in the active band-pass filter and press ENTER")
System.in.read

println("Playing for 2 seconds and recording input")

val f1 = 500
val f2 = 1333
val Fs = 44100
val xtrue : Double => Double = t => sin(2*Pi*f1*t)/3 + sin(2*Pi*f2*t)/3
val (right, left) = playRecord(xtrue, 0, 2.0, Fs)
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

//circuit parameters
val R1 = 3300
val C1 = 100e-9 
val R2 = 15000
val C2 = 10e-9
val a = -R2*C1
val b = R2*C2 + R1*C1
val c = C1*R1*C2*R2
val alpha = (-b+sqrt(b*b-4*c))/c/2
val beta = (-b-sqrt(b*b-4*c))/c/2
val A = a*alpha/(alpha-beta)/c  //equal to a\alpha/(alpha - beta)
val B = a*beta/(alpha-beta)/c  //equal to a\beta(alpha - beta)

println(alpha,beta,A,B)

//the function f from the tests.  Requires numerical integration
def f(t : Double) : Double = {
  val K = -log(1e-4)/min(alpha.abs,beta.abs) //log(1e-4) should get around 1e-4 error in the trapezoidal sum
  val N = ceil(K*10*Fs).toInt //number of intervals in the trapezoidal sum, 10 points per sample.
  val g : Double=>Double = tau => (A*exp(alpha*tau) - B*exp(beta*tau)) * sinc(t - Fs*tau) //g function from Test (Active RC again)
  return trapezoidal(g,0,K,N)
}

def Hx(t : Double) : Double = {
  val mini = max(0, floor(Fs*t - sinc_truncate).toInt)
  val maxi = min(L-1, ceil(Fs*t + 2*sinc_truncate).toInt)
  (mini to maxi).foldLeft(0.0){ (sum, i) => sum + left(i)*f(Fs*t - i) }
}

println("Writing data to file impulsedata.csv")
val tmin = 0.9999
val tmax = 1.0041
val filetfun = new java.io.FileWriter("impulsedata.csv")
(tmin to tmax by 0.00002) foreach { t => //writing time in milliseconds
   filetfun.write((1000*t).toString.replace('E', 'e') + "\t" + x(t).toString.replace('E', 'e')  + "\t" + y(t).toString.replace('E', 'e') + "\t" + Hx(t).toString.replace('E', 'e') +  "\n")
}
filetfun.close

println("Scala finished")
