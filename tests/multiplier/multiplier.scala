import sounder.Sounder._
import sounder.Util._
import scala.math.sin
import scala.math.min
import scala.math.max
import scala.math.floor
import scala.math.ceil
import scala.math.Pi

print("Plug in the multiplier circuit and press ENTER")
System.in.read

println("Playing signal for 2 seconds and recording input")

val f1 = 100
val f2 = 233
val Fs = 44100
val xtrue : Double => Double = t => sin(2*Pi*f1*t)/3 + sin(2*Pi*f2*t)/3
val (right,left) = playRecord(xtrue, 0, 2.0, Fs)
//playSamples(left)
//playSamples(right)

//total number of samples recorded
val L = min(left.length, right.length)

//value at which we truncate sinc function (makes things faster)
val sinc_truncate = 200

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
//H(x)
val R1 = 12e3
val R2 = 56e3
def Hx(t : Double) : Double = (1 + R2/R1)*x(t)

println("Writing data to file data.csv")
val tmin = 0.998
val tmax = 1.022
val filetfun = new java.io.FileWriter("data.csv")
(tmin to tmax by 0.00005) foreach { t =>
   filetfun.write(t.toString.replace('E', 'e') + "\t" + x(t).toString.replace('E', 'e')  + "\t" + y(t).toString.replace('E', 'e') + "\t" + Hx(t).toString.replace('E', 'e') +  "\n")
}
filetfun.close

println("Scala finished")
