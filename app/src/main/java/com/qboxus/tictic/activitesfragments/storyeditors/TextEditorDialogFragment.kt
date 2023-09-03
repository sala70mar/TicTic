package com.qboxus.tictic.activitesfragments.storyeditors

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.qboxus.tictic.adapters.FontAdapter
import com.qboxus.tictic.Constants
import com.qboxus.tictic.interfaces.AdapterClickListener
import com.qboxus.tictic.interfaces.FragmentCallBack
import com.qboxus.tictic.models.FontModel
import com.qboxus.tictic.models.TextEditorModel
import com.qboxus.tictic.R
import com.qboxus.tictic.simpleclasses.Functions
import com.qboxus.tictic.databinding.AddTextDialogBinding

class TextEditorDialogFragment() : DialogFragment(),View.OnClickListener {
    private lateinit var mAddTextEditText: EditText
    private lateinit var mAddTextDoneTextView: TextView
    lateinit var callback:FragmentCallBack
    constructor(callback:FragmentCallBack) : this()
    {
        this.callback=callback;
    }

    var binding:AddTextDialogBinding?=null
    var model:TextEditorModel= TextEditorModel()

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        //Make dialog full screen with transparent background
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window!!.setLayout(width, height)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.add_text_dialog, container, false)

        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAddTextEditText = view.findViewById(R.id.add_text_edit_text)
        mAddTextDoneTextView = view.findViewById(R.id.add_text_done_tv)

        //Setup the color picker for text color
        val addTextColorPickerRecyclerView: RecyclerView =
            view.findViewById(R.id.add_text_color_picker_recycler_view)
        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        addTextColorPickerRecyclerView.layoutManager = layoutManager
        addTextColorPickerRecyclerView.setHasFixedSize(true)
        val colorPickerAdapter = ColorPickerAdapter(requireActivity())

        try {
            if(requireArguments().getSerializable("data")!=null){
                model= requireArguments().getSerializable("data") as TextEditorModel
                mAddTextEditText.setText(model.text)
                mAddTextEditText.setTextColor(model.colorCode)
            }
            else{
                model.colorCode=ContextCompat.getColor((requireContext()), R.color.white)
                mAddTextEditText.setTextColor(model.colorCode)
            }
        }catch (e:Exception)
        {}
        //This listener will change the text color when clicked on any color from picker
        colorPickerAdapter.setOnColorPickerClickListener(object :
            ColorPickerAdapter.OnColorPickerClickListener {
            override fun onColorPickerClickListener(colorCode: Int) {
                model.colorCode = colorCode
                mAddTextEditText.setTextColor(colorCode)
            }
        })
        addTextColorPickerRecyclerView.adapter = colorPickerAdapter


        Log.d(Constants.tag,"mColorCode: $model.colorCode")
        //Make a callback on activity when user is done with text editing
        mAddTextDoneTextView.setOnClickListener { onClickListenerView ->
            model.text = mAddTextEditText.text.toString()
            val bundle=Bundle()
            bundle.putSerializable("data",model);

            callback.onResponce(bundle)
            dismiss()
        }

        binding!!.txtDirectionBtn.setOnClickListener(this)
        setFontRecyclerView()
    }



    @SuppressLint("SuspiciousIndentation")
    fun setFontDirection(){
        if(model.direction==2){
            model.direction=0
        }
        else
        model.direction++

        when(model.direction){
            0 ->{
                binding!!.txtDirectionBtn.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_direction_left))
                binding!!.addTextEditText.gravity = Gravity.START or Gravity.CENTER_VERTICAL
            }
            1 ->{
                binding!!.txtDirectionBtn.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_direction_center))
                 binding!!.addTextEditText.gravity = Gravity.CENTER or Gravity.CENTER_VERTICAL

            }
            2 ->{
                binding!!.txtDirectionBtn.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_direction_right))
               binding!!.addTextEditText.gravity = Gravity.END or Gravity.CENTER_VERTICAL


            }
        }



    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.txtDirectionBtn->{
                setFontDirection()
            }
        }
    }

    var fontAdapter:FontAdapter?=null

    fun setFontRecyclerView(){
        var fontDataList = ArrayList<FontModel>()
        fontDataList.add(FontModel("Classic",R.font.font_classic))
        fontDataList.add(FontModel("Typewriter",R.font.font_typewriter))
        fontDataList.add(FontModel("HandWriting",R.font.font_handwriting))
        fontDataList.add(FontModel("Neon",R.font.font_neon))
        fontDataList.add(FontModel("Serif",R.font.font_serif))

        model.selectedFont=fontDataList.get(0)
        fontAdapter = FontAdapter(context,fontDataList,object :AdapterClickListener{
            override fun onItemClick(view: View?, pos: Int, `dataobject`: Any?) {
                val fontModel:FontModel = dataobject as FontModel
                model.selectedFont=fontModel;
                fontAdapter!!.setSelectedFont(model.selectedFont)

                 val typeface = ResourcesCompat.getFont(context!!, model.selectedFont!!.font)
                 binding!!.addTextEditText.setTypeface(typeface)

                fontAdapter!!.notifyDataSetChanged()

            }
        })
        fontAdapter!!.setSelectedFont(model.selectedFont)

        binding!!.fontRecyclerView.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
       binding!!.fontRecyclerView.adapter = fontAdapter

    }

    companion object {
        private val TAG: String = TextEditorDialogFragment::class.java.simpleName
        const val EXTRA_INPUT_TEXT = "extra_input_text"
        const val EXTRA_COLOR_CODE = "extra_color_code"

        //Show dialog with provide text and text color
        //Show dialog with default text input as empty and text color white
        @JvmOverloads
        @JvmStatic
        fun show(
            appCompatActivity: AppCompatActivity,
            inputText: String = "",
            @ColorInt colorCode: Int = ContextCompat.getColor(appCompatActivity, R.color.white)
        ): TextEditorDialogFragment {
            val args = Bundle()
            args.putString(EXTRA_INPUT_TEXT, inputText)
            args.putInt(EXTRA_COLOR_CODE, colorCode)
            val fragment = TextEditorDialogFragment()
            fragment.arguments = args
            fragment.show(appCompatActivity.supportFragmentManager, TAG)
            return fragment
        }
    }

    override fun onDetach() {
        Functions.hideSoftKeyboard(requireActivity())
        super.onDetach()
    }


}