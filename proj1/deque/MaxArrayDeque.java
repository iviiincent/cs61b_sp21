package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {

    private Comparator<T> cmp;

    public MaxArrayDeque(Comparator<T> c) {
        super();
        cmp = c;
    }

    public T max() {
        if (size() == 0) {
            return null;
        }
        T maxT = get(0);
        for (T t : this) {
            if (cmp.compare(t, maxT) > 0) {
                maxT = t;
            }
        }
        return maxT;
    }

    public T max(Comparator<T> c) {
        Comparator<T> oc = cmp;
        cmp = c;
        T resT = max();
        cmp = oc;
        return resT;
    }
}
