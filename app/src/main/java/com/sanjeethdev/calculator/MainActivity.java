package com.sanjeethdev.calculator;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.sanjeethdev.calculator.databinding.ActivityMainBinding;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    DBHelper dbHelper;
    private ActivityMainBinding binding;

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

        binding.mainParentheses.setOnClickListener(view -> {
            binding.mainResult.setText("");
            String equation = binding.mainEquation.getText().toString();
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

        binding.mainBackspace.setOnClickListener(view -> {
            binding.mainEquation.setText(removeLastCharacter(binding.mainEquation.getText().toString()));
        });

        binding.mainPlus.setOnClickListener(view -> {
            String lastOperatedValue = binding.mainResult.getText().toString();
            if (!binding.mainEquation.getText().toString().isEmpty() && isOperatorPlacementValid(binding.mainEquation.getText().toString())) {
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
            if (!binding.mainEquation.getText().toString().isEmpty() && isOperatorPlacementValid(binding.mainEquation.getText().toString())) {
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
            if (!binding.mainEquation.getText().toString().isEmpty() && isOperatorPlacementValid(binding.mainEquation.getText().toString())) {
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
            if (!binding.mainEquation.getText().toString().isEmpty() && isOperatorPlacementValid(binding.mainEquation.getText().toString())) {
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
            if (!binding.mainEquation.getText().toString().isEmpty() && isOperatorPlacementValid(binding.mainEquation.getText().toString())) {
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
//            TODO: Expand on abilities of +/-
            binding.mainResult.setText("");
            binding.mainEquation.append("(-");

        });

        binding.mainDot.setOnClickListener(view -> {
            if (binding.mainEquation.getText().toString().isEmpty()) {
                binding.mainEquation.append("0.");
            } else {
                binding.mainEquation.append(".");
            }
        });


        binding.mainEqual.setOnClickListener(view -> {
//            TODO: Add Equate to last used operand and operation.
            String equation = binding.mainEquation.getText().toString();

            if (!equation.isEmpty() && containsDigits(equation)) {
                String evaluationResult = evaluateExpression(equation);

                if (!evaluationResult.equals("Error")) {

                    if (evaluationResult.endsWith(".0")) {
                        evaluationResult = evaluationResult.replace(".0", "");
                    }

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
        Deque<Character> stack = new ArrayDeque<Character>();

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

    private String removeLastCharacter(String str) {
        return removeCharacter(str);
    }

    private String removeCharacter(String string) {
        if (string != null && !string.trim().isEmpty()) {
            return string.substring(0, string.length() - 1);
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