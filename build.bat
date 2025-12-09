@echo off
echo ================================
echo    Building PinoyQuest...
echo ================================

REM Clean output directory
if exist out (
    rmdir /s /q out
)
mkdir out

echo Finding source files...

REM Generate list of source files using reliable Windows find
dir /S /B src\*.java > sources.txt

REM Check if no files were found
for %%A in (sources.txt) do (
    if %%~zA equ 0 (
        echo ERROR: No Java source files found in src/.
        pause
        exit /b
    )
)

echo Compiling Java files...
javac -d out @sources.txt

if %errorlevel% neq 0 (
    echo Compilation failed.
    pause
    exit /b
)

echo Creating JAR file...
jar cfe PinoyQuest.jar main.Main -C out .

echo ================================
echo      BUILD SUCCESSFUL!
echo ================================
pause
