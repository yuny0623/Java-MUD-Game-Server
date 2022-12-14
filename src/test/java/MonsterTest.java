import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.server.game.monster.Monster;

import java.util.*;

public class MonsterTest {

    @Test
    @DisplayName("랜덤 난수를 생성하는지 테스트")
    public void randomNumberGenerateTest(){
        // given
        int x = 0;
        int y = 0;
        List<Integer> numberList = new ArrayList<>();

        // when
        for(int i = 0; i < 100; i++){
            x = (int) (Math.random() * (1 - (-1) + 1)) + (-1);
            y = (int) (Math.random() * (1 - (-1) + 1)) + (-1);
            numberList.add(x);
            numberList.add(y);
        }

        // then
        Assert.assertTrue(numberList.contains(-1));
        Assert.assertTrue(numberList.contains(0));
        Assert.assertTrue(numberList.contains(1));
    }

    @Test
    @DisplayName("5초에 한번 동작하는지 테스트")
    public void attackEveryFiveSecondsTest(){
        // given
        int val = 0;
        int count = 0;

        // when
        for(int i = 0; i < 100; i++){
            ++val;
            if((val / 5) == 1){
                val = 0;
                count ++;
            }
        }

        // then
        Assert.assertEquals(count, 20);
    }


    @Test
    @DisplayName("몬스터가 죽었을 경우 interrupt 메소드에 의해 몬스터 스레드가 중지되는지 테스트")
    public void monster_thread_interrupt_test(){
        // given
        Monster monster = new Monster(UUID.randomUUID().toString());
        monster.start();

        // when
        while(true) {
            boolean isDead = monster.receiveDamage(3);
            if(isDead){
                monster.interrupt();
                break;
            }
        }

        // then
        Assert.assertTrue(monster.isInterrupted());
        try {
            Thread.sleep(100);
        }catch(InterruptedException e){
            e.printStackTrace();
        }
        Assert.assertFalse(monster.isAlive());
    }

    @Test
    @DisplayName("몬스터가 죽었을 경우 새로운 몬스터가 생성되는지 테스트")
    public void new_monster_create_test_when_monster_die(){
        // given
        Map<String, Monster> monsterMap = new HashMap<>();
        for(int i = 0; i< 10; i++){
            String monsterName = String.valueOf(i);
            Monster monster = new Monster(monsterName);
            monsterMap.put(monsterName, monster);
            monster.start();
        }

        // when
        Monster monster = monsterMap.get("3");
        while(true){
            boolean isDead = monster.receiveDamage(3);
            if(isDead){
                monsterMap.remove("3");
                break;
            }
        }
        System.out.println(monsterMap.size());
        int diff = 10 - monsterMap.size();
        for(int i = 0; i <diff; i++){
            String monsterName = UUID.randomUUID().toString();
            Monster newMonster = new Monster(monsterName);
            monsterMap.put(monsterName, newMonster);
            newMonster.start();
        }

        // then
        Assert.assertEquals(10, monsterMap.size());
    }

    @Test
    @DisplayName("몬스터 포션 생성 확인 테스트")
    public void monster_potion_generate_test(){
        // given
        String nickname = "gradle";
        Monster monster = new Monster(nickname);
        monster.start();

        // when
        int hpPotion = monster.getHpPotion();
        int strPotion = monster.getStrPotion();

        // then
        Assert.assertTrue(hpPotion > 0);
        Assert.assertTrue(strPotion > 0);
    }
}
