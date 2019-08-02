package com.tiza.pub.rp.support.protocol.cmd;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tiza.pub.air.model.Gb6Header;
import com.tiza.pub.air.model.Header;
import com.tiza.pub.air.model.PackUnit;
import com.tiza.pub.air.util.CommonUtil;
import com.tiza.pub.air.util.GpsCorrectUtil;
import com.tiza.pub.rp.support.protocol.Gb6DataProcess;
import com.tiza.pub.rp.support.task.VehicleInfoTask;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Description: Gb6_02
 * Author: DIYILIU
 * Update: 2019-07-03 09:51
 */

@Service
public class Gb6_02 extends Gb6DataProcess {

    public Gb6_02() {
        this.cmdId = 0x02;
    }

    Long obdDataTime ;
    @Override
    public void parse(byte[] content, Header header) {
        Gb6Header sixHeader = (Gb6Header) header;
        ByteBuf buf = Unpooled.copiedBuffer(content);
        // 解析时间
        fetchDate(sixHeader, buf);
        obdDataTime=header.getTime();
        int indexInfo = 0;

        List<PackUnit> list = new ArrayList();
        while (buf.readableBytes() > 1) {
            int type = buf.readUnsignedByte();
             // OBD 信息
            if (0x01 == type) {
                indexInfo++;
                if (indexInfo == 1 ){
                    int flowInfo = buf.readUnsignedShort();
                }

                PackUnit unit = new PackUnit(type);
                unit.setData(parseOBD(buf));
                list.add(unit);
            }
            // 数据流信息
            else if (0x02 == type) {
                indexInfo++;
                if (indexInfo == 1 ){
                    int flowInfo = buf.readUnsignedShort();
                }
                PackUnit unit = new PackUnit(type);
                unit.setData( parseFlow(buf));
                list.add(unit);
            }
            // 颗粒物
            else if(0xAA == type){

                indexInfo++;
                if (indexInfo == 1 ){
                    int flowInfo = buf.readUnsignedShort();
                }
                PackUnit unit = new PackUnit(type);
                unit.setData( parseGrain(buf));
                list.add(unit);
            }
        }

        detach(sixHeader, list);
    }


