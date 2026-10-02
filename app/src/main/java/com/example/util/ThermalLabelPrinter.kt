package com.example.util

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.PrintManager
import android.print.pdf.PrintedPdfDocument
import androidx.core.content.FileProvider
import com.example.data.model.RepairRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

object ThermalLabelPrinter {

    private val SPP_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    /**
     * Generates a 100% black & white high-contrast 50x30mm thermal label bitmap (approx 480x288 px).
     */
    fun createLabelBitmap(
        record: RepairRecord,
        workshopName: String = "Phone Repair Register",
        workshopPhone: String = ""
    ): Bitmap {
        val width = 480
        val height = 288
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Clear with crisp solid white
        canvas.drawColor(Color.WHITE)

        val paintText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            typeface = Typeface.DEFAULT_BOLD
        }
        val paintMono = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            typeface = Typeface.MONOSPACE
        }
        val paintFill = Paint().apply {
            color = Color.BLACK
            style = Paint.Style.FILL
        }

        // Outer margin border
        val borderPaint = Paint().apply {
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeWidth = 2f
        }
        canvas.drawRect(4f, 4f, width - 4f, height - 4f, borderPaint)

        // Top Row: Code Badge (#001) in large bold monospace
        paintMono.textSize = 38f
        paintMono.isFakeBoldText = true
        val codeText = "#${record.code}"
        canvas.drawText(codeText, 16f, 46f, paintMono)

        // Date & Time on top right
        val formattedDate = formatTimestampShort(record.receivedAt)
        paintMono.textSize = 18f
        paintMono.isFakeBoldText = false
        val dateWidth = paintMono.measureText(formattedDate)
        canvas.drawText(formattedDate, width - 16f - dateWidth, 38f, paintMono)

        // Divider line
        canvas.drawLine(12f, 56f, width - 12f, 56f, borderPaint)

        // Right side: QR Code
        val qrSize = 130
        val qrBitmap = QrCodeGenerator.generateQrBitmap(record.qrPayload(), qrSize)
        val qrX = width - qrSize - 16f
        val qrY = 66f
        if (qrBitmap != null) {
            canvas.drawBitmap(qrBitmap, qrX, qrY, null)
        }

        // Left side details (Width available is ~ 300px)
        // Device info
        paintText.textSize = 24f
        paintText.isFakeBoldText = true
        val deviceStr = "${record.brand} ${record.model}".take(20)
        canvas.drawText(deviceStr, 16f, 88f, paintText)

        // Color badge
        paintMono.textSize = 17f
        paintMono.isFakeBoldText = false
        canvas.drawText("Color: ${record.color}", 16f, 114f, paintMono)

        // Client info: Name
        paintText.textSize = 21f
        paintText.isFakeBoldText = true
        val clientStr = record.clientName.take(18)
        canvas.drawText(clientStr, 16f, 146f, paintText)

        // Client info: Phone (Prominent bold monospace)
        paintMono.textSize = 21f
        paintMono.isFakeBoldText = true
        val phoneStr = MoroccanPhoneUtils.formatMoroccanPhone(record.clientPhone)
        canvas.drawText(phoneStr, 16f, 174f, paintMono)

        // Issue description
        paintMono.textSize = 17f
        paintMono.isFakeBoldText = false
        val issueStr = record.problem.take(24)
        canvas.drawText(issueStr, 16f, 202f, paintMono)

        // Bottom row: Inverted price badge & workshop footer
        // Solid black price box
        val priceBoxRect = RectF(14f, 222f, 190f, 274f)
        canvas.drawRoundRect(priceBoxRect, 6f, 6f, paintFill)

        // Price text in white
        val paintWhite = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            typeface = Typeface.MONOSPACE
            textSize = 28f
            isFakeBoldText = true
        }
        val priceStr = "${record.price.toInt()} DH"
        val priceWidth = paintWhite.measureText(priceStr)
        canvas.drawText(priceStr, priceBoxRect.centerX() - (priceWidth / 2f), priceBoxRect.centerY() + 10f, paintWhite)

        // Label footer text
        paintMono.textSize = 13f
        paintMono.color = Color.BLACK
        val footerTitle = if (workshopName.isNotBlank()) workshopName.take(24) else "Phone Repair Register"
        val footerSub = if (workshopPhone.isNotBlank()) workshopPhone else "Ne pas retirer / لا تنزع الملصق"
        canvas.drawText(footerTitle, 204f, 244f, paintMono)
        canvas.drawText(footerSub, 204f, 266f, paintMono)

        return bitmap
    }

    private fun formatTimestampShort(iso: String): String {
        return try {
            val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
            val outFormat = SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault())
            val date = isoFormat.parse(iso) ?: Date()
            outFormat.format(date)
        } catch (_: Exception) {
            val simple = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault())
            simple.format(Date())
        }
    }

    /**
     * Standard Android PrintManager printing (Label/Thermal/PDF)
     */
    fun printViaPrintManager(
        context: Context,
        record: RepairRecord,
        workshopName: String = "Phone Repair Register",
        workshopPhone: String = ""
    ) {
        val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager ?: return
        val jobName = "RepairLabel_${record.code}"

        val adapter = object : PrintDocumentAdapter() {
            private var pdfDocument: PrintedPdfDocument? = null

            override fun onLayout(
                oldAttributes: PrintAttributes?,
                newAttributes: PrintAttributes?,
                cancellationSignal: CancellationSignal?,
                callback: LayoutResultCallback?,
                extras: Bundle?
            ) {
                if (cancellationSignal?.isCanceled == true) {
                    callback?.onLayoutCancelled()
                    return
                }

                pdfDocument = PrintedPdfDocument(context, newAttributes ?: PrintAttributes.Builder().build())
                val info = PrintDocumentInfo.Builder(jobName)
                    .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                    .setPageCount(1)
                    .build()
                callback?.onLayoutFinished(info, true)
            }

            override fun onWrite(
                pages: Array<out PageRange>?,
                destination: ParcelFileDescriptor?,
                cancellationSignal: CancellationSignal?,
                callback: WriteResultCallback?
            ) {
                val doc = pdfDocument ?: return
                try {
                    val page = doc.startPage(0)
                    if (cancellationSignal?.isCanceled == true) {
                        callback?.onWriteCancelled()
                        doc.close()
                        return
                    }

                    val canvas = page.canvas
                    val labelBitmap = createLabelBitmap(record, workshopName, workshopPhone)
                    val destRect = Rect(0, 0, canvas.width, (canvas.width * 288f / 480f).toInt())
                    canvas.drawBitmap(labelBitmap, null, destRect, null)

                    doc.finishPage(page)
                    FileOutputStream(destination?.fileDescriptor).use { output ->
                        doc.writeTo(output)
                    }
                    callback?.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
                } catch (e: Exception) {
                    callback?.onWriteFailed(e.message)
                } finally {
                    doc.close()
                    pdfDocument = null
                }
            }
        }

        val attributes = PrintAttributes.Builder()
            .setColorMode(PrintAttributes.COLOR_MODE_MONOCHROME)
            .setMediaSize(PrintAttributes.MediaSize.ISO_A6)
            .build()

        printManager.print(jobName, adapter, attributes)
    }

    /**
     * Converts a monochrome bitmap into standard ESC/POS raster bit image command (GS v 0).
     * Works on 99% of portable 58mm / 80mm Bluetooth thermal printers.
     */
    fun createEscPosRasterBytes(bitmap: Bitmap): ByteArray {
        val width = bitmap.width
        val height = bitmap.height
        val widthBytes = (width + 7) / 8

        val output = ByteArrayOutputStream()

        // Initialize printer (ESC @)
        output.write(byteArrayOf(0x1B, 0x40))

        // Center alignment (ESC a 1)
        output.write(byteArrayOf(0x1B, 0x61, 0x01))

        // ESC/POS GS v 0 (Raster image)
        // Format: GS v 0 m xL xH yL yH d1...dk
        val xL = (widthBytes and 0xFF).toByte()
        val xH = ((widthBytes shr 8) and 0xFF).toByte()
        val yL = (height and 0xFF).toByte()
        val yH = ((height shr 8) and 0xFF).toByte()

        output.write(byteArrayOf(0x1D, 0x76, 0x30, 0x00, xL, xH, yL, yH))

        // Image data
        for (y in 0 until height) {
            for (xByte in 0 until widthBytes) {
                var byteVal = 0
                for (b in 0 until 8) {
                    val x = xByte * 8 + b
                    if (x < width) {
                        val pixel = bitmap.getPixel(x, y)
                        val r = (pixel shr 16) and 0xFF
                        val g = (pixel shr 8) and 0xFF
                        val blue = pixel and 0xFF
                        val lum = (r * 299 + g * 587 + blue * 114) / 1000
                        if (lum < 128) {
                            byteVal = byteVal or (1 shl (7 - b))
                        }
                    }
                }
                output.write(byteVal)
            }
        }

        // Feed 3 lines & cut paper
        output.write(byteArrayOf(0x1B, 0x64, 0x03))
        output.write(byteArrayOf(0x1D, 0x56, 0x41, 0x00))

        return output.toByteArray()
    }

    /**
     * Lists paired bluetooth devices that may be thermal printers
     */
    @SuppressLint("MissingPermission")
    fun getPairedBluetoothPrinters(): List<BluetoothDevice> {
        val adapter = BluetoothAdapter.getDefaultAdapter() ?: return emptyList()
        return try {
            adapter.bondedDevices.toList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    /**
     * Sends ESC/POS raster print job to a paired bluetooth printer in background
     */
    @SuppressLint("MissingPermission")
    suspend fun printToBluetoothDevice(device: BluetoothDevice, bitmap: Bitmap): Result<Unit> = withContext(Dispatchers.IO) {
        var socket: BluetoothSocket? = null
        try {
            socket = device.createRfcommSocketToServiceRecord(SPP_UUID)
            socket.connect()
            val bytes = createEscPosRasterBytes(bitmap)
            val outputStream = socket.outputStream
            outputStream.write(bytes)
            outputStream.flush()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        } finally {
            try {
                socket?.close()
            } catch (_: Exception) {
            }
        }
    }

    /**
     * Shares high-res label PNG directly to WhatsApp or another app
     */
    fun shareLabelImage(
        context: Context,
        record: RepairRecord,
        workshopName: String = "Phone Repair Register",
        workshopPhone: String = ""
    ) {
        try {
            val bitmap = createLabelBitmap(record, workshopName, workshopPhone)
            val cachePath = File(context.cacheDir, "labels")
            cachePath.mkdirs()
            val file = File(cachePath, "sticker_${record.code}.png")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }

            val contentUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, contentUri)
                putExtra(Intent.EXTRA_TEXT, "Sticker #${record.code} - ${record.brand} ${record.model} (${record.clientName})")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share Sticker #${record.code}"))
        } catch (_: Exception) {
        }
    }
}
