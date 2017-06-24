#NoEnv  ; Recommended for performance and compatibility with future AutoHotkey releases.
; #Warn  ; Enable warnings to assist with detecting common errors.
SendMode Input  ; Recommended for new scripts due to its superior speed and reliability.
SetWorkingDir %A_ScriptDir%  ; Ensures a consistent starting directory.

FileRead, urlVar, url.txt ; Read full link from url.txt and put into parameter urlVar

UrlDownloadToFile, %urlVar%, content.txt ; Download content from the link to content.txt

ExitApp