package com.zNova;

import cn.xuyanwu.spring.file.storage.EnableFileStorage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * 启动程序
 * 
 * @author ruoyi
 */
@EnableFileStorage
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class zNovaApplication
{
    public static void main(String[] args)
    {
        // System.setProperty("spring.devtools.restart.enabled", "false");
        SpringApplication.run(zNovaApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  zNova启动成功   ლ(´ڡ`ლ)ﾞ  \n" +
                "      _   _                 \n" +
                "     | \\ | |                \n" +
                "  ___|  \\| | _____   ____ _ \n" +
                " |_  / . ` |/ _ \\ \\ / / _` |\n" +
                "  / /| |\\  | (_) \\ V / (_| |\n" +
                " /___|_| \\_|\\___/ \\_/ \\__,_|\n" +
                "                            ");
    }
}
