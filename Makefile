run: compile
	java -cp bin Main

compile:
	mkdir -p bin
	javac -d bin $(shell find . -name "*.java")

clean:
	rm -rf bin
