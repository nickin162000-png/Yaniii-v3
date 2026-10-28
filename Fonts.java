package com.yani.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Fachada de texto. Usa a fonte moderna (TTF) e, se der problema ou se estiver
 * desligada no menu, volta para a fonte padrao do Minecraft.
 * Para usar sua propria fonte: coloque um arquivo font.ttf em
 * src/main/resources/assets/yaniclient/fonts/
 */
public class Fonts {
    private static final float SIZE = 10F;
    private static CustomFont regular;
    private static CustomFont bold;
    private static boolean tried = false;

    // estilos de fonte dos menus: 0 = original do client, 1 = Minecraft, 2 = serifada
    public static final String[] STYLE_NAMES = {"Original", "Minecraft", "Serifada"};
    private static int override = -1;
    private static CustomFont serifRegular;
    private static CustomFont serifBold;
    private static boolean serifTried = false;

    public static int menuStyle() {
        int s = Config.getInt("menu.font", 0);
        return s < 0 || s >= STYLE_NAMES.length ? 0 : s;
    }

    public static void setMenuStyle(int s) {
        Config.setInt("menu.font", (s % STYLE_NAMES.length + STYLE_NAMES.length) % STYLE_NAMES.length);
    }

    /** Comeca a desenhar usando um estilo de fonte (chame end() depois). */
    public static void begin(int style) {
        override = style <= 0 ? -1 : style;
    }

    public static void end() {
        override = -1;
    }

    private static void initSerif() {
        if (serifTried) return;
        serifTried = true;
        try {
            String[] pref = {"Georgia", "Palatino Linotype", "Times New Roman", "DejaVu Serif", "Serif"};
            Set<String> avail = new HashSet<String>(Arrays.asList(
                    GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));
            String pick = "Serif";
            for (String p : pref) {
                if (avail.contains(p)) {
                    pick = p;
                    break;
                }
            }
            Font base = new Font(pick, Font.PLAIN, 12);
            serifRegular = new CustomFont(base.deriveFont(Font.PLAIN, SIZE * CustomFont.SCALE));
            serifBold = new CustomFont(base.deriveFont(Font.BOLD, SIZE * CustomFont.SCALE));
        } catch (Throwable t) {
            serifRegular = null;
            serifBold = null;
        }
    }

    /** Fonte customizada a usar agora, ou null para a fonte padrao do Minecraft. */
    private static CustomFont pick(boolean isBold) {
        init();
        if (override == 1) return null;
        if (override == 2) {
            initSerif();
            if (serifRegular == null) return null;
            return isBold ? serifBold : serifRegular;
        }
        if (regular != null && Config.get("Fonte Moderna")) return isBold ? bold : regular;
        return null;
    }

    private static void init() {
        if (tried) return;
        tried = true;
        try {
            Font base = null;
            InputStream in = Fonts.class.getResourceAsStream("/assets/yaniclient/fonts/font.ttf");
            if (in != null) {
                try {
                    base = Font.createFont(Font.TRUETYPE_FONT, in);
                } finally {
                    in.close();
                }
            }
            if (base == null) {
                String[] pref = {"Segoe UI", "Helvetica Neue", "SF Pro Display", "Roboto", "Ubuntu", "Arial", "SansSerif"};
                Set<String> avail = new HashSet<String>(Arrays.asList(
                        GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));
                String pick = "SansSerif";
                for (String p : pref) {
                    if (avail.contains(p)) {
                        pick = p;
                        break;
                    }
                }
                base = new Font(pick, Font.PLAIN, 12);
            }
            regular = new CustomFont(base.deriveFont(Font.PLAIN, SIZE * CustomFont.SCALE));
            bold = new CustomFont(base.deriveFont(Font.BOLD, SIZE * CustomFont.SCALE));
        } catch (Throwable t) {
            regular = null;
            bold = null;
        }
    }

    public static float width(String text, float s, boolean isBold) {
        CustomFont f = pick(isBold);
        if (f != null) return f.width(text, s);
        return Minecraft.func_71410_x().field_71466_p.func_78256_a(text) * s;
    }

    /** Desenha com o topo do texto em y. */
    public static void draw(String text, float x, float y, int color, float s, boolean isBold, boolean shadow) {
        if ((color & 0xFC000000) == 0) color |= 0xFF000000;
        CustomFont f = pick(isBold);
        if (f != null) {
            if (shadow) {
                int sc = ((int) ((color >>> 24) * 0.6F) << 24);
                f.draw(text, x + 0.8F * s, y + 0.8F * s, sc, s);
            }
            f.draw(text, x, y, color, s);
        } else {
            GlStateManager.func_179094_E();
            GlStateManager.func_179109_b(x, y, 0F);
            GlStateManager.func_179152_a(s, s, 1F);
            Minecraft.func_71410_x().field_71466_p.func_175065_a(text, 0, 0, color, shadow);
            GlStateManager.func_179121_F();
            GlStateManager.func_179131_c(1F, 1F, 1F, 1F);
        }
    }

    /** Desenha com o texto centralizado verticalmente em cy. */
    public static void drawC(String text, float x, float cy, int color, float s, boolean isBold, boolean shadow) {
        CustomFont f = pick(isBold);
        float y = f != null ? cy - f.centerOffset() * s : cy - 4F * s;
        draw(text, x, y, color, s, isBold, shadow);
    }
}
