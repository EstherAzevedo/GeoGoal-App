import android.os.AsyncTask
import android.util.Log
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class GeocodingService(private val apiKey: String) {

    fun getCountryName(latitude: Double, longitude: Double, callback: (String) -> Unit) {
        val url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=$latitude,$longitude&key=$apiKey"

        GeocodingTask(callback).execute(url)
    }

    private inner class GeocodingTask(private val callback: (String) -> Unit) :
        AsyncTask<String, Void, String>() {

        override fun doInBackground(vararg urls: String): String {
            try {
                val url = URL(urls[0])
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = StringBuilder()
                var line: String?

                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }

                reader.close()
                connection.disconnect()

                return response.toString()

            } catch (e: Exception) {
                Log.e("GeocodingTask", "Erro ao obter dados de geocodificação: ${e.message}")
            }

            return ""
        }

        override fun onPostExecute(result: String) {
            val countryName = parseCountryName(result)
            callback(countryName)
        }

        private fun parseCountryName(jsonResponse: String): String {
            try {
                val jsonObject = JSONObject(jsonResponse)
                val results = jsonObject.getJSONArray("results")

                if (results.length() > 0) {
                    val addressComponents = results.getJSONObject(0).getJSONArray("address_components")

                    for (i in 0 until addressComponents.length()) {
                        val component = addressComponents.getJSONObject(i)
                        val types = component.getJSONArray("types")

                        for (j in 0 until types.length()) {
                            if (types.getString(j) == "country") {
                                return component.getString("long_name")
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("GeocodingTask", "Erro ao analisar resposta de geocodificação: ${e.message}")
            }

            return "País Desconhecido"
        }
    }
}
