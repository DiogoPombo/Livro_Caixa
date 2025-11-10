::------------------------------------------::
:: Author: Diogo Santos Pombo - \Õ/ - @2025 ::
::------------------------------------------::

::----------------------------------------------------------------------------------------------------------------------------------------------------::
:: Customized JLauncher embedded!                                                                                                                     ::
:: This is a custom JLauncher for the project amd it will only work with this specific project and with the parameters defined by the project author. ::
::----------------------------------------------------------------------------------------------------------------------------------------------------::


@echo off

setlocal enabledelayedexpansion

:: Verificar se o Java está instalado
java -version >nul 2>&1
if not %errorlevel%==0 (
	COLOR 0C
    echo ============================================================
    echo [ERRO] O Java nao foi encontrado neste computador.
    echo Por favor, instale o Java antes de executar o Livro Caixa!
    echo ============================================================
    timeout /t 5 /nobreak >nul
    goto end
)

set "BASESCRIPTFILE=%~dp0\JLS\"

if not exist "%BASESCRIPTFILE%" (
    COLOR 0C
    echo [ERROR] Folder JLS not found: %BASESCRIPTFILE%
    timeout /t 5 /nobreak >nul
    goto end
)

cd /d "%BASESCRIPTFILE%"


set "ORIGINAL_LAUNCHER=%BASESCRIPTFILE%LAUNCHER.cmd"
set "CORRECTED_LAUNCHER=%BASESCRIPTFILE%LAUNCHERW10.cmd"


if not exist "%ORIGINAL_LAUNCHER%" (
    COLOR 0C
    echo [ERROR] LAUNCHER.cmd not found at: %ORIGINAL_LAUNCHER%
    timeout /t 5 /nobreak >nul
    goto end
)
if not exist "%CORRECTED_LAUNCHER%" (
    COLOR 0C
    echo [ERROR] LAUNCHERW10.cmd not found at: %CORRECTED_LAUNCHER%
    timeout /t 5 /nobreak >nul
    goto end
)



for /f "tokens=2 delims==" %%i in ('wmic os get BuildNumber /value 2^>nul ^| findstr /b /c:"BuildNumber="') do set "BUILD=%%i"
set "BUILD=%BUILD: =%"
if "%BUILD%"=="" set "BUILD=0"
set "WINDOWS_VERSION=Win10"

if %BUILD% GEQ 22000 (
    set "WINDOWS_VERSION=Win11+"
    set "LAUNCHER_PATH=%ORIGINAL_LAUNCHER%"
) else (
    set "WINDOWS_VERSION=Win10-"
    set "LAUNCHER_PATH=%CORRECTED_LAUNCHER%"
)

:: Executar o launcher selecionado
COLOR 0A
echo [INFO] Iniciando Livro Caixa no %WINDOWS_VERSION%...
call "%LAUNCHER_PATH%"
if %errorlevel% neq 0 (
    COLOR 0C
    echo [ERRO] Falha ao iniciar o launcher. Verifique os arquivos e Java.
    timeout /t 5 /nobreak >nul
)

:end
endlocal

pause
exit