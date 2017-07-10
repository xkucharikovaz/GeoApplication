package com.example.zuzanka.geoapplication.file_manager;

import java.io.File;

/**
 * Created by zuzanka on 16. 10. 2016.
 */

public interface FileManagerInterface {
    File createFile(String name);
    File createFile(String name, String Dir);
    void writeToFile(File file, String string);
    void appendToFile(File file, String string);
}
