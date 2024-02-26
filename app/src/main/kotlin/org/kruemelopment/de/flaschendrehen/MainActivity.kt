package org.kruemelopment.de.flaschendrehen

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import android.provider.Settings
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.WindowInsets
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.view.animation.TranslateAnimation
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatDrawableManager
import androidx.core.text.HtmlCompat
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.Random
import kotlin.math.sqrt

open class MainActivity : Activity() {
    private var imageView: ImageView? = null
    var namenanzahl = 0
    lateinit var namen: Array<String>
    var aniint = 0
    private var lastAngle = -1
    var layoutid = 0
    var drehhilfe = 0
    var angle = 0
    var pl = 0
    var dialog: Dialog? = null
    private var help = 0
    var bildint = 0
    var modechoose = false
    var vorschlag: VorschlagAdapter? = null
    var editText1: AutoCompleteTextView? = null
    var adde: ArrayList<String>? = null
    var aufgabenAdapter: AufgabenAdapter? = null
    private var editText: EditText? = null
    private var image: ImageView? = null
    private var listeaufgaben: ListView? = null
    private var welcheaufgaben = 1
    var nochmadrehen = true
    private var pflichtliste: MutableList<Liste>? = null
    private var wahrheitsliste: MutableList<Liste>? = null
    var button: Button? = null
    var showdialog = true
    private var exporttypepflicht = false

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        setFullScreen()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkAGBs()
        start()
        handlelink(intent)
    }

    @SuppressLint("ClickableViewAccessibility")
    fun start() {
        setContentView(R.layout.test)
        layoutid = 0
        bildint = 0
        lastAngle = 0
        aniint = 0
        pl = 0
        drehhilfe = 0
        val spe = getSharedPreferences("Einstellungen", 0)
        nochmadrehen = spe.getBoolean("Nochmal", true)
        val ohnenamen = BitmapFactory.decodeResource(resources, R.drawable.spielohnenamen)
        val mitnamen = BitmapFactory.decodeResource(resources, R.drawable.spielmitnamen)
        val reihenfolge = BitmapFactory.decodeResource(resources, R.drawable.spielreihenfolge)
        val left = getBitmapFromVectorDrawable(this, R.drawable.ic_keyboard_arrow_left_black_48px)
        val right = getBitmapFromVectorDrawable(this, R.drawable.ic_keyboard_arrow_right_black_48px)
        val bild = findViewById<ImageView>(R.id.imageView3)
        val links = findViewById<ImageView>(R.id.imageView6)
        val rechts = findViewById<ImageView>(R.id.imageView5)
        links.setImageBitmap(left)
        rechts.setImageBitmap(right)
        val info = findViewById<ImageView>(R.id.imageView12)
        val einstellungen = findViewById<ImageView>(R.id.imageView4)
        val art = findViewById<TextView>(R.id.textView2)
        val starten = findViewById<Button>(R.id.button12)
        art.text = getString(R.string.classic_game)
        bild.setImageBitmap(mitnamen)
        val outtoRight: Animation = TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, +1.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f
        )
        outtoRight.duration = 200
        outtoRight.interpolator = AccelerateInterpolator()
        val outtoLeft: Animation = TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, -1.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f
        )
        outtoLeft.duration = 200
        outtoLeft.interpolator = AccelerateInterpolator()
        val infromRight: Animation = TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, +1.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f
        )
        infromRight.duration = 200
        infromRight.interpolator = AccelerateInterpolator()
        val infromLeft: Animation = TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, -1.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f
        )
        infromLeft.duration = 200
        infromLeft.interpolator = AccelerateInterpolator()
        links.setOnClickListener(View.OnClickListener {
            if (modechoose) {
                return@OnClickListener
            }
            bild.startAnimation(outtoLeft)
            art.startAnimation(outtoLeft)
            outtoLeft.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {
                    modechoose = true
                }

                override fun onAnimationEnd(animation: Animation) {
                    var help = 0
                    if (bildint == 0) {
                        bild.setImageBitmap(reihenfolge)
                        art.text = getString(R.string.ordered_game)
                        help = 2
                    }
                    if (bildint == 1) {
                        bild.setImageBitmap(mitnamen)
                        art.text = getString(R.string.classic_game)
                        help = 0
                    }
                    if (bildint == 2) {
                        bild.setImageBitmap(ohnenamen)
                        art.text = getString(R.string.noname_game)
                        help = 1
                    }
                    bildint = help
                    bild.startAnimation(infromRight)
                    art.startAnimation(infromRight)
                    infromRight.setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationStart(animation: Animation) {}
                        override fun onAnimationEnd(animation: Animation) {
                            modechoose = false
                        }

                        override fun onAnimationRepeat(animation: Animation) {}
                    })
                }

                override fun onAnimationRepeat(animation: Animation) {}
            })
        })
        rechts.setOnClickListener(View.OnClickListener {
            if (modechoose) {
                return@OnClickListener
            }
            bild.startAnimation(outtoRight)
            art.startAnimation(outtoRight)
            outtoRight.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {
                    modechoose = true
                }

                override fun onAnimationEnd(animation: Animation) {
                    var help = 0
                    if (bildint == 0) {
                        bild.setImageBitmap(ohnenamen)
                        art.text = getString(R.string.noname_game)
                        help = 1
                    }
                    if (bildint == 1) {
                        bild.setImageBitmap(reihenfolge)
                        art.text = getString(R.string.ordered_game)
                        help = 2
                    }
                    if (bildint == 2) {
                        bild.setImageBitmap(mitnamen)
                        art.text = getString(R.string.classic_game)
                        help = 0
                    }
                    bildint = help

                    bild.startAnimation(infromLeft)
                    art.startAnimation(infromLeft)
                    infromLeft.setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationStart(animation: Animation) {}
                        override fun onAnimationEnd(animation: Animation) {
                            modechoose = false
                        }

                        override fun onAnimationRepeat(animation: Animation) {}
                    })
                }

                override fun onAnimationRepeat(animation: Animation) {}
            })
        })
        art.setOnTouchListener(object : OnSwipeDetector(this@MainActivity) {
            override fun onSwipeRight() {
                if (modechoose) {
                    return
                }
                bild.startAnimation(outtoRight)
                art.startAnimation(outtoRight)
                outtoRight.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation) {
                        modechoose = true
                    }

                    override fun onAnimationEnd(animation: Animation) {
                        var help = 0
                        if (bildint == 0) {
                            bild.setImageBitmap(ohnenamen)
                            art.text = getString(R.string.noname_game)
                            help = 1
                        }
                        if (bildint == 1) {
                            bild.setImageBitmap(reihenfolge)
                            art.text = getString(R.string.ordered_game)
                            help = 2
                        }
                        if (bildint == 2) {
                            bild.setImageBitmap(mitnamen)
                            art.text = getString(R.string.classic_game)
                            help = 0
                        }
                        bildint = help
                        bild.startAnimation(infromLeft)
                        art.startAnimation(infromLeft)
                        infromLeft.setAnimationListener(object : Animation.AnimationListener {
                            override fun onAnimationStart(animation: Animation) {}
                            override fun onAnimationEnd(animation: Animation) {
                                modechoose = false
                            }

                            override fun onAnimationRepeat(animation: Animation) {}
                        })
                    }

                    override fun onAnimationRepeat(animation: Animation) {}
                })
            }

            override fun onSwipeLeft() {
                if (modechoose) {
                    return
                }
                bild.startAnimation(outtoRight)
                art.startAnimation(outtoRight)
                outtoRight.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation) {
                        modechoose = true
                    }

                    override fun onAnimationEnd(animation: Animation) {
                        var help = 0
                        if (bildint == 0) {
                            bild.setImageBitmap(reihenfolge)
                            art.text = getString(R.string.ordered_game)
                            help = 2
                        }
                        if (bildint == 1) {
                            bild.setImageBitmap(mitnamen)
                            art.text = getString(R.string.classic_game)
                            help = 0
                        }
                        if (bildint == 2) {
                            bild.setImageBitmap(ohnenamen)
                            art.text = getString(R.string.noname_game)
                            help = 1
                        }
                        bildint = help
                        bild.startAnimation(infromRight)
                        art.startAnimation(infromRight)
                        infromRight.setAnimationListener(object : Animation.AnimationListener {
                            override fun onAnimationStart(animation: Animation) {}
                            override fun onAnimationEnd(animation: Animation) {
                                modechoose = false
                            }

                            override fun onAnimationRepeat(animation: Animation) {}
                        })
                    }

                    override fun onAnimationRepeat(animation: Animation) {}
                })
            }
        })
        bild.setOnTouchListener(object : OnSwipeDetector(this@MainActivity) {
            override fun onSwipeRight() {
                if (modechoose) {
                    return
                }
                bild.startAnimation(outtoRight)
                art.startAnimation(outtoRight)
                outtoRight.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation) {
                        modechoose = true
                    }

                    override fun onAnimationEnd(animation: Animation) {
                        var help = 0
                        if (bildint == 0) {
                            bild.setImageBitmap(ohnenamen)
                            art.text = getString(R.string.noname_game)
                            help = 1
                        }
                        if (bildint == 1) {
                            bild.setImageBitmap(reihenfolge)
                            art.text = getString(R.string.ordered_game)
                            help = 2
                        }
                        if (bildint == 2) {
                            bild.setImageBitmap(mitnamen)
                            art.text = getString(R.string.classic_game)
                            help = 0
                        }
                        bildint = help
                        bild.startAnimation(infromLeft)
                        art.startAnimation(infromLeft)
                        infromLeft.setAnimationListener(object : Animation.AnimationListener {
                            override fun onAnimationStart(animation: Animation) {}
                            override fun onAnimationEnd(animation: Animation) {
                                modechoose = false
                            }

                            override fun onAnimationRepeat(animation: Animation) {}
                        })
                    }

                    override fun onAnimationRepeat(animation: Animation) {}
                })
            }

            override fun onSwipeLeft() {
                if (modechoose) {
                    return
                }
                bild.startAnimation(outtoRight)
                art.startAnimation(outtoRight)
                outtoRight.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation) {
                        modechoose = true
                    }

                    override fun onAnimationEnd(animation: Animation) {
                        var help = 0
                        if (bildint == 0) {
                            bild.setImageBitmap(reihenfolge)
                            art.text = getString(R.string.ordered_game)
                            help = 2
                        }
                        if (bildint == 1) {
                            bild.setImageBitmap(mitnamen)
                            art.text = getString(R.string.classic_game)
                            help = 0
                        }
                        if (bildint == 2) {
                            bild.setImageBitmap(ohnenamen)
                            art.text = getString(R.string.noname_game)
                            help = 1
                        }
                        bildint = help
                        val inFromRight: Animation = TranslateAnimation(
                            Animation.RELATIVE_TO_PARENT, +1.0f,
                            Animation.RELATIVE_TO_PARENT, 0.0f,
                            Animation.RELATIVE_TO_PARENT, 0.0f,
                            Animation.RELATIVE_TO_PARENT, 0.0f
                        )
                        inFromRight.duration = 200
                        inFromRight.interpolator = AccelerateInterpolator()
                        bild.startAnimation(inFromRight)
                        art.startAnimation(inFromRight)
                        inFromRight.setAnimationListener(object : Animation.AnimationListener {
                            override fun onAnimationStart(animation: Animation) {}
                            override fun onAnimationEnd(animation: Animation) {
                                modechoose = false
                            }

                            override fun onAnimationRepeat(animation: Animation) {}
                        })
                    }

                    override fun onAnimationRepeat(animation: Animation) {}
                })
            }
        })
        info.setOnClickListener {
            val dialo = Dialog(this@MainActivity, R.style.AppDialog)
            dialo.setCancelable(true)
            dialo.setContentView(R.layout.settings)
            val relativeLayout = dialo.findViewById<RelativeLayout>(R.id.back5)
            relativeLayout.setOnClickListener { dialo.dismiss() }
            layoutid = 1
            val nutzung = dialo.findViewById<TextView>(R.id.textView5)
            val daten = dialo.findViewById<TextView>(R.id.textView3)
            val kontakt = dialo.findViewById<TextView>(R.id.textView24)
            val wahrheit = dialo.findViewById<TextView>(R.id.textView16)
            val pflicht = dialo.findViewById<TextView>(R.id.textView17)
            val eigenew = dialo.findViewById<TextView>(R.id.textView18)
            val eigenep = dialo.findViewById<TextView>(R.id.textView19)
            val aufgabe = resources.getStringArray(R.array.wahrheiten)
            var helper=aufgabe.size+1
            wahrheit.text = helper.toString()
            val pflichtstr = resources.getStringArray(R.array.pflichten)
            helper=pflichtstr.size+1
            pflicht.text = helper.toString()
            var eigw = 0
            var eigp = 0
            val myDB = DataBaseHelper(this@MainActivity)
            val res = myDB.allData
            if (res.count > 0) {
                while (res.moveToNext()) {
                    if (res.getString(2) == "Wahrheit") {
                        eigw += 1
                    } else {
                        eigp += 1
                    }
                }
            }
            eigenew.text = eigw.toString()
            eigenep.text = eigp.toString()
            daten.setOnClickListener {
                val uri = Uri.parse("https://www.kruemelopment-dev.de/datenschutzerklaerung")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            }
            nutzung.setOnClickListener {
                val uri = Uri.parse("https://www.kruemelopment-dev.de/nutzungsbedingungen")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            }
            kontakt.setOnClickListener {
                val intent = Intent(Intent.ACTION_SENDTO)
                intent.setData(Uri.parse("mailto:kontakt@kruemelopment-dev.de"))
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            dialo.setOnKeyListener { _, i, _ ->
                if (i == KeyEvent.KEYCODE_BACK) {
                    dialo.dismiss()
                }
                false
            }
            dialo.show()
        }
        einstellungen.setOnClickListener {
            val dialo = Dialog(this@MainActivity, R.style.AppDialog)
            dialo.setCancelable(true)
            dialo.setContentView(R.layout.einstellungen)
            val relativeLayout = dialo.findViewById<RelativeLayout>(R.id.back6)
            relativeLayout.setOnClickListener { dialo.dismiss() }
            layoutid = 1
            val myDB = DataBaseHelper(this@MainActivity)
            val loschen = dialo.findViewById<Button>(R.id.button13)
            loschen.setOnClickListener {
                delete(getString(R.string.delete_name_proposes),getString(R.string.delete_name_proposes_text),getString(R.string.name_proposals_deleted),0)
            }
            val low = dialo.findViewById<Button>(R.id.button2)
            val lop = dialo.findViewById<Button>(R.id.button)
            low.setOnClickListener {
                delete(getString(R.string.delete_truth),getString(R.string.delete_truth_text),getString(R.string.all_truth_deleted),1)
            }
            lop.setOnClickListener {
                delete(getString(R.string.delete_dares),getString(R.string.delete_dares_text),getString(R.string.all_dares_deleted),2)
            }
            dialo.setOnKeyListener { _, i, _ ->
                if (i == KeyEvent.KEYCODE_BACK) {
                    dialo.dismiss()
                }
                false
            }
            val res = myDB.allData
            var wahr = 0
            var pfli = 0
            if (res.count > 0) {
                while (res.moveToNext()) {
                    if (res.getString(2) == "Wahrheit") wahr += 1 else pfli += 1
                }
            }
            val sp = getSharedPreferences("Einstellungen", 0)
            val welr = sp.getInt("Aufgaben", 1)
            val app = dialo.findViewById<CheckBox>(R.id.checkBox)
            val eigene = dialo.findViewById<CheckBox>(R.id.checkBox2)
            if (welr == 0) {
                app.isChecked = false
                eigene.isChecked = false
            }
            if (welr == 1) {
                eigene.isChecked = false
                app.isChecked = true
            }
            if (welr == 2) {
                app.isChecked = false
                eigene.isChecked = true
            }
            if (wahr < 10 || pfli < 10) {
                val ede = sp.edit()
                ede.putInt("Aufgaben", 1)
                ede.apply()
                eigene.isChecked = false
                eigene.isEnabled = false
                app.isChecked = true
                app.isEnabled = false
                eigene.setOnClickListener {
                    Toast.makeText(
                        this@MainActivity,
                        getString(R.string.created_mincount_of_options),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                eigene.setOnCheckedChangeListener { _, b ->
                    val eingabe: Int
                    if (b) {
                        eingabe = 2
                        if (app.isChecked) app.isChecked = false
                    } else {
                        eingabe = 0
                    }
                    val ede = sp.edit()
                    ede.putInt("Aufgaben", eingabe)
                    ede.apply()
                }
            }
            app.setOnCheckedChangeListener { _, b ->
                val eingabe: Int
                if (b) {
                    eingabe = 1
                    if (eigene.isChecked) eigene.isChecked = false
                } else {
                    eingabe = 0
                }
                val ede = sp.edit()
                ede.putInt("Aufgaben", eingabe)
                ede.apply()
            }
            val checke = sp.getBoolean("Nochmal", true)
            val checkBox = dialo.findViewById<CheckBox>(R.id.checkBox3)
            checkBox.isChecked = checke
            checkBox.setOnCheckedChangeListener { _, b ->
                val ede = sp.edit()
                ede.putBoolean("Nochmal", b)
                ede.apply()
                nochmadrehen = b
            }
            dialo.show()
        }
        starten.setOnClickListener(View.OnClickListener {
            val sp = getSharedPreferences("Einstellungen", 0)
            val nampref = sp.getString("Namen", "")
            adde = ArrayList()
            adde!!.addAll(
                mutableListOf(
                    *nampref!!.split("§%20teiler02%§".toRegex()).dropLastWhile { it.isEmpty() }
                        .toTypedArray()))
            if (nampref.isNotEmpty()) {
                vorschlag = VorschlagAdapter(this@MainActivity, R.id.txtName, adde!!)
            }
            if (modechoose) {
                return@OnClickListener
            }
            if (bildint == 0) {
                dialog = Dialog(this@MainActivity, R.style.AppDialog)
                dialog!!.setCancelable(false)
                dialog!!.setContentView(R.layout.alertdia)
                val relativeLayout = dialog!!.findViewById<RelativeLayout>(R.id.back1)
                relativeLayout.setOnClickListener { dialog!!.dismiss() }
                val tv4 = dialog!!.findViewById<TextView>(R.id.textView4)
                editText1 = dialog!!.findViewById(R.id.editText3)
                dialog!!.show()
                layoutid = 2
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
                            vorschlag = VorschlagAdapter(this@MainActivity, R.layout.adapter, elp)
                            vorschlag!!.notifyDataSetChanged()
                            editText1!!.setAdapter(vorschlag)
                        }

                        override fun afterTextChanged(editable: Editable) {}
                    })
                }
                tv4.setOnClickListener {
                    var help = editText1!!.text.toString()
                    help = help.replace(" ", "")
                    help = help.replace(",", "")
                    help = help.replace("\n", "")
                    if (help.isEmpty()) {
                        editText1!!.setText("")
                        editText1!!.hint = getString(R.string.insert_names_error_msg)
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
                        var nampref2 = sp.getString("Namen", "")
                        if (nampref2.isNullOrEmpty()) nampref2 = help else {
                            val hi = ArrayList(
                                mutableListOf(
                                    *nampref2.split("§%20teiler02%§".toRegex())
                                        .dropLastWhile { it.isEmpty() }
                                        .toTypedArray()))
                            var test = true
                            for (check in hi) {
                                if (check == help) {
                                    test = false
                                    break
                                }
                            }
                            if (test) nampref2 = "$nampref2§%20teiler02%§$help"
                        }
                        val ede = sp.edit()
                        ede.putString("Namen", nampref2)
                        ede.apply()
                        namen =
                            help.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                        namenanzahl = namen.size
                        dialog!!.dismiss()
                        layoutid = 1
                        startgame()
                    }
                }
                dialog!!.setOnKeyListener { _, i, _ ->
                    if (i == KeyEvent.KEYCODE_BACK) {
                        dialog!!.dismiss()
                    }
                    false
                }
            }
            else if (bildint == 1) {
                layoutid = 1
                startgame1()
            }
            else if (bildint == 2) {
                dialog = Dialog(this@MainActivity, R.style.AppDialog)
                dialog!!.setCancelable(false)
                dialog!!.setContentView(R.layout.alertdia)
                val relativeLayout = dialog!!.findViewById<RelativeLayout>(R.id.back1)
                relativeLayout.setOnClickListener { dialog!!.dismiss() }
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
                            vorschlag = VorschlagAdapter(this@MainActivity, R.layout.adapter, elp)
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
                        editText1!!.hint = getString(R.string.insert_names_error_msg)
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
                        var nampref2 = sp.getString("Namen", "")
                        if (nampref2 == null || nampref2!!.isEmpty()) nampref2 = help else {
                            val hi = ArrayList(
                                mutableListOf(
                                    *nampref2!!.split("§%20teiler02%§".toRegex())
                                        .dropLastWhile { it.isEmpty() }
                                        .toTypedArray()))
                            var test = true
                            for (check in hi) {
                                if (check == help) {
                                    test = false
                                    break
                                }
                            }
                            if (test) nampref2 = "$nampref2§%20teiler02%§$help"
                        }
                        val ede = sp.edit()
                        ede.putString("Namen", nampref2)
                        ede.apply()
                        namen =
                            help.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                        namenanzahl = namen.size
                        dialog!!.dismiss()
                        layoutid = 1
                        startgame2()
                    }
                }
                dialog!!.setOnKeyListener { _, i, _ ->
                    if (i == KeyEvent.KEYCODE_BACK) {
                        dialog!!.dismiss()
                    }
                    false
                }
            }
        })
        val fragezeichen = findViewById<ImageView>(R.id.imageView7)
        val pflicht = findViewById<ImageView>(R.id.imageView8)
        fragezeichen.setOnClickListener {
            showOwnQuestions(0)
        }
        pflicht.setOnClickListener {
            showOwnQuestions(1)
        }
        button = findViewById(R.id.button9)
        button!!.setOnClickListener {
            if (!Settings.canDrawOverlays(this@MainActivity)) {
                val intent2 = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startActivityForResult(intent2, 107)
            } else {
                if (!isMyServiceRunning) {
                    startService(Intent(this@MainActivity, Popup::class.java))
                    finishAndRemoveTask()
                } else {
                    stopService(Intent(this@MainActivity, Popup::class.java))
                    button!!.text = getString(R.string.start_mini_version)
                }
            }
        }
        if (isMyServiceRunning) {
            button!!.text = getString(R.string.stop_mini_version)
        }
    }

    fun startgame1() {
        setContentView(R.layout.game2)
        layoutid = 4
        val sp = getSharedPreferences("Einstellungen", 0)
        welcheaufgaben = sp.getInt("Aufgaben", 1)
        imageView = findViewById(R.id.imageView)
        imageView!!.setOnClickListener { imageviewforgame1() }
    }

    fun startgame() {
        layoutid = 3
        val sp = getSharedPreferences("Einstellungen", 0)
        welcheaufgaben = sp.getInt("Aufgaben", 1)
        setContentView(R.layout.game)
        val tvlayout = findViewById<RelativeLayout>(R.id.real)
        val metrics  = Resources.getSystem().displayMetrics
        imageView = findViewById(R.id.imageView)
        for (i in namen.indices) {
            val name = TextView(this)
            name.text = namen[i]
            val yInches = metrics.heightPixels / metrics.ydpi
            val xInches = metrics.widthPixels / metrics.xdpi
            val diagonalInches = sqrt((xInches * xInches + yInches * yInches).toDouble())
            if (diagonalInches >= 6.5) {
                name.textSize = 40f
            } else {
                name.textSize = 20f
            }
            name.setTypeface(null, Typeface.BOLD)
            name.setTextColor(Color.BLACK)
            val params = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )
            params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE)
            params.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE)
            name.layoutParams = params
            name.width = metrics.heightPixels * 2 / 3
            name.gravity = Gravity.END
            name.rotation = -90f
            var dreh = 360 / namenanzahl / 2
            dreh += 360 / namenanzahl * (i + 1)
            val animRotate = RotateAnimation(
                0.0f, dreh.toFloat(),
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.0f
            )
            animRotate.duration = 5000
            animRotate.fillAfter = true
            animRotate.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {
                    aniint = 1
                }

                override fun onAnimationEnd(animation: Animation) {
                    aniint = 0
                }

                override fun onAnimationRepeat(animation: Animation) {}
            })
            name.startAnimation(animRotate)
            tvlayout.addView(name)
        }
        imageView!!.setOnClickListener { imageviewforgame() }
        val btn = findViewById<Button>(R.id.button7)
        val mageView = findViewById<ImageView>(R.id.imageView2)
        mageView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                if (btn.visibility == View.GONE) {
                    run {
                        btn.visibility = View.VISIBLE
                        val params = mageView.layoutParams as RelativeLayout.LayoutParams
                        params.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                        params.addRule(RelativeLayout.LEFT_OF, R.id.button7)
                        mageView.layoutParams = params
                        mageView.setImageResource(R.drawable.eye_off)
                    }
                } else {
                    btn.visibility = View.GONE
                    val params = mageView.layoutParams as RelativeLayout.LayoutParams
                    params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                    params.removeRule(RelativeLayout.LEFT_OF)
                    mageView.layoutParams = params
                    mageView.setImageResource(R.drawable.eye)
                }
            }
        })
        btn.setOnClickListener {
            dialog = Dialog(this@MainActivity, R.style.AppDialog)
            dialog!!.setCancelable(true)
            dialog!!.setContentView(R.layout.alertdia)
            val relativeLayout = dialog!!.findViewById<RelativeLayout>(R.id.back1)
            relativeLayout.setOnClickListener {
                dialog!!.dismiss()
                layoutid = 3
            }
            val tv4 = dialog!!.findViewById<TextView>(R.id.textView4)
            editText1 = dialog!!.findViewById(R.id.editText3)
            var edit = StringBuilder()
            for (i in namen.indices) {
                if (i == 0) edit = StringBuilder(namen[i]) else edit.append(",").append(
                    namen[i]
                )
            }
            editText1!!.setText(edit.toString())
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
                    var nampref = sp.getString("Namen", "")
                    if (nampref.isNullOrEmpty()) nampref = help else {
                        val hi = ArrayList(
                            mutableListOf(
                                *nampref.split("§%20teiler02%§".toRegex())
                                    .dropLastWhile { it.isEmpty() }
                                    .toTypedArray()))
                        var test = true
                        for (check in hi) {
                            if (check == help) {
                                test = false
                                break
                            }
                        }
                        if (test) nampref = "$nampref§%20teiler02%§$help"
                    }
                    val ede = sp.edit()
                    ede.putString("Namen", nampref)
                    ede.apply()
                    namen = help.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    namenanzahl = namen.size
                    dialog!!.dismiss()
                    layoutid = 3
                    lastAngle = 0
                    startgame()
                }
            }
            dialog!!.setOnKeyListener { _, i, _ ->
                if (i == KeyEvent.KEYCODE_BACK) {
                    dialog!!.dismiss()
                    layoutid = 3
                }
                false
            }
        }
    }

    fun chooseplayer(degree: Float) {
        pl = degree.toInt() / (360 / namenanzahl)
        pl += 1
        if (pl == 1) pl = namenanzahl + 1
        dialog(1)
    }

    private fun aufgabe(i: Int,withPlayer:Boolean) {
        val test1 = wahrheiten()
        val test2 = pflichten()
        if (test1.size < 10 || test2.size < 10) {
            val sp3 = getSharedPreferences("Einstellungen", 0)
            val ede = sp3.edit()
            ede.putInt("Aufgaben", 1)
            ede.apply()
            welcheaufgaben = 1
        }
        if (welcheaufgaben == 0) {
            if (i == 1) {
                val aufgabe = resources.getStringArray(R.array.wahrheiten)
                val spr = aufgabe[Random().nextInt(aufgabe.size)]
                val wahrheit = wahrheiten()
                val spr2 = wahrheit[Random().nextInt(wahrheit.size)]
                val za = Random().nextBoolean()
                val player= if(withPlayer) pl else -1
                if (za) ergebnis(spr,player) else ergebnis(spr2,player)
            }
            if (i == 2) {
                val pflichten = pflichten()
                val pflicht = resources.getStringArray(R.array.pflichten)
                val pflich = pflicht[Random().nextInt(pflicht.size)]
                val pflich2 = pflichten[Random().nextInt(pflichten.size)]
                val za = Random().nextBoolean()
                val player= if(withPlayer) pl else -1
                if (za) ergebnis(pflich,player) else ergebnis(pflich2,-1)
            }
            if (i == 3) {
                val konsum = resources.getStringArray(R.array.pflichten)
                val konsumeig = pflichten()
                var konsu = StringBuilder()
                for (`in` in 0..2) {
                    var help = konsum[Random().nextInt(konsum.size)]
                    var help2 = konsumeig[Random().nextInt(konsumeig.size)]
                    while (konsu.toString().contains(help!!) || konsu.toString().contains(help2)) {
                        help = konsum[Random().nextInt(konsum.size)]
                        help2 = konsumeig[Random().nextInt(konsumeig.size)]
                    }
                    val za = Random().nextBoolean()
                    if (za) help = help2
                    if (konsu.isEmpty()) konsu = StringBuilder(help) else konsu.append("§")
                        .append(help)
                }
                val player= if(withPlayer) pl else -1
                ergebniskonsum(konsu.toString().split("§".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray(),player)
            }
        }
        else if (welcheaufgaben == 1) {
            if (i == 1) {
                val aufgabe = resources.getStringArray(R.array.wahrheiten)
                val spr = aufgabe[Random().nextInt(aufgabe.size)]
                val player= if(withPlayer) pl else -1
                ergebnis(spr,player)
            }
            if (i == 2) {
                val pflicht = resources.getStringArray(R.array.pflichten)
                val pflich = pflicht[Random().nextInt(pflicht.size)]
                val player= if(withPlayer) pl else -1
                ergebnis(pflich,player)
            }
            if (i == 3) {
                val konsum = resources.getStringArray(R.array.pflichten)
                var konsu = StringBuilder()
                for (`in` in 0..2) {
                    var help = konsum[Random().nextInt(konsum.size)]
                    while (konsu.toString().contains(help!!)) {
                        help = konsum[Random().nextInt(konsum.size)]
                    }
                    if (konsu.isEmpty()) konsu = StringBuilder(help) else konsu.append("§")
                        .append(help)
                }
                val player= if(withPlayer) pl else -1
                ergebniskonsum(konsu.toString().split("§".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray(),player)
            }
        }
        else if (welcheaufgaben == 2) {
            if (i == 1) {
                val wahrheit = wahrheiten()
                val spr2 = wahrheit[Random().nextInt(wahrheit.size)]
                val player= if(withPlayer) pl else -1
                ergebnis(spr2,player)
            }
            if (i == 2) {
                val pflichten = pflichten()
                val pflich2 = pflichten[Random().nextInt(pflichten.size)]
                val player= if(withPlayer) pl else -1
                ergebnis(pflich2,player)
            }
            if (i == 3) {
                val konsumeig = pflichten()
                var konsu = StringBuilder()
                for (`in` in 0..2) {
                    var help2 = konsumeig[Random().nextInt(konsumeig.size)]
                    while (konsu.toString().contains(help2)) {
                        help2 = konsumeig[Random().nextInt(konsumeig.size)]
                    }
                    if (konsu.isEmpty()) konsu = StringBuilder(help2) else konsu.append("§")
                        .append(help2)
                }
                val player= if(withPlayer) pl else -1
                ergebniskonsum(konsu.toString().split("§".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray(),player)
            }
        }
    }

    private fun ergebnis(aufgab: String,player: Int) {
        dialog = Dialog(this@MainActivity, R.style.AppDialog)
        dialog!!.setCancelable(false)
        dialog!!.setContentView(R.layout.anzeige)
        val relativeLayout = dialog!!.findViewById<RelativeLayout>(R.id.back3)
        relativeLayout.setOnClickListener {
            dialog!!.dismiss()
            layoutid = help
        }
        val weiter = dialog!!.findViewById<Button>(R.id.button10)
        val aufgabe = dialog!!.findViewById<TextView>(R.id.textView8)
        val helper= if(player>-1){
            """
             ${namen[player - 2]}
             
             $aufgab
             
             """.trimIndent()

        } else aufgab + "\n"

        aufgabe.text = helper
        aufgabe.movementMethod = ScrollingMovementMethod()
        dialog!!.show()
        help = layoutid
        layoutid = 2
        weiter.setOnClickListener {
            layoutid = help
            dialog!!.dismiss()
        }
        dialog!!.setOnKeyListener { _, i, _ ->
            if (i == KeyEvent.KEYCODE_BACK) {
                dialog!!.dismiss()
                layoutid = help
            }
            false
        }
    }

    private fun ergebniskonsum(pflichten: Array<String>,player: Int) {
        dialog = Dialog(this@MainActivity, R.style.AppDialog)
        dialog!!.setCancelable(false)
        dialog!!.setContentView(R.layout.anzeige)
        val relativeLayout = dialog!!.findViewById<RelativeLayout>(R.id.back3)
        relativeLayout.setOnClickListener {
            dialog!!.dismiss()
            layoutid = help
        }
        val weiter = dialog!!.findViewById<Button>(R.id.button10)
        val aufgabe = dialog!!.findViewById<TextView>(R.id.textView8)
        val helper=if(player>-1){
            """
             ${namen[player - 2]}
             
             1.${pflichten[0]}
             2.${pflichten[1]}
             3.${pflichten[2]}
             
             """.trimIndent()
        }
        else{
            """
             1.${pflichten[0]}
             2.${pflichten[1]}
             3.${pflichten[2]}
             """.trimIndent()
        }
        aufgabe.text = helper
        aufgabe.movementMethod = ScrollingMovementMethod()
        dialog!!.show()
        help = layoutid
        layoutid = 2
        weiter.setOnClickListener {
            dialog!!.dismiss()
            layoutid = help
        }
        dialog!!.setOnKeyListener { _, i, _ ->
            if (i == KeyEvent.KEYCODE_BACK) {
                dialog!!.dismiss()
                layoutid = help
            }
            false
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (layoutid == 1 || layoutid == 3 || layoutid == 4 || layoutid == 5) start() else if (layoutid == 0) {
                finish()
            }
        }
        return keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP
    }

    fun startgame2() {
        setContentView(R.layout.game)
        layoutid = 5
        val sp = getSharedPreferences("Einstellungen", 0)
        welcheaufgaben = sp.getInt("Aufgaben", 1)
        val tvlayout = findViewById<RelativeLayout>(R.id.real)
        val metrics = Resources.getSystem().displayMetrics
        imageView = findViewById(R.id.imageView)
        for (i in namen.indices) {
            val name = TextView(this)
            name.text = namen[i]
            val yInches = metrics.heightPixels / metrics.ydpi
            val xInches = metrics.widthPixels / metrics.xdpi
            val diagonalInches = sqrt((xInches * xInches + yInches * yInches).toDouble())
            if (diagonalInches >= 6.5) {
                name.textSize = 40f
            } else {
                name.textSize = 20f
            }
            name.setTypeface(null, Typeface.BOLD)
            name.setTextColor(Color.BLACK)
            val params = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )
            params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE)
            params.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE)
            name.layoutParams = params
            name.width = metrics.heightPixels * 2 / 3
            name.gravity = Gravity.END
            name.rotation = -90f
            var dreh = 360 / namenanzahl / 2
            dreh += 360 / namenanzahl * (i + 1)
            val animRotate = RotateAnimation(
                0.0f, dreh.toFloat(),
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.0f
            )
            animRotate.duration = 5000
            animRotate.fillAfter = true
            name.startAnimation(animRotate)
            animRotate.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {
                    aniint = 1
                }

                override fun onAnimationEnd(animation: Animation) {
                    aniint = 0
                }

                override fun onAnimationRepeat(animation: Animation) {}
            })
            tvlayout.addView(name)
        }
        imageView!!.setOnClickListener { imageviewforgame2() }
        val btn = findViewById<Button>(R.id.button7)
        val mageView = findViewById<ImageView>(R.id.imageView2)
        mageView.setOnClickListener {
            if (btn.visibility == View.GONE) {
                btn.visibility = View.VISIBLE
                val params = mageView.layoutParams as RelativeLayout.LayoutParams
                params.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                params.addRule(RelativeLayout.LEFT_OF, R.id.button7)
                mageView.layoutParams = params
                mageView.setImageResource(R.drawable.eye_off)
            } else {
                btn.visibility = View.GONE
                val params = mageView.layoutParams as RelativeLayout.LayoutParams
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                params.removeRule(RelativeLayout.LEFT_OF)
                mageView.layoutParams = params
                mageView.setImageResource(R.drawable.eye)
            }
        }
        btn.setOnClickListener {
            dialog = Dialog(this@MainActivity, R.style.AppDialog)
            dialog!!.setCancelable(false)
            dialog!!.setContentView(R.layout.alertdia)
            val relativeLayout = dialog!!.findViewById<RelativeLayout>(R.id.back1)
            relativeLayout.setOnClickListener {
                dialog!!.dismiss()
                layoutid = 5
            }
            layoutid = 2
            val tv4 = dialog!!.findViewById<TextView>(R.id.textView4)
            editText1 = dialog!!.findViewById(R.id.editText3)
            var edit = StringBuilder()
            for (i in namen.indices) {
                if (i == 0) edit = StringBuilder(namen[i]) else edit.append(",").append(
                    namen[i]
                )
            }
            editText1!!.setText(edit.toString())
            dialog!!.show()
            tv4.setOnClickListener {
                var help = editText1!!.text.toString()
                help = help.replace(" ", "")
                help = help.replace(",", "")
                help = help.replace("\n", "")
                if (help.isEmpty()) {
                    editText1!!.setText("")
                    editText1!!.hint = getString(R.string.insert_names_error_msg)
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
                    var nampref = sp.getString("Namen", "")
                    if (nampref.isNullOrEmpty()) nampref = help else {
                        val hi = ArrayList(
                            mutableListOf(
                                *nampref.split("§%20teiler02%§".toRegex())
                                    .dropLastWhile { it.isEmpty() }
                                    .toTypedArray()))
                        var test = true
                        for (check in hi) {
                            if (check == help) {
                                test = false
                                break
                            }
                        }
                        if (test) nampref = "$nampref§%20teiler02%§$help"
                    }
                    val ede = sp.edit()
                    ede.putString("Namen", nampref)
                    ede.apply()
                    namen = help.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    namenanzahl = namen.size
                    dialog!!.dismiss()
                    layoutid = 5
                    lastAngle = 0
                    startgame2()
                }
            }
            dialog!!.setOnKeyListener { _, i, _ ->
                if (i == KeyEvent.KEYCODE_BACK) {
                    dialog!!.dismiss()
                    layoutid = 5
                }
                false
            }
        }
    }

    fun imageviewforgame() {
        if (aniint == 0) {
            val random = Random()
            val drehung = random.nextInt(5) + 4
            angle = random.nextInt(361) + 360 * drehung
            val pivotX = imageView!!.width / 2f
            val pivotY = imageView!!.height / 2f
            val animRotate: Animation = RotateAnimation(
                (if (lastAngle == -1) 0 else lastAngle).toFloat(),
                angle.toFloat(),
                pivotX,
                pivotY
            )
            while (angle > 360) {
                angle -= 360
            }
            lastAngle = angle
            animRotate.duration = 1500
            animRotate.fillAfter = true
            imageView!!.startAnimation(animRotate)
            animRotate.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {}
                override fun onAnimationEnd(animation: Animation) {
                    aniint = 0
                    var anglee = angle
                    while (anglee > 360) {
                        anglee -= 360
                    }
                    if (nochmadrehen) {
                        var pla = anglee / (360 / namenanzahl)
                        pla += 1
                        if (pla == 1) pla = namenanzahl + 1
                        if (pl == pla) {
                            imageviewforgame()
                        } else chooseplayer(anglee.toFloat())
                    } else chooseplayer(anglee.toFloat())
                }

                override fun onAnimationRepeat(animation: Animation) {}
            })
            aniint = 1
        }
    }

    private fun imageviewforgame1() {
        if (aniint == 0) {
            val random = Random()
            val drehung = random.nextInt(5) + 4
            angle = random.nextInt(361) + 360 * drehung
            val pivotX = imageView!!.width / 2f
            val pivotY = imageView!!.height / 2f
            val animRotate: Animation = RotateAnimation(
                (if (lastAngle == -1) 0 else lastAngle).toFloat(),
                angle.toFloat(),
                pivotX,
                pivotY
            )
            while (angle > 360) {
                angle -= 360
            }
            lastAngle = angle
            animRotate.duration = 1500
            animRotate.fillAfter = true
            imageView!!.startAnimation(animRotate)
            animRotate.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {}
                override fun onAnimationEnd(animation: Animation) {
                    aniint = 0
                    dialog(0)
                }

                override fun onAnimationRepeat(animation: Animation) {}
            })
            aniint = 1
        }
    }

    private fun imageviewforgame2() {
        if (aniint == 0) {
            val angle = 360 / namenanzahl / 2 + 360 / namenanzahl * (drehhilfe + 1)
            val pivotX = imageView!!.width / 2f
            val pivotY = imageView!!.height / 2f
            val animRotate: Animation = RotateAnimation(
                (if (lastAngle == -1) 0 else lastAngle).toFloat(),
                angle.toFloat(),
                pivotX,
                pivotY
            )
            lastAngle = angle
            animRotate.duration = 1500
            animRotate.fillAfter = true
            imageView!!.startAnimation(animRotate)
            animRotate.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {}
                override fun onAnimationEnd(animation: Animation) {
                    aniint = 0
                    drehhilfe++
                    var anglee = angle
                    while (anglee > 360) {
                        anglee -= 360
                    }
                    chooseplayer(anglee.toFloat())
                }

                override fun onAnimationRepeat(animation: Animation) {}
            })
            aniint = 1
        }
    }

    private fun getBitmapFromVectorDrawable(context: Context?, drawableId: Int): Bitmap {
        @SuppressLint("RestrictedApi") val drawable = AppCompatDrawableManager.get().getDrawable(
            context!!, drawableId
        )
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    private fun wahrheiten(): ArrayList<String> {
        val wahrheit = ArrayList<String>()
        val myDB = DataBaseHelper(this@MainActivity)
        val res = myDB.allData
        if (res.count > 0) {
            while (res.moveToNext()) {
                if (res.getString(2) == "Wahrheit") {
                    wahrheit.add(res.getString(1))
                }
            }
        }
        return wahrheit
    }

    private fun pflichten(): ArrayList<String> {
        val pflicht = ArrayList<String>()
        val myDB = DataBaseHelper(this@MainActivity)
        val res = myDB.allData
        if (res.count > 0) {
            while (res.moveToNext()) {
                if (res.getString(2) == "Pflicht") {
                    pflicht.add(res.getString(1))
                }
            }
        }
        return pflicht
    }

    private val isMyServiceRunning: Boolean
        get() {
            val manager = (getSystemService(ACTIVITY_SERVICE) as ActivityManager)
            for (service in manager.getRunningServices(Int.MAX_VALUE)) {
                if (Popup::class.java.name == service.service.className) {
                    return true
                }
            }
            return false
        }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        when (requestCode) {
            107 -> {
                if (Settings.canDrawOverlays(this)) {
                    startService(Intent(this, Popup::class.java))
                    finishAndRemoveTask()
                }
            }
            20 -> {
                if (resultCode == RESULT_OK) {
                    importfile(data, TYPE_WAHRHEIT, true)
                }
            }
            21 -> {
                if (resultCode == RESULT_OK) {
                    importfile(data, TYPE_PFLICHT, true)
                }
            }
            30 -> {
                if (data.data == null) return
                val art: String = if (exporttypepflicht) "Pflicht" else "Wahrheit"
                val write = StringBuilder()
                val myDB = DataBaseHelper(this)
                val res = myDB.allData
                if (res.count > 0) {
                    while (res.moveToNext()) {
                        if (res.getString(2) == art) {
                            write.append(res.getString(1))
                            write.append("\n")
                        }
                    }
                }
                var writing = write.toString()
                writing = writing.substring(0, writing.length - 1)
                try {
                    val cr = contentResolver
                    val os = cr.openOutputStream(data.data!!)
                    if (os == null) {
                        Toast.makeText(this, getString(R.string.export_failed), Toast.LENGTH_SHORT).show()
                        return
                    }
                    os.write(writing.toByteArray())
                    os.close()
                    Toast.makeText(this, getString(R.string.tasks_saved), Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(this, getString(R.string.export_failed), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handlelink(intent)
    }

    private fun handlelink(intent: Intent) {
        val appLinkAction = intent.action
        if (Intent.ACTION_VIEW == appLinkAction) {
            val mime = getFileName(intent.data)
            if (mime!!.contains("fldw")) {
                importfile(intent, TYPE_WAHRHEIT, false)
            } else if (mime.contains("fldp")) {
                importfile(intent, TYPE_PFLICHT, false)
            } else {
                Toast.makeText(
                    this@MainActivity,
                    getString(R.string.no_truth_dare_file),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun importfile(intent: Intent, type: Int, dialogvisisble: Boolean) {
        try {
            if (intent.data == null) {
                Toast.makeText(this@MainActivity,
                    getString(R.string.import_failed), Toast.LENGTH_SHORT)
                    .show()
                return
            }
            val mime = getFileName(intent.data)
            if (type == TYPE_WAHRHEIT && !mime!!.contains("fldw") || type == TYPE_PFLICHT && !mime!!.contains(
                    "fldp"
                )
            ) {
                Toast.makeText(
                    this@MainActivity,
                    getString(R.string.no_truth_dare_file),
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
            val cr = contentResolver
            val `is` = cr.openInputStream(intent.data!!) ?: return
            val buf = StringBuilder()
            val reader = BufferedReader(InputStreamReader(`is`))
            var str: String?
            while (reader.readLine().also { str = it } != null) {
                buf.append(str).append("\n")
            }
            `is`.close()
            val help =
                buf.toString().split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val myDB = DataBaseHelper(this)
            if (type == TYPE_PFLICHT) {
                for (task in help) {
                    myDB.insertData(task, "Pflicht")
                }
                if (dialogvisisble) {
                    pflichtliste!!.clear()
                    alle!!.clear()
                    val res = myDB.allData
                    if (res.count > 0) {
                        while (res.moveToNext()) {
                            if (res.getString(2) == "Pflicht") {
                                pflichtliste!!.add(
                                    Liste(
                                        res.getString(0),
                                        res.getString(1),
                                        res.getString(2)
                                    )
                                )
                                alle!!.add(
                                    Liste(
                                        res.getString(0),
                                        res.getString(1),
                                        res.getString(2)
                                    )
                                )
                            }
                        }
                    }
                    if (pflichtliste!!.isEmpty()) {
                        pflichtliste!!.add(Liste("", getString(R.string.no_dares_created), ""))
                    }
                    aufgabenAdapter!!.notifyDataSetChanged()
                }
            } else if (type == TYPE_WAHRHEIT) {
                for (task in help) {
                    myDB.insertData(task, "Wahrheit")
                }
                if (dialogvisisble) {
                    wahrheitsliste!!.clear()
                    alle!!.clear()
                    val res = myDB.allData
                    if (res.count > 0) {
                        while (res.moveToNext()) {
                            if (res.getString(2) == "Wahrheit") {
                                wahrheitsliste!!.add(
                                    Liste(
                                        res.getString(0),
                                        res.getString(1),
                                        res.getString(2)
                                    )
                                )
                                alle!!.add(
                                    Liste(
                                        res.getString(0),
                                        res.getString(1),
                                        res.getString(2)
                                    )
                                )
                            }
                        }
                    }
                    if (wahrheitsliste!!.isEmpty()) {
                        wahrheitsliste!!.add(
                            Liste(
                                "",
                                getString(R.string.no_questions_created),
                                ""
                            )
                        )
                    }
                    aufgabenAdapter!!.notifyDataSetChanged()
                }
            }
            Toast.makeText(this@MainActivity,
                getString(R.string.import_finished), Toast.LENGTH_SHORT)
                .show()
        } catch (e: Exception) {
            Toast.makeText(this@MainActivity, getString(R.string.import_failed), Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun exportfile(type: Int) {
        exporttypepflicht = type == TYPE_PFLICHT
        val c = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.GERMANY)
        val titel = sdf.format(c.time)
        val name: String = if (type == TYPE_PFLICHT) {
            "Pflichten_$titel.fldp"
        } else "Wahrheiten_$titel.fldw"
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.setType("application/" + name.substring(name.length - 4))
        intent.putExtra(Intent.EXTRA_TITLE, name)
        startActivityForResult(intent, 30)
    }

    private fun getFileName(uri: Uri?): String? {
        var result: String? = null
        if (uri!!.scheme == "content") {
            contentResolver.query(uri, null, null, null, null).use { cursor ->
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result!!.substring(cut + 1)
            }
        }
        return result
    }
    private fun setFullScreen(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
        } else {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
        window.decorView.setOnApplyWindowInsetsListener { _: View?, insets: WindowInsets ->
            insets
        }
    }
    fun checkAGBs(){
        val sese = getSharedPreferences("Start", 0)
        val web = sese.getBoolean("agbs", false)
        if (!web && showdialog) {
            val dialog = Dialog(this, R.style.AppDialog)
            dialog.setContentView(R.layout.webdialog)
            val ja = dialog.findViewById<TextView>(R.id.textView5)
            val nein = dialog.findViewById<TextView>(R.id.textView8)
            ja.setOnClickListener {
                val ed = sese.edit()
                ed.putBoolean("agbs", true)
                ed.apply()
                dialog.dismiss()
            }
            nein.setOnClickListener {
                finishAndRemoveTask()
            }
            val textView = dialog.findViewById<TextView>(R.id.textView4)
            textView.text = Html.fromHtml(
                "Mit der Nutzung dieser App aktzeptiere ich die " +
                        "<a href=\"https://www.kruemelopment-dev.de/datenschutzerklaerung\">Datenschutzerklärung</a>" + " und die " + "<a href=\"https://www.kruemelopment-dev.de/nutzungsbedingungen\">Nutzungsbedingungen</a>" + " von Krümelopment Dev",HtmlCompat.FROM_HTML_MODE_LEGACY
            )
            textView.movementMethod = LinkMovementMethod.getInstance()
            dialog.setCancelable(false)
            dialog.show()
        }
    }
    private fun delete(title:String,text:String,toastText:String,what:Int) {
        dialog = Dialog(this@MainActivity, R.style.AppDialog)
        dialog!!.setCancelable(true)
        dialog!!.setContentView(R.layout.loeschen)
        dialog!!.show()
        val btn1 = dialog!!.findViewById<Button>(R.id.button6)
        val btn2 = dialog!!.findViewById<Button>(R.id.button8)
        val tvt = dialog!!.findViewById<TextView>(R.id.textView21)
        val tvt2 = dialog!!.findViewById<TextView>(R.id.textView22)
        tvt.text = title
        tvt2.text = text
        btn1.setOnClickListener {
            if(what==0){
                val sp3 = getSharedPreferences("Einstellungen", 0)
                val ede = sp3.edit()
                ede.putString("Namen", "")
                ede.apply()
            }
            else if (what==1||what==2){
                val myDB = DataBaseHelper(this@MainActivity)
                myDB.deletespecified(if(what==1)"Wahrheit" else "Pflicht")
            }
            Toast.makeText(
                this@MainActivity,
                toastText,
                Toast.LENGTH_SHORT
            ).show()
            dialog!!.dismiss()
        }
        btn2.setOnClickListener { dialog!!.dismiss() }
    }

    private fun showOwnQuestions(type:Int){
        dialog = Dialog(this@MainActivity, R.style.AppDialog)
        dialog!!.setCancelable(true)
        dialog!!.setContentView(R.layout.aufgaben)
        val relativeLayout = dialog!!.findViewById<RelativeLayout>(R.id.back8)
        relativeLayout.setOnClickListener {
            dialog!!.dismiss()
            layoutid = 0
        }
        if(type==0) wahrheitsliste=ArrayList()
        else pflichtliste=ArrayList()
        alle = ArrayList()
        val myDB = DataBaseHelper(this@MainActivity)
        var res = myDB.allData
        if (res.count > 0) {
            while (res.moveToNext()) {
                if (res.getString(2) == (if(type==0)"Wahrheit" else "Pflicht")) {
                    val item = Liste(res.getString(0), res.getString(1), res.getString(2))
                    alle!!.add(item)
                }
            }
        }
        if(type==0)wahrheitsliste!!.addAll(alle!!)
        else pflichtliste!!.addAll(alle!!)
        editText = dialog!!.findViewById(R.id.editText)
        listeaufgaben = dialog!!.findViewById(R.id.listview)
        image = dialog!!.findViewById(R.id.imageView9)
        image!!.setOnClickListener {
            var check = false
            if(type==0) {
                for (a in wahrheitsliste!!.indices) {
                    if (wahrheitsliste!![a].aufgabe == editText!!.text.toString()) {
                        check = true
                    }
                }
            }
            else {
                for (a in pflichtliste!!.indices) {
                    if (pflichtliste!![a].aufgabe == editText!!.text.toString()) {
                        check = true
                    }
                }
            }
            if (editText!!.text.toString().isNotEmpty() && editText!!.text.toString()
                    .replace(" ", "").isNotEmpty() && !check
            ) {
                myDB.insertData(editText!!.text.toString(), (if(type==0)"Wahrheit" else "Pflicht"))
                res=myDB.allData
                if (res.count > 0) {
                    while (res.moveToNext()) {
                        if (res.getString(1) == editText!!.text.toString()) {
                            val item = Liste(res.getString(0), res.getString(1), res.getString(2))
                            alle!!.add(item)
                            if(type==0)wahrheitsliste!!.add(item)
                            else pflichtliste!!.add(item)
                            aufgabenAdapter!!.notifyDataSetChanged()
                            break
                        }
                    }
                }
                editText!!.setText("")
            }
        }
        editText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
            }

            override fun onTextChanged(charSequenc: CharSequence, i: Int, i1: Int, i2: Int) {
                if(type==0)wahrheitsliste!!.clear()
                else pflichtliste!!.clear()
                var charSequence = charSequenc.toString()
                charSequence = charSequence.lowercase(Locale.getDefault())
                for (`in` in alle!!.indices) {
                    val check = alle!![`in`]
                    try {
                        if (check.aufgabe!!.lowercase(Locale.getDefault())
                                .contains(charSequence)
                        ) {
                            if(type==0)wahrheitsliste!!.add(Liste(check.id, check.aufgabe, check.art))
                            else pflichtliste!!.add(Liste(check.id, check.aufgabe, check.art))
                        }
                    } catch (ignored: Exception) {
                    }
                }
                aufgabenAdapter!!.notifyDataSetChanged()
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        editText!!.setOnEditorActionListener { _, i, _ ->
            if (i == EditorInfo.IME_ACTION_DONE) {
                val view = this@MainActivity.currentFocus
                if (view != null) {
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                }
            }
            false
        }
        if (type==0&&wahrheitsliste!!.isEmpty()){
            val item = Liste("", getString(R.string.no_questions_created), "")
            wahrheitsliste!!.add(item)
        }
        else if (type==1&& pflichtliste!!.isEmpty()){
            val item = Liste("", getString(R.string.no_dares_created), "")
            pflichtliste!!.add(item)
        }
        aufgabenAdapter = AufgabenAdapter(this@MainActivity, if(type==0)wahrheitsliste!! else pflichtliste!!,type)
        listeaufgaben!!.adapter = aufgabenAdapter
        val importimg = dialog!!.findViewById<ImageView>(R.id.imageView14)
        val export = dialog!!.findViewById<ImageView>(R.id.imageView13)
        importimg.setOnClickListener {
            val chooseFile = Intent(Intent.ACTION_GET_CONTENT)
            chooseFile.addCategory(Intent.CATEGORY_OPENABLE)
            chooseFile.setType("*/*")
            startActivityForResult(
                Intent.createChooser(chooseFile, "Datei auswählen"),
                20
            )
        }
        export.setOnClickListener {
            if (!myDB.isEmpty((if(type==0)"Wahrheit" else "Pflicht"))) exportfile((if(type==0) TYPE_WAHRHEIT else TYPE_PFLICHT)) else Toast.makeText(
                this@MainActivity,
                (if(type==0)getString(R.string.no_questions_created) else getString(R.string.no_dares_created)),
                Toast.LENGTH_SHORT
            ).show()
        }
        dialog!!.show()
        dialog!!.setOnKeyListener { _, i, _ ->
            if (i == KeyEvent.KEYCODE_BACK) {
                dialog!!.dismiss()
                layoutid = 0
            }
            false
        }
    }

    private fun dialog(type:Int){
        dialog = Dialog(this@MainActivity, R.style.AppDialog)
        dialog!!.setCancelable(true)
        dialog!!.setContentView(R.layout.chose)
        val relativeLayout = dialog!!.findViewById<RelativeLayout>(R.id.back2)
        relativeLayout.setOnClickListener {
            dialog!!.dismiss()
            layoutid = help
        }
        val wahrheit = dialog!!.findViewById<Button>(R.id.button3)
        val konsum = dialog!!.findViewById<Button>(R.id.button4)
        val pflicht = dialog!!.findViewById<Button>(R.id.button5)
        val textView = dialog!!.findViewById<TextView>(R.id.textView)
        dialog!!.show()
        if(type==1)textView.text = namen[pl - 2]
        help = layoutid
        layoutid = 2
        wahrheit.setOnClickListener {
            dialog!!.dismiss()
            layoutid = help
            if (type==0)aufgabe(1,false)
            else aufgabe(1,true)
        }
        pflicht.setOnClickListener {
            dialog!!.dismiss()
            layoutid = help
            if (type==0)aufgabe(2,false)
            else aufgabe(2,true)
        }
        konsum.setOnClickListener {
            dialog!!.dismiss()
            layoutid = help
            if (type==0)aufgabe(3,false)
            else aufgabe(3,true)
        }
        dialog!!.setOnKeyListener { _, i, _ ->
            if (i == KeyEvent.KEYCODE_BACK) {
                dialog!!.dismiss()
                layoutid = help
            }
            false
        }
    }

    companion object {
        const val TYPE_WAHRHEIT = 1
        const val TYPE_PFLICHT = 2
        var alle: MutableList<Liste>? = null
    }
}