2022-11-24

server 에서 전달하는 데이터가 없으면 클라이언트에서 readLine에서
blocking 되어서 계속 기다리는데 이걸 server 측에서 지속적으로
healthcheck signal을 보내는 방식으로 운영하면 어떨지?

남은 과제:
1.  login 했을때 nickname 중복 처리를 어떻게 할래?
    똑같은 nickname 으로 재접속하면 이전 연결은 끊어야하는데
    그럴려면 Thread를 관리하는 list에서 nickname을 알고 있어야함.
    이걸 어떻게 처리할건지?
2. redisTemplate 를 완성을 해야하는데 Jedis 사용법 숙지하기.

주의사항:
MainServer 와 ServerSocketThread 사이에서 순환참조되지 않는 방향으로
해결해봅시다.

그리고 List<Map<String, Thread>> 인걸로 하나만 유지하자.

