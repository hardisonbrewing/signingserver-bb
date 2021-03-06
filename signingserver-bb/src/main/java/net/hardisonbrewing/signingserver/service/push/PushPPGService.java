/**
 * Copyright (c) 2011 Martin M Reed
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.hardisonbrewing.signingserver.service.push;

import net.hardisonbrewing.signingserver.SigservApplication;
import net.hardisonbrewing.signingserver.closed.Settings.BISPushInfo;
import net.hardisonbrewing.signingserver.service.icon.IconService;
import net.hardisonbrewing.signingserver.service.store.push.PushPPGStatusChangeListenerStore;
import net.hardisonbrewing.signingserver.service.store.push.PushPPGStatusStore;
import net.rim.blackberry.api.push.PushApplicationDescriptor;
import net.rim.blackberry.api.push.PushApplicationRegistry;
import net.rim.blackberry.api.push.PushApplicationStatus;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.ApplicationManagerException;
import net.rim.device.api.system.DeviceInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PushPPGService {

    private static final Logger log = LoggerFactory.getLogger( PushPPGService.class );

    private static PushApplicationDescriptor pushApplicationDescriptor;

    public static byte getStatus() {

        PushApplicationDescriptor pushApplicationDescriptor = getPushApplicationDescriptor();
        PushApplicationStatus pushApplicationStatus = PushApplicationRegistry.getStatus( pushApplicationDescriptor );
        return pushApplicationStatus.getStatus();
    }

    public static boolean shouldRegister() {

        if ( DeviceInfo.isSimulator() ) {
            return false;
        }

        switch (getStatus()) {
            case PushApplicationStatus.STATUS_ACTIVE:
            case PushApplicationStatus.STATUS_PENDING: {
                return false;
            }
            default: {
                return true;
            }
        }
    }

    public static final void waitUntilUnregistered() {

        if ( shouldRegister() ) {
            return;
        }

        PushApplicationDescriptor pushApplicationDescriptor = getPushApplicationDescriptor();
        PushApplicationRegistry.unregisterApplication( pushApplicationDescriptor );

        log.info( "Waiting for push to unregister" );

        while (!shouldRegister()) {
            try {
                Thread.sleep( 200 );
            }
            catch (InterruptedException e) {
                // do nothing
            }
        }

        log.info( "Push is no longer registered" );
    }

    public static void registerOnStartup() throws ApplicationManagerException {

        waitUntilUnregistered();
        register( true );
    }

    public static void unregister() {

        PushApplicationRegistry.unregisterApplication();
        updateStoredStatus( PushApplicationStatus.STATUS_NOT_REGISTERED );
    }

    public static int register( boolean register ) throws ApplicationManagerException {

        log.info( "Invoking PUSH un/registration application" );

        ApplicationDescriptor systemApplicationDescriptor = SigservApplication.getSystemApplicationDescriptor();

        if ( !register ) {
            ApplicationDescriptor currentApplicationDescriptor = ApplicationDescriptor.currentApplicationDescriptor();
            if ( currentApplicationDescriptor.getModuleHandle() == systemApplicationDescriptor.getModuleHandle() ) {
                unregister();
                return -1;
            }
        }

        String[] arguments = new String[] { register ? SigservApplication.PUSH_REGISTER : SigservApplication.PUSH_UNREGISTER };
        ApplicationDescriptor applicationDescriptor = new ApplicationDescriptor( systemApplicationDescriptor, arguments );
        return ApplicationManager.getApplicationManager().runApplication( applicationDescriptor );
    }

    public static void updateStoredStatus( byte status ) {

        log.info( "Updating PUSH stored status" );

        PushPPGStatusStore.put( status );
        IconService.updateIcon();

        PushPPGStatusChangeListenerStore.notifyListeners( status );
    }

    public static PushApplicationDescriptor getPushApplicationDescriptor() {

        if ( pushApplicationDescriptor == null ) {

            String[] startupArguments = { SigservApplication.PUSH_STARTUP };
            ApplicationDescriptor systemApplicationDescriptor = SigservApplication.getSystemApplicationDescriptor();
            ApplicationDescriptor applicationDescriptor = new ApplicationDescriptor( systemApplicationDescriptor, startupArguments );

            PushApplicationDescriptor pushApplicationDescriptor = new PushApplicationDescriptor( BISPushInfo.PORT );
            pushApplicationDescriptor.setAppId( BISPushInfo.APPID );
            pushApplicationDescriptor.setAppDescriptor( applicationDescriptor );
            pushApplicationDescriptor.setServerUrl( BISPushInfo.URL );
            pushApplicationDescriptor.setServerType( PushApplicationDescriptor.SERVER_TYPE_BPAS );
            PushPPGService.pushApplicationDescriptor = pushApplicationDescriptor;
        }

        return pushApplicationDescriptor;
    }
}
