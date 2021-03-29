# farming_server

#### 도커 실행
```
./gradlew build jibDockerBuild //이미지 빌드
docker-compose up -d //도커컴포즈 컨테이너 만들기 및 시작 
docker-compose start // 서비스 시작
docker-compose stop //서비스 중지
docker-compose down //도커컴포즈 컨테이너 네트워크 볼륨 중지 및 제거
```
#### db 접속

``` 
docker ps -a  //실행중인 컨테이너
docker exec -i -t [컨테이너id또는이름] bash //도커접속
mysql -u [아이디] -p[비밀번호] //디비접속 띄어쓰기 주의
```
