package com.mottainai.mottainaicrud.util;

import java.util.function.Function;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/** Mantém a mesma regra da tela: páginas fora do intervalo vão para a primeira/última. */
public final class DatabasePageUtils {
    private DatabasePageUtils() {}

    // Consulta só a página pedida; se ela ultrapassa o limite, consulta a última página disponível.
    public static <T> Page<T> load(int requestedPage, int pageSize,
                                 Function<Pageable, Page<T>> query) {
        int index = Math.max(1, requestedPage) - 1;
        Page<T> result = query.apply(PageRequest.of(index, pageSize));
        int last = Math.max(0, result.getTotalPages() - 1);
        if (index > last) result = query.apply(PageRequest.of(last, pageSize));
        return result;
    }
}
