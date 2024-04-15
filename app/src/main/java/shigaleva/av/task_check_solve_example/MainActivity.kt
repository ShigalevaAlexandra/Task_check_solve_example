package shigaleva.av.task_check_solve_example

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.os.SystemClock
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import kotlin.math.max
import kotlin.math.min

class MainActivity : AppCompatActivity() {

    private var countRightAnswers = 0  //кол-во правильно решенных примеров
    private var countWrongAnswers = 0  //кол-во не правильно решенных примеров
    private var countAnswers = 0  //кол-во решенных примеров

    private var backgroundResult = 0  //сохранение значения цвета выделения результата

    private var firstNumberSave = 0
    private var secondNumberSave = 0
    private var resultSave = 0
    private var operationSave = ""

    private var startTime: Long = 0
    private var endTime: Long = 0
    private var minTime: Long = Long.MAX_VALUE
    private var maxTime: Long = 0
    private var averageTime: Long = 0

    private var visibleBtnStart = true
    private var visibleBtnCheck = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        //Поиск элементов и запись их состояния в переменные
        val btnRight = findViewById<Button>(R.id.btn_right)
        val btnWrong = findViewById<Button>(R.id.btn_wrong)
        val btnStart = findViewById<Button>(R.id.btn_start)
        val gridExampleArea = findViewById<GridLayout>(R.id.example_area)

        //Обработка событий кнопок
        btnRight.isEnabled = false   //изначально конпки "верно" и "неверно" недоступны
        btnWrong.isEnabled = false

        //нажатие кнопки "старт"
        btnStart.setOnClickListener {
            visibleBtnStart = false
            it.isEnabled = visibleBtnStart
            visibleBtnCheck = true
            btnRight.isEnabled = visibleBtnCheck
            btnWrong.isEnabled = visibleBtnCheck
            startTime = SystemClock.elapsedRealtime()
            gridExampleArea.setBackgroundColor(Color.TRANSPARENT)
            backgroundResult = 0
            addNewExample()
        }

