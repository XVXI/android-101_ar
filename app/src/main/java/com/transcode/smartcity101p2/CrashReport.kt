package com.transcode.smartcity101p2

import android.os.Environment
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.*

class CrashReport : Thread.UncaughtExceptionHandler {
    private val defaultUEH = Thread.getDefaultUncaughtExceptionHandler()
    private var deleteCount = 0
    val CRASH_LIMIT_SIZE = 1000000

    override fun uncaughtException(t: Thread?, e: Throwable?) {
        //Write a printable representation of this Throwable
        //The StringWriter gives the lock used to synchronize access to this writer.
        val stringBuffSync = StringWriter()
        val printWriter = PrintWriter(stringBuffSync)
        e?.printStackTrace(printWriter)
        val stacktrace = stringBuffSync.toString()
        printWriter.close()

        writeToFile(stacktrace)

        //Used only to prevent from any code getting executed.
        // Not needed in this example
        defaultUEH.uncaughtException(t, e)
    }

    private fun writeToFile(currentStacktrace: String) {
//        Log.e("ExceptionHandler", currentStacktrace)
        try {
            val dir = getFileCrashLocation()

            if (getDirSize(dir) < CRASH_LIMIT_SIZE) {

                if (!dir.exists()) {
                    dir.mkdirs()
                }

                val dateFormat = SimpleDateFormat("yyyyMMdd_HH_mm_ss")
                val date = Date()
                val filename = dateFormat.format(date) + ".txt"

                // Write the file into the folder
                val reportFile = File(dir, filename)
                val fileWriter = FileWriter(reportFile)
                fileWriter.append(currentStacktrace)
                fileWriter.flush()
                fileWriter.close()
                deleteCount = 0
            } else if (deleteCount < 5) {
                deleteRecursive(dir)
                writeToFile(currentStacktrace)
                deleteCount++
            }
        } catch (e: Exception) {
//            Log.e("ExceptionHandler", e.message)
        }

    }

    private fun getFileCrashLocation(): File {
        val path = Environment.getExternalStorageDirectory()
        return File(path, "android/CitizenCrashReports")
    }

    private fun getDirSize(dir: File): Long {
        if (dir.exists()) {
            var result: Long = 0
            val fileList = dir.listFiles()
            for (i in fileList.indices) {
                // Recursive call if it's a directory
                if (fileList[i].isDirectory) {
                    result += getDirSize(fileList[i])
                } else {
                    // Sum the file size in bytes
                    result += fileList[i].length()
                }
            }
            return result // return the file size
        }
        return 0
    }

    @Throws(Exception::class)
    private fun deleteRecursive(fileOrDirectory: File) {
        if (fileOrDirectory.isDirectory) {
            for (child in fileOrDirectory.listFiles()) {
                deleteRecursive(child)
            }
        }
        fileOrDirectory.delete()
    }
}