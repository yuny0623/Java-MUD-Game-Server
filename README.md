# ๐ฎ Java MUD Game Project - Server

## ๐ Project Intro
```
๐ ํ๋ก์ ํธ ์๊ฐ
   1๏ธโฃ MUD: Multi User Dungeon์ ์ด๊ธฐ ์จ๋ผ์ธ ๊ฒ์์ ํ ํํ์ด๋ค. 
   2๏ธโฃ ์ฌ๋ฌ ์ฌ์ฉ์๊ฐ RPG ๋ฐฐ๊ฒฝ์ ๋์ ์ ํํํ๋ค๋ ์ด๋ฆ์์ MUD๋ผ๋ ์ด๋ฆ์ด ๋ถ์๋ค. 
   3๏ธโฃ ๋น์ GUI๊ฐ ์์๊ธฐ ๋๋ฌธ์ ํ์คํธ๋ก ์๋ ฅ์ ์ฃผ๊ณ  ํ์คํธ ์ถ๋ ฅ์ ์ป๋ ํํ์ด๋ค.
   4๏ธโฃ ํด๋น MUD ๊ฒ์์ฉ Server์ Client๋ฅผ ๋ง๋ค์ด๋ณด๋๋ก ํ๋ค. 
   5๏ธโฃ ์ถ๊ฐ TCP ์์ผ์ ์ฌ์ฉํ์ฌ HTTPํ๋กํ ์ฝ์ ํตํ REST API๋ฅผ ์ ๊ณตํ๋ค. 
 
๐ ์๋ฒ ๊ธฐ๋ฅ ์๊ฐ 
   1๏ธโฃ ์ฌ์ฉ์ ๋ก๊ทธ์ธ ์ฒ๋ฆฌ 
   2๏ธโฃ ์ฌ์ฉ์ ๋ฐ์ดํฐ ์ ์ฅ 
   3๏ธโฃ ๋ชฌ์คํฐ ์์ฑ 
   4๏ธโฃ ์์ดํ ์ ๊ณต 
   5๏ธโฃ ๊ณต๊ฒฉ์ ๋ฐ๋ฅธ ๋ฐ๋ฏธ์ง ๊ณ์ฐ 
   6๏ธโฃ ์ ์  ์ ์ ์ข๋ฃ ๋ฐ ๋ก๊ทธ์์ ์ฒ๋ฆฌ 
   7๏ธโฃ ์ ์  ๋ค์ค ์ ์ ์ฒ๋ฆฌ 
   8๏ธโฃ HTTP ์ ์์ ์ํ REST API ์ ๊ณต 
   
๐ ํ๋ ์ด ๋ฐฉ๋ฒ 
   1๏ธโฃ move x y
   2๏ธโฃ attack
   3๏ธโฃ monsters
   4๏ธโฃ users
   5๏ธโฃ chat <username> <message>
   6๏ธโฃ potion hp
   7๏ธโฃ potion str 
   8๏ธโฃ bot 
   9๏ธโฃ exit bot 
```

