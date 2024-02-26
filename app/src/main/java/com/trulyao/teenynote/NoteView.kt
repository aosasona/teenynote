package com.trulyao.teenynote

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import java.nio.charset.Charset
import kotlin.io.path.readText
import kotlin.io.path.writeText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteView(
    context: Context,
    navController: NavController,
    note: Note
) {

    fun readFile(): String {
        return try {
            val content = note.path.readText(Charset.defaultCharset())
            content;
        } catch (e: Exception) {
            println(e)
            Toast.makeText(context, "Failed to read file: ${e.message}", Toast.LENGTH_LONG).show()
            "";
        }
    }

    var content by rememberSaveable {
        mutableStateOf<String>(readFile())
    }

    val enableSaveButton = remember {
        derivedStateOf {
            content.isNotEmpty()
        }
    }

    fun saveContent() {
        try {
            note.path.writeText(content)
            Toast.makeText(context, "Saved!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            println(e)
            Toast.makeText(context, "Unable to save file: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(note.name) },
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Go back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { saveContent() }, enabled = enableSaveButton.value) {
                        Icon(
                            imageVector = Icons.Filled.Done,
                            contentDescription = "Save content"
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        TextField(
            value = content,
            onValueChange = { v -> content = v },
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        )
    }
}