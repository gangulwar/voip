package com.gangulwar.cometchat

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.gangulwar.cometchat.ui.theme.CallUI
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine
import io.agora.rtc.RtcEngineConfig

import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import kotlin.random.Random

class MainActivity : ComponentActivity() {

    private lateinit var rtcEngine: RtcEngine
    private val appId = "61598d6b533142fc965e650c6fff5fa7" // Your Agora App ID
    private var channelName = ""
    private val userList = mutableStateListOf<Int>() // List to hold user IDs
    private var isAudioPermissionGranted = false // To track if audio permission is granted

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request the necessary permissions
        requestPermissions()

        // Initialize the Agora Engine once permissions are granted
        if (isAudioPermissionGranted) {
            initializeAgoraEngine()
        }

        // Set the UI content
        setContent {
            Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
                CallUI(
                    onJoin = { enteredChannel ->
                        channelName = if (enteredChannel.isEmpty()) {
                            generateRandomChannelName() // Generate random channel if none entered
                        } else {
                            enteredChannel
                        }
                        joinChannel(channelName)
                    },
                    onLeave = {
                        leaveChannel()
                    }
                )
            }
        }
    }

    private fun requestPermissions() {
        // Launch the permission request for RECORD_AUDIO
        val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            isAudioPermissionGranted = permissions[Manifest.permission.RECORD_AUDIO] ?: false
            if (!isAudioPermissionGranted) {
                // Notify the user that permission is required
                Toast.makeText(this, "Microphone permission is required for the call", Toast.LENGTH_LONG).show()
            } else {
                // Initialize Agora Engine once permission is granted
                initializeAgoraEngine()
            }
        }
        permissionLauncher.launch(arrayOf(Manifest.permission.RECORD_AUDIO))
    }

    private fun initializeAgoraEngine() {
        // Initialize Agora Engine
        val config = RtcEngineConfig().apply {
            mContext = applicationContext
            mAppId = appId
            mEventHandler = object : IRtcEngineEventHandler() {
                override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
                    super.onJoinChannelSuccess(channel, uid, elapsed)
                    Log.d("Agora", "Successfully joined channel $channel with UID $uid")
                    userList.add(uid) // Add local user to the list
                }

                override fun onUserJoined(uid: Int, elapsed: Int) {
                    super.onUserJoined(uid, elapsed)
                    Log.d("Agora", "User with UID $uid joined")
                    userList.add(uid) // Add new remote user to the list
                }

                override fun onUserOffline(uid: Int, reason: Int) {
                    super.onUserOffline(uid, reason)
                    Log.d("Agora", "User with UID $uid left")
                    userList.remove(uid) // Remove user from the list
                }
            }
        }
        rtcEngine = RtcEngine.create(config)
    }

    private fun joinChannel(channelName: String) {
        // Join the specified channel and enable audio
        rtcEngine.enableAudio() // Ensure audio is enabled
        rtcEngine.setEnableSpeakerphone(true) // Ensure audio is routed to the speakerphone
        rtcEngine.joinChannel(null, channelName, "Extra Optional Data", 0) // Join the channel with a null token
    }

    private fun leaveChannel() {
        // Leave the current channel
        rtcEngine.leaveChannel()
        userList.clear() // Clear the user list when leaving
    }

    private fun generateRandomChannelName(): String {
        // Generate a random 6-character alphanumeric channel name
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return (1..6)
            .map { chars[Random.nextInt(chars.length)] }
            .joinToString("")
    }
}
