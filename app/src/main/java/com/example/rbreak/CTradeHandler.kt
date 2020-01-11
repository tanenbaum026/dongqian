package com.example.rbreak
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.sfit.ctp.thosttraderapi.*
import com.sfit.ctp.thosttraderapi.thosttradeapiConstants.*
import com.sfit.ctp.thosttraderapi.CThostFtdcQryInstrumentField
import android.R.string.no
import kotlin.math.log2


class CTradeHandler(api: CThostFtdcTraderApi?,handle:Handler?,instrumentid:String): CThostFtdcTraderSpi() {

    var theapi: CThostFtdcTraderApi? = null
    private var pRequestID: Int = 0
    //var looper: Looper? = null
    var myhandler: Handler? = null
    //定义合约列表
    private var linstrumentID:String=""
    //定义行情私有变量
    private var llastprice: Double=0.00
    private var lopenprice:Double=0.00
    private var lcloseprice:Double=0.00
    private var lhighestprice:Double=0.00
    private var llowestprice:Double = 0.00
    private var lopeninterest:Double = 0.00
    private var laskprice1:Double = 0.00
    private var lbidprice1:Double = 0.00
    private var lvolumn: Int =0
    //定义开仓状态私有成员变量
    private var pDirection: String =""
    private var pVolumn: Int = 0
    private var pPrice: Double = 0.00
    //定义消息变量
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

    init {
        theapi = api
        //looper = Looper.getMainLooper()
        //这里以主线程的Looper对象创建了handler，
        //所以，这个handler发送的Message会被传递给主线程的MessageQueue。
        //myhandler = Handler(looper)
        myhandler=handle
        linstrumentID=instrumentid
    }

    fun getOpenprice(): Double {return this.lopenprice}
    fun setOpenprice(openprice:Double){this.lopenprice=openprice}

    fun getLastprice(): Double {return this.llastprice}
    fun setLastprice(lastprice:Double) {this.llastprice=lastprice}

    fun getCloseprice(): Double {return this.lcloseprice}
    fun setCloseprice(closeprice:Double) {this.lcloseprice=closeprice}

    fun getLowestprice(): Double {return this.llowestprice}
    fun setLowestprice(lowestprice:Double) {this.llowestprice=lowestprice}

    fun getHighestprice(): Double {return this.lhighestprice}
    fun setHighestprice(highestprice:Double) {this.lhighestprice=highestprice}

    fun getOpeninterest(): Double {return this.lopeninterest}
    fun setOpeninterest(openinterest:Double) {this.lopeninterest=openinterest}

    fun getAskprice1(): Double {return this.laskprice1}
    fun setAskprice1(askprice1:Double) {this.laskprice1=askprice1}

    fun getBidprice1(): Double {return this.lbidprice1}
    fun setBidprice1(bidprice1:Double) {this.lbidprice1=bidprice1}

    fun getVolumn(): Int {return this.lvolumn}
    fun setVolumn(volumn:Int) {this.lvolumn=volumn}

    fun getTradeDirection(): String {return this.pDirection}
    fun setTradeDirection(tradedirection:String) {this.pDirection=tradedirection}

    fun getTradeVolumn(): Int {return this.pVolumn}
    fun setTradeVolumn(tradevolumn:Int) {this.pVolumn=tradevolumn}

    fun getTradePrice(): Double {return this.pPrice}
    fun setTradePrice(tradeprice:Double) {this.pPrice=tradeprice}


    fun ReqAuthenticate(AuthCode: String,BrokerId: String,UserId: String,UserProductInfo: String,appID:String) {
        val pReqAuthenticateField :CThostFtdcReqAuthenticateField= CThostFtdcReqAuthenticateField()
        pReqAuthenticateField.authCode = AuthCode
        pReqAuthenticateField.brokerID = BrokerId
        pReqAuthenticateField.userID = UserId
        pReqAuthenticateField.userProductInfo = UserProductInfo
        pReqAuthenticateField.appID=appID
        pRequestID=pRequestID+1
        theapi?.ReqAuthenticate(pReqAuthenticateField, pRequestID)
    }

