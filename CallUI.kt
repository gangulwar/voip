package com.gangulwar.cometchat.ui.theme

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp


@Composable
fun CallUI(
    onJoin: (String) -> Unit,
    onLeave: () -> Unit
) {
    var isInCall by remember { mutableStateOf(false) }
    var channelName by remember { mutableStateOf("") }
    val usersInChannel = remember { mutableStateListOf<Int>() } // Mutable list of user IDs

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        if (isInCall) {
            Text("In a call", modifier = Modifier.padding(16.dp))
            Button(
                onClick = {
                    isInCall = false
                    onLeave()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Leave Call")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Users in the Channel:")

            LazyColumn {
                items(usersInChannel) { uid ->
                    Text(text = "User ID: $uid")
                }
            }
        } else {
            Text("Enter Channel Name", modifier = Modifier.padding(16.dp))
            BasicTextField(
                value = channelName,
                onValueChange = { channelName = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .border(1.dp, Color.Black)
                    .padding(8.dp)
            )
            Button(
                onClick = {
                    isInCall = true
                    onJoin(channelName)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Join Call")
            }
        }
    }
}
