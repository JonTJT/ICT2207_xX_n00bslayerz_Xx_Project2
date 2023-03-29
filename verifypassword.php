<?php
$id = ($_POST["id"]);
$userpassword = ($_POST["password"]);

try {
    require_once('/home/wesley/db/config.php');
    $con = mysqli_connect($host, $user, $password, $db_name);
} catch (Exception $e) {
    echo 'Error: ' . $e->getMessage();
    echo "An error occurred: " . mysqli_error($con);
}

if ((isset($_POST["id"])) && (isset($_POST["password"]))) {
    $sql = "SELECT salt, hashedPassword FROM users WHERE userID='$id'";
    $result = mysqli_query($con, $sql);
    if (mysqli_num_rows($result) == 1) {
        $row = mysqli_fetch_assoc($result);
        $salt = $row['salt'];
        $hashed_password = $row['hashedPassword'];
        $hashed_input_password = hash('sha256', $salt . $userpassword);
        if ($hashed_input_password == $hashed_password) {
            echo "true";
        } else {
            echo "Password is incorrect.";
        }
    } 
} else {
    echo "No data input.";
    exit();
}

mysqli_close($con);
?> 