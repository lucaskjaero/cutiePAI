url=www.onlinevideoconverter.com ; <-- place url here.
url1=www.videograbby.com
url2=keepvid.com
url3=www.clipconverter.cc
run % "chrome.exe" ( winExist("ahk_class Chrome_WidgetWin_1") ? " --new-window " : " " ) url
Sleep, 200
Run chrome.exe %url1%
Run chrome.exe %url2%
Run chrome.exe %url3%
ExitApp
Return