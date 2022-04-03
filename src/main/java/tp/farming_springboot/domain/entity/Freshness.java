package tp.farming_springboot.domain.entity;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Freshness {

    그저_그래요(0, "그저 그래요"),
    괜찮아요(1, "괜찮아요"),
    신선해요(2, "신선해요"),
    아주_신선해요(3, "아주 신선해요");

    int id;
    String content;

    Freshness(int id, String content) {
        this.id = id;
        this.content = content;
    }
    public static Freshness fromId(int id) {
        Freshness freshness = Arrays.stream(Freshness.values())
                .filter(f -> f.getId() == id)
                .findAny().get();

        return freshness;

    }
}
