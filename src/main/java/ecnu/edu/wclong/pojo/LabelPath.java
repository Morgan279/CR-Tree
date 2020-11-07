package ecnu.edu.wclong.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.neo4j.graphdb.Label;

import java.util.List;
import java.util.Objects;

@Getter
@AllArgsConstructor
public class LabelPath {

    private List<Label> labelSequence;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LabelPath)) return false;
        LabelPath labelPath = (LabelPath) o;

        if (labelPath.getLabelSequence().size() != this.labelSequence.size()) {
            return false;
        }

        for (int i = 0; i < labelSequence.size(); ++i) {
            if (!labelSequence.get(i).equals(labelPath.getLabelSequence().get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLabelSequence().stream().map(Label::name).reduce(String::concat));
    }
}
