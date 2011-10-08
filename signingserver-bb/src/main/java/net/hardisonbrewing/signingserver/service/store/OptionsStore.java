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
package net.hardisonbrewing.signingserver.service.store;

import net.hardisonbrewing.signingserver.closed.HBCID;
import net.hardisonbrewing.signingserver.model.OptionProperties;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;

public class OptionsStore {

    private static final long UID = HBCID.getUUID( OptionsStore.class );

    public static void commit() {

        PersistentObject persistentObject = PersistentStore.getPersistentObject( UID );
        persistentObject.commit();
    }

    public static OptionProperties get() {

        PersistentObject persistentObject = PersistentStore.getPersistentObject( UID );
        OptionProperties optionProperties = null;

        try {
            optionProperties = (OptionProperties) persistentObject.getContents();
        }
        catch (Exception e) {
            PersistentStore.destroyPersistentObject( UID );
        }

        boolean created = false;

        if ( optionProperties == null ) {
            optionProperties = new OptionProperties();
            persistentObject.setContents( optionProperties );
            created = true;
        }

        boolean updated = optionProperties.update();

        if ( created || updated ) {
            persistentObject.commit();
        }

        return optionProperties;
    }

    public static boolean getBoolean( String key ) {

        return get().getBoolean( key );
    }
}
