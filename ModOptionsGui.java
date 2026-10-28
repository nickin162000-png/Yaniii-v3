package com.yani.client;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Pagina de opcoes de UM mod (ex.: CPS, Zoom, Keystrokes).
 * So abre para mods que realmente tem algo para mudar (ver hasOptions).
 */
public class ModOptionsGui extends GuiScreen {

    private static final int PW = 560;
    private static final int PH = 344;

    /** Mods que tem tamanho/posicao ajustaveis no HUD. */
    private static final Set<String> HUD_ELEMENTS = new HashSet<String>(Arrays.asList(
            "Marca", "FPS", "CPS", "Ping", "Coordenadas", "Direcao", "Relogio", "Keystrokes", "Teclas Mouse",
            "Armor HUD", "Efeitos", "Velocidade", "Memoria", "Data", "Sessao", "Rotacao", "Grafico FPS",
            "Bioma", "Dia", "Luz", "Servidor", "Durabilidade", "Pots",
            "Combo", "Reach", "TNT", "Itens", "Alvo", "Hits", "Abates"));

    // ---------- quais mods tem opcoes ----------

    public static boolean hasOptions(String key) {
        return HUD_ELEMENTS.contains(key) || key.equals("Zoom") || key.equals("Mira Moderna")
                || key.equals("Menus Modernos");
    }

    /** Tela certa para as opcoes do mod (a mira usa a pagina completa de mira). */
    public static GuiScreen open(GuiScreen parent, String key) {
        if (key.equals("Mira Moderna")) return new ModernGui(2);
        return new ModOptionsGui(parent, key);
    }

    private static String desc(String n) {
        if (n.equals("Marca")) return "Marca do Yani Client no canto da tela";
        if (n.equals("FPS")) return "Quadros por segundo do jogo";
        if (n.equals("CPS")) return "Cliques por segundo (esquerdo e direito)";
        if (n.equals("Ping")) return "Latencia ate o servidor em ms";
        if (n.equals("Coordenadas")) return "Sua posicao X, Y e Z no mundo";
        if (n.equals("Direcao")) return "Para onde voce esta olhando";
        if (n.equals("Relogio")) return "Hora atual do seu computador";
        if (n.equals("Keystrokes")) return "Teclas de movimento e pulo em tempo real";
        if (n.equals("Teclas Mouse")) return "Botoes esquerdo e direito do mouse";
        if (n.equals("Armor HUD")) return "Armadura e item da mao com durabilidade";
        if (n.equals("Efeitos")) return "Efeitos de pocao ativos e tempo restante";
        if (n.equals("Velocidade")) return "Sua velocidade em blocos por segundo";
        if (n.equals("Memoria")) return "Uso de memoria RAM do jogo";
        if (n.equals("Data")) return "Data de hoje";
        if (n.equals("Sessao")) return "Tempo desde que voce abriu o jogo";
        if (n.equals("Rotacao")) return "Angulos da camera (yaw e pitch)";
        if (n.equals("Grafico FPS")) return "Grafico com o historico de FPS";
        if (n.equals("Bioma")) return "Bioma onde voce esta";
        if (n.equals("Dia")) return "Dia do mundo no Minecraft";
        if (n.equals("Luz")) return "Nivel de luz no seu bloco";
        if (n.equals("Servidor")) return "Endereco do servidor atual";
        if (n.equals("Durabilidade")) return "Durabilidade do item na mao";
        if (n.equals("Pots")) return "Quantidade de pocoes no inventario";
        if (n.equals("Combo")) return "Golpes seguidos sem apanhar";
        if (n.equals("Reach")) return "Distancia do ultimo golpe";
        if (n.equals("TNT")) return "TNT acesa por perto e tempo para explodir";
        if (n.equals("Itens")) return "Flechas, perolas, macas, pocoes e blocos";
        if (n.equals("Alvo")) return "Vida e distancia do inimigo";
        if (n.equals("Hits")) return "Golpes dados | recebidos";
        if (n.equals("Abates")) return "Abates | mortes (por mundo)";
        if (n.equals("Zoom")) return "Segure a tecla C para aproximar";
        if (n.equals("Menus Modernos")) return "Tela inicial e menu de pausa novos";
        return "";
    }

