package ivk.danilo.v6.Models.Base;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class Utils {
    public static final String DEFAULT_SORTABLE_OPTION = "Default";

    @NotNull
    @Contract("_ -> new")
    public static String getSortableColumn(@NotNull String sortable) {
        int endIndex = sortable.indexOf(" (");

        return sortable.substring(0, endIndex).toLowerCase().replace(' ', '_');
    }

    @NotNull
    @Contract("_ -> new")
    public static String getSortableOrder(@NotNull String sortable) {
        int beginIndex = sortable.indexOf(" (") + 2;
        int endIndex = sortable.length() - 3;

        return sortable.substring(beginIndex, endIndex);
    }

    @NotNull
    @Contract("_ -> new")
    public static String[] createSortableOptions(@NotNull String... options) {
        int optionsLength = options.length;
        String[] sortableOptions = new String[2 * optionsLength + 1];

        sortableOptions[0] = DEFAULT_SORTABLE_OPTION;

        int i = 1;
        for (String option : options) {
            sortableOptions[i++] = option + " (ASC ▲)";
            sortableOptions[i++] = option + " (DESC ▼)";
        }

        return sortableOptions;
    }

    @Contract(pure = true)
    public static boolean isDefaultSortableOption(@NonNull String option) {
        return option.equals(DEFAULT_SORTABLE_OPTION);
    }
}
