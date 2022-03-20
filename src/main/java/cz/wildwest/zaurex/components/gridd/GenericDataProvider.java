package cz.wildwest.zaurex.components.gridd;

import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.SortDirection;
import cz.wildwest.zaurex.data.AbstractEntity;
import cz.wildwest.zaurex.data.service.GenericService;
import cz.wildwest.zaurex.data.service.repository.GenericRepository;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class GenericDataProvider <T extends AbstractEntity, S extends GenericService<T, ? extends GenericRepository<T>>> extends AbstractBackEndDataProvider<T, GridFilter> {

    protected final S service;
    private final Class<T> tClass;

    public GenericDataProvider(S service, Class<T> tClass) {
        this.service = service;
        this.tClass = tClass;
    }

    @Override
    //idk why query sort info isn't being passed
    protected Stream<T> fetchFromBackEnd(Query<T, GridFilter> query) {
        int offset = query.getOffset();
        int limit = query.getLimit();
        Stream<T> stream = service.findAll().stream();
        if (query.getFilter().isPresent()) {
            stream = stream
                    .filter(predicate(query.getFilter().get()))
                    .sorted(comparator(query.getFilter().get()));
        }
        return stream.skip(offset).limit(limit);
    }

    private Predicate<T> predicate(GridFilter filter) {
        return filter.getConstraints().entrySet().stream()
                .map(constraint -> (Predicate<T>) objekt -> {
                    try {
                        Object value = valueOf(constraint.getKey(), objekt);
                        return value != null && value.toString().toLowerCase()
                                .contains(constraint.getValue().toLowerCase());
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                })
                .reduce(Predicate::and)
                .orElse(e -> true);
    }

    private Comparator<T> comparator(GridFilter filter) {
        return filter.getSortOrders().entrySet().stream()
                .map(sortClause -> {
                    try {
                        @SuppressWarnings({"unchecked", "rawtypes"})
                        Comparator<T> comparator = Comparator.comparing(objekt -> (Comparable) valueOf(sortClause.getKey(), objekt));
                        if (sortClause.getValue() == SortDirection.DESCENDING) {
                            comparator = comparator.reversed();
                        }
                        return comparator;
                    } catch (Exception ex) {
                        return (Comparator<T>) (o1, o2) -> 0;
                    }
                })
                .reduce(Comparator::thenComparing)
                .orElse((o1, o2) -> 0);
    }

    private Object valueOf(String fieldName, T object) {
        try {
            Field field = tClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(object);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    protected int sizeInBackEnd(Query<T, GridFilter> query) {
        long count = fetchFromBackEnd(query).count();
        return (int) count;
    }

    public void save(T objekt) {
        service.save(objekt);
    }

    public void delete(T objekt) {
        service.delete(objekt);
    }
}
