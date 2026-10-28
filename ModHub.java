package com.yani.client;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

/**
 * Tela inicial do Right Shift: so mostra a opcao MODS.
 * Ao clicar, abre a pagina de escolher os mods (ModMenu).
 */
public class ModHub extends GuiScreen {

    private static final int PW = 300;
    private static final int PH = 178;

    private long openTime;
    private float fit = 1F;
    private float hoverAnim = 0F;
    private float closeAnim = 0F;

    @Override
    public void func_73866_w_() {
        openTime = System.currentTimeMillis();
        fit = Math.min(1F, Math.min((field_146294_l - 12F) / PW, (field_146295_m - 12F) / PH));
        if (fit < 0.3F) fit = 0.3F;
    }

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

    private float[] modsBounds() {
        return new float[]{px() + 24, py() + 74, px() + PW - 24, py() + 74 + 64};
    }

    private float[] closeBounds() {
        return new float[]{px() + PW - 30, py() + 14, px() + PW - 10, py() + 34};
    }

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

        // cabecalho
        Ui.logo(px + 14, py + 9, 30, accent);
        String a = "YANI";
        String b = " CLIENT";
        float wa = Fonts.width(a, 1.3F, true);
        Fonts.drawC(a, px + 50, py + 24, 0xFFFFFFFF, 1.3F, true, false);
        Fonts.drawC(b, px + 50 + wa, py + 24, 0xFFC8CCE0, 1.3F, false, false);

        float[] c = closeBounds();
        boolean hovClose = RenderUtil.hover(x, y, c[0], c[1], c[2], c[3]);
        closeAnim = Ui.approach(closeAnim, hovClose);
        RenderUtil.roundedRect(c[0], c[1], c[2], c[3], 5, RenderUtil.lerpColor(th.btn, 0xFFB3243F, closeAnim));
        float cx = (c[0] + c[2]) / 2F;
        float cy = (c[1] + c[3]) / 2F;
        RenderUtil.line(cx - 3.5F, cy - 3.5F, cx + 3.5F, cy + 3.5F, 1.4F, 0xFFFFFFFF);
        RenderUtil.line(cx - 3.5F, cy + 3.5F, cx + 3.5F, cy - 3.5F, 1.4F, 0xFFFFFFFF);
        RenderUtil.rect(px + 12, py + 46, px + PW - 12, py + 46.6F, 0x22FFFFFF);

        Fonts.drawC("MENU", px + 24, py + 62, 0xFF6F6F7C, 0.7F, true, false);

        // botao MODS
        float[] r = modsBounds();
        boolean hov = RenderUtil.hover(x, y, r[0], r[1], r[2], r[3]);
        hoverAnim = Ui.approach(hoverAnim, hov);
        RenderUtil.roundedRect(r[0] - 1, r[1] - 1, r[2] + 1, r[3] + 1, 10,
                RenderUtil.lerpColor(th.line, accent, hoverAnim));
        RenderUtil.roundedRect(r[0], r[1], r[2], r[3], 9,
                RenderUtil.lerpColor(Theme.ga(th.card), Theme.ga(th.cardHover), hoverAnim));

        float icx = r[0] + 34;
        float icy = (r[1] + r[3]) / 2F;
        RenderUtil.circle(icx, icy, 22, RenderUtil.withAlpha(accent, 0x30));
        RenderUtil.ring(icx, icy, 20, 1.6F, accent);
        Icons.drawNamed("mods", icx, icy, 24, 0xFFFFFFFF);

        Fonts.drawC("MODS", r[0] + 68, icy - 8, 0xFFFFFFFF, 1.35F, true, false);
        Fonts.drawC("Ative, desative e configure", r[0] + 68, icy + 10, 0xFF8E8E9C, 0.78F, false, false);

        // seta
        float ax = r[2] - 24 + hoverAnim * 3F;
        RenderUtil.line(ax - 3F, icy - 6F, ax + 3F, icy, 1.8F, 0xFFFFFFFF);
        RenderUtil.line(ax + 3F, icy, ax - 3F, icy + 6F, 1.8F, 0xFFFFFFFF);

        Fonts.drawC("Right Shift fecha", px + 24, py + PH - 16, 0xFF5E5E6A, 0.72F, false, false);

        GlStateManager.func_179121_F();
        GlStateManager.func_179131_c(1F, 1F, 1F, 1F);
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
        float[] r = modsBounds();
        if (RenderUtil.hover(x, y, r[0], r[1], r[2], r[3])) {
            field_146297_k.func_147108_a(new ModMenu());
        }
    }

    @Override
    protected void func_73869_a(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_RSHIFT) {
            field_146297_k.func_147108_a(null);
            return;
        }
        super.func_73869_a(typedChar, keyCode);
    }

    @Override
    public boolean func_73868_f() {
        return false;
    }
}
