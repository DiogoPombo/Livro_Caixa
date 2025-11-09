::------------------------------------------::
:: Author: Diogo Santos Pombo - \Õ/ - @2025 ::
::------------------------------------------::

::----------------------------------------------------------------------------------------------------------------------------------------------------::
:: Customized JLauncher embedded!                                                                                                                     ::
:: This is a custom JLauncher for the project amd it will only work with this specific project and with the parameters defined by the project author. ::
::----------------------------------------------------------------------------------------------------------------------------------------------------::

@echo off

setlocal

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

cd %BASESCRIPTFILE%

call "%BASESCRIPTFILE%LAUNCHER.cmd"

:end
endlocal

exit