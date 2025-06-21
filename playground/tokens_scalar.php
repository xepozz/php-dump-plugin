<?php

echo json_encode(
    array_map(
        function ($token) {
            return [
                'line' => $isArray = is_array($token) ? $token[2] : null,
                'name' => $isArray ? token_name($token[0]) : null,
                'value' => $isArray ? $token[1] : $token,
            ];
        },
        token_get_all(
            file_get_contents($argv[1])
        ),
    )
);