    fun ReqUserLogin(context1:Context,BrokerId: String, UserId: String, Password: String,ProductInfo:String) {
        val pReqUserLoginField:CThostFtdcReqUserLoginField= CThostFtdcReqUserLoginField()
        pReqUserLoginField.brokerID = BrokerId
        pReqUserLoginField.userID = UserId
        pReqUserLoginField.password = Password
        pReqUserLoginField.userProductInfo=ProductInfo
        pRequestID=pRequestID+1
        theapi?.ReqUserLogin(context1,pReqUserLoginField, pRequestID)
    }

    fun ReqUserLogout(BrokeID:String, UserID:String) {
        val pUserLogout:CThostFtdcUserLogoutField = CThostFtdcUserLogoutField()
        pUserLogout.brokerID = BrokeID
        pUserLogout.userID = UserID
        pRequestID=pRequestID+1
        theapi?.ReqUserLogout(pUserLogout, pRequestID)
    }

    fun ReqOrderInsert(
        BrokeID: String,
        UserID: String,
        InvestID: String,
        InstrumentID: String,
        newlimitprice: Double,
        volume: Int,
        direct: Int,
        openclose: Int
    ) {
        val ord:CThostFtdcInputOrderField = CThostFtdcInputOrderField()
        ord.brokerID = BrokeID
        ord.userID = UserID
        ord.investorID = InvestID
        ord.instrumentID = InstrumentID
        ord.orderRef = ""
        ord.orderPriceType = THOST_FTDC_OPT_LimitPrice
        if (direct == 1) {
            ord.direction = THOST_FTDC_D_Buy
        }
        if (direct == 2) {
            ord.direction = THOST_FTDC_D_Sell
        }
        when (openclose) {
            1 -> ord.combOffsetFlag = THOST_FTDC_OF_Open.toString()
            2 -> ord.combOffsetFlag = THOST_FTDC_OF_Close.toString()
            3 -> ord.combOffsetFlag = THOST_FTDC_OF_ForceClose.toString()
            4 -> ord.combOffsetFlag = THOST_FTDC_OF_CloseToday.toString()
            5 -> ord.combOffsetFlag = THOST_FTDC_OF_CloseYesterday.toString()
            6 -> ord.combOffsetFlag = THOST_FTDC_OF_ForceOff.toString()
            7 -> ord.combOffsetFlag = THOST_FTDC_OF_LocalForceClose.toString()
        }
        ord.combHedgeFlag = "1"
        ord.limitPrice = newlimitprice
        ord.volumeTotalOriginal = volume
        ord.timeCondition = THOST_FTDC_TC_GFD
        ord.volumeCondition = THOST_FTDC_VC_AV
        ord.minVolume = 1
        ord.contingentCondition = THOST_FTDC_CC_Immediately
        ord.stopPrice = 0.0
        ord.forceCloseReason = THOST_FTDC_FCC_NotForceClose
        ord.isAutoSuspend = 0
        ord.businessUnit = ""
        ord.userForceClose = 0
        ord.isSwapOrder = 0
        pRequestID=pRequestID+1
        theapi?.ReqOrderInsert(ord, pRequestID)
    }

    fun ReqSettlementInfoConfirm(BrokeID: String, InvestID: String) {
        val pSettlementInfoConfirm:CThostFtdcSettlementInfoConfirmField = CThostFtdcSettlementInfoConfirmField()
        pSettlementInfoConfirm.brokerID = BrokeID
        pSettlementInfoConfirm.investorID = InvestID
        pRequestID=pRequestID+1
        theapi?.ReqSettlementInfoConfirm(pSettlementInfoConfirm, pRequestID)


    }

