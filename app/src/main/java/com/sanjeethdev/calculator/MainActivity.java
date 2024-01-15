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
            //Don't close an empty bracket.
            if (areBracketsBalanced(equation)) {
                binding.mainEquation.append("(");
            } else {
                binding.mainEquation.append(")");
            }
        });

        binding.mainClear.setOnClickListener(view -> {
            binding.mainEquation.setText("");
            binding.mainResult.setText("");
        });

        binding.mainBackspace.setOnClickListener(view -> binding.mainEquation.setText(removeLastCharacters(equation, 1)));

        // TODO: Add last operator swapping function
        binding.mainPlus.setOnClickListener(view -> {
            String lastOperatedValue = binding.mainResult.getText().toString();
            if (!equation.isEmpty() && isOperatorPlacementValid(equation)) {
                binding.mainEquation.append("+");
            } else if (!lastOperatedValue.isEmpty()) {
                binding.mainEquation.append(lastOperatedValue + "+");
                binding.mainResult.setText("");
            } else {
                Toast.makeText(this, "Invalid format used.", Toast.LENGTH_SHORT).show();
            }

        });

        binding.mainMinus.setOnClickListener(view -> {
            String lastOperatedValue = binding.mainResult.getText().toString();
            if (!equation.isEmpty() && isOperatorPlacementValid(equation)) {
                binding.mainEquation.append("-");
            } else if (!lastOperatedValue.isEmpty()) {
                binding.mainEquation.append(lastOperatedValue + "-");
                binding.mainResult.setText("");
            } else {
                Toast.makeText(this, "Invalid format used.", Toast.LENGTH_SHORT).show();
            }
        });

        binding.mainMultiply.setOnClickListener(view -> {
            String lastOperatedValue = binding.mainResult.getText().toString();
            if (!equation.isEmpty() && isOperatorPlacementValid(equation)) {
                binding.mainEquation.append("*");
            } else if (!lastOperatedValue.isEmpty()) {
                binding.mainEquation.append(lastOperatedValue + "*");
                binding.mainResult.setText("");
            } else {
                Toast.makeText(this, "Invalid format used.", Toast.LENGTH_SHORT).show();
            }
        });

        binding.mainDivide.setOnClickListener(view -> {
            String lastOperatedValue = binding.mainResult.getText().toString();
            if (!equation.isEmpty() && isOperatorPlacementValid(equation)) {
                binding.mainEquation.append("/");
            } else if (!lastOperatedValue.isEmpty()) {
                binding.mainEquation.append(lastOperatedValue + "/");
                binding.mainResult.setText("");
            } else {
                Toast.makeText(this, "Invalid format used.", Toast.LENGTH_SHORT).show();
            }
        });

        binding.mainPercentage.setOnClickListener(view -> {
            String lastOperatedValue = binding.mainResult.getText().toString();
            if (!equation.isEmpty() && isOperatorPlacementValid(equation)) {
                binding.mainEquation.append("%");
            } else if (!lastOperatedValue.isEmpty()) {
                binding.mainEquation.append(lastOperatedValue + "%");
                binding.mainResult.setText("");
            } else {
                Toast.makeText(this, "Invalid format used.", Toast.LENGTH_SHORT).show();
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
                    // Sorta captures the last operand in reverse
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
                String evaluationResult = evaluateExpression(equation);

                if (!evaluationResult.equals("Error")) {

                    if (evaluationResult.endsWith(".0")) {
                        evaluationResult = evaluationResult.replace(".0", "");
                    }

                    // Store the recent calculations in database
                    boolean checkInsert = dbHelper.insertEquation(equation, evaluationResult);
                    if (!checkInsert) {
                        Toast.makeText(this, "Not Recorded, something went wrong.", Toast.LENGTH_SHORT).show();
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

    private boolean doesHistoryExist() {
        Cursor cursor = dbHelper.getHistory();
        boolean result = cursor.getCount() != 0;
        cursor.close();
        return result;

    }

    private boolean containsDigits(String expression) {
        return expression.matches(".*\\d.*");
    }

    private boolean areBracketsBalanced(String expression) {
//        TODO: Better & Wider range in balancing of brackets
        Deque<Character> stack = new ArrayDeque<>();

        for (int i = 0; i < expression.length(); i++) {
            char x = expression.charAt(i);

            if (x == '(') {
                stack.push(x);
                continue;
            }

            if (x == ')') {
                stack.pop();
            }
        }
        return (stack.isEmpty());
    }

    private String removeLastCharacters(String string, int count) {
        if (string != null && !string.trim().isEmpty()) {
            return string.substring(0, string.length() - count);
        }
        return "";
    }

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

    private boolean isOperatorPlacementValid(String expression) {
        String[] operators = {"+", "-", "*", "/","%"};
        return !Arrays.asList(operators).contains(expression.substring(expression.length() - 1));
    }

}