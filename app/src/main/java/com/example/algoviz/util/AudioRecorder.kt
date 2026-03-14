package com.example.algoviz.util

import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import java.io.File
import java.io.FileOutputStream
import java.io.RandomAccessFile

class AudioRecorder(private val context: Context) {
    private var audioRecord: AudioRecord? = null
    private var isRecording = false
    private var audioFile: File? = null
    private var recordingThread: Thread? = null

    private val sampleRate = 16000
    private val channelConfig = AudioFormat.CHANNEL_IN_MONO
    private val audioEncoding = AudioFormat.ENCODING_PCM_16BIT

    fun startRecording(): File? {
        val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioEncoding)
        if (bufferSize == AudioRecord.ERROR || bufferSize == AudioRecord.ERROR_BAD_VALUE) {
            return null
        }

        audioFile = File(context.cacheDir, "audio_record_${System.currentTimeMillis()}.wav")

        try {
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                channelConfig,
                audioEncoding,
                bufferSize
            )

            if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
                return null
            }

            audioRecord?.startRecording()
            isRecording = true

            // Write raw PCM with WAV header in background thread
            recordingThread = Thread {
                writeWavFile(bufferSize)
            }
            recordingThread?.start()

        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

        return audioFile
    }

    fun stopRecording(): File? {
        isRecording = false
        try {
            recordingThread?.join(2000) // Wait for writing to finish
            audioRecord?.stop()
            audioRecord?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        audioRecord = null
        recordingThread = null
        return audioFile
    }

    private fun writeWavFile(bufferSize: Int) {
        val data = ByteArray(bufferSize)
        val outputStream = FileOutputStream(audioFile)

        // Write placeholder WAV header (44 bytes)
        val header = ByteArray(44)
        outputStream.write(header)

        var totalDataSize: Long = 0

        while (isRecording) {
            val read = audioRecord?.read(data, 0, bufferSize) ?: 0
            if (read > 0) {
                outputStream.write(data, 0, read)
                totalDataSize += read
            }
        }

        outputStream.close()

        // Go back and fill in the WAV header with correct sizes
        updateWavHeader(audioFile!!, totalDataSize)
    }

    private fun updateWavHeader(file: File, totalDataSize: Long) {
        val totalFileSize = totalDataSize + 36 // header - 8 bytes for RIFF chunk
        val byteRate = (sampleRate * 1 * 16 / 8).toLong()
        val blockAlign = (1 * 16 / 8)

        try {
            val raf = RandomAccessFile(file, "rw")

            // RIFF header
            raf.seek(0)
            raf.writeBytes("RIFF")
            raf.write(intToByteArray(totalFileSize.toInt()), 0, 4)
            raf.writeBytes("WAVE")

            // fmt subchunk
            raf.writeBytes("fmt ")
            raf.write(intToByteArray(16), 0, 4) // Sub chunk size
            raf.write(shortToByteArray(1), 0, 2) // Audio format (PCM = 1)
            raf.write(shortToByteArray(1), 0, 2) // Num channels (Mono = 1)
            raf.write(intToByteArray(sampleRate), 0, 4) // Sample rate
            raf.write(intToByteArray(byteRate.toInt()), 0, 4) // Byte rate
            raf.write(shortToByteArray(blockAlign.toShort().toInt()), 0, 2) // Block align
            raf.write(shortToByteArray(16), 0, 2) // Bits per sample

            // data subchunk
            raf.writeBytes("data")
            raf.write(intToByteArray(totalDataSize.toInt()), 0, 4)

            raf.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun intToByteArray(value: Int): ByteArray {
        return byteArrayOf(
            (value and 0xFF).toByte(),
            ((value shr 8) and 0xFF).toByte(),
            ((value shr 16) and 0xFF).toByte(),
            ((value shr 24) and 0xFF).toByte()
        )
    }

    private fun shortToByteArray(value: Int): ByteArray {
        return byteArrayOf(
            (value and 0xFF).toByte(),
            ((value shr 8) and 0xFF).toByte()
        )
    }
}
