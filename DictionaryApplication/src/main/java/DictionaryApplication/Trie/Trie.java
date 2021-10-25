package DictionaryApplication.Trie;

import java.util.*;

public class Trie {
    protected final Map<Character, Trie> children;
    protected String content;
    protected boolean terminal = false;

    public Trie() {
        this(null);
    }

    private Trie(String content) {
        this.content = content;
        children = new HashMap<Character, Trie>();
    }

    protected void add(char character) {
        String s;
        if (this.content == null) s = Character.toString(character);
        else s = this.content + character;
        children.put(character, new Trie(s));
    }

    public void insert(String diagnosis) {
        if (diagnosis == null) throw new IllegalArgumentException("Null diagnoses entries are not valid.");
        Trie node = this;
        for (char c : diagnosis.toCharArray()) {
            if (!node.children.containsKey(c)) node.add(c);
            node = node.children.get(c);
        }
        node.terminal = true;
    }

    public List<String> autoComplete(String prefix) {
        Trie trieNode = this;
        for (char c : prefix.toCharArray()) {
            if (!trieNode.children.containsKey(c)) return null;
            trieNode = trieNode.children.get(c);
        }
        return trieNode.allPrefixes();
    }

    protected List<String> allPrefixes() {
        List<String> diagnosisResults = new ArrayList<String>();
        if (this.terminal) diagnosisResults.add(this.content);
        for (Map.Entry<Character, Trie> entry : children.entrySet()) {
            Trie child = entry.getValue();
            Collection<String> childPrefixes = child.allPrefixes();
            diagnosisResults.addAll(childPrefixes);
        }
        return diagnosisResults;
    }
}
