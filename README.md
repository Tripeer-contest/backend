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



`PR` 제목 및 `Commit` 메세지 예시

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
