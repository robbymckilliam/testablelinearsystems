#bash function to encode video
#usage:
#
# encode inputfilename outputfilename starttime duration
#
function encode {
avconv -i $1 -ss $3 -t $4 -vcodec libx264 -tune animation -qp 25 -acodec mp3 -ab 32k -ac 1 -ar 22050 $2
}

#extract unisa copyright
encode audio-vga1.m4v unisacopyright.mp4 00:00:00 00:00:08

#course information
encode audio-vga1.m4v courseinformation.mp4 00:01:48 00:18:00 
#avconv -i audio-vga1.m4v -r 10 -ss 00:01:48 -t 00:18:00 -vcodec libx264 -tune animation -qp 30 -acodec mp3 -ab 32k -ac 1 -ar 22050 courseinformation.mp4

#Signals and Systems
#avconv -i audio-vga1.m4v -r 10 -ss 00:18:15 -t 00:30:00 -vcodec libx264 -tune animation -qp 30 -acodec mp3 -ab 32k -ac 1 -ar 22050 ch1.mp4

#Section 1.1 Properties of Signals
#avconv -i audio-vga1.m4v -r 10 -ss 00:18:15 -t 00:3 -vcodec libx264 -tune animation -qp 30 -acodec mp3 -ab 32k -ac 1 -ar 22050 ch1sec1p1.mp4
