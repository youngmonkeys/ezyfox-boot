CREATE TABLE IF NOT EXISTS `ezy_data_indices` (
    `id` bigint unsigned NOT NULL AUTO_INCREMENT,
    `data_type` varchar(120) COLLATE utf8mb4_unicode_520_ci NOT NULL,
    `data_id` bigint unsigned NOT NULL,
    `keyword` varchar(300) COLLATE utf8mb4_unicode_520_ci NOT NULL,
    `priority` int unsigned NOT NULL,
    `created_at` datetime NOT NULL,
    `updated_at` datetime NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `key_data_type_id_keyword` (`data_type`, `data_id`, `keyword`),
    INDEX `index_pagination_data_type_keyword` (`data_type`, `keyword`, `priority`, `id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci;
