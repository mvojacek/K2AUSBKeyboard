package th.in.whs.k2ausbkbd.hid;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import th.in.whs.k2ausbkbd.layout.KeyCode;
import th.in.whs.k2ausbkbd.layout.Layout;

public class Keyboard {
    private static Keyboard instance = null;

    private static final String DEVICE = "/dev/hidg0";

    private Process process;

    private Keyboard() throws UnsupportedOperationException {
        if(!new File(DEVICE).exists()){
            throw new UnsupportedOperationException("Unsupported kernel");
        }

        try {
            process = Runtime.getRuntime().exec(new String[]{
                    "su",
                    "-c",
                    "sh"
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close(){
        process.destroy();
    }


    public void type(String text, Layout layout) throws IOException {
        for(int i = 0, j = text.length(); i < j; i++){
            char typeChar = text.charAt(i);

            sendChar(typeChar, layout);

            // Fix for double tap.
            /*if( typeChar == '^' || typeChar == '´' || typeChar == '`' ) {
                sendChar( ' ', layout );
            }*/
        }
    }

    private void sendChar(char data, Layout layout) throws IOException {
        KeyCode key = layout.getKeycode(data);

        byte[] buffer = new byte[]{
                (key != null) ? (byte)key.modifier : 0,
                0,
                (key != null) ? (byte)key.code : 0,
                0 , 0, 0, 0, 0
        };

        writeToDevice(buffer);
        keyUp();
    }

    private void writeToDevice(byte[] bytes) throws IOException {
        OutputStream stdin = process.getOutputStream();
        stdin.write(("echo -ne " + bytesToEcho(bytes) + " > " + DEVICE + "\n").getBytes());
        stdin.flush();
    }

    private void keyUp() throws IOException {
        OutputStream stdin = process.getOutputStream();
        stdin.write(("echo -ne \\\\x0\\\\x0\\\\x0\\\\x0\\\\x0\\\\x0\\\\x0\\\\x0 > " + DEVICE + "\n").getBytes());
        stdin.flush();
    }

    private String bytesToEcho(byte[] bytes){
        StringBuilder builder = new StringBuilder();
        for (byte aByte : bytes) {
            builder.append("\\\\x");
            builder.append(Integer.toHexString(Byte.valueOf(aByte).intValue()));
        }
        return builder.toString();
    }

    // Singleton
    public static Keyboard getInstance() {
        if(instance == null) {
            instance = new Keyboard();
        }
        return instance;
    }
}
