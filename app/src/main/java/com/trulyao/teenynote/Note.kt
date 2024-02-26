package com.trulyao.teenynote

import java.nio.file.Path
import kotlin.io.path.name

data class Note(val name: String, val path: Path);

fun pathToEntry(path: Path): Note {
    return Note(name = path.fileName.name.replace(".txt", ""), path = path.toAbsolutePath())
}