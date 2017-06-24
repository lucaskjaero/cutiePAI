; auto insert .ahk when press Ctrl + Space

url=www.google.com ; <-- place.ahk{space} url here.

^Space::
	Send, !{Space} ; Emulate Alt + Space to open Wox
	Sleep, 100
	SendInput {Raw}.ahk ; Type the string ".ahk" into Wox input box
	Sleep, 100
	Send, {Space} ; Type a space after the string ".ahk"
Return