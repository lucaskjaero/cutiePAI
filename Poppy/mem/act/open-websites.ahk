url=www.google.com

run % "chrome.exe" ( winExist("ahk_class Chrome_WidgetWin_1") ? " --new-window " : " " ) url
Sleep, 200
Loop, %0%  ; For each parameter:
{
    urlParam := %A_Index%  ; Fetch the contents of the variable whose name is contained in A_Index.
    Run chrome.exe %urlParam%
}
ExitApp
Return