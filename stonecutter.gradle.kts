plugins {
    id("dev.kikugie.stonecutter")
}

// The node currently written into src/. Switch it with the Stonecutter "Set active project" tasks,
// and reset it to 1.20.1-forge ("Reset active project") before committing.
stonecutter active "1.20.1-forge"

// File-processor parameters only (constants / swaps / replacements). Never put dependencies or tasks here.
stonecutter parameters {
    val loader = current.project.substringAfterLast('-')
    // Enables `//? if forge {` and `//? if neoforge {` in Java sources (platform package only, see CONVENTIONS.md).
    constants { match(loader, "forge", "neoforge") }
}
