package com.atguigu.gulimall.third.utils;

import java.util.Map;

public class Test {

    public static void main(String[] args) throws Exception {
     // String result = "<CLASS name='com.sgm.dms.de.valueobject.VORepairItemSeting' ><labourCode type=\"String\" isArray=\"false\"><![CDATA[CBY00001  ]]></labourCode><labourName type=\"String\" isArray=\"false\"><![CDATA[基础换油保养]]></labourName><workerType type=\"String\" isArray=\"false\"><![CDATA[机电]]></workerType><stdLabourHour type=\"double\" isArray=\"false\">2.8</stdLabourHour><assignLabourHour type=\"double\" isArray=\"false\">2.8</assignLabourHour><ownedBrand type=\"String\" isArray=\"false\"><![CDATA[CADI]]></ownedBrand><spellCode type=\"String\" isArray=\"false\"><![CDATA[JCHYBY]]></spellCode></CLASS>" ;
      String result = "<CLASS name='com.sgm.dms.de.valueobject.VOStatSeriesLists' ><CLASS name=\"com.sgm.dms.de.valueobject.VOStatSeriesItem\" type=\"com.sgm.dms.de.valueobject.VOStatSeriesItem\" isArray=\"true\" ><CLASS name='com.sgm.dms.de.valueobject.VOStatSeriesItem' ><BRAND_CODE type=\"String\" isArray=\"false\"><![CDATA[BUICK]]></BRAND_CODE><SERIES_CODE type=\"String\" isArray=\"false\"><![CDATA[ENCLAVE]]></SERIES_CODE><STAT_SERIES_CODE type=\"String\" isArray=\"false\"><![CDATA[PKA ENCLAVE 荣御]]></STAT_SERIES_CODE><STAT_SERIES_DESC type=\"String\" isArray=\"false\"><![CDATA[PKA ENCLAVE 荣御]]></STAT_SERIES_DESC><IS_LARGE type=\"int\" isArray=\"false\">1</IS_LARGE><DOWN_STAMP type=\"Timestamp\" isArray=\"false\">1583388548565</DOWN_STAMP></CLASS>";
        Map<String, String> stringStringMap = WXPayUtil.xmlToMap(result);

        System.out.println(stringStringMap);
    }
}
