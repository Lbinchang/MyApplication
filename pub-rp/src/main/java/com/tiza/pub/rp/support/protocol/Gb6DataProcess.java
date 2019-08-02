package com.tiza.pub.rp.support.protocol;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tiza.pub.air.cache.ICache;
import com.tiza.pub.air.entry.KafkaUtil;
import com.tiza.pub.air.model.*;
import com.tiza.pub.air.util.CommonUtil;
import com.tiza.pub.air.util.JacksonUtil;
import com.tiza.pub.air.util.SpringUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.parser.Entity;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.tiza.pub.air.util.WebServiceUtil.doPut;
import static org.mortbay.util.LazyList.remove;

/**
 * Description: Gb6DataProcess
 * Author: DIYILIU
 * Update: 2019-07-03 09:50
 */

@Slf4j
@Service
public class Gb6DataProcess implements IDataProcess {
    protected int cmdId = 0xFF;

    @Resource
    protected ICache cmdCacheProvider;

    @Value("${kafka.work-topic}")
    private String workTopic;

    @Value("${areaIdPath}")
    private String areaIdPath;

    @Value("${kafka.brokers}")
    private String brokerList;



    @Override
    public Header parseHeader(byte[] bytes) {
        ByteBuf buf = Unpooled.copiedBuffer(bytes);
        // 读取头标志[0x23,0x23]
        buf.readBytes(new byte[2]);

        // 命令标识
        int cmd = buf.readUnsignedByte();

        //车辆识别码vin
        byte[] vinBytes = new byte[17];
        buf.readBytes(vinBytes);
        String vin = new String(vinBytes);

        // 软件版本号
        int version = buf.readUnsignedByte();
        // 加密方式
        int encrypt = buf.readUnsignedByte();

        //数据单元长度及读取数据
        int length = buf.readUnsignedShort();
        byte[] content = new byte[length];
        buf.readBytes(content);

        Gb6Header header = new Gb6Header();
        header.setCmd(cmd);
        header.setDevice(vin);
        header.setVersion(version);
        header.setEncrypt(encrypt);
        header.setBytes(content);

        return header;
    }

    @Override
    public void parse(byte[] content, Header header) {

    }

    @Override
    public byte[] pack(Header header, Object... argus) {
        return new byte[0];
    }

    @Override
    public void init() {
        cmdCacheProvider.put(cmdId, this);
    }

