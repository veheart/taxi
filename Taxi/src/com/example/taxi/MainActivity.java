package com.example.taxi;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.example.taxi.R.color;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;



public class MainActivity extends Activity  /*implements LocationListener*/ implements android.view.View.OnClickListener {
	private static final String TAG = "MainActivity";
	public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
	private static final String MY_SETTINGS = "app_settings";
	public static Context appcontext;
	final int DIALOG_TIME = 1;
	final int DIALOG_REFRESH = 2;
	final int DIALOG_CALL = 3;
	final int DIALOG_MAP = 4;
	final int DIALOG_EXIT = 5;
	final int DIALOG_FREE = 6;
	final int DIALOG_TAKE = 7;
	final int DIALOG_INSTALL_TIME = 8;
	final int DIALOG_INSTALL_TIME_OUT = 9;
	final int DIALOG_INSTALL_TIME_IN = 10;
	final int DIALOG_TAXI = 11;
	final int DIALOG_KM_BEG = 12;
	final int DIALOG_KM_END = 13;
	final int DIALOG_KM = 14;
	
	public String ServerTaxi;
	public int ServerTaxiPortGPS;
	public int ServerTaxiPortCMD;
	public int Measuredwidth = 0;  
	public int Measuredheight = 0; 
		
	AlertDialog.Builder ad;
	Context context;
	
	Button btnGPS;
	Button btnTaxiCmd;
	TextView rsltTXT;
	EditText editGPSServer;
	EditText editGPSPort;
	EditText editTaxiServer;
	EditText editTaxiPort;
	EditText editTaxiCmd;
	public static String LGT="0000.0000";
	public static String LAT="0000.0000";
	public static String ALT="0000.0000";
	public LocationManager locationManager;
	public LocationListener mLocationListener;	
    public sysDictionary dic ;
	public sysLog LGWR;
	public SocketTAXI mSocket;
	public sysConfig sysConf;
	public sysPrefs sysPref;
	public TableLayout table;
	public static List<clsOrders> list;
    public static List<clsDriverInfo> driver;
    public static List<clsCarInfo> car;
    public static String Sysdate;
    public String strcar = null;
    public String strdriver = null;
    public static Timer myTimer;
    public static Timer myTimer2;
    public static Timer myTimer3;
    public static int flg_interfacecrt=0;
    public static int flg_refreshdata=0;
    public static int flg_refreshclock=0;
    public static int flg_numorder=0;
    public static int flg_threads=0;
    public static Date flg_gps_date_beg;
    public static Date flg_gps_date_end;
    public static int flg_icon=0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		if (this.flg_icon<1) { 
			this.flg_icon=1;
			//addshortcut();
		}
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.setTitle("����� �1");
		this.flg_refreshdata=0;
				
		super.onCreate(savedInstanceState);
        setContentView(com.example.taxi.R.layout.taxi);
        SharedPreferences sp = getSharedPreferences(MY_SETTINGS, Context.MODE_PRIVATE);
        // ���������, ������ �� ��� ����������� ���������
        boolean hasVisited = sp.getBoolean("hasVisited", false);
		
