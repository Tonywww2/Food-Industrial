package com.tonywww.foodindustrial.material;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Reads {@code /foodindustrial/materials.json} from the mod jar. */
public final class MaterialTable {
    public static final String RESOURCE = "/foodindustrial/materials.json";

    private MaterialTable() {
    }

    public static List<MaterialDef> load() {
        try (InputStream in = MaterialTable.class.getResourceAsStream(RESOURCE)) {
            if (in == null) {
                throw new IllegalStateException("Missing " + RESOURCE + "; run tools/materials/export_materials.py");
            }
            try (Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
                return parse(JsonParser.parseReader(reader).getAsJsonObject());
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Cannot read " + RESOURCE, exception);
        }
    }

    static List<MaterialDef> parse(JsonObject root) {
        List<MaterialDef> result = new ArrayList<>();
        for (JsonElement element : root.getAsJsonArray("materials")) {
            JsonObject json = element.getAsJsonObject();
            JsonObject name = json.getAsJsonObject("name");
            JsonObject professional = json.getAsJsonObject("professional");
            JsonObject common = json.get("common").isJsonNull() ? null : json.getAsJsonObject("common");
            List<String> hazards = new ArrayList<>();
            JsonArray hazardArray = json.getAsJsonArray("hazards");
            hazardArray.forEach(hazard -> hazards.add(hazard.getAsString()));
            result.add(new MaterialDef(
                json.get("id").getAsString(),
                name.get("zh").getAsString(), name.get("en").getAsString(),
                professional.get("zh").getAsString(), professional.get("en").getAsString(),
                common == null ? null : common.get("zh").getAsString(), common == null ? null : common.get("en").getAsString(),
                string(json, "formula"),
                MaterialDef.State.parse(json.get("state").getAsString()),
                json.get("category").getAsString(),
                json.get("bearer").getAsBoolean(),
                json.get("purity").isJsonNull() ? null : json.getAsJsonObject("purity"),
                color(string(json, "color")),
                string(json, "gtceu"),
                json.get("tag").getAsString(),
                Collections.unmodifiableList(hazards)));
        }
        return Collections.unmodifiableList(result);
    }

    @Nullable
    private static String string(JsonObject json, String key) {
        JsonElement value = json.get(key);
        return value == null || value.isJsonNull() ? null : value.getAsString();
    }

    /** {@code #RRGGBB} → opaque ARGB; {@code 0} when absent. */
    private static int color(@Nullable String hex) {
        if (hex == null) {
            return 0;
        }
        return 0xFF000000 | Integer.parseInt(hex.substring(1), 16);
    }
}
