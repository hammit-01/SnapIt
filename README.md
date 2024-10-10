# SnapIt📸

## 목표와 기능
### 목표
- 여러 포토 부스에서 촬영한 사진을 모아 볼 수 있는 모바일 어플리케이션
- 포토 부스에서 촬영한 사진의 QR 코드를 통해 바로 어플리케이션 사진과 영상을 다운로드할 수 있다
### 기능
- 기본 카메라에서 QR 코드 촬영시 어플리케이션에 자동으로 이미지와 동영상을 등록한다
## 개발 환경
- Front : Kotlin
- Back: Firebase

## :star: 구현 완료 목록 :star:
- **Backend**
1. 사용자 로그인, 로그아웃, 회원가입
보안 강화를 위해 Firebase Authentication을 사용하여 이메일을 이용한 인증을 사용하여 사용자 로그인을 구현하였습니다.
회원 가입 시 입력되는 사용자의 정보를 Firestore를 사용해 저장하여 사용자의 개인정보 보안을 강화하였습니다.
사용자의 정보는 사용자 세션에 저장되어 관리됩니다.
2. 사용자 프로필 변경
사용자의 사진 어플리케이션을 통해 Firebase Storage에 저장할 수 있습니다.
3. 사용자의 이미지, 동영상 불러오기
사용자의 이미지와 동영상은 Firebase Storage에 저장되며 사용자 세션을 통해 정보를 주고 받습니다.

++
안드로이드 모바일 어플리케이션 백엔드를 구축하기 위해 Firebase를 조금 더 심도 있게 구현하려 노력했습니다. <br>
웹 페이지 백엔드를 구축할 때에는 MySQL과 PHP를 사용하였는데 Firebase의 데이터베이스 구조는 NoSQL이기에 새로운 도전이었습니다. <br>
Google에서 지원하는 Firebase의 보안 특성 상 편리한 부분이 많지만 세션 관리와 사용자 정보 보안 구축에 있어 이전 프로젝트인 Firstpage보다 더욱 더 나은 모습을 보여주려 노력하고 있습니다. <br>
이전 프로젝트인 Firstpage에서 API의 중요성을 깨닫게 되어 API 사용을 자유롭게 구사하는 모습을 보여주려 노력하고 있습니다. <br>

- **Frontend**
1. 메인 화면 구현
여러 이미지와 동영상을 한 눈에 보게 하기 위해 RecyclerView를 이용해 Grid 형식으로 구현했습니다.
2. 튜토리얼 화면 구현
사용자의 세션 로그인 상태에 따라 메인화면과 튜토리얼 화면을 구분하였습니다.
3. 로그인, 회원가입 화면 구현
사용자는 이메일 형식으로 회원가입을 할 수 있으며 Firebase Authentication을 이용해 보안을 강화하였습니다.
4. 사용자 프로필, 프로필 수정 화면 구현
수정 버튼을 통해 사용자의 프로필을 수정할 수 있도록 구현하였습니다.

## 🩵Todo🩵
|Check|Todo|Comment|
|--|--|--|
|[X]|QR 로직 구현||
|[X]|회원가입시 입력하지 않은 사항들 프로필에 빈칸으로 뜨지 않고 입력해주세요로 뜨게||
|[X]|회원가입 시 프로필 등록 구현|Todo 목록 참고|
|[X]|로그인 정보 잃어버렸을 때 아이디 찾기 비밀번호 찾기|
|[X]|로그인 시 이메일 인증이나 휴대폰 문자 인증 받도록|
|[X]|어플 로고 제작|
|[X]|이미지, 동영상 상세보기 및 삭제 기능|

## :smiling_imp: Error :smiling_imp:
이미지, 동영상 뷰어가 양이 많으면 높이가 한쪽으로 쏠리게 나오는 오류

## :exclamation: 추가 고려 사항 :exclamation:
- 보안 문제 <br>
firebase 사용, firestore/firestorage 보안 규칙 강화 <br>
추가 데이터 암호화: 세션 관리, 취약 토큰 관리(토큰 갱신), 2단계 인증 <br><br>
- 추가로 고안할 수 있는 아이템 <br>
사진 편집, 필터/프레임/템플릿, 쇼셜 미디어 공유 기능 <br>