        if (!hasVisited) {
            // ������� ������ ����������
        	addshortcut();
            Editor e = sp.edit();
            e.putBoolean("hasVisited", true);
            e.commit(); // �� �������� ����������� ���������
        }
        this.appcontext =  getApplicationContext();
        //Context context = getApplicationContext();
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context); 
        //Toast.makeText(this, prefs.getString("ServerTaxi", "90.189.119.84"), Toast.LENGTH_LONG).show();
        
		try {
			this.setTitle("����� �1");
		 TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		 dic = new sysDictionary();
		 mSocket = new SocketTAXI(dic, LGWR);
		 //sysConf = new sysConfig(dic);	
		 //sysConf.writeConfig();
		 sysPref = new sysPrefs(dic);
		 this.dic = sysPref.getConfig();
		 sysPref.putConfig();
		 
		 LGWR = new sysLog();
		 LGWR.logwriter(dic.logcom, dic.logpath, dic.getSysdate()+" - Starting program Taxi1...");
		 LGWR.logwriter(dic.logcom, dic.logpath, dic.getSysdate()+" - android.os.Build.VERSION.SDK_INT:"+android.os.Build.VERSION.SDK_INT);
		try {
		 //dic.setUid(tm.getDeviceId());
		} catch (Exception e) {
			dic.setUid(dic.getDefaultIMEI());
	    } 
		dic.setUid(dic.getDefaultIMEI());
		


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocationListener = new GPSLocationListener(dic, LGWR, mSocket); 
  
        //setContentView(R.layout.activity_main);

        sysThreads dataThready = new sysThreads(this,dic,LGWR,mSocket,"refreshdata","");
        dataThready.start();
        
        
        LGWR.logwriter(dic.logcom, dic.logpath, dic.getSysdate()+" - "+ TAG + ".." +"initialized thread DATA:"+dataThready.getId());

        //sysThreads clockThready = new sysThreads(this,dic,LGWR,mSocket,"refreshclock");
        //clockThready.start();
        
        /*
			do {
				try{
	                Thread.sleep(1000);		
	            }catch(InterruptedException e){}
			} while(this.flg_refreshdata<1);
			*/
        
        //cmdOrderlist();
        if (MainActivity.flg_interfacecrt<1) {
            cmdOrderheadNull();
            cmdOrderlistNull();
        } else {
        	cmdOrderhead();
        	cmdOrderlist();
        }

        

    			 /*TableLayout tablehead = (TableLayout)findViewById(com.example.taxi.R.id.TaxiHeadLayout);
    			 Button btn = (Button) tablehead.findViewById(1060000000);
    			 btn.setText(dic.getSysdate()); */
    			 

        		 myTimer = new Timer(); // ������� ������
    			 final Handler uiHandler = new Handler();
    			 myTimer.purge();
    			 myTimer.schedule(new TimerTask() { // ���������� ������
    			     @Override
    			     public void run() {
    			         final String result = dic.getSysdate();
    			         uiHandler.post(new Runnable() {
    			             @Override
    			             public void run() {
    			            	 //TableLayout tablehead = (TableLayout)findViewById(com.example.taxi.R.id.TaxiHeadLayout);
    			            	 //Button txtResult = (Button) tablehead.findViewById(1060000000);
    			                 //txtResult.setText(result);
    			            	 
    			            	 TimeTask tmTask = new TimeTask(); 
    			            	 tmTask.execute();
    			             }
    			         });
    			     };
    			 }, 0L, 1L * 1000);

       
        		 myTimer2 = new Timer();   			   
    			 final Handler uiHandler2 = new Handler();
    			 myTimer2.purge();
    			 myTimer2.schedule(new TimerTask() { 
    			     @Override
    			     public void run() {
    			         uiHandler2.post(new Runnable() {
    			             @Override
    			             public void run() {
    			             	OrderTask ordTask = new OrderTask(); 
    			            	ordTask.execute();
    			             }
    			         });
    			     };
    			 }, 1L * dic.getOrderFreshPer(), 1L * dic.getOrderFreshPer());
    			 
          
        
         myTimer3 = new Timer();	   
		 final Handler uiHandler3 = new Handler();
		 myTimer3.purge();
		 myTimer3.schedule(new TimerTask() { 
		     @Override
		     public void run() {
		         uiHandler3.post(new Runnable() {
		             @Override
		             public void run() {
			            	 OrderFillTask ordFillTask = new OrderFillTask(); 
		            	 ordFillTask.execute();
		             }
		         });
		     };
		 }, 0L, 1L * 1000);
		 
       // } 
    			 /*new Thread(new Runnable() {
    			        public void run() {
    			        	TableLayout tablehead = (TableLayout)findViewById(com.example.taxi.R.id.TaxiHeadLayout);
        			        final Button txtResult = (Button) tablehead.findViewById(1060000000);
    			        	txtResult.post(new Runnable() {
    			                public void run() {
    			                	
    			                	do {
    			        				try{
    			        					txtResult.setText(dic.getSysdate());
    			        	                Thread.sleep(1000);		
    			        	            }catch(InterruptedException e){}
    			        			} while(true);
    			                }
    			            });
    			        }
    			    }).start();*/
    			 
    			 flg_gps_date_beg=new Date();
    			 flg_gps_date_end=new Date();


         LGWR.logwriter(dic.logcom, dic.logpath, dic.getSysdate()+" - Interface loaded!");
         
		}catch (Exception e) {
		 LGWR.logwriter(dic.logcom, dic.logpath, dic.getSysdate()+" - "+ TAG + ":onCreate " + e.toString());

		}
	}
    @Override
    protected void onResume() {
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
		 sysPref = new sysPrefs(dic);
		 this.dic = sysPref.getConfig();
		 sysPref.putConfig();
        
        // ��� ������������� ����� ���� GSM
        // locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,
        //        mLocationListener);
        
        super.onResume();
    }

    @Override
    protected void onPause() {
     locationManager.removeUpdates(mLocationListener);
     super.onPause();
    }
    
    @Override
    protected void onStart() {
   
        
     super.onStart();
    }
    
    @Override
    protected void onDestroy() {
    //final int pid = android.os.Process.myPid();
    //android.os.Process.killProcess(pid);
    	myTimer.cancel();
    	myTimer2.cancel();
    	myTimer3.cancel();
    	myTimer.purge();
    	myTimer2.purge();
    	myTimer3.purge();
    super.onDestroy();
    } 
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(com.example.taxi.R.menu.main, menu);
	      menu.add("� ���������");
	      menu.add("���������");
	      menu.add("�������");
	      menu.add("�����");
	      
	      return super.onCreateOptionsMenu(menu);
		//return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		int s=0;
      //Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
      if (item.getTitle()=="� ���������") {s=1;}
      if (item.getTitle()=="���������") {s=2;}
      if (item.getTitle()=="�������") {s=3;}
      if (item.getTitle()=="�����") {s=4;}

		switch (s) {
	     case 1: 
	    	 Toast.makeText(this, "����� �1\n1.0\n\n- ���������� �������� �����\n- �����\n- IP-���������\n\nCopyright � 2013 Kargin Alexandr. All rights reserved.", Toast.LENGTH_LONG).show();
	    	   break;
	     case 2: 
	 		/*this.requestWindowFeature(Window.FEATURE_NO_TITLE);
			this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
			this.setTitle("����� �1");
			
			TableLayout params = (TableLayout)findViewById(com.example.taxi.R.layout.params);
	        setContentView(params);*/
	    	    Intent intent = new Intent(this, SettingsActivity.class);
	    	    //EditText editText = (EditText) findViewById(R.id.editGPSPort);
	    	    //String message = editText.getText().toString();
	    	    //intent.putExtra(EXTRA_MESSAGE, message);
	    	    startActivity(intent);
	        
		       break;
	     case 3: 
	    	 iaxConnection ic = new iaxConnection();
		       ic.connect();
		       try {
		           Thread.sleep(10000);
		         } catch (InterruptedException ie) {
		           ie.printStackTrace();
		         }
		       
		       ic.call("999");
		       break;
	     case 4: 
	    	 
		       break;
		       
		}
      return super.onOptionsItemSelected(item);
    }
	
	public void onClickGPS(View v) {
		//cmdOrderlist();
     
		        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMddHHmm");
		        String strTime = simpleDateFormat.format(new Date());
		        String testconnect = "imei:"+dic.getUid()+",tracker,"+strTime+",,F,"+ALT+",A,"+LAT+",N,"+LGT+",E,0;";
		        rsltTXT.setText(rsltTXT.getText().toString().trim()+"\n"+testconnect);
				try {
		            		            
		            /*PrintWriter in = null;
		            Socket clientSocketIn = null;
		            
		            String toServer = editGPSServer.getText().toString().trim();
                    int toServerPort = Integer.parseInt(editGPSPort.getText().toString().trim());
		            clientSocketIn = new Socket(toServer, toServerPort);

		            in = new PrintWriter(clientSocketIn.getOutputStream(), true);
  	                in.println(testconnect);
		            in.close();
		            clientSocketIn.close();*/
					
					//SocketTAXI mSocket = new SocketTAXI();
					//mSocket.ServerPutGPS(uid,editGPSServer.getText().toString().trim(),Integer.parseInt(editGPSPort.getText().toString().trim()),ALT,LAT,LGT);
					
					//subTable();
		    }catch (Exception e) {
		    	rsltTXT.setText(rsltTXT.getText().toString().trim()+"\n"+e.toString());
		    	LGWR.logwriter(dic.logcom, dic.logpath, dic.getSysdate()+" - "+ TAG + ":onClickGPS " + e.toString());

		    }


	  }
	public void onClickTaxi(View v) {
		rsltTXT.setText("������������ �������"+"\n");		
		 try
	        {

			    String str_address = editTaxiServer.getText().toString().trim();
	            int str_port = Integer.parseInt(editTaxiPort.getText().toString().trim());
	            String str_command = editTaxiCmd.getText().toString().trim();
	            /*rsltTXT.setText(rsltTXT.getText().toString().trim()+"\n"+str_command);

	            Socket s = new Socket(str_address, str_port);

	            str_command = str_command+"\n";//+s.getInetAddress().getHostAddress()+":"+s.getLocalPort();
	            s.getOutputStream().write(str_command.getBytes());
          
	            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream(), "UTF8"),16384);
	            String line = null;
	            StringBuilder responseData = new StringBuilder();
	            while((line = in.readLine()) != null) {
	            	//System.out.println( line);
		            rsltTXT.setText(rsltTXT.getText().toString().trim()+"\n"+ line);
	            } */
	            rsltTXT.setText("");
	            //SocketTAXI mSocket = new SocketTAXI();
	            List<clsOrders> list = mSocket.ServerPutCmdOrders(dic.getUid(),str_address, str_port,str_command);
	            //String list = mSocket.ServerPutCmdOrders(this.uid,str_address, str_port,str_command);
				TableLayout table = new TableLayout(this);
				addHead(table);
				addRowTitle(table);
	 		    for(clsOrders tmp : list) {
				//	 System.out.println(tmp.toString());
					addRowOrders(table, tmp.getStatus()+" "+tmp.getId(),tmp.getOrd_date(),tmp.getOrd_from(),tmp.getOrd_to(),tmp.getPrice(),tmp.getOrd_date_beg(), tmp.getOrd_date_out(), tmp.getOrd_date_end(), tmp.getOrd_km(), tmp.getStatus(), Integer.parseInt(tmp.getId()));
					 rsltTXT.setText(rsltTXT.getText().toString().trim()+"\n"+tmp.getId()+"  "
				+tmp.getStatus()+"  "+tmp.getOrd_date()+"  "+tmp.getOrd_from()+"  "+tmp.getPrice());
					 //Toast.makeText(this, rsltTXT.getText().toString().trim(), Toast.LENGTH_LONG).show();

					 }
		        setContentView(table);

	            
	            
	        }
	        catch(Exception e)
	        {
	        	//System.out.println("init error: "+e);
	        	// Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
	        	rsltTXT.setText(rsltTXT.getText().toString().trim()+"\n"+e.toString());
	        	LGWR.logwriter(dic.logcom, dic.logpath, dic.getSysdate()+" - "+ TAG + ":onClickTaxi " + e.toString());
	        	}
	  }

	public void rsltLOG(String valstr) {
		rsltTXT.setText(valstr);
	}
	
	/*public void subTable()
	{
		TableLayout table = new TableLayout(this);

        table.setStretchAllColumns(true);
        table.setShrinkAllColumns(true);

        TableRow rowTitle = new TableRow(this);
        rowTitle.setGravity(Gravity.CENTER_HORIZONTAL);

        TableRow rowDayLabels = new TableRow(this);
        TableRow rowHighs = new TableRow(this);
        TableRow rowLows = new TableRow(this);
        TableRow rowConditions = new TableRow(this);
        rowConditions.setGravity(Gravity.CENTER);

        TextView empty = new TextView(this);

        // title column/row
        TextView title = new TextView(this);
        title.setText("����� �1");

        title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        title.setGravity(Gravity.LEFT);
        title.setTypeface(Typeface.SERIF, Typeface.BOLD);
        title.setBackgroundColor(Color.GRAY);
        title.setTextColor(Color.WHITE);
        //title.setCompoundDrawables(1, 1, 1, 1);

        TableRow.LayoutParams params = new TableRow.LayoutParams();
        params.span = 6;


        rowTitle.addView(title, params);

        // labels column
        TextView highsLabel = new TextView(this);
        highsLabel.setText("11:22:33");
        highsLabel.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        highsLabel.setTypeface(Typeface.DEFAULT_BOLD);

        TextView lowsLabel = new TextView(this);
        lowsLabel.setText("Day Low");
        lowsLabel.setTypeface(Typeface.DEFAULT_BOLD);

        TextView conditionsLabel = new TextView(this);
        conditionsLabel.setText("Conditions");
        conditionsLabel.setTypeface(Typeface.DEFAULT_BOLD);

        rowDayLabels.addView(empty);
        rowHighs.addView(highsLabel);
        rowLows.addView(lowsLabel);
        rowConditions.addView(conditionsLabel);

        // day 1 column
        TextView day1Label = new TextView(this);
        day1Label.setText("Feb 7");
        day1Label.setTypeface(Typeface.SERIF, Typeface.BOLD);

        TextView day1High = new TextView(this);
        day1High.setText("28�F");
        day1High.setGravity(Gravity.CENTER_HORIZONTAL);

        TextView day1Low = new TextView(this);
        day1Low.setText("15�F");
        day1Low.setGravity(Gravity.CENTER_HORIZONTAL);

        ImageView day1Conditions = new ImageView(this);
        day1Conditions.setImageResource(R.drawable.ic_launcher);

        rowDayLabels.addView(day1Label);
        rowHighs.addView(day1High);
        rowLows.addView(day1Low);
        rowConditions.addView(day1Conditions);

        // day2 column
        TextView day2Label = new TextView(this);
        day2Label.setText("Feb 8");
        day2Label.setTypeface(Typeface.SERIF, Typeface.BOLD);

        TextView day2High = new TextView(this);
        day2High.setText("26�F");
        day2High.setGravity(Gravity.CENTER_HORIZONTAL);

        TextView day2Low = new TextView(this);
        day2Low.setText("14�F");
        day2Low.setGravity(Gravity.CENTER_HORIZONTAL);

        ImageView day2Conditions = new ImageView(this);
        day2Conditions.setImageResource(R.drawable.ic_launcher);

        rowDayLabels.addView(day2Label);
        rowHighs.addView(day2High);
        rowLows.addView(day2Low);
        rowConditions.addView(day2Conditions);

        // day3 column
        TextView day3Label = new TextView(this);
        day3Label.setText("Feb 9");
        day3Label.setTypeface(Typeface.SERIF, Typeface.BOLD);

        TextView day3High = new TextView(this);
        day3High.setText("23�F");
        day3High.setGravity(Gravity.CENTER_HORIZONTAL);

        TextView day3Low = new TextView(this);
        day3Low.setText("3�F");
        day3Low.setGravity(Gravity.CENTER_HORIZONTAL);

        ImageView day3Conditions = new ImageView(this);
        day3Conditions.setImageResource(R.drawable.ic_launcher);

        rowDayLabels.addView(day3Label);
        rowHighs.addView(day3High);
        rowLows.addView(day3Low);
        rowConditions.addView(day3Conditions);

        // day4 column
        TextView day4Label = new TextView(this);
        day4Label.setText("Feb 10");
        day4Label.setTypeface(Typeface.SERIF, Typeface.BOLD);

        TextView day4High = new TextView(this);
        day4High.setText("17�F");
        day4High.setGravity(Gravity.CENTER_HORIZONTAL);

        TextView day4Low = new TextView(this);
        day4Low.setText("5�F");
        day4Low.setGravity(Gravity.CENTER_HORIZONTAL);

        ImageView day4Conditions = new ImageView(this);
        day4Conditions.setImageResource(R.drawable.ic_launcher);

        rowDayLabels.addView(day4Label);
        rowHighs.addView(day4High);
        rowLows.addView(day4Low);
        rowConditions.addView(day4Conditions);

        // day5 column
        TextView day5Label = new TextView(this);
        day5Label.setText("Feb 11");
        day5Label.setTypeface(Typeface.SERIF, Typeface.BOLD);

        TextView day5High = new TextView(this);
        day5High.setText("19�F");
        day5High.setGravity(Gravity.CENTER_HORIZONTAL);

        TextView day5Low = new TextView(this);
        day5Low.setText("6�F");
        day5Low.setGravity(Gravity.CENTER_HORIZONTAL);

        ImageView day5Conditions = new ImageView(this);
        day5Conditions.setImageResource(R.drawable.ic_launcher);

        rowDayLabels.addView(day5Label);
        rowHighs.addView(day5High);
        rowLows.addView(day5Low);
        rowConditions.addView(day5Conditions);

        table.addView(rowTitle);
        table.addView(rowDayLabels);
        table.addView(rowHighs);
        table.addView(rowLows);
        table.addView(rowConditions);

        setContentView(table);
	}*/
	

	public void cmdOrderhead() {
		 try
	        {
		        Point size = new Point();
		        WindowManager w = getWindowManager();
		              int stat=0;
		              String statstr="";
		              Display d = w.getDefaultDisplay(); 
		              Measuredwidth = d.getWidth(); 
		              Measuredheight = d.getHeight(); 
		              dic.setMsr(Measuredwidth);
		              //Toast.makeText(this, ""+Measuredwidth , Toast.LENGTH_LONG).show();
		              
        
				TableLayout tablehead = (TableLayout)findViewById(com.example.taxi.R.id.TaxiHeadLayout);
		        tablehead.setStretchAllColumns(true);
		        tablehead.setShrinkAllColumns(true);
		        
				addHead(tablehead);
				addRowButton(tablehead);
				    for(clsCarInfo tmp : this.car) {
				    	strcar=tmp.getCarName();
					 }
				    for(clsDriverInfo tmp : this.driver) {
				    	strdriver=tmp.getDriverName();
					 }
				addRowCarDriver(tablehead,strcar,strdriver);
				addRowTitle(tablehead);

	        
				
	        }
	        catch(Exception e)
	        {
	        	 Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
	        	 LGWR.logwriter(dic.logcom, dic.logpath, dic.getSysdate()+" - "+ TAG + ":cmdOrderlist " + e.toString());
	        	}
	}
	
	public void cmdOrderheadNull() {
		 try
	        {
		        Point size = new Point();
		        WindowManager w = getWindowManager();
		              int stat=0;
		              String statstr="";
		              Display d = w.getDefaultDisplay(); 
		              Measuredwidth = d.getWidth(); 
		              Measuredheight = d.getHeight(); 
		              dic.setMsr(Measuredwidth);
		              //Toast.makeText(this, ""+Measuredwidth , Toast.LENGTH_LONG).show();
		              
       
				TableLayout tablehead = (TableLayout)findViewById(com.example.taxi.R.id.TaxiHeadLayout);
		        tablehead.setStretchAllColumns(true);
		        tablehead.setShrinkAllColumns(true);
		        
				addHead(tablehead);
				addRowButton(tablehead);
				addRowCarDriver(tablehead,"---","---");
				addRowTitle(tablehead);

	        
				
	        }
	        catch(Exception e)
	        {
	        	 Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
	        	 LGWR.logwriter(dic.logcom, dic.logpath, dic.getSysdate()+" - "+ TAG + ":cmdOrderlist " + e.toString());
	        	}
	}
	
	public void cmdOrderheadUpd() {
		 try
	        {
		        Point size = new Point();
		        WindowManager w = getWindowManager();
		              int stat=0;
		              String statstr="";
		              Display d = w.getDefaultDisplay(); 
		              Measuredwidth = d.getWidth(); 
		              Measuredheight = d.getHeight(); 
		              dic.setMsr(Measuredwidth);
		              //Toast.makeText(this, ""+Measuredwidth , Toast.LENGTH_LONG).show();
		              
       
				TableLayout tablehead = (TableLayout)findViewById(com.example.taxi.R.id.TaxiHeadLayout);
		        tablehead.setStretchAllColumns(true);
		        tablehead.setShrinkAllColumns(true);
		        tablehead.removeView(findViewById(11111111));
		        tablehead.removeView(findViewById(11111112));
				
				    for(clsCarInfo tmp : this.car) {
				    	strcar=tmp.getCarName();
					 }
				    for(clsDriverInfo tmp : this.driver) {
				    	strdriver=tmp.getDriverName();
					 }
				addRowCarDriver(tablehead,strcar,strdriver);
				addRowTitle(tablehead);

	        
				
	        }
	        catch(Exception e)
	        {
	        	 Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
	        	 LGWR.logwriter(dic.logcom, dic.logpath, dic.getSysdate()+" - "+ TAG + ":cmdOrderlist " + e.toString());
	        	}
	}
	
	public void cmdOrderlist() {
		 try
	        {
		        Point size = new Point();
		        WindowManager w = getWindowManager();
		              int stat=0;
		              String statstr="";
		              Display d = w.getDefaultDisplay(); 
		              Measuredwidth = d.getWidth(); 
		              Measuredheight = d.getHeight(); 
		              dic.setMsr(Measuredwidth);
		              //Toast.makeText(this, ""+Measuredwidth , Toast.LENGTH_LONG).show();
		              
	            //SocketTAXI mSocket = new SocketTAXI(dic, LGWR);
	            //List<clsOrders> list = mSocket.ServerPutCmdOrders(dic.getUid(),ServerTaxi, ServerTaxiPortCMD,"imei:"+dic.getUid()+":orders_list,quit;");
	            //List<clsDriverInfo> driver = mSocket.ServerPutCmdDriverInfo(dic.getUid(),ServerTaxi, ServerTaxiPortCMD,"imei:"+dic.getUid()+":driver_info,quit;");
	            //List<clsCarInfo> car = mSocket.ServerPutCmdCarInfo(dic.getUid(),ServerTaxi, ServerTaxiPortCMD,"imei:"+dic.getUid()+":car_info,quit;");
	            
				//TableLayout table = new TableLayout(this);
		         
		        TableLayout table = (TableLayout)findViewById(com.example.taxi.R.id.TaxiLayout);

		        
				if (Measuredwidth==1024 || Measuredwidth==800 || Measuredwidth==960) {
					table.setBackgroundResource(com.example.taxi.R.drawable.map1024552);
					
				} else {
					table.setBackgroundResource(com.example.taxi.R.drawable.map600976);
					
				}
					
	
		        table.setStretchAllColumns(true);
		        table.setShrinkAllColumns(true);
				addHead(table);
				//addRowButton(table);
	 		    //for(clsCarInfo tmp : this.car) {
	 		    //	strcar=tmp.getCarName();
				//	 }
	 		    //for(clsDriverInfo tmp : this.driver) {
	 		    //	strdriver=tmp.getDriverName();
				//	 }
	 		    //addRowCarDriver(table,strcar,strdriver);
				//addRowTitle(table);
	 		    for(clsOrders tmp : this.list) {

	 		    	if (tmp.getStatus().trim().length()<5) {
	 		    		statstr="�����";
	 		        } else {
	 		        	 statstr="����������";
	 		        	 //OrderBusy=tmp.getId().trim();
	 		        }
	 		    	//System.out.println(tmp.getOrd_to()+"");
					addRowOrders(table, statstr+" "+tmp.getId(),tmp.getOrd_date(),tmp.getOrd_from(),tmp.getOrd_to(),tmp.getPrice(), tmp.getOrd_date_beg(), tmp.getOrd_date_out(),tmp.getOrd_date_end(), tmp.getOrd_km(), tmp.getStatus(), Integer.parseInt(tmp.getId()));
					 }

		        //setContentView(table);

	        
				
	        }
	        catch(Exception e)
	        {
	        	 Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
	        	 LGWR.logwriter(dic.logcom, dic.logpath, dic.getSysdate()+" - "+ TAG + ":cmdOrderlist " + e.toString());
	        	}
	}
	
	public void cmdOrderlistNull() {
		 try
	        {
		        Point size = new Point();
		        WindowManager w = getWindowManager();
		              int stat=0;
		              String statstr="";
		              Display d = w.getDefaultDisplay(); 
		              Measuredwidth = d.getWidth(); 
		              Measuredheight = d.getHeight(); 
		              dic.setMsr(Measuredwidth);
		         
		        TableLayout table = (TableLayout)findViewById(com.example.taxi.R.id.TaxiLayout);

		        
				if (Measuredwidth==1024 || Measuredwidth==800 || Measuredwidth==960) {
					table.setBackgroundResource(com.example.taxi.R.drawable.map1024552);
					
				} else {
					table.setBackgroundResource(com.example.taxi.R.drawable.map600976);
					
				}
					
	
		        table.setStretchAllColumns(true);
		        table.setShrinkAllColumns(true);
				addHead(table);

/////////////////
				TableRow rowOrders = new TableRow(this);
		        rowOrders.setGravity(Gravity.CENTER);
		        rowOrders.setBackgroundColor(Color.GRAY);
		      

		        TableRow.LayoutParams params4 = new TableRow.LayoutParams();
		        //params.gravity = Gravity.CENTER;
		        params4.setMargins(1, 1, 1, 1);
		        params4.span = 4;
		        params4.height= dic.RowOrdersHeight;
		        
		        TableRow.LayoutParams params16 = new TableRow.LayoutParams();
		        //params.gravity = Gravity.CENTER;
		        params16.setMargins(1, 1, 1, 1);
		        params16.span = 16;
		        params16.height= dic.RowOrdersHeight;
		        
		        TableRow.LayoutParams paramsFrame = new TableRow.LayoutParams();
		        //params.gravity = Gravity.CENTER;
		        paramsFrame.setMargins(10, 10, 10, 10);

		        
		        int nSize=dic.RowOrdersSize;
		        int nSizebtn=dic.RowOrdersButtonSize;
		        int nHeight=dic.RowOrdersHeight;
		        int nHeightbtn=dic.RowOrdersButtonHeight;
		        int nGravity=Gravity.CENTER;

		        Button order = new Button(this);
		        order.setText("---");
		        order.setTextSize(TypedValue.COMPLEX_UNIT_DIP, nSize);
		        order.setGravity(nGravity);
		        order.setPadding(1, 1, 1, 1);
		        order.setOnClickListener(this);


		        TextView orderdata = new TextView(this);
		        orderdata.setText("��������� �������� ������,\n � ������ �������� ������ ���������� � ���������� ��������");
		        orderdata.setTextSize(TypedValue.COMPLEX_UNIT_DIP, nSize);
		        orderdata.setGravity(nGravity);
		        orderdata.setPadding(1, 1, 1, 1);
		        

		            order.setTextColor(Color.WHITE);
		            orderdata.setTextColor(Color.BLACK);
		            order.setBackgroundColor(Color.rgb(00, 80, 00));
		            orderdata.setBackgroundColor(Color.WHITE);
		            order.setBackgroundDrawable(getResources().getDrawable(R.drawable.greenbutton));
		            
		            rowOrders.addView(order,params4);
		            rowOrders.addView(orderdata,params16);
		            table.addView(rowOrders,paramsFrame);
				
				
/////////////////				
	        
				
	        }
	        catch(Exception e)
	        {
	        	 Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
	        	 LGWR.logwriter(dic.logcom, dic.logpath, dic.getSysdate()+" - "+ TAG + ":cmdOrderlist " + e.toString());
	        	}
	}
	
	public void addRowOrders(TableLayout table, String cell1, String cell2, String cell3, String cell4, String cell5, String cell6, String cell7, String cell8, String cell9, String iffcell, int numOrder) {
		
        TableRow rowOrders = new TableRow(this);
        rowOrders.setGravity(Gravity.CENTER);
        rowOrders.setBackgroundColor(Color.GRAY);
      
        //TextView empty = new TextView(this);

        TableRow.LayoutParams params = new TableRow.LayoutParams();
        //params.gravity = Gravity.CENTER;
        params.setMargins(1, 1, 1, 1);
        params.span = 4;
        params.height= dic.RowOrdersHeight;
        
        TableRow.LayoutParams paramsAdr = new TableRow.LayoutParams();
        //paramsAdr.gravity = Gravity.CENTER;
        paramsAdr.setMargins(1, 1, 1, 1);
        paramsAdr.span = 5;
        paramsAdr.height= dic.RowOrdersHeight;
        
        TableRow.LayoutParams paramsSum = new TableRow.LayoutParams();
        //paramsSum.gravity = Gravity.CENTER;
        paramsSum.setMargins(1, 1, 1, 1);
        paramsSum.span = 2;
        paramsSum.height= dic.RowOrdersHeight;
        
        TableRow.LayoutParams paramsB = new TableRow.LayoutParams();
        //params.gravity = Gravity.CENTER;
        paramsB.setMargins(1, 1, 1, 1);
        paramsB.span = 4;
        paramsB.height= dic.RowOrdersButtonHeight;

        TableRow.LayoutParams paramsB5 = new TableRow.LayoutParams();
        //params.gravity = Gravity.CENTER;
        paramsB5.setMargins(1, 1, 1, 1);
        paramsB5.span = 5;
        paramsB5.height= dic.RowOrdersButtonHeight;

        TableRow.LayoutParams paramsB3 = new TableRow.LayoutParams();
        //params.gravity = Gravity.CENTER;
        paramsB3.setMargins(1, 1, 1, 1);
        paramsB3.span = 3;
        paramsB3.height= dic.RowOrdersButtonHeight;

        TableRow.LayoutParams paramsB2 = new TableRow.LayoutParams();
        //params.gravity = Gravity.CENTER;
        paramsB2.setMargins(1, 1, 1, 1);
        paramsB2.span = 2;
        paramsB2.height= dic.RowOrdersButtonHeight;
        
        TableRow.LayoutParams paramsFrame = new TableRow.LayoutParams();
        //params.gravity = Gravity.CENTER;
        paramsFrame.setMargins(10, 10, 10, 10);

        
        int nSize=dic.RowOrdersSize;
        int nSizebtn=dic.RowOrdersButtonSize;
        int nHeight=dic.RowOrdersHeight;
        int nHeightbtn=dic.RowOrdersButtonHeight;
        int nGravity=Gravity.CENTER;

        Button order = new Button(this);
        order.setText(cell1);
        order.setTextSize(TypedValue.COMPLEX_UNIT_DIP, nSize);
        order.setGravity(nGravity);
        order.setPadding(1, 1, 1, 1);
        order.setOnClickListener(this);
        //order.setHeight(nHeight);
        //order.setTypeface(Typeface.DEFAULT_BOLD);

        TextView orderdata = new TextView(this);
        orderdata.setText(cell2);
        orderdata.setTextSize(TypedValue.COMPLEX_UNIT_DIP, nSize);
        orderdata.setGravity(nGravity);
        orderdata.setPadding(1, 1, 1, 1);
        //orderdata.setHeight(nHeight);
        //orderdata.setTypeface(Typeface.DEFAULT_BOLD);

        TextView orderfrom = new TextView(this);
        orderfrom.setText(cell3);
        orderfrom.setTextSize(TypedValue.COMPLEX_UNIT_DIP, nSize);
        orderfrom.setGravity(nGravity);
        orderfrom.setPadding(1, 1, 1, 1);
        //orderfrom.setHeight(nHeight);
        //orderfrom.setTypeface(Typeface.DEFAULT_BOLD);

        TextView orderto = new TextView(this);
        orderto.setText(cell4);
        orderto.setTextSize(TypedValue.COMPLEX_UNIT_DIP, nSize);
        orderto.setGravity(nGravity);
        orderto.setPadding(1, 1, 1, 1);
        //orderto.setHeight(nHeight);
        //orderto.setTypeface(Typeface.DEFAULT_BOLD);

        TextView orderprice = new TextView(this);
        orderprice.setText(cell5);
        orderprice.setTextSize(TypedValue.COMPLEX_UNIT_DIP, nSize);
        orderprice.setGravity(nGravity);
        orderprice.setPadding(1, 1, 1, 1);
        //orderprice.setHeight(nHeight);
        //orderprice.setTypeface(Typeface.DEFAULT_BOLD);
        if (iffcell.length()<5) {

            order.setTextColor(Color.WHITE);
            orderdata.setTextColor(Color.BLACK);
            orderfrom.setTextColor(Color.BLACK);
            orderto.setTextColor(Color.BLACK);
            orderprice.setTextColor(Color.BLACK);
            order.setBackgroundColor(Color.rgb(00, 80, 00));
            order.setId(1000000000+numOrder);
            orderdata.setBackgroundColor(Color.WHITE);
            orderfrom.setBackgroundColor(Color.WHITE);
            orderto.setBackgroundColor(Color.WHITE);
            orderprice.setBackgroundColor(Color.WHITE); 
            order.setBackgroundDrawable(getResources().getDrawable(R.drawable.greenbutton));
            
            rowOrders.addView(order,params);
            rowOrders.addView(orderdata,params);
            rowOrders.addView(orderfrom,paramsAdr);
            rowOrders.addView(orderto,paramsAdr);
            rowOrders.addView(orderprice,paramsSum);
            
            table.addView(rowOrders,paramsFrame);
        } else {
            order.setTextColor(Color.WHITE);
            orderdata.setTextColor(Color.WHITE);
            orderfrom.setTextColor(Color.WHITE);
            orderto.setTextColor(Color.WHITE);
            orderprice.setTextColor(Color.WHITE); 	
            order.setBackgroundColor(Color.rgb(139,00,00));
            order.setId(-1000000000-numOrder);
            orderdata.setBackgroundColor(Color.rgb(64,95,237));
            orderfrom.setBackgroundColor(Color.rgb(64,95,237));
            orderto.setBackgroundColor(Color.rgb(64,95,237));
            orderprice.setBackgroundColor(Color.rgb(64,95,237));
            order.setBackgroundDrawable(getResources().getDrawable(R.drawable.redbutton));
            
            TableRow rowBtnOrders = new TableRow(this);
            rowBtnOrders.setGravity(Gravity.CENTER);
            rowBtnOrders.setBackgroundColor(Color.YELLOW);
            
            rowOrders.setBackgroundColor(Color.YELLOW);

            Button Ordersbtn1 = new Button(this);
            if (cell6.trim().length()<1) {
            	Ordersbtn1.setText("���������� ����� ������"); 
            } else {
            Ordersbtn1.setText("������\n"+cell6); 
            }
            Ordersbtn1.setBackgroundColor(Color.rgb(69,69,69)); //#454545
            Ordersbtn1.setTextColor(Color.WHITE);
            Ordersbtn1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, nSizebtn);
            Ordersbtn1.setGravity(nGravity);
            Ordersbtn1.setPadding(1, 1, 1, 1);
            Ordersbtn1.setId(1010000000+numOrder);
            Ordersbtn1.setOnClickListener(this);
            Ordersbtn1.setBackgroundDrawable(getResources().getDrawable(R.drawable.greybutton));
            
            //Ordersbtn1.setHeight(nHeightbtn);
            //Ordersbtn1.setTypeface(Typeface.DEFAULT_BOLD);

            Button Ordersbtn2 = new Button(this);
            if (cell7.trim().length()<1) {
            	Ordersbtn2.setText("���������� ����� �������");
            } else {
            Ordersbtn2.setText("������\n"+cell7);
            }
            Ordersbtn2.setBackgroundColor(Color.BLACK);
            Ordersbtn2.setTextColor(Color.WHITE);
            Ordersbtn2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, nSizebtn);
            Ordersbtn2.setGravity(nGravity);
            Ordersbtn2.setPadding(1, 1, 1, 1);
            Ordersbtn2.setId(1020000000+numOrder);
            Ordersbtn2.setOnClickListener(this);
            Ordersbtn2.setBackgroundDrawable(getResources().getDrawable(R.drawable.defbutton));
            
            //Ordersbtn2.setHeight(nHeightbtn);
            //Ordersbtn2.setTypeface(Typeface.DEFAULT_BOLD);
            

            Button Ordersbtn3 = new Button(this);
            if (cell8.trim().length()<1) {
            	Ordersbtn3.setText("���������� ����� ��������");	
            } else {
            Ordersbtn3.setText("��������\n"+cell8);
            }
            Ordersbtn3.setBackgroundColor(Color.rgb(69,69,69));
            Ordersbtn3.setTextColor(Color.WHITE);
            Ordersbtn3.setTextSize(TypedValue.COMPLEX_UNIT_DIP, nSizebtn);
            Ordersbtn3.setGravity(nGravity);
            Ordersbtn3.setPadding(1, 1, 1, 1);
            Ordersbtn3.setId(1030000000+numOrder);
            Ordersbtn3.setOnClickListener(this);
            Ordersbtn3.setBackgroundDrawable(getResources().getDrawable(R.drawable.greybutton));
            
            //Ordersbtn3.setHeight(nHeightbtn);
            //Ordersbtn3.setTypeface(Typeface.DEFAULT_BOLD);

            Button Ordersbtn4 = new Button(this);
            Ordersbtn4.setText("����\n "+cell9+"��.");
            Ordersbtn4.setBackgroundColor(Color.BLACK);
            Ordersbtn4.setTextColor(Color.WHITE);
            Ordersbtn4.setTextSize(TypedValue.COMPLEX_UNIT_DIP, nSizebtn);
            Ordersbtn4.setGravity(nGravity);
            Ordersbtn4.setPadding(1, 1, 1, 1);
            Ordersbtn4.setId(1040000000+numOrder);
            Ordersbtn4.setOnClickListener(this);
            Ordersbtn4.setBackgroundDrawable(getResources().getDrawable(R.drawable.defbutton));
            
            //Ordersbtn4.setHeight(nHeightbtn);
            //Ordersbtn4.setTypeface(Typeface.DEFAULT_BOLD);

            Button Ordersbtn5 = new Button(this);
            Ordersbtn5.setText("�����\n�������");
            Ordersbtn5.setBackgroundColor(Color.rgb(00, 80, 00));
            Ordersbtn5.setTextColor(Color.WHITE);
            Ordersbtn5.setTextSize(TypedValue.COMPLEX_UNIT_DIP, nSizebtn);
            Ordersbtn5.setGravity(nGravity);
            Ordersbtn5.setPadding(1, 1, 1, 1);
            Ordersbtn5.setId(1050000000+numOrder);
            Ordersbtn5.setOnClickListener(this);
            Ordersbtn5.setBackgroundDrawable(getResources().getDrawable(R.drawable.greenbutton));
            
            //Ordersbtn5.setHeight(nHeightbtn);
            //Ordersbtn5.setTypeface(Typeface.DEFAULT_BOLD);
            

            
            rowOrders.addView(order,params);
            rowOrders.addView(orderdata,params);
            rowOrders.addView(orderfrom,paramsAdr);
            rowOrders.addView(orderto,paramsAdr);
            rowOrders.addView(orderprice,paramsSum);
            
            table.addView(rowOrders,paramsFrame);
            
            
            rowBtnOrders.addView(Ordersbtn1,paramsB5);
            rowBtnOrders.addView(Ordersbtn2,paramsB5);
            rowBtnOrders.addView(Ordersbtn3,paramsB5);
            rowBtnOrders.addView(Ordersbtn4,paramsB2);
            rowBtnOrders.addView(Ordersbtn5,paramsB3);

            table.addView(rowBtnOrders,paramsFrame);
        }
        



	}
	
	public void addRowCarDriver(TableLayout table, String cell1, String cell2) {

		TableRow rowcardriver = new TableRow(this);
        rowcardriver.setGravity(Gravity.CENTER);
        //rowcardriver.setBackgroundColor(Color.rgb(00, 80, 00));
        rowcardriver.setBackgroundColor(Color.GRAY);
        rowcardriver.setId(11111111);

        TableRow.LayoutParams params = new TableRow.LayoutParams();
        TableRow.LayoutParams params2 = new TableRow.LayoutParams();
        //params.gravity = Gravity.CENTER;
        params.setMargins(1, 1, 1, 1);
        params.span = 8;
        params.height= dic.RowCarDriverHeight;
        params2.setMargins(1, 1, 1, 1);
        params2.span = 12;
        params2.height= dic.RowCarDriverHeight;
        
        int nSize=dic.RowCarDriverSize;
        int nHeight=dic.RowCarDriverHeight;
        int nGravity=Gravity.CENTER;

        TextView car = new TextView(this);
        car.setText(cell1);
        car.setTextSize(TypedValue.COMPLEX_UNIT_DIP, nSize);
        car.setBackgroundColor(Color.rgb(00, 80, 00)); //green
        car.setTextColor(Color.WHITE);
        car.setGravity(nGravity);
        car.setPadding(1, 1, 1, 1);
        //car.setTypeface(Typeface.DEFAULT_BOLD);

        TextView driver = new TextView(this);
        driver.setText(cell2);
        driver.setTextSize(TypedValue.COMPLEX_UNIT_DIP, nSize);
        driver.setBackgroundColor(Color.rgb(00, 80, 00));
        driver.setTextColor(Color.WHITE);
        driver.setGravity(nGravity);
        driver.setPadding(1, 1, 1, 1);
        //driver.setTypeface(Typeface.DEFAULT_BOLD);
   
        rowcardriver.addView(car,params);
        rowcardriver.addView(driver,params2);

        
        table.addView(rowcardriver);

	}
	
		public void addRowTitle(TableLayout table) {
		
     
        TableRow.LayoutParams params = new TableRow.LayoutParams();
        //params.gravity = Gravity.CENTER;
        params.setMargins(1, 1, 1, 1);
        params.span = 4;
        params.height= dic.RowTitleHeight;

        TableRow.LayoutParams paramsAdr = new TableRow.LayoutParams();
        //paramsAdr.gravity = Gravity.CENTER;
        paramsAdr.setMargins(1, 1, 1, 1);
        paramsAdr.span = 5;
        paramsAdr.height= dic.RowTitleHeight;
        
        TableRow.LayoutParams paramsSum = new TableRow.LayoutParams();
        //paramsSum.gravity = Gravity.CENTER;
        paramsSum.setMargins(1, 1, 1, 1);
        paramsSum.span = 2;
        paramsSum.height= dic.RowTitleHeight;

        TableRow rowTitle = new TableRow(this);
        rowTitle.setGravity(Gravity.CENTER);
        rowTitle.setBackgroundColor(Color.GRAY);
        rowTitle.setId(11111112);
        
        int nSize=dic.RowTitleSize;
        int nHeight=dic.RowTitleHeight;
        int nGravity=Gravity.CENTER;

        TextView order = new TextView(this);
        order.setText("�����");
        order.setBackgroundColor(Color.BLACK);
        order.setTextColor(Color.WHITE);
        order.setTextSize(TypedValue.COMPLEX_UNIT_DIP, nSize);
        order.setGravity(nGravity);
        //order.setHeight(nHeight);
        //order.setTypeface(Typeface.DEFAULT_BOLD);

        TextView orderdata = new TextView(this);
        orderdata.setText("����� ������");
        orderdata.setBackgroundColor(Color.BLACK);
        orderdata.setTextColor(Color.WHITE);
        orderdata.setTextSize(TypedValue.COMPLEX_UNIT_DIP, nSize);
        orderdata.setGravity(nGravity);
        //orderdata.setHeight(nHeight);
        //orderdata.setTypeface(Typeface.DEFAULT_BOLD);

        TextView orderfrom = new TextView(this);
        orderfrom.setText("����� ������");
        orderfrom.setBackgroundColor(Color.BLACK);
        orderfrom.setTextColor(Color.WHITE);
        orderfrom.setTextSize(TypedValue.COMPLEX_UNIT_DIP, nSize);
        orderfrom.setGravity(nGravity);
        //orderfrom.setHeight(nHeight);
        //orderfrom.setTypeface(Typeface.DEFAULT_BOLD);

        TextView orderto = new TextView(this);
        orderto.setText("����� ����������");
        orderto.setBackgroundColor(Color.BLACK);
        orderto.setTextColor(Color.WHITE);
        orderto.setTextSize(TypedValue.COMPLEX_UNIT_DIP, nSize);
        orderto.setGravity(nGravity);
        //orderto.setHeight(nHeight);
        //orderto.setTypeface(Typeface.DEFAULT_BOLD);

        TextView orderprice = new TextView(this);
        orderprice.setText("�����");
        orderprice.setBackgroundColor(Color.BLACK);
        orderprice.setTextColor(Color.WHITE);
        orderprice.setTextSize(TypedValue.COMPLEX_UNIT_DIP, nSize);
        orderprice.setGravity(nGravity);
        //orderprice.setHeight(nHeight);
        //orderprice.setTypeface(Typeface.DEFAULT_BOLD);
        
        rowTitle.addView(order,params);
        rowTitle.addView(orderdata,params);
        rowTitle.addView(orderfrom,paramsAdr);
        rowTitle.addView(orderto,paramsAdr);
        rowTitle.addView(orderprice,paramsSum);

        table.addView(rowTitle);

	}
	
	public void addRowButton(TableLayout table) {
		
		//SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy ");
        //String strTime = simpleDateFormat.format(new Date());
        
        TableRow.LayoutParams params = new TableRow.LayoutParams();
        //params.gravity = Gravity.CENTER;
        params.setMargins(1, 1, 1, 1);
        params.span = 4;
        params.height= dic.RowButtonHeight;

        TableRow.LayoutParams params3 = new TableRow.LayoutParams();
        //params.gravity = Gravity.CENTER;
        params3.setMargins(1, 1, 1, 1);
        params3.span = 4;
        params3.height= dic.RowButtonHeight;
        
        TableRow.LayoutParams params5 = new TableRow.LayoutParams();
        //params.gravity = Gravity.CENTER;
        params5.setMargins(1, 1, 1, 1);
        params5.span = 5;
        params5.height= dic.RowButtonHeight;
        
        int nSize=dic.RowButtonSize;
        int nHeight=dic.RowButtonHeight;
        int nGravity=Gravity.CENTER;
        
        TableRow rowBtn = new TableRow(this);
        rowBtn.setGravity(Gravity.CENTER);
        rowBtn.setBackgroundColor(Color.GRAY);
 

        Button btn1 = new Button(this);
        btn1.setText(dic.getSysdate());
        btn1.setBackgroundColor(Color.BLACK);
        btn1.setTextColor(Color.WHITE);
        btn1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, nSize);
        btn1.setGravity(nGravity);
        //btn1.setHeight(nHeight);
        //btn1.setTypeface(Typeface.DEFAULT_BOLD);
        btn1.setPadding(1, 1, 1, 1);
        btn1.setId(1060000000);
        btn1.setOnClickListener(this);
        btn1.setBackgroundDrawable(getResources().getDrawable(R.drawable.defbutton));
 
        Button btn2 = new Button(this);
        btn2.setText("��������\n������");
        btn2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, nSize);
        btn2.setBackgroundColor(Color.BLACK);
        btn2.setTextColor(Color.WHITE);
        btn2.setGravity(nGravity);
        //btn2.setHeight(nHeight);
        //btn2.setTypeface(Typeface.DEFAULT_BOLD);
        btn2.setPadding(1, 1, 1, 1);
        btn2.setId(1070000000);
        btn2.setOnClickListener(this);
        btn2.setBackgroundDrawable(getResources().getDrawable(R.drawable.defbutton));
 
        Button btn3 = new Button(this);
        btn3.setText("�������\n���������");
        btn3.setTextSize(TypedValue.COMPLEX_UNIT_DIP, nSize);
        btn3.setBackgroundColor(Color.BLACK);
        btn3.setTextColor(Color.WHITE);
        btn3.setGravity(nGravity);
        //btn3.setHeight(nHeight);
        //btn3.setTypeface(Typeface.DEFAULT_BOLD);
        btn3.setPadding(1, 1, 1, 1);
        btn3.setId(1080000000);
        btn3.setOnClickListener(this);
        btn3.setBackgroundDrawable(getResources().getDrawable(R.drawable.defbutton));
        
        Button btn4 = new Button(this);
        btn4.setText("�����");
        btn4.setTextSize(TypedValue.COMPLEX_UNIT_DIP, nSize);
        btn4.setBackgroundColor(Color.rgb(64,95,237)); //blue 405FED
        btn4.setTextColor(Color.WHITE);
        btn4.setGravity(nGravity);
        //btn4.setHeight(nHeight);
        //btn4.setTypeface(Typeface.DEFAULT_BOLD);
        btn4.setPadding(1, 1, 1, 1);
        btn4.setId(1090000000);
        btn4.setOnClickListener(this);        
        btn4.setBackgroundDrawable(getResources().getDrawable(R.drawable.bluebutton));

        Button btn5 = new Button(this);
        btn5.setText("�����");
        btn5.setTextSize(TypedValue.COMPLEX_UNIT_DIP, nSize);
        btn5.setBackgroundColor(Color.rgb(139,00,00)); //8B0000
        btn5.setTextColor(Color.WHITE);
        btn5.setGravity(nGravity);
        //btn5.setHeight(nHeight);
        //btn5.setTypeface(Typeface.DEFAULT_BOLD);
        btn5.setPadding(1, 1, 1, 1);
        btn5.setId(1100000000);
        btn5.setOnClickListener(this);
        btn5.setBackgroundDrawable(getResources().getDrawable(R.drawable.redbutton));

        
        rowBtn.addView(btn1,params);
        rowBtn.addView(btn2,params);
        rowBtn.addView(btn3,params);
        rowBtn.addView(btn4,params);
        rowBtn.addView(btn5,params);

        table.addView(rowBtn);

	}
	
	public void addHead(TableLayout table) {
        TableRow.LayoutParams params = new TableRow.LayoutParams();
        //params.setMargins(1, 1, 1, 1);
        params.span = 1;
        params.height= 0;
		
        int ngrav=Gravity.CENTER;
        TableRow rowColHead = new TableRow(this);
        rowColHead.setGravity(ngrav);
        //rowColHead.setPadding(1, 1, 1, 1);
        
        

        TextView c1 = new TextView(this);
        c1.setText("1");
        c1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
        c1.setGravity(ngrav);
        c1.setBackgroundColor(Color.GRAY);
        c1.setTextColor(Color.WHITE);

        TextView c2 = new TextView(this);
        c2.setText("2");
        c2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
        c2.setGravity(ngrav);
        c2.setBackgroundColor(Color.GRAY);
        c2.setTextColor(Color.WHITE);
        
        TextView c3 = new TextView(this);
        c3.setText("3");
        c3.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
        c3.setGravity(ngrav);
        c3.setBackgroundColor(Color.GRAY);
        c3.setTextColor(Color.WHITE);
        
        TextView c4 = new TextView(this);
        c4.setText("4");
        c4.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
        c4.setGravity(ngrav);
        c4.setBackgroundColor(Color.GRAY);
        c4.setTextColor(Color.WHITE);
        
        TextView c5 = new TextView(this);
        c5.setText("5");
        c5.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
        c5.setGravity(ngrav);
        c5.setBackgroundColor(Color.GRAY);
        c5.setTextColor(Color.WHITE);
        
        TextView c6 = new TextView(this);
        c6.setText("6");
        c6.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
        c6.setGravity(ngrav);
        c6.setBackgroundColor(Color.GRAY);
        c6.setTextColor(Color.WHITE);
        
        TextView c7 = new TextView(this);
        c7.setText("7");
        c7.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
        c7.setGravity(ngrav);
        c7.setBackgroundColor(Color.GRAY);
        c7.setTextColor(Color.WHITE);
        
        TextView c8 = new TextView(this);
        c8.setText("8");
        c8.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
        c8.setGravity(ngrav);
        c8.setBackgroundColor(Color.GRAY);
        c8.setTextColor(Color.WHITE);
        
        TextView c9 = new TextView(this);
        c9.setText("9");
        c9.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
        c9.setGravity(ngrav);
        c9.setBackgroundColor(Color.GRAY);
        c9.setTextColor(Color.WHITE);
        
        TextView c10 = new TextView(this);
        c10.setText("10");
        c10.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
        c10.setGravity(ngrav);
        c10.setBackgroundColor(Color.GRAY);
        c10.setTextColor(Color.WHITE);

        TextView c11 = new TextView(this);
        c11.setText("11");
        c11.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
        c11.setGravity(ngrav);
        c11.setBackgroundColor(Color.GRAY);
        c11.setTextColor(Color.WHITE);

        TextView c12 = new TextView(this);
        c12.setText("12");
        c12.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
        c12.setGravity(ngrav);
        c12.setBackgroundColor(Color.GRAY);
        c12.setTextColor(Color.WHITE);

        TextView c13 = new TextView(this);
        c13.setText("13");
        c13.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
        c13.setGravity(ngrav);
        c13.setBackgroundColor(Color.GRAY);
        c13.setTextColor(Color.WHITE);

        TextView c14 = new TextView(this);
        c14.setText("14");
        c14.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
        c14.setGravity(ngrav);
        c14.setBackgroundColor(Color.GRAY);
        c14.setTextColor(Color.WHITE);

        TextView c15 = new TextView(this);
        c15.setText("15");
        c15.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
        c15.setGravity(ngrav);
        c15.setBackgroundColor(Color.GRAY);
        c15.setTextColor(Color.WHITE);

        TextView c16 = new TextView(this);
        c16.setText("16");
        c16.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
        c16.setGravity(ngrav);
        c16.setBackgroundColor(Color.GRAY);
        c16.setTextColor(Color.WHITE);

        TextView c17 = new TextView(this);
        c17.setText("17");
        c17.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
        c17.setGravity(ngrav);
        c17.setBackgroundColor(Color.GRAY);
        c17.setTextColor(Color.WHITE);

        TextView c18 = new TextView(this);
        c18.setText("18");
        c18.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
        c18.setGravity(ngrav);
        c18.setBackgroundColor(Color.GRAY);
        c18.setTextColor(Color.WHITE);

        TextView c19 = new TextView(this);
        c19.setText("19");
        c19.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
        c19.setGravity(ngrav);
        c19.setBackgroundColor(Color.GRAY);
        c19.setTextColor(Color.WHITE);

        TextView c20 = new TextView(this);
        c20.setText("20");
        c20.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
        c20.setGravity(ngrav);
        c20.setBackgroundColor(Color.GRAY);
        c20.setTextColor(Color.WHITE);



        rowColHead.addView(c1,params);
        rowColHead.addView(c2,params);
        rowColHead.addView(c3,params);
        rowColHead.addView(c4,params);
        rowColHead.addView(c5,params);
        rowColHead.addView(c6,params);
        rowColHead.addView(c7,params);
        rowColHead.addView(c8,params);
        rowColHead.addView(c9,params);
        rowColHead.addView(c10,params);
        rowColHead.addView(c11,params);
        rowColHead.addView(c12,params);
        rowColHead.addView(c13,params);
        rowColHead.addView(c14,params);
        rowColHead.addView(c15,params);
        rowColHead.addView(c16,params);
        rowColHead.addView(c17,params);
        rowColHead.addView(c18,params);
        rowColHead.addView(c19,params);
        rowColHead.addView(c20,params);
        
        table.addView(rowColHead);

        
	}

	public void addshortcut(){
		Intent shortcutIntent = new Intent();
		ShortcutIconResource icon = Intent.ShortcutIconResource.fromContext(this, R.drawable.ic_taxi);

		shortcutIntent.setClassName("com.example.taxi", ".MainActivity");
		shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		shortcutIntent.addCategory(Intent.ACTION_PICK_ACTIVITY);
		Intent intentSC = new Intent();
		intentSC.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
		intentSC.putExtra(Intent.EXTRA_SHORTCUT_NAME, "����� �1");
		//intentSC.putExtra(Intent.EXTRA_SHORTCUT_ICON,icon);
		intentSC.putExtra(Intent.EXTRA_SHORTCUT_ICON,BitmapFactory.decodeFile("ic_taxi.png"));
		intentSC.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
		sendBroadcast(intentSC);

	}
	
	public Dialog onCreateDialog(int id) {
		if (id == DIALOG_TIME) {
	        AlertDialog.Builder adb = new AlertDialog.Builder(this);
	        adb.setTitle(R.string.DIALOG_TITLE);
	        adb.setMessage(R.string.DIALOG_TIME);
	        adb.setIcon(android.R.drawable.ic_dialog_info);
	        adb.setPositiveButton(R.string.DIALOG_YES, lsnrTIME);
	        adb.setNegativeButton(R.string.DIALOG_NO, lsnrTIME);
	        return adb.create();
		}
		if (id == DIALOG_REFRESH) {
	        AlertDialog.Builder adb = new AlertDialog.Builder(this);
	        adb.setTitle(R.string.DIALOG_TITLE);
	        adb.setMessage(R.string.DIALOG_REFRESH);
	        adb.setIcon(android.R.drawable.ic_dialog_info);
	        adb.setPositiveButton(R.string.DIALOG_YES, lsnrREFRESH);
	        adb.setNegativeButton(R.string.DIALOG_NO, lsnrREFRESH);
	        return adb.create();
		}
		if (id == DIALOG_CALL) {
	        AlertDialog.Builder adb = new AlertDialog.Builder(this);
	        adb.setTitle(R.string.DIALOG_TITLE);
	        adb.setMessage(R.string.DIALOG_CALL);
	        adb.setIcon(android.R.drawable.ic_dialog_info);
	        adb.setPositiveButton(R.string.DIALOG_YES, lsnrCALL);
	        adb.setNegativeButton(R.string.DIALOG_NO, lsnrCALL);
	        return adb.create();
		}
		if (id == DIALOG_MAP) {
	        AlertDialog.Builder adb = new AlertDialog.Builder(this);
	        adb.setTitle(R.string.DIALOG_TITLE);
	        adb.setMessage(R.string.DIALOG_MAP);
	        adb.setIcon(android.R.drawable.ic_dialog_info);
	        adb.setPositiveButton(R.string.DIALOG_OK, lsnrMAP);
	        adb.setNegativeButton(R.string.DIALOG_CANCEL, lsnrMAP);
	        return adb.create();
		}
		if (id == DIALOG_FREE) {
	        AlertDialog.Builder adb = new AlertDialog.Builder(this);
	        adb.setTitle(R.string.DIALOG_TITLE);
	        adb.setMessage(R.string.DIALOG_FREE);
	        adb.setIcon(android.R.drawable.ic_dialog_info);
	        adb.setPositiveButton(R.string.DIALOG_YES, lsnrFREE);
	        adb.setNegativeButton(R.string.DIALOG_NO, lsnrFREE);
	        return adb.create();
		}
		if (id == DIALOG_TAKE) {
	        AlertDialog.Builder adb = new AlertDialog.Builder(this);
	        adb.setTitle(R.string.DIALOG_TITLE);
	        adb.setMessage(R.string.DIALOG_TAKE);
	        adb.setIcon(android.R.drawable.ic_dialog_info);
	        adb.setPositiveButton(R.string.DIALOG_YES, lsnrTAKE);
	        adb.setNegativeButton(R.string.DIALOG_NO, lsnrTAKE);
	        return adb.create();
		}
		if (id == DIALOG_INSTALL_TIME) {
	        AlertDialog.Builder adb = new AlertDialog.Builder(this);
	        adb.setTitle("������ ");//+this.flg_numorder);
	        adb.setMessage(R.string.DIALOG_INSTALL_TIME);
	        adb.setIcon(android.R.drawable.ic_dialog_info);
	        adb.setPositiveButton(R.string.DIALOG_YES, lsnrINSTALL_TIME);
	        adb.setNegativeButton(R.string.DIALOG_NO, lsnrINSTALL_TIME);
	        return adb.create();
		}
		if (id == DIALOG_INSTALL_TIME_OUT) {
	        AlertDialog.Builder adb = new AlertDialog.Builder(this);
	        adb.setTitle("������ ");//+this.flg_numorder);
	        adb.setMessage(R.string.DIALOG_INSTALL_TIME_OUT);
	        adb.setIcon(android.R.drawable.ic_dialog_info);
	        adb.setPositiveButton(R.string.DIALOG_YES, lsnrINSTALL_TIME_OUT);
	        adb.setNegativeButton(R.string.DIALOG_NO, lsnrINSTALL_TIME_OUT);
	        return adb.create();
		}
		if (id == DIALOG_INSTALL_TIME_IN) {
	        AlertDialog.Builder adb = new AlertDialog.Builder(this);
	        adb.setTitle("������ ");//+this.flg_numorder);
	        adb.setMessage(R.string.DIALOG_INSTALL_TIME_IN);
	        adb.setIcon(android.R.drawable.ic_dialog_info);
	        adb.setPositiveButton(R.string.DIALOG_YES, lsnrINSTALL_TIME_IN);
	        adb.setNegativeButton(R.string.DIALOG_NO, lsnrINSTALL_TIME_IN);
	        return adb.create();
		}
		if (id == DIALOG_KM) {
	        AlertDialog.Builder adb = new AlertDialog.Builder(this);
	        adb.setTitle("������ ");//+this.flg_numorder);
	        adb.setMessage(R.string.DIALOG_KM_BEG);
	        adb.setIcon(android.R.drawable.ic_dialog_info);
	        adb.setPositiveButton(R.string.DIALOG_KM_BEG, lsnrKM);
	        adb.setNeutralButton(R.string.DIALOG_KM_END, lsnrKM);
	        adb.setNegativeButton(R.string.DIALOG_CANCEL, lsnrKM);
	        return adb.create();
		}

		if (id == DIALOG_TAXI) {
	        AlertDialog.Builder adb = new AlertDialog.Builder(MainActivity.this);
	        adb.setTitle("������ ");//+this.flg_numorder);
	        adb.setMessage(R.string.DIALOG_TAXI);
	        adb.setIcon(android.R.drawable.ic_dialog_info);
	        adb.setPositiveButton(R.string.DIALOG_TAXI_CALL, lsnrTAXI);
	        adb.setNeutralButton(R.string.DIALOG_TAXI_SMS, lsnrTAXI);
	        adb.setNegativeButton(R.string.DIALOG_CANCEL, lsnrTAXI);
	        //Log.d(TAG,"dialog:"+this.flg_numorder);
	        //LGWR.logwriter(dic.logcom, dic.logpath,"dialog:"+dic.getNumOrder());
	        return adb.create();
		}
	    if (id == DIALOG_EXIT) {
	        AlertDialog.Builder adb = new AlertDialog.Builder(this);
	        adb.setTitle(R.string.DIALOG_TITLE);
	        //adb.setInverseBackgroundForced(true);
	        adb.setMessage(R.string.DIALOG_EXIT);
	        adb.setIcon(android.R.drawable.ic_dialog_info);
	        adb.setPositiveButton(R.string.DIALOG_EXT, lsnrlExit);
	        adb.setNegativeButton(R.string.DIALOG_CANCEL, lsnrlExit);
	        return adb.create();
	      }
	      
	      return super.onCreateDialog(id);
	    }
	    
