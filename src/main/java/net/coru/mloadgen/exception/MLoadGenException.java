/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.coru.mloadgen.exception;

public class MLoadGenException extends RuntimeException {

    public MLoadGenException(String message) {
        super(message);
    }

    public MLoadGenException(Exception exc) {
        super(exc);
    }

    public MLoadGenException(String message, Exception exc) {
        super(message, exc);
    }
}