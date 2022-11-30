2022-11-30

이슈:
server 가 구동이 안되는 이슈 발생.
원인: 서버 구동 과정에서 무한 루프가 발생했는데 그 이유를 찾았음.
RedisTemplate과 Game을 둘 다 singleton 으로 작성했는데
해당 클래스의 생성자 내에서 서로를 생성해주고 있었음.
그래서 서로가 생성해주는 코드 때문에 무한 루프가 걸려서
서버가 제대로 동작하지 못했음.
자 그렇다면 이걸 어떻게 수정할 수 있을까?
RedisTemplate 을 아예 Utility 클래스로 바꿔줄 수 도 있다.

특정 범위의 난수 생성: (int) (Math.random() * (최댓값-최소값 + 1)) + 최소값

고려할 사항:
친구의 의견에 따르면 Monster 를 Thread로 만드는게 낫다는 얘기를 했는데
이전에도 Monster를 Thread로 만들지에 대해 고민했었는데 바꿔야할까...?
exit bot을 하려면 readLine으로 입력을 읽어야하는데 readLine이 blocking 동작이라서
아무래도 bot동작과 exit bot 을 입력받는 동작 사이에서 분리가 필요함.
왜냐? readLine으로 exit bot 기다리면 블로킹되서 애초에 bot 이 동작할 수가 없거든요.

thread로 만들까 말까. -> 나중에 처리합시다. 분명 Thread 안 만들고 할 수 있는 방법이 있을텐데 그 방법이 훨씬 더 좋을거임.

Hot Fix:
RedisTemplate 에서 get 을 진행할때 TypeError 가 발생하는데 아마 Jedis 를 잘못 사용한 것 같다.
RedisTemplate은 Test코드로 직접 검증하고 짜자.

코드리뷰:
RedisTemplate 이 너무 추상화되어있음. 
redis 의 기능을 간단하게 쓰기 위함인데 너무 구체적인 비즈니스 로직이 들어간듯. 
monsters 를 Thread 로 만들고 MonsterAttacker 랑 MonsterManager 를 없애자. 

수정사항: 

