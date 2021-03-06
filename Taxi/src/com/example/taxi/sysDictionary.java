package com.example.taxi;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.graphics.Color;
import android.telephony.TelephonyManager;

public class sysDictionary {
	
	public String ServerTaxi="90.189.119.84";
	public int ServerTaxiPortGPS=35570;
	public int ServerTaxiPortCMD=35572;
	public String DefaultIMEI="354473050898478";
	public boolean InternalIMEI=false;
	public int orderfreshper=20*1000;
	public int gpsperfresh=10*1000;
	public int gpsidlefresh=600*1000;
	public String OrderBySave="/mnt/sdcard2";
	public boolean AutoAnsIPATS=false;
	public boolean AutoGotoOrders=false;
	public String ServerIPATSIP="90.189.119.84";
	public int ServerIPATSPort=14570;
	public String ServerIPATSLogin="201";
	public String ServerIPATSPassword="q1kdid93";
	public String ServerIPATSname1="��������";
	public String ServerIPATSnumber1="101";
	public String ServerIPATSname2="��������";
	public String ServerIPATSnumber2="102";
	public String ServerIPATSname3="";
	public String ServerIPATSnumber3="";
	public String ServerIPATSname4="";
	public String ServerIPATSnumber4="";
	public String ServerIPATSname5="";
	public String ServerIPATSnumber5="";
	public String ServerIPATSname6="���";
	public String ServerIPATSnumber6="83904122222";
	public String RingFile="Ringln.wav";
	public String OpenStreetMapsUpdate="https://data.gis-lab.info/osm_dump/lates/RU-KK.osm.pbf";
	
	

	public String Uid;
	public int numorder=0;
	public String confpath = "/taxi1/conf/";
	public String logpath = "/taxi1/logs/";
	public String logtype = ".log";
	public String loggps = "taxi-log-gps";
	public String logsock = "taxi-log-sock";
	public String logcmd = "taxi-log-cmd.log";
	public String logcom = "taxi-log-common.log";
	public String logerr = "taxi-log-error.log";
	public int PaleTurquoise=Color.rgb(175, 238, 238);	//#AFEEEE
	public int LightCyan=Color.rgb(224, 255, 255);	//#E0FFFF
	public int Azure=Color.rgb(240, 255, 255);	//#F0FFFF
	public int CadetBlue=Color.rgb(95, 158, 160);	//#5F9EA0
	public int PowderBlue=Color.rgb(176, 224, 230);	//#B0E0E6
	public int LightBlue=Color.rgb(173, 216, 230);	//#ADD8E6
	public int SkyBlue=Color.rgb(135, 206, 235);	//#87CEEB
	public int LightskyBlue=Color.rgb(135, 206, 250);//	#87CEFA
	public int SteelBlue=Color.rgb(70, 130, 180);	//#4682B4
	public int AliceBlue=Color.rgb(240, 248, 255);	//#F0F8FF
	public int SlateGray=Color.rgb(112, 128, 144);	//#708090
	public int LightSlateGray=Color.rgb(119, 136, 153);	//#778899
	public int LightsteelBlue=Color.rgb(176, 196, 222);	//#B0C4DE
	
	public int RowButtonSize = 22;
	public int RowButtonHeight = 60;
	public int RowCarDriverSize = 24;
	public int RowCarDriverHeight = 35;
	public int RowTitleSize = 18;
	public int RowTitleHeight = 25;
	public int RowOrdersSize = 18;
	public int RowOrdersHeight = 70;
	public int RowOrdersButtonSize = 20;
	public int RowOrdersButtonHeight = 80;
	public int msr;
	/*

PaleTurquoise	#AFEEEE
LightCyan	#E0FFFF
Azure	#F0FFFF
CadetBlue	#5F9EA0
PowderBlue	#B0E0E6
LightBlue	#ADD8E6
SkyBlue	#87CEEB
LightskyBlue	#87CEFA
SteelBlue	#4682B4
AliceBlue	#F0F8FF
SlateGray	#708090
LightSlateGray	#778899
LightsteelBlue	#B0C4DE
CornflowerBlue	#6495ED
	 */
	
