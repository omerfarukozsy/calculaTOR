package com.ofo.calculator.ui.calculator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ofo.calculator.databinding.FragmentCalculatorBinding

class CalculatorFragment : Fragment() {
    private var _binding: FragmentCalculatorBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CalculatorViewModel by viewModels()
    private val stepsAdapter = StepsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalculatorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        binding.rvSteps.apply {
            layoutManager = LinearLayoutManager(context).apply {
                stackFromEnd = true
            }
            adapter = stepsAdapter
        }
    }

    private fun setupObservers() {
        viewModel.expression.observe(viewLifecycleOwner) { expression ->
            binding.tvExpression.text = expression
        }

        viewModel.result.observe(viewLifecycleOwner) { result ->
            binding.tvResult.text = result
        }

        viewModel.calculationSteps.observe(viewLifecycleOwner) { steps ->
            stepsAdapter.submitList(steps)
            binding.rvSteps.smoothScrollToPosition(steps.size)
        }
    }

    private fun setupClickListeners() {
        with(binding) {
            // Numbers
            btn0.setOnClickListener { viewModel.onNumberClick("0") }
            btn1.setOnClickListener { viewModel.onNumberClick("1") }
            btn2.setOnClickListener { viewModel.onNumberClick("2") }
            btn3.setOnClickListener { viewModel.onNumberClick("3") }
            btn4.setOnClickListener { viewModel.onNumberClick("4") }
            btn5.setOnClickListener { viewModel.onNumberClick("5") }
            btn6.setOnClickListener { viewModel.onNumberClick("6") }
            btn7.setOnClickListener { viewModel.onNumberClick("7") }
            btn8.setOnClickListener { viewModel.onNumberClick("8") }
            btn9.setOnClickListener { viewModel.onNumberClick("9") }

            // Operators
            btnPlus.setOnClickListener { viewModel.onOperatorClick("+") }
            btnMinus.setOnClickListener { viewModel.onOperatorClick("-") }
            btnMultiply.setOnClickListener { viewModel.onOperatorClick("×") }
            btnDivide.setOnClickListener { viewModel.onOperatorClick("÷") }

            // Special functions
            btnDot.setOnClickListener { viewModel.onDecimalClick() }
            btnEquals.setOnClickListener { viewModel.onEqualsClick() }
            btnClear.setOnClickListener { viewModel.onClearClick() }
            btnBackspace.setOnClickListener { viewModel.onBackspaceClick() }
            btnPercent.setOnClickListener { viewModel.onPercentClick() }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = CalculatorFragment()
    }
} 