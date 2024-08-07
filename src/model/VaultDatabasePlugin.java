package src.model;

/* 
 * Vault Database Config 구성 시 필요한 DB 별 기본 세팅 값
 */
public enum VaultDatabasePlugin {
    MySQL(
            "mysql-database-plugin",
            "CREATE USER '{{name}}'@'%' IDENTIFIED BY '{{password}}';\n" + //
                    "GRANT SELECT ON *.* TO '{{name}}'@'%';"),
    MariaDB(
            "mysql-database-plugin",
            "CREATE USER '{{name}}'@'%' IDENTIFIED BY '{{password}}';\n" + //
                    "GRANT SELECT ON *.* TO '{{name}}'@'%';"),
    PostgreSQL(
            "postgresql-database-plugin",
            "CREATE ROLE \"{{name}}\" WITH LOGIN PASSWORD '{{password}}' VALID UNTIL '{{expiration}}';\n" + //
                    "GRANT SELECT ON ALL TABLES IN SCHEMA public TO \"{{name}}\";");

    private final String plugin;
    private final String statements;

    VaultDatabasePlugin(String plugin, String statements) {
        this.plugin = plugin;
        this.statements = statements;
    }

    public String getPlugin() {
        return plugin;
    }

    public String getStatements() {
        return statements;
    }

    /*
     * Plugin Name 목록 조회
     */
    public static String[] getPluginNames() {
        VaultDatabasePlugin[] plugins = VaultDatabasePlugin.values();
        String[] pluginNames = new String[plugins.length];

        for (int i = 0; i < plugins.length; i++) {
            pluginNames[i] = plugins[i].name();
        }

        return pluginNames;
    }

}
