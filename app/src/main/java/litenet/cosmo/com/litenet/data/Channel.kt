package com.m6park.tt.data

data class Channel(var res:Int, var data : ArrayList<DataResult>){
    class DataResult(var film_sort : String, var name : String)
}