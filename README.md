## 🗺️ Hoops
### : 나의 방문 일지들로 만드는 나만의 지도

## ⭐ 활용 목적 
* 대상 : 기록하고 싶은 장소들로 지역별 나만의 일지를 만들고 싶은 사람들
* 목적 : 내가 방문한 장소들을 사진과 함께 기록하여 지역별로 나만의 맵을 만들 수 있다.
* 효과 :
  * 사용자는 자신만의 지역별 맵을 구축하여 하나의 일지로 사용할 수 있다.
  * 사용자는 검색을 통해 다른 사용자가 작성한 장소들을 볼 수 있고 참고할 수 있다.
  * 새로운 장소 발굴 + 검색을 통해 알게 된 장소들을 통해 지역별로 나만의 장소 및 인사이트를 얻을 수 있다.
# 
## ⚙️ 활용 기술 스택
<img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"> <img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white"> <img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white"> <img src="https://img.shields.io/badge/springsecurity-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white"> <img src="https://img.shields.io/badge/redis-DC382D?style=for-the-badge&logo=redis&logoColor=white"> <img src="https://img.shields.io/badge/amazons3-569A31?style=for-the-badge&logo=amazons3&logoColor=white"> 
#
## 구성 ERD
![my-map-erd](https://github.com/seowonn/my-map/assets/144876148/70da2c49-c133-4d54-9b54-26d2ea01d0f5)



#
## 🙎‍♀ 회원
#### ▶️ 회원 가입
  - [x] 회원은 사용자 - USER, 관리자 - ADMIN 의 권한(role)을 갖는다.
  - [x] 회원 가입 시 아이디(이메일)는 이메일 인증을 통해 인증 절차를 거친다.
  - [x] 아이디는 유일하며, 비밀번호는 암호화된 비밀번호를 저장한다.
    
#### ▶️ 회원 정보 조회
  - [x] 로그인한 사용자의 정보를 조회할 수 있다. 

#### ▶️ 회원 정보 수정
  - [x] 아이디, 비밀번호 수정 시 이메일 인증을 거친다.
  
  #### ▶️ 인증 방식 
   - [x] 서버는 임의의 인증 코드를 전송해주고 동시에 redis에 저장한다.
   - [x] 사용자가 입력한 인증 코드와 redis에 저장된 인증 코드를 비교하여 인증을 진행한다.

#### ▶️ 회원 탈퇴
  - [x] 회원 탈퇴 시, 해당 회원 정보는 익명으로 전환되고 작성된 방문일지들은 삭제되지 않는다.
#
### 🔐 로그인
- [x] 아이디(이메일) & 비밀번호를 통해 로그인한다.
- [x] 성공적으로 로그인한 사용자는 JWT 토큰을 발급 받는다.
#
### 🔑 비밀번호 재발급
- [x] 이메일을 통하여 임의의 비밀번호를 발송해준다.
- [x] 임의의 비밀번호를 통해 로그인 후, 회원 정보 수정을 통해 비밀번호를 변경한다.
#
## 🗺️ 마이맵
#### ▶️  마이맵 등록
  - [x] 사용자는 만들 마이맵의 지역 (시) 를 선택한다. 
  - [x] 사용자는 마이맵 이름, 공개 여부를 작성하여 마이맵을 생성한다.
        
#### ▶️ 사용자의 마이맵 조회
  - [x] 작성자는 등록한 최신순으로 마이맵의 항목들을 조회할 수 있다.
  - [x] 마이맵을 검색한 사용자는 공개된 마이맵 목록을 조회할수 있다.
   - [x] 해당 마이맵 조회시, 공개된 방문 일지들을 조회할 수 있다.
   - [x] 방문일지들의 나열 순서는 작성자의 방문 순번이 존재할 경우, 해당 순서로, 아닐 경우 최신 등록순으로 나열한다.
         
#### ▶️ 마이맵 수정
  - [x] 마이맵의 작성자만 공개 여부, 마이맵 제목을 수정할 수 있다.
        
#### ▶️ 마이맵 삭제
  - [x] 작성자만 해당 마이맵을 삭제할 수 있고, 포함된 모든 방문일지들도 삭제 된다.  
#
### 🏡 지역 
- [x] 지역 정보는 공공 데이터 open api에서 스크래핑 해와서 DB( 광역시도 테이블과 시군구 테이블 )에 저장한다.
- [x] 광역시도는 마이맵과 매핑되고, 시군구는 방문일지와 매핑된다. 
### 📗 방문일지
#### ▶️ 방문일지 작성
  - [x] 사진(이미지 파일)을  최소 1개 ~ 10개 등록할 수 있다.
   - [x] 이미지 저장은 s3를 사용한다. 
  - [x] 방문일지는 마이맵의 하위 내용으로 지역의 더 구체적인 위치 구, 동을 작성하여 데이터를 추가하게 된다.
  - [x] 방문일지 작성 시 사용자는 카테고리를 지정한다.

#### ▶️ 방문일지 조회
  - [x] 방문일지의 공개 유무는 마이맵의 공개 유무를 우선적으로 따라간다.
   - [x] 마이맵 공개 설정 시, 방문일지는 공개로 작성된 것만 조회된다.
     
#### ▶️ 방문일지 수정
 - [x] 작성자는 방문일지의 모든 내용을 수정할 수 있다. (단 이미지는 삭제만 가능하다.)

#### ▶️ 방문일지 삭제
 - [x] 해당 방문일지를 작성한 사용자만 방문일지를 삭제할 수 있다.    
        
#### ▶️ 좋아요, 조회한 사람
  - [x] 좋아요는 로그인한 사용자 아이디 1명 당 1회 적립된다.
  - [x] 조회수는 로그인한 사용자 아이디 1명 당 1회 적립된다. 단 15분 당 재접속한 사용자는 집계하지 않는다.
   - [x] 방문일지 동시 접속에 따른 조회수 처리는 redis lock을 이용한다.  
#
### 🏷️ 즐겨찾기
  - [x] 사용자는 저장하고 싶은 방문일지를 추가할 수 있다.
  - [x] 즐겨찾기 목록 조회는 전체 보기, 카테고리별 보기로 구성된다.
  - [x] 즐겨찾기 조회 순서는 페이지 당 20개씩이며 즐겨찾기 등록순으로 나열된다.
#
## 🔎 검색
- [x] 검색 결과의 단위는 기본적으로 방문일지 단위로 나온다.
- [x] 사용자가 임의 작성한 검색어는 Elastic Search를 통해 방문일지들의 제목, 글 내용에서 찾는다.
#
### 🗃️ 카테고리
- [x] 관리자는 카테고리를 추가, 삭제할 수 있다. (ex. 카페, 공원, 음식점)
- [x] 검색 결과는 검색어의 prefix가 같은 방문일지들을 페이지 당 20개씩 나열해서 보여준다.
#
---
### 추가해 볼 기능
- [ ] map 단위 검색 시, 검색 결과는 마이맵 단위로 나오며, 공개처리된 마이맵들의 목록이 총 조회수 순으로 페이지당 20개씩 나열해서 나온다.
- [ ] 검색 기능 구체화 및 보완 - 필터링 항목 뿐만 아니라 다양한 검색어 및 검색 범위를 넓혀서 활용하기
- [ ] OAuth 2.0 로그인 기능 적용
- [ ] refreshToken 유효성 검사 
- [ ] 활동명 미 설정 시, 랜덤명으로 설정해주기
- [ ] 배포하고 운영해보기
