package nl.peperzaken.renametest;

import nl.peperzaken.renameannotation.Rename;

public interface RenameJava {
    @Rename
    void methodToRename();
}