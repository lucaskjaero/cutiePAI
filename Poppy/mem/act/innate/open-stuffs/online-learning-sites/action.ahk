url=www.coursera.org ; <-- place url here.
url1=www.edx.org
url2=www.codecademy.com
url3=www.tutorialspoint.com
run % "chrome.exe" ( winExist("ahk_class Chrome_WidgetWin_1") ? " --new-window " : " " ) url
Sleep, 200
Run chrome.exe %url1%
Run chrome.exe %url2%
Run chrome.exe %url3%
ExitApp
Return