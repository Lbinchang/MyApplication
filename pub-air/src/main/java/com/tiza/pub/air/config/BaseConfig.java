package com.tiza.pub.air.config;

import com.tiza.pub.air.cache.ICache;
import com.tiza.pub.air.cache.ram.RamCacheProvider;
import com.tiza.pub.air.util.SpringUtil;
import org.springframework.context.annotation.Bean;

/**
 * Description: BaseConfig
 * Author: DIYILIU
 * Update: 2018-12-06 10:21
 */

public class BaseConfig {

    @Bean
    public SpringUtil springUtil(){

        return new SpringUtil();
    }

    @Bean
    public ICache cmdCacheProvider(){

        return new RamCacheProvider();
    }

    @Bean
    public ICache vehicleInfoProvider(){

        return new RamCacheProvider();
    }
}
