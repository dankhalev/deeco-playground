@echo off

cd ..

if not exist "out" mkdir out

if exist "simulation-logs/firefighters.txt" (

	echo Compiling...

	dir /s /B *.java > sources4

	javac -encoding UTF-8 -source 8 -target 8 -Xlint:unchecked -d ./out/ -cp libs/*  @sources4

	del sources4

	java -cp "out;libs/*" com.khalev.efd.visualization.Main simulation-logs/firefighters.txt examples/configs/fire-config.xml

) else (
	echo You need to simulate this scenario before you can visualize it

	pause
)