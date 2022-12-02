2022-12-01 

이슈: 
없음.

해야할 일:
monster -> Thread 로 만들자. 

Hot Fix: sadd 에서 TypeError 발생 -> nickname이 redis 에서 keyword 로 잡혀있어서
nickname을 key로 주려고 하면 예외가 발생함.

아래와 같은 예외가 발생함. 
redis.clients.jedis.exceptions.JedisDataException: WRONGTYPE Operation against a key holding the wrong kind of value

그러니 nickname 대신에 nicknames 를 쓰자. 

ServerAction이 굳이 필요하지 않을듯. -> ServerAction 을 start 시켜주는게 아니라
MainServer를 Thread 로 만들고 MainServer 를 run 하는 방식으로 바꾸자. 