    // ---------- componentes ----------

    private static class Sl {
        final String key, label;
        final float min, max, step, def;
        final int fmt; // 1 = porcentagem, 3 = duas casas
        float x, y, w;

        Sl(String key, String label, float min, float max, float step, float def, int fmt) {
            this.key = key;
            this.label = label;
            this.min = min;
            this.max = max;
            this.step = step;
            this.def = def;
            this.fmt = fmt;
        }

        float value() {
            return Config.getFloat(key, def);
        }

        String text(float v) {
            if (fmt == 1) return Math.round(v * 100F) + "%";
            return String.format("%.2f", v);
        }
    }

    private static class Btn {
        final String label;
        final int id;
        final boolean primary;
        float x, y, w, h;

        Btn(String label, int id, boolean primary) {
            this.label = label;
            this.id = id;
            this.primary = primary;
        }
    }

    private final GuiScreen parent;
    private final String key;
    private final List<Sl> sliders = new ArrayList<Sl>();
    private final List<Btn> btns = new ArrayList<Btn>();
    private final float[] btnAnim = new float[4];
    private final float[] themeAnim = new float[Theme.ALL.length];
    private float backAnim = 0F, closeAnim = 0F, statusAnim = 0F, scrollOptAnim = 0F, zoomScrollAnim = 0F;
    private Sl dragSl = null;
    private long openTime;
    private float fit = 1F;

    public ModOptionsGui(GuiScreen parent, String key) {
        this.parent = parent;
        this.key = key;
    }

    @Override
    public void func_73866_w_() {
        openTime = System.currentTimeMillis();
        fit = Math.min(1F, Math.min((field_146294_l - 12F) / PW, (field_146295_m - 12F) / PH));
        if (fit < 0.3F) fit = 0.3F;
        build();
    }

    @Override
    public void func_146281_b() {
        Config.save();
    }

    // ---------- layout ----------

    private float lx(int mx) {
        return (mx - field_146294_l / 2F) / fit + field_146294_l / 2F;
    }

    private float ly(int my) {
        return (my - field_146295_m / 2F) / fit + field_146295_m / 2F;
    }

    private float px() {
        return (field_146294_l - PW) / 2F;
    }

    private float py() {
        return (field_146295_m - PH) / 2F;
    }

    private float[] backBounds() {
        return new float[]{px() + 14, py() + 14, px() + 98, py() + 34};
    }

    private float[] closeBounds() {
        return new float[]{px() + PW - 30, py() + 14, px() + PW - 10, py() + 34};
    }

    private float[] infoBounds() {
        return new float[]{px() + 14, py() + 58, px() + 190, py() + PH - 14};
    }

    private float[] statusBounds() {
        float[] i = infoBounds();
        return new float[]{i[0] + 12, i[3] - 40, i[2] - 12, i[3] - 12};
    }

    private float rightX() {
        return px() + 204;
    }

    private float rightW() {
        return PW - 204 - 14;
    }

    private float rowY(int i) {
        return py() + 76 + i * 50;
    }

    private float[] zoomScrollBounds() {
        float y = rowY(1);
        return new float[]{rightX(), y, rightX() + rightW(), y + 34};
    }

    private float[] themeBounds(int i) {
        float gap = 8;
        float w = (rightW() - gap * 2) / 3F;
        float h = 78;
        float x = rightX() + (i % 3) * (w + gap);
        float y = py() + 76 + (i / 3) * (h + gap);
        return new float[]{x, y, x + w, y + h};
    }

    private boolean isHudElement() {
        return HUD_ELEMENTS.contains(key);
    }

