package com.yohan.esg_app

import android.content.res.AssetManager
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

object CSVReader {

    //CSVReader.readCSV("/data/data/com.yohan.esg_app/files/"+"recipe.csv")
    var recipe:MutableList<Recipe>?=null
    var baseUrl="http://43.201.132.87:5000/"
    var innerUrl=""
    val defaultUrl="http://43.201.132.87:5000/"
    val defaultInnerUrl=""
    @Throws(IOException::class)
    fun readCSV_asset(inputStream:InputStream): MutableList<Recipe> {

        val br = BufferedReader(InputStreamReader(inputStream))
        val list: MutableList<Array<String>> = ArrayList()
        var temp: String=""
        var tempList: Array<String>
        while (br.readLine()?.also { temp = it } != null) {
            tempList = temp.split(",").toTypedArray()
            list.add(tempList)
        }

        val output= mutableListOf<Recipe>()
        for (item in list) {
            output.add(Recipe(item.get(0),item.get(1),item.get(2),item.get(3),item.get(4)))
        }
        return output
    }


    @Throws(IOException::class)
    fun readCSV_file(filePath:String): MutableList<Recipe> {
        val csv = File(filePath)

        val br = BufferedReader(FileReader(csv))
        val list: MutableList<Array<String>> = ArrayList()
        var temp: String=""
        var tempList: Array<String>
        while (br.readLine()?.also { temp = it } != null) {
            tempList = temp.split(",").toTypedArray()
            list.add(tempList)
        }

        val output= mutableListOf<Recipe>()
        for (item in list) {
            output.add(Recipe(item.get(0),item.get(1),item.get(2),item.get(3),item.get(4)))
        }
        return output
    }
}

//fun main() {
//
//    val list=CSVReader.recipe.subList(61,80)
//    var cnt=61
//    for (item in list) {
//        println("(${cnt})${item.idx}, ${item.url}, ${item.imageUrl}, ${item.title}, ${item.content}")
//        cnt++
//    }
//}