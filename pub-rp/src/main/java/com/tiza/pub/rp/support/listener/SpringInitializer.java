package com.tiza.pub.rp.support.listener;

import com.tiza.pub.air.model.IDataProcess;
import com.tiza.pub.air.util.JacksonUtil;
import com.tiza.pub.air.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Description: SpringInitializer
 * Author: DIYILIU
 * Update: 2018-12-06 10:49
 */

@Slf4j
public class SpringInitializer implements ApplicationListener {

    private List<Class> protocols;

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        log.info("协议{}解析初始化 ...", JacksonUtil.toJson(protocols));

        for (Class protocol : protocols) {
            Map parses = SpringUtil.getBeansOfType(protocol);

            for (Iterator iterator = parses.keySet().iterator(); iterator.hasNext(); ) {
                String key = (String) iterator.next();
                IDataProcess process = (IDataProcess) parses.get(key);
                process.init();

                log.info("注册指令[{}]解析器...", key);
            }
        }
    }

    public void setProtocols(List<Class> protocols) {
        this.protocols = protocols;
    }
}