    private void build() {
        sliders.clear();
        btns.clear();
        float x = rightX();
        float w = rightW();
        if (isHudElement()) {
            Sl s = new Sl("scale." + key, "Tamanho", 0.5F, 2F, 0.05F, 1F, 1);
            s.x = x + 12;
            s.w = w - 24;
            s.y = rowY(0) + 8;
            sliders.add(s);

            float by = rowY(1) + 4;
            Btn edit = new Btn("EDITAR POSICAO", 0, true);
            Btn reset = new Btn("RESETAR POSICAO", 1, false);
            float bw = (w - 8) / 2F;
            edit.x = x;
            edit.y = by;
            edit.w = bw;
            edit.h = 26;
            reset.x = x + bw + 8;
            reset.y = by;
            reset.w = bw;
            reset.h = 26;
            btns.add(edit);
            btns.add(reset);

            Btn all = new Btn("RESETAR TAMANHO", 2, false);
            all.x = x;
            all.y = by + 34;
            all.w = w;
            all.h = 26;
            btns.add(all);
        } else if (key.equals("Zoom")) {
            Sl s = new Sl("zoom.factor", "Forca do zoom (menor = mais perto)", 0.06F, 0.7F, 0.01F, 0.25F, 3);
            s.x = x + 12;
            s.w = w - 24;
            s.y = rowY(0) + 8;
            sliders.add(s);
            Btn reset = new Btn("RESETAR ZOOM", 3, false);
            reset.x = x;
            reset.y = rowY(2) + 4;
            reset.w = w;
            reset.h = 26;
            btns.add(reset);
        }
    }

    // ---------- desenho ----------

    @Override
    public void func_73863_a(int mx, int my, float partialTicks) {
        float t = Math.min(1F, (System.currentTimeMillis() - openTime) / 160F);
        int accent = Config.accent();
        Theme.T th = Theme.cur();
        float x = lx(mx);
        float y = ly(my);

        RenderUtil.gradient(0, 0, field_146294_l, field_146295_m,
                RenderUtil.withAlpha(th.menuTop, (int) (150 * t)), RenderUtil.withAlpha(th.menuBottom, (int) (100 * t)));

        GlStateManager.func_179094_E();
        GlStateManager.func_179109_b(field_146294_l / 2F, field_146295_m / 2F, 0F);
        GlStateManager.func_179152_a(fit, fit, 1F);
        GlStateManager.func_179109_b(-field_146294_l / 2F, -field_146295_m / 2F, 0F);

        float px = px();
        float py = py();
        RenderUtil.roundedRect(px - 1, py - 1, px + PW + 1, py + PH + 1, 11, th.line);
        RenderUtil.roundedRect(px, py, px + PW, py + PH, 10, Theme.ga(th.panel));

        drawHeader(x, y, accent, th);
        drawInfo(x, y, accent, th);
        drawOptions(x, y, accent, th);

        GlStateManager.func_179121_F();
        GlStateManager.func_179131_c(1F, 1F, 1F, 1F);
    }

    private void drawHeader(float x, float y, int accent, Theme.T th) {
        float px = px();
        float py = py();

        float[] b = backBounds();
        boolean hovBack = RenderUtil.hover(x, y, b[0], b[1], b[2], b[3]);
        backAnim = Ui.approach(backAnim, hovBack);
        RenderUtil.roundedRect(b[0], b[1], b[2], b[3], 5, RenderUtil.lerpColor(th.btn, th.btnHover, backAnim));
        Icons.drawNamed("voltar", b[0] + 14, (b[1] + b[3]) / 2F, 11, 0xFFFFFFFF);
        Fonts.drawC("VOLTAR", b[0] + 26, (b[1] + b[3]) / 2F, 0xFFFFFFFF, 0.85F, true, false);

        Fonts.drawC(key, b[2] + 14, py + 24, 0xFFFFFFFF, 1.3F, true, false);
        Fonts.drawC("OPCOES", b[2] + 14 + Fonts.width(key, 1.3F, true) + 8, py + 25, 0xFF6F6F7C, 0.75F, true, false);

        float[] c = closeBounds();
        boolean hovClose = RenderUtil.hover(x, y, c[0], c[1], c[2], c[3]);
        closeAnim = Ui.approach(closeAnim, hovClose);
        RenderUtil.roundedRect(c[0], c[1], c[2], c[3], 5, RenderUtil.lerpColor(th.btn, 0xFFB3243F, closeAnim));
        float cx = (c[0] + c[2]) / 2F;
        float cy = (c[1] + c[3]) / 2F;
        RenderUtil.line(cx - 3.5F, cy - 3.5F, cx + 3.5F, cy + 3.5F, 1.4F, 0xFFFFFFFF);
        RenderUtil.line(cx - 3.5F, cy + 3.5F, cx + 3.5F, cy - 3.5F, 1.4F, 0xFFFFFFFF);

        RenderUtil.rect(px + 12, py + 46, px + PW - 12, py + 46.6F, 0x22FFFFFF);
    }