OnClickListener lsnrTIME = new OnClickListener() {
public void onClick(DialogInterface dialog, int which) {
switch (which) {
case Dialog.BUTTON_POSITIVE: 	
	//TimeTask tmTask = new TimeTask();
	//tmTask.execute();
	break;
case Dialog.BUTTON_NEGATIVE:  break;    }  } };

OnClickListener lsnrREFRESH = new OnClickListener() {
public void onClick(DialogInterface dialog, int which) {
switch (which) {
case Dialog.BUTTON_POSITIVE: 	
	OrderTask ordTask = new OrderTask(); 
	ordTask.execute();
	break;
case Dialog.BUTTON_NEGATIVE:  break;    }  } };

OnClickListener lsnrCALL = new OnClickListener() {
public void onClick(DialogInterface dialog, int which) {
switch (which) {
case Dialog.BUTTON_POSITIVE: 	
    sysThreads cmdThready1 = new sysThreads(MainActivity.this,dic,LGWR,mSocket,"cmdsocket","driver_call_operator,quit;");
    cmdThready1.start();
    LGWR.logwriter(dic.logcom, dic.logpath, dic.getSysdate()+" - "+ TAG + ".." +"initialized thread CMD:"+cmdThready1.getId());
	
	OrderTask ordTask = new OrderTask(); 
	ordTask.execute();
	
	break;
case Dialog.BUTTON_NEGATIVE:  break;    }  } };