## ๐ Game Rule
``` 
๐ Map 
   1๏ธโฃ ๊ฒ์์ 30x30 ํฌ๊ธฐ์ ๋์ ์ ๊ฐ์ ํ๋ค. 
   
๐ User 
   1๏ธโฃ ์ฌ์ฉ์๋ ์ต์ด ๋ก๊ทธ์ธํ๋ฉด ๋์ ์ ์์์ ์์น์ ๋จ์ด์ง๋ค. 
   2๏ธโฃ 5๋ถ ์ด๋ด์ ์ฌ์ ์ํ์ง ์์ผ๋ฉด ๋ก๊ทธ์ธ ์ ๋ณด๊ฐ ์ฌ๋ผ์ง๋ค. 
   3๏ธโฃ ๋์ ์ ์ ์ ๊ธฐ์กด ์ ์์ ๊ฐ์  ์ข๋ฃํ๋ค. 
   4๏ธโฃ ์ฌ์ฉ์์ ์ฒด๋ ฅ ๊ธฐ๋ณธ๊ฐ์ 30์ด๋ค. 
   5๏ธโฃ ์ฌ์ฉ์์ ๊ธฐ๋ณธ ๊ณต๊ฒฉ๋ ฅ์ 3์ด๋ค.
   6๏ธโฃ ์ฌ์ฉ์์ ๊ธฐ๋ณธ ์ฒด๋ ฅ ํ๋ณต ํฌ์์ 1๊ฐ์ด๋ค. 
   7๏ธโฃ ์ฌ์ฉ์์ ๊ธฐ๋ณธ ๊ณต๊ฒฉ๋ ฅ ๊ฐํ ํฌ์์ 1๊ฐ์ด๋ค. 
   
๐ Monster 
   1๏ธโฃ 1๋ถ์ ํ๋ฒ์ฉ ๋ชฌ์คํฐ๊ฐ 10๋ง๋ฆฌ๊ฐ ๋๋๋ก ์์ฑ๋๋ค. 
   2๏ธโฃ ๋ชฌ์คํฐ์ ์์ฑ ์์น๋ ๋๋ค์ด๋ค. 
   3๏ธโฃ ๋ชฌ์คํฐ๋ 5~10 ์ฌ์ด์ ๋๋คํ ์ฒด๋ ฅ(hp)์ ๋ถ์ฌ๋ฐ๋๋ค. 
   4๏ธโฃ ๋ชฌ์คํฐ๋ 3~5 ์ฌ์ด์ ๋๋คํ ๊ณต๊ฒฉ๋ ฅ(str)์ ๋ถ์ฌ๋ฐ๋๋ค. 
   5๏ธโฃ ๋ชฌ์คํฐ๋ ๋๋คํ ๊ฐ์์ ์ฒด๋ ฅ ํ๋ณต ํฌ์๊ณผ ๊ณต๊ฒฉ๋ ฅ ๊ฐํ ํฌ์์ ๊ฐ๊ณ  ์๋ค. 
   6๏ธโฃ ์ฌ์ฉ์๊ฐ ๋ชฌ์คํฐ๋ฅผ ์ฃฝ์ด๋ฉด ๋ชฌ์คํฐ๊ฐ ๊ฐ๊ณ  ์๋ ํฌ์์ด ์ฌ์ฉ์์๊ฒ ์ฃผ์ด์ง๋ค. 
   7๏ธโฃ ๋ชฌ์คํฐ๋ ์๊ธฐ ์ขํ ๊ธฐ์ค ์ฃผ๋ณ์ [-1,-1] ~ [1,1] ์ฌ์ด์ 9์นธ์ ์ ์ ๋ฅผ 5์ด ๊ฐ๊ฒฉ์ผ๋ก ๊ณต๊ฒฉํ๋ค. 

๐น Item 
   1๏ธโฃ ์ฒด๋ ฅ ํ๋ณต ํฌ์: ์ฒด๋ ฅ(hp)์ 10 ํ๋ณต์ํจ๋ค. 
   2๏ธโฃ ๊ณต๊ฒฉ๋ ฅ ๊ฐํ ํฌ์: 1๋ถ ๋์ ๊ณต๊ฒฉ๋ ฅ(str)์ +3 ์ฆ๊ฐ์ํจ๋ค. 
   
๐ก Extra 
   1๏ธโฃ HTTP ์ ์์ ์ํ REST API๋ฅผ ์ ๊ณตํ๊ณ  ๋์ผํ ๋ช๋ น์ด๋ก ํ๋ ์ดํ  ์ ์๋ค. 
```

## ๐ Command Intro
``` 
๐ move x y
    ํ์ฌ ์ขํ์์ x, y ๋งํผ ์ด๋ํ๋ค. x, y๋ ๊ฐ๊ฐ 3์ดํ๋ก ์์ง์ธ๋ค. 
   
๐ฅ attack 
    ํ์ฌ ์ขํ ๊ธฐ์ค [-1,1] ~ [1,1]๊น์ง 9์นธ ์์ญ์ ๋ํด ๋ชฌ์คํฐ๋ฅผ ๊ณต๊ฒฉํ๋ค.  
    
๐บ monsters
    ๋ชฌ์คํฐ๋ค์ ์ขํ๋ฅผ ๋์ดํ๋ค. 
    
๐ช users
    ์ ์ ์ ์ด๋ฆ๊ณผ ์ขํ๋ฅผ ๋์ดํ๋ค. 
    
๐ข chat <username> <message>
    ์ฃผ์ด์ง ์ ์ ์ด๋ฆ์ ์ ์ ์๊ฒ ๋ํ ๋ด์ฉ์ ์ ๋ฌํ๋ค. 
    
๐ potion hp 
    ์ฒด๋ ฅ ์ฆ๊ฐ ํฌ์์ ๋ณต์ฉํ๋ค.     
        
๐ธ potion str 
    ๊ณต๊ฒฉ๋ ฅ ์ฆ๊ฐ ํ์์ ๋ณต์ฉํ๋ค. 
    
๐ฐ bot 
    Bot ๋ชจ๋๋ฅผ ํ์ฑํํ๋ค. Bot ๋ชจ๋์์๋ ๋๋คํ ๋ช๋ น์ด๋ฅผ 1์ด๋ง๋ค ์์๋ก ๊ณจ๋ผ ์ํํ๋ค. 
    
๐ง exit bot 
    Bot ๋ชจ๋๋ฅผ ์ข๋ฃํ๋ค. 
```