    fun ReqQryDepthMarketData(InstrumentID: String,ExchangeID:String)
    {
        try{
            val pQryDepthMarketData:CThostFtdcQryDepthMarketDataField=CThostFtdcQryDepthMarketDataField()
            pQryDepthMarketData.instrumentID=InstrumentID
            pQryDepthMarketData.exchangeID=ExchangeID
            //pQryDepthMarketData.setInstrumentID(InstrumentID)
            //pQryDepthMarketData.setExchangeID("DCE")
            pRequestID=pRequestID+1
            theapi?.ReqQryDepthMarketData(pQryDepthMarketData,pRequestID)
        }catch(e:SecurityException )
        {
           val s:String=e.message.toString()
        }


    }

    override fun OnRspQryDepthMarketData(
    pDepthMarketData:CThostFtdcDepthMarketDataField,
    pRspInfo:CThostFtdcRspInfoField,
    nRequestID:Int,
    bIsLast:Boolean)
    {

    }

    fun ReqQryInstrument(InstrumentID: String,ExchangeID:String)
    {
        val qryInstrument = CThostFtdcQryInstrumentField()
        //qryInstrument.instrumentID=InstrumentID
        //qryInstrument.exchangeID=ExchangeID
        pRequestID=pRequestID+1
        theapi?.ReqQryInstrument(qryInstrument, pRequestID);
    }

    override fun OnRspQryInstrument(pInstrument:CThostFtdcInstrumentField,pRspInfo:CThostFtdcRspInfoField,
    nRequestID:Int,bIsLast:Boolean)
    {

    }

    fun ReqQryInvestor(BrokeID: String,InvestID: String)
    {
        val QryInvestorField:CThostFtdcQryInvestorField=CThostFtdcQryInvestorField()
        QryInvestorField.brokerID=BrokeID
        QryInvestorField.investorID=InvestID
        pRequestID=pRequestID+1
        theapi?.ReqQryInvestor(QryInvestorField, pRequestID)
    }

    override fun OnRspQryInvestor(pInvestor:CThostFtdcInvestorField,pRspInfo:CThostFtdcRspInfoField,
    nRequestID:Int,bIsLast:Boolean)
    {

    }

    override fun OnFrontConnected() {
        //构建Message对象
        //第一个参数：是自己指定的message代号，方便在handler选择性地接收
        //第二三个参数没有什么意义
        //第四个参数需要封装的对象
        val msg = myhandler?.obtainMessage(MSG_FRONT_SUCCESS, 1, 1, "前置已经连接\n")
        myhandler?.sendMessage(msg) //发送消息
    }

    override fun OnFrontDisconnected(nReason: Int) {
        val msg = myhandler?.obtainMessage(MSG_FRONT_DISCONNECT, 1, 1, nReason)
        myhandler?.sendMessage(msg) //发送消息

    }

    override fun OnRspAuthenticate(
        pRspAuthenticateField: CThostFtdcRspAuthenticateField,
        pRspInfo: CThostFtdcRspInfoField,
        nRequestID: Int,
        bIsLast: Boolean
    ) {
        if (pRspInfo.errorID == 0) {
            val msg = myhandler?.obtainMessage(MSG_COLLECT_SUCCESS, 1, 1, "收集客户端信息成功")
            myhandler?.sendMessage(msg) //发送消息
        } else {
            val msg = myhandler?.obtainMessage(MSG_COLLECT_FAILURE, 1, 1, "收集客户端信息失败\n")
            myhandler?.sendMessage(msg) //发送消息
        }

    }

    override fun OnRspUserLogin(
        pRspUserLogin:CThostFtdcRspUserLoginField,
        pRspInfo:CThostFtdcRspInfoField,
        nRequestID:Int,
        bIsLast:Boolean)
    {
        if (pRspInfo.getErrorID() == 0) {
            val msg = myhandler?.obtainMessage(MSG_LOGIN_SUCCESS, 1, 1, "登入成功")
            myhandler?.sendMessage(msg) //发送消息
        } else {
            val msg = myhandler?.obtainMessage(MSG_LOGIN_FAILURE, 1, 1, "登入失败\n")
            myhandler?.sendMessage(msg) //发送消息
        }

    }

