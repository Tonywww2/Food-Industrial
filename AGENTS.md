# Food Industrial — agent instructions

Read and follow [CONVENTIONS.md](CONVENTIONS.md) before changing code.

- Stonecutter tree: `1.20.1-forge` (default/active, committed form), `1.21.1-neoforge`, `26.1.2-neoforge`.
- Stonecutter conditions (`//?`, `/*?`) and loader imports (`net.minecraftforge.*`, `net.neoforged.*`) are allowed **only** in `com.tonywww.foodindustrial.platform` and its subpackages.
- Everything else calls the `platform` API / helpers. Missing API → add a semantic method to `platform` for all three nodes, then use it.
- Validate with `gradlew :1.20.1-forge:compileJava :1.21.1-neoforge:compileJava :26.1.2-neoforge:compileJava`.
- When something is uncertain (API behaviour, vanilla or mod mechanics, numbers, design intent), do not guess or decide alone: gather evidence first, or ask the user and wait for a clear answer (CONVENTIONS.md §8).
- Player-facing text stays concise; detailed descriptions belong only in detail areas such as Shift tooltips, the GUI info panel, JEI info pages and the Patchouli book (CONVENTIONS.md §9, docs/text-standard.md).
