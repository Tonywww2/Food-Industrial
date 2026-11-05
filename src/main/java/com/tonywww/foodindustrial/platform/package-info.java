/**
 * Version / loader compatibility layer.
 * <p>
 * This package tree is the only place allowed to contain Stonecutter conditions ({@code //?}),
 * loader imports ({@code net.minecraftforge.*}, {@code net.neoforged.*}) and Minecraft APIs whose
 * shape differs between the supported nodes (1.20.1 Forge, 1.21.1 NeoForge, 26.1.2 NeoForge).
 * Everything else in the mod calls the stable methods exposed here. See CONVENTIONS.md.
 */
package com.tonywww.foodindustrial.platform;
