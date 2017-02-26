cd $PSScriptRoot#it changes the directory to the location of the script
while($true){
$checker = Test-Path check.txt#check to see if a file named check.txt exists
if($checker -eq $true){
Set-Content .\NirVolume.txt "$null"#it will exit if so
break
}
$var = cat .\NirVolume.txt
if($var.Length -lt 10){
continue
}
$vol=$var[$var.Length-2]
$vol
.\nircmd.exe setvolume 0 $vol $vol #the command to set the volume
}
exit
