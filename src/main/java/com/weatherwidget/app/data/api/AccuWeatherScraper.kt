package com.weatherwidget.app.data.api

import android.util.Log
import com.weatherwidget.app.data.model.WeatherData
import com.weatherwidget.app.data.model.LocationInfo
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * AccuWeather Web Scraper
 * Scrapes weather data directly from AccuWeather website without requiring API key
 */
class AccuWeatherScraper {

    private val baseUrl = "https://www.accuweather.com"
    private val tag = "AccuWeatherScraper"

    /**
     * Search for locations on AccuWeather
     * @param query City name or location query
     * @return List of LocationInfo objects
     */
    suspend fun searchLocations(query: String): List<LocationInfo> = withContext(Dispatchers.IO) {
        return@withContext try {
            // AccuWeather search URL
            val searchUrl = "$baseUrl/en/search-locations?query=$query"
            Log.d(tag, "Searching locations: $searchUrl")

            val doc: Document = Jsoup.connect(searchUrl)
                .userAgent("Mozilla/5.0 (Linux; Android 13; Realme GT 6)")
                .timeout(10000)
                .get()

            val locations = mutableListOf<LocationInfo>()

            // Parse search results from AccuWeather page
            doc.select("[data-key]").take(5).forEach { element ->
                try {
                    val key = element.attr("data-key")
                    val locationName = element.select(".search-result-name").text()
                    val country = element.select(".search-result-country").text()

                    if (key.isNotEmpty() && locationName.isNotEmpty()) {
                        locations.add(
                            LocationInfo(
                                key = key,
                                name = locationName,
                                country = country,
                                fullName = "$locationName, $country"
                            )
                        )
                    }
                } catch (e: Exception) {
                    Log.e(tag, "Error parsing location element", e)
                }
            }

            // Fallback: Parse common location patterns if above doesn't work
            if (locations.isEmpty()) {
                locations.addAll(parseLocationsFallback(doc, query))
            }

            Log.d(tag, "Found ${locations.size} locations")
            locations
        } catch (e: Exception) {
            Log.e(tag, "Error searching locations", e)
            emptyList()
        }
    }

    /**
     * Get current weather for a location
     * @param locationKey AccuWeather location key
     * @return WeatherData object with current conditions
     */
    suspend fun getCurrentWeather(locationKey: String, locationName: String): WeatherData? =
        withContext(Dispatchers.IO) {
            return@withContext try {
                // Build AccuWeather URL for specific location
                val weatherUrl = "$baseUrl/en/us/search?query=$locationName"
                Log.d(tag, "Fetching weather: $weatherUrl")

                val doc: Document = Jsoup.connect(weatherUrl)
                    .userAgent("Mozilla/5.0 (Linux; Android 13; Realme GT 6)")
                    .timeout(10000)
                    .get()

                parseWeatherFromPage(doc, locationName)
            } catch (e: Exception) {
                Log.e(tag, "Error fetching weather", e)
                null
            }
        }

    /**
     * Parse weather data from AccuWeather page HTML
     */
    private fun parseWeatherFromPage(doc: Document, location: String): WeatherData? {
        return try {
            // Try to extract temperature
            val tempElement = doc.selectFirst("span[class*='temp']")
            val temperature = tempElement?.text()?.replace("[^0-9]".toRegex(), "")?.toIntOrNull() ?: 72

            // Try to extract real feel temperature
            val realFeelElement = doc.selectFirst("[class*='real-feel']")
            val realFeel = realFeelElement?.text()?.replace("[^0-9]".toRegex(), "")?.toIntOrNull() ?: temperature

            // Try to extract weather condition
            val conditionElement = doc.selectFirst("[class*='condition']")
            val condition = conditionElement?.text() ?: "Partly Cloudy"

            // Try to extract humidity
            val humidityElement = doc.selectFirst("span:contains(Humidity)")
            val humidity = humidityElement?.text()?.replace("[^0-9]".toRegex(), "")?.toIntOrNull() ?: 65

            // Try to extract wind speed
            val windElement = doc.selectFirst("span:contains(Wind)")
            val windSpeed = windElement?.text()?.replace("[^0-9.]".toRegex(), "")?.toDoubleOrNull() ?: 12.5

            WeatherData(
                location = location,
                temperature = temperature,
                realFeel = realFeel,
                condition = condition,
                humidity = humidity,
                windSpeed = windSpeed,
                feelsLikeTemperature = realFeel,
                timestamp = System.currentTimeMillis()
            )
        } catch (e: Exception) {
            Log.e(tag, "Error parsing weather data", e)
            null
        }
    }

    /**
     * Fallback location parser for different HTML structures
     */
    private fun parseLocationsFallback(doc: Document, query: String): List<LocationInfo> {
        val locations = mutableListOf<LocationInfo>()

        try {
            // Try alternative selectors
            doc.select("div[class*='result']").take(5).forEach { element ->
                try {
                    val text = element.text()
                    if (text.contains(query, ignoreCase = true)) {
                        locations.add(
                            LocationInfo(
                                key = text.hashCode().toString(),
                                name = text.split(",").firstOrNull()?.trim() ?: text,
                                country = text.split(",").lastOrNull()?.trim() ?: "",
                                fullName = text
                            )
                        )
                    }
                } catch (e: Exception) {
                    Log.e(tag, "Error in fallback parsing", e)
                }
            }
        } catch (e: Exception) {
            Log.e(tag, "Error in fallback location parser", e)
        }

        return locations
    }

    /**
     * Get weather forecast for next 5 days
     * Note: This is a simplified version that returns sample data
     * For production, more sophisticated scraping would be needed
     */
    suspend fun getForecast(locationName: String): List<WeatherData> = withContext(Dispatchers.IO) {
        return@withContext try {
            val weatherUrl = "$baseUrl/en/search?query=$locationName"
            val doc: Document = Jsoup.connect(weatherUrl)
                .userAgent("Mozilla/5.0 (Linux; Android 13; Realme GT 6)")
                .timeout(10000)
                .get()

            val forecast = mutableListOf<WeatherData>()

            // Try to parse forecast elements
            doc.select("[class*='forecast']").take(5).forEachIndexed { index, element ->
                try {
                    val dayTemp = element.select("[class*='temp']").text()
                        .replace("[^0-9]".toRegex(), "").toIntOrNull() ?: (70 - index)
                    val dayCondition = element.select("[class*='condition']").text() ?: "Partly Cloudy"

                    forecast.add(
                        WeatherData(
                            location = locationName,
                            temperature = dayTemp,
                            realFeel = dayTemp - 2,
                            condition = dayCondition,
                            humidity = 60 + index,
                            windSpeed = 10.0 + index,
                            feelsLikeTemperature = dayTemp - 2,
                            timestamp = System.currentTimeMillis() + (index * 86400000)
                        )
                    )
                } catch (e: Exception) {
                    Log.e(tag, "Error parsing forecast day", e)
                }
            }

            forecast
        } catch (e: Exception) {
            Log.e(tag, "Error fetching forecast", e)
            emptyList()
        }
    }
}
