package nl.peperzaken.renametest

import nl.peperzaken.renameannotation.Rename

interface RenameKotlin {
    @Rename
    fun functionToRename()
}