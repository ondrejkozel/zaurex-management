package cz.wildwest.zaurex.components.gridd;

import com.vaadin.flow.data.provider.SortDirection;

import java.util.LinkedHashMap;
import java.util.Map;

public class GridFilter {

    private final Map<String, String> constraints = new LinkedHashMap<>();
    private final Map<String, SortDirection> sortOrders = new LinkedHashMap<>();

    /**
     * Returns the filter constraint applied to the grid as a map of column to
     * filter text.
     *
     * @return all constraints for the grid
     */
    public Map<String, String> getConstraints() {
        return constraints;
    }

    /**
     * Returns the sort orders applied to the grid as a map of column to sort
     * direction. Only columns with active sorting are present.
     *
     * @return the sort orders for the grid
     */
    public Map<String, SortDirection> getSortOrders() {
        return sortOrders;
    }
}
