package com.ofo.calculator.ui.calculator

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.text.DecimalFormat
import java.util.*

class CalculatorViewModel : ViewModel() {
    private val _expression = MutableLiveData<String>()
    val expression: LiveData<String> = _expression

    private val _result = MutableLiveData<String>()
    val result: LiveData<String> = _result

    private val _calculationSteps = MutableLiveData<List<String>>()
    val calculationSteps: LiveData<List<String>> = _calculationSteps

    private var currentNumber = StringBuilder()
    private var currentExpression = StringBuilder()
    private val steps = mutableListOf<String>()
    private val decimalFormat = DecimalFormat("#.########")

    init {
        _expression.value = ""
        _result.value = "0"
        _calculationSteps.value = emptyList()
    }

    fun onNumberClick(number: String) {
        if (currentNumber.length < 15) {
            currentNumber.append(number)
            currentExpression.append(number)
            updateExpression()
            calculateResult()
        }
    }

    fun onOperatorClick(operator: String) {
        if (currentNumber.isNotEmpty()) {
            currentNumber.clear()
            currentExpression.append(" $operator ")
            updateExpression()
        } else if (currentExpression.isNotEmpty() && currentExpression.last() != ' ') {
            currentExpression.append(" $operator ")
            updateExpression()
        }
    }

    fun onDecimalClick() {
        if (!currentNumber.contains(".")) {
            if (currentNumber.isEmpty()) {
                currentNumber.append("0")
                currentExpression.append("0")
            }
            currentNumber.append(".")
            currentExpression.append(".")
            updateExpression()
        }
    }

    fun onClearClick() {
        currentNumber.clear()
        currentExpression.clear()
        steps.clear()
        _expression.value = ""
        _result.value = "0"
        _calculationSteps.value = emptyList()
    }

    fun onBackspaceClick() {
        if (currentExpression.isNotEmpty()) {
            if (currentExpression.last() == ' ') {
                currentExpression.delete(currentExpression.length - 3, currentExpression.length)
            } else {
                currentExpression.deleteCharAt(currentExpression.length - 1)
                if (currentNumber.isNotEmpty()) {
                    currentNumber.deleteCharAt(currentNumber.length - 1)
                }
            }
            updateExpression()
            calculateResult()
        }
    }

    fun onEqualsClick() {
        if (currentExpression.isNotEmpty()) {
            val result = calculateExpression(currentExpression.toString())
            steps.add("${currentExpression.toString()} = $result")
            _calculationSteps.value = steps.toList()
            currentExpression.clear()
            currentExpression.append(result)
            currentNumber.clear()
            updateExpression()
            _result.value = result
        }
    }

    fun onPercentClick() {
        if (currentNumber.isNotEmpty()) {
            val number = currentNumber.toString().toDouble()
            val result = number / 100.0
            currentNumber.clear()
            currentNumber.append(decimalFormat.format(result))
            
            val lastSpaceIndex = currentExpression.lastIndexOf(" ")
            if (lastSpaceIndex != -1) {
                currentExpression.replace(lastSpaceIndex + 1, currentExpression.length, decimalFormat.format(result))
            } else {
                currentExpression.replace(0, currentExpression.length, decimalFormat.format(result))
            }
            
            updateExpression()
            calculateResult()
        }
    }

    private fun updateExpression() {
        _expression.value = currentExpression.toString()
    }

    private fun calculateResult() {
        if (currentExpression.isNotEmpty()) {
            val result = calculateExpression(currentExpression.toString())
            _result.value = result
        } else {
            _result.value = "0"
        }
    }

    private fun calculateExpression(expression: String): String {
        try {
            val tokens = expression.split(" ")
            if (tokens.isEmpty()) return "0"

            val numbers = Stack<Double>()
            val operators = Stack<String>()

            for (token in tokens) {
                when {
                    token.isEmpty() -> continue
                    isNumber(token) -> numbers.push(token.toDouble())
                    isOperator(token) -> {
                        while (operators.isNotEmpty() && hasPrecedence(token, operators.peek())) {
                            numbers.push(applyOperation(operators.pop(), numbers.pop(), numbers.pop()))
                        }
                        operators.push(token)
                    }
                }
            }

            while (operators.isNotEmpty()) {
                numbers.push(applyOperation(operators.pop(), numbers.pop(), numbers.pop()))
            }

            return formatResult(numbers.pop())
        } catch (e: Exception) {
            return "Error"
        }
    }

    private fun isNumber(token: String): Boolean {
        return try {
            token.toDouble()
            true
        } catch (e: NumberFormatException) {
            false
        }
    }

    private fun isOperator(token: String): Boolean {
        return token in listOf("+", "-", "×", "÷")
    }

    private fun hasPrecedence(op1: String, op2: String): Boolean {
        if (op2 == "(" || op2 == ")") return false
        return !((op1 == "×" || op1 == "÷") && (op2 == "+" || op2 == "-"))
    }

    private fun applyOperation(operator: String, b: Double, a: Double): Double {
        return when (operator) {
            "+" -> a + b
            "-" -> a - b
            "×" -> a * b
            "÷" -> if (b != 0.0) a / b else Double.POSITIVE_INFINITY
            else -> 0.0
        }
    }

    private fun formatResult(number: Double): String {
        return when {
            number.isInfinite() -> "Error"
            number.isNaN() -> "Error"
            number == number.toLong().toDouble() -> number.toLong().toString()
            else -> decimalFormat.format(number)
        }
    }
} 