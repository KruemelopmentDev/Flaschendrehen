package org.kruemelopment.de.flaschendrehen

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filterable
import android.widget.TextView

class VorschlagAdapter(private var context: Context, textid: Int, private val suggest: ArrayList<String>) :
    ArrayAdapter<String?>(
        context, textid, suggest as List<String?>
    ), Filterable {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var v = convertView
        if (v == null) {
            val vi = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            v = vi.inflate(R.layout.adapter, parent, false)
        }
        val string = suggest[position]
        val tt = v!!.findViewById<TextView>(R.id.txtName)
        tt.text = string
        return v
    }
}