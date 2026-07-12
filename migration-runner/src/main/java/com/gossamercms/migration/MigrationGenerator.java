package com.gossamercms.migration;


import com.gossamercms.mvc.models.ModelMeta;

import java.time.Instant;
import java.util.UUID;

public class MigrationGenerator {

    public static String generateCreateTableSql(ModelMeta meta) {
        System.out.println("generating sql for "+meta.table());
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS ").append(meta.table()).append(" (\n");

        boolean first = true;
        for (var entry : meta.columns().entrySet()) {
            if (!first) sb.append(",\n");
            first = false;

            String name = entry.getKey();
            Class<?> type = entry.getValue();
            Integer length = meta.length(name);

            sb.append("    \"").append(name).append("\" ")
                    .append(sqlType(type, length));

            if (name.equals("id")) {
                sb.append(" PRIMARY KEY");
            }
        }

        sb.append("\n);\n");
        return sb.toString();
    }

    private static String sqlType(Class<?> type, Integer length) {
        System.out.println("type: " + type.getName() + " length " + length);
        if (type == UUID.class) return "UUID";
        if (type == Instant.class) return "TIMESTAMP";
        if (type == Integer.class || type == int.class) return "INTEGER";
        if (type == Long.class || type == long.class) return "BIGINT";
        if (type == Boolean.class || type == boolean.class) return "BOOLEAN";

        if (type == String.class) {
            if (length != null) return "VARCHAR(" + length + ")";
            return "TEXT"; // fallback
        }

        return "TEXT";
    }
}

