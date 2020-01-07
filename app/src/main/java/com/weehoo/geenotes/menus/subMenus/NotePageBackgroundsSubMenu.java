package com.weehoo.geenotes.menus.subMenus;

import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;

import com.weehoo.geenotes.background.IBackground;

import java.util.ArrayList;

public class NotePageBackgroundsSubMenu {

    private MenuItem mMenuItem;
    private ArrayList<IBackground> mBackgrounds;
    private SparseArray<IBackground> mBackgroundsMap;
    private IBackground mBackground;

    public NotePageBackgroundsSubMenu(ArrayList<IBackground> backgrounds) {
        mBackgrounds = backgrounds;
        mBackground = backgrounds.get(0);
        mBackgroundsMap = new SparseArray<>(); // Populated in onCreateOptionsMenu.
    }

    /**
     * Gets the current selected background.
     * @return The current selected background.
     */
    public IBackground getBackground() {
        return mBackground;
    }

    /**
     * Initializes the backgrounds submenu by adding submenu items.
     * @param groupId The group identifier that this item should be part of.
     *        This can be used to define groups of items for batch state
     *        changes.
     * @return The default selected background.
     */
    public IBackground onCreateOptionsMenu(Menu subMenu, int groupId, int order) {
        subMenu.clear();

        for (int i = 0; i < mBackgrounds.size(); i++) {
            IBackground background = mBackgrounds.get(i);
            int iconRes = i == 0 ? background.getIconResActive() : background.getIconResInactive();

            MenuItem menuItem = subMenu.add(groupId, i, order, background.getText());
            menuItem.setIcon(iconRes);

            mBackgroundsMap.put(i, background);
        }

        // Set default selected submenu item and background.
        mMenuItem = subMenu.getItem(0);
        mBackground = mBackgrounds.get(0);

        return mBackground;
    }

    /**
     * This hook is called whenever an item in your options menu is selected.
     * The default implementation simply returns false to have the normal
     * processing happen (calling the item's Runnable or sending a message to
     * its Handler as appropriate).  You can use this method for any items
     * for which you would like to do processing without those other
     * facilities.
     *
     * <p>Derived classes should call through to the base class for it to
     * perform the default menu handling.</p>
     * @param item The menu item that was selected.
     * @return The new background.
     */
    public IBackground onOptionsItemSelected(MenuItem item) {
        // Set the previous menu item as deselected (inactive).
        mMenuItem.setIcon(mBackground.getIconResInactive());

        // Set the current menu item as selected (active).
        int itemId = item.getItemId();
        mBackground = mBackgroundsMap.get(itemId);
        mMenuItem = item;
        mMenuItem.setIcon(mBackground.getIconResActive());

        return mBackground;
    }
}