    private Map parseOBD(ByteBuf buf) {
        int protocol = buf.readUnsignedByte();
        int mil = buf.readUnsignedByte();

        byte[] supStatus = new byte[2];
        buf.readBytes(supStatus);

        byte[] readyStatus = new byte[2];
        buf.readBytes(readyStatus);

        String support = CommonUtil.bytes2BinaryStr(supStatus);
        String read =  CommonUtil.bytes2BinaryStr(readyStatus);

        String catalystSupport = support.substring(0,1);
        String heatedCatalystSupport = support.substring(1,2);
        String evaporativeSupport = support.substring(2,3);
        String secondAirSupport = support.substring(3,4);
        String acRefrigerantSupport = support.substring(4,5);
        String exhaustSensorSupport =support.substring(5,6);
        String exhaustHeaterSupport = support.substring(6,7);
        String egrVvtSupport = support.substring(7,8);
        String coldStartAidSupport = support.substring(8,9);
        String boostPressureSupport = support.substring(9,10);
        String dpfSupport = support.substring(10,11);
        String scrSupport =support.substring(11,12);
        String nmhcSupport = support.substring(12,13);
        String misfireSupport =support.substring(13,14);
        String fuelSupport =support.substring(14,15);
        String componentSupport = support.substring(15,16);
        String catalystReady = read.substring(0,1);
        String heatedCatalystReady = read.substring(1,2);
        String evaporativeReady= read.substring(2,3);
        String secondAirReady = read.substring(3,4);
        String acRefrigerantReady = read.substring(4,5);
        String exhaustSensorReady =read.substring(5,6);
        String exhaustHeaterReady = read.substring(6,7);
        String egrVvtReady = read.substring(7,8);
        String coldStartAidReady = read.substring(8,9);
        String boostPressureReady = read.substring(9,10);
        String dpfReady = read.substring(10,11);
        String scrReady =read.substring(11,12);
        String nmhcReady = read.substring(12,13);
        String misfireReady =read.substring(13,14);
        String fuelReady =read.substring(14,15);
        String componentReady = read.substring(15,16);

//          车辆识别码
        byte[] vinBytes = new byte[17];
        buf.readBytes(vinBytes);

        String vin = new String(vinBytes);

//        软件标定识别
        byte[] softBytes = new byte[18];
        buf.readBytes(softBytes);
        String soft = new String(softBytes);
//         标定验证码CVN
        byte[] cvnBytes = new byte[18];
        buf.readBytes(cvnBytes);
        String cvn = new String(cvnBytes);

        // IUPR值
        byte[] iuprBytes = new byte[36];

        buf.readBytes(iuprBytes);
        String iupr = CommonUtil.bytes2BinaryStr(supStatus);
        System.out.println(iupr);

//        故障码总数
        int faultNum = buf.readUnsignedByte();

        // 故障信息
        List faults = Lists.newArrayList();

        if (faultNum > 0 && faultNum != 0xFE) {
            for (int i = 0; i < faultNum; i++) {
                int code = buf.readInt();
                faults.add(code);
            }
        }


        Map map = Maps.newHashMap();


        String equipid= VehicleInfoTask.vehicleMap.get(vin);

//        map.put("equipId", equipid);
        map.put("obdProtocol", protocol);
        map.put("obdMil", mil);
        map.put("catalystSupport",catalystSupport);
        map.put("heatedCatalystSupport",heatedCatalystSupport);
        map.put("evaporativeSupport",evaporativeSupport);
        map.put("secondAirSupport",secondAirSupport);
        map.put("acRefrigerantSupport",acRefrigerantSupport);
        map.put("exhaustSensorSupport",exhaustSensorSupport);
        map.put("exhaustHeaterSupport",exhaustHeaterSupport);
        map.put("egrVvtSupport",egrVvtSupport);
        map.put("coldStartAidSupport",coldStartAidSupport);
        map.put("boostPressureSupport",boostPressureSupport);
        map.put("dpfSupport",dpfSupport);
        map.put("scrSupport",scrSupport);
        map.put("nmhcSupport",nmhcSupport);
        map.put("misfireSupport",misfireSupport);
        map.put("fuelSupport",fuelSupport);
        map.put("componentSupport",componentSupport);

        map.put("catalystReady",catalystReady);
        map.put("heatedCatalystReady",heatedCatalystReady);
        map.put("evaporativeReady",evaporativeReady);
        map.put("secondAirReady",secondAirReady);
        map.put("acRefrigerantReady",acRefrigerantReady);
        map.put("exhaustSensorReady",exhaustSensorReady);
        map.put("exhaustHeaterReady",exhaustHeaterReady);
        map.put("egrVvtReady",egrVvtReady);
        map.put("coldStartAidReady",coldStartAidReady);
        map.put("boostPressureReady",boostPressureReady);
        map.put("dpfReady",dpfReady);
        map.put("scrReady",scrReady);
        map.put("nmhcReady",nmhcReady);
        map.put("misfireReady",misfireReady);
        map.put("fuelReady",fuelReady);
        map.put("componentReady",componentReady);
        map.put("obdVin",vin);
        map.put("obdCalid",soft);
        map.put("obdCvn",cvn);
        map.put("iupr",iupr);
        map.put("obdFault",faultNum);
        map.put("obdDataTime",new Date(obdDataTime));

        return map;
    }

