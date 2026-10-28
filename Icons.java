package com.yani.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;

/**
 * Icones (png branco, 96x96) de cada mod. O arquivo e o nome do mod em minusculo,
 * sem espacos: "Flash de Dano" -> textures/icons/flash_de_dano.png
 */
public class Icons {

    private static final int TEX = 96;
    private static final Map<String, ResourceLocation> CACHE = new HashMap<String, ResourceLocation>();

    public static String slug(String key) {
        return key.toLowerCase().replace(' ', '_');
    }

    private static ResourceLocation loc(String name) {
        ResourceLocation r = CACHE.get(name);
        if (r == null) {
            r = new ResourceLocation("yaniclient", "textures/icons/" + name + ".png");
            CACHE.put(name, r);
        }
        return r;
    }

    /** Icone do mod, centralizado em (cx, cy), com "size" de lado, pintado com a cor dada. */
    public static void draw(String key, float cx, float cy, float size, int argb) {
        drawNamed(slug(key), cx, cy, size, argb);
    }

    /** Icone geral pelo nome do arquivo (ex.: "mods", "voltar", "engrenagem"). */
    public static void drawNamed(String name, float cx, float cy, float size, int argb) {
        Minecraft.func_71410_x().func_110434_K().func_110577_a(loc(name));
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GlStateManager.func_179147_l();
        GlStateManager.func_179120_a(770, 771, 1, 0);
        GlStateManager.func_179098_w();
        GlStateManager.func_179131_c(((argb >> 16) & 255) / 255F, ((argb >> 8) & 255) / 255F,
                (argb & 255) / 255F, ((argb >>> 24) & 255) / 255F);
        GlStateManager.func_179094_E();
        GlStateManager.func_179109_b(cx - size / 2F, cy - size / 2F, 0F);
        float k = size / TEX;
        GlStateManager.func_179152_a(k, k, 1F);
        Gui.func_146110_a(0, 0, 0F, 0F, TEX, TEX, (float) TEX, (float) TEX);
        GlStateManager.func_179121_F();
        GlStateManager.func_179131_c(1F, 1F, 1F, 1F);
    }
}
