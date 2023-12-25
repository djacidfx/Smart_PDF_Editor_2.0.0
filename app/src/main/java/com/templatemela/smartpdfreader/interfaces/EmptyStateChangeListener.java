package com.templatemela.smartpdfreader.interfaces;

public interface EmptyStateChangeListener {
    void filesPopulated();

    void hideNoPermissionsView();

    void setEmptyStateInvisible();

    void setEmptyStateVisible();

    void showNoPermissionsView();
}
