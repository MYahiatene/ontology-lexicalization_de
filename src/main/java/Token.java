public class Token {
    private final String token;
    private final String lemma;
    private final String pos;

    public Token(String token, String lemma, String pos) {
        this.token = token;
        this.lemma = lemma;
        this.pos = pos;
    }

    public String getToken() {
        return token;
    }

    public String getLemma() {
        return lemma;
    }

    public String getPos() {
        return pos;
    }
}
