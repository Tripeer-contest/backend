[user 관련 변경사항 정리]

1. UserDTO 압축

JoinDTO, SocialInfoDTO, UserInfoDTO, UserSearchDTO
-> dto.UserDTO (inner Info, Search, Social)로 압축, vo.JoinVO, vo.InfoVO 추가

2. VO class 처리
Join (회원가입), Info(회원정보 수정) 모두 Entity를 만드는데 사용하므로 바로 VOToEntity 되어 service 변환 유지

3. 정적 팩토리 메소드

EntityToDTO / Make 구현 전체검색 0802 검색시 변경점 확인 가능하도록 표기
주로 Service class 압축됨

4. Valid
JoinVO, InfoVO 선제 적용
Exception 발동시 처리 내용 Custom 화 하였음
exception.CustomExceptionHandler 안의 MethodArgumentNotValidException.class 제어부분 과
exception.ErrorResponseEntity 안의 CustomValid 메소드를 새로 만들어서 handling 하도록 구현

5. HttpServletRequest access token 관련

request를 받아오는 부분 모두 [ @AuthenticationPrincipal CustomOAuth2User user ] 로 변경 완료

Context가 set 되는 순간들을 구성하는데 고민이 많았습니다.
현재는 CustomOAuth2User 에 있는 userId 변수만 사용 ( EX. user.getUserId() )
현재는
a. 소셜 로그인을 통해 우리 사이트 회원 인증 시 CustomOAuth2UserService 의 return 값으로 Context가 set 됨
b. 소셜 로그인 했는데 우리 사이트 회원이 아니면 CustomOAuth2UserService에서 role만 비회원으로 설정되고 userId는 0인 상태로 SET됨

(★★★★★중요★★★★★)
c. 로컬 단일 백엔드 환경에서는 소셜 로그인을 할 수 없으므로 개발용 메소드로 토큰을 받아오고 jwtFilter에서 검증 할 때마다, set 되게 하였음
Authentication 구성을 위해서 Oath2Response 형식을 가진 클래스가 필요한데 소셜로그인이 안되는 환경이므로 TestResponse를 만들어서 일단 주입함.

c-1. 실 환경에서는 소셜로그인이 되어있기 때문에 문제 계속 set 안되어도 문제가 없지 않을까 ?
c-2. 최초 로그인 이후에는 자체 Response를 아예 구성하거나 현재 testResponse 같이 그냥 값을 비워주는게 좋지 않을까? -> 파라미터 자리에 null 썻더니 null은 안됨, 빈 클래스가 있긴해야함


# Tripeer-backend

## 컨벤션

### Git 관련 컨벤션

- `develop` 브랜치 기준으로 브랜치를 새롭게 생성 할 것

- `PR`이 `merge`된 브랜치는 반드시 삭제할 것

- `PR` 템플릿을 성실히 작성할 것

    - 기왕이면 이미지를 활용해도 좋음
    
    - [PR 이미지 올리는 방법](https://caileb.tistory.com/201)

브랜치 명명 예시

```
develop/{feature-name or domain-name}
```

`Commit` 메세지 예시

```
{TYPE}: 뭐시기 저시기~
```

`Commit` TYPE 종류
```
feature:    기능 개발

fix:        버그를 포함한 영향을 줄수 도 있는 수정 
            (현 기능에 허점 발견 및 문제점 발견, 패키지 및 의존성 수정)

docs:       md 파일과 같은 문서화 작업

refactor:   실제 로직에 영향을 주지않는 모든 수정을 포함 
            (주석, 변수명 변경, 성능개선, 가독성 개선, 폴더 명 및 구조 수정 등)
```

### 코드 컨벤션

[네이버 핵데이 코딩 컨벤션](https://naver.github.io/hackday-conventions-java/)

[적용법](https://bestinu.tistory.com/64)
