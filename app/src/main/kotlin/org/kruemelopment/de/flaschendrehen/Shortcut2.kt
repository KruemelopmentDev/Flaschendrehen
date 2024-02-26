package org.kruemelopment.de.flaschendrehen

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.widget.TextView
import java.util.Locale

class Shortcut2 : MainActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        showdialog = false
        super.onCreate(savedInstanceState)
        start()
        checkAGBs()
        texte()
    }

    private fun texte() {
        val sp = getSharedPreferences("Einstellungen", 0)
        val nampref = sp.getString("Namen", "")
        adde = ArrayList()
        adde!!.addAll(
            listOf(
                *nampref!!.split("§%20teiler02%§".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()))
        if (nampref.isNotEmpty()) {
            vorschlag = VorschlagAdapter(this, R.id.txtName, adde!!)
        }
        dialog = Dialog(this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen)
        dialog!!.setCancelable(false)
        dialog!!.setContentView(R.layout.alertdia)
        val tv4 = dialog!!.findViewById<TextView>(R.id.textView4)
        editText1 = dialog!!.findViewById(R.id.editText3)
        if (nampref.isNotEmpty()) {
            editText1!!.setAdapter(vorschlag)
            editText1!!.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    charSequence: CharSequence,
                    i: Int,
                    i1: Int,
                    i2: Int
                ) {
                }

                override fun onTextChanged(
                    charSequenc: CharSequence,
                    start: Int,
                    i4: Int,
                    i5: Int
                ) {
                    val elp = ArrayList<String>()
                    for (i in adde!!.indices) {
                        val check = adde!![i].lowercase(Locale.getDefault())
                        var charSequence = charSequenc.toString()
                        charSequence = charSequence.lowercase(Locale.getDefault())
                        try {
                            if (check.contains(charSequence) && check != charSequence) {
                                elp.add(adde!![i])
                            }
                        } catch (ignored: Exception) {
                        }
                    }
                    vorschlag = VorschlagAdapter(this@Shortcut2, R.layout.adapter, elp)
                    vorschlag!!.notifyDataSetChanged()
                    editText1!!.setAdapter(vorschlag)
                }

                override fun afterTextChanged(editable: Editable) {}
            })
        }
        dialog!!.show()
        layoutid = 2
        tv4.setOnClickListener {
            var help = editText1!!.text.toString()
            help = help.replace(" ", "")
            help = help.replace(",", "")
            help = help.replace("\n", "")
            if (help.isEmpty()) {
                editText1!!.setText("")
                editText1!!.hint = "Scherzkekse, bitte gebt eure Namen kommagetrennt ein"
            } else {
                help = editText1!!.text.toString()
                try {
                    while (help.endsWith(",")) help = help.substring(0, help.length - 1)
                } catch (ignored: Exception) {
                }
                try {
                    while (help.startsWith(",")) help = help.substring(1)
                } catch (ignored: Exception) {
                }
                help = help.replace("\n", "")
                val sp1 = getSharedPreferences("Einstellungen", 0)
                var nampref1 = sp1.getString("Namen", "")
                if (nampref1!!.isEmpty()) nampref1 = help else {
                    val hi = ArrayList(
                        listOf(
                            *nampref1.split("§%20teiler02%§".toRegex())
                                .dropLastWhile { it.isEmpty() }
                                .toTypedArray()))
                    var test = true
                    for (check in hi) {
                        if (check == help) {
                            test = false
                            break
                        }
                    }
                    if (test) nampref1 = "$nampref1§%20teiler02%§$help"
                }
                val sp3 = getSharedPreferences("Einstellungen", 0)
                val ede = sp3.edit()
                ede.putString("Namen", nampref1)
                ede.apply()
                namen = help.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                namenanzahl = namen.size
                dialog!!.dismiss()
                layoutid = 1
                startgame2()
            }
        }
        dialog!!.setOnKeyListener { _: DialogInterface?, i: Int, _: KeyEvent? ->
            if (i == KeyEvent.KEYCODE_BACK) {
                dialog!!.dismiss()
            }
            false
        }
    }
}
