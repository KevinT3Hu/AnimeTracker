package me.kht.animetracker

import kotlinx.serialization.json.Json

val JsonSerializer = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
}