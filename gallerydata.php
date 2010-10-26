<?php 
/*
header("Content-type: text/xml");
header('Cache-Control: no-store, no-cache, must-revalidate'); 
header('Cache-Control: post-check=0, pre-check=0', FALSE); 
header('Pragma: no-cache'); 
*/
//ISO-8859-1
	
	if ( file_exists('config.php') )
{
   include('config.php');
}
	
echo '<?xml version="1.0" encoding="UTF-8"?>';
$id = (int)$_REQUEST['id'];
//get the gallery details
mysql_connect('localhost',$username,$password);
@mysql_select_db($database) or die('Unable to select database');
$query= "SELECT * FROM galleriat where id=" . $id;
$result=mysql_query($query);
mysql_close();
$i = 0;

$title = mysql_result($result,$i,"nimi");
$frameColor = mysql_result($result,$i,"frameColor");
$frameWidth = mysql_result($result,$i,"frameWidth");
$imagePadding = mysql_result($result,$i,"imagePadding");
$displayTime = mysql_result($result,$i,"displayTime");


print("<gallery title=\"" . $title . "\" frameColor=\"" . $frameColor . "\" frameWidth=\"" . $frameWidth . "\" imagePadding=\"" . $imagePadding ."\" displayTime=\"" . $displayTime . "\">");
if ( file_exists('config.php') )
{
   include('config.php');
}
mysql_connect('localhost',$username,$password);
@mysql_select_db($database) or die('Unable to select database');
$query= "SELECT * FROM kuvat where galleria = " .  $id. " ORDER by jarjestys";
$result=@mysql_query($query);
$num=@mysql_numrows($result);
mysql_close();
$i=0;
while ($i < $num) {
	$file = mysql_result($result,$i,"name");
	$nimi = mysql_result($result,$i,"nimi");
	$koko = mysql_result($result,$i,"lapimitta");
	$hinta = mysql_result($result,$i,"hinta");
	$pysty = mysql_result($result,$i,"pysty");

	echo '<image>';
   	echo '<filename>images/'.$file.'</filename>';
	echo '<caption>';
	if(strlen($nimi) >= 1) echo $nimi ."\n"; // \n
	if($koko >= 1) echo ' ø ' . $koko . "cm \n";
	if($hinta >= 1) echo '€'. $hinta . "\n";
	echo '</caption>';

//ø = &Oslash; ja € = &#8364;
//\n escape character

/* ei oo boolean*/
	if($pysty == 0){	
		echo '<width>533</width><height>800</height>';
	}else{
		echo '<width>800</width><height>533</height>';
	}
	echo '</image>';      
	$i++;
}
echo '</gallery>';
?>