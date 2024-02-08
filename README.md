# PriceGuard
<img width="275" alt="priceguard_icon_web" src="https://github.com/boostcampwm2023/and09-PriceGuard/assets/37584805/b3fd42cf-f075-4cc7-a263-8195f28ab6ac">

## 🛡️ 프로젝트 소개

_원하는 상품의 가격을 추적하고 저렴할때 구입하세요!_

```
PriceGuard는 국내 상거래 사이트들의 상품 가격을 추적합니다.
사용자가 원하는 상품 링크를 등록하면 해당 상품들의 정보를 주기적으로 확인합니다.
사용자가 목표 가격을 설정해서 목표 가격 이하인 상품이 있으면 알림을 보냅니다.
또한 앱 내에서 원하는 상품의 가격 변화를 그래프로 표현합니다.
```

어플리케이션은 [링크](https://appdistribution.firebase.google.com/pub/i/b299ae01bd67c829)를 통해 받을 수 있습니다.

## 🥅 기술적 도전

우리 팀은 이 프로젝트에서 다음과 같은 기술적 도전 과제를 해결하려 합니다.

1. 그래프를 그릴 수 있는 기능을 개발하고 이를 보다 일반적인 상황에 쓸 수 있도록 라이브러리화한다.
2. 다양한 안드로이드 테마에 대응할 수 있는 안드로이드 앱을 개발한다.
3. 주기적으로 데이터를 수집하고, 다수의 사용자에게 대량의 데이터를 전달 및 처리하는 과정에 대한 효율적인 해결책을 찾는다.

이와 같은 기술적 도전을 해결하는 과정은 아래 링크에서 확인할 수 있습니다.

1. [그래프 라이브러리 구현](https://github.com/boostcampwm2023/and09-PriceGuard/wiki/%EA%B7%B8%EB%9E%98%ED%94%84-%EB%9D%BC%EC%9D%B4%EB%B8%8C%EB%9F%AC%EB%A6%AC-%EA%B0%9C%EB%B0%9C-%EA%B8%B0%EB%A1%9D)
2. [안드로이드 테마 적용](https://github.com/boostcampwm2023/and09-PriceGuard/wiki/%EC%95%88%EB%93%9C%EB%A1%9C%EC%9D%B4%EB%93%9C-%ED%85%8C%EB%A7%88-%EC%A0%81%EC%9A%A9)
3. [데이터 캐싱](https://github.com/boostcampwm2023/and09-PriceGuard/wiki/Cache-%EC%A0%81%EC%9A%A9%EA%B8%B0)
   
그래프 라이브러리 구현 및 배포 저장소는 여기서 확인해주세요.

<a href="https://central.sonatype.com/artifact/app.priceguard/materialchart/overview">
    <img src="https://img.shields.io/maven-central/v/app.priceguard/materialchart?color=0096FF" />
</a>

<a href="https://github.com/Taewan-P/material-android-chart/">
    <img src="https://img.shields.io/badge/materialchart-181717?logo=github&logoColor=FFFFFF" />
</a>


## ❓ 문제 해결

프로젝트 진행 과정에서 만난 이슈들과 해결 방법에 대해 기술합니다. 아래 링크에서 확인할 수 있습니다.

**DB 쿼리 개선**
> 가격 그래프 데이터는 DB에서 자주 조회되며, 1개월, 3개월 단위로 넓은 범위를 가진 데이터를 불러오기 때문에 양이 많습니다.
> 
> 현재 프로젝트 구조에서 데이터 조회 성능을 향상시키기 위해 어떤 노력을 했는지 기록했습니다.
> 
> [바로가기](https://velog.io/@kdogs/MongoDB%EC%97%90%EC%84%9C-%EC%BF%BC%EB%A6%AC-%EC%84%B1%EB%8A%A5-%EB%B6%84%EC%84%9D%ED%95%98%EA%B8%B0)

**메모리 누수**
> 안드로이드 앱 개발 과정에서 발견된 여러 메모리 누수 상황에 대해 소개합니다.
> 
> 어떻게 발견했는지, 왜 발생하는지, 어떻게 해결했는지 확인할 수 있습니다.
> 
> [바로가기](https://github.com/boostcampwm2023/and09-PriceGuard/wiki/TroubleShooting-%E2%80%90-Memory-Leak)

**RecyclerView 갱신 오류**
> 알림 토글의 값을 변경하고, 스크롤을 내렸다가 다시 돌아오면 해당 토글의 값이 변경 이전의 값으로 되돌아가는 현상이 발생했습니다.
> 
> 어떤 부분이 잘못되었고 어떻게 해결했는지를 다루는 문서입니다.
> 
> [바로가기](https://github.com/boostcampwm2023/and09-PriceGuard/wiki/Troubleshooting-%E2%80%90-RecyclerView%EC%9D%98-View%EA%B0%80-%EC%8A%A4%ED%81%AC%EB%A1%A4-%EB%B0%96%EC%9C%BC%EB%A1%9C-%EB%B2%97%EC%96%B4%EB%82%98%EB%A9%B4-%EA%B0%92%EC%9D%B4-%EB%B0%94%EB%80%8C%EB%8A%94-%ED%98%84%EC%83%81)

**JWT 인증 처리**
> 프로젝트를 진행하며 사용자 인증을 구현했던 과정에 대해 적어보고자 합니다.
>
> JWT 인증 처리를 하면서 했던 고민과 선택 이유를 기록했습니다.
> 
> [바로가기](https://velog.io/@mks1103/JWT%EB%A1%9C-%EC%9D%B8%EC%A6%9D-%EC%B2%98%EB%A6%AC%ED%95%98%EA%B8%B0)

**HTTPS**
> app 루트 도메인은 모든 연결에 HTTPS를 요구합니다.
> 
> Let's Encrypt를 활용해 어떻게 해당 프로젝트에 HTTPS를 적용했는지 기술하였습니다.
> 
> [바로가기](https://velog.io/@kdogs/HTTPS-%EC%A0%81%EC%9A%A9%ED%95%98%EA%B8%B0feat.-Lets-Encrypt)

**DeepLink 지원 및 DeepLink용 웹페이지 구현**
> 앱이 깔린 두 사람이 서로 상품 정보를 텍스트로 공유하려면 어떻게 해야될까요?
>
> Deeplink 와 Deeplink 이동용 웹페이지의 구현 과정을 소개합니다.
> 
> [바로가기](https://github.com/boostcampwm2023/and09-PriceGuard/wiki/Deeplink-%EC%A7%80%EC%9B%90-%EB%B0%8F-Deeplink-%EC%9D%B4%EB%8F%99%EC%9A%A9-%EC%9B%B9%ED%8E%98%EC%9D%B4%EC%A7%80-%EA%B5%AC%EC%B6%95%ED%95%98%EA%B8%B0)

**Navigation 백스택 오류**
> Jetpack Navigate에서 발생했던 에러에 대해 소개합니다.
> 
> 그리고 해당 에러의 원인과 해결 방법도 소개합니다.
> 
> [바로가기](https://github.com/boostcampwm2023/and09-PriceGuard/wiki/TroubleShooting-%E2%80%90-Navigation-%EB%B0%B1%EC%8A%A4%ED%83%9D-%EC%98%A4%EB%A5%98)

**테마 변경**
> 테마 변경 다이얼로그를 만들 때 참고했던 애플리케이션은 Now in Android입니다.
> 
> 해당 기능과 똑같은 인터렉션을 구현하려 했으나 잘 되지 않았습니다. 그 이유에 대해 기술합니다.
> 
> [테마 변경](https://github.com/boostcampwm2023/and09-PriceGuard/wiki/테마-변경-(compose-vs-xml))

**NestJS에서 Redis 사용하기**
>캐싱을 위해 Redis를 NestJS에 적용하며 겪은 이슈들에 대해 적어보고자 합니다.
>
>Redis를 적용하며 모듈을 2번이나 변경하고, 코드를 수정했던 과정에 대해 소개합니다.
>
>[바로가기](https://github.com/boostcampwm2023/and09-PriceGuard/wiki/NestJS%EC%97%90%EC%84%9C-Redis-%EC%82%AC%EC%9A%A9%ED%95%98%EA%B8%B0)
   
## 📚 기술 스택

<table>
    <thead>
        <tr>
            <th>분류</th>
            <th>기술 스택</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td>
                  <p>안드로이드</p>
            </td>
            <td>
                  <img src="https://img.shields.io/badge/Android-32DE84?logo=android&logoColor=FFFFFF">
                  <img src="https://img.shields.io/badge/Kotlin-7F52FF?logo=kotlin&logoColor=FFFFFF">
                  <img src="https://img.shields.io/badge/Jetpack%20Navigation-F6F6F7?logo=data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCA0MjIuMDY4OTEgNDIxLjMyMiI+PHBhdGggZD0iTTYyLjc3NjkyLDE4Mi4zNDJ2MTlhMTksMTksMCwwLDEsMTMuNDQsMzIuNDNjLTIuNDIsMi40My0xMi44NiwxMC40Mi0yNiwxNy45LTE3LjU5LDEwLTI3LjI5LDEyLTMxLjIsMTIuMzguNC00LjM0LDIuNy0xMy4zLDEwLjQ4LTI3LjY5LDguMS0xNSwxNy4zNC0yNywxOS44My0yOS40N2ExOC44OSwxOC44OSwwLDAsMSwxMy40NC01LjU1di0xOWgwYTM3Ljg2MDE0LDM3Ljg2MDE0LDAsMCwwLTI2Ljg2LDExLjFjLTkuODIsOS44My00OS4xNiw2Ny4yLTMxLjMzLDg1LDMuMjMsMy4yMiw3Ljc0LDQuNTcsMTMsNC41NywyNCwwLDY0LTI3Ljg1LDcyLTM1LjlhMzgsMzgsMCwwLDAtMjYuODYtNjQuODFabTE2NS4xMywxNDkuNGEzNy44NDk5LDM3Ljg0OTksMCwwLDAtMjYuODUtMTEuMTF2MTlhMTksMTksMCwwLDEsMTMuNDQsMzIuNDNjLTIuNDMsMi40My0xMi44NywxMC40Mi0yNiwxNy45LTE3LjU5LDEwLTI3LjI5LDEyLTMxLjIsMTIuMzguNC00LjM0LDIuNy0xMy4zLDEwLjQ4LTI3LjY5LDguMS0xNSwxNy4zNC0yNywxOS44NC0yOS40N2ExOC44NSwxOC44NSwwLDAsMSwxMy40My01LjU1di0xOWgwYTM3Ljg1LDM3Ljg1LDAsMCwwLTI2Ljg1LDExLjExYy05LjgyLDkuODItNDkuMTYsNjcuMTktMzEuMzMsODUsMy4yMywzLjIyLDcuNzQsNC41OCwxMyw0LjU4LDI0LDAsNjQtMjcuODYsNzItMzUuOTFhMzgsMzgsMCwwLDAsLjA0LTUzLjY3WiIgc3R5bGU9ImZpbGw6IzA5MzA0MiIvPjxwYXRoIGQ9Ik0zNTEuMjk2OTMsMTM0LjU2MmE4LjE1LDguMTUsMCwxLDEsMTEuNTMsMCw4LjE1LDguMTUsMCwwLDEtMTEuNTMsMG0tNjMuNzUtNjMuNzRhOC4xNiw4LjE2LDAsMSwxLDExLjU0LDAsOC4xNiw4LjE2LDAsMCwxLTExLjU0LDBsMCwwbS0yNCwyMjYuMjcsMTA1LjM2LTEwNS40M2E5Ni4yMiw5Ni4yMiwwLDAsMCwxOS4xNi04OS43N2wzMS40OS04LjQzYTMuMzkxMTcsMy4zOTExNywwLDAsMC0xLjc2LTYuNTVsLTMxLjg5LDguNTRhMTAyLjQ3LDEwMi40NywwLDAsMC01OS4yNC01OS4zbDguNTQtMzEuODhhMy4zOTExNiwzLjM5MTE2LDAsMSwwLTYuNTUtMS43NmwtOC40NCwzMS40OWE5Ni4yNCw5Ni4yNCwwLDAsMC04OS43OCwxOS4xNWwtMTA1LjQyLDEwNS40NVoiIHN0eWxlPSJmaWxsOiMzZGRjODQiLz48cGF0aCBkPSJNMTA2LjE3NjkyLDEzOS43NzJsNTguMzktNTguNCwyMS4xMS0yMS4xYTM3LjI4LDM3LjI4LDAsMCwxLDUyLjcyLDUyLjcxbC03OS40OSw3OS41aDBabTE3NS4xNCwxNzUuMTJoMGw3OS41LTc5LjQ5YTM3LjI4LDM3LjI4LDAsMCwwLTUyLjcyLTUyLjcybC0yMS4xLDIxLjExLTU4LjQsNTguMzZaIiBzdHlsZT0iZmlsbDojMzdiZjZlIi8+PHBhdGggZD0iTTIzMC42NzY5LDEwNS4yNjJhMzcuMjgsMzcuMjgsMCwwLDAtNTIuNzItNTIuNzJsLTIxLjExLDIxLjExLTYwLjcyLDYwLjcxLTExLDExYTYuNyw2LjcsMCwwLDAsMCw5LjQ1bDQzLjI1LDQzLjM0YTYuNyw2LjcsMCwwLDAsOS40NSwwbDExLTExaDBabTEzOC4yMywxMzguMjNhMzcuMjgsMzcuMjgsMCwwLDAtNTIuNzItNTIuNzJsLTIxLjExLDIxLjExLTYwLjcyLDYwLjcxLTExLDExYTYuNyw2LjcsMCwwLDAsMCw5LjQ1bDQzLjI3LDQzLjI3YTYuNzIsNi43MiwwLDAsMCw5LjQ1LDBsMTEtMTFoMFoiIHN0eWxlPSJmaWxsOiM0Mjg1ZjQiLz48L3N2Zz4=">
                  <img src="https://img.shields.io/badge/DataStore-F6F6F7?logo=data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCA0MjIuMDY4OTEgNDIxLjMyMiI+PHBhdGggZD0iTTYyLjc3NjkyLDE4Mi4zNDJ2MTlhMTksMTksMCwwLDEsMTMuNDQsMzIuNDNjLTIuNDIsMi40My0xMi44NiwxMC40Mi0yNiwxNy45LTE3LjU5LDEwLTI3LjI5LDEyLTMxLjIsMTIuMzguNC00LjM0LDIuNy0xMy4zLDEwLjQ4LTI3LjY5LDguMS0xNSwxNy4zNC0yNywxOS44My0yOS40N2ExOC44OSwxOC44OSwwLDAsMSwxMy40NC01LjU1di0xOWgwYTM3Ljg2MDE0LDM3Ljg2MDE0LDAsMCwwLTI2Ljg2LDExLjFjLTkuODIsOS44My00OS4xNiw2Ny4yLTMxLjMzLDg1LDMuMjMsMy4yMiw3Ljc0LDQuNTcsMTMsNC41NywyNCwwLDY0LTI3Ljg1LDcyLTM1LjlhMzgsMzgsMCwwLDAtMjYuODYtNjQuODFabTE2NS4xMywxNDkuNGEzNy44NDk5LDM3Ljg0OTksMCwwLDAtMjYuODUtMTEuMTF2MTlhMTksMTksMCwwLDEsMTMuNDQsMzIuNDNjLTIuNDMsMi40My0xMi44NywxMC40Mi0yNiwxNy45LTE3LjU5LDEwLTI3LjI5LDEyLTMxLjIsMTIuMzguNC00LjM0LDIuNy0xMy4zLDEwLjQ4LTI3LjY5LDguMS0xNSwxNy4zNC0yNywxOS44NC0yOS40N2ExOC44NSwxOC44NSwwLDAsMSwxMy40My01LjU1di0xOWgwYTM3Ljg1LDM3Ljg1LDAsMCwwLTI2Ljg1LDExLjExYy05LjgyLDkuODItNDkuMTYsNjcuMTktMzEuMzMsODUsMy4yMywzLjIyLDcuNzQsNC41OCwxMyw0LjU4LDI0LDAsNjQtMjcuODYsNzItMzUuOTFhMzgsMzgsMCwwLDAsLjA0LTUzLjY3WiIgc3R5bGU9ImZpbGw6IzA5MzA0MiIvPjxwYXRoIGQ9Ik0zNTEuMjk2OTMsMTM0LjU2MmE4LjE1LDguMTUsMCwxLDEsMTEuNTMsMCw4LjE1LDguMTUsMCwwLDEtMTEuNTMsMG0tNjMuNzUtNjMuNzRhOC4xNiw4LjE2LDAsMSwxLDExLjU0LDAsOC4xNiw4LjE2LDAsMCwxLTExLjU0LDBsMCwwbS0yNCwyMjYuMjcsMTA1LjM2LTEwNS40M2E5Ni4yMiw5Ni4yMiwwLDAsMCwxOS4xNi04OS43N2wzMS40OS04LjQzYTMuMzkxMTcsMy4zOTExNywwLDAsMC0xLjc2LTYuNTVsLTMxLjg5LDguNTRhMTAyLjQ3LDEwMi40NywwLDAsMC01OS4yNC01OS4zbDguNTQtMzEuODhhMy4zOTExNiwzLjM5MTE2LDAsMSwwLTYuNTUtMS43NmwtOC40NCwzMS40OWE5Ni4yNCw5Ni4yNCwwLDAsMC04OS43OCwxOS4xNWwtMTA1LjQyLDEwNS40NVoiIHN0eWxlPSJmaWxsOiMzZGRjODQiLz48cGF0aCBkPSJNMTA2LjE3NjkyLDEzOS43NzJsNTguMzktNTguNCwyMS4xMS0yMS4xYTM3LjI4LDM3LjI4LDAsMCwxLDUyLjcyLDUyLjcxbC03OS40OSw3OS41aDBabTE3NS4xNCwxNzUuMTJoMGw3OS41LTc5LjQ5YTM3LjI4LDM3LjI4LDAsMCwwLTUyLjcyLTUyLjcybC0yMS4xLDIxLjExLTU4LjQsNTguMzZaIiBzdHlsZT0iZmlsbDojMzdiZjZlIi8+PHBhdGggZD0iTTIzMC42NzY5LDEwNS4yNjJhMzcuMjgsMzcuMjgsMCwwLDAtNTIuNzItNTIuNzJsLTIxLjExLDIxLjExLTYwLjcyLDYwLjcxLTExLDExYTYuNyw2LjcsMCwwLDAsMCw5LjQ1bDQzLjI1LDQzLjM0YTYuNyw2LjcsMCwwLDAsOS40NSwwbDExLTExaDBabTEzOC4yMywxMzguMjNhMzcuMjgsMzcuMjgsMCwwLDAtNTIuNzItNTIuNzJsLTIxLjExLDIxLjExLTYwLjcyLDYwLjcxLTExLDExYTYuNyw2LjcsMCwwLDAsMCw5LjQ1bDQzLjI3LDQzLjI3YTYuNzIsNi43MiwwLDAsMCw5LjQ1LDBsMTEtMTFoMFoiIHN0eWxlPSJmaWxsOiM0Mjg1ZjQiLz48L3N2Zz4=">
                  <img src="https://img.shields.io/badge/Hilt-F6F6F7?logo=data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCA0MjIuMDY4OTEgNDIxLjMyMiI+PHBhdGggZD0iTTYyLjc3NjkyLDE4Mi4zNDJ2MTlhMTksMTksMCwwLDEsMTMuNDQsMzIuNDNjLTIuNDIsMi40My0xMi44NiwxMC40Mi0yNiwxNy45LTE3LjU5LDEwLTI3LjI5LDEyLTMxLjIsMTIuMzguNC00LjM0LDIuNy0xMy4zLDEwLjQ4LTI3LjY5LDguMS0xNSwxNy4zNC0yNywxOS44My0yOS40N2ExOC44OSwxOC44OSwwLDAsMSwxMy40NC01LjU1di0xOWgwYTM3Ljg2MDE0LDM3Ljg2MDE0LDAsMCwwLTI2Ljg2LDExLjFjLTkuODIsOS44My00OS4xNiw2Ny4yLTMxLjMzLDg1LDMuMjMsMy4yMiw3Ljc0LDQuNTcsMTMsNC41NywyNCwwLDY0LTI3Ljg1LDcyLTM1LjlhMzgsMzgsMCwwLDAtMjYuODYtNjQuODFabTE2NS4xMywxNDkuNGEzNy44NDk5LDM3Ljg0OTksMCwwLDAtMjYuODUtMTEuMTF2MTlhMTksMTksMCwwLDEsMTMuNDQsMzIuNDNjLTIuNDMsMi40My0xMi44NywxMC40Mi0yNiwxNy45LTE3LjU5LDEwLTI3LjI5LDEyLTMxLjIsMTIuMzguNC00LjM0LDIuNy0xMy4zLDEwLjQ4LTI3LjY5LDguMS0xNSwxNy4zNC0yNywxOS44NC0yOS40N2ExOC44NSwxOC44NSwwLDAsMSwxMy40My01LjU1di0xOWgwYTM3Ljg1LDM3Ljg1LDAsMCwwLTI2Ljg1LDExLjExYy05LjgyLDkuODItNDkuMTYsNjcuMTktMzEuMzMsODUsMy4yMywzLjIyLDcuNzQsNC41OCwxMyw0LjU4LDI0LDAsNjQtMjcuODYsNzItMzUuOTFhMzgsMzgsMCwwLDAsLjA0LTUzLjY3WiIgc3R5bGU9ImZpbGw6IzA5MzA0MiIvPjxwYXRoIGQ9Ik0zNTEuMjk2OTMsMTM0LjU2MmE4LjE1LDguMTUsMCwxLDEsMTEuNTMsMCw4LjE1LDguMTUsMCwwLDEtMTEuNTMsMG0tNjMuNzUtNjMuNzRhOC4xNiw4LjE2LDAsMSwxLDExLjU0LDAsOC4xNiw4LjE2LDAsMCwxLTExLjU0LDBsMCwwbS0yNCwyMjYuMjcsMTA1LjM2LTEwNS40M2E5Ni4yMiw5Ni4yMiwwLDAsMCwxOS4xNi04OS43N2wzMS40OS04LjQzYTMuMzkxMTcsMy4zOTExNywwLDAsMC0xLjc2LTYuNTVsLTMxLjg5LDguNTRhMTAyLjQ3LDEwMi40NywwLDAsMC01OS4yNC01OS4zbDguNTQtMzEuODhhMy4zOTExNiwzLjM5MTE2LDAsMSwwLTYuNTUtMS43NmwtOC40NCwzMS40OWE5Ni4yNCw5Ni4yNCwwLDAsMC04OS43OCwxOS4xNWwtMTA1LjQyLDEwNS40NVoiIHN0eWxlPSJmaWxsOiMzZGRjODQiLz48cGF0aCBkPSJNMTA2LjE3NjkyLDEzOS43NzJsNTguMzktNTguNCwyMS4xMS0yMS4xYTM3LjI4LDM3LjI4LDAsMCwxLDUyLjcyLDUyLjcxbC03OS40OSw3OS41aDBabTE3NS4xNCwxNzUuMTJoMGw3OS41LTc5LjQ5YTM3LjI4LDM3LjI4LDAsMCwwLTUyLjcyLTUyLjcybC0yMS4xLDIxLjExLTU4LjQsNTguMzZaIiBzdHlsZT0iZmlsbDojMzdiZjZlIi8+PHBhdGggZD0iTTIzMC42NzY5LDEwNS4yNjJhMzcuMjgsMzcuMjgsMCwwLDAtNTIuNzItNTIuNzJsLTIxLjExLDIxLjExLTYwLjcyLDYwLjcxLTExLDExYTYuNyw2LjcsMCwwLDAsMCw5LjQ1bDQzLjI1LDQzLjM0YTYuNyw2LjcsMCwwLDAsOS40NSwwbDExLTExaDBabTEzOC4yMywxMzguMjNhMzcuMjgsMzcuMjgsMCwwLDAtNTIuNzItNTIuNzJsLTIxLjExLDIxLjExLTYwLjcyLDYwLjcxLTExLDExYTYuNyw2LjcsMCwwLDAsMCw5LjQ1bDQzLjI3LDQzLjI3YTYuNzIsNi43MiwwLDAsMCw5LjQ1LDBsMTEtMTFoMFoiIHN0eWxlPSJmaWxsOiM0Mjg1ZjQiLz48L3N2Zz4=">
                  <img src="https://img.shields.io/badge/Firebase%20Cloud%20Messaging-FFA000?logo=firebase&logoColor=FFFFFF">
                  <img src="https://img.shields.io/badge/Material%20Design-757575?logo=materialdesign&logoColor=FFFFFF">
                  <img src="https://img.shields.io/badge/Retrofit-3E4348?logo=square&logoColor=FFFFFF">
                  <img src="https://img.shields.io/badge/Coroutines-7F52FF?logo=kotlin&logoColor=FFFFFF">
            </td>
        </tr>
        <tr>
            <td>
                <p>백엔드</p>
            </td>
            <td>
                  <img src="https://img.shields.io/badge/Nest.js-E0234E?logo=NestJS&logoColor=white"/>
                  <img src="https://img.shields.io/badge/TypeScript-3178C6?logo=typescript&logoColor=FFFFFF">
                  <img src="https://img.shields.io/badge/Redis-DC382D?logo=redis&logoColor=FFFFFF">
                  <img src="https://img.shields.io/badge/MySQL-4479A1?logo=mysql&logoColor=FFFFFF">
                  <img src="https://img.shields.io/badge/MongoDB-47A248?logo=mongodb&logoColor=FFFFFF">
                  <img src="https://img.shields.io/badge/Firebase%20Cloud%20Messaging-FFA000?logo=firebase&logoColor=FFFFFF">
                  <img src="https://img.shields.io/badge/Swagger-85EA2D?logo=swagger&logoColor=000000">
            </td>
        </tr>
        <tr>
            <td>
                <p>배포</p>
            </td>
            <td>
                <img src="https://img.shields.io/badge/Firebase%20App%20Distribution-FFA000?logo=firebase&logoColor=FFFFFF">
                <img src="https://img.shields.io/badge/Sonatype-1B1C30?logo=sonatype&logoColor=FFFFFF">
                <img src="https://img.shields.io/badge/Nginx-014532?logo=Nginx&logoColor=009639&">
                <img src="https://img.shields.io/badge/Naver Cloud Platform-03C75A?logo=naver&logoColor=ffffff">
                <img src="https://img.shields.io/badge/Docker-2496ED?logo=Docker&logoColor=white">
                <img src="https://img.shields.io/badge/GitHub%20Actions-2088FF?logo=githubactions&logoColor=white">
            </td>
        </tr>
        <tr>
            <td>
                <p>패키지 매니저</p>
            </td>
            <td>
                <img src="https://img.shields.io/badge/Gradle-02303A?logo=gradle&logoColor=FFFFFF">
                <img src="https://img.shields.io/badge/npm-CB3837?logo=npm&logoColor=FFFFFF">
            </td>
        </tr>
        <tr>
            <td>
                <p>협업</p>
            </td>
            <td>
                <img src="https://img.shields.io/badge/Figma-F24E1E?logo=Figma&logoColor=ffffff">
                <img src="https://img.shields.io/badge/Slack-4A154B?logo=Slack&logoColor=ffffff">
            </td>
        </tr>
    </tbody>
</table>

## 🏛️ 시스템 아키텍처

![system_architecture](https://github.com/boostcampwm2023/and09-PriceGuard/assets/39708676/21b9516b-3c01-49bf-8805-824b92c94500)


## 👨‍👨‍👦 팀원 소개

<table>
  <tr>
    <td align="center"><a href="https://github.com/muungi">J070 손문기</a></td>
    <td align="center"><a href="https://github.com/sickbirdd">J157 최병익</a></td>
    <td align="center"><a href="https://github.com/EunhoKang">K001 강은호</a></td>
    <td align="center"><a href="https://github.com/ootr47">K012 박승준</a></td>
    <td align="center"><a href="https://github.com/Taewan-P">K017 박태완</a></td>
  </tr>
  <tr>
    <td align="center"><a href="https://github.com/muungi"><img src="https://github.com/muungi.png" width="100px;" alt=""/></a></td>
    <td align="center"><a href="https://github.com/sickbirdd"><img src="https://github.com/sickbirdd.png" width="100px;" alt=""/></a></td>
    <td align="center"><a href="https://github.com/EunhoKang"><img src="https://github.com/EunhoKang.png" width="100px;" alt=""/></a></td>
    <td align="center"><a href="https://github.com/ootr47"><img src="https://github.com/ootr47.png" width="100px;" alt=""/></a></td>
    <td align="center"><a href="https://github.com/Taewan-P"><img src="https://github.com/Taewan-P.png" width="100px;" alt=""/></a></td>
  </tr>
  <tr>
    <td align="center">Back-End</td>
    <td align="center">Back-End</td>
    <td align="center">Android</td>
    <td align="center">Android</td>
    <td align="center">Android</td>
  </tr>
</table>
<br/>

## 📺︎ 작동 화면
| 로그인/회원가입 | 상품 추천/상품 상세 | 상품 추가 / 마이페이지 | 알람 확인 |
| ----------- | --------------- | ----------------- | ------- |
|<img src="https://github.com/boostcampwm2023/and09-PriceGuard/assets/83055885/f5c9cbab-c274-48b3-af62-76c74cf3a24e" width="200" height="400"/> <img src="https://github.com/boostcampwm2023/and09-PriceGuard/assets/83055885/cc932b39-9a82-4790-afc9-75d942b07c2f" width="200" height="400"/> | <img src="https://github.com/boostcampwm2023/and09-PriceGuard/assets/83055885/1632223d-62b4-410d-8ebd-619fdc10afe5" width="200" height="400"/> <img src="https://github.com/boostcampwm2023/and09-PriceGuard/assets/83055885/edd8efc4-ec57-480e-b14e-ee1ce410f095" width="200" height="400"/> | <img src="https://github.com/boostcampwm2023/and09-PriceGuard/assets/83055885/78a6263e-f589-4fe3-be9d-5867c7602c47" width="200" height="400"/> <img src="https://github.com/boostcampwm2023/and09-PriceGuard/assets/83055885/a8007a7f-b7bb-4181-a125-4ab0aceb0362" width="200" height="400"/> | <img src="https://github.com/boostcampwm2023/and09-PriceGuard/assets/83055885/ccdfb93e-ed85-4b57-9b87-e16e34f5fd96" width="200"/> |

## :memo: 기술 문서
- [Feature List](https://docs.google.com/spreadsheets/d/1e1Z9YpHPZxcBZN2XBPeoaz88hDby6WG5jmMz8xjqMrU/edit#gid=1955813262)
- [칸반 보드](https://github.com/orgs/boostcampwm2023/projects/47/views/2)
- [기능 명세](https://github.com/boostcampwm2023/and09-PriceGuard/wiki/%EC%9A%94%EA%B5%AC%EC%82%AC%ED%95%AD-%EB%AA%85%EC%84%B8%EC%84%9C)
- [디자인](https://www.figma.com/file/lym7gZiLmcpXEKMw7UpSSp/Android-new?type=design&node-id=54696%3A327&mode=design&t=udiVXXmXkEnqYArE-1)
- [ERD](https://github.com/boostcampwm2023/and09-PriceGuard/wiki/ERD)

더 많은 정보는 [저장소 Wiki](https://github.com/boostcampwm2023/and09-PriceGuard/wiki)를 참고해주세요.
