cd $PSScriptRoot
while($true){
$checker = Test-Path check.txt
if($checker -eq $true){
Set-Content .\NirVolume.txt "$null"
break
}
$var = cat .\NirVolume.txt
if($var.Length -lt 10){
continue
}
$vol=$var[$var.Length-2]
$vol
.\nircmd.exe setvolume 0 $vol $vol
}
exit
