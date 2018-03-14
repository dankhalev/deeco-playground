#!/bin/sh

cd "${0%/*}"/..

if [ -e simulation-logs/competition.txt ]; then
	echo "Compiling..."

	mkdir -p out

	javac -encoding UTF-8 -source 8 -target 8 -d out -cp "libs/*" $(find . -name "*.java")

	java -cp "out:libs/*" cz.cuni.mff.d3s.deeco.playground.visualization.Main simulation-logs/competition.txt examples/configs/competition-config.xml
else
	echo "You need to simulate this scenario before you can visualize it"
fi
