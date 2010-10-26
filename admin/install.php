<?php 
if ( file_exists('../config.php') )
{
   include('../config.php');
}
echo 'Creating database: ' . $database . '....';
$conn = mysql_connect($host,$username,$password);
if (!$conn)
  {
  die('Could not connect: ' . mysql_error());
  }
$sql = "CREATE DATABASE " . $database . " IF NOT EXISTS";
mysql_query( $sql, $conn );
$conn = mysql_connect('localhost',$username,$password);
@mysql_select_db($database) or die('Unable to select database');
$sql = 'DROP TABLE IF EXISTS `kuvat`';
mysql_query( $sql, $conn );
 

$sql = 'CREATE TABLE  `kuvat` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(120) NOT NULL,
  `nimi` varchar(120) NOT NULL,
  `lapimitta` varchar(45) NOT NULL,
  `hinta` int(10) unsigned NOT NULL,
  `pysty` int(10) unsigned NOT NULL,
  `galleria` int(10) unsigned NOT NULL,
  `jarjestys` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`)
)';
   
echo 'Creating table: \'images\'....';
mysql_query( $sql, $conn );

$sql = 'DROP TABLE IF EXISTS `galleriat`';
mysql_query( $sql, $conn);

$sql = 'CREATE TABLE  `galleriat` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `link` varchar(120) NOT NULL,
  `frameColor` varchar(120) NOT NULL,
  `frameWidth` int(10) unsigned NOT NULL,
  `imagePadding` int(10) unsigned NOT NULL,
  `displayTime` int(10) unsigned NOT NULL,
  `nimi` varchar(120) NOT NULL,
  `jarjestys` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`)
)';  
   
echo 'Creating table: \'gallery\'....';
mysql_query( $sql, $conn );

?>
