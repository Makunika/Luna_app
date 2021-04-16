package ru.pshiblo.services.keypress;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import ru.pshiblo.Config;
import ru.pshiblo.gui.log.ConsoleOut;
import ru.pshiblo.services.Context;
import ru.pshiblo.services.Service;
import ru.pshiblo.services.ServiceType;

import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class GlobalKeyListenerService implements NativeKeyListener, Service {

    private boolean isInit;

    public GlobalKeyListenerService() {
        isInit = false;
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {

    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        if (e.getKeyCode() == NativeKeyEvent.VC_F12) {
            Context.getMusicService().skip();
        }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) {

    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.KEYPRESS;
    }

    @Override
    public boolean isInitializer() {
        return isInit;
    }

    @Override
    public void start() {
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException e) {
            e.printStackTrace();
            ConsoleOut.println(e.getMessage());
            return;
        }
        LogManager.getLogManager().reset();

        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);

        GlobalScreen.addNativeKeyListener(this);
        isInit = true;
    }

    @Override
    public void shutdown() {
        GlobalScreen.removeNativeKeyListener(this);
        isInit = false;
    }
}
