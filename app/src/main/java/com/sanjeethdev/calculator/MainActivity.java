package com.sanjeethdev.calculator;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.sanjeethdev.calculator.databinding.ActivityMainBinding;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

public class MainActivity extends AppCompatActivity {
    DBHelper dbHelper;
    private ActivityMainBinding binding;
    private String equation = "";

    @Override
    protected void onResume() {
        super.onResume();
        binding.mainHistory.setEnabled(doesHistoryExist());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View bindingView = binding.getRoot();
        setContentView(bindingView);

        dbHelper = new DBHelper(this);
        binding.mainHistory.setEnabled(doesHistoryExist());

        binding.mainEquation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                equation = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        binding.mainParentheses.setOnClickListener(view -> {
            binding.mainResult.setText("");
            // Deals with the parenthesis insertion; either '(' or ')'
            if (equation.matches(".*\\d")) {
                binding.mainEquation.append(")");
            } else if (equation.endsWith(")") && bracketsBalance(equation) != 0) {
                binding.mainEquation.append(")");
            } else if (equation.endsWith(")") && bracketsBalance(equation) == 0) {
                binding.mainEquation.append("*(");
            } else {
                binding.mainEquation.append("(");
            }
        });

        binding.mainClear.setOnClickListener(view -> {
            binding.mainEquation.setText("");
            binding.mainResult.setText("");
        });

        binding.mainBackspace.setOnClickListener(view -> binding.mainEquation.setText(removeLastCharacters(equation, 1)));

        binding.mainPlus.setOnClickListener(view -> {
            String lastOperatedValue = binding.mainResult.getText().toString();
            if (!equation.isEmpty()) {
                if (!equation.endsWith("(")) {
                    operatorSwap("+");
                }
            } else if (!lastOperatedValue.isEmpty()) {
                binding.mainEquation.append(lastOperatedValue + "+");
                binding.mainResult.setText("");
            } else {
                Toast.makeText(this, "Invalid format used", Toast.LENGTH_SHORT).show();
            }

        });

        binding.mainMinus.setOnClickListener(view -> {
            String lastOperatedValue = binding.mainResult.getText().toString();
            if (!equation.isEmpty()) {
                operatorSwap("-");
            } else if (!lastOperatedValue.isEmpty()) {
                binding.mainEquation.append(lastOperatedValue + "-");
                binding.mainResult.setText("");
            } else {
                Toast.makeText(this, "Invalid format used", Toast.LENGTH_SHORT).show();
            }
        });

        binding.mainMultiply.setOnClickListener(view -> {
            String lastOperatedValue = binding.mainResult.getText().toString();
            if (!equation.isEmpty()) {
                if (!equation.endsWith("(")) {
                    operatorSwap(getString(R.string.Multiply));
                }
            } else if (!lastOperatedValue.isEmpty()) {
                binding.mainEquation.append(lastOperatedValue + R.string.Multiply);
                binding.mainResult.setText("");
            } else {
                Toast.makeText(this, "Invalid format used", Toast.LENGTH_SHORT).show();
            }
        });

        binding.mainDivide.setOnClickListener(view -> {
            String lastOperatedValue = binding.mainResult.getText().toString();
            if (!equation.isEmpty()) {
                if (!equation.endsWith("(")) {
                    operatorSwap(getString(R.string.Divide));
                }
            } else if (!lastOperatedValue.isEmpty()) {
                binding.mainEquation.append(lastOperatedValue + R.string.Divide);
                binding.mainResult.setText("");
            } else {
                Toast.makeText(this, "Invalid format used", Toast.LENGTH_SHORT).show();
            }
        });

        binding.mainPercentage.setOnClickListener(view -> {
            String lastOperatedValue = binding.mainResult.getText().toString();
            if (!equation.isEmpty()) {
                if (!equation.endsWith("(")) {
                    operatorSwap("%");
                }
            } else if (!lastOperatedValue.isEmpty()) {
                binding.mainEquation.append(lastOperatedValue + "%");
                binding.mainResult.setText("");
            } else {
                Toast.makeText(this, "Invalid format used", Toast.LENGTH_SHORT).show();
            }
        });

        binding.mainOne.setOnClickListener(view -> {
            binding.mainResult.setText("");
            binding.mainEquation.append("1");
        });

        binding.mainTwo.setOnClickListener(view -> {
            binding.mainResult.setText("");
            binding.mainEquation.append("2");
        });

        binding.mainThree.setOnClickListener(view -> {
            binding.mainResult.setText("");
            binding.mainEquation.append("3");
        });

        binding.mainFour.setOnClickListener(view -> {
            binding.mainResult.setText("");
            binding.mainEquation.append("4");
        });

        binding.mainFive.setOnClickListener(view -> {
            binding.mainResult.setText("");
            binding.mainEquation.append("5");
        });

        binding.mainSix.setOnClickListener(view -> {
            binding.mainResult.setText("");
            binding.mainEquation.append("6");
        });

        binding.mainSeven.setOnClickListener(view -> {
            binding.mainResult.setText("");
            binding.mainEquation.append("7");
        });

        binding.mainEight.setOnClickListener(view -> {
            binding.mainResult.setText("");
            binding.mainEquation.append("8");
        });

        binding.mainNine.setOnClickListener(view -> {
            binding.mainResult.setText("");
            binding.mainEquation.append("9");
        });

        binding.mainZero.setOnClickListener(view -> {
            binding.mainResult.setText("");
            binding.mainEquation.append("0");
        });

        binding.mainPlusminus.setOnClickListener(view -> {
            binding.mainResult.setText("");
            if (!equation.isEmpty()) {
                // Checks for negative number and swaps it for positive
                StringBuilder reversedLastOperand = new StringBuilder();
                StringBuilder lastOperand = new StringBuilder();
                int digitsCount = 0;
                boolean negativeFlag = false;

                for (int i = equation.length() - 1; i >= 0; i--) {
                    // Sorta captures the last operand in reverse and sets the negative flag
                    if (containsDigits(String.valueOf(equation.charAt(i)))) {
                        reversedLastOperand.append(equation.charAt(i));
                        digitsCount++;
                    } else if (String.valueOf(equation.charAt(i)).equals("-")) {
                        if (String.valueOf(equation.charAt(i-1)).equals("(")) {
                            negativeFlag = true;
                        } else {
                            break;
                        }
                    } else {
                        break;
                    }
                }

                //Reverse the reversed operand
                for (int i = reversedLastOperand.length() - 1; i >= 0; i--) {
                    lastOperand.append(reversedLastOperand.charAt(i));
                }

                String finalAdjustment;
                if (negativeFlag) {
                    finalAdjustment = removeLastCharacters(equation, digitsCount+2) + lastOperand;
                } else {
                    finalAdjustment = removeLastCharacters(equation, digitsCount) + "(-" + lastOperand;
                }

                binding.mainEquation.setText(finalAdjustment);
            } else {
                binding.mainEquation.append("(-");
            }

        });

        binding.mainDot.setOnClickListener(view -> {
            binding.mainResult.setText("");
            if (equation.isEmpty()) {
                binding.mainEquation.append("0.");
            } else {
                binding.mainEquation.append(".");
            }
        });


        binding.mainEqual.setOnClickListener(view -> {
            if (!equation.isEmpty() && containsDigits(equation)) {
                if (bracketsBalance(equation) != 0) {
                    int bracketSize = bracketsBalance(equation);
                    for (int i = 0; i < bracketSize; i++) {
                        binding.mainEquation.append(")");
                    }
                }

                String adjustedEquation = equation;
                if (equation.contains(getString(R.string.Divide)) || equation.contains(getString(R.string.Multiply))) {
                    adjustedEquation = adjustedEquation.replace(getString(R.string.Divide), "/").replace(getString(R.string.Multiply), "*");
                }

                String evaluationResult = evaluateExpression(adjustedEquation);
                if (!evaluationResult.equals("Error")) {
                    if (evaluationResult.endsWith(".0")) {
                        evaluationResult = evaluationResult.replace(".0", "");
                    }
                    // Store the recent calculations in database
                    boolean checkInsert = dbHelper.insertEquation(equation, evaluationResult);
                    if (!checkInsert) {
                        Toast.makeText(this, "Not Recorded, something went wrong", Toast.LENGTH_SHORT).show();
                    }
                    binding.mainResult.setText(evaluationResult);
                    binding.mainEquation.setText("");
                    binding.mainHistory.setEnabled(doesHistoryExist());
                } else {
                    binding.mainResult.setText(R.string.error);
                    binding.mainEquation.setText("");
                }
            }
        });

        binding.mainHistory.setOnClickListener(view -> {
            if (doesHistoryExist()) {
                startActivity(new Intent(this, HistoryActivity.class));
            }
        });

    }

