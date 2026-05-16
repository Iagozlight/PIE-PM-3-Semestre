package projeto.views.componentes;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class CursorUtil {

    private CursorUtil() {}

    public static Cursor carregar(String resourcePath) {
        URL url = CursorUtil.class.getResource(resourcePath);
        if (url == null) return Cursor.getDefaultCursor();

        Image image = new ImageIcon(url).getImage();
        try {
            return Toolkit.getDefaultToolkit().createCustomCursor(
                    image, new Point(16, 16), "cursor");
        } catch (RuntimeException ex) {
            return Cursor.getDefaultCursor();
        }
    }
}