    private void drawInfo(float x, float y, int accent, Theme.T th) {
        float[] i = infoBounds();
        RenderUtil.roundedRect(i[0], i[1], i[2], i[3], 8, Theme.ga(th.side));
        boolean on = Config.get(key);
        float cx = (i[0] + i[2]) / 2F;
        float iy = i[1] + 48;
        int tone = on ? accent : 0xFF6A6A78;
        RenderUtil.circle(cx, iy, 34, RenderUtil.withAlpha(tone, 0x30));
        RenderUtil.ring(cx, iy, 32, 2F, RenderUtil.withAlpha(tone, on ? 0xFF : 0xA0));
        Icons.draw(key, cx, iy, 40, on ? 0xFFFFFFFF : 0xFF9A9AA8);

        float nw = Fonts.width(key, 1.15F, true);
        Fonts.drawC(key, cx - nw / 2F, iy + 50, 0xFFFFFFFF, 1.15F, true, false);

        // descricao em ate 3 linhas
        String d = desc(key);
        float maxW = i[2] - i[0] - 24;
        float ty = iy + 68;
        String line = "";
        int lines = 0;
        for (String word : d.split(" ")) {
            String test = line.length() == 0 ? word : line + " " + word;
            if (Fonts.width(test, 0.78F, false) > maxW && line.length() > 0) {
                float lw = Fonts.width(line, 0.78F, false);
                Fonts.drawC(line, cx - lw / 2F, ty, 0xFF8E8E9C, 0.78F, false, false);
                ty += 12;
                lines++;
                line = word;
                if (lines >= 3) break;
            } else {
                line = test;
            }
        }
        if (lines < 3 && line.length() > 0) {
            float lw = Fonts.width(line, 0.78F, false);
            Fonts.drawC(line, cx - lw / 2F, ty, 0xFF8E8E9C, 0.78F, false, false);
        }

        // ativar / desativar
        float[] s = statusBounds();
        boolean hov = RenderUtil.hover(x, y, s[0], s[1], s[2], s[3]);
        statusAnim = Ui.approach(statusAnim, hov);
        int base = on ? 0xFF22A55E : 0xFFB3243F;
        int hovc = on ? 0xFF2BC070 : 0xFFCC2E4C;
        RenderUtil.roundedRect(s[0], s[1], s[2], s[3], 7, RenderUtil.lerpColor(base, hovc, statusAnim));
        String st = on ? "ATIVADO" : "DESATIVADO";
        float sw = Fonts.width(st, 0.9F, true);
        Fonts.drawC(st, (s[0] + s[2] - sw) / 2F, (s[1] + s[3]) / 2F, 0xFFFFFFFF, 0.9F, true, false);
    }

