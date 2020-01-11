package com.example.rbreak

import android.Manifest.permission.*
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.sfit.ctp.thosttraderapi.*
import com.sfit.ctp.thosttraderapi.THOST_TE_RESUME_TYPE.THOST_TERT_QUICK
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.os.Handler
import android.os.Message
import android.view.View
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.timerTask
import android.R.string.no
//import androidx.core.app.ComponentActivity
//`import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.R.attr.name
import android.R.attr.stepSize
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.SystemClock
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.sfit.ctp.thosttraderapi.THOST_TE_RESUME_TYPE.THOST_TERT_RESTART
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.util.jar.Manifest
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement
import java.util.ArrayList
import java.util.List

class MainActivity : AppCompatActivity(){

    private var m_strBrokerID:String = ""
    private var m_strInvestorID:String = ""
    private var m_strUserID:String = ""
    private var m_strFrontAddr:String=""
    private var m_strProductInfo:String=""
    private var m_strAuthenCode:String=""
    private var m_strAppId:String=""
    private var m_strPassword:String=""
    private var m_strInstrumentID:String=""
    private var m_strInstrumentID1:String=""
    private var m_strExchangeID:String =""               ///CFFEX、CZCE、DCE、INE、SHFE;
    private var m_intdefaultopenvolumn:Int=0
    private var m_instrutimes:Int=0
    //定义权限请求码
    private val permissions = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.READ_PHONE_STATE,
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_BACKGROUND_LOCATION,
        android.Manifest.permission.GET_ACCOUNTS,
        android.Manifest.permission.ACCESS_WIFI_STATE,
        android.Manifest.permission.ACCESS_NETWORK_STATE)
    private val mPermissionList = ArrayList<String>()
    private val mRequestCode = 0x1//权限请求码

    //定义消息列表
    private val MSG_FRONT_SUCCESS = 0// 连接前置成功
    private val MSG_FRONT_DISCONNECT = 1// 连接前置断开
    private val MSG_LOGIN_SUCCESS = 2// 登入成功
    private val MSG_LOGIN_FAILURE = 3// 登入失败
    private val MSG_REQ_INSTRUMTN_DISPLAY = 4
    private val MSG_REQ_INSTRUMENT_FINSH = 5    // 合约查询已经返回
    private val MSG_REQ_ACCOUNT_DISPLAY = 6
    private val MSG_REQ_ACCOUNT_FINSH = 7    // 资金查询已经返回
    private val MSG_REQ_POSITION_DISPLAY = 8
    private val MSG_REQ_POSITION_FINSH = 9   // 持仓查询已经返回
    private val MSG_REQ_POSITIONDETAIL_DISPLAY = 10
    private val MSG_REQ_POSITIONDETAIL_FINSH = 11   // 持仓明细查询已经返回
    private val MSG_ORDERINSERT_OK = 12
    private val MSG_RTNORDEROPEN = 13
    private val MSG_ERROR = 14
    private val MSG_COLLECT_SUCCESS = 15
    private val MSG_COLLECT_FAILURE = 16
    private val MSG_HANQING_RECEIVED = 17
    private val MSG_RESETTLEMENT_CONFIRM = 18

    private val MSG_CONNECTION_SUCCESS=19  //连接数据库成功
    private val MSG_CLOSE_SUCCESS=20   //关闭数据库成功
    private val MSG_RTNORDER_CLOSE=21  //平仓成功
    private val MSG_UPDATE_KUIYIN=22

    //定义行情数据是否已经保存好的的控制变量
    private var isdatainsert:Boolean=false
    private var isorderinsert:Boolean=false
    private var isorderclose:Boolean=false
    //定义交易对象指针
    private var sTradeApi:CThostFtdcTraderApi?=null
    private var traderHandler:CTradeHandler? = null
    private var apppath:String=""
    //定义数据库连接对象
    private var conn1:Connection?=null

    //定义OkHTTP库的相关变量
    private val client:OkHttpClient =OkHttpClient()
    private var request:Request?=null
    private var response:Response?=null
    //定义开仓状态变量
    private var Openclosestatus:openclose=openclose()

    //定义定时器对象
    private val mTimer1 = Timer()
    private val mTimer2 = Timer()
    //定义控件变量
    private var txtopen2:TextView?=null
    private var txthighest2:TextView? = null
    private var txtlowest2:TextView? = null
    private var txtclose2:TextView? = null
    private var txtlast2:TextView? = null
    private var txtvolumn2:TextView? =null
    private var txtopeninterest2:TextView? = null
    private var txtinstruname2: TextView?=null
    private var txtdirection2:TextView?= null
    private var txtdirevolumn2:TextView? = null
    private var txtopenprice2:TextView? = null
    private var txtkuiying2:TextView?= null
    private var txtdongqianup2:TextView? = null
    private var txtdongqiandown2:TextView? = null
    private var txtzhisun2:TextView?= null
    private var butthangqing:Button?=null
    private var buttbuy:Button?= null
    private var buttsell:Button? =null
    private var buttclose:Button?=null
    private var buttquit:Button?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //获取系统路径
        apppath = this.applicationContext.filesDir.absolutePath + "/"
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //绑定界面控件
        txtopen2=findViewById(R.id.txt_open2)
        txthighest2= findViewById(R.id.txt_highest2)
        txtlowest2= findViewById(R.id.txt_lowest2)
        txtclose2= findViewById(R.id.txt_close2)
        txtlast2 = findViewById(R.id.txt_last2)
        txtvolumn2=findViewById(R.id.txt_volumn2)
        txtopeninterest2= findViewById(R.id.txt_openinterest2)
        txtinstruname2=findViewById(R.id.txt_instruname2)
        txtdirection2= findViewById(R.id.txt_direction2)
        txtdirevolumn2= findViewById(R.id.txt_direvolumn2)
        txtopenprice2= findViewById(R.id.txt_openprice2)
        txtkuiying2= findViewById(R.id.txt_kuiying2)
        txtdongqianup2= findViewById(R.id.txt_dongqianup2)
        txtdongqiandown2= findViewById(R.id.txt_dongqiandown2)
        txtzhisun2= findViewById(R.id.txt_zhisun2)
        butthangqing=findViewById(R.id.butt_hangqing)
        buttbuy= findViewById(R.id.butt_buy)
        buttsell=findViewById(R.id.butt_sell)
        buttclose=findViewById(R.id.butt_close)
        buttquit= findViewById(R.id.butt_quit)

        //连接数据库初始化各类开仓变量
        initconnectdb()

        butthangqing?.setOnClickListener(){

            initPermission()
            Thread{
                try{
                    // apppath应该是应用可读写的路径，一般使用应用内路径
                    // CreateFtdcTraderApi方法的参数不能为空
                    if(traderHandler==null || traderHandler==null)
                    {
                        sTradeApi = CThostFtdcTraderApi.CreateFtdcTraderApi(apppath)
                        traderHandler = CTradeHandler(sTradeApi, handler,m_strInstrumentID)
                        // CTPTradeHandler继承CThostFtdcTraderSpi，负责事件回调处理
                        // traderHandler不要声明为局部变量，否则会出错。
                        sTradeApi?.RegisterSpi(traderHandler)
                        sTradeApi?.RegisterFront(m_strFrontAddr)
                        var m_iResumeType: THOST_TE_RESUME_TYPE = THOST_TERT_QUICK
                        sTradeApi?.SubscribePublicTopic(THOST_TERT_QUICK)
                        sTradeApi?.SubscribePrivateTopic(THOST_TERT_QUICK)
                        sTradeApi?.Init()
                    }
                    //sTradeApi?.Join()
                }catch (e:Exception) {
                    e.printStackTrace(System.out);
                    Toast.makeText(getApplicationContext(), "初始化交易服务器错误："+e.message, Toast.LENGTH_SHORT);
                }

            }.start()
            //开启读行情定时器
            val timerTask2 = timerTask2()
            mTimer2.schedule(timerTask2, 10, 10)

            butthangqing?.setEnabled(false);

        }

        buttbuy?.setOnClickListener(){
            JYBuy()
        }

        buttsell?.setOnClickListener(){
            JYSell()
        }

        buttclose?.setOnClickListener(){
            JYClose()
        }

        buttquit?.setOnClickListener() {
             closedb()
            //finish()
        }
    }

    fun initconnectdb(){
        Thread{

            val JDBC_DRIVER = "com.mysql.jdbc.Driver"
            val DB_URL = "jdbc:mysql://46.17.42.200:3306/hangqing?user=username&password= &useUnicode=true&characterEncoding=UTF-8&autoReconnect=true"
            //用户名和密码必须自己指定
            try {
                Class.forName(JDBC_DRIVER)
                val conn= DriverManager.getConnection(DB_URL)
                var stmt1 = conn.createStatement()
                var sql: String="SELECT * FROM userinfodouyou"
                var rs1 = stmt1.executeQuery(sql)
                rs1?.first()
                m_strBrokerID=rs1.getString("BrokeID")
                m_strInvestorID=rs1.getString("InvestorID")
                m_strUserID= rs1.getString("UserID")
                m_strFrontAddr=rs1.getString("FrontAddr")
                m_strProductInfo=rs1.getString("ProductInfo")
                m_strAuthenCode=rs1.getString("AuthenCode")
                m_strAppId=rs1.getString("AppID")
                m_strPassword=rs1.getString("Password")
                m_strInstrumentID=rs1.getString("InstrumentID")
                m_strInstrumentID1=rs1.getString("InstrumentID1")
                m_strExchangeID =rs1.getString("ExchangeID")              ///CFFEX、CZCE、DCE、INE、SHFE;
                m_intdefaultopenvolumn=rs1.getInt("defaultopenvolumn")
                m_instrutimes=rs1.getInt("InstruTimes")

                sql = "SELECT * FROM douyou"
                var stmt2 = conn.createStatement()
                var rs2=stmt2?.executeQuery(sql)
                rs2?.last()
                Openclosestatus.SetDongqianup(rs2!!.getDouble("highestprice"))
                Openclosestatus.SetDongqiandown(rs2!!.getDouble("lowestprice"))
                for(i in 0 until 20) {
                    if (rs2.getDouble("highestprice") > Openclosestatus.GetDongqianup()) {
                        Openclosestatus.SetDongqianup(rs2.getDouble("highestprice"))
                    }

                    if (rs2.getDouble("lowestprice") < Openclosestatus.GetDongqiandown()) {
                        Openclosestatus.SetDongqiandown(rs2.getDouble("lowestprice"))
                    }
                    rs2.previous()
                }
                Openclosestatus.SetInstrumentID(rs1.getString("InstrumentID"))
                Openclosestatus.SetInstrumentName(rs1.getString("InstrumentName"))
                Openclosestatus.SetKuiyin(0.00)
                Openclosestatus.SetZhisun((Openclosestatus.GetDongqianup()+Openclosestatus.GetDongqiandown())/2)
                Openclosestatus.SetOpenVolumn(rs1.getInt("openvolumn"))
                Openclosestatus.SetOpenPrice(rs1.getDouble("openprice"))
                Openclosestatus.SetOpenTradecount(rs1.getInt("opentradecount"))
                Openclosestatus.SetOpenDirection(rs1.getString("opendirection"))
                Openclosestatus.SetOpenDirectionint(rs1.getInt("opendirectionint"))

                if (rs1 != null) {
                    rs1.close()
                }

                if (rs2 != null) {
                    rs2.close()
                }

                if (stmt1 != null) {
                    stmt1.close()
                }

                if (stmt2 != null) {
                    stmt2.close()
                }

                if(conn!=null){
                    conn.close()
                }

                val msg = handler.obtainMessage(MSG_CONNECTION_SUCCESS, 1, 1, "数据库连接成功")
                handler.sendMessage(msg) //发送消息

            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }.start()
    }

    fun closedb()
    {
        Thread {
            try {

                val JDBC_DRIVER = "com.mysql.jdbc.Driver"
                val DB_URL = "jdbc:mysql://46.17.42.200:3306/hangqing?user=root&password=030906zxy&useUnicode=true&characterEncoding=UTF-8&autoReconnect=true"
                Class.forName(JDBC_DRIVER)
                val conn= DriverManager.getConnection(DB_URL)
                var stmt1 = conn.createStatement()
                var sql: String =
                    "UPDATE userinfodouyou SET openvolumn=" + Openclosestatus.GetOpenVolumn().toString() +
                            ",openprice=" + Openclosestatus.GetOpenPrice().toString() +
                            ",opentradecount=" +Openclosestatus.GetOpenTradecount().toString()+
                            ",opendirection='" + Openclosestatus.GetOpenDirection().toString() + " '" +
                            ",opendirectionint=" + Openclosestatus.GetOpenDirectionint().toString() +
                            " WHERE InstrumentID='" + Openclosestatus.GetInstrumentID() + "'"
                stmt1.execute(sql)

                if (stmt1!=null)
                {
                    stmt1.close()
                }
                if (conn!=null)
                {
                    conn.close()
                }
                val msg = handler.obtainMessage(MSG_CLOSE_SUCCESS, 1, 1, "数据库关闭成功")
                handler.sendMessage(msg) //发送消息

            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    override fun onDestroy() {

        if(traderHandler!=null){
            traderHandler?.ReqUserLogout(m_strBrokerID, m_strUserID)
        }
        if(sTradeApi!=null)
        {
            sTradeApi?.Release()
        }
        if (mTimer1!=null)
        {
            mTimer1.cancel()
        }
        if (mTimer2!=null)
        {
            mTimer2.cancel()
        }

        if(conn1!=null)
        {
            conn1?.close()
        }
        super.onDestroy()
    }

    fun initPermission() {
        mPermissionList.clear()
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    this@MainActivity,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                mPermissionList.add(permission)
            }
        }
        if (!mPermissionList.isEmpty()) {
            // 后续操作...
            ActivityCompat.requestPermissions(this@MainActivity, permissions, mRequestCode)

        } else {
            Toast.makeText(this@MainActivity,"全部授予！",Toast.LENGTH_SHORT).show()
        }
    }
    //重写
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            0x1 -> for (i in 0 until grantResults.size) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED)
                {Toast.makeText(this,"您有未授予的权限，可能影响使用",Toast.LENGTH_SHORT).show()}
            }

        }
    }
    //开平仓定时器
    internal inner class timerTask1:TimerTask()
    {
        override  fun run() {
            try {
                val sdf: SimpleDateFormat = SimpleDateFormat("HH:mm")
                val nowtime: String = sdf.format(Date()).toString()
                if (nowtime == "14:58") {
                    if (isorderinsert == false) {
                        if (traderHandler!!.getLastprice() > Openclosestatus.GetDongqianup() && Openclosestatus.GetOpenTradecount() == 0 && Openclosestatus.GetOpenVolumn() == 0 && traderHandler!!.getLastprice() != 0.00) {
                            isorderinsert = true
                            JYBuy()
                        }

                        //开空仓
                        if (traderHandler!!.getLastprice() < Openclosestatus.GetDongqiandown() && Openclosestatus.GetOpenTradecount() == 0 && Openclosestatus.GetOpenVolumn() == 0 && traderHandler!!.getLastprice() != 0.00) {
                            isorderinsert = true
                            JYSell()
                        }
                    }
                    //平仓条件，如果为多仓
                    if (isorderclose == false) {
                        if (traderHandler!!.getLastprice() < Openclosestatus.GetZhisun() && Openclosestatus.GetOpenVolumn() != 0 && Openclosestatus.GetOpenDirectionint() == 1 && traderHandler!!.getLastprice() != 0.00) {
                            isorderclose = true
                            JYClose()
                        }

                        //如果为空仓
                        if (traderHandler!!.getLastprice() > Openclosestatus.GetZhisun() && Openclosestatus.GetOpenVolumn() != 0 && Openclosestatus.GetOpenDirectionint() == 2 && traderHandler!!.getLastprice() != 0.00) {
                            isorderclose = true
                            JYClose()
                        }
                    }
                }
                if (nowtime == "15:01") {
                    if (isdatainsert == false) {
                        val sdf: SimpleDateFormat = SimpleDateFormat("YYYYMMDD")
                        val date: String = sdf.format(Date()).toString()
                        val JDBC_DRIVER = "com.mysql.jdbc.Driver"
                        val DB_URL =
                            "jdbc:mysql://46.17.42.200:3306/hangqing?user=root&password=030906zxy&useUnicode=true&characterEncoding=UTF-8&autoReconnect=true"
                        Class.forName(JDBC_DRIVER)
                        val conn = DriverManager.getConnection(DB_URL)
                        val stmt2 = conn.createStatement()
                        val sql2: String = "INSERT INTO douyou VALUES('" + date + "'" +
                                "," + traderHandler!!.getOpenprice() +
                                "," + traderHandler!!.getHighestprice() +
                                "," + traderHandler!!.getLowestprice() +
                                "," + traderHandler!!.getLastprice() +
                                "," + traderHandler!!.getVolumn() +
                                "," + traderHandler!!.getOpeninterest() +
                                "," + Openclosestatus.GetDongqianup() +
                                "," + Openclosestatus.GetDongqiandown() + ")"

                        stmt2.execute(sql2)

                        if (stmt2 != null) {
                            stmt2.close()
                        }

                        if (conn != null) {
                            conn.close()
                        }
                        isdatainsert = true   //将保存控制变量设置为true
                    }
                    closedb()
                }

            }catch(e:Exception){
                e.printStackTrace()
            }catch(e:ClassNotFoundException){
                e.printStackTrace()
            }
        }

    }

    fun JYBuy() {
        traderHandler?.ReqOrderInsert(m_strBrokerID,m_strUserID,m_strInvestorID,m_strInstrumentID,
        traderHandler!!.getAskprice1(),
        m_intdefaultopenvolumn,
            1,
            1
        )
    }

    fun JYSell() {
        traderHandler?.ReqOrderInsert(m_strBrokerID,m_strUserID,m_strInvestorID,m_strInstrumentID,
            traderHandler!!.getBidprice1(),
            m_intdefaultopenvolumn,
            2,
            1
        )
    }

    fun JYClose(){
        if(Openclosestatus.GetOpenDirectionint()==1)
        {
            traderHandler?.ReqOrderInsert(m_strBrokerID,m_strUserID,m_strInvestorID,m_strInstrumentID,
                traderHandler!!.getBidprice1(),
                m_intdefaultopenvolumn,
                2,
                2)
        }

        if(Openclosestatus.GetOpenDirectionint()==2)
        {
            traderHandler?.ReqOrderInsert(m_strBrokerID,m_strUserID,m_strInvestorID,m_strInstrumentID,
                traderHandler!!.getAskprice1(),
                m_intdefaultopenvolumn,
                1,
                2
            )
        }
    }

   //定时读取行情的任务
    internal inner class timerTask2:TimerTask()
    {
        override fun run()
        {
            try {
                val tmpstr: String = "http://hq.sinajs.cn/list=" + m_strInstrumentID1
                request = Request.Builder()
                    .url(tmpstr).get().build();
                val response1:Response = client.newCall(request).execute()
                var responData1123 = response1.body().string()
                //responData1123=responData1123+" "
                if(responData1123.isNotEmpty() && !responData1123.isNullOrBlank()) {
                    var pos1 = responData1123.indexOf(',')
                    responData1123 = responData1123.substring(pos1 + 1)
                    pos1 = responData1123.indexOf(',')
                    responData1123 = responData1123.substring(pos1 + 1)

                    pos1 = responData1123.indexOf(',')
                    traderHandler?.setOpenprice(responData1123.substring(0, pos1).toDouble())//设置开盘价

                    pos1 = responData1123.indexOf(',')
                    responData1123 = responData1123.substring(pos1 + 1)
                    traderHandler?.setHighestprice(
                        responData1123.substring(
                            0,
                            pos1
                        ).toDouble()
                    )//设置最高价

                    pos1 = responData1123.indexOf(',')
                    responData1123 = responData1123.substring(pos1 + 1)
                    traderHandler?.setLowestprice(
                        responData1123.substring(
                            0,
                            pos1
                        ).toDouble()
                    )//设置最低价

                    pos1 = responData1123.indexOf(',')
                    responData1123 = responData1123.substring(pos1 + 1)
                    traderHandler?.setCloseprice(
                        responData1123.substring(
                            0,
                            pos1
                        ).toDouble()
                    )//设置前一天收盘价

                    pos1 = responData1123.indexOf(',')
                    responData1123 = responData1123.substring(pos1 + 1)
                    traderHandler?.setBidprice1(responData1123.substring(0, pos1).toDouble())//设置买一价

                    pos1 = responData1123.indexOf(',')
                    responData1123 = responData1123.substring(pos1 + 1)
                    traderHandler?.setAskprice1(responData1123.substring(0, pos1).toDouble())//设置卖一价

                    pos1 = responData1123.indexOf(',')
                    responData1123 = responData1123.substring(pos1 + 1)
                    traderHandler?.setLastprice(responData1123.substring(0, pos1).toDouble())//设置最新价

                    //跳过结算价，昨结算，买量，卖量
                    pos1 = responData1123.indexOf(',')
                    responData1123 = responData1123.substring(pos1 + 1)

                    pos1 = responData1123.indexOf(',')
                    responData1123 = responData1123.substring(pos1 + 1)

                    pos1 = responData1123.indexOf(',')
                    responData1123 = responData1123.substring(pos1 + 1)

                    pos1 = responData1123.indexOf(',')
                    responData1123 = responData1123.substring(pos1 + 1)

                    pos1 = responData1123.indexOf(',')
                    responData1123 = responData1123.substring(pos1 + 1)

                    pos1 = responData1123.indexOf(',')
                    traderHandler?.setOpeninterest(
                        responData1123.substring(
                            0,
                            pos1
                        ).toDouble()
                    )//设置持仓量
                    responData1123 = responData1123.substring(pos1 + 1)


                    pos1 = responData1123.indexOf(',')
                    traderHandler?.setVolumn(responData1123.substring(0, pos1).toInt())//设置成交量
                    responData1123 = responData1123.substring(pos1 + 1)

                    var msg = handler.obtainMessage(MSG_HANQING_RECEIVED, 1, 1, "行情接受成功")
                    handler.sendMessage(msg) //发送消息

                    MSG_UPDATE_KUIYIN
                    msg = handler.obtainMessage(MSG_UPDATE_KUIYIN, 1, 1, "行情接受成功")
                    handler.sendMessage(msg) //发送消息

                }
            }catch(e:Exception)
            {
                e.printStackTrace()
                Toast.makeText(getApplicationContext(),"行情读取错误",Toast.LENGTH_SHORT).show()
            }catch (e: ClassNotFoundException)
            {
                e.printStackTrace()
                Toast.makeText(getApplicationContext(),"行情读取错误",Toast.LENGTH_SHORT).show()
            }

        }
    }


    private val handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSG_RTNORDEROPEN -> {
                    //设置开仓状态参数
                    Openclosestatus.SetOpenTradecount(1)
                    Openclosestatus.SetOpenDirectionint(traderHandler!!.getTradeDirection().toInt())
                    Openclosestatus.SetOpenVolumn(traderHandler!!.getTradeVolumn())
                    Openclosestatus.SetOpenPrice(traderHandler!!.getTradePrice())
                    Openclosestatus.SetKuiyin(0.00)
                    //txtdirection2?.setText(traderHandler!!.getTradeDirection())
                    txtdirevolumn2?.setText(traderHandler!!.getTradeVolumn().toString())
                    txtopenprice2?.setText(traderHandler!!.getTradePrice().toString())

                    if (traderHandler!!.getTradeDirection() == "1") {
                        Openclosestatus.SetOpenDirection("多")
                        val temp1 = traderHandler!!.getLastprice() - traderHandler!!.getTradePrice()
                        val temp = "" + temp1
                        Openclosestatus.SetKuiyin(temp1)
                        txtkuiying2?.setText(temp)
                        txtdirection2?.setText("多")
                    }

                    if (traderHandler!!.getTradeDirection() == "2"){
                        Openclosestatus.SetOpenDirection("空")
                        val temp1 = traderHandler!!.getTradePrice() - traderHandler!!.getLastprice()
                        val temp = "" + temp1
                        Openclosestatus.SetKuiyin(temp1)
                        txtkuiying2?.setText(temp)
                        txtdirection2?.setText("空")
                    }

                }

                MSG_RTNORDER_CLOSE->{
                    Openclosestatus.SetOpenTradecount(0)
                    Openclosestatus.SetOpenDirection("")
                    Openclosestatus.SetOpenDirectionint(0)
                    Openclosestatus.SetOpenVolumn(0)
                    Openclosestatus.SetOpenPrice(0.00)
                    Openclosestatus.SetKuiyin(0.00)
                    txtdirection2?.setText("")
                    txtdirevolumn2?.setText("0")
                    txtopenprice2?.setText("0.00")
                }

                MSG_HANQING_RECEIVED -> {
                    txtopen2?.setText(traderHandler!!.getOpenprice().toString())
                    txthighest2?.setText(traderHandler!!.getHighestprice().toString())
                    txtlowest2?.setText(traderHandler!!.getLowestprice().toString())
                    txtclose2?.setText(traderHandler!!.getCloseprice().toString())
                    txtlast2?.setText(traderHandler!!.getLastprice().toString())
                    txtvolumn2?.setText(traderHandler!!.getVolumn().toString())
                    txtopeninterest2?.setText(traderHandler!!.getOpeninterest().toString())
                    if(traderHandler!!.getLastprice()>traderHandler!!.getCloseprice())
                    {
                        txtopen2?.setTextColor(android.graphics.Color.RED)
                        txthighest2?.setTextColor(android.graphics.Color.RED)
                        txtlowest2?.setTextColor(android.graphics.Color.RED)
                        txtclose2?.setTextColor(android.graphics.Color.RED)
                        txtlast2?.setTextColor(android.graphics.Color.RED)
                        txtvolumn2?.setTextColor(android.graphics.Color.RED)
                        txtopeninterest2?.setTextColor(android.graphics.Color.RED)
                    }

                    if(traderHandler!!.getLastprice()<traderHandler!!.getCloseprice())
                    {
                        txtopen2?.setTextColor(android.graphics.Color.GREEN)
                        txthighest2?.setTextColor(android.graphics.Color.GREEN)
                        txtlowest2?.setTextColor(android.graphics.Color.GREEN)
                        txtclose2?.setTextColor(android.graphics.Color.GREEN)
                        txtlast2?.setTextColor(android.graphics.Color.GREEN)
                        txtvolumn2?.setTextColor(android.graphics.Color.GREEN)
                        txtopeninterest2?.setTextColor(android.graphics.Color.GREEN)
                    }


                }

                MSG_UPDATE_KUIYIN->{
                    //动态实时计算亏损额
                    var temp=0.00
                    if (Openclosestatus.GetOpenDirectionint() == 1 && Openclosestatus.GetOpenVolumn() != 0) {
                        //Openclosestatus.SetOpenDirectionint(1)
                        val temp1 = traderHandler!!.getLastprice() - Openclosestatus.GetOpenPrice()
                        temp = temp1*m_instrutimes
                        Openclosestatus.SetKuiyin(temp)
                        txtkuiying2?.setText(temp.toString())

                    }

                    if (Openclosestatus.GetOpenDirectionint() == 2 && Openclosestatus.GetOpenVolumn() != 0) {
                        //Openclosestatus.SetOpenDirectionint(2)
                        val temp1 = Openclosestatus.GetOpenPrice() - traderHandler!!.getLastprice()
                        temp = temp1*m_instrutimes
                        Openclosestatus.SetKuiyin(temp)
                        txtkuiying2?.setText(temp.toString())
                    }

                    if(temp>0)
                    {
                        txtinstruname2?.setTextColor(android.graphics.Color.RED)
                        txtdirection2?.setTextColor(android.graphics.Color.RED)
                        txtdirevolumn2?.setTextColor(android.graphics.Color.RED)
                        txtopenprice2?.setTextColor(android.graphics.Color.RED)
                        txtkuiying2?.setTextColor(android.graphics.Color.RED)
                    }
                    else
                    {
                        txtinstruname2?.setTextColor(android.graphics.Color.GREEN)
                        txtdirection2?.setTextColor(android.graphics.Color.GREEN)
                        txtdirevolumn2?.setTextColor(android.graphics.Color.GREEN)
                        txtopenprice2?.setTextColor(android.graphics.Color.GREEN)
                        txtkuiying2?.setTextColor(android.graphics.Color.GREEN)
                    }

                }

                MSG_FRONT_SUCCESS->{
                    traderHandler?.ReqAuthenticate(m_strAuthenCode,m_strBrokerID,m_strUserID,m_strProductInfo,m_strAppId)
                }

                MSG_COLLECT_SUCCESS->{
                    traderHandler?.ReqUserLogin(applicationContext,m_strBrokerID,m_strUserID,m_strPassword,m_strProductInfo)
                }

                MSG_LOGIN_SUCCESS->{
                    //traderHandler?.ReqQryDepthMarketData(m_strInstrumentID,m_strExchangeID)
                    traderHandler?.ReqSettlementInfoConfirm(m_strBrokerID, m_strInvestorID)
                    //traderHandler?.ReqQryInvestor(m_strBrokerID,m_strInvestorID)
                }

                MSG_RESETTLEMENT_CONFIRM->{
                    Toast.makeText(applicationContext,"登陆成功",Toast.LENGTH_SHORT).show()
                    //开起开平仓定时器
                    val timerTask1 = timerTask1()
                    mTimer1.schedule(timerTask1, 10,15)
                }

                MSG_CONNECTION_SUCCESS->{
                    //数据连接成功以后设置界面内容
                    txtopen2?.setText("0.00")
                    txthighest2?.setText("0.00")
                    txtlowest2?.setText("0.00")
                    txtclose2?.setText("0.00")
                    txtlast2?.setText("0.00")
                    txtvolumn2?.setText("0")
                    txtopeninterest2?.setText("0")
                    txtinstruname2?.setText(Openclosestatus.GetInstrumentName().toString())  //设置界面合约名称内容
                    txtdirection2?.setText(Openclosestatus.GetOpenDirection().toString())
                    txtdirevolumn2?.setText(Openclosestatus.GetOpenVolumn().toString())
                    txtopenprice2?.setText(Openclosestatus.GetOpenPrice().toString())
                    txtkuiying2?.setText(Openclosestatus.GetKuiyin().toString())
                    txtdongqianup2?.setText(Openclosestatus.GetDongqianup().toString())
                    txtdongqiandown2?.setText(Openclosestatus.GetDongqiandown().toString())
                    txtzhisun2?.setText(Openclosestatus.GetZhisun().toString())
                }

                MSG_CLOSE_SUCCESS->{
                    finish()
                }

            }
        }
    }

    companion object {
        init {
            try {
                System.loadLibrary("thosttraderapi")
                System.loadLibrary("thosttraderapi_wrap")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


}