	public void setUid(String vl) {
		Uid=vl;
	}
	public String getUid() {
		return Uid;
	}

	public void setNumOrder(int vl) {
		numorder=vl;
	}
	public int getNumOrder() {
		return numorder;
	}
	
	public void setServerTaxi(String vl) {
		ServerTaxi=vl;
	}
	public String getServerTaxi() {
		return ServerTaxi;
	}
	
	public void setServerTaxiPortGPS(int vl) {
		ServerTaxiPortGPS=vl;
	}
	public int getServerTaxiPortGPS() {
		return ServerTaxiPortGPS;
	}
	
	public void setServerTaxiPortCMD(int vl) {
		ServerTaxiPortCMD=vl;
	}
	public int getServerTaxiPortCMD() {
		return ServerTaxiPortCMD;
	}
	
	public void setDefaultIMEI(String vl) {
		DefaultIMEI=vl;
	}
	public String getDefaultIMEI() {
		return DefaultIMEI;
	}
	
	public void setInternalIMEI(boolean vl) {
		InternalIMEI=vl;
	}
	public boolean getInternalIMEI() {
		return InternalIMEI;
	}
	
	public void setOrderFreshPer(int vl) {
		orderfreshper=vl;
	}
	public int getOrderFreshPer() {
		return orderfreshper;
	}

	public void setGpsPerFresh(int vl) {
		gpsperfresh=vl;
	}
	public int getGpsPerFresh() {
		return gpsperfresh;
	}
	
	public void setGpsIdleFresh(int vl) {
		gpsidlefresh=vl;
	}
	public int getGpsIdleFresh() {
		return gpsidlefresh;
	}
	
	public void setOrderBySave(String vl) {
		OrderBySave=vl;
	}
	public String getOrderBySave() {
		return OrderBySave;
	}
	
	public void setAutoAnsIPATS(boolean vl) {
		AutoAnsIPATS=vl;
	}
	public boolean getAutoAnsIPATS() {
		return AutoAnsIPATS;
	}

	public void setAutoGotoOrders(boolean vl) {
		AutoGotoOrders=vl;
	}
	public boolean getAutoGotoOrders() {
		return AutoGotoOrders;
	}
	
	public void setServerIPATSIP(String vl) {
		ServerIPATSIP=vl;
	}
	public String getServerIPATSIP() {
		return ServerIPATSIP;
	}
	
	public void setServerIPATSPort(int vl) {
		ServerIPATSPort=vl;
	}
	public int getServerIPATSPort() {
		return ServerIPATSPort;
	}

	public void setServerIPATSLogin(String vl) {
		ServerIPATSLogin=vl;
	}
	public String getServerIPATSLogin() {
		return ServerIPATSLogin;
	}

	public void setServerIPATSPassword(String vl) {
		ServerIPATSPassword=vl;
	}
	public String getServerIPATSPassword() {
		return ServerIPATSPassword;
	}

	public void setServerIPATSname1(String vl) {
		ServerIPATSname1=vl;
	}
	public String getServerIPATSname1() {
		return ServerIPATSname1;
	}

	public void setServerIPATSname2(String vl) {
		ServerIPATSname2=vl;
	}
	public String getServerIPATSname2() {
		return ServerIPATSname2;
	}
	
	public void setServerIPATSname3(String vl) {
		ServerIPATSname3=vl;
	}
	public String getServerIPATSname3() {
		return ServerIPATSname3;
	}
	
	public void setServerIPATSname4(String vl) {
		ServerIPATSname4=vl;
	}
	public String getServerIPATSname4() {
		return ServerIPATSname4;
	}
	
	public void setServerIPATSname5(String vl) {
		ServerIPATSname5=vl;
	}
	public String getServerIPATSname5() {
		return ServerIPATSname5;
	}
	
	public void setServerIPATSname6(String vl) {
		ServerIPATSname6=vl;
	}
	public String getServerIPATSname6() {
		return ServerIPATSname6;
	}
	
	public void setServerIPATSnumber1(String vl) {
		ServerIPATSnumber1=vl;
	}
	public String getServerIPATSnumber1() {
		return ServerIPATSnumber1;
	}
	
