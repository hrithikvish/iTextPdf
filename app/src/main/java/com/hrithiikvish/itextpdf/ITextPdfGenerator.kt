package com.hrithiikvish.itextpdf

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import com.hrithiikvish.itextpdf.MainActivity.Companion.movieList
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.property.UnitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class ITextPdfGenerator(private val context: Context) {

    suspend fun generatePdf() {

        try {

            val directory = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            if(directory != null && !directory.exists()) {
                directory.mkdirs()
            }

            val pdfFile = File(directory, "test-pdf.pdf")
            val pdfWriter = PdfWriter(pdfFile)

            PdfDocument(pdfWriter).use { pdfDocument ->
                Document(pdfDocument, PageSize.A4).use { doc ->

                    doc.add(Paragraph("x265-RARBG Movies List:"))

                    val moviesTable = Table(UnitValue.createPercentArray(floatArrayOf(5f, 1f, 1f))).useAllAvailableWidth().setMarginTop(5f)

                    moviesTable.addCell(Cell().add(Paragraph("Title")).setPadding(5f))
                    moviesTable.addCell(Cell().add(Paragraph("Size")).setPadding(5f))
                    moviesTable.addCell(Cell().add(Paragraph("IMDB ID")).setPadding(5f))

                    movieList.forEach { movie ->
                        moviesTable.addCell(
                            Cell().add(
                                Paragraph(movie.title.substringBefore(".1080p").split(".").joinToString(" "))
                            ).setPadding(5f))
                        moviesTable.addCell(Cell().add(Paragraph(movie.size)).setPadding(5f))
                        moviesTable.addCell(Cell().add(Paragraph(movie.imdb)).setPadding(5f))
                    }

                    doc.add(moviesTable)

                }
            }

            withContext(Dispatchers.Main) {
                openPdf(pdfFile)
            }

        }
        catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                AlertDialog.Builder(context)
                    .setTitle("Something went wrong")
                    .setMessage(e.message)
                    .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                    .show()
            }
        }

    }

    @SuppressLint("ObsoleteSdkInt")
    private fun openPdf(pdfFile: File) {

        val uri = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(context, "${context.packageName}.provider", pdfFile)
        }
        else {
            Uri.fromFile(pdfFile)
        }

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val chooser = Intent.createChooser(intent, "Choose an app to view PDF")
        context.startActivity(chooser)

    }

}