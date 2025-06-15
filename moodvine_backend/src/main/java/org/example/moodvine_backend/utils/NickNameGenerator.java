package org.example.moodvine_backend.utils;

import java.util.Random;

public class NickNameGenerator {
    private static final String[] ADJECTIVES = {
            "快乐的", "忧郁的", "阳光的", "安静的", "热情的",
            "聪明的", "勇敢的", "害羞的", "好奇的", "友好的",
            "神秘的", "优雅的", "活泼的", "冷静的", "浪漫的"
    };
    private static final String[] NOUNS = {
            "旅行者", "梦想家", "观察者", "探险家", "艺术家",
            "思考者", "诗人", "音乐家", "园丁", "收藏家",
            "舞者", "读者", "作家", "摄影师", "美食家"
    };
    private static final String[] ANIMALS = {
            "熊猫", "狐狸", "海豚", "猎豹", "夜莺",
            "天鹅", "蝴蝶", "猫咪", "松鼠", "鲸鱼"
    };
    public static String generateRandomNickname() {
        Random random = new Random();
        int type = random.nextInt(3);

        switch(type) {
            case 0: // 形容词 + 名词
                return ADJECTIVES[random.nextInt(ADJECTIVES.length)] +
                        NOUNS[random.nextInt(NOUNS.length)];

            case 1: // 动物 + 编号
                return ANIMALS[random.nextInt(ANIMALS.length)] +
                        (1000 + random.nextInt(9000));

            case 2: // 名词 + 动物
                return NOUNS[random.nextInt(NOUNS.length)] +
                        ANIMALS[random.nextInt(ANIMALS.length)];

            default:
                return "用户" + (10000 + random.nextInt(90000));
        }
    }
}
