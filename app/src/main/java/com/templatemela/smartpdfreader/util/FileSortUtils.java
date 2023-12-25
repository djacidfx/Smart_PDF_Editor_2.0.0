package com.templatemela.smartpdfreader.util;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FileSortUtils {
    public final int DATE_INDEX;
    public final int NAME_INDEX;
    public final int SIZE_DECREASING_ORDER_INDEX;
    public final int SIZE_INCREASING_ORDER_INDEX;

    private FileSortUtils() {
        this.NAME_INDEX = 0;
        this.DATE_INDEX = 1;
        this.SIZE_INCREASING_ORDER_INDEX = 2;
        this.SIZE_DECREASING_ORDER_INDEX = 3;
    }

    public void performSortOperation(int i, List<File> list) {
        if (i == 0) {
            sortByNameAlphabetical(list);
        } else if (i == 1) {
            sortFilesByDateNewestToOldest(list);
        } else if (i == 2) {
            sortFilesBySizeIncreasingOrder(list);
        } else if (i == 3) {
            sortFilesBySizeDecreasingOrder(list);
        }
    }

    private void sortByNameAlphabetical(List<File> list) {
        Collections.sort(list);
    }

    private void sortFilesByDateNewestToOldest(List<File> list) {
        Collections.sort(list, new Comparator<File>() {
            @Override
            public int compare(File file, File t1) {
                return Long.compare(file.lastModified(), t1.lastModified());
            }
        });
    }

    private void sortFilesBySizeIncreasingOrder(List<File> list) {
        Collections.sort(list, new Comparator<File>() {
            @Override
            public int compare(File file, File t1) {
                  return Long.compare(file.length(), t1.length());
            }
        });
    }

    private void sortFilesBySizeDecreasingOrder(List<File> list) {
        Collections.sort(list, new Comparator<File>() {
            @Override
            public int compare(File file, File t1) {
                return Long.compare(file.length(), t1.length());
            }
        });
    }

    private static class SingletonHolder {
        static final FileSortUtils INSTANCE = new FileSortUtils();

        private SingletonHolder() {
        }
    }

    public static FileSortUtils getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
