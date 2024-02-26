package org.kruemelopment.de.flaschendrehen

import android.os.Bundle

class Shortcut1 : MainActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        showdialog = false
        super.onCreate(savedInstanceState)
        start()
        checkAGBs()
        startgame1()
    }
}
