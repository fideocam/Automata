<?php
print '<?xml version=3D"1.0" encoding=3D"utf-8"?>';
print '<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml10.dtd">';
?>
<html>
<head>
<title>IMAGE EDIT</title>
<style type="text/css">
@import 'css/index.css';
@import 'css/lightbox.css';
</style>
<script type="text/javascript" src="js/prototype.js"></script>
<script type="text/javascript" src="js/scriptaculous.js?load=effects"></script>
<script type="text/javascript" src="js/lightbox.js"></script>
</head>
<body>
<?php


if ( file_exists('../config.php') )
{
   include('../config.php');
}

mysql_connect('localhost',$username,$password);
@mysql_select_db($database) or die('Unable to select database');


if(isset($_REQUEST['testi'])){
	$id = (int)$_REQUEST['testi'];

	$pystyarvo = $_REQUEST['pysty'];


	$query= "update kuvat set nimi = '" . $_REQUEST['nimi'] . "', pysty = " . $pystyarvo . ", lapimitta = " . $_REQUEST['radius'] . ", hinta = " . $_REQUEST['hinta'] . " where id=" . $id;
 
	$result=mysql_query($query);
	echo "Changes made, go back to <a href=\"index.php\">admin page</a><br/>";
}else{
	$id = (int)$_REQUEST['id'];
}

//hae tiedot jos jo kannassa, näytä kuva

$query= "SELECT * FROM kuvat where id = " . $id;
$result=mysql_query($query);
$num=mysql_numrows($result);
mysql_close();
$i=0;

while ($i < $num) {

	$file = mysql_result($result,$i,"name");
	$nimi = mysql_result($result,$i,"nimi");
	$radius = mysql_result($result,$i,"lapimitta");
	$hinta = mysql_result($result,$i,"hinta");
	$pysty = mysql_result($result,$i,"pysty");
	$i++;

}

?>
<img src="../images/<?php echo $file; ?>" width="100">
<form>
	name: <input type="text" rows="1" name="nimi" value="<?php echo $nimi; ?>"><br/>
	price: <input type="text" name="hinta" value="<?php echo $hinta; ?>"><br/>
	diameter: <input type="text" name="radius" value="<?php echo $radius; ?>"><br/>
	orientation<SELECT name="pysty" ID="pysty" VALUE="pysty"> 
		<OPTION value="1" <?php if($pysty == 1) echo "SELECTED"; ?>>horizontal</OPTION>
      		<OPTION value="0" <?php if($pysty == 0) echo "SELECTED"; ?>>vertical</OPTION>
   	</SELECT><br/>
	<input type="hidden" name="testi" value="<?php echo $id?>">
	<input type="hidden" name="id" value="<?php echo $id?>">
	<input type="submit" value="Change">
</form> 
</body>
</html>