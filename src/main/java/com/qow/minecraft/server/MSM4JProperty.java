package com.qow.minecraft.server;

import com.qow.util.Property;
import com.qow.util.qon.QONObject;
import com.qow.util.qon.UntrustedQONException;

import java.io.File;
import java.io.IOException;

public class MSM4JProperty extends Property {
    public MSM4JProperty(File qonFile) throws UntrustedQONException, IOException {
        QONObject qonObject = new QONObject(qonFile);

    }
}
