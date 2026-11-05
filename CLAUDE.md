# Food Industrial

Stonecutter multi-version mod: 1.20.1 Forge 47.4.0 (default/active), 1.21.1 NeoForge 21.1.235, 26.1.2 NeoForge 26.1.2.109.

Follow the project conventions in @CONVENTIONS.md. The non-negotiable rules:

- Every Stonecutter condition (`//?`, `/*?`) and every loader import (`net.minecraftforge.*`, `net.neoforged.*`) lives only in `com.tonywww.foodindustrial.platform` (and its subpackages).
- Code outside `platform` uses only the stable APIs / helpers exposed by `platform`. If one is missing, add a semantic method to `platform` for all three nodes first, then call it.
- After touching `platform`, compile all three nodes (`:1.20.1-forge`, `:1.21.1-neoforge`, `:26.1.2-neoforge`); reset the active project to `1.20.1-forge` before committing.
- When something is uncertain (API behaviour, vanilla or mod mechanics, numbers, design intent), do not guess or decide alone: gather evidence first, or ask the user and wait for a clear answer (CONVENTIONS.md §8).
- Player-facing text stays concise; detailed descriptions belong only in detail areas such as Shift tooltips, the GUI info panel, JEI info pages and the Patchouli book (CONVENTIONS.md §9, docs/text-standard.md).
- Communicate with the user in Chinese: replies, questions, progress and completion reports (CONVENTIONS.md §10).
