package projeto.models.util;

import java.awt.Font;

public final class Fontes {

    private Fontes() {
    }

    public static Font segoe(int style, int size) {
        return new Font("Segoe UI", style, size);
    }

    public static Font arial(int style, int size) {
        return new Font("Arial", style, size);
    }
}
