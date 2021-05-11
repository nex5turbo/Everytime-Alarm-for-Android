package com.example.myapplication2.utils

import kotlin.collections.ArrayList

class AlarmFunction {
    fun splitArr(array:IntArray):ArrayList<ArrayList<Int>>{
        val rt = ArrayList<ArrayList<Int>>()
        val monday = ArrayList<Int>()
        val tuesday = ArrayList<Int>()
        val thursday = ArrayList<Int>()
        val wednesday = ArrayList<Int>()
        val friday = ArrayList<Int>()
        var count = 0
        if(array.size%2 == 0) {
            while (true) {
                if (count == (array.size / 5) - 1) {
                    monday.add(array[count])
                    count++
                    break
                }
                monday.add(array[count])
                count++
            }
            while (true) {
                if (count == ((array.size / 5) * 2) - 1) {
                    tuesday.add(array[count])
                    count++
                    break
                }
                tuesday.add(array[count])
                count++
            }
            while (true) {
                if (count == ((array.size / 5) * 3) - 1) {
                    wednesday.add(array[count])
                    count++
                    break
                }
                wednesday.add(array[count])
                count++
            }
            while (true) {
                if (count == ((array.size / 5) * 4) - 1) {
                    thursday.add(array[count])
                    count++
                    break
                }
                thursday.add(array[count])
                count++
            }
            while (true) {
                if (count == (array.size) - 1) {
                    friday.add(array[count])
                    count++
                    break
                }
                friday.add(array[count])
                count++
            }
        }else{
            while (true) {
                if (count == (array.size / 5) - 2) {
                    monday.add(array[count])
                    count++
                    break
                }
                monday.add(array[count])
                count++
            }
            count++
            while (true) {
                if (count == ((array.size / 5) * 2) - 2) {
                    tuesday.add(array[count])
                    count++
                    break
                }
                tuesday.add(array[count])
                count++
            }
            count++
            while (true) {
                if (count == ((array.size / 5) * 3) - 2) {
                    wednesday.add(array[count])
                    count++
                    break
                }
                wednesday.add(array[count])
                count++
            }
            count++
            while (true) {
                if (count == ((array.size / 5) * 4) - 2) {
                    thursday.add(array[count])
                    count++
                    break
                }
                thursday.add(array[count])
                count++
            }
            count++
            while (true) {
                if (count == (array.size) - 2) {
                    friday.add(array[count])
                    count++
                    break
                }
                friday.add(array[count])
                count++
            }
        }
        rt.add(monday)
        rt.add(tuesday)
        rt.add(wednesday)
        rt.add(thursday)
        rt.add(friday)
        return rt
    }

    fun getFirstEachDay(arrList:ArrayList<ArrayList<Int>>):Array<Int>{
        val rtArray = Array<Int>(5){-1}
        var count = 0
        for(temp in arrList[0]){
            if(temp != 0){
                rtArray[0] = count
                break
            }
            count++
        }
        count = 0
        for(temp in arrList[1]){
            if(temp != 0){
                rtArray[1] = count
                break
            }
            count++
        }
        count = 0
        for(temp in arrList[2]){
            if(temp != 0){
                rtArray[2] = count
                break
            }
            count++
        }
        count = 0
        for(temp in arrList[3]){
            if(temp != 0){
                rtArray[3] = count
                break
            }
            count++
        }
        count = 0
        for(temp in arrList[4]){
            if(temp != 0){
                rtArray[4] = count
                break
            }
        }
        return rtArray
    }

}