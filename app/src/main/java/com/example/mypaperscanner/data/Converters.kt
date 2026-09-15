package com.example.mypaperscanner.data

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromSourceType(value: DocumentSourceType): String {
        return value.name
    }

    @TypeConverter
    fun toSourceType(value: String): DocumentSourceType {
        return DocumentSourceType.valueOf(value)
    }
}