    private void drawOptions(float x, float y, int accent, Theme.T th) {
        float rx = rightX();
        float rw = rightW();
        Fonts.drawC("CONFIGURACOES", rx, py() + 62, 0xFF6F6F7C, 0.7F, true, false);

        if (key.equals("Menus Modernos")) {
            drawThemes(x, y, accent, th);
            return;
        }

        // sliders (cada um dentro de um cartao)
        for (Sl s : sliders) {
            RenderUtil.roundedRect(rx, rowY(0), rx + rw, rowY(0) + 44, 7, Theme.ga(th.card));
            drawSlider(s, accent, th, RenderUtil.hover(x, y, s.x - 2, s.y, s.x + s.w + 2, s.y + 24));
        }

        if (key.equals("Zoom")) {
            float[] z = zoomScrollBounds();
            boolean hov = RenderUtil.hover(x, y, z[0], z[1], z[2], z[3]);
            RenderUtil.roundedRect(z[0], z[1], z[2], z[3], 7, Theme.ga(hov ? th.cardHover : th.card));
            Fonts.drawC("Scroll muda o zoom", z[0] + 12, z[1] + 12, 0xFFFFFFFF, 0.95F, true, false);
            Fonts.drawC("Gire o mouse segurando C", z[0] + 12, z[1] + 25, 0xFF7A7A88, 0.75F, false, false);
            boolean on = Config.get("Zoom Scroll");
            zoomScrollAnim += ((on ? 1F : 0F) - zoomScrollAnim) * 0.25F;
            float tw = 28F;
            float tg = 14F;
            float tx = z[2] - 12 - tw;
            float ty = (z[1] + z[3]) / 2F - tg / 2F;
            RenderUtil.roundedRect(tx, ty, tx + tw, ty + tg, tg / 2F, RenderUtil.lerpColor(th.off, accent, zoomScrollAnim));
            int knob = Ui.isLight(accent) && zoomScrollAnim > 0.5F ? 0xFF111114 : 0xFFFFFFFF;
            RenderUtil.circle(tx + tg / 2F + zoomScrollAnim * (tw - tg), (z[1] + z[3]) / 2F, tg / 2F - 2F, knob);
        }

        for (Btn b : btns) {
            boolean hov = RenderUtil.hover(x, y, b.x, b.y, b.x + b.w, b.y + b.h);
            btnAnim[b.id] = Ui.approach(btnAnim[b.id], hov);
            Ui.button(b.label, b.x, b.y, b.w, b.h, btnAnim[b.id], b.primary);
        }
    }

    private void drawSlider(Sl s, int accent, Theme.T th, boolean hov) {
        float v = s.value();
        float t = Math.max(0F, Math.min(1F, (v - s.min) / (s.max - s.min)));
        Fonts.drawC(s.label, s.x, s.y + 6, 0xFFE8E8F0, 0.88F, false, false);
        String vt = s.text(v);
        float tw = Fonts.width(vt, 0.85F, true);
        Fonts.drawC(vt, s.x + s.w - tw, s.y + 6, 0xFF9A9AA8, 0.85F, true, false);
        float by = s.y + 20;
        RenderUtil.roundedRect(s.x, by - 2, s.x + s.w, by + 2, 2, Theme.ga(th.track));
        if (t > 0.001F) RenderUtil.roundedRect(s.x, by - 2, s.x + Math.max(4F, s.w * t), by + 2, 2, accent);
        RenderUtil.circle(s.x + s.w * t, by, (hov || s == dragSl) ? 5.5F : 4.5F, 0xFFFFFFFF);
    }

    private void drawThemes(float x, float y, int accent, Theme.T th) {
        for (int i = 0; i < Theme.ALL.length; i++) {
            float[] b = themeBounds(i);
            boolean sel = i == Theme.index();
            boolean hov = RenderUtil.hover(x, y, b[0], b[1], b[2], b[3]);
            themeAnim[i] = Ui.approach(themeAnim[i], hov);
            if (sel) RenderUtil.roundedRect(b[0] - 1.5F, b[1] - 1.5F, b[2] + 1.5F, b[3] + 1.5F, 8, accent);
            else if (hov) RenderUtil.roundedRect(b[0] - 1, b[1] - 1, b[2] + 1, b[3] + 1, 8, 0x66FFFFFF);
            RenderUtil.roundedRect(b[0], b[1], b[2], b[3], 7, Theme.ga(th.card));
            MenuBg.thumb(i, b[0] + 3, b[1] + 3, b[2] - b[0] - 6, 50, Theme.ALL[i].accent);
            Fonts.drawC(Theme.ALL[i].name, b[0] + 8, b[3] - 11, sel ? 0xFFFFFFFF : 0xFFB4B4BE, 0.85F, sel, false);
        }
        Fonts.drawC("Muda a tela inicial, o menu e as cores do client", rightX(), py() + 76 + 2 * 86 + 8,
                0xFF7A7A88, 0.78F, false, false);
    }