	public void setServerIPATSnumber2(String vl) {
		ServerIPATSnumber2=vl;
	}
	public String getServerIPATSnumber2() {
		return ServerIPATSnumber2;
	}
	
	public void setServerIPATSnumber3(String vl) {
		ServerIPATSnumber3=vl;
	}
	public String getServerIPATSnumber3() {
		return ServerIPATSnumber3;
	}
	
	public void setServerIPATSnumber4(String vl) {
		ServerIPATSnumber4=vl;
	}
	public String getServerIPATSnumber4() {
		return ServerIPATSnumber4;
	}
	
	public void setServerIPATSnumber5(String vl) {
		ServerIPATSnumber5=vl;
	}
	public String getServerIPATSnumber5() {
		return ServerIPATSnumber5;
	}
	
	public void setServerIPATSnumber6(String vl) {
		ServerIPATSnumber6=vl;
	}
	public String getServerIPATSnumber6() {
		return ServerIPATSnumber6;
	}

	public void setRingFile(String vl) {
		RingFile=vl;
	}
	public String getRingFile() {
		return RingFile;
	}
	
	public void setOpenStreetMapsUpdate(String vl) {
		OpenStreetMapsUpdate=vl;
	}
	public String getOpenStreetMapsUpdate() {
		return OpenStreetMapsUpdate;
	}
	
	public void setMsr(int vl) {
		msr=vl;
		switch (vl) {
		case 480:
			RowButtonSize = 9;
			RowButtonHeight = 40;
			RowCarDriverSize = 9;
			RowCarDriverHeight = 25;
			RowTitleSize = 9;
			RowTitleHeight = 25;
			RowOrdersSize = 9;
			RowOrdersHeight = 40;
			RowOrdersButtonSize = 9;
			RowOrdersButtonHeight = 60;
		break;
		case 540:
			RowButtonSize = 9;
			RowButtonHeight = 40;
			RowCarDriverSize = 9;
			RowCarDriverHeight = 25;
			RowTitleSize = 9;
			RowTitleHeight = 35;
			RowOrdersSize = 9;
			RowOrdersHeight = 50;
			RowOrdersButtonSize = 9;
			RowOrdersButtonHeight = 75;
		break;
		case 600:
			RowButtonSize = 18;
			RowButtonHeight = 60;
			RowCarDriverSize = 18;
			RowCarDriverHeight = 35;
			RowTitleSize = 14;
			RowTitleHeight = 25;
			RowOrdersSize = 14;
			RowOrdersHeight = 55;
			RowOrdersButtonSize = 16;
			RowOrdersButtonHeight = 100;
		break;
		case 800:
			RowButtonSize = 10;
			RowButtonHeight = 40;
			RowCarDriverSize = 10;
			RowCarDriverHeight = 25;
			RowTitleSize = 10;
			RowTitleHeight = 25;
			RowOrdersSize = 10;
			RowOrdersHeight = 40;
			RowOrdersButtonSize = 10;
			RowOrdersButtonHeight = 75;
		break;
		case 960:
			RowButtonSize = 12;
			RowButtonHeight = 50;
			RowCarDriverSize = 11;
			RowCarDriverHeight = 25;
			RowTitleSize = 11;
			RowTitleHeight = 25;
			RowOrdersSize = 11;
			RowOrdersHeight = 40;
			RowOrdersButtonSize = 12;
			RowOrdersButtonHeight = 75;
		break;
		default:
			RowButtonSize = 22;
			RowButtonHeight = 60;
			RowCarDriverSize = 24;
			RowCarDriverHeight = 35;
			RowTitleSize = 18;
			RowTitleHeight = 25;
			RowOrdersSize = 18;
			RowOrdersHeight = 70;
			RowOrdersButtonSize = 20;
			RowOrdersButtonHeight = 80;
		
		}
	}
	public int getMsr() {
		return msr;
	}
	
	public String getSysdate() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy ");
        String strTime = simpleDateFormat.format(new Date());
        return strTime;
	}
	public String getSysdateGps() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMddHHmm");
        String strTime = simpleDateFormat.format(new Date());
        return strTime;
	}
	
	public String getSysdateLog() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        String strTime = simpleDateFormat.format(new Date());
        return strTime;
	}
	

	
}
