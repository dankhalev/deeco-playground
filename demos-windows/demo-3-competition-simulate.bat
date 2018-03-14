@echo off

cd %~dp0/..

if not exist "out" mkdir out

echo Compiling...

dir /s /B *.java > sources5

javac -encoding UTF-8 -source 8 -target 8 -Xlint:unchecked -d ./out/ -cp libs/*  @sources5

del sources5

java -cp "out;libs/*" cz.cuni.mff.d3s.deeco.playground.simulation.Main examples\scenarios\competition.xml

pause