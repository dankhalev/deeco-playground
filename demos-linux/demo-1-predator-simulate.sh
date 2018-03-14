#!/bin/sh

cd "${0%/*}"/..

echo "Compiling..."

mkdir -p out

javac -encoding UTF-8 -source 8 -target 8 -d out -cp "libs/*" $(find . -name "*.java")

java -cp "out:libs/*" cz.cuni.mff.d3s.deeco.playground.simulation.Main examples/scenarios/predator.xml
