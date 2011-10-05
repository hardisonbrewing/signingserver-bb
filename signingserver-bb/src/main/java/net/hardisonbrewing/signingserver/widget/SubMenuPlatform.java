/**
 * Copyright (c) 2011 Martin M Reed
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package net.hardisonbrewing.signingserver.widget;

import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.Menu;

public class SubMenuPlatform {

    public static void addSubMenu( Menu menu, MenuItem[] menuItems, String text, int ordering, int priority ) {

        for (int i = 0; i < menuItems.length; i++) {
            menu.add( menuItems[i] );
        }
    }
}