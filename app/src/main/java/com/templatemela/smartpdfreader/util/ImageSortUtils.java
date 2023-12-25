package com.templatemela.smartpdfreader.util;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ImageSortUtils {
    private static final int DATE_ASC = 2;
    private static final int DATE_DESC = 3;
    private static final int NAME_ASC = 0;
    private static final int NAME_DESC = 1;

    private static class SingletonHolder {
        static final ImageSortUtils INSTANCE = new ImageSortUtils();

        private SingletonHolder() {
        }
    }

    public static ImageSortUtils getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void performSortOperation(int i, List<String> list) {
        if (i < NAME_ASC || i > DATE_DESC) {
            throw new IllegalArgumentException("Invalid sort option. Sort option must be in [0; 3] range!");
        } else if (i == NAME_ASC) {
            sortByNameAsc(list);
        } else if (i == NAME_DESC) {
            sortByNameDesc(list);
        } else if (i == DATE_ASC) {
            sortByDateAsc(list);
        } else {
            sortByDateDesc(list);
        }
    }

    private void sortByNameAsc(List<String> list) {
        Collections.sort(list, new Comparator<String>() {
            @Override
            public int compare(String s, String t1) {
                return s.substring(s.lastIndexOf(47)).compareTo(t1.substring(t1.lastIndexOf(47)));
            }
        });
    }

    private void sortByNameDesc(List<String> list) {
        Collections.sort(list, new Comparator<String>() {
            @Override
            public int compare(String s, String t1) {
                return t1.substring(t1.lastIndexOf(47)).compareTo(s.substring(s.lastIndexOf(47)));
            }
        });
    }

    private void sortByDateAsc(List<String> list) {
        Collections.sort(list, new Comparator<String>() {
            @Override
            public int compare(String s, String t1) {
                return Long.compare(new File(s).lastModified(), new File(t1).lastModified());
            }
        });
    }

    private void sortByDateDesc(List<String> list) {
        Collections.sort(list, new Comparator<String>() {
            @Override
            public int compare(String s, String t1) {
                return Long.compare(new File(s).lastModified(), new File(t1).lastModified());
            }
        });
    }
}
