<?php
$id = $_POST['id'];

try {
    require_once('/home/wesley/db/config.php');
    $con = mysqli_connect($host, $user, $password, $db_name);
} catch (Exception $e) {
    echo 'Error: ' . $e->getMessage();
    echo "An error occurred: " . mysqli_error($con);
}

$sql = "SELECT userID FROM users WHERE userID='$id'";
$result = mysqli_query($con, $sql);

if (mysqli_num_rows($result) == 1) {
    echo "true";
} else {
    echo "Id does not exist in the database.";
}

mysqli_close($con);
?>