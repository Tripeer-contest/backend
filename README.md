[Place 관련 변경사항 정리]

1. DTO 압축

CityAndTownDTO
CityDTO
TownDTO

-> CityAndTownDTO 하나에 두개의 이너클래스로 압축

2. VO class 처리
Place 도메인의 경우 도시 조회가 주류이고 도시 생성 하나만 POST 되기 때문에 VO하나만 존재 valid 또한 한개만 적용
VO class로 분리되면서 검증 어노테이션이 달리지 않은 변수 몇개가 있는데 QA 후 삭제가능(사용하지 않는 변수)

3. 정적 팩토리 메소드

특별 기조 없음

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
