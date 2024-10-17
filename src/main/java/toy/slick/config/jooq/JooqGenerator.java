package toy.slick.config.jooq;

import org.jooq.codegen.GeneratorStrategy;
import org.jooq.codegen.JavaGenerator;
import org.jooq.codegen.JavaWriter;
import org.jooq.meta.Definition;

@SuppressWarnings({"unused"})
public class JooqGenerator extends JavaGenerator {
    @Override
    protected void printClassAnnotations(JavaWriter out, Definition definition, GeneratorStrategy.Mode mode) {
        if (mode.equals(GeneratorStrategy.Mode.POJO)) {
            out.println("@lombok.Builder");
        }

        super.printClassAnnotations(out, definition, mode);
    }
}