OnClickListener lsnrMAP = new OnClickListener() {
public void onClick(DialogInterface dialog, int which) {
switch (which) {
case Dialog.BUTTON_POSITIVE: 	
	break;
case Dialog.BUTTON_NEGATIVE:  break;    }  } };

OnClickListener lsnrFREE = new OnClickListener() {
public void onClick(DialogInterface dialog, int which) {
switch (which) {
case Dialog.BUTTON_POSITIVE: 	 
    sysThreads cmdThready2 = new sysThreads(MainActivity.this,dic,LGWR,mSocket,"cmdsocket","orders_"+MainActivity.flg_numorder+"_free,quit;");
    cmdThready2.start();
	LGWR.logwriter(dic.logcom, dic.logpath, dic.getSysdate()+" - "+ TAG + ".." +"initialized thread CMD:"+cmdThready2.getId());

	
	OrderTask ordTask = new OrderTask(); 
	ordTask.execute();
	break;
case Dialog.BUTTON_NEGATIVE:  break;    }  } };

OnClickListener lsnrTAKE = new OnClickListener() {
public void onClick(DialogInterface dialog, int which) {
switch (which) {
case Dialog.BUTTON_POSITIVE: 	 
    sysThreads cmdThready1 = new sysThreads(MainActivity.this,dic,LGWR,mSocket,"cmdsocket","orders_"+MainActivity.flg_numorder+"_accept,quit;");
    cmdThready1.start();
    LGWR.logwriter(dic.logcom, dic.logpath, dic.getSysdate()+" - "+ TAG + ".." +"initialized thread CMD:"+cmdThready1.getId());
	
	OrderTask ordTask = new OrderTask(); 
	ordTask.execute();
	break;
case Dialog.BUTTON_NEGATIVE:  break;    }  } };

