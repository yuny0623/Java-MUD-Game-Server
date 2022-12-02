# Java MUD Game project - Server

## Project Intro
```
🌊 프로젝트 소개
MUD: Multi User Dungeon 은 초기 온라인 게임의 한 형태이다. 
여러 사용자가 RPG 배경의 던전을 탐험한다는 이름에서 MUD라는 이름이 붙었다. 
당시 GUI가 없었기 때문에 텍스트로 입력을 주고 텍스트 출력을 얻는 형태이다.
해당 MUD 게임용 Server와 Client를 만들어보도록 한다. 
 
💾 기능 소개 
   - 사용자 로그인 처리 
   - 사용자 데이터 저장 
   - 몬스터 생성 
   - 아이템 제공 
   - 공격에 따른 데미지 계산 
   - 유저 접속 종료 및 로그아웃 처리 
   - 다중 접속 처리 
   
📑 플레이 방법 
   - move x y
   - attack
   - monsters
   - users
   - chat <유저이름> <대화내용>
   - bot 
```

## Tech
```
- Java 17
- Gradle 
- TCP/IP Socket Programming
- Jedis
- json-simple
```

## Reference
 - Jedis: https://github.com/redis/jedis 
 - Jedis: https://www.baeldung.com/jedis-java-redis-client-library 
 - Redis: https://redis.io/docs/ 
 - Redis: https://architecturenotes.co/redis/
 - json-simple: https://tychejin.tistory.com/139
 - json-simple: https://mkil.tistory.com/323 


## Game Client Repository
> [클라이언트 리포지토리](https://github.com/yuny0623/Java-MUD-Game-Client)