    override fun OnRspUserLogout(
        pUserLogout: CThostFtdcUserLogoutField,
        pRspInfo: CThostFtdcRspInfoField,
        nRequestID: Int,
        bIsLast: Boolean
    ) {

    }

    override fun OnRspOrderInsert(
        pInputOrder: CThostFtdcInputOrderField,
        pRspInfo: CThostFtdcRspInfoField,
        nRequestID: Int,
        bIsLast: Boolean
    ) {
        if (bIsLast == true) {
            //Message msg = myhandler.obtainMessage(MSG_ORDERINSERT_OK,1,1,pRspInfo.getErrorID());
            val msg = myhandler?.obtainMessage(MSG_ORDERINSERT_OK, 1, 1, pRspInfo.errorMsg)
            myhandler?.sendMessage(msg) //发送消息
        }
    }

    override fun OnRspOrderAction(
        pInputOrderAction: CThostFtdcInputOrderActionField,
        pRspInfo: CThostFtdcRspInfoField,
        nRequestID: Int,
        bIsLast: Boolean
    ) {

    }

    override fun OnRspSettlementInfoConfirm(
        pSettlementInfoConfirm: CThostFtdcSettlementInfoConfirmField,
        pRspInfo: CThostFtdcRspInfoField,
        nRequestID: Int,
        bIsLast: Boolean
    ) {
        if (pRspInfo.errorID==0) {
            //ReqQryDepthMarketData("y2005","DCE")
            val msg = myhandler?.obtainMessage(MSG_RESETTLEMENT_CONFIRM, 1, 1, pRspInfo.errorMsg)
            myhandler?.sendMessage(msg) //发送消息
        }
}

    override fun OnRspQryOrder(
        pOrder: CThostFtdcOrderField,
        pRspInfo: CThostFtdcRspInfoField,
        nRequestID: Int,
        bIsLast: Boolean
    ) {

    }

    override fun OnRtnOrder(pOrder: CThostFtdcOrderField) {

    }

    override fun OnRtnTrade(pTrade: CThostFtdcTradeField) {
        if(pTrade.instrumentID==linstrumentID)
        {
            if (pTrade.offsetFlag == THOST_FTDC_OF_Open) {
                if(pTrade.direction==THOST_FTDC_D_Buy)
                {
                    this.pDirection = "1"  //1表示买入开仓
                }
                if(pTrade.direction==THOST_FTDC_D_Sell)
                {
                    this.pDirection = "2"  //2表示卖出开仓
                }
                this.pVolumn = pTrade.volume
                this.pPrice = pTrade.price
                val msg =
                    myhandler?.obtainMessage(MSG_RTNORDEROPEN, 1, 1, "开仓成功:" + pTrade.orderSysID)
                myhandler?.sendMessage(msg) //发送消息
            }

            if (pTrade.offsetFlag == THOST_FTDC_OF_Close) {
                this.pDirection = pTrade.direction.toString()
                this.pVolumn = pTrade.volume
                this.pPrice = pTrade.price
                val msg =
                    myhandler?.obtainMessage(MSG_RTNORDER_CLOSE, 1, 1, "平仓成功:" + pTrade.orderSysID)
                myhandler?.sendMessage(msg) //发送消息
            }

        }

    }

    override fun OnErrRtnOrderInsert(
        pInputOrder: CThostFtdcInputOrderField,
        pRspInfo: CThostFtdcRspInfoField
    ) {
        val msg = myhandler?.obtainMessage(MSG_ERROR, 1, 1, "OnErrRtnOrderInsert")
        myhandler?.sendMessage(msg) //发送消息

    }

    override fun OnErrRtnOrderAction(
        pOrderAction: CThostFtdcOrderActionField,
        pRspInfo: CThostFtdcRspInfoField
    ) {

    }

    override fun OnRtnTradingNotice(pTradingNoticeInfo: CThostFtdcTradingNoticeInfoField) {

    }

    override fun OnRtnExecOrder(pExecOrder: CThostFtdcExecOrderField) {

    }




}