## ๐ก HTTP Command Intro 
### ๐ซ login
```
๐จ Request
    POST/127.0.0.1:8081
    {
        "nickname": "tony",
        "command": "login"
    }
    
๐ฉ Response
    {
        "Notice":"[Create User] [nickname: tony, hp:30, str:3, x_pos: 21, y_pos: 9]"
    }   
```

### ๐ฅ attack 
```
๐จ Request
    POST/127.0.0.1:8081
    {
        "nickname": "tony",
        "command": "attack"
    }
    
๐ฉ Response
    {
        "Notice":"tony attack a Monster with power of 3."
    }    
```
### ๐ move x y 
```
๐จ Request
    POST/127.0.0.1:8081
    {
        "nickname": "tony",
        "command": "move 1 2"
    }
    
๐ฉ Response
    {
        "Notice":"tony move to [22, 11]"
    }
```
### ๐ช users
```
๐จ Request
    POST/127.0.0.1:8081
    {
        "nickname": "tony",
        "command": "users"
    }
    
๐ฉ Response
    {
        "UserInfo":"razlo 29 24\ntony 22 11\n"
    }
```
### ๐บ monsters
```
๐จ Request
    POST/127.0.0.1:8081
    {
        "nickname": "tony",
        "command": "monsters"
    }
    
๐ฉ Response
    {
        "MonsterInfo":"Monster 14 22\nMonster 17 13\nMonster 20 7\nMonster 9 15\nMonster 26 0\nMonster 4 18\nMonster 5 26\nMonster 26 22\nMonster 19 18\nMonster 16 15\n"
    }
```
### ๐ potion hp
```
๐จ Request
    POST/127.0.0.1:8081
    {
        "nickname": "tony",
        "command": "potion hp"
    }
    
๐ฉ Response
    {
        "Notice":"tony recover 10 hp."
    }
    
```
### ๐ธ potion str 
```
๐จ Request
    POST/127.0.0.1:8081
    {
        "nickname": "tony",
        "command": "potion str"
    }
    
๐ฉ Response
    {
        "Notice":"tony increase 3 str."
    }
```

### ๐ข chat \<username\> \<message\>
```
๐จ Request
    POST/127.0.0.1:8081
    {
        "nickname": "tony",
        "command": "chat razlo hi"
    }
    
๐ฉ No Response     
```


## โ How to execute
```
๐ป Windows 
    1๏ธโฃ gradlew build 
    2๏ธโฃ *.jar ํ์ผ ์คํ 
   
๐ Linux/Mac
    1๏ธโฃ ./gradlew build 
    2๏ธโฃ *.jar ํ์ผ ์คํ 
```

## ๐ Game Server Download
```
๐ฆ Notice 
    1๏ธโฃ Java 17 ๊ถ์ฅ
    2๏ธโฃ java -jar game-server.jar 
```
> [Game Server Download](https://drive.google.com/file/d/15AO6JmIPGinchSFgUaUHsIzBJmONOHop/view?usp=share_link)


## ๐ฒ Tech
```
๐พ Java 17
๐ท gradle 
๐น TCP/IP Socket Programming
๐ป Jedis
๐บ json-simple
```

## ๐ Reference
 - Jedis: https://github.com/redis/jedis 
 - Jedis: https://www.baeldung.com/jedis-java-redis-client-library 
 - Redis: https://redis.io/docs/ 
 - Redis: https://architecturenotes.co/redis/
 - json-simple: https://tychejin.tistory.com/139
 - json-simple: https://mkil.tistory.com/323 


## ๐ Game Client Repository
> [ํด๋ผ์ด์ธํธ ๋ฆฌํฌ์งํ ๋ฆฌ](https://github.com/yuny0623/Java-MUD-Game-Client)
