package com.example.rbreak

class openclose {

    private var pInstrumentID: String = ""
    private var pInstrumentName: String = ""
    private var pdirection: String = ""
    private var pdirectionint=0
    private var pvolumn: Int =0
    private var ptradecount: Int = 0
    private var pprice: Double = 0.00
    private var pkuiyin: Double = 0.00
    private var pdongqianup: Double = 0.00
    private var pdongqiandown: Double = 0.00
    private var pdongqianup1: Double = 0.00
    private var pdongqiandown1: Double = 0.00
    private var pzhisun: Double = 0.00

    fun SetDongqianup1(pdongqianup1: Double) {
        this.pdongqianup1 = pdongqianup1
    }

    fun GetDongqianup1(): Double {
        return this.pdongqianup1
    }

    fun SetDongqiandown1(pdongqiandown1: Double) {
        this.pdongqiandown1 = pdongqiandown1
    }

    fun GetDongqiandown1(): Double {
        return this.pdongqiandown1
    }

    fun SetInstrumentName(Instrumentname: String) {
        this.pInstrumentName = Instrumentname
    }

    fun GetInstrumentName(): String {
        return this.pInstrumentName
    }

    fun SetInstrumentID(Instrument: String) {
        this.pInstrumentID = Instrument
    }

    fun GetInstrumentID(): String {
        return this.pInstrumentID
    }

    fun SetOpenDirectionint(direction: Int) {
        this.pdirectionint = direction
    }

    fun GetOpenDirectionint(): Int{
        return this.pdirectionint
    }

    fun SetOpenDirection(direction: String) {
        this.pdirection = direction
    }

    fun GetOpenDirection(): String {
        return this.pdirection
    }

    fun SetOpenVolumn(volumn: Int) {
        this.pvolumn = volumn
    }

    fun GetOpenVolumn(): Int {
        return this.pvolumn
    }

    fun SetOpenPrice(price: Double) {
        this.pprice = price
    }

    fun GetOpenPrice(): Double {
        return this.pprice
    }

    fun SetKuiyin(kuiyin: Double) {
        this.pkuiyin = kuiyin
    }

    fun GetKuiyin(): Double {
        return this.pkuiyin
    }

    fun SetDongqianup(dongqianup: Double) {
        this.pdongqianup = dongqianup
    }

    fun GetDongqianup(): Double {
        return this.pdongqianup
    }

    fun SetDongqiandown(dongqiandown: Double) {
        this.pdongqiandown = dongqiandown
    }

    fun GetDongqiandown(): Double {
        return this.pdongqiandown
    }

    fun SetZhisun(zhisun: Double) {
        this.pzhisun = zhisun
    }

    fun GetZhisun(): Double {
        return this.pzhisun
    }

    fun SetOpenTradecount(tradecount: Int) {
        this.ptradecount = tradecount
    }

    fun GetOpenTradecount(): Int {
        return this.ptradecount
    }


}