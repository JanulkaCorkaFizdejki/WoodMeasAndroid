package com.mobile.woodmeas.helpers

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.mobile.woodmeas.BuildConfig
import java.io.File

object EmailManager {

    fun sendWithMultipleAttachments(
        context: Context,
        fileAttachments: List<String> = emptyList()
    ): Intent {

        return Intent(Intent.ACTION_SEND_MULTIPLE).apply {
            type = "text/html"

            // Configure attachments
            val attachments =
                fileAttachments.map { File(it) }.filter { it.exists() && !it.isDirectory }.map {
                    FileProvider.getUriForFile(
                        context,
                        "${BuildConfig.APPLICATION_ID}.provider",
                        it
                    )
                }.toList()

            if (attachments.isNotEmpty())
                putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(attachments))
        }
    }

    fun getTextBody(note: String, footer: String): String {
        return """
            $note
            
            ----
            $footer
        """.trimIndent()
    }

    fun getTextHtml(note: String, footer: String): String {
        return """
            <div>
            <p style="padding-top: 10px; padding-bottom: 10px">$note</p>
            <hr>
            <p style="padding-top: 10px; padding-bottom: 10px; font-size: 10px">$footer</p>
            </div>
        """.trimIndent()
    }
    fun getTextBodyFooter(footer: String): String {
        return """
            
            ----
            $footer
        """.trimIndent()
    }

    fun getTextHtmlFooter(footer: String): String {
        return """
            <div style="padding-top: 10px">
            <hr>
            <p style="padding-top: 10px; padding-bottom: 10px; font-size: 10px">$footer</p>
            </div>
        """.trimIndent()
    }
}