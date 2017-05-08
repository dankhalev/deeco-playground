#!/bin/sh

cd ..

if [ -e simulation-logs/predator.txt ]; then
	echo "Compiling..."

	javac -encoding UTF-8 -source 8 -target 8 -d out -cp "libs/*" $(find . -name "*.java")

	java -cp "out:libs/*" com.khalev.efd.visualization.Main simulation-logs/predator.txt examples/configs/config.xml
else
	echo "You need to simulate this scenario before you can visualize it"
fi

