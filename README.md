# db-creds-manager-with-vault
## 1. 개요
Vault 기반 Database 계정 발급 프로그램 (`현재 MySQL만 지원`)

### 특징
1. 관리자는 UI를 통해 Vault와 Database 연동

2. 관리자는 UI를 통해 Vault 사용자 계정 생성 및 삭제

3. 사용자는 UI를 통해 DataBase 계정 발급

## 요구 사항
### JAVA
### Vault 관리자 토큰
프로그램에서 관리자 로그인을 위한 최소 권한 이상을 가진 토큰
```shell
# Vault Policy 생성
vault policy write db-manager-admin -<<EOF
# 1. Auth 관련
## 1-1. Token 상태 조회
path "auth/token/lookup-self" {
    capabilities = ["read"]
}

## 1-2. Auth 목록 조회
path "sys/auth" {
    capabilities = ["read", "list"]
}

## 1-3. Auth(db-userpass) 활성화 및 사용자 생성, 조회
path "sys/auth/db-userpass/*" {
    capabilities = ["read", "list", "create", "update"]
}

## 1-4. 사용자 공통 권한 생성 및 조회
path "sys/policies/acl/db-user" {
    capabilities = ["read", "create", "update"]
}

## 1-5. Database 계정 발급 권한 생성 및 조회
path "sys/policies/acl/creds-*" {
    capabilities = ["read", "create", "update"]
}


# 2. DB Engine 관련
## 2-1. Engine 목록 조회
path "sys/mounts" {
  capabilities = ["read"]
}

## 2-2. Engine 생성
path "sys/mounts/db-manager" {
  capabilities = ["create", "update"]
}

## 2-3. Config(=Database-Connection) 생성 및 조회
path "db-manager/config/*" {
  capabilities = ["read", "list", "create", "update"]
}

## 2-4. 발급 역할 생성 및 조회
path "db-manager/roles/*" {
  capabilities = ["read", "create", "update"]
}
EOF
```

## 프로세스
### 관리자
1. 관리자 로그인
    - vault read sys/health

    - vault login

2. 사용자 생성
    - vault auth enable -path=db-userpass userpass
    
    - vault write auth/db-userpass/users/$USER_NAME password=$PASSWORD token_policies=db-creds

    1) DB Connection 리스트 조회

    2) DB Connection 별 사용자 전용 Role 생성
    
    3) DB Creds 접근 Policy 부여

3. DB 등록
    - vault secrets enable -path=db-manager database

    1) DB Connection(Vault DB config) 생성

    2) DB Connection을 통해 생성된 Role에 접근가능한 Policy 생성

    3) 사용자 별 DB Role 생성

    4) 사용자 별 DB Creds 접근 Policy 부여

### 사용자
1. 사용자 로그인
    - vault read sys/health

    - vault login -method=db-userpass -username=$USER_NAME -password=$USER_PW

2. DB 계정 발급
    - 기존 lease revoke
        - vault write sys/lease/....
    - DB 계정 발급
        - vault write db-manager/creds/$USER_NAME
    

### 사용자에게 부여되는 권한
1. DB 계정 발급 (path : db-manager/creds/{{db_config_name}}-{{username}})
2. Userpass 패스워드 변경 (paht : auth/db-userpass/user/{{username}})
```shell
# 본인의 패스워드 변경 권한 (추후 param - password만 허용하도록 변경)
vault policy write db-user -<<EOF
path "auth/db-userpass/users/{{identity.entity.aliases.db-userpass.name}}" {
    capabilities = ["create", "update"]
}
EOF

# ex) DB Connection Name : mysql-test
vault policy write creds-mysql-test -<<EOF
path "db-manager/creds/mysql-test-{{identity.entity.aliases.db-userpass.name}}" {
    capabilities = ["read"]
}
EOF
```

## 추후 작업 계획
1. 사용자가 DB 계정 발급 후 DB Connection 정보를 가져오려면, read db-connection/config 권한이 추가적으로 필요. 혹은 kv secret engine 사용 고려.

2. 사용자 DB 계정 발급 시 이전에 발급받은 lease 확인 후 삭제 플로우 추가 고려(한번 발급받은 후 재발급 시 동일 계정명으로 생성되어 에러 발생)

3. 사용자 패스워드 변경
