package cz.wildwest.zaurex.data;

public enum Role {
    SALESMAN("prodavač"), WAREHOUSEMAN("skladník"), SHIFT_LEADER("vedoucí směny"), MANAGER("manažer");

    private final String text;

    Role(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return text;
    }
}
