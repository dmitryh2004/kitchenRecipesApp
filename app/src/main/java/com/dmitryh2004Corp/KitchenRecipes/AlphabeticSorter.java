package com.dmitryh2004Corp.KitchenRecipes;

import java.util.Comparator;
import java.util.List;

public class AlphabeticSorter {
    public static List<Dinner> AlphabeticSort (List<Dinner> dinnerList) {
        dinnerList.sort(new Comparator<Dinner>() {
            @Override
            public int compare(Dinner o1, Dinner o2) {
                //comparing categories
                String category1 = o1.getCategory();
                String category2 = o2.getCategory();

                for (int i = 0; i < Math.min(category1.length(), category2.length()); i++) {
                    char ch1 = category1.toLowerCase().charAt(i);
                    char ch2 = category2.toLowerCase().charAt(i);
                    
                    if (ch1 < ch2) return -1;
                    if (ch1 > ch2) return 1;
                }
                if (category1.length() < category2.length()) return -1;
                if (category1.length() > category2.length()) return 1;
                //comparing names
                String name1 = o1.getName();
                String name2 = o2.getName();
                for (int i = 0; i < Math.min(name1.length(), name2.length()); i++) {
                    char ch1 = name1.toLowerCase().charAt(i);
                    char ch2 = name2.toLowerCase().charAt(i);

                    if (ch1 < ch2) return -1;
                    if (ch1 > ch2) return 1;
                }

                return Integer.compare(name1.length(), name2.length());
            }
        });
        return dinnerList;
    }
}