    public void detach(Gb6Header header, List<PackUnit> paramValues) {
        int cmd = header.getCmd();
        String vehicle = header.getVehicle();

        // 写入kafka中的map
        Map<String, Object> map2 = Maps.newHashMap();

        //获取当前时间用于存入网关时间
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // 更新当前位置信息
        if (0x02 == cmd) {

            Map<String,Object> map4sql= Maps.newHashMap();
            List keys = new ArrayList();
            for (int i = 0; i < paramValues.size(); i++) {
                PackUnit unit = paramValues.get(i);

                //将当前读取到的解析信息存入map for sql，用于向数据库中更新数据
                map4sql.putAll(paramValues.get(i).getData());
                // 此map是用于存解析到的信息中的第一条信息
                Map<String, Object> map = unit.getData();

                // 若解析到的信息有多条记录时，相同key时将读取最新的一条map，不同key时综合不同的key在一条map记录中
                map.putAll(map4sql);
                map2.putAll(map);

                //解析记录中的相同id时，continue。
                int id = unit.getId();
                if (keys.contains(id)) {
                    continue;
                }
                keys.add(id);
            }

            // 存入行政区县的代码
            com.alibaba.fastjson.JSONObject jsonObject = new com.alibaba.fastjson.JSONObject();
            jsonObject.put("lat",map2.get("gcj02Lat"));
            jsonObject.put("lng",map2.get("gcj02Lng"));
            String result= doPut(areaIdPath,jsonObject.toString());

            if (!result.isEmpty()){
                JSONObject json = JSON.parseObject(result);
                map2.put("districtBranch",json.get("branch"));
            }

            //更新mysql 的datetime的格式，mysql 8.0.15中插入datetime格式必须用单引号注释
            Timestamp dates = Timestamp.valueOf(sdf.format(d));
            String timeStr=dates.toString().substring(0, dates.toString().indexOf("."));//时间转换

            StringBuffer body = new StringBuffer();

            for (Iterator<String> iterator = map2.keySet().iterator(); iterator.hasNext(); ) {
                String key = iterator.next();
                Object value = map2.get(key);
                body.append(",");
                body.append(CommonUtil.underline(key));
                body.append("=");
                //处理空值，避免空指针
                if (value == null) {
                    body.append("null");
                } else {
                    body.append("'");
                    if (!CommonUtil.underline(key).equals("obd_data_time")) {
                        body.append(formatValue(value).toString());
                    } else {
                        // 时间格式转化
                        Timestamp dates1 = Timestamp.valueOf(sdf.format(new Date(header.getTime())));
                        String timeStr_obd = dates1.toString().substring(0, dates.toString().indexOf("."));//时间转换
                        body.append(timeStr_obd);
                    }
                    body.append("'");
                }
            }

            // gps时间格式转化
            Timestamp dates1 = Timestamp.valueOf(sdf.format(new Date(header.getTime())));
            String timeStr_gps=dates1.toString().substring(0, dates.toString().indexOf("."));//时间转换
            String sql = "UPDATE equip_status set online = '1', gps_time=  "+"'"+timeStr_gps+"'"+", gateway_time =  "+"'"+timeStr+"'"+body +" where equip_id = "+vehicle;
            log.info("更新车辆[{}]实时信息 ... ", vehicle);
            JdbcTemplate jdbcTemplate = SpringUtil.getBean("jdbcTemplate");
            jdbcTemplate.update(sql);
            log.info("sql 语句为{}",sql);
        }

        // 更新cmd3的kafka数据,不向数据库中写入
        if (0x03 == cmd) {

            Map<String,Object> map4sql= Maps.newHashMap();
            List keys = new ArrayList();
            for (int i = 0; i < paramValues.size(); i++) {

                PackUnit unit = paramValues.get(i);

                map4sql.putAll(paramValues.get(i).getData());
                // 此map是用于存解析到的信息中的第一条信息
                Map<String, Object> map = unit.getData();
                // 若解析到的信息有多条记录时，相同key时将读取最新的一条map，不同key时综合不同的key在一条map记录中
                map.putAll(map4sql);
                map2.putAll(map);
                //解析记录中的相同id时，continue。
                int id = unit.getId();
                if (keys.contains(id)) {
                    continue;
                }
                keys.add(id);
            }
        }
        // 写入 kafka
        map2.put("equipId",vehicle);
        map2.put("gpsTime",new Date(header.getTime()));
        map2.put("gatewayTime",sdf.format(d));


//     当信息量只有时间等无效信息时，不写入kafka中。
        if(map2.size()>3){
            Map<String,Object> notNull = Maps.newHashMap();
            notNull =  removeMapEmptyValue(map2);
            String jsonBody2= JacksonUtil.toJson(notNull);


            KafkaUtil.send(new KafkaMsg(workTopic, vehicle, jsonBody2));

            log.info("车辆[{}]写入Kafka[{}] ... ", vehicle, jsonBody2);

        }
    }

    /**
     * 解析协议中时间
     *
     * @param header
     * @param buf
     */
    public void fetchDate(Gb6Header header, ByteBuf buf) {
        // 数据采集时间
        byte[] dateArr = new byte[6];
        buf.readBytes(dateArr);

        Date date = CommonUtil.bytesToDate(dateArr);
        header.setTime(date.getTime());
    }


    public Object formatValue(Object obj) {
        if (obj instanceof Map || obj instanceof Collection) {
            return JacksonUtil.toJson(obj);
        }
        return obj;
    }

    /**
     * @param paramMap
     * @return
     */
    public static Map<String,Object> removeMapEmptyValue(Map<String,Object> paramMap){
             Map<String,Object> notNull = Maps.newHashMap();
        for (Iterator<String> iterator = paramMap.keySet().iterator(); iterator.hasNext(); ) {
            String key = iterator.next();
            Object value = paramMap.get(key);
            //处理空值，避免空指针
            if (value == null) {
            } else {
                notNull.put(key,value);
            }
        }
        return notNull;
    }
}
