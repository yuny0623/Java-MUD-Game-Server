# ⭐️Java MUD Game project - Server

## 🍀 Project Intro
```
🌊 프로젝트 소개
MUD: Multi User Dungeon은 초기 온라인 게임의 한 형태이다. 
여러 사용자가 RPG 배경의 던전을 탐험한다는 이름에서 MUD라는 이름이 붙었다. 
당시 GUI가 없었기 때문에 텍스트로 입력을 주고 텍스트 출력을 얻는 형태이다.
해당 MUD 게임용 Server와 Client를 만들어보도록 한다. 
 
🔎 서버 기능 소개 
   1️⃣ 사용자 로그인 처리 
   2️⃣ 사용자 데이터 저장 
   3️⃣ 몬스터 생성 
   4️⃣ 아이템 제공 
   5️⃣ 공격에 따른 데미지 계산 
   6️⃣ 유저 접속 종료 및 로그아웃 처리 
   7️⃣ 다중 접속 처리 
   
🔎 플레이 방법 
   1️⃣ move x y
   2️⃣ attack
   3️⃣ monsters
   4️⃣ users
   5️⃣ chat <유저이름> <대화내용>
   6️⃣ potion hp
   7️⃣ potion str 
   8️⃣ bot 
   9️⃣ exit bot 
```

## 👍 Play Rule
``` 
   🌏 Map 
   1️⃣ 게임은 30x30 크기의 던전을 가정한다. 
   
   🏃 User 
   1️⃣ 사용자는 최초 로그인하면 던전의 임의의 위치에 떨어진다. 
   2️⃣ 5분 이내에 재접속하지 않으면 로그인 정보가 사라진다. 
   3️⃣ 동시 접속 시 기존 접속을 강제 종료한다. 
   4️⃣ 사용자의 체력 기본값은 30이다. 
   5️⃣ 사용자의 기본 공격력은 3이다.
   6️⃣ 사용자의 기본 체력 회복 포션은 1개이다. 
   7️⃣ 사용자의 기본 공격력 강화 포션은 1개이다. 
   
   🐞 Monster 
   1️⃣ 1분에 한번씩 몬스터가 10마리가 되도록 생성된다. 
   2️⃣ 몬스터의 생성 위치는 랜덤이다. 
   3️⃣ 몬스터는 5~10 사이의 랜덤한 체력(hp)을 부여받는다. 
   4️⃣ 몬스터는 3~5 사이의 랜덤한 공격력(str)을 부여받는다. 
   5️⃣ 몬스터는 랜덤한 개수의 체력 회복 포션과 공격력 강화 포션을 갖고 있다. 
   6️⃣ 사용자가 몬스터를 죽이면 몬스터가 갖고 있던 포션이 사용자에게 주어진다. 
   7️⃣ 몬스터는 자기 좌표 기준 주변의 [-1,-1] ~ [1,1] 사이의 9칸의 유저를 5초 간격으로 공격한다. 

   🍹 Item 
   1️⃣ 체력 회복 포션: 체력(hp)을 10 회복시킨다. 
   2️⃣ 공격력 강화 포션: 1분 동안 공격력(str)을 +3 증가시킨다. 
```

## ❓ How to use 
```
💻 Windows 
    1️⃣ gradlew build 
    2️⃣ *.jar 파일 실행 
   
🍎 Linux/Mac
    1️⃣ ./gradlew build 
    2️⃣ *.jar 파일 실행 
```

## 📲 Tech
```
💾 Java 17
📷 Gradle 
📹 TCP/IP Socket Programming
💻 Jedis
📺 json-simple
```

## 📚 Reference
 - Jedis: https://github.com/redis/jedis 
 - Jedis: https://www.baeldung.com/jedis-java-redis-client-library 
 - Redis: https://redis.io/docs/ 
 - Redis: https://architecturenotes.co/redis/
 - json-simple: https://tychejin.tistory.com/139
 - json-simple: https://mkil.tistory.com/323 


## 🔗 Game Client Repository
> [클라이언트 리포지토리](https://github.com/yuny0623/Java-MUD-Game-Client)
