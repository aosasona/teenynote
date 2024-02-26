package com.trulyao.teenynote

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import java.io.File
import java.io.IOException
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.name

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun Notes(
    context: Context,
    navController: NavController,
    setCurrentNote: (Note) -> Unit,
    notes: SnapshotStateList<Note>
) {
    var showNewFileDialog by remember { mutableStateOf(false) }
    var filename by remember { mutableStateOf("") }
    val enableButton = remember {
        derivedStateOf { filename.isNotEmpty() && filename.length >= 2 && filename.length < 32; }
    }

    fun handleCreate() {
        try {
            val filePath = Path(context.filesDir.absolutePath).resolve("${filename}.txt");
            val file = createNote(filePath);
            val note = pathToEntry(file);
            notes.add(note)

            setCurrentNote(note)
            filename = ""

            Toast.makeText(
                context,
                "${file.fileName.name} created successfully",
                Toast.LENGTH_SHORT
            ).show()

            navController.navigate(Views.Note.name)
        } catch (e: Exception) {
            Toast.makeText(
                context,
                e.message?.ifEmpty { "Something went wrong" },
                Toast.LENGTH_LONG
            ).show()
        } finally {
            showNewFileDialog = false
        }
    }


    Scaffold(modifier = Modifier.fillMaxSize(), floatingActionButton = {
        FloatingActionButton(onClick = { showNewFileDialog = true }, shape = CircleShape) {
            Icon(Icons.Filled.Add, "Create new note")
        }
    }
    ) { innerPadding ->
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            stickyHeader {
                Text(
                    text = "Notes",
                    fontSize = 56.sp,
                    fontWeight = FontWeight(700),
                    modifier = Modifier.padding(8.dp)
                )
            }

            items(notes) { note ->
                Surface(
                    onClick = {
                        setCurrentNote(note);
                        navController.navigate(Views.Note.name)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = note.name,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .size(50.dp)
                            .wrapContentHeight()
                    )
                }

                Divider(color = Color.DarkGray, modifier = Modifier.padding(start = 10.dp))
            }
        }

        when {
            showNewFileDialog -> Dialog(onDismissRequest = { showNewFileDialog = false }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "Create note",
                            fontSize = 22.sp,
                            fontWeight = FontWeight(600)
                        )

                        TextField(
                            value = filename,
                            onValueChange = { e -> filename = e },
                            label = { Text("File name") },
                            modifier = Modifier
                                .padding(vertical = 12.dp)
                                .fillMaxWidth()
                        )

                        Row(
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = { handleCreate() },
                                enabled = enableButton.value
                            ) {
                                Text("Save")
                            }
                        }
                    }
                }
            }
        }
    }
}

fun createNote(path: Path): Path {
    try {
        val file = File(path.toUri());
        if (file.exists()) throw Exception("A file with this name already exists!")
        file.createNewFile();
        return file.toPath();
    } catch (e: IOException) {
        println(e);
        throw Exception("Failed to create file, see logs for more details.")
    } catch (e: SecurityException) {
        println(e);
        throw Exception("Unable to create file due to permission error(s).")
    } catch (e: Exception) {
        throw e;
    }
}

//@Composable
//@Preview(showBackground = true)
//fun NotesPreview() {
//    val navController = rememberNavController()
//    var current by remember { mutableStateOf("") }
//    val demoList = remember { mutableStateListOf<String>() }
//
//    TeenyNoteTheme {
//        Notes(navController, { e -> current = e }, demoList)
//    }
//}