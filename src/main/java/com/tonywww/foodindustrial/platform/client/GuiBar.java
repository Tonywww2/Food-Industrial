package com.tonywww.foodindustrial.platform.client;

/**
 * A horizontal bar for the mod's own widgets (I35): a track with tinted zones, a fill from the left and tick
 * marks; drawn with a 1-pixel edge around it. Fractions run from 0 to 1 across the bar; colours are ARGB.
 *
 * @param fill        filled part, 0–1 (anything above 0 shows at least one pixel)
 * @param fillColor   colour of the fill
 * @param zones       tinted zones as {from, to, from, to, ...}
 * @param zoneColors  one colour per zone
 * @param ticks       positions of the tick marks
 * @param trackColor  colour of the empty track
 * @param edgeColor   colour of the edge around the bar, or 0 for none
 * @param tickColor   colour of the tick marks
 * @param stripeColor colour of the stripes over the track every other pixel, or 0 for none
 */
public record GuiBar(float fill, int fillColor, float[] zones, int[] zoneColors, float[] ticks, int trackColor, int edgeColor,
                     int tickColor, int stripeColor) {
    private static final float[] NONE = new float[0];
    private static final int[] NO_COLORS = new int[0];

    /** A bar without zones and ticks. */
    public static GuiBar plain(float fill, int fillColor, int trackColor, int edgeColor, int stripeColor) {
        return new GuiBar(fill, fillColor, NONE, NO_COLORS, NONE, trackColor, edgeColor, 0, stripeColor);
    }
}
