<?php
session_start();
include('db.php');
//$demo = new SortableExample();
//$demo->updateList2($_POST['listContainer'], $gallery);
//$demo->updateList($_POST['listContainer']);
updateList($_POST['listContainer']);
?>