    // ---------- entrada ----------

    private void setSlider(Sl s, float xf) {
        float t = Math.max(0F, Math.min(1F, (xf - s.x) / s.w));
        float v = s.min + t * (s.max - s.min);
        v = Math.round(v / s.step) * s.step;
        v = Math.max(s.min, Math.min(s.max, v));
        Config.setFloat(s.key, v);
    }

    @Override
    protected void func_73864_a(int mx, int my, int btn) throws IOException {
        super.func_73864_a(mx, my, btn);
        if (btn != 0) return;
        float x = lx(mx);
        float y = ly(my);

        float[] c = closeBounds();
        if (RenderUtil.hover(x, y, c[0], c[1], c[2], c[3])) {
            field_146297_k.func_147108_a(null);
            return;
        }
        float[] b = backBounds();
        if (RenderUtil.hover(x, y, b[0], b[1], b[2], b[3])) {
            goBack();
            return;
        }
        float[] s = statusBounds();
        if (RenderUtil.hover(x, y, s[0], s[1], s[2], s[3])) {
            Config.toggle(key);
            Config.save();
            Config.setString("profile.active", "");
            return;
        }

        if (key.equals("Menus Modernos")) {
            for (int i = 0; i < Theme.ALL.length; i++) {
                float[] tb = themeBounds(i);
                if (RenderUtil.hover(x, y, tb[0], tb[1], tb[2], tb[3])) {
                    Theme.set(i);
                    return;
                }
            }
            return;
        }

        for (Sl sl : sliders) {
            if (RenderUtil.hover(x, y, sl.x - 4, sl.y, sl.x + sl.w + 4, sl.y + 26)) {
                dragSl = sl;
                setSlider(sl, x);
                return;
            }
        }

        if (key.equals("Zoom")) {
            float[] z = zoomScrollBounds();
            if (RenderUtil.hover(x, y, z[0], z[1], z[2], z[3])) {
                Config.toggle("Zoom Scroll");
                Config.save();
                return;
            }
        }

        for (Btn bt : btns) {
            if (!RenderUtil.hover(x, y, bt.x, bt.y, bt.x + bt.w, bt.y + bt.h)) continue;
            if (bt.id == 0) {
                field_146297_k.func_147108_a(new HudEditorGui(this));
            } else if (bt.id == 1) {
                for (Hud.El e : Hud.ELEMENTS) {
                    if (e.name.equals(key)) e.reset();
                }
                Hud.savePositions();
            } else if (bt.id == 2) {
                Config.removePrefix("scale." + key);
                Config.save();
            } else if (bt.id == 3) {
                Config.setFloat("zoom.factor", 0.25F);
                Config.save();
            }
            return;
        }
    }

    private void goBack() {
        field_146297_k.func_147108_a(parent);
    }

    @Override
    protected void func_146273_a(int mx, int my, int btn, long time) {
        if (dragSl != null) setSlider(dragSl, lx(mx));
    }

    @Override
    protected void func_146286_b(int mx, int my, int state) {
        super.func_146286_b(mx, my, state);
        if (dragSl != null) {
            dragSl = null;
            Config.save();
        }
    }

    @Override
    protected void func_73869_a(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_RSHIFT) {
            field_146297_k.func_147108_a(null);
            return;
        }
        if (keyCode == Keyboard.KEY_ESCAPE) {
            goBack();
            return;
        }
        super.func_73869_a(typedChar, keyCode);
    }

    @Override
    public boolean func_73868_f() {
        return false;
    }
}
