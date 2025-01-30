package ivk.danilo.v6.Models.Base;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface Queryable {
    @NotNull
    @Contract("_ -> param1")
    QueryBuilder execute(QueryBuilder query);
}
