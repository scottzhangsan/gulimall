package com.atguigu.gulimall.product.config;

import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyRedissonConfig {

    /**
     * 获取redisson的客户端
     * @return
     */
    /**
     * redisson 的好处
     * 1：锁的自动续期，如果业务超长，运行期间自动给锁续上新的30s,不用担心业务的时间长，锁自动被删除掉
     * 2：加锁的业务只要运行完成，就不会给当前锁续期，即使不手动解锁，锁默认在30s以后自动删除
     * 3:如果要设定锁的时间【时间到了以后就不会自动的续期】，设定的时间一定要大于业务的执行时间，要不然下一次的解锁会失败
     *
     * @return
     */
    @Bean
    public RedissonClient redisson() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://150.158.106.207:6379").setPassword("123456");
        RedissonClient client = Redisson.create(config);
        return client;
    }
}
