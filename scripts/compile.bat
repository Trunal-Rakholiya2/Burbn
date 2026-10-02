@echo off
echo Compiling Burbn - Social Media Feed Simulator...
if not exist bin mkdir bin

javac -encoding UTF-8 -d bin src/main/java/com/burbn/config/*.java src/main/java/com/burbn/util/*.java src/main/java/com/burbn/model/*.java src/main/java/com/burbn/service/*.java src/main/java/com/burbn/ui/*.java src/main/java/com/burbn/*.java

if %errorlevel% equ 0 (
    echo [SUCCESS] Compilation completed without errors. Binaries saved in bin/
) else (
    echo [ERROR] Compilation failed. Please check error log above.
)
pause
