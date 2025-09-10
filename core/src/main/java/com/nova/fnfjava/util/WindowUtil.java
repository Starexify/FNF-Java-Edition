package com.nova.fnfjava.util;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;

import javax.swing.*;

public class WindowUtil {
    public static void showError(String name, String desc) {
        if (Gdx.app.getType() == Application.ApplicationType.Desktop) JOptionPane.showMessageDialog(null, desc, name, JOptionPane.ERROR_MESSAGE);
    }
}
