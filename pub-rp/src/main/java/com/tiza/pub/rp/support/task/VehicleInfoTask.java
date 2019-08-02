package com.tiza.pub.rp.support.task;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description: VehicleInfoTask
 * Author: DIYILIU
 * Update: 2019-06-13 10:25
 */

@Slf4j
@Service
public class VehicleInfoTask {
    public static Map<String, String> vehicleMap = new ConcurrentHashMap();

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Scheduled(fixedDelay = 5 * 60 * 1000, initialDelay = 5 * 1000)

    public void execute() {
/*        //启动IoC容器
        ApplicationContext ctx=new ClassPathXmlApplicationContext("applicationContext.xml");
//获取IoC容器中JdbcTemplate实例
        JdbcTemplate jdbcTemplate=(JdbcTemplate) ctx.getBean("jdbcTemplate");*/
        log.info("刷新车辆信息 ... ");

         String sql = "SELECT t.vin,t.id " +
                "FROM equip t " +
                "WHERE t.vin IS NOT NULL AND t.id IS NOT NULL";


        Map temp = new HashMap();
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql);
        while (rowSet.next()) {
            temp.put(rowSet.getString("vin"), rowSet.getString("id"));
        }

        Set oldKeys = vehicleMap.keySet();
        Set tempKeys = temp.keySet();

        Collection subKeys = CollectionUtils.subtract(oldKeys, tempKeys);
        for (Iterator iterator = subKeys.iterator(); iterator.hasNext(); ) {
            String key = (String) iterator.next();
            vehicleMap.remove(key);
        }
        vehicleMap.putAll(temp);
    }
}
