# SnapIt📸

- 여러 포토 부스에서 촬영한 사진을 모아 볼 수 있는 모바일 어플리케이션
- 포토 부스에서 촬영한 사진의 QR 코드를 통해 바로 어플리케이션 사진과 영상을 다운로드할 수 있음
- 기본 카메라에서 QR 코드 촬영시 어플리케이션에 자동 등록

## 🩵Todo🩵
|Check|Todo|--|
|--|--|
|[X]|QR 로직 구현||
|[X]|회원가입시 입력하지 않은 사항들 프로필에 빈칸으로 뜨지 않고 입력해주세요로 뜨게||
|[X]|회원가입 시 프로필 등록 구현|Todo 목록 보면 댐|
|[X]|로그인 정보 잃어버렸을 때 아이디 찾기 비밀번호 찾기|
|[X]|로그인 시 이메일 인증이나 휴대폰 문자 인증 받도록|
|[X]|어플 로고 제작|
|[X]|이미지, 동영상 상세보기 및 삭제 기능|

## Error
:wran: 이미지, 동영상 뷰어가 양이 많으면 높이가 한쪽으로 쏠리게 나옴

### 보안 문제
#### firebase 사용, firestore/firestorage 보안 규칙 강화
#### 추가 데이터 암호화: 세션 관리, 취약 토큰 관리(토큰 갱신), 2단계 인증 <br>

### 추가로 고안할 수 있는 아이템
#### 사진 편집, 필터/프레임/템플릿, 쇼셜 미디어 공유 기능