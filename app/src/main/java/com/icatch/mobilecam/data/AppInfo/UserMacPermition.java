package com.icatch.mobilecam.data.AppInfo;

import java.util.HashMap;

/**
 * Created by zhang yanhu C001012 on 2015/11/18 16:22.
 */
public class UserMacPermition {
    private static UserMacPermition userMacPermition;
    private static HashMap<String, MacPermit> macPermitList;

    public UserMacPermition() {
        initUserMacPermition();
    }

    public static UserMacPermition getInstance() {
        if (userMacPermition == null) {
            userMacPermition = new UserMacPermition();
        }
        return userMacPermition;
    }

    public void initUserMacPermition() {
        if (macPermitList == null) {
            macPermitList = new HashMap<String, MacPermit>();
            macPermitList.put("00:08:22:a8:f7:fb", new MacPermit("Asus-me173x", "stanford"));
            macPermitList.put("84:7a:88:44:71:a4", new MacPermit("New HTC One", "Kent"));
            macPermitList.put("ac:f7:f3:cf:24:09", new MacPermit("MI2S", "Chris"));
            macPermitList.put("b0:ec:71:70:d5:c6", new MacPermit("Tab 7.7", "Chris"));
            macPermitList.put("30:85:a9:da:3d:03", new MacPermit("Nexus 7", "Chris"));
            macPermitList.put("38:aa:3c:3f:15:c4", new MacPermit("Samsung S3", "Ethan"));
            macPermitList.put("c4:43:8f:5c:26:1c", new MacPermit("LG Gpro", "Ethan"));
            macPermitList.put("64:b4:73:45:c1:24", new MacPermit("红米note", "SZ  QA"));
            macPermitList.put("cc:3a:61:7f:be:1e", new MacPermit("Samsung S4", "SZ  QA"));
            macPermitList.put("5c:ff:35:8a:fd:62", new MacPermit("Nexus5", "CD"));
            macPermitList.put("00:7c:05:02:f9:ae", new MacPermit("台电P88", "CD"));
            macPermitList.put("cc:fa:00:e9:81:c6", new MacPermit("华硕平板", "CD"));
            macPermitList.put("9c:c1:72:36:fd:30", new MacPermit("华为荣耀3X", "CD"));
            macPermitList.put("a0:93:47:0b:62:09", new MacPermit("OPPO Finder7", "SZ yong.sun"));
            macPermitList.put("18:87:96:8b:e9:a0", new MacPermit("HTC One X", "TW QA"));
            macPermitList.put("30:a8:db:90:16:62", new MacPermit("Sony Z2", "giselle"));
            macPermitList.put("f8:a9:d0:3e:b2:d8", new MacPermit("xx", "giselle"));
            macPermitList.put("68:df:dd:14:da:3c", new MacPermit("红米", "Jimmy"));
            macPermitList.put("b4:ce:f6:9d:c9:3f", new MacPermit("Nexus 9", "TW QA"));
            macPermitList.put("10:bf:48:d0:c9:bf", new MacPermit("ASUS ME301T", "TW QA"));
            macPermitList.put("78:24:af:22:a6:41", new MacPermit("ASUS Zenfone4", "Andy.Li"));
            macPermitList.put("90:b6:86:73:92:46", new MacPermit("Samsung S6", "Stanford"));
            macPermitList.put("88:32:9b:e4:8a:df", new MacPermit("Samsung Note7108", "Yanhu"));

            macPermitList.put("80:01:84:11:b0:05", new MacPermit("***", "weber"));// 20150617

            macPermitList.put("f8:a9:d0:3e:b2:d8", new MacPermit("LG", "TW QA"));
            macPermitList.put("48:5a:3f:23:c1:0c", new MacPermit("Samsung Note3", "TW QA"));
            macPermitList.put("60:21:c0:31:c6:b0", new MacPermit("Samsung S3", "TW QA"));
            macPermitList.put("08:62:66:15:7f:42", new MacPermit("ASUS Zenfone 6", "TW QA"));
            macPermitList.put("80:01:84:13:63:7b", new MacPermit("***", "kent"));

            macPermitList.put("cc:a2:23:fb:23:78", new MacPermit("Huawei", "Ruanxuan"));
            macPermitList.put("90:e7:c4:c1:08:46", new MacPermit("HTC Desire 820", "TW QA"));
            macPermitList.put("c8:a8:23:bf:fe:5f", new MacPermit("Samsung E7", "Jimmy.chiu"));
            macPermitList.put("24:21:ab:81:68:7d", new MacPermit("xxx", "aries"));
            macPermitList.put("00:94:08:00:91:d0", new MacPermit("Vido MINI S", "b.jiang"));

            macPermitList.put("90:e7:c4:16:37:a3", new MacPermit("HTC 826", "Chris Wang"));
            macPermitList.put("34:80:b3:a2:fd:9f", new MacPermit("Mi4", "zhouyongqiang"));
            macPermitList.put("04:02:1f:1f:3e:f7", new MacPermit("huawei", "tiger"));
            macPermitList.put("68:3e:34:69:c0:21", new MacPermit("Meizu MX5", "b.jiang"));
            macPermitList.put("02:00:00:00:00:00", new MacPermit("Android6.0", "b.jiang"));
        }
    }

    public boolean isAllowedMac(String mac) {
//        if (mac == null) {
//            return false;
//        } else {
//            return macPermitList.containsKey(mac);
//        }
          return true;
    }

    public class MacPermit {
        public String device;
        public String person;

        public MacPermit(String device, String person) {
            this.device = device;
            this.person = person;
        }
    }
}
