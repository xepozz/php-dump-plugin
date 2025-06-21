<?php

echo json_encode(
    array_map(
        function (PhpToken $token) {
            return [
                'line' => $token->line,
                'pos' => $token->pos,
                'name' => $token->getTokenName(),
                'value' => $token->text,
            ];
        },
        \PhpToken::tokenize(file_get_contents($argv[1])),
    )
);
