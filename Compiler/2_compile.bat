@echo off
echo This script will create a .class file from a .j file.
echo.
echo Enter the name of the .j file (without extension):
set /p file=

java -jar jasmin.jar %file%.j
@pause