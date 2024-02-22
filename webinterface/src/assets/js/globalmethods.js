/*
 * Copyright (c) SimonPlays15 2024. All Rights Reserved
 */

import {Alert} from 'bootstrap';
import {SET_AUTHENTICATION, SET_TOKEN, SET_USERNAME} from "@/store/storeconstants.js";
import router from "@/router/index.js";

export function addAlert(type, title, message, addTimeout = true, closeable = true) {
    function uuidv4() {
        return ([1e7] + -1e3 + -4e3 + -8e3 + -1e11).replace(/[018]/g, (c) =>
            (c ^ (crypto.getRandomValues(new Uint8Array(1))[0] & (15 >> (c / 4)))).toString(16)
        )
    }

    const randomId = `alert-${uuidv4()}`;
    let aAlert = '<div id="' + randomId + '" class="alert alert-' + type + ' alert-dismissible fade show" role="alert">\n' +
        '          <strong>' + title + '</strong> ' + message +
        '          <button aria-label="Close" class="btn-close" data-bs-dismiss="alert" type="button"></button>\n' +
        '        </div>';

    if (!closeable)
        aAlert = '<div id="' + randomId + '" class="alert alert-' + type + ' fade show" role="alert">\n' +
            '          <strong>' + title + '</strong> ' + message +
            '        </div>';


    document.getElementById("alert-container").innerHTML += aAlert;
    if (addTimeout)
        setTimeout(() => {
            const exists = document.getElementById(`${randomId}`);
            if (exists)
                new Alert(`#${randomId}`, '').close();
        }, 10000)
}

export function logout(store) {
    store.commit(`auth/${SET_AUTHENTICATION}`, false);
    store.commit(`auth/${SET_TOKEN}`, undefined);
    store.commit(`auth/${SET_USERNAME}`, "");
    router.push(router.getRoutes().find(a => a.name === "home"))
}