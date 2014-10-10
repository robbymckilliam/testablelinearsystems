import sounder.Sounder._
import sounder.Util._
import scala.math.sin
import scala.math.min
import scala.math.max
import scala.math.floor
import scala.math.ceil
import scala.math.Pi

print("Plug the voltage divider and press ENTER")
System.in.read

println("Playing the function sin(200 pi t) for 2 seconds and recording input")

val fc = 100
val Fs = 44100
val (left, right) = playRecord(t => sin(2*Pi*fc*t), 0, 2.0, Fs)
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

//hypothesised signal
val R1 = 100.0
val R2 = 470.0
//l R1 = 820.0
//l R2 = 6800.0
def Hx(t : Double) : Double = R2/(R1+R2)*x(t)

println("Writing data to file voltagedivider.csv")
val tmin = 0.998
val tmax = 1.022
val filetfun = new java.io.FileWriter("voltagedivider.csv")
(tmin to tmax by 0.00005) foreach { t =>
   filetfun.write((1000*t).toString.replace('E', 'e') + "\t" + x(t).toString.replace('E', 'e')  + "\t" + y(t).toString.replace('E', 'e') + "\t" + Hx(t).toString.replace('E', 'e') +  "\t" +"\n")
}
filetfun.close

println("Scala finished")
