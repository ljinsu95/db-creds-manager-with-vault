# db-creds-manager-with-vault
Vault 기반 Database 계정 발급 프로그램

## 프로세스
### 관리자
1. 관리자 로그인
    - vault login
2. 사용자 생성
    - vault auth enable -path=db-userpass userpass
    - vault write auth/db-userpass/users/$USER_NAME password=$PASSWORD token_policies=db-creds
3. DB 등록
    - vault secrets enable -path=db-manager database
### 사용자
1. 사용자 로그인
    - vault login -method=db-userpass -username=$USER_NAME -password=$USER_PW
2. DB 계정 발급
    - vault write db-manager/creds/$USER_NAME
