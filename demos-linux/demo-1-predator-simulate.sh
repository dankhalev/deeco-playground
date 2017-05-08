#!/bin/sh

cd ..

echo "Compiling..."

javac -encoding UTF-8 -source 8 -target 8 -d out -cp "libs/*" $(find . -name "*.java")

java -cp "out:libs/*" com.khalev.efd.simulation.Main examples/scenarios/predator.xml

