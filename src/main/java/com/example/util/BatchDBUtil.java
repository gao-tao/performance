package com.example.util;

import com.example.domain.BasePO;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/**
 * @ClassName BatchDBUtil
 **/
@Component
public class BatchDBUtil<T extends BasePO> {

    public static final Integer COUNT = 1000;

    @Transactional
    public void batchSliceOperate(Function<Collection<T>, Boolean> fun, List<T> list, int batchSize) {

        int size = list.size();
        List<T> forms = new ArrayList<>(batchSize);
        for (int i = 0; i < size; i++) {
            forms.add(list.get(i));
            if (batchSize != forms.size() && size - 1 != i) {
                continue;
            }
            fun.apply(forms);
            forms = new ArrayList<>(batchSize);
        }
    }

}
