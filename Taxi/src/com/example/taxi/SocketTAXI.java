package com.example.taxi;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import android.widget.Toast;


import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class SocketTAXI {
	sysDictionary dic;
	sysLog LGWR;
	
	private static final String TAG = "SocketTAXI";
	
	
	public SocketTAXI(sysDictionary dic,sysLog LGWR) {
    	this.dic=dic;
    	this.LGWR=LGWR;

	}
	
	public void ServerPutGPS(String uid,String toServer,int toServerPort, String ALT, String LAT, String LGT, String SPD)
	{
     

		        String strconnect = "imei:"+uid+",tracker,"+dic.getSysdateGps()+",,F,"+ALT+",A,"+LAT+",N,"+LGT+",E,"+SPD+";";
		        //Log.d(TAG, "GPS:"+strconnect);
		        LGWR.logwriter(dic.logsock+"-"+dic.getSysdateLog()+dic.logtype, dic.logpath, dic.getSysdate()+" - "+ TAG + ".." + strconnect);

				try {
		            		            
		            PrintWriter in = null;
		            Socket clientSocketIn = null;

		            clientSocketIn = new Socket(toServer, toServerPort);

		            in = new PrintWriter(clientSocketIn.getOutputStream(), true);
  	                in.println(strconnect);
		            in.close();
		            clientSocketIn.close();
		            

		    }catch (Exception e) {
		    	Log.d(TAG, e.toString());
		    	LGWR.logwriter(dic.logsock+"-"+dic.getSysdateLog()+dic.logtype, dic.logpath, dic.getSysdate()+" - " + TAG + ":ServerPutGPS " + e.toString());
		    }
	}
	
	public void ServerPutCMD(String uid,String toServer,int toServerPort, String CMD)
	{
   		        String strCMD = "imei:"+uid+":"+CMD+"\n";
		        Log.d(TAG, "CMD:"+strCMD);
		        LGWR.logwriter(dic.logsock+"-"+dic.getSysdateLog()+dic.logtype, dic.logpath, dic.getSysdate()+" - "+ TAG + ".." + strCMD);

			try {
		            		            
		            PrintWriter in = null;
		            Socket clientSocketIn = null;

		            clientSocketIn = new Socket(toServer, toServerPort);

		            in = new PrintWriter(clientSocketIn.getOutputStream(), true);
  	                in.println(strCMD);
		            in.close();
		            clientSocketIn.close();
		            

		    }catch (Exception e) {
		    	Log.d(TAG, e.toString());
		    	LGWR.logwriter(dic.logsock+"-"+dic.getSysdateLog()+dic.logtype, dic.logpath, dic.getSysdate()+" - " + TAG + ":ServerPutCMD " + e.toString());
		    }
	}
	
	public List<clsOrders> ServerPutCmdOrders(String uid,String toServer,int toServerPort, String cmdOrders)
	{  List<clsOrders> list = new ArrayList<clsOrders>();
		try
        {
			Socket s = new Socket(toServer, toServerPort);
        	String delims = "[|]";
        	String[] tokens ;

            String str_command = cmdOrders+"\n";
            s.getOutputStream().write(str_command.getBytes());
            String line = null;
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream(), "UTF8"),16384);
            StringBuilder responseData = new StringBuilder();
            while((line = in.readLine()) != null) {
                clsOrders obj = new clsOrders();
            	 tokens = line.split(delims);
            	// Log.d(TAG, "line: "+line);
            	// Log.d(TAG, "tokens.length: "+tokens.length);
            	/*obj.setOrder(tokens[0]);
            	 obj.setId(tokens[1]);
            	 obj.setStatus(tokens[2]);
            	 obj.setOrd_date(tokens[3]);
            	 obj.setOrd_date_beg(tokens[4]);
            	 obj.setOrd_date_out(tokens[5]);
            	 obj.setOrd_date_end(tokens[6]);
            	 obj.setOrd_km(tokens[7]);
            	 obj.setPrice(tokens[8]);
            	 obj.setOrd_from(tokens[9]);
            	 
            	 if (tokens.length<11) {
            	 obj.setOrd_to("");            		 
            	 } else {
            	 obj.setOrd_to(tokens[10]);
            	 }*/
            	
         		switch (tokens.length) {
       	  case 11: 
       	   	obj.setOrder(tokens[0]);
       	   	obj.setId(tokens[1]);
       	   	obj.setStatus(tokens[2]);
       	   	obj.setOrd_date(tokens[3]);
       	   	obj.setOrd_date_beg(tokens[4]);
       	   	obj.setOrd_date_out(tokens[5]);
       	   	obj.setOrd_date_end(tokens[6]);
       	   	obj.setOrd_km(tokens[7]);
       	   	obj.setPrice(tokens[8]);
       	   	obj.setOrd_from(tokens[9]);
       	   	obj.setOrd_to(tokens[10]);
       	   	   break;
       	  case 10:
     	    obj.setOrder(tokens[0]);
     	    obj.setId(tokens[1]);
     	    obj.setStatus(tokens[2]);
     	    obj.setOrd_date(tokens[3]);
     	    obj.setOrd_date_beg(tokens[4]);
     	    obj.setOrd_date_out(tokens[5]);
     	    obj.setOrd_date_end(tokens[6]);
     	    obj.setOrd_km(tokens[7]);
     	    obj.setPrice(tokens[8]);
     	    obj.setOrd_from(tokens[9]);
     	    obj.setOrd_to("");
     	       break;   
       	  case 9:
       		obj.setOrder(tokens[0]);
   	    	obj.setId(tokens[1]);
   	    	obj.setStatus(tokens[2]);
   	    	obj.setOrd_date(tokens[3]);
   	    	obj.setOrd_date_beg(tokens[4]);
   	    	obj.setOrd_date_out(tokens[5]);
   	    	obj.setOrd_date_end(tokens[6]);
   	    	obj.setOrd_km(tokens[7]);
   	    	obj.setPrice(tokens[8]);
   	    	obj.setOrd_from("");
   	    	obj.setOrd_to("");
   	    	   break;    	   
       	    	   
       	    	   
         		}
            	 
            	 //Log.d(TAG, "obj: "+obj.toString());
            	 list.add(obj);
            } 
            
            Log.d(TAG, "������ �� ������� �� ServerPutCmdOrders ��������");
			LGWR.logwriter(dic.logsock+"-"+dic.getSysdateLog()+dic.logtype, dic.logpath, dic.getSysdate()+" - "+ TAG + ".." + cmdOrders);
            
        } catch(Exception e)  {    
        	Log.d(TAG, e.toString());
        	LGWR.logwriter(dic.logsock+"-"+dic.getSysdateLog()+dic.logtype, dic.logpath, dic.getSysdate()+" - "+ TAG + ":ServerPutCmdOrders " + e.toString());
        	}
		
		return list;
  }
	
	public List<clsDriverInfo> ServerPutCmdDriverInfo(String uid,String toServer,int toServerPort, String cmdOrders)
	{  List<clsDriverInfo> list = new ArrayList<clsDriverInfo>();

		try
        {
			Socket s = new Socket(toServer, toServerPort);
        	String delims = "[|]";
        	String[] tokens ;

            String str_command = cmdOrders+"\n";
            s.getOutputStream().write(str_command.getBytes());
               
            String line = null;
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream(), "UTF8"),16384);
            StringBuilder responseData = new StringBuilder();
            while((line = in.readLine()) != null) {
                clsDriverInfo obj = new clsDriverInfo();
            	 tokens = line.split(delims);
            	 //Log.d(TAG, "line: "+line);
            	 obj.setDriver(tokens[0]);
            	 obj.setDriverName(tokens[1]);
            	
            	 //Log.d(TAG, "obj: "+obj.toString());
            	 list.add(obj);
            } 

            Log.d(TAG, "������ �� ������� �� ServerPutCmdDriverInfo ��������");
            LGWR.logwriter(dic.logsock+"-"+dic.getSysdateLog()+dic.logtype, dic.logpath, dic.getSysdate()+" - "+ TAG + ".." + cmdOrders);
            
        } catch(Exception e)  {    
        	Log.d(TAG, e.toString());
        	LGWR.logwriter(dic.logsock+"-"+dic.getSysdateLog()+dic.logtype, dic.logpath, dic.getSysdate()+" - "+ TAG + ":ServerPutCmdDriverInfo " + e.toString());
        	}
		
		return list;
  }
	
	public List<clsCarInfo> ServerPutCmdCarInfo(String uid,String toServer,int toServerPort, String cmdOrders)
	{  List<clsCarInfo> list = new ArrayList<clsCarInfo>();

		try
        {
			Socket s = new Socket(toServer, toServerPort);
        	String delims = "[|]";
        	String[] tokens ;

            String str_command = cmdOrders+"\n";
            s.getOutputStream().write(str_command.getBytes());
               
            String line = null;
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream(), "UTF8"),16384);
            StringBuilder responseData = new StringBuilder();
            while((line = in.readLine()) != null) {
                clsCarInfo obj = new clsCarInfo();
            	 tokens = line.split(delims);
            	 //Log.d(TAG, "line: "+line);
            	 obj.setCar(tokens[0]);
            	 obj.setCarName(tokens[1]);
            	
            	 //Log.d(TAG, "obj: "+obj.toString());
            	 list.add(obj);
            } 

            Log.d(TAG, "������ �� ������� �� ServerPutCmdCarInfo ��������");
            LGWR.logwriter(dic.logsock+"-"+dic.getSysdateLog()+dic.logtype, dic.logpath, dic.getSysdate()+" - "+ TAG + ".." + cmdOrders);
            
        } catch(Exception e)  {    
        	Log.d(TAG, e.toString());
        	LGWR.logwriter(dic.logsock+"-"+dic.getSysdateLog()+dic.logtype, dic.logpath, dic.getSysdate()+" - "+ TAG + ":ServerPutCmdCarInfo " + e.toString());
        	}
		
		return list;
  }	
	

}