    // Checks if history has and records in the database
    private boolean doesHistoryExist() {
        Cursor cursor = dbHelper.getHistory();
        boolean result = cursor.getCount() != 0;
        cursor.close();
        return result;

    }

    // Checks if the given string contains any digit
    private boolean containsDigits(String expression) {
        return expression.matches(".*\\d.*");
    }

    private int bracketsBalance(String expression) {
        Deque<Character> stack = new ArrayDeque<>();

        for (int i = 0; i < expression.length(); i++) {
            char x = expression.charAt(i);
            if (x == '(') {
                stack.push(x);
                continue;
            }
            if (x == ')' && !stack.isEmpty()) {
                stack.pop();
            }
        }
        return (stack.size());
    }

    // Removes characters from the end of the string based on the count
    private String removeLastCharacters(String string, int count) {
        if (string != null && !string.trim().isEmpty()) {
            return string.substring(0, string.length() - count);
        }
        return "";
    }

    // Evaluates the expression
    private String evaluateExpression(String expression) {
        try {
            Context context = Context.enter();
            context.setOptimizationLevel(-1);
            Scriptable scriptable = context.initSafeStandardObjects();
            if (expression.contains("%")) {
                expression = expression.replace("%", "/100*");
            }
            return context.evaluateString(scriptable, expression, "Javascript", 1, null).toString();
        } catch (Exception exception) {
            Log.d(TAG, "evaluateExpression: " + exception);
            return "Error";
        }
    }

    // Checks if the last character of the string is an operator
    private boolean isItAnOperator(String expression) {
        String[] operators = {"+", "-", "×", "÷","%"};
        return !Arrays.asList(operators).contains(expression.substring(expression.length() - 1));
    }

    // Swaps the operator if its at the end
    private void operatorSwap(String operator) {
        if (!equation.endsWith(operator) && !isItAnOperator(equation)) {
            String changeOperator;
            changeOperator = removeLastCharacters(equation, 1) + operator;
            binding.mainEquation.setText(changeOperator);
        } else if (!String.valueOf(equation.charAt(equation.length()-1)).equals(operator)) {
            binding.mainEquation.append(operator);
        } else {
            Toast.makeText(this, "Invalid format used", Toast.LENGTH_SHORT).show();
        }
    }

}