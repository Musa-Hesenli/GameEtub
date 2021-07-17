package com.example.quiz.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quiz.R;
import com.example.quiz.apiSelectedPackage.Option;
import com.example.quiz.apiSelectedPackage.Packages;
import com.example.quiz.apiSelectedPackage.Question;
import com.example.quiz.authSharedPreference.AuthPreferences;
import com.example.quiz.resultsRoomDatabase.ResultEntity;
import com.example.quiz.resultsRoomDatabase.ViewModel;
import com.example.quiz.utilities.Constants;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link QuizInterface#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QuizInterface extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static boolean is_option_selected;
    private final Packages questionList;
    private static int question_index;
    private static boolean is_last_question;
    private static int answer_index;
    private static int selected_correct;
    private static int correct_answers = 0;
    private static int wrong_answers = 0;
    private static int progress;
    private Context context;
    private TextView questionNumber;
    private FirebaseFirestore firebaseFirestore;
    private String user_id;
    private CircularProgressIndicator progressBar;
    private AuthPreferences authPreferences;
    private String totalPoint = null;
    CountDownTimer countDownTimer;
    TextView option1, option2, option3, option4, questionTitle;

    public QuizInterface(Packages questionList, int index, int size) {
        this.questionList = questionList;
        question_index = index;
        is_option_selected = false;
        is_last_question = index == size - 1;
        progress = 0;
        if (index == 0) {
            correct_answers = 0;
            wrong_answers = 0;
        }
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment QuizInterface.
     */
    // TODO: Rename and change types and number of parameters
    public static QuizInterface newInstance(String param1, String param2, Packages questionList, int index, int size) {
        QuizInterface fragment = new QuizInterface(questionList, index, size);
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // TODO: Rename and change types of parameters
            String mParam1 = getArguments().getString(ARG_PARAM1);
            String mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_quiz_interface, container, false);
        context = view.getContext();
        questionNumber = view.findViewById(R.id.questionNumber);
        option1 = view.findViewById(R.id.option1);
        option2 = view.findViewById(R.id.option2);
        option3 = view.findViewById(R.id.option3);
        option4 = view.findViewById(R.id.option4);
        questionTitle = view.findViewById(R.id.questionTitle);
        progressBar = view.findViewById(R.id.progress_circular);

        questionNumber.setText(String.valueOf(question_index + 1) + "/" + String.valueOf(questionList.getQuestions().size()));
        attachQuestion();
        TextView timeShower = view.findViewById(R.id.timeShower);
        countDownTimer = new CountDownTimer(50000, 1000) {
            public void onTick(long millisUntilFinished) {
                timeShower.setText(String.valueOf(millisUntilFinished / 1000));
                progress += 2;
                progressBar.setProgress(progress);
            }
            public void onFinish() {
                try {
                    if (!Constants.is_quiz_finished) {
                        Log.i(Constants.TAG, "Time end");
                        timeShower.setText("0");
                        wrongOptionSelected("Time end", "Time finished and you just didn't select any answer.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        countDownTimer.start();
        defineClick();
        authPreferences = new AuthPreferences(view.getContext());
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                firebaseFirestore = FirebaseFirestore.getInstance();
                firebaseFirestore.collection("users")
                        .whereEqualTo("email", authPreferences.getString("email", ""))
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful() && task.getResult() != null) {
                                for (QueryDocumentSnapshot doc : task.getResult()) {
                                    user_id = doc.getId();
                                    totalPoint = doc.getString("total_point");
                                }
                            }
                        });
            }
        };
        thread.start();
        return view;
    }

    protected void attachQuestion() {
        Question attachedQuestion = questionList.getQuestions().get(question_index);
        questionTitle.setText(attachedQuestion.getQuestion());
        List<Option> options = attachedQuestion.getOptions();
        option1.setText("A: " + options.get(0).getTitle());
        option2.setText("B: " + options.get(1).getTitle());
        option3.setText("C: " + options.get(2).getTitle());
        option4.setText("D: " +options.get(3).getTitle());
        int index = 0;
        for (Option option : options) {
            if (option.getIs_answer()) {
                answer_index = index;
            }
            index++;
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    protected void defineClick() {
        option1.setOnClickListener(v -> {
            if(!is_option_selected) {
                if (answer_index == 0) {
                    v.setBackground(getResources().getDrawable(R.drawable.right_answer));
                    selected_correct = 1;
                    correct_answers++;
                } else {
                    v.setBackground(getResources().getDrawable(R.drawable.wrong_answer));
                    selected_correct = 0;
                    wrong_answers++;
                }
                is_option_selected = true;
                handle();
            }
        });
        option2.setOnClickListener(v -> {
            if(!is_option_selected) {
                if (answer_index == 1) {
                    v.setBackground(getResources().getDrawable(R.drawable.right_answer));
                    selected_correct = 1;
                    correct_answers++;
                } else {
                    v.setBackground(getResources().getDrawable(R.drawable.wrong_answer));
                    selected_correct = 0;
                    wrong_answers++;
                }
                is_option_selected = true;
                handle();
            }

        });
        option3.setOnClickListener(v -> {
            if(!is_option_selected) {
                if (answer_index == 2) {
                    v.setBackground(getResources().getDrawable(R.drawable.right_answer));
                    selected_correct = 1;
                    correct_answers++;
                } else {
                    v.setBackground(getResources().getDrawable(R.drawable.wrong_answer));
                    selected_correct = 0;
                    wrong_answers++;
                }
                is_option_selected = true;
                handle();
            }

        });
        option4.setOnClickListener(v -> {
            if(!is_option_selected) {
                if (answer_index == 3) {
                    v.setBackground(getResources().getDrawable(R.drawable.right_answer));
                    selected_correct = 1;
                    correct_answers++;
                } else {
                    v.setBackground(getResources().getDrawable(R.drawable.wrong_answer));
                    selected_correct = 0;
                    wrong_answers++;
                }
                is_option_selected = true;
                handle();
            }
        });

    }

    private void handle() {
        countDownTimer.cancel();
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            if (is_last_question) {
                successFinish();
            } else {
                QuizInterface quizInterface = new QuizInterface(questionList, question_index + 1, questionList.getQuestions().size());
                getActivity().getSupportFragmentManager().beginTransaction().replace(
                        R.id.fragmentContainer, quizInterface
                ).addToBackStack(null).commit();
            }
        }, 1000);
    }

    private void wrongOptionSelected(String title, String message) {
        if (!Constants.is_quiz_finished) {
            Constants.is_quiz_finished = true;
            countDownTimer.cancel();
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(title);
            builder.setMessage(message);
            builder.setCancelable(false);
            builder.setPositiveButton("OK", (dialog, which) -> {
                countDownTimer.cancel();
                countDownTimer.onFinish();
                finishActivity();
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }



    private void successFinish() {
        countDownTimer.cancel();
        Constants.is_quiz_finished = true;
        insertResultToRoom();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Woah!");
        builder.setMessage("Congratulations! You found " + String.valueOf(correct_answers)  + " questions right.");
        builder.setCancelable(false);
        builder.setPositiveButton("OK", (dialog, which) -> {
            if (totalPoint != null && user_id != null) {
                firebaseFirestore = FirebaseFirestore.getInstance();
                firebaseFirestore.collection("users")
                        .document(user_id)
                        .update("total_point", String.valueOf(correct_answers + Integer.parseInt(totalPoint)))
                        .addOnSuccessListener(task -> {
                            Log.d(Constants.TAG, "Success");
                            authPreferences.putString("total_point", String.valueOf(correct_answers + Integer.parseInt(totalPoint)));
                        })
                        .addOnFailureListener(task -> Log.d(Constants.TAG, task.getMessage()));
            }
            finishActivity();
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void finishActivity() {
        try {
            Constants.is_quiz_finished = true;
            countDownTimer.cancel();
            countDownTimer.onFinish();
            requireActivity().onBackPressed();
        } catch (NullPointerException e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void insertResultToRoom() {
        ViewModel viewModel = new ViewModelProvider(this).get(ViewModel.class);
        ResultEntity entity = new ResultEntity();
        entity.setQuiz_name(questionList.getPackageInfo().getName());
        entity.setRight_answers(String.valueOf(correct_answers));
        entity.setWrong_answers(String.valueOf(wrong_answers));
        entity.setTotal_point(String.valueOf(correct_answers));
        viewModel.insert(entity);
    }
}