        //нажатие кнопки "верно"
        btnRight.setOnClickListener {
            checkResult(true)
        }
        //нажатие кнопки "неверно"
        btnWrong.setOnClickListener {
            checkResult(false)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    //функция создания нового примера
    private fun addNewExample() {
        val txtViewFirstNumber = findViewById<TextView>(R.id.txtView_first_number)
        val txtViewSecondNumber = findViewById<TextView>(R.id.txtView_second_number)
        val txtViewOperation = findViewById<TextView>(R.id.txtView_operation)
        val txtViewResult = findViewById<TextView>(R.id.txtView_result)

        val firstNumber = (10..99).random()
        var secondNumber = (10..99).random()

        //Если осуществляется операция деление, то делимое,
        //делитель и частное должны быть целыми числами
        while (firstNumber % secondNumber != 0) secondNumber = (10..99).random()

        txtViewFirstNumber.text = "$firstNumber"
        txtViewSecondNumber.text = "$secondNumber"

        when ((0..3).random()) {
            0 -> txtViewOperation.text = "+"
            1 -> txtViewOperation.text = "-"
            2 -> txtViewOperation.text = "*"
            3 -> txtViewOperation.text = "/"
        }

        val result = botSolveExample()

        if ((0..1).random() == 0) txtViewResult.text = result.toString()
        else {
            var botAnswer = 0
            while (botAnswer == result) botAnswer = (0..100).random()

            txtViewResult.text = botAnswer.toString()
        }

        firstNumberSave = firstNumber
        secondNumberSave = secondNumber
        operationSave = txtViewOperation.text.toString()
        resultSave = txtViewResult.text.toString().toInt()
    }

    //функция решения примера компьютером
    private fun botSolveExample(): Int {
        val txtViewFirstNumber = findViewById<TextView>(R.id.txtView_first_number)
        val txtViewSecondNumber = findViewById<TextView>(R.id.txtView_second_number)
        val txtViewOperation = findViewById<TextView>(R.id.txtView_operation)

        val firstNumber = txtViewFirstNumber.text.toString().toInt()
        val secondNumber = txtViewSecondNumber.text.toString().toInt()
        val operation = txtViewOperation.text.toString()

        var result = 0

        when (operation) {
            "+" -> result = firstNumber + secondNumber
            "-" -> result = firstNumber - secondNumber
            "*" -> result = firstNumber * secondNumber
            "/" -> result = firstNumber / secondNumber
        }

        return  result
    }

    //функция проверки правильности решения примера
    private fun checkResult(answer: Boolean) {
        val txtViewResult = findViewById<TextView>(R.id.txtView_result)
        val btnRight = findViewById<Button>(R.id.btn_right)
        val btnWrong = findViewById<Button>(R.id.btn_wrong)
        val btnStart = findViewById<Button>(R.id.btn_start)

        countAnswers++
        endTime = SystemClock.elapsedRealtime()

        val time = endTime - startTime

        averageTime = (averageTime * countAnswers + time) / countAnswers
        maxTime = max(time, maxTime)
        minTime = min(time, minTime)

        if (answer == (botSolveExample() == (txtViewResult.text.toString().toIntOrNull() ?: 0))) {
            countRightAnswers++
            backgroundResult = 5
        }
        else {
            countWrongAnswers++
            backgroundResult = 2
        }

        visibleBtnStart = true
        visibleBtnCheck = false

        btnStart.isEnabled = visibleBtnStart
        btnRight.isEnabled = visibleBtnCheck
        btnWrong.isEnabled = visibleBtnCheck
        updateDataInElement()
    }

    //функция обновления данных в элементах активити
    @SuppressLint("SetTextI18n")
    private fun updateDataInElement() {
        val txtViewCountExample = findViewById<TextView>(R.id.txtView_count_example)
        val txtViewCountRightAnswer = findViewById<TextView>(R.id.txtView_count_right)
        val txtViewCountWrongAnswer = findViewById<TextView>(R.id.txtView_count_wrong)
        val txtViewProgress = findViewById<TextView>(R.id.txtView_progress)

        val gridExampleArea = findViewById<GridLayout>(R.id.example_area)

        val txtViewFirstNumber = findViewById<TextView>(R.id.txtView_first_number)
        val txtViewSecondNumber = findViewById<TextView>(R.id.txtView_second_number)
        val txtViewOperation = findViewById<TextView>(R.id.txtView_operation)

        val txtViewResult = findViewById<TextView>(R.id.txtView_result)

        val txtViewMaxTime = findViewById<TextView>(R.id.txtView_max_time)
        val txtViewMinTime = findViewById<TextView>(R.id.txtView_min_time)
        val txtViewAverageTime = findViewById<TextView>(R.id.txtView_average_time)

        val btnStart = findViewById<Button>(R.id.btn_start)
        val btnRight = findViewById<Button>(R.id.btn_right)
        val btnWrong = findViewById<Button>(R.id.btn_wrong)

        btnStart.isEnabled = visibleBtnStart
        btnWrong.isEnabled = visibleBtnCheck
        btnRight.isEnabled = visibleBtnCheck

        txtViewCountExample.text = "$countAnswers"
        txtViewCountRightAnswer.text = "$countRightAnswers"
        txtViewCountWrongAnswer.text = "$countWrongAnswers"
        txtViewProgress.text = "${"%.2f".format((countRightAnswers * 100 / countAnswers.toDouble()))} %"

        when (backgroundResult) {
            0 -> gridExampleArea.setBackgroundColor(Color.TRANSPARENT)
            2 -> gridExampleArea.setBackgroundColor(Color.RED)
            5 -> gridExampleArea.setBackgroundColor(Color.GREEN)
        }

        txtViewFirstNumber.text = "$firstNumberSave"
        txtViewSecondNumber.text = "$secondNumberSave"
        txtViewOperation.text = "$operationSave"
        txtViewResult.text = "$resultSave"

        txtViewMaxTime.text = (maxTime / 1000).toString()
        txtViewMinTime.text = ((
                if (minTime == Long.MAX_VALUE) 0
                else minTime) / 1000).toString()
        txtViewAverageTime.text = (averageTime / 1000).toString()
    }

    //сохранение значений переменных
    override fun onSaveInstanceState(instanceState: Bundle) {
        super.onSaveInstanceState(instanceState)
        instanceState.putInt("countAnswers", countAnswers)
        instanceState.putInt("countRightAnswers", countRightAnswers)
        instanceState.putInt("countWrongAnswers", countWrongAnswers)
        instanceState.putInt("backgroundResult", backgroundResult)
        instanceState.putInt("firstNumberSave", firstNumberSave)
        instanceState.putInt("secondNumberSave", secondNumberSave)
        instanceState.putString("operationSave", operationSave)
        instanceState.putInt("resultSave", resultSave)
        instanceState.putLong("maxTime", maxTime)
        instanceState.putLong("minTime", minTime)
        instanceState.putLong("averageTime", averageTime)
        instanceState.putBoolean("visibleBtnStart", visibleBtnStart)
        instanceState.putBoolean("visibleBtnCheck", visibleBtnCheck)
    }

    //использование сохраненных значений переменных
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        countAnswers = savedInstanceState.getInt("countAnswers")
        countRightAnswers = savedInstanceState.getInt("countRightAnswers")
        countWrongAnswers = savedInstanceState.getInt("countWrongAnswers")
        backgroundResult = savedInstanceState.getInt("backgroundResult")
        firstNumberSave = savedInstanceState.getInt("firstNumberSave")
        secondNumberSave = savedInstanceState.getInt("secondNumberSave")
        operationSave = savedInstanceState.getString("operationSave").toString()
        resultSave = savedInstanceState.getInt("resultSave")
        maxTime = savedInstanceState.getLong("maxTime")
        minTime = savedInstanceState.getLong("minTime")
        averageTime = savedInstanceState.getLong("averageTime")
        visibleBtnStart = savedInstanceState.getBoolean("visibleBtnStart")
        visibleBtnCheck = savedInstanceState.getBoolean("visibleBtnCheck")
        updateDataInElement()
    }
}
