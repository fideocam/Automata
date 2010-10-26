<?php
$id = (int)$_REQUEST['gallery'];
	/*
	require('db.php');
	$demo = new SortableExample();
	$list = $demo->getList($id);
	*/
	if ( file_exists('db.php') )
	{
   include('db.php');
	}
	$list = getList($id);
?>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<title>GALLERY ADMIN</title>
<link rel="stylesheet" type="text/css" href="style.css">
<script src="js/prototype.js"></script>
<script src="js/scriptaculous.js"></script>
<script>
Event.observe(window,'load',init,false);
function init() {
Sortable.create('listContainer',{tag:'div',onUpdate:updateList});
}
function updateList(container) {
var url = 'ajax.php';
var params = Sortable.serialize(container.id);
var ajax = new Ajax.Request(url,{
method: 'post',
parameters: params,
onLoading: function(){$('workingMsg').show()},
onLoaded: function(){$('workingMsg').hide()}
});
}
</script>
</head>
<body>

<h2>MANAGE</h2>
<div id="valikko">
<a href="upload.php?gallery=<?php echo $id;?>">Add image</a>
<a href="galleryedit.php?id=<?php echo $id;?>">Edit parameters</a>
<a href="index.php">Back to galleries</a>
</div>
<div id="listContainer">
	<?php
	foreach($list as $item) {
		?>
		<div id="item_<?php echo$item['id'];?>"><img src="../images/<?php echo $item['name'];?>" width="200"/>
		<?php echo $item['nimi'];
		echo '<br/><a href=edit.php?id='.$item['id'].'gallery='.$id.'><img src="icons/change.gif" border="0" alt="muuta"></a>';
		echo '<a href=delete.php?id='.$item['id'].'gallery='.$id.'><img src="icons/close.gif" border="0" alt="poista"></a>';
	?>
		</div>
		<?php
	}
	?>
</div>
<div id="workingMsg" style="display:none;">Updating database...</div>

</body>
</html>