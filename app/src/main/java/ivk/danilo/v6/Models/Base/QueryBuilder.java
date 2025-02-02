package ivk.danilo.v6.Models.Base;

import androidx.annotation.NonNull;
import androidx.annotation.ReturnThis;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import ivk.danilo.v6.Models.Base.Attribute.Attribute;

public final class QueryBuilder implements Queryable {
    private final Model model;
    private final boolean isSubFilter;
    private StringBuilder select;
    private StringBuilder from;
    private StringBuilder join;
    private StringBuilder where;
    private StringBuilder groupBy;
    private StringBuilder having;
    private StringBuilder orderBy;
    private StringBuilder limit;

    private QueryBuilder(@Nullable Model model, boolean isSubFilter) {
        this.model = model;
        this.isSubFilter = isSubFilter;
    }

    public QueryBuilder(@NotNull Model model) {
        this(model, false);

        this.from(model.getTable());
    }

    public QueryBuilder(boolean isSubFilter) {
        this(null, isSubFilter);
    }

    public QueryBuilder() {
        this(null, false);
    }

    @NotNull
    @Contract("_ -> param1")
    public QueryBuilder execute(QueryBuilder query) {
        return query;
    }

    // <editor-fold desc="Select clause functions">
    @NotNull
    @ReturnThis
    @Contract("_ -> this")
    public QueryBuilder select(@NonNull String column) {
        if (this.select == null) {
            this.select = new StringBuilder("SELECT ");
        } else {
            this.select.append(", ");
        }

        this.select.append(column);

        return this;
    }
    // </editor-fold> Select clause functions

    @NotNull
    @ReturnThis
    @Contract("_ -> this")
    public QueryBuilder select(@NonNull String... columns) {
        for (String column : columns) {
            this.select(column);
        }

        return this;
    }
    // </editor-fold> From clause functions

    // <editor-fold desc="From clause functions">
    @NotNull
    @ReturnThis
    @Contract("_, -> this")
    public QueryBuilder from(@NonNull String table) {
        this.from = new StringBuilder("\nFROM ")
                .append(table);

        return this;
    }
    // </editor-fold> Join clause functions

    // <editor-fold desc="Join clause functions">
    @NotNull
    @ReturnThis
    @Contract("_, _, _, _ -> this")
    public QueryBuilder join(@NotNull String table, @NotNull String first, @NotNull String operator, @NotNull String second) {
        if (this.join == null) {
            this.join = new StringBuilder();
        }

        this.join.append("\nJOIN ")
                 .append(table)
                 .append(" ON ")
                 .append(first)
                 .append(" ")
                 .append(operator)
                 .append(" ")
                 .append(second);

        return this;
    }

    // <editor-fold desc="Where clause functions">
    private void initializeWhere(@Nullable FilterType filterType) {
        if (this.where == null) {
            this.where = this.isSubFilter
                    ? new StringBuilder()
                    : new StringBuilder("\nWHERE ");
        } else if (filterType != null) {
            this.where.append(" ")
                      .append(this.getOperatorForFilterType(filterType))
                      .append(" ");
        }
    }

    @NotNull
    @ReturnThis
    @Contract("_, _, _, _ -> this")
    private QueryBuilder where(@NonNull String column, @NotNull String operator, @Nullable Object value, @NotNull FilterType filterType) {
        this.initializeWhere(filterType);

        if (!column.isEmpty()) {
            this.where.append(column);
        }

        if (!operator.isEmpty()) {
            this.where.append(" ")
                      .append(operator);
        }

        this.where.append(" ")
                  .append(this.bindValue(value));

        return this;
    }

    @NotNull
    @Contract("_, _, _ -> this")
    public QueryBuilder where(@NonNull String column, @NotNull String operator, @Nullable Object value) {
        return this.where(column, operator, value, FilterType.AND);
    }

    @NotNull
    @Contract("_, _ -> this")
    public QueryBuilder where(@NonNull String column, @Nullable Object value) {
        return this.where(column, "=", value);
    }

    @NotNull
    @ReturnThis
    @Contract("_ -> this")
    public QueryBuilder where(@NonNull String where) {
        this.initializeWhere(FilterType.AND);

        if (!this.isSubFilter) {
            this.where.append(" ");
        }

        this.where.append(where);

        return this;
    }

    @NotNull
    @Contract("_, _, _ -> this")
    public QueryBuilder orWhere(@NonNull String column, @NotNull String operator, @Nullable Object value) {
        return this.where(column, operator, value, FilterType.OR);
    }

    @NotNull
    @Contract("_, _ -> this")
    public QueryBuilder orWhere(@NonNull String column, @Nullable Object value) {
        return this.where(column, "=", value);
    }

