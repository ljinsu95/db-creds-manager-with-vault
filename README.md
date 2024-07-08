# db-creds-manager-with-vault
Vault 기반 Database 계정 발급 프로그램

## 프로세스
### 관리자
1. 관리자 로그인
    - vault read sys/health
    - vault login
2. 사용자 생성
    - vault auth enable -path=db-userpass userpass
    - vault write auth/db-userpass/users/$USER_NAME password=$PASSWORD token_policies=db-creds
3. DB 등록
    - vault secrets enable -path=db-manager database
### 사용자
1. 사용자 로그인
    - vault read sys/health
    - vault login -method=db-userpass -username=$USER_NAME -password=$USER_PW
2. DB 계정 발급
    - vault write db-manager/creds/$USER_NAME

## 필요 권한
### 관리자

### 사용자
    

## 추후 작업 계획
1. 관리자 - USER 등록
2. USER 추가 시 config 별 Role 추가
3. config 추가 시 USER 별 Role 추가

### 사용자 권한
1. DB 계정 발급 (path : db-manager/creds/{{username}})
2. Userpass 패스워드 변경 (paht : auth/db-userpass/user/{{username}})
```shell
vault policy write -output-curl-string db-user -<<EOF
path "db-manager/creds/{{identity.entity.aliases.db-userpass.name}}" {
    capabilities = ["read"]
}
path "auth/db-userpass/users/{{identity.entity.aliases.db-userpass.name}}" {
    capabilities = ["create", "update"]
}
EOF
```

