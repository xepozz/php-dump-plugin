<?php

function dump(array $config)
{
    $result = [];
    foreach ($config as $key => $value) {
        if (is_array($value)) {
            $result[] = [
                'name' => $key,
                'value' => '',
                'children' => dump($value),
            ];
        } else {
            $result[] = [
                'name' => $key,
                'value' => $value,
                'children' => [],
            ];
        }
    }
    return $result;
}

echo json_encode(dump([
    'configuration' => opcache_get_configuration(),
    'status' => opcache_get_status(true),
]));