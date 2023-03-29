<?php
$id = ($_POST["id"]);
$passwordhash = ($_POST["passwordhash"]);
$salt = ($_POST["salt"]);

try {
    require_once('/home/wesley/db/config.php');
    $con = mysqli_connect($host, $user, $password, $db_name);
} catch (Exception $e) {
    echo 'Error: ' . $e->getMessage();
    echo "An error occurred: " . mysqli_error($con);
}

if ((isset($_POST["id"])) && (isset($_POST["passwordhash"])) && (isset($_POST["salt"]))) {
    $sql = "SELECT id FROM users WHERE id = '$id'";
    $result = mysqli_query($con, $sql);
    if (mysqli_num_rows($result) > 0) {
        // The id already exists
        echo "Id already exists.";
    } else {
        // The id does not exist, so insert the new row
        $sql = "INSERT INTO users (userID, salt, hashedPassword) VALUES ('$id', '$salt', '$passwordhash')";
        if (mysqli_query($con, $sql)) {
            echo "true";
        } else {
            echo "An error occurred.";
        }
    }
} else {
    echo "No data input.";
}

mysqli_close($con);
?>