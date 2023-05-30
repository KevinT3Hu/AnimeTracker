package me.kht.animetracker.dataclient.db

import androidx.room.TypeConverter

class Converter {

    @TypeConverter
    fun mutableSetToString(set: MutableSet<Float>): String {
        return set.joinToString(",")
    }

    @TypeConverter
    fun stringToMutableSet(string: String): MutableSet<Float> {
        return if (string.isEmpty()) mutableSetOf()
        else string.split(",").map { it.toFloat() }.toMutableSet()
    }
}