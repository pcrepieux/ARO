@echo off

REM Copyright 2012 AT&T
 
REM Licensed under the Apache License, Version 2.0 (the "License");
REM you may not use this file except in compliance with the License.
REM You may obtain a copy of the License at

REM http://www.apache.org/licenses/LICENSE-2.0

REM Unless required by applicable law or agreed to in writing, software
REM distributed under the License is distributed on an "AS IS" BASIS,
REM WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
REM See the License for the specific language governing permissions and
REM limitations under the License.

@setlocal

if "%JAVA_HOME%" == "" goto noJavaHome
if not exist "%JAVA_HOME%\bin\java.exe" goto noJavaHome
if "%_JAVA%" == "" set _JAVA=%JAVA_HOME%\bin\java.exe
goto run

:noJavaHome
if "%_JAVA%" == "" set _JAVA=java.exe

:run
"%_JAVA%" -classpath "%~dp0..\lib\ARO.jar;%~dp0..\lib\ddmlib.jar;%~dp0..\lib\jcommon-1.0.13.jar;%~dp0..\lib\jfreechart-1.0.13.jar;%~dp0..\lib\jmf.jar" -Xms100m -Xmx1024m com.att.aro.main.Launch %*

