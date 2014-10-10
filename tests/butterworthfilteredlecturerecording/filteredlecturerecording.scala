import numbers.finite.Complex
import numbers.finite.PolarComplex
import numbers.finite.RectComplex
import sounder.FileIO.MonoWavReader
import sounder.FileIO.WavWriter.writeMonoSamples
import sounder.Sounder._
import sounder.Util._
import scala.math.sin
import scala.math.abs
import scala.math.sqrt
import scala.math.min
import scala.math.max
import scala.math.floor
import scala.math.round
import scala.math.ceil
import scala.math.pow
import scala.math.Pi

println("Reading file audio.wav")
val wavereader = new MonoWavReader("../fouriertransformlecturevideo/audio.wav") //object for reading wav files
val F = wavereader.sampleRate //sample rate
val P = 1.0/F //sample period
val c = wavereader.toArray //read samples to array c
val N = c.size
println("File contains " + N + " samples at rate " + F + "Hz")

println("Playing back audio and recording.  This will take 20 seconds.")
//uncomment to play back samples (takes one minute)
val (filtered, unfiltered) = playRecordSamples(c,F)

println("Writing recorded audio to filtered.wav and unfiltered.wav")
writeMonoSamples(filtered, "filtered.wav", F) //write filtered audio to file
writeMonoSamples(unfiltered, "unfiltered.wav", F) //write unfiltered audio to file