    @NotNull
    @ReturnThis
    @Contract("_ -> this")
    public QueryBuilder orWhere(@NonNull String where) {
        this.initializeWhere(FilterType.OR);

        if (!this.isSubFilter) {
            this.where.append(" ");
        }

        this.where.append(where);

        return this;
    }

    @NotNull
    @ReturnThis
    @Contract("_, _ -> this")
    private QueryBuilder where(@NotNull Queryable query, @NotNull FilterType filterType) {
        this.initializeWhere(filterType);

        String where = query.execute(new QueryBuilder(true)).toString();

        if (this.containsFilterTypeOperator(where)) {
            this.where.append("(")
                      .append(where)
                      .append(")");
        } else {
            this.where.append(where);
        }

        return this;
    }

    @NotNull
    @Contract("_ -> this")
    public QueryBuilder where(@NotNull Queryable query) {
        return this.where(query, FilterType.AND);
    }

    @NotNull
    @Contract("_ -> this")
    public QueryBuilder orWhere(@NotNull Queryable query) {
        return this.where(query, FilterType.OR);
    }
    // </editor-fold> Where clause functions

    // <editor-fold desc="Group by clause functions">
    @NotNull
    @ReturnThis
    @Contract("_ -> this")
    public QueryBuilder groupBy(@NotNull String column) {
        if (this.groupBy == null) {
            this.groupBy = new StringBuilder("\nGROUP BY ");
        } else {
            this.groupBy.append(", ");
        }

        this.groupBy.append(column);

        return this;
    }
    // </editor-fold> Group by clause functions

    @NotNull
    @ReturnThis
    @Contract("_ -> this")
    public QueryBuilder groupBy(@NotNull String... columns) {
        for (String column : columns) {
            this.groupBy(column);
        }

        return this;
    }

    // <editor-fold desc="Having clause functions">
    private void initializeHaving(@Nullable FilterType filterType) {
        if (this.having == null) {
            this.having = this.isSubFilter
                    ? new StringBuilder()
                    : new StringBuilder("\nHAVING ");
        } else if (filterType != null) {
            this.having.append(" ")
                       .append(this.getOperatorForFilterType(filterType))
                       .append(" ");
        }
    }

    @NotNull
    @ReturnThis
    @Contract("_, _, _, _ -> this")
    private QueryBuilder having(@NonNull String column, @NotNull String operator, @Nullable Object value, @NotNull FilterType filterType) {
        this.initializeHaving(filterType);

        if (!column.isEmpty()) {
            this.having.append(column);
        }

        if (!operator.isEmpty()) {
            this.having.append(" ")
                       .append(operator);
        }

        this.having.append(" ")
                   .append(this.bindValue(value));

        return this;
    }

    @NotNull
    @Contract("_, _, _ -> this")
    public QueryBuilder having(@NonNull String column, @NotNull String operator, @Nullable Object value) {
        return this.having(column, operator, value, FilterType.AND);
    }

    @NotNull
    @Contract("_, _ -> this")
    public QueryBuilder having(@NonNull String column, @Nullable Object value) {
        return this.having(column, "=", value);
    }

    @NotNull
    @ReturnThis
    @Contract("_ -> this")
    public QueryBuilder having(@NonNull String having) {
        this.initializeHaving(FilterType.AND);

        if (!this.isSubFilter) {
            this.having.append(" ");
        }

        this.having.append(having);

        return this;
    }

    @NotNull
    @Contract("_, _, _ -> this")
    public QueryBuilder orHaving(@NonNull String column, @NotNull String operator, @Nullable Object value) {
        return this.having(column, operator, value, FilterType.OR);
    }

    @NotNull
    @Contract("_, _ -> this")
    public QueryBuilder orHaving(@NonNull String column, @Nullable Object value) {
        return this.orHaving(column, "=", value);
    }

    @NotNull
    @ReturnThis
    @Contract("_ -> this")
    public QueryBuilder orHaving(@NonNull String having) {
        this.initializeHaving(FilterType.OR);

        if (!this.isSubFilter) {
            this.having.append(" ");
        }

        this.having.append(having);

        return this;
    }

    @NotNull
    @ReturnThis
    @Contract("_, _ -> this")
    private QueryBuilder having(@NotNull Queryable query, @NotNull FilterType filterType) {
        this.initializeHaving(filterType);

        String having = query.execute(new QueryBuilder(true)).toString();

        if (this.containsFilterTypeOperator(having)) {
            this.having.append("(")
                       .append(having)
                       .append(")");
        } else {
            this.having.append(having);
        }

        return this;
    }

    @NotNull
    @Contract("_ -> this")
    public QueryBuilder having(@NotNull Queryable query) {
        return this.having(query, FilterType.AND);
    }

    @NotNull
    @Contract("_ -> this")
    public QueryBuilder orHaving(@NotNull Queryable query) {
        return this.having(query, FilterType.OR);
    }
    // </editor-fold> Having clause functions

