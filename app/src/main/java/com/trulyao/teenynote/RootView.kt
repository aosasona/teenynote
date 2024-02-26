package com.trulyao.teenynote

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.trulyao.teenynote.ui.theme.TeenyNoteTheme
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.extension
import kotlin.io.path.isRegularFile


@Composable
fun RootView(context: Context) {
    val navController = rememberNavController()
    var currentNote by remember { mutableStateOf<Note?>(null) }
    val notes = remember {
        mutableStateListOf<Note>()
    }

    fun load() {
        val files = getFiles(context.filesDir.toPath())
        if (files.isSuccess) {
            val filesList = files.getOrElse { listOf() }
            // TODO: find a more efficient way to do this
            notes.clear();
            notes.addAll(filesList)
            return;
        }

        Toast.makeText(context, files.exceptionOrNull()?.message, Toast.LENGTH_LONG).show()
    }

    LaunchedEffect(true) {
        load();
    };

    TeenyNoteTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = Views.Home.name,
                    modifier = Modifier.padding(innerPadding)
                ) {
                    composable(route = Views.Home.name) {
                        Notes(
                            context = context,
                            navController = navController,
                            setCurrentNote = { value -> currentNote = value },
                            notes = notes
                        )
                    }

                    composable(route = Views.Note.name) {
                        currentNote?.let { it1 ->
                            NoteView(
                                context = context,
                                navController = navController,
                                note = it1
                            )
                        }
                    }
                }
            }
        }
    }
}


fun getFiles(dataDir: Path): Result<ArrayList<Note>> {
    return try {
        val files = arrayListOf<Note>();
        Files.walk(dataDir).forEach { path ->
            if (path.isRegularFile() && path.extension == "txt") {
                files.add(pathToEntry(path))
            }
        }

        Result.success(files);
    } catch (e: Error) {
        Result.failure(Error(e.message?.ifEmpty { "Something went wrong" }));
    }
}

