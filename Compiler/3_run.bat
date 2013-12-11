@echo off
echo This script will run a .class file with it's .c file.
echo.
echo Enter the name of the .class file (without extension):
set /p file=
echo.

java -classpath .;PascalRTL.jar %file% < %file%.c
@pause