import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.server.game.monster.Monster;

import java.util.ArrayList;
import java.util.List;

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
    @DisplayName("몬스터가 죽었을 경우 interrupt메소드에 의해 몬스터 스레드가 중지되는지 테스트")
    public void monster_thread_interrupt_test(){
        // given
        Monster monster = new Monster();
        monster.start();

        // when
        while(true) {
            boolean isDead = monster.attacked(3);
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

        // when

        // then
    }
}
