#cd $pwd
#echo $PSScriptRoot
cd $PSScriptRoot
while($true){
$checker = Test-Path check.txt#checks to see if a file named check.txt exits
if($checker -eq $true){
Set-Content .\NirBrightness.txt "$null"#it will exit if so
break
}
$var = cat .\NirBrightness.txt

if($var.Length -lt 10){
continue
}
$bri=$var[$var.Length-2]
$bri
.\nircmd.exe setbrightness $bri #the command to set the brightness
}
exit
