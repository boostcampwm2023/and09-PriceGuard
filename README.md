# Price Guard
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

1. 그래프 작성을 위한 대용량의 시계열 데이터를 정의하고 이를 효율적으로 처리하는 방법을 고안한다.
2. 다수의 사용자에 대해 짧은 주기로 데이터를 수집해야하는 시나리오에 대한 효율적인 해결책을 찾는다.
3. 개발한 기능을 보다 일반적인 상황에 쓸 수 있도록 라이브러리화한다.

이와 같은 기술적 도전을 해결하는 과정은 아래 링크에서 확인할 수 있습니다.

1. 그래프 라이브러리 구현
2. 데이터 캐싱
3. DB 쿼리 개선

## ❓ 문제 해결

프로젝트 진행 과정에서 만난 이슈들과 해결 방법에 대해 기술합니다. 아래 링크에서 확인할 수 있습니다.

1. [안드로이드 테마 적용](https://github.com/boostcampwm2023/and09-PriceGuard/wiki/%EC%95%88%EB%93%9C%EB%A1%9C%EC%9D%B4%EB%93%9C-%ED%85%8C%EB%A7%88-%EC%A0%81%EC%9A%A9)
2. [메모리 누수](https://github.com/boostcampwm2023/and09-PriceGuard/wiki/TroubleShooting-%E2%80%90-Memory-Leak)
3. [JWT 인증 처리](https://velog.io/@mks1103/JWT%EB%A1%9C-%EC%9D%B8%EC%A6%9D-%EC%B2%98%EB%A6%AC%ED%95%98%EA%B8%B0)
4. [HTTPS](https://velog.io/@kdogs/HTTPS-%EC%A0%81%EC%9A%A9%ED%95%98%EA%B8%B0feat.-Lets-Encrypt)
5. [Navigation 백스택 오류](https://github.com/boostcampwm2023/and09-PriceGuard/wiki/TroubleShooting-%E2%80%90-Navigation-%EB%B0%B1%EC%8A%A4%ED%83%9D-%EC%98%A4%EB%A5%98)

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
                  <img src="https://camo.githubusercontent.com/c71519c505cafc8d4cc686186e518c03d489b6e6038f3fdcd2d57e9c87d308f3/68747470733a2f2f696d672e736869656c64732e696f2f62616467652f436f726f7574696e65732d6633383431633f7374796c653d666f7274686562616765266c6f676f3d4b6f746c696e266c6f676f436f6c6f723d626c756576696f6c6574">
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
                <img src="https://camo.githubusercontent.com/6841025a5cd57f3ac26ae8b48390b17454c7a553e747ed06e569edd2c47e9b8e/68747470733a2f2f696d672e736869656c64732e696f2f62616467652f47697448756220416374696f6e732d3230383846463f7374796c653d666f7274686562616765266c6f676f3d47697448756220416374696f6e73266c6f676f436f6c6f723d626c61636b">
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

![서비스 아이콘 및 활용예시_230421](https://github.com/boostcampwm2023/and09-PriceGuard/assets/37584805/a0ef03ac-b821-4642-b499-5bd2f32671ea)


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

- 로그인/회원가입 화면
<img src="https://github.com/boostcampwm2023/and09-PriceGuard/assets/37584805/5508d8e1-f0ad-44d8-9278-70e9e41b5df0" width="200" height="400"/>
<img src="https://github.com/boostcampwm2023/and09-PriceGuard/assets/37584805/27e4d667-0ffb-4be9-ae22-fae9cab068e9" width="200" height="400"/>

- 상품 추천/ 상품 상세 화면
<img src="https://github.com/boostcampwm2023/and09-PriceGuard/assets/37584805/7c418d77-eca0-437c-b220-22ad89fdc76d" width="200" height="400"/>
<img src="https://github.com/boostcampwm2023/and09-PriceGuard/assets/37584805/4a111411-46b6-4f86-b750-5827948d246b" width="200" height="400"/>

- 상품 추가/ 마이페이지 화면
<img src="https://github.com/boostcampwm2023/and09-PriceGuard/assets/37584805/e6d89da9-cb39-49f2-b0ee-073aa666a695" width="200" height="400"/>
<img src="https://github.com/boostcampwm2023/and09-PriceGuard/assets/37584805/f7778bae-e10c-4459-98d6-a74bbc686efa" width="200" height="400"/>

- 알람 확인
<img src="https://github.com/boostcampwm2023/and09-PriceGuard/assets/37584805/a43b64c9-2b6b-431c-8fbb-a93d8560f605" width="200" height="400"/>

## :memo: 기술 문서
- [Feature List](https://docs.google.com/spreadsheets/d/1e1Z9YpHPZxcBZN2XBPeoaz88hDby6WG5jmMz8xjqMrU/edit#gid=1955813262)
- [칸반 보드](https://github.com/orgs/boostcampwm2023/projects/47/views/2)
- [기능 명세](https://github.com/boostcampwm2023/and09-PriceGuard/wiki/%EC%9A%94%EA%B5%AC%EC%82%AC%ED%95%AD-%EB%AA%85%EC%84%B8%EC%84%9C)
- [디자인](https://www.figma.com/file/lym7gZiLmcpXEKMw7UpSSp/Android-new?type=design&node-id=54696%3A327&mode=design&t=udiVXXmXkEnqYArE-1)
- [ERD](https://github.com/boostcampwm2023/and09-PriceGuard/wiki/ERD)

더 많은 정보는 [저장소 Wiki](https://github.com/boostcampwm2023/and09-PriceGuard/wiki)를 참고해주세요.
