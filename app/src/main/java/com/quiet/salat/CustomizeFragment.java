package com.quiet.salat;

import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class CustomizeFragment extends DialogFragment {
    private TextView dur;
    private ImageButton addBtn,subBtn;
    private Button timeSubh,timeDuhr,timeAsr,timemagh,timeisha,save;
    static String DURATION;
    SaveListener listener;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);


        return inflater.inflate(R.layout.customizefragment, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dur=view.findViewById(R.id.duration);
        addBtn=view.findViewById(R.id.addBtn);
        subBtn=view.findViewById(R.id.subtractBtn);
        timeSubh=view.findViewById(R.id.custom_subah_time);
        timeDuhr=view.findViewById(R.id.custom_duhar_time);
        timeAsr=view.findViewById(R.id.custom_asr_time);
        timemagh=view.findViewById(R.id.custom_maghrib_time);
        timeisha=view.findViewById(R.id.custom_isha_time);
        save=view.findViewById(R.id.save_custom);
        dur.setText(getArguments().getString("dur","30"));
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dur.setText(String.valueOf(Integer.parseInt(dur.getText().toString()) + 5));
                DURATION = dur.getText().toString();
            }
        });
        subBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!dur.getText().toString().equals("0"))
                dur.setText(String.valueOf(Integer.parseInt(dur.getText().toString()) - 5));
                 DURATION = dur.getText().toString();
            }
        });
        timeSubh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog subh=new TimePickerDialog(getActivity(),R.style.TimePickerTheme, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if (minute < 10)
                            timeSubh.setText(hourOfDay + ":0" + minute);
                        else
                            timeSubh.setText(hourOfDay + ":" + minute);
                    }
                }, 0, 0, true);
                subh.show();
            }
        });
        timeDuhr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog subh=new TimePickerDialog(getActivity(), R.style.TimePickerTheme,new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if (minute < 10)
                        timeDuhr.setText(hourOfDay + ":0" + minute);
                        else
                            timeDuhr.setText(hourOfDay + ":" + minute);

                    }
                }, 0, 0, true);
                subh.show();
            }
        });
        timeAsr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog subh=new TimePickerDialog(getActivity(), R.style.TimePickerTheme,new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if (minute < 10)
                            timeAsr.setText(hourOfDay + ":0" + minute);
                        else
                            timeAsr.setText(hourOfDay + ":" + minute);

                    }
                }, 0, 0, true);
                subh.show();
            }
        });
        timemagh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog subh=new TimePickerDialog(getActivity(),R.style.TimePickerTheme, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if (minute < 10)
                            timemagh.setText(hourOfDay + ":0" + minute);
                        else
                            timemagh.setText(hourOfDay + ":" + minute);

                    }
                }, 0, 0, true);
                subh.show();
            }
        });
        timeisha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog subh=new TimePickerDialog(getActivity(),R.style.TimePickerTheme, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if (minute < 10)
                            timeisha.setText(hourOfDay + ":0" + minute);
                        else
                            timeisha.setText(hourOfDay + ":" + minute);

                    }
                }, 0, 0, true);
                subh.show();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SalatData.shared.salatList.size()!=0) {
                    if(!timeSubh.getText().toString().equals("00:00"))
                    SalatData.shared.salatList.get(0).time=timeSubh.getText().toString();
                    if(!timeDuhr.getText().toString().equals("00:00"))
                    SalatData.shared.salatList.get(1).time=timeDuhr.getText().toString();
                    if(!timeAsr.getText().toString().equals("00:00"))
                    SalatData.shared.salatList.get(2).time=timeAsr.getText().toString();
                    if(!timemagh.getText().toString().equals("00:00"))
                    SalatData.shared.salatList.get(3).time=timemagh.getText().toString();
                    if(!timeisha.getText().toString().equals("00:00"))
                    SalatData.shared.salatList.get(4).time=timeisha.getText().toString();
                }
                else
                {
                    Salat subh=new Salat(1,timeSubh.getText().toString(),false);
                    Salat duhr=new Salat(2,timeDuhr.getText().toString(),false);
                    Salat asr=new Salat(3,timeAsr.getText().toString(),false);
                    Salat maghrib =new Salat(4,timemagh.getText().toString(),false);
                    Salat isha=new Salat(5,timeisha.getText().toString(),false);
                    SalatData.shared.salatList.clear();
                    SalatData.shared.salatList.add(subh);
                    SalatData.shared.salatList.add(duhr);
                    SalatData.shared.salatList.add(asr);
                    SalatData.shared.salatList.add(maghrib);
                    SalatData.shared.salatList.add(isha);
                }

                listener.cusSave(DURATION);

            }
        });
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener=(SaveListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()+"must impelement SaveListener");
        }
    }

    interface SaveListener
    {
        void cusSave(String dur);
    }
}