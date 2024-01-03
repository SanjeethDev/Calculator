package com.sanjeethdev.calculator;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.TypedArrayUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.sanjeethdev.calculator.databinding.ActivityMainBinding;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;


public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View bindingView = binding.getRoot();
        setContentView(bindingView);

        dbHelper = new DBHelper(this);

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
            Log.d(TAG, "Backspace: 1");
            binding.mainEquation.setText(removeLastChar(binding.mainEquation.getText().toString()));
        });

        binding.mainPlus.setOnClickListener(view -> {

            binding.mainResult.setText("");
            if (!binding.mainEquation.getText().toString().isEmpty() && !isOperatorValid(binding.mainEquation.getText().toString())) {
                binding.mainEquation.append("+");
            } else {
                Toast.makeText(this, "Invalid format used.", Toast.LENGTH_SHORT).show();
            }

        });

        binding.mainMinus.setOnClickListener(view -> {
            binding.mainResult.setText("");
            if (!binding.mainEquation.getText().toString().isEmpty() && !isOperatorValid(binding.mainEquation.getText().toString())) {
                binding.mainEquation.append("-");
            } else {
                Toast.makeText(this, "Invalid format used.", Toast.LENGTH_SHORT).show();
            }
        });

        binding.mainMultiply.setOnClickListener(view -> {
            binding.mainResult.setText("");
            if (!binding.mainEquation.getText().toString().isEmpty() && !isOperatorValid(binding.mainEquation.getText().toString())) {
                binding.mainEquation.append("*");
            } else {
                Toast.makeText(this, "Invalid format used.", Toast.LENGTH_SHORT).show();
            }
        });

        binding.mainDivide.setOnClickListener(view -> {
            binding.mainResult.setText("");
            if (!binding.mainEquation.getText().toString().isEmpty() && !isOperatorValid(binding.mainEquation.getText().toString())) {
                binding.mainEquation.append("/");
            } else {
                Toast.makeText(this, "Invalid format used.", Toast.LENGTH_SHORT).show();
            }
        });

        binding.mainPercentage.setOnClickListener(view -> {
            binding.mainResult.setText("");
            if (!binding.mainEquation.getText().toString().isEmpty() && !isOperatorValid(binding.mainEquation.getText().toString())) {
                binding.mainEquation.append("%");
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
            if (!binding.mainEquation.getText().toString().isEmpty()) {
                String expression = binding.mainEquation.getText().toString();
                String evaluationResult = evaluateExpression(expression);
                if (!evaluationResult.equals("Error")) {

                    if (evaluationResult.endsWith(".0")) {
                        evaluationResult = evaluationResult.replace(".0", "");
                    }

                    boolean checkInsert = dbHelper.insertEquation(expression, evaluationResult);
                    if (!checkInsert) {
                        Toast.makeText(this, "Not Recorded, something went wrong.", Toast.LENGTH_SHORT).show();
                    }

                    binding.mainResult.setText(evaluationResult);
                    binding.mainEquation.setText("");
                } else {
                    binding.mainResult.setText(R.string.error);
                    binding.mainEquation.setText("");
                }
            }

        });

        binding.mainHistory.setOnClickListener(view -> {
            startActivity(new Intent(this, HistoryActivity.class));
        });


    }

    private boolean areBracketsBalanced(String expression)
    {
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

    private String removeLastChar(String str) {
        return removeChars(str, 1);
    }

    private String removeChars(String str, int numberOfCharactersToRemove) {
        if(str != null && !str.trim().isEmpty()) {
            return str.substring(0, str.length() - numberOfCharactersToRemove);
        }
        return "";
    }

    private String evaluateExpression(String expression) {
        try {
            Context context = Context.enter();
            context.setOptimizationLevel(-1);
            Scriptable scriptable = context.initSafeStandardObjects();
            if (expression.contains("%")) {
                expression = expression.replace("%","/100*");
            } else if (expression.contains(")") && !(expression.indexOf(")") == (expression.length()-1))) {
                expression = expression.replace(")",")*");
            }
            return context.evaluateString(scriptable,expression,"Javascript",1,null).toString();
        } catch (Exception exception) {
            return "Error";
        }
    }

    private boolean isOperatorValid(String expression) {
        String[] operators = {"+", "-", "*","/"};
        return  Arrays.stream(operators).anyMatch(expression.substring(expression.length() - 1)::equals);
    }
}