#NoEnv  ; Recommended for performance and compatibility with future AutoHotkey releases.
; #Warn  ; Enable warnings to assist with detecting common errors.
SendMode Input  ; Recommended for new scripts due to its superior speed and reliability.
SetWorkingDir %A_ScriptDir%  ; Ensures a consistent starting directory.

Esc::ExitApp ; Exit when press Esc

; Alt+g autocorrect selected text using Google
!g::
clipback := ClipboardAll
clipboard=
Send ^c
ClipWait, 0
UrlDownloadToFile % "https://www.google.com/search?q=" . clipboard, temp
FileRead, contents, temp
;msgbox, %contents%
FileDelete temp
if (RegExMatch(contents, "(Showing results for|Did you mean:)</span>.*?>(.*?)</a>", match)) {
   StringReplace, clipboard, match2, <b><i>,, All
   StringReplace, clipboard, clipboard, </i></b>,, All
   StringReplace, clipboard, clipboard, &#39;,', All
}
Send ^v
Sleep 500
clipboard := clipback
return