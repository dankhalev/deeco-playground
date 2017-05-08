@echo off

cd ..

if not exist "out" mkdir out

if exist "simulation-logs/predator.txt" (

	echo Compiling...

	dir /s /B *.java > sources2

	javac -encoding UTF-8 -source 8 -target 8 -Xlint:unchecked -d ./out/ -cp libs/*  @sources2

	del sources2

	java -cp "out;libs/*" com.khalev.efd.visualization.Main simulation-logs/predator.txt examples/configs/config.xml

) else (
	echo You need to simulate this scenario before you can visualize it

	pause
)