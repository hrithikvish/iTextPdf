package com.hrithiikvish.itextpdf

import android.app.ProgressDialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.hrithiikvish.itextpdf.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.InputStreamReader

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    companion object {
        val movieList = mutableListOf<MovieData>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.genPdfBtn.setOnClickListener {

            val progressDialog = ProgressDialog(this)
            progressDialog.setMessage("Creating your PDF...")

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    withContext(Dispatchers.Main) {
                        progressDialog.show()
                    }

                    val inputFile = assets.open("x265Rarbg500.csv")
                    val bufferedReader = BufferedReader(InputStreamReader(inputFile))

                    bufferedReader.lines().forEach { line ->
                        val lineData = line.split(",").map { it.trim() }

                        val title = lineData[0]
                        val size = "%.2f".format(lineData[1].toLong().div(1000000000.0)) + "GB"
                        val imdb = lineData[2]

                        movieList.add(MovieData(title, size, imdb))
                    }

                    ITextPdfGenerator(this@MainActivity).generatePdf()
                }
                catch (e: Exception) {
                    e.printStackTrace()
                    withContext(Dispatchers.Main) {
                        AlertDialog.Builder(this@MainActivity)
                            .setTitle("Something went wrong")
                            .setMessage(e.message)
                            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                            .show()
                    }
                }
                finally {
                    withContext(Dispatchers.Main) {
                        progressDialog.dismiss()
                    }
                }

            }

        }

    }
}