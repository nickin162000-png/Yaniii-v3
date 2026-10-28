package com.yani.client;

/**
 * Temas completos do client. Cada tema muda a tela inicial, o menu do client,
 * o menu de pausa, os botoes, o HUD e a cor de destaque.
 */
public class Theme {

    public static class T {
        public final String name, desc;
        public final int accent, menuTop, menuBottom, panel, side, card, cardHover, track, off, btn, btnHover, line, hudRgb, textDim;

        T(String name, String desc, int accent, int menuTop, int menuBottom, int panel, int side, int card,
          int cardHover, int track, int off, int btn, int btnHover, int line, int hudRgb, int textDim) {
            this.name = name;
            this.desc = desc;
            this.accent = accent;
            this.menuTop = menuTop;
            this.menuBottom = menuBottom;
            this.panel = panel;
            this.side = side;
            this.card = card;
            this.cardHover = cardHover;
            this.track = track;
            this.off = off;
            this.btn = btn;
            this.btnHover = btnHover;
            this.line = line;
            this.hudRgb = hudRgb;
            this.textDim = textDim;
        }
    }

    public static final T[] ALL = {
            new T("Meia-Noite", "Ceu estrelado e cidade acesa",
                    0xFF7C5CFF, 0xFF05050B, 0xFF16112B,
                    0xD80C0C12, 0xC0121218, 0xB0181820, 0xC8262634, 0xFF2A2A36, 0xFF3A3A48,
                    0xB41E1E26, 0xD22D2D39, 0x26FFFFFF, 0x0E0E12, 0xFF8A8A98),
            new T("Abobora Maldita", "Lua laranja, morcegos e abobora",
                    0xFFFF7A00, 0xFF0D0408, 0xFF3A1406,
                    0xD81A0A06, 0xC0240F08, 0xB02E140B, 0xC84A2010, 0xFF3C2016, 0xFF553322,
                    0xB42C130B, 0xD25A2810, 0x40FF7A00, 0x1A0A06, 0xFFA8826C),
            new T("Gelo Eterno", "Aurora boreal e neve nas montanhas",
                    0xFF35B8F0, 0xFF030D18, 0xFF0F3552,
                    0xD80A1824, 0xC0102334, 0xB0143048, 0xC81F4668, 0xFF24425C, 0xFF35566F,
                    0xB4143048, 0xD2235A80, 0x4035B8F0, 0x08161F, 0xFF86A9C2),
            new T("Sakura", "Petalas rosas caindo ao luar",
                    0xFFFF7EB6, 0xFF14060F, 0xFF3A1230,
                    0xD8180A14, 0xC0200F1B, 0xB02A1424, 0xC84A2442, 0xFF3A2034, 0xFF55354D,
                    0xB4301A2A, 0xD25A2E4C, 0x40FF7EB6, 0x1A0A14, 0xFFB88AA6),
            new T("Esmeralda", "Cristais verdes e bolhas de luz",
                    0xFF2ED573, 0xFF020D08, 0xFF0A3A24,
                    0xD8081410, 0xC00D1D17, 0xB0112A20, 0xC81D4A38, 0xFF1F4034, 0xFF2F5A4A,
                    0xB4112A20, 0xD21E5440, 0x402ED573, 0x07130E, 0xFF86B8A0),
            new T("Por do Sol", "Sol baixo e montanhas em camadas",
                    0xFFFF8A5B, 0xFF1B0A33, 0xFF7A2A3A,
                    0xD816091F, 0xC01F0F2B, 0xB02A1636, 0xC84A2656, 0xFF3C2450, 0xFF55386A,
                    0xB4291637, 0xD2503070, 0x40FF8A5B, 0x120819, 0xFFB79AC8)
    };

    public static int index() {
        int i = Config.getInt("theme", 0);
        return i < 0 || i >= ALL.length ? 0 : i;
    }

    public static T cur() {
        return ALL[index()];
    }

    public static T get(int i) {
        return ALL[i < 0 || i >= ALL.length ? 0 : i];
    }

    /** Troca o tema e volta para a cor de destaque do tema. */
    public static void set(int i) {
        if (i < 0 || i >= ALL.length) return;
        Config.setInt("theme", i);
        Config.setAccent(-1);
        Config.save();
    }

    /** Aplica a transparencia escolhida do menu a uma cor. */
    public static int ga(int argb) {
        int a = (int) (((argb >>> 24) & 255) * Config.guiAlpha());
        return (a << 24) | (argb & 0xFFFFFF);
    }
}
