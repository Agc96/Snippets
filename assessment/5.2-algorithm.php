<?php

$arrayA = array(
	// Associative array of custom 'AttributeName' key names
	'AttributeName1'=> array(
		'AttributeValueList' => array(
			array(
				'S' => 'Value1',
				'N' => 'string',
				'B' => 'string',
				'NULL' => true || false,
				'BOOL' => true || false,
			),
		),
		// ComparisonOperator is required
		'ComparisonOperator' => 'string',
	),
	// ... repeated
);

$stringB = 'WHERE AttributeName1 ComparisonOperator1 Value1 AND AttributeName2 ComparisonOperator1 Value2...';

?>
