<?php
print '<?xml version=3D"1.0" encoding=3D"utf-8"?>';
print '<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml10.dtd">';
?>
<html>
<head>
<title>GALLERY EDIT</title>
<style type="text/css">
@import 'css/index.css';
@import 'css/lightbox.css';
</style>
</head>
<body>
<?php
if ( file_exists('../config.php') )
{
   include('../config.php');
}

	$nimi = " ";
	$link = " ";
	$bgcolor = "#000000";
	$padding = 10;
	$frame = 10;
	$displaytime = 3;
	$button = "Create";
	$id = 0;

mysql_connect($host,$username,$password);
@mysql_select_db($database) or die('Unable to select database');

/*
three cases
* new gallery input = do nothing = no need to identify
* new gallery creation = insert = id on 0, testi on 0
* old gallery update input = get data = id submitted, no testi - ok
* old gallery data update = update = both testi and id not 0
*/



if(isset($_REQUEST['testi']) & isset($_REQUEST['id'])){
	//uusia arvoja vietäväksi kantaan!!!
	$id = (int)$_REQUEST['id'];
	if($id == 0){ //uuden lisäys		
		$query3 = "select max(jarjestys)+1 as top from galleriat";
		$query3 = "select max(jarjestys)+1 as top from galleriat";
		$result = mysql_query($query3);
		$top = (int)mysql_result($result,0,"top");	
		$query= "insert into galleriat (nimi, link, frameColor, imagePadding, frameWidth, displayTime, jarjestys) values ('" . $_REQUEST['nimi'] . "','" . $_REQUEST['link'] . "','" . $_REQUEST['bgcolor'] . "'," . $_REQUEST['padding'] . "," . $_REQUEST['frame'] .",".  $_REQUEST['displaytime']. "," . $top . ")";
		echo "A new gallery created. ";
		//echo "<a href='upload.php?gallery=" .  $top ."'>add images</a> or";
		echo "Go back to <a href=\"index.php\">admin page</a><br/>";
	}else{ // vanhan päivitys
		$query= "update galleriat set nimi = '" . $_REQUEST['nimi'] . "', displayTime = " . $_REQUEST['displaytime'] . ", link = '" . $_REQUEST['link'] . "', frameColor = '" . $_REQUEST['bgcolor'] . "', imagePadding = " . $_REQUEST['padding'] . ", frameWidth = " . $_REQUEST['frame'] . " where id=" . $id;	
		echo "Changes made, go back to <a href=\"index.php\">admin page</a><br/>";
	}
	$result= mysql_query($query);

}else if(!isset($_REQUEST['testi']) & isset($_REQUEST['id'])){
	
	$button = "Change";
	$id = (int)@$_REQUEST['id'];
	//hae tiedot jos jo kannassa, näytä kuva
	$query= "SELECT * FROM galleriat where id = " . $id;
	$result=mysql_query($query);
	$num=mysql_numrows($result);
	//mysql_close();
	$i=0;

	while ($i < $num) {
		$nimi = mysql_result($result,$i,"nimi");
		$link = mysql_result($result,$i,"link");
		$bgcolor = mysql_result($result,$i,"frameColor");
		$padding = mysql_result($result,$i,"imagePadding");
		$frame = mysql_result($result,$i,"frameWidth");
		$displaytime = mysql_result($result,$i,"displaytime");;
		$i++;
	}
	
}

mysql_close();
/*<img src="../images/<?php echo $file; ?>" width="100">*/
?>

<form>
	<input type="hidden" name="id" value="<?php echo $id?>">
	<input type="hidden" name="testi" value="<?php echo $id?>">
	gallery title: <input type="text" rows="1" name="nimi" value="<?php echo $nimi?>"><br/>
	gallery link: <input type="text" rows="1" name="link" value="<?php echo $link?>"><br/>
	background color: <input type="text" name="bgcolor" value="<?php echo $bgcolor?>"><br/>
	image padding: <input type="text" name="padding" value="<?php echo $padding?>"><br/>
	frame width: <input type="text" name="frame" value="<?php echo $frame?>"><br/>
	displayTime: <input type="text" name="displaytime" value="<?php echo $displaytime?>"><br/>
	<input type="submit" value="<?php echo $button?>">
</form> 
</body>
</html>