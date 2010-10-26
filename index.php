<html><head><title>Gallery</title>
<style type="text/css">
@import 'index.css';
@import 'layout1.css';
</style>
</head><body><div id="lista">
<?php
if ( file_exists('config.php') )
{
   include('config.php');
}
mysql_connect('localhost',$username,$password);
@mysql_select_db($database) or die('Unable to select database');
//TODO: galleries with images
$query= "SELECT * FROM galleriat order by jarjestys";
$result=mysql_query($query);
$num= @mysql_numrows($result);

if ($num <1){ 
		echo 'Log-in as admin to add the first images';
}else{
$i=0;
while ($i < $num) {
	$title = mysql_result($result,$i,"link");
	$id = mysql_result($result,$i,"id");
	//thumbnail - first image from each gallery?
	echo '<div><a href=gallery.php?id=';
	echo $id;
	echo '>';
	echo $title;
	echo '</a></div>';      
	$i++;
}
}
mysql_close();
echo '</div>';
?>

<?php
echo '</body></html>';
?>
	
