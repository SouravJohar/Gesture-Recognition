#cd $pwd
#echo $PSScriptRoot
cd $PSScriptRoot
while($true){
$checker = Test-Path check.txt
if($checker -eq $true){
Set-Content .\NirBrightness.txt "$null"
break
}
$var = cat .\NirBrightness.txt

if($var.Length -lt 10){
continue
}
$bri=$var[$var.Length-2]
$bri
.\nircmd.exe setbrightness $bri
}
exit