OnClickListener lsnrINSTALL_TIME = new OnClickListener() {
public void onClick(DialogInterface dialog, int which) {
switch (which) {
case Dialog.BUTTON_POSITIVE: 	   
    sysThreads cmdThready1 = new sysThreads(MainActivity.this,dic,LGWR,mSocket,"cmdsocket","orders_"+MainActivity.flg_numorder+"_driver_wait,quit;");
    cmdThready1.start();
    LGWR.logwriter(dic.logcom, dic.logpath, dic.getSysdate()+" - "+ TAG + ".." +"initialized thread CMD:"+cmdThready1.getId());

	OrderTask ordTask = new OrderTask(); 
	ordTask.execute();
	break;
case Dialog.BUTTON_NEGATIVE:  break;    }  } };

OnClickListener lsnrINSTALL_TIME_OUT = new OnClickListener() {
public void onClick(DialogInterface dialog, int which) {
switch (which) {
case Dialog.BUTTON_POSITIVE: 	   
	sysThreads cmdThready1 = new sysThreads(MainActivity.this,dic,LGWR,mSocket,"cmdsocket","orders_"+MainActivity.flg_numorder+"_driver_away,quit;");
    cmdThready1.start();
    LGWR.logwriter(dic.logcom, dic.logpath, dic.getSysdate()+" - "+ TAG + ".." +"initialized thread CMD:"+cmdThready1.getId());

	OrderTask ordTask = new OrderTask(); 
	ordTask.execute();
	break;
case Dialog.BUTTON_NEGATIVE:  break;    }  } };

