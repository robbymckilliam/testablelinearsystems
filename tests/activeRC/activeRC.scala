import sounder.Sounder._
import sounder.Util._
import scala.math.sin
import scala.math.min
import scala.math.max
import scala.math.floor
import scala.math.ceil
import scala.math.Pi

print("Plug in the active RC circuit and press ENTER")
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
val sinc_truncate = 2000

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

//derivative of reconstructed input signal x
def Dy(t : Double) : Double = {
  val mini = max(0, floor(Fs*t - sinc_truncate).toInt)
  val maxi = min(L-1, ceil(Fs*t + sinc_truncate).toInt)
  Fs * (mini to maxi).foldLeft(0.0){ (sum, i) => sum + right(i)*dsinc(Fs*t - i) }
}

//hypothesised input signal
val R1 = 27e3
val R2 = 27e3
val C1 = 10e-9
val C2 = 0
def Hy(t : Double) : Double = -y(t) - 27e-5*Dy(t) // - R1/R2 y - R1*C*D(y)

println("Writing data to file data.csv")
val tmin = 0.9999
val tmax = 1.0041
val filetfun = new java.io.FileWriter("data.csv")
(tmin to tmax by 0.00001) foreach { t => //writing time in milliseconds
   filetfun.write((1000*t).toString.replace('E', 'e') + "\t" + x(t).toString.replace('E', 'e')  + "\t" + y(t).toString.replace('E', 'e') + "\t" + Hy(t).toString.replace('E', 'e') +  "\n")
}
filetfun.close

println("Scala finished")
