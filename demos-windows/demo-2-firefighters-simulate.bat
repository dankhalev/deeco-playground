@echo off

cd %~dp0/..

if not exist "out" mkdir out

echo Compiling...

dir /s /B *.java > sources3

javac -encoding UTF-8 -source 8 -target 8 -Xlint:unchecked -d ./out/ -cp libs/*  @sources3

del sources3

java -cp "out;libs/*" cz.cuni.mff.d3s.deeco.playground.simulation.Main examples\scenarios\firefighters.xml

pause