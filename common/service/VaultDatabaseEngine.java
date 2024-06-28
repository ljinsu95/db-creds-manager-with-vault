package common.service;

import common.Common;
import common.model.Vault;

public class VaultDatabaseEngine {
    private Vault vault;

    public VaultDatabaseEngine(Vault vault) {
        this.vault = vault;
    }

    public String EngineCheck() {
        System.out.println("Database Engine Check");
        System.out.println(vault.getVaultUrl());

        String token = vault.getVaultToken();
        String url = vault.getVaultUrl() + "/v1/sys/mounts";
        Common.request(url, token);
        return "";
    }
}
