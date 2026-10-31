package com.mottainai.crudinicial.util;

import org.springframework.ui.Model;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.Comparator;
import java.util.List;
import java.util.function.ToIntFunction;

// PAGINAÇÃO DAS TELAS: limita somente a tabela; Services e opções dos formulários permanecem completos.
public final class ViewPaginationUtils {
    public static final int PAGE_SIZE = 6;

    private ViewPaginationUtils() {
    }

    public static <T> void addPage(Model model, String attribute, List<T> records,
                                  int requestedPage, ToIntFunction<T> id) {
        // ID fornece ordem estável entre requisições; os filtros já foram aplicados pelo controller.
        List<T> ordered = records.stream().sorted(Comparator.comparingInt(id)).toList();
        addPage(model, attribute, ordered, requestedPage);
    }

    // O dashboard já chega ordenado por data; mantém essa ordem ao paginar os recentes.
    public static <T> void addPage(Model model, String attribute, List<T> ordered, int requestedPage) {
        int total = ordered.size();
        int pages = total == 0 ? 1 : (total - 1) / PAGE_SIZE + 1;
        int current = Math.max(1, Math.min(requestedPage, pages));
        int start = (current - 1) * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, total);
        model.addAttribute(attribute, ordered.subList(start, end));
        model.addAttribute("paginaAtual", current);
        model.addAttribute("totalPaginas", pages);
        model.addAttribute("paginasVisiveis", visiblePages(current, pages));
        model.addAttribute("totalRegistros", total);
        model.addAttribute("inicioLista", total == 0 ? 0 : start + 1);
        model.addAttribute("fimLista", end);
    }

    // Primeira/última e até duas vizinhas de cada lado; zero representa uma lacuna visual.
    private static List<Integer> visiblePages(int current, int total) {
        var selected = new TreeSet<Integer>();
        selected.add(1);
        selected.add(total);
        for (int page = Math.max(1, current - 2); page <= Math.min(total, current + 2); page++) {
            selected.add(page);
        }
        var visible = new ArrayList<Integer>();
        int previous = 0;
        for (int page : selected) {
            if (previous > 0 && page > previous + 1) visible.add(0);
            visible.add(page);
            previous = page;
        }
        return visible;
    }

}
