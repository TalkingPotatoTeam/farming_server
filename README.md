# 파밍 API 서버
- 아직 관리중 🏃

# Member

<table>
  <tr>
    <td align="center"><a href="https://github.com/ammerss"><img src="https://avatars.githubusercontent.com/u/29909348?v=4" width="75px;"/><br/><sub><b>윤연경</b></sub></a></td>
     <td align="center"><a href="https://github.com/Youhoseong"><img src="https://avatars.githubusercontent.com/u/33655186?v=4" width="75px;"/><br/><sub><b>유호성</b></sub></a></td>
         <td align="center"><a href="https://github.com/ilene97"><img src="https://avatars.githubusercontent.com/u/33650014?v=44" width="75px;"/><br/><sub><b>박진선</b></sub></a></td>
  </tr>
</table>

# 서비스 컨셉
- 식료품 중고 거래 플랫폼

# 특징
- Access Token과 Refresh Token을 활용한 보안성 극대화
- Layered Architecture를 통한 책임 분리
- 올바른 JPA 연관관계 설계와 쿼리 성능을 위한 튜닝 반영(N+1 Problem, LAZY Loading)
- 디자이너와 프론트엔드 개발자와의 협업
- 지속적인 Refactoring


# API 문서
https://documenter.getpostman.com/view/15247030/Tzm6nGJi

# 배포 자동화 전략 아키텍쳐
![배포자동화 아키텍쳐](https://user-images.githubusercontent.com/33655186/151618758-f448ad8f-4c0e-492a-92a5-c176f7b52b78.png)

# 실행 가이드

#### 도커 실행
```
./gradlew build jibDockerBuild //이미지 빌드
docker-compose up -d //도커컴포즈 컨테이너 만들기 및 시작 
docker-compose start // 서비스 시작
docker-compose stop //서비스 중지
docker-compose down //도커컴포즈 컨테이너 네트워크 볼륨 중지 및 제거
```
#### 도커 푸시
```
docker images
docker tag [이미지이름] [내이름]/[이미지이름]
docker push [내이름/이미지이름]
```
#### db 접속

``` 
docker ps -a  //실행중인 컨테이너
docker exec -i -t [컨테이너id또는이름] bash //도커접속
mysql -u [아이디] -p[비밀번호] //디비접속 띄어쓰기 주의
```
