PACKAGE=LabB.zip

all: mainTask

run: mainTask
	java -Xms256m -Xmx512m -cp src MainTask

mainTask:
	javac -cp src src/MainTask.java

pack:
	zip $(PACKAGE) -r Makefile src