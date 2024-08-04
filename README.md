[user 관련 변경사항 정리]

1. DTO 압축

PlanDetailMain -> 여행 계획 관련 data 모음

TownDTO city -> 강원도 경상도 등등, town  도 안의 도시들

Plan, SPOT, TOWN 세가지로 분류

PublicRootDTO
RootOptimizeDTO는 최단거리 계산 관련 -> 수정중

PlanListResDTO는 History관련으로 History는 동천작성이 아니라서 일단 스킵


2. VO class 처리
VO 종류도 inner 클래스를 사용해야 하나 의문,
valid에서 변수 형식을 체크해주는건 방법은 있을만한데 왠지 안보임

3. 정적 팩토리 메소드

PlanDetailMainDTO.MyPlan에 보면 여러가지 정보를 파라미터로 해서 toDTO 하는데 리뷰 부탁합니다

4. Valid

user 와 비슷한 맥락

5. 기타

PlanServiceImpl 에서

5-1. getSpotSearch 메소드에서 jpa where 조건문을 구체적으로 쓰기위해 Specification을 사용하는데 이부분을 ServiceImpl 에서 길게 작성하지 않는 방법이 통 떠오르지 않아 현상유지.

5-2. (하단 3개 메소드) getShortTime, getOptimizingTime, MakeRootInfo는 최단거리 조정 관련이므로 tmap 도메인을 처리할때 같이 수정

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
