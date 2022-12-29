public class Lemon {
    private String label;
    private String partOfSpeech;
    private String reference;

    @Override
    public String toString() {
        return "Lemon{" +
                "label='" + label + '\'' +
                ", partOfSpeech='" + partOfSpeech + '\'' +
                ", reference='" + reference + '\'' +
                '}';
    }

    public Lemon(String label, String partOfSpeech, String reference) {
        this.label = label;
        this.partOfSpeech = partOfSpeech;
        this.reference = reference;
    }
}
