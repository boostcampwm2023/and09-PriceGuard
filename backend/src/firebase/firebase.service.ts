import { Injectable } from '@nestjs/common';
import * as admin from 'firebase-admin';
import { serviceAccount } from './firebase.config';

@Injectable()
export class FirebaseService {
    constructor() {
        admin.initializeApp({
            credential: admin.credential.cert(serviceAccount as admin.ServiceAccount),
        });
    }

    getMessaging() {
        return admin.messaging();
    }
}
