# Makefile
ImageReceiver: ImageReceiver.java
	javac -encoding UTF-8 -source 1.8 -target 1.8 ImageReceiver.java
	jar cvfm ImageReceiver.jar mani.mf *.class
clean:
	rm *.class
	rm *.jar