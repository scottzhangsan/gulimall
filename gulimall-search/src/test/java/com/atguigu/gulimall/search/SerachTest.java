package com.atguigu.gulimall.search;

import org.elasticsearch.client.RestHighLevelClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootApplication
public class SerachTest {
     @Autowired
     private RestHighLevelClient client ;

     @Test
     public void test01(){
         System.out.println(client.toString()+"***************8");
     }
}