OnClickListener lsnrINSTALL_TIME_IN = new OnClickListener() {
public void onClick(DialogInterface dialog, int which) {
switch (which) {
case Dialog.BUTTON_POSITIVE: 	  
	sysThreads cmdThready1 = new sysThreads(MainActivity.this,dic,LGWR,mSocket,"cmdsocket","orders_"+MainActivity.flg_numorder+"_driver_complete,quit;");
    cmdThready1.start();
    LGWR.logwriter(dic.logcom, dic.logpath, dic.getSysdate()+" - "+ TAG + ".." +"initialized thread CMD:"+cmdThready1.getId());

	OrderTask ordTask = new OrderTask(); 
	ordTask.execute();
	break;
case Dialog.BUTTON_NEGATIVE:  break;    }  } };

OnClickListener lsnrKM = new OnClickListener() {
public void onClick(DialogInterface dialog, int which) {
switch (which) {
case Dialog.BUTTON_POSITIVE: 	  
	sysThreads cmdThready1 = new sysThreads(MainActivity.this,dic,LGWR,mSocket,"cmdsocket","orders_"+MainActivity.flg_numorder+"_km_start,quit;");
    cmdThready1.start();
    LGWR.logwriter(dic.logcom, dic.logpath, dic.getSysdate()+" - "+ TAG + ".." +"initialized thread CMD:"+cmdThready1.getId());

	//Log.d(TAG,"dialog:"+MainActivity.flg_numorder);
	OrderTask ordTask1 = new OrderTask(); 
	ordTask1.execute();
	break;
	
case Dialog.BUTTON_NEUTRAL:
	sysThreads cmdThready2 = new sysThreads(MainActivity.this,dic,LGWR,mSocket,"cmdsocket","orders_"+MainActivity.flg_numorder+"_km_stop,quit;");
    cmdThready2.start();
    LGWR.logwriter(dic.logcom, dic.logpath, dic.getSysdate()+" - "+ TAG + ".." +"initialized thread CMD:"+cmdThready2.getId());

	//Log.d(TAG,"dialog:"+MainActivity.flg_numorder);
	OrderTask ordTask2 = new OrderTask(); 
	ordTask2.execute();
	break;
	
case Dialog.BUTTON_NEGATIVE:  break;    }  } };

