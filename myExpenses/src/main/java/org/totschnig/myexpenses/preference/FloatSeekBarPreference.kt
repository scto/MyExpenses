package org.totschnig.myexpenses.preference

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.widget.SeekBar
import android.widget.TextView
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import org.totschnig.myexpenses.R
import androidx.preference.R as RP

//inspired by https://gist.github.com/mtotschnig/bfc7966d41c67f2aa0be140214e7a1a4
//licenced under MIT LICENCE https://gist.github.com/mtotschnig/bfc7966d41c67f2aa0be140214e7a1a4#file-license

class FloatSeekBarPreference(context: Context, attrs: AttributeSet) :
    Preference(context, attrs), SeekBar.OnSeekBarChangeListener {

    private val minValue: Float
    private val maxValue: Float
    private val valueSpacing: Float
    private val format: String

    private lateinit var textView: TextView

    private var defaultValue = 0F
    private var newValue = 0F

    init {
        layoutResource = androidx.preference.R.layout.preference_widget_seekbar_material

        val ta = context.obtainStyledAttributes(attrs, R.styleable.FloatSeekBarPreference, 0, 0)
        minValue = ta.getFloat(R.styleable.FloatSeekBarPreference_minValue, 0F)
        maxValue = ta.getFloat(R.styleable.FloatSeekBarPreference_maxValue, 1F)
        valueSpacing = ta.getFloat(R.styleable.FloatSeekBarPreference_valueSpacing, .1F)
        format = ta.getString(R.styleable.FloatSeekBarPreference_format) ?: "%3.1f"
        ta.recycle()
    }


    override fun onGetDefaultValue(ta: TypedArray, i: Int): Any {
        defaultValue = ta.getFloat(i, 0F)
        return defaultValue
    }

    override fun onSetInitialValue(initValue: Any?) {
        newValue = getPersistedFloat(
            if (initValue is Float) initValue
            else this.defaultValue
        )
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)

        holder.itemView.isClickable = false
        textView = holder.findViewById(RP.id.seekbar_value) as TextView

        with(holder.findViewById(RP.id.seekbar) as SeekBar) {
            setOnSeekBarChangeListener(this@FloatSeekBarPreference)
            max = ((maxValue - minValue) / valueSpacing).toInt()
            progress = ((newValue - minValue) / valueSpacing).toInt()
            isEnabled = isEnabled
        }

        textView.text = format.format(newValue)
    }

    override fun onProgressChanged(seekbar: SeekBar, progress: Int, fromUser: Boolean) {
        if (!fromUser) return
        val v = minValue + progress * valueSpacing
        textView.text = format.format(v)
    }

    override fun onStartTrackingTouch(seekbar: SeekBar) {}

    override fun onStopTrackingTouch(seekbar: SeekBar) {
        val v = minValue + seekbar.progress * valueSpacing
        persistFloat(v)
    }
}