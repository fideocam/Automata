<?php
	if ( file_exists('gdb.php') )
	{
		include('gdb.php');	
	}
	$list = getList();
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
var url = 'gajax.php';
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
<div id="menu">
<b>ADMIN</b><br/>
<a href=galleryedit.php>Add new gallery</a>
</div>
<div id="workingMsg" style="display:none;">Updating database...</div>
<div id="listContainer">
	<?php
	foreach($list as $item) {
		?>
		<div id="item_<?php echo$item['id'];?>">
		<a href="gallery.php?gallery=<?php echo $item['id'];?>">
		<?php 
		//echo '<img src=/"../images/' . $item['image'] . '/" width="200"/></br>';
		echo $item['nimi'] . "</a>";
		echo '<br/><a href=galleryedit.php?id='.$item['id'].'&gallery='.$item['id'].'><img src="icons/change.gif" border="0" alt="edit"></a>';
		echo '<a href=deletegallery.php?id='.$item['id'].'&gallery='.$item['id'].'><img src="icons/close.gif" border="0" alt="delete"></a>';
	?>
		</div>
		<?php
	}
	?>
</div>
<div id="todo">
<b>Sprint list ie. bugs to fix:</b>
<ul>
<li>full list of galleries with first pictures (use join statement?)
</ul>
<b>Backlog eg. Nice to have additional features still missing:</b>
<ul>
<li>do not show empty galleries in the public list
<li>move image from gallery to another
<li>show image before detail input
<li>Batch image import
<li>enableRightClickOpen variable for gallery update and creation
<li>don't show gallery if there are no images associated with it
<li>highlight selected gallery
<li> gallery red color if there are no images (add images to publish the gallery)
<li>dynamic AJAX update interface with functions in containers, functions separated from frontend, showing selected gallery
</ul>
</div>
</body></html>