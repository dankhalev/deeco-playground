@echo off

cd ..

if not exist "out" mkdir out

if exist "simulation-logs/competition.txt" (

	echo Compiling...

	dir /s /B *.java > sources6

	javac -encoding UTF-8 -source 8 -target 8 -Xlint:unchecked -d ./out/ -cp libs/*  @sources6

	del sources6

	java -cp "out;libs/*" com.khalev.efd.visualization.Main simulation-logs/competition.txt examples/configs/competition-config.xml

) else (
	echo You need to simulate this scenario before you can visualize it

	pause
)