package org.kruemelopment.de.flaschendrehen

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.AutoCompleteTextView
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView

class AufgabenAdapter(private val context: Context, private val aufgaben: MutableList<Liste>,private val type:Int) :
    BaseAdapter() {
    private var myDB: DataBaseHelper? = null

    override fun getCount(): Int {
        return aufgaben.size
    }

    override fun getItem(i: Int): Any {
        return aufgaben[i]
    }

    override fun getItemId(i: Int): Long {
        return aufgaben[i].hashCode().toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val convertView2: View? = convertView ?: LayoutInflater.from(parent.context).inflate(R.layout.eigeneaufgabenlistitem,parent,false)
        myDB = DataBaseHelper(context)
        val string = aufgaben[position].aufgabe
        val tt= convertView2!!.findViewById<TextView>(R.id.textView7)
        val edit=convertView2.findViewById<ImageView>(R.id.imageView11)
        val imageView=convertView2.findViewById<ImageView>(R.id.imageView10)
        tt!!.text = string
        if (string == context.getString(R.string.no_questions_created)||string==context.getString(R.string.no_dares_created)) {
            imageView!!.setImageResource(0)
            val layoutParams = imageView.layoutParams
            layoutParams.width = 0
            layoutParams.height = 0
            imageView.layoutParams = layoutParams
            edit!!.setImageResource(0)
            val layoutParam = edit.layoutParams
            layoutParam.width = 0
            layoutParam.height = 0
            edit.layoutParams = layoutParam
        }
        val relativeLayout = convertView2.findViewById<RelativeLayout>(R.id.relao)
        imageView!!.setOnClickListener {
            val myDB = DataBaseHelper(context)
            myDB.deleteData(aufgaben[position].id)
            val outtoRight: Animation = TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f
            )
            outtoRight.duration = 300
            outtoRight.interpolator = AccelerateInterpolator()
            relativeLayout.startAnimation(outtoRight)
            outtoRight.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {}
                override fun onAnimationEnd(animation: Animation) {
                    aufgaben.removeAt(position)
                    MainActivity.alle!!.removeAt(position)
                    if (aufgaben.isEmpty()) aufgaben.add(
                        Liste("", if(type==0) context.getString(R.string.no_questions_created) else context.getString(R.string.no_dares_created), "")
                    )
                    notifyDataSetChanged()
                }

                override fun onAnimationRepeat(animation: Animation) {}
            })
        }
        edit!!.setOnClickListener {
            val dialog = Dialog(context, R.style.Theme_AppCompat_Dialog)
            dialog.setCancelable(true)
            dialog.setContentView(R.layout.bearbeiten)
            val tv4 = dialog.findViewById<TextView>(R.id.textView4)
            val tv5 = dialog.findViewById<TextView>(R.id.textView21)
            val editText1 = dialog.findViewById<AutoCompleteTextView>(R.id.editText3)
            dialog.show()
            tv4.text = context.getString(R.string.update)
            editText1.setText(string)
            tv4.setOnClickListener {
                var check = false
                for (a in aufgaben.indices) {
                    if (aufgaben[a].aufgabe == editText1.text.toString()) {
                        check = true
                        tv5.text = if(type==0) context.getString(R.string.question_already_exisits) else context.getString(R.string.dare_already_noted)
                    }
                }
                if (editText1.text.toString().isNotEmpty() && editText1.text.toString()
                        .replace(" ", "").isNotEmpty() && !check
                ) {
                    myDB!!.updateData(aufgaben[position].id, editText1.text.toString(), if(type==1)context.getString(R.string.truth) else context.getString(R.string.dare))
                    aufgaben[position].aufgabe = editText1.text.toString()
                    notifyDataSetChanged()
                    dialog.cancel()
                }
            }
        }
        return convertView2
    }
}