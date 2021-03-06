<?php
$uri_string = "/level1b(0)/level2a(0)";
$data_json = '{
    "la": "the",
    "lb": "one"
}';
$data_array = json_decode($data_json, true);
$ssd_filename_default = "origin.json";
$ssd_json_default = file_get_contents($ssd_filename_default);
$ssd_array = json_decode($ssd_json_default, true);

$value = &$ssd_array;
$uri_array = explode("/", $uri_string);
foreach ($uri_array as $item) {
    if (substr($item, -1) == ")") {
        $key = substr($item, 0, strpos($item, "("));
        $index = substr($item, strpos($item, "(") + 1, -1);
        $value = &$value[$key][$index];
    } else {
        $key = $item;
        $value = &$value[$key];
    }
}

echo PHP_EOL;
echo "----- read ------" . PHP_EOL;
print_r($value);
echo PHP_EOL;

echo PHP_EOL;
echo "----- write -----" . PHP_EOL;
$value = $data_array;
print_r($ssd_array);
echo PHP_EOL;