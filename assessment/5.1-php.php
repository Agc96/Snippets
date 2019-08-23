<?php

$stringA = "red, yellow, green, blue";

// Part B

$arrayB = explode(', ', $stringA);
var_dump($arrayB);

// Part C

$stringC = $arrayB[1];
$arrayB = array_splice($arrayB, 1, 1);

?>
