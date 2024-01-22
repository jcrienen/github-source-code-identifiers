package com.juulcrienen.githubapiwrapper.helpers;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface FileCallback {

    void doTrigger(List<File> contents) throws IOException;

}
