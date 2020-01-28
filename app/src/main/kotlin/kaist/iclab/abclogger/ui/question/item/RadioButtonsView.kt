package kaist.iclab.abclogger.ui.question.item

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.children
import androidx.core.widget.addTextChangedListener
import kaist.iclab.abclogger.R

class RadioButtonsView(context: Context, attributeSet: AttributeSet?) : QuestionView(context, attributeSet) {
    private val layoutRadioGroup = LinearLayout(context).apply {
        id = View.generateViewId()
        orientation = LinearLayout.VERTICAL
    }

    private val btnEtc: RadioButton = RadioButton(context).apply {
        id = View.generateViewId()
        text = context.getString(R.string.general_etc)
        setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.txt_size_text))
        setOnCheckedChangeListener(onCheckedChanged)
    }

    private val edtEtc: EditText = EditText(context).apply {
        id = View.generateViewId()
        setHint(R.string.general_free_text)
        setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.txt_size_text))
        addTextChangedListener({ _, _, _, _ -> }, { _, _, _, _ -> attrChanged?.onChange() }, {})
    }

    private val onCheckedChanged: (CompoundButton, Boolean) -> Unit = { button, _ ->
        (layoutRadioGroup.children + btnEtc).forEach { view ->
            (view as? CompoundButton)?.isChecked = view == button
        }
        edtEtc.isEnabled = btnEtc.isChecked
        attrChanged?.onChange()
    }

    init {
        addView(layoutRadioGroup, LayoutParams(0, LayoutParams.WRAP_CONTENT))
        addView(btnEtc, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT))
        addView(edtEtc, LayoutParams(0, LayoutParams.WRAP_CONTENT))

        ConstraintSet().also { constraint ->
            constraint.clone(this)

            constraint.connect(layoutRadioGroup.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
            constraint.connect(layoutRadioGroup.id, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT)
            constraint.connect(layoutRadioGroup.id, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT)

            constraint.connect(btnEtc.id, ConstraintSet.TOP, edtEtc.id, ConstraintSet.TOP)
            constraint.connect(btnEtc.id, ConstraintSet.BOTTOM, edtEtc.id, ConstraintSet.BOTTOM)
            constraint.connect(btnEtc.id, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT)

            constraint.connect(edtEtc.id, ConstraintSet.TOP, layoutRadioGroup.id, ConstraintSet.BOTTOM)
            constraint.connect(edtEtc.id, ConstraintSet.LEFT, edtEtc.id, ConstraintSet.RIGHT)
            constraint.connect(edtEtc.id, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT)
        }.applyTo(this)
    }

    override fun setAvailable(isAvailable: Boolean) {
        (layoutRadioGroup.children + btnEtc + edtEtc).forEach { view -> view.isEnabled = isAvailable }
    }

    override fun setShowEtc(showEtc: Boolean) {
        btnEtc.visibility = if (showEtc) View.VISIBLE else View.GONE
        edtEtc.visibility = if (showEtc) View.VISIBLE else View.GONE
    }

    override fun setOptions(options: Array<String>) {
        layoutRadioGroup.removeAllViews()

        options.map { option ->
            RadioButton(context).apply {
                id = View.generateViewId()
                text = option
                setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.txt_size_text))
                setOnCheckedChangeListener(onCheckedChanged)
            }
        }.forEach { button ->
            layoutRadioGroup.addView(button)
        }
    }

    override var responses: Array<String> = arrayOf()
        get() =
            (layoutRadioGroup.children + btnEtc).find { view ->
                (view as? CompoundButton)?.isChecked == true
            }?.let { view ->
                val text = if (view == btnEtc) {
                    edtEtc.text
                } else {
                    (view as? CompoundButton)?.text
                }?.toString()

                if (!text.isNullOrBlank()) arrayOf(text) else null
            } ?: arrayOf()
        set(value) {
            if(field.firstOrNull() == value.firstOrNull()) return

            val response = value.firstOrNull()
            if (response.isNullOrBlank()) return

            val button = layoutRadioGroup.children.find { view ->
                (view as? CompoundButton)?.text == response
            }

            if (button != null) {
                (button as? CompoundButton?)?.isChecked = true
            } else {
                btnEtc.isChecked = true
                edtEtc.setText(response)
            }
        }
}