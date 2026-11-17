@echo off
rem Maven wrapper sederhana (tipe only-script) untuk Windows.
setlocal
set "BASE_DIR=%~dp0"
powershell -NoProfile -ExecutionPolicy Bypass -Command ^
  "$ErrorActionPreference='Stop';" ^
  "$base='%BASE_DIR%'.TrimEnd('\');" ^
  "$url=((Get-Content \"$base\.mvn\wrapper\maven-wrapper.properties\" | Where-Object { $_ -like 'distributionUrl=*' }) -replace 'distributionUrl=','').Trim();" ^
  "$name=[IO.Path]::GetFileName($url) -replace '-bin\.zip$','';" ^
  "$root=Join-Path $env:USERPROFILE \".m2\wrapper\dists\$name\";" ^
  "$mvn=Join-Path $root \"$name\bin\mvn.cmd\";" ^
  "if (-not (Test-Path $mvn)) { New-Item -ItemType Directory -Force $root | Out-Null; $zip=Join-Path $root 'maven.zip'; Write-Host \"Downloading $url\"; Invoke-WebRequest -Uri $url -OutFile $zip; Expand-Archive -Force $zip $root; Remove-Item $zip };" ^
  "Set-Content -Path (Join-Path $env:TEMP 'mvnw-path.txt') -Value $mvn"
set /p MVN=<"%TEMP%\mvnw-path.txt"
set "MAVEN_PROJECTBASEDIR=%BASE_DIR%"
call "%MVN%" %*
endlocal & exit /b %ERRORLEVEL%