    // <editor-fold desc="Order by clause functions">
    @NotNull
    @ReturnThis
    @Contract("_, _ -> this")
    public QueryBuilder orderBy(@NonNull String column, @NonNull String direction) {
        if (this.orderBy == null) {
            this.orderBy = new StringBuilder("\nORDER BY ");
        } else {
            this.orderBy.append(", ");
        }

        this.orderBy.append(column)
                    .append(" ")
                    .append(direction);

        return this;
    }

    @NotNull
    @Contract("_ -> this")
    public QueryBuilder orderByAsc(@NonNull String column) {
        return this.orderBy(column, "ASC");
    }
    // </editor-fold> Order by clause functions

    @NotNull
    @Contract("_ -> this")
    public QueryBuilder orderByDesc(@NonNull String column) {
        return this.orderBy(column, "DESC");
    }
    // </editor-fold> Limit clause functions

    // <editor-fold desc="Limit clause functions">
    @NotNull
    @ReturnThis
    @Contract("_ -> this")
    public QueryBuilder limit(@NotNull Integer limit) {
        this.limit = new StringBuilder("\nLIMIT ")
                .append(limit);

        return this;
    }
    // </editor-fold> Limit clause functions

    // <editor-fold desc="CRUD functions">
    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public Model create(@NotNull Attribute... attributes) {
        if (this.model == null) {
            return new Model();
        }

        this.model.setQueryBuilder(this);

        return this.model.create(new Attributes(attributes));
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public Model update(@NotNull Attribute... attributes) {
        if (this.model == null) {
            return new Model();
        }

        this.model.setQueryBuilder(this);

        return this.model.update(new Attributes(attributes));
    }

    @NotNull
    @Contract(value = " -> new", pure = true)
    public List<Model> get() {
        if (this.model == null) {
            return new ArrayList<>();
        }

        this.model.setQueryBuilder(this);

        return this.model.get();
    }

    @Contract(pure = true)
    public void delete() {
        if (this.model == null) {
            return;
        }

        this.model.delete();
    }

    @NotNull
    @Contract(pure = true)
    public String[] pluck(@NotNull String column) {
        if (this.model == null) {
            return new String[]{};
        }

        this.model.setQueryBuilder(this);

        return this.model.pluck(column);
    }
    // </editor-fold> CRUD functions

    // <editor-fold desc="When functions">
    @NotNull
    @Contract("_, _, -> this")
    public QueryBuilder whenWhere(@NotNull Boolean condition, @NotNull Queryable query) {
        if (!condition) {
            return this;
        }

        return this.where(query);
    }

    @NotNull
    @Contract("_, _, -> this")
    public QueryBuilder whenHaving(@NotNull Boolean condition, @NotNull Queryable query) {
        if (!condition) {
            return this;
        }

        return this.having(query);
    }
    // </editor-fold> When functions

    // <editor-fold desc="Helper functions">
    @NotNull
    @Contract("_, _ -> param1")
    private String bindValues(@NotNull String query, @Nullable Object[] params) {
        if (params == null || !query.contains("?")) {
            return query;
        }

        for (Object param : params) {
            query = query.replaceFirst("/?/gm", this.bindValue(param).toString());
        }

        return query;
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    private Object bindValue(@Nullable Object value) {
        if (value == null) {
            return "NULL";
        }

        if (value instanceof String || value instanceof Character) {
            return this.bindString(value.toString());
        }

        return value;
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    private String bindString(@NotNull String string) {
        return '"' + string.replace("\"", "\\\"") + '"';
    }

    @NotNull
    @Contract(pure = true)
    public String toString() {
        if (this.select == null && this.from != null) {
            this.select("*");
        }

        return this.getQuery(this.select) +
                this.getQuery(this.from) +
                this.getQuery(this.join) +
                this.getQuery(this.where) +
                this.getQuery(this.groupBy) +
                this.getQuery(this.having) +
                this.getQuery(this.orderBy) +
                this.getQuery(this.limit) +
                (this.isSubFilter ? "" : ";");
    }

    @NotNull
    @Contract("_ -> new")
    private String getQuery(@Nullable StringBuilder builder) {
        if (builder == null) {
            return "";
        }

        return builder.toString();
    }

    @NotNull
    @Contract(pure = true)
    private String getOperatorForFilterType(@NotNull FilterType filterType) {
        switch (filterType) {
            case OR:
                return "OR";
            case AND:
            default:
                return "AND";
        }
    }

    private boolean containsFilterTypeOperator(@NotNull String value) {
        return value.contains(this.getOperatorForFilterType(FilterType.AND)) ||
                value.contains(this.getOperatorForFilterType(FilterType.OR));
    }
    // <editor-fold desc="Helper functions">

    public enum FilterType {
        AND,
        OR,
    }
}
