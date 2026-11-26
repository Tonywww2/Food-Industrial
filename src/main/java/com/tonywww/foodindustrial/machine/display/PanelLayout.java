package com.tonywww.foodindustrial.machine.display;

/**
 * Where things sit on a machine's panel (machines/04 §9.2), in panel pixels with the origin at the
 * panel's centre, x to the right and y upwards: the input slots on the left, the progress arrow, the
 * product slots on the right. A side with more than three entries takes two rows, with more than eight
 * three.
 */
public final class PanelLayout {
    public static final int SLOT = 18;
    public static final int PAD = 4;
    public static final int GAP = 3;
    public static final int ARROW_W = 24;
    public static final int ARROW_H = 17;
    /** Entries shown per side: the most a machine holds (six input slots and six input tanks, machines/03 §3). */
    public static final int MAX_ENTRIES = 12;

    private final int inputs;
    private final int outputs;
    private final int inCols;
    private final int inRows;
    private final int outCols;
    private final int outRows;
    private final int width;
    private final int height;

    public PanelLayout(int inputs, int outputs) {
        this.inputs = Math.min(MAX_ENTRIES, Math.max(0, inputs));
        this.outputs = Math.min(MAX_ENTRIES, Math.max(0, outputs));
        this.inCols = columns(this.inputs);
        this.inRows = rows(this.inputs);
        this.outCols = columns(this.outputs);
        this.outRows = rows(this.outputs);
        this.width = PAD + inCols * SLOT + GAP + ARROW_W + GAP + outCols * SLOT + PAD;
        this.height = PAD + Math.max(inRows, outRows) * SLOT + PAD;
    }

    /** Columns of a side with {@code entries} entries: at least one slot is always drawn. */
    static int columns(int entries) {
        int rows = rows(entries);
        return rows == 1 ? Math.max(1, entries) : (entries + rows - 1) / rows;
    }

    static int rows(int entries) {
        return entries <= 3 ? 1 : entries <= 8 ? 2 : 3;
    }

    public int inputs() {
        return inputs;
    }

    public int outputs() {
        return outputs;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    /** Slots drawn on a side: its whole grid. */
    public int slots(boolean output) {
        return output ? outCols * outRows : inCols * inRows;
    }

    /** Left edge of slot {@code index} of a side; slots fill their grid row by row from the top left. */
    public float slotX(boolean output, int index) {
        int cols = output ? outCols : inCols;
        float left = output ? -width / 2F + PAD + inCols * SLOT + GAP + ARROW_W + GAP : -width / 2F + PAD;
        return left + (index % cols) * SLOT;
    }

    /** Lower edge of slot {@code index} of a side; a side with fewer rows than the panel is centred. */
    public float slotY(boolean output, int index) {
        int cols = output ? outCols : inCols;
        int rows = output ? outRows : inRows;
        float top = rows * SLOT / 2F;
        return top - (index / cols + 1) * SLOT;
    }

    /** Left edge of the input area, the block of input slots. */
    public float inputLeft() {
        return -width / 2F + PAD;
    }

    public float inputWidth() {
        return inCols * SLOT;
    }

    public float inputTop() {
        return inRows * SLOT / 2F;
    }

    public float inputHeight() {
        return inRows * SLOT;
    }

    public float arrowX() {
        return -width / 2F + PAD + inCols * SLOT + GAP;
    }

    public float arrowY() {
        return -ARROW_H / 2F;
    }
}
