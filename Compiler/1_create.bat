@echo off
echo This script will create a .j file from a .c file.
echo.
echo Enter the name of the .c file (without extension):
set /p file=
echo.

cd bin
java C compile ..\%file%.c
move %file%.j ..\%file%.j
@pause