OnClickListener lsnrTAXI = new OnClickListener() {
public void onClick(DialogInterface dialog, int which) {
switch (which) {
case Dialog.BUTTON_POSITIVE: 	
	sysThreads cmdThready1 = new sysThreads(MainActivity.this,dic,LGWR,mSocket,"cmdsocket","orders_"+MainActivity.flg_numorder+"_driver_call_auto,quit;");
    cmdThready1.start();
    LGWR.logwriter(dic.logcom, dic.logpath, dic.getSysdate()+" - "+ TAG + ".." +"initialized thread CMD:"+cmdThready1.getId());

	//Log.d(TAG,"dialog:"+MainActivity.flg_numorder);
	OrderTask ordTask1 = new OrderTask(); 
	ordTask1.execute();
	break;
case Dialog.BUTTON_NEUTRAL:
	sysThreads cmdThready2 = new sysThreads(MainActivity.this,dic,LGWR,mSocket,"cmdsocket","orders_"+MainActivity.flg_numorder+"_sms_client,quit;");
    cmdThready2.start();
    LGWR.logwriter(dic.logcom, dic.logpath, dic.getSysdate()+" - "+ TAG + ".." +"initialized thread CMD:"+cmdThready2.getId());

	//Log.d(TAG,"dialog:"+MainActivity.flg_numorder);
	OrderTask ordTask2 = new OrderTask(); 
	ordTask2.execute();
	break;
case Dialog.BUTTON_NEGATIVE:  break;    }  } };

OnClickListener lsnrlExit = new OnClickListener() {
public void onClick(DialogInterface dialog, int which) {
switch (which) {
case Dialog.BUTTON_POSITIVE: 
	LGWR.logwriter(dic.logcom, dic.logpath, dic.getSysdate()+" - Closing program Taxi1...");
	MainActivity.myTimer.purge();
	MainActivity.myTimer2.purge();
	MainActivity.myTimer3.purge();
    final int pid = android.os.Process.myPid();
    android.os.Process.killProcess(pid);
	finish();    break;
case Dialog.BUTTON_NEGATIVE:  break;    }  } };


	  
	@Override
		public void onClick(View v) {
		this.flg_numorder =0;
		int i = (int) Math.round(v.getId()/10000000);
		/*if (v.getId()>0) {
		int j = v.getId()-i*10000000;
		MainActivity.flg_numorder = v.getId()-i*10000000;
		} else {
		int j = Math.abs(v.getId()+i*10000000);
		MainActivity.flg_numorder = j;
		}*/
		//Toast.makeText(this, "������ � "+(j) , Toast.LENGTH_LONG).show();
			switch (i) {
		     case 106://�����
		    	 //showDialog(DIALOG_TIME);
		    	 //MainActivity.myTimer.purge();
		       break;
			 case 107://��������
				 showDialog(DIALOG_REFRESH);
				/* 	TableLayout table = (TableLayout)findViewById(com.example.taxi.R.id.TaxiLayout);
				 	table.removeAllViewsInLayout();

				 	
			        sysThreads dataThready = new sysThreads(this,dic,LGWR,mSocket,"refreshdata");
			        dataThready.start();
			        LGWR.logwriter(dic.logcom, dic.logpath, dic.getSysdate()+" - "+ TAG + ".." +"initialized thread DATA:"+dataThready.getId());
			        
					do {
						try{
			                Thread.sleep(1000);		
			            }catch(InterruptedException e){}
					} while(this.flg_refreshdata<1);

		            cmdOrderlist();*/
		        
		       break;
		     case 108://������� ���������
		    	 showDialog(DIALOG_CALL);
		       break;
		     case 109://�����
		    	 showDialog(DIALOG_MAP);
		       break;
		     case 110://�����
		    	 showDialog(DIALOG_EXIT);
			       break;
		     case 101://���������� ����� ������
		    	 this.flg_numorder = v.getId()-i*10000000;
		    	 showDialog(DIALOG_INSTALL_TIME);
			       break;
		     case 102://���������� ����� �������
		    	 this.flg_numorder = v.getId()-i*10000000;
		    	 showDialog(DIALOG_INSTALL_TIME_OUT);
			       break;
		     case 103://���������� ����� ��������
		    	 this.flg_numorder = v.getId()-i*10000000;
		    	 showDialog(DIALOG_INSTALL_TIME_IN);
			       break;
		     case 104://���������
		    	 this.flg_numorder = v.getId()-i*10000000;
		    	 showDialog(DIALOG_KM);
			       break;
		     case 105://����� �������
		    	 this.flg_numorder=v.getId()-i*10000000;
		    	 //Log.d(TAG,"case 105:"+this.flg_numorder);
		    	 showDialog(DIALOG_TAXI);
			       break;
		     case 100://�����  ������
		    	 this.flg_numorder = (v.getId()-1000000000);
		         //sysThreads cmdThready1 = new sysThreads(this,dic,LGWR,mSocket,"cmdsocket","orders_"+this.flg_numorder+"_accept,quit;");
		         //cmdThready1.start();
		         ////String cmdsock1="orders_"+this.flg_numorder+"_accept,quit;";
		         ////SocketTAXI mSocket1 = new SocketTAXI(dic, LGWR);
				 ////mSocket1.ServerPutCMD(dic.getUid(),dic.getServerTaxi(),dic.getServerTaxiPortCMD(), cmdsock1);
		         //LGWR.logwriter(dic.logcom, dic.logpath, dic.getSysdate()+" - "+ TAG + ".." +"initialized thread CMD:"+cmdThready1.getId());
		    	 //OrderTask ordTask1 = new OrderTask(); 
		    	 //ordTask1.execute();
		    	 showDialog(DIALOG_TAKE);
		    	 break;
		    	 
		     case -100:// ���������� ������
		    	 this.flg_numorder = Math.abs((v.getId()+1000000000));
		         //sysThreads cmdThready2 = new sysThreads(this,dic,LGWR,mSocket,"cmdsocket","orders_"+this.flg_numorder+"_free,quit;");
		         //cmdThready2.start();
		         ////String cmdsock2="orders_"+this.flg_numorder+"_accept,quit;";
		         ////SocketTAXI mSocket2 = new SocketTAXI(dic, LGWR);
				 ////mSocket2.ServerPutCMD(dic.getUid(),dic.getServerTaxi(),dic.getServerTaxiPortCMD(), cmdsock2);		         
		         //LGWR.logwriter(dic.logcom, dic.logpath, dic.getSysdate()+" - "+ TAG + ".." +"initialized thread CMD:"+cmdThready2.getId());
		    	 //OrderTask ordTask2 = new OrderTask(); 
		    	 //ordTask2.execute();
		    	 showDialog(DIALOG_FREE);
				   break;
			}
			
		}
	
	 class TimeTask extends AsyncTask<Void, Void, String> {
	        
	        @Override
	        protected String doInBackground(Void... noargs) {
	            return dic.getSysdate();
	        }

	        @Override
	        protected void onPostExecute(String result) {
           	 	TableLayout tablehead = (TableLayout)findViewById(com.example.taxi.R.id.TaxiHeadLayout);
           	 	Button txtResult = (Button) tablehead.findViewById(1060000000);
                txtResult.setText(result);
	        }
	    }

	 class OrderTask extends AsyncTask<Void, Void, String> {
	        
	        @Override
	        protected String doInBackground(Void... noargs) {
	            sysThreads dataThready = new sysThreads(MainActivity.this,dic,LGWR,mSocket,"refreshdataper","");
	            dataThready.start();
	            return dic.getSysdate();
	        }

	        @Override
	        protected void onPostExecute(String result) {
	        	TableLayout table = (TableLayout)findViewById(com.example.taxi.R.id.TaxiLayout);
	        	
		        LGWR.logwriter(dic.logcom, dic.logpath, dic.getSysdate()+" - "+ TAG + ".." +"initialized thread AsyncTask:OrderTask");
		        
				do {
					try{
		                Thread.sleep(1000);		
		            }catch(InterruptedException e){}
				} while(MainActivity.flg_refreshdata<1);
			 	table.removeAllViewsInLayout();
	            cmdOrderlist();
	        }
	    }

	 class OrderFillTask extends AsyncTask<Void, Void, String> {
	        
	        @Override
	        protected String doInBackground(Void... noargs) {

	            return MainActivity.flg_refreshdata+"";
	        }

	        @Override
	        protected void onPostExecute(String result) {
	        	if (MainActivity.flg_interfacecrt<1) {
	        		if (MainActivity.flg_refreshdata>0) {
	        			TableLayout table = (TableLayout)findViewById(com.example.taxi.R.id.TaxiLayout);	        		
	        			table.removeAllViewsInLayout();
	        			cmdOrderheadUpd();
	        			cmdOrderlist();
	        			MainActivity.flg_interfacecrt=1;
	        			LGWR.logwriter(dic.logcom, dic.logpath, dic.getSysdate()+" - "+ TAG + ".." +"initialized thread AsyncTask:OrderFillTask");
	            //	MainActivity.myTimer3.purge();
	        		}
	        	}	
	        }
	    }	 
}
