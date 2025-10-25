package ie.setu.travelnotes.helpers

import android.content.Context
import timber.log.Timber.e
import java.io.*

fun write(context: Context, fileName: String, jsonString: String) {
    try {
        val outputStreamWriter = OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE))
        outputStreamWriter.write(jsonString)
        outputStreamWriter.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun read(context: Context, fileName: String): String? {
    var jsonString: String? = null
    try {
        val inputStream = context.openFileInput(fileName)
        if (inputStream != null) {
            val inputStreamReader = InputStreamReader(inputStream)
            val bufferedReader = BufferedReader(inputStreamReader)
            val partialJsonString = StringBuilder()
            var readString: String? = bufferedReader.readLine()
            while (readString != null) {
                partialJsonString.append(readString)
                readString = bufferedReader.readLine()
            }
            inputStream.close()
            jsonString = partialJsonString.toString()
        }
    } catch (e: FileNotFoundException) {
        return null
    } catch (e: IOException) {
        e("cannot read file: %s", e.toString())
    }
    return jsonString
}

fun exists(context: Context, fileName: String): Boolean {
    val file = context.getFileStreamPath(fileName)
    return file.exists()
}