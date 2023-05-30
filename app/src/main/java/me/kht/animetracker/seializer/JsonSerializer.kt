package me.kht.animetracker.seializer

import kotlinx.serialization.json.Json

val JsonSerializer = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
}