    private Map<String, Object> parseFlow(ByteBuf buf) {

        Map map = Maps.newHashMap();

        // 若为有效值则传有效值，如为无效值则传‘null’


        int speed = buf.readUnsignedShort();
        if (0xFFFF != speed) {
//            System.out.println("速度为："+CommonUtil.keepDecimal(speed, 0.00390625, 3)+"原速度"+CommonUtil.keepDecimal(speed, 1/256, 3));
            map.put("gaugesSpeed", CommonUtil.keepDecimal(speed, 0.00390625, 3));
        }else {
            map.put("gaugesSpeed",null);
        }

        int pressure = buf.readUnsignedByte();
        if (0xFFFF != pressure) {
            map.put("barometric", CommonUtil.keepDecimal(pressure, 0.5, 1));
        }else {
            map.put("barometric", null);
        }

        // 扭矩
        int torque = buf.readUnsignedByte();
        if (0xFF != torque) {
            torque = torque - 125;
            map.put("outTorque", torque);
        }else {
            map.put("outTorque", null);
        }

        // 摩擦扭矩
        int frictionTq = buf.readUnsignedByte();
        if (0xFF != frictionTq) {
            frictionTq = frictionTq - 125;
            map.put("frictionTorque", frictionTq);
        }else {
            map.put("frictionTorque", null);
        }

        // 发动机转速
        int rpm = buf.readUnsignedShort();
        if (0xFFFF != rpm) {
            map.put("engineRpm", CommonUtil.keepDecimal(rpm, 0.125, 3));
        }else {
            map.put("engineRpm", null);
        }

        // 燃料流量
        int ff = buf.readUnsignedShort();
        if (0xFFFF != ff) {
            map.put("oilFlowRate", CommonUtil.keepDecimal(ff, 0.05, 2));
        }else {
            map.put("oilFlowRate", null);
        }


        int upNox = buf.readUnsignedShort();
        if (0xFFFF != ff) {
            map.put("scrUpNox", CommonUtil.keepDecimal(upNox, 0.05, 2));
        }else {
            map.put("scrUpNox", null);
        }

            int downNox = buf.readUnsignedShort();
        if (0xFFFF != ff) {
            map.put("scrDownNox", CommonUtil.keepDecimal(downNox, 0.05, 2));
        }else {
            map.put("scrDownNox", null);
        }

        // 反应剂余量
        int reactant = buf.readUnsignedByte();
        if (0xFF != reactant) {
            map.put("reactantRemain", CommonUtil.keepDecimal(reactant, 0.4, 1));
        }else {
            map.put("reactantRemain", null);
        }

        // 进气量
        int inflow = buf.readUnsignedShort();
        if (0xFFFF != inflow) {
            map.put("airflow", CommonUtil.keepDecimal(inflow, 0.05, 2));
        }else {
            map.put("airflow", null);
        }

        //scr 入口温度
        int inTemp = buf.readUnsignedShort();
        if (0xFFFF != inTemp) {
            map.put("scrInTemp", CommonUtil.keepDecimal(inTemp, 0.03125, 5) - 273);
        }else {
            map.put("scrInTemp", null);
        }



        // scr 出口温度
        int outTemp = buf.readUnsignedShort();
        if (0xFFFF != outTemp) {
            map.put("scrOutTemp", CommonUtil.keepDecimal(outTemp, 0.03125, 5) - 273);
        }else {
            map.put("scrOutTemp", null);
        }


        // DFP 压差
        int dfpDif = buf.readUnsignedShort();
        if (0xFFFF != dfpDif) {
            map.put("dpfDiff", CommonUtil.keepDecimal(dfpDif, 0.1, 1));
        }else {
            map.put("dpfDiff", null);
        }

        // 冷却液温度
        int coolantTemp = buf.readUnsignedByte();
        if (0xFF != coolantTemp) {
            map.put("coolantTemp", coolantTemp - 40);
        }else {
            map.put("coolantTemp", null);
        }

        // 油位
        int fuelLevel = buf.readUnsignedByte();
        if (0xFF != fuelLevel) {
            map.put("oilLevel", CommonUtil.keepDecimal(fuelLevel, 0.4, 1));
        }else {
            map.put("oilLevel", null);
        }

        // 定位状态
        int location = buf.readByte();

        //0:有效;1:无效
        int status = location & 0x01;
        int latDir = location & 0x02;
        int lngDir = location & 0x04;

        long lng = buf.readUnsignedInt();
        long lat = buf.readUnsignedInt();



        double lngD = CommonUtil.keepDecimal(lng * (lngDir == 0 ? 1 : -1), 0.00001, 6);
        double latD = CommonUtil.keepDecimal(lat * (latDir == 0 ? 1 : -1), 0.00001, 6);

  /*       double[] latLng = GpsCorrectUtil.transform(latD, lngD);
       double lng1 = latLng[1];
        double lat1 = latLng[0];*/

        int status4Sql = status == 0 ? 1: 0;
        map.put("location", status4Sql);

        if( Math.abs(lngD)<180  && Math.abs(latD) <180 ) {

            map.put("gcj02Lng", lngD);
            map.put("gcj02Lat", latD);
        }else {
            map.put("gcj02Lng", null);
            map.put("gcj02Lat", null);

        }


        // 累计里程
        long mileage = buf.readUnsignedInt();
        if (0xFFFFFFFF != mileage && mileage<999999999 ) {
            map.put("totalMileage", CommonUtil.keepDecimal(mileage, 0.1, 1));
        }else {
            map.put("totalMileage", null);
        }

        return map;
    }

    // 颗粒物排放解析
    private  Map<String, Object> parseGrain(ByteBuf buf){

        Map map = Maps.newHashMap();

        // PM浓度
        int PM_density = buf.readUnsignedShort();
        if (0xFFFF != PM_density) {
            map.put("pmDensity", CommonUtil.keepDecimal(PM_density, 0.001, 3));
        }else{
            map.put("pmDensity", null);
        }

        // PM均值
        int PM_Average = buf.readUnsignedShort();
        if (0xFFFF != PM_Average) {
            map.put("pmDensityAvg", CommonUtil.keepDecimal(PM_Average, 0.001, 3));
        }else{
            map.put("pmDensityAvg", null);
        }
        // 不透光度
        int Opacity = buf.readUnsignedShort();
        if (0xFFFF != Opacity) {
            map.put("pmOpacity", CommonUtil.keepDecimal(Opacity, 0.1, 3));
        }else{
            map.put("pmOpacity", null);
        }





        return map ;
    }


}
