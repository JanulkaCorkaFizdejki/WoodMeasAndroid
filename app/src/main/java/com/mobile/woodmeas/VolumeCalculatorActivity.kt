package com.mobile.woodmeas

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.mobile.woodmeas.math.Calculator
import com.mobile.woodmeas.model.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.thread
import kotlin.properties.Delegates

class VolumeCalculatorActivity : AppCompatActivity() {

    enum class CurrentSize  {
        LENGTH, WIDTH
    }

    enum class ButtonChanges {
        PLUS, MINUS
    }

    private lateinit var textViewLengthValueCm: TextView
    private lateinit var textViewLengthValueM: TextView
    private lateinit var textViewWidthValueCm: TextView
    private lateinit var textViewWidthValueM: TextView
    lateinit var seekBarLogLength: SeekBar
    lateinit var seekBarLogWidth: SeekBar
    private var lengthValueCm: Int = 0
    private var widthValueCm: Int = 0
    private var lengthValueM: Float = 0.0F
    private var widthValueM: Float = 0.0F
    private lateinit var textViewResume: TextView
    lateinit var imageButtonLengthMinus: ImageButton
    private lateinit var imageButtonLengthPlus: ImageButton
    private lateinit var imageButtonWidthMinus: ImageButton
    private lateinit var imageButtonWidthPlus: ImageButton
    private lateinit var imageButtonAddWoodenLog: ImageButton
    lateinit var switchTreeBark: Switch
    private var timer: CountDownTimer? = null
    private var maxLogLength by Delegates.notNull<Int>()
    private var maxLogWidth by Delegates.notNull<Int>()
    private val treesList: ArrayList<Trees> = arrayListOf()
    private val spinnerList: ArrayList<String> = arrayListOf()
    private lateinit var spinnerTreesList: Spinner
    private var currentSpinnerItem: Int = Settings.VolumeCalculatorView.currentTree
    private lateinit var textViewWoodPackagesName: TextView
    private var woodPackagesCurrent: WoodPackages? = null
    private var mediaBleep: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_volume_calculator)

        textViewLengthValueCm       = findViewById(R.id.textViewLengthValueCm)
        textViewLengthValueM        = findViewById(R.id.textViewLengthValueM)
        textViewWidthValueCm        = findViewById(R.id.textViewWidthValueCm)
        textViewWidthValueM         = findViewById(R.id.textViewWidthValueM)
        seekBarLogLength            = findViewById(R.id.seekBarLogLength)
        seekBarLogWidth             = findViewById(R.id.seekBarLogWidth)
        textViewResume              = findViewById(R.id.textViewResume)
        imageButtonLengthMinus      = findViewById(R.id.imageButtonLengthMinus)
        imageButtonLengthPlus       = findViewById(R.id.imageButtonLengthPlus)
        imageButtonWidthMinus       = findViewById(R.id.imageButtonWidthMinus)
        imageButtonWidthPlus        = findViewById(R.id.imageButtonWidthPlus)
        spinnerTreesList            = findViewById(R.id.spinnerTreesList)
        switchTreeBark              = findViewById(R.id.switchTreeBark)
        textViewWoodPackagesName    = findViewById(R.id.textViewWoodPackagesName)
        imageButtonAddWoodenLog     = findViewById(R.id.imageButtonAddWoodenLog)
        maxLogLength = applicationContext.resources.getInteger(R.integer.max_log_length)
        maxLogWidth = applicationContext.resources.getInteger(R.integer.max_log_width)

        thread {
            DatabaseManagerDao.getDataBase(this)?.let {
                it.treesDao().selectAll().forEach { item ->
                    treesList.add(item)
                    spinnerList.add(item.name)
                }
                this.runOnUiThread {
                  val arrayAdapter: ArrayAdapter<String> = ArrayAdapter(this,
                      android.R.layout.simple_spinner_dropdown_item,
                      spinnerList)

                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerTreesList.adapter = arrayAdapter
                    spinnerTreesList.setSelection(Settings.VolumeCalculatorView.currentTree)
                }
            }
        }

        // SET VIEW ________________________________________________________________________________
            setView()
        // _________________________________________________________________________________________


        spinnerTreesList.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, index: Int, p3: Long) {
                    currentSpinnerItem = index
                    Settings.VolumeCalculatorView.currentTree = currentSpinnerItem
                    if (switchTreeBark.isChecked) {
                        setResult()
                    }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) { }
        }

        switchTreeBark.setOnClickListener {
           // spinnerTreesList.isEnabled = switchTreeBark.isChecked
            Settings.VolumeCalculatorView.barkOn = switchTreeBark.isChecked
            setResult()
        }


        seekBarLogLength.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, progress: Int, p2: Boolean) {
                lengthValueCm = progress
                setValuesView(CurrentSize.LENGTH)
                setResult()
            }
            override fun onStartTrackingTouch(p0: SeekBar?) { }
            override fun onStopTrackingTouch(p0: SeekBar?) { }

        })

        seekBarLogWidth.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, progress: Int, p2: Boolean) {
                widthValueCm = progress
                setValuesView(CurrentSize.WIDTH)
                setResult()

            }
            override fun onStartTrackingTouch(p0: SeekBar?) {  }
            override fun onStopTrackingTouch(p0: SeekBar?) {  }
        })


        // LOG LENGTH BUTTONS  _____________________________________________________________________
        // Minus
        imageButtonLengthMinus.setOnClickListener {
            if (lengthValueCm == 0) return@setOnClickListener

            lengthValueCm --
            seekBarLogLength.progress = lengthValueCm
        }

        imageButtonLengthMinus.setOnLongClickListener {
            if (lengthValueCm == 0) return@setOnLongClickListener true

            valuesLooper(it as ImageButton, CurrentSize.LENGTH, ButtonChanges.MINUS)
            return@setOnLongClickListener true
        }

        // Plus
        imageButtonLengthPlus.setOnClickListener {
            if (lengthValueCm == maxLogLength) return@setOnClickListener

            lengthValueCm ++
            seekBarLogLength.progress = lengthValueCm
        }

        imageButtonLengthPlus.setOnLongClickListener {
            if (lengthValueCm == maxLogLength) return@setOnLongClickListener true

            valuesLooper(it as ImageButton, CurrentSize.LENGTH, ButtonChanges.PLUS)
            return@setOnLongClickListener true
        }
        // _________________________________________________________________________________________

        // LOG WIDTH BUTTONS _______________________________________________________________________
        // Minus
        imageButtonWidthMinus.setOnClickListener{
            if (widthValueCm == 0) return@setOnClickListener

            widthValueCm --
            seekBarLogWidth.progress = widthValueCm
        }

        imageButtonWidthMinus.setOnLongClickListener {
            if (widthValueCm == 0) return@setOnLongClickListener true

            valuesLooper(it as ImageButton, CurrentSize.WIDTH, ButtonChanges.MINUS)
            return@setOnLongClickListener true
        }

        // Plus
        imageButtonWidthPlus.setOnClickListener {
            if (widthValueCm == maxLogWidth) return@setOnClickListener

            widthValueCm ++
            seekBarLogWidth.progress = widthValueCm
        }

        imageButtonWidthPlus.setOnLongClickListener {
            if (widthValueCm == maxLogWidth) return@setOnLongClickListener true

            valuesLooper(it as ImageButton, CurrentSize.WIDTH, ButtonChanges.PLUS)
            return@setOnLongClickListener true
        }
        // _________________________________________________________________________________________

        // Add Wooden Log to Package _______________________________________________________________
        imageButtonAddWoodenLog.setOnLongClickListener {
            if  (woodPackagesCurrent == null )  {
                val alertDialog = AlertDialog.Builder(this)
                alertDialog.setMessage(R.string.select_or_create_wood_package)
                alertDialog.setNegativeButton("Ok") {_:DialogInterface, _: Int ->}
                alertDialog.show()
                return@setOnLongClickListener true
            }

            if (lengthValueCm < 1 || widthValueCm < 1) {
                val alertDialog = AlertDialog.Builder(this)
                alertDialog.setMessage(R.string.length_width_log_min_info)
                alertDialog.setNegativeButton("Ok") {_:DialogInterface, _: Int ->}
                alertDialog.show()
                return@setOnLongClickListener true
            }



            val cubicCm: Int = (textViewResume.text.toString().replace(",", ".")
                .toFloat() * 100.0F).toInt()


            val woodenLog = WoodenLog(
                0,
                woodPackagesCurrent!!.id,
                lengthValueCm,
                widthValueCm, cubicCm,
                treesList[currentSpinnerItem].id,
                if (switchTreeBark.isChecked) 1 else 0,
                Date()
            )

            thread {
                DatabaseManagerDao.getDataBase(this)?.woodenLog()?.insert(woodenLog)
                this.runOnUiThread {
                    mediaBleep = null
                    mediaBleep = MediaPlayer.create(this, R.raw.bleep)
                    mediaBleep?.start()

                    Toast.makeText(this, R.string.added_to_wood_package, Toast.LENGTH_SHORT)
                        .show()

                }
            }
            return@setOnLongClickListener true
        }

    }

    private fun setView() {
        switchTreeBark.isChecked = Settings.VolumeCalculatorView.barkOn
        //spinnerTreesList.isEnabled = Settings.VolumeCalculatorView.barkOn
    }


    // BUTTONS for Wood Packages ___________________________________________________________________
    fun onClickBtnSelect(view: View) {
        val intent = Intent(this, WoodPackageListActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun onClickBtnCreate(view: View) {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle(R.string.create_wood_package)
        val viewAlertInflate: View = View.inflate(this, R.layout.add_wood_package, null)
        val editTextWoodPackages: EditText = viewAlertInflate.findViewById(R.id.editTextWoodPackageName)
        alertDialog.setView(viewAlertInflate)
        alertDialog.setNeutralButton("Anuluj") {_: DialogInterface, _: Int -> }

        alertDialog.setNegativeButton("Utwórz") { _: DialogInterface, _: Int ->
            if(editTextWoodPackages.text.isNotEmpty()) {
                val woodPackages =  WoodPackages(0,
                    editTextWoodPackages.text.toString(),
                    Date())
                thread {
                    DatabaseManagerDao.getDataBase(this)?.let {
                        it.woodPackagesDao().insert(woodPackages)
                        this.runOnUiThread {
                            Toast.makeText(this, R.string.created_wood_package, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
        }
        alertDialog.setPositiveButton("Utwórz i użyj") {_: DialogInterface, _: Int ->
            if(editTextWoodPackages.text.isNotEmpty()) {
                val woodPackages =  WoodPackages(0,
                    editTextWoodPackages.text.toString(),
                    Date())
                thread {
                    DatabaseManagerDao.getDataBase(this)?.let {
                        it.woodPackagesDao().insert(woodPackages)
                        woodPackagesCurrent = it.woodPackagesDao().selectLast()
                        woodPackagesCurrent?.let { woodPackagesCurr ->
                            this.runOnUiThread {
                                textViewWoodPackagesName.text = woodPackagesCurr.name
                                Toast.makeText(this, R.string.created_wood_package, Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }
                }
            }
        }
        alertDialog.show()

    }

    fun onClickBtnRemove(view: View) {
        if (woodPackagesCurrent == null) return

        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setMessage(R.string.do_you_want_to_delete_wood_package_from_currency_view)
        alertDialog.setPositiveButton(R.string.yes){_:DialogInterface, _: Int ->
            woodPackagesCurrent = null
            textViewWoodPackagesName.text = applicationContext.getText(R.string.absence)
        }
        alertDialog.setNegativeButton(R.string.cancel) {_: DialogInterface, _:Int ->}
        alertDialog.show()

    }
    // _____________________________________________________________________________________________


    fun onClickGetWoodPackageDetails(view: View) {
        if (woodPackagesCurrent == null) return

        val intent = Intent(this, WoodPackageDetails::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.putExtra(Settings.IntentsPutValues.WOOD_PACKAGE_ID, woodPackagesCurrent!!.id)
        startActivity(intent)
    }


    @SuppressLint("SetTextI18n")
    private fun setResult() {
        if (lengthValueCm == 0 || widthValueCm == 0) {
            textViewResume.text = "0.00"
        }

        else {
            if (switchTreeBark.isChecked) {
                val tree = treesList[currentSpinnerItem]
                textViewResume.text = Calculator.calculate(lengthValueCm, widthValueCm, tree)
            } else {
                textViewResume.text = Calculator.calculate(lengthValueCm, widthValueCm, null)
            }

        }
    }

    private fun setValuesView(currentSize: CurrentSize) {
        if (currentSize == CurrentSize.LENGTH) {
            lengthValueM = lengthValueCm.toFloat() / 100.0F
            textViewLengthValueCm.text = lengthValueCm.toString()
            val lengthValueMFormat = "%.2f".format(lengthValueM)
            textViewLengthValueM.text = lengthValueMFormat
        } else {
            widthValueM = widthValueCm.toFloat() / 100.0F
            textViewWidthValueCm.text = widthValueCm.toString()
            val widthValueMFormat = "%.2f".format(widthValueM)
            textViewWidthValueM.text = widthValueMFormat
        }
    }

    private fun valuesLooper(imgBtn: ImageButton, currentSize: CurrentSize, buttonChanges: ButtonChanges) {

        fun cancel() {
            timer?.cancel()
            timer = null
        }

        timer = object : CountDownTimer(10000, 10) {
            override fun onTick(p0: Long) {
                if (!imgBtn.isPressed) { cancel() }

                if (currentSize == CurrentSize.LENGTH) {
                    if (buttonChanges == ButtonChanges.MINUS) {
                        if (lengthValueCm == 0) {
                            seekBarLogLength.progress = lengthValueCm
                            cancel()
                        }
                        else {
                            lengthValueCm --
                            seekBarLogLength.progress = lengthValueCm
                        }
                    }
                    else {

                        if (lengthValueCm == maxLogLength) {
                            seekBarLogLength.progress = lengthValueCm
                            cancel()
                        }
                        else {
                            lengthValueCm ++
                            seekBarLogLength.progress = lengthValueCm
                        }
                    }
                }

                else {
                    if (buttonChanges == ButtonChanges.MINUS) {
                        if (widthValueCm == 0) {
                            seekBarLogWidth.progress = widthValueCm
                            cancel()
                        }
                        else {
                            widthValueCm --
                            seekBarLogWidth.progress = widthValueCm
                        }
                    }
                    else {
                        if (widthValueCm == maxLogWidth) {
                            seekBarLogWidth.progress = widthValueCm
                            cancel()
                        }
                        else {
                            widthValueCm++
                            seekBarLogWidth.progress = widthValueCm
                        }
                    }
                }
                setResult()
            }

            override fun onFinish() {}
        }.start()
    }

    override fun onStart() {
        super.onStart()
        val woodPackageIndex = Settings.VolumeCalculatorView.woodPackageFromSelectIndex
        if (woodPackageIndex >= 0) {
            Settings.VolumeCalculatorView.woodPackageFromSelectIndex = - 1
            thread {
                DatabaseManagerDao.getDataBase(this)?.let {
                    it.woodPackagesDao().selectWithId(woodPackageIndex).let { woodPackages ->
                        woodPackagesCurrent = woodPackages
                        this.runOnUiThread {
                            textViewWoodPackagesName.text = woodPackagesCurrent?.name
                        }
                    }
                }
            }
        }
    }

}