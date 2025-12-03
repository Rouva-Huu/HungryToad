package com.example.hungrytoad.ui.data

import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(name = "ValCurs", strict = false)
data class ValCursResponse @JvmOverloads constructor(
    @field:ElementList(entry = "Record", inline = true, required = false)
    var records: List<Record> = emptyList()
)

@Root(name = "Record", strict = false)
data class Record @JvmOverloads constructor(
    @field:Element(name = "Buy", required = false)
    var buy: String = "0",

    @field:Element(name = "Sell", required = false)
    var sell: String = "0"
)

interface CbrApiService {
    @retrofit2.http.GET("scripts/xml_metall.asp")
    suspend fun getGoldPrices(
        @retrofit2.http.Query("date_req1") dateReq1: String,
        @retrofit2.http.Query("date_req2") dateReq2: String
    ): ValCursResponse
}