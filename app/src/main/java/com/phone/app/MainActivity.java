package com.phone.app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.*;
import java.util.*;

public class MainActivity extends AppCompatActivity {
    private EditText phoneNumberInput;
    private RecyclerView historyRecyclerView;
    private CallHistoryAdapter adapter;
    private List<CallLogItem> callLogList;
    private static final int REQUEST_CALL_PERMISSION = 1;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        phoneNumberInput = findViewById(R.id.phoneNumberInput);
        historyRecyclerView = findViewById(R.id.historyRecyclerView);
        
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        loadCallHistory();
        
        setupDialPad();
        setupCallButton();
        
        checkPermissions();
    }
    
    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) 
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                new String[]{
                    Manifest.permission.CALL_PHONE,
                    Manifest.permission.READ_CALL_LOG,
                    Manifest.permission.READ_CONTACTS
                }, REQUEST_CALL_PERMISSION);
        }
    }
    
    private void setupDialPad() {
        int[] buttonIds = {
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9,
            R.id.btnStar, R.id.btnHash
        };
        
        View.OnClickListener dialListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button button = (Button) v;
                String currentText = phoneNumberInput.getText().toString();
                phoneNumberInput.setText(currentText + button.getText().toString());
                phoneNumberInput.setSelection(phoneNumberInput.getText().length());
            }
        };
        
        for (int id : buttonIds) {
            findViewById(id).setOnClickListener(dialListener);
        }
        
        findViewById(R.id.btnDelete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentText = phoneNumberInput.getText().toString();
                if (currentText.length() > 0) {
                    phoneNumberInput.setText(currentText.substring(0, currentText.length() - 1));
                    phoneNumberInput.setSelection(phoneNumberInput.getText().length());
                }
            }
        });
        
        findViewById(R.id.btnDelete).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                phoneNumberInput.setText("");
                return true;
            }
        });
    }
    
    private void setupCallButton() {
        findViewById(R.id.btnCall).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = phoneNumberInput.getText().toString().trim();
                if (!phoneNumber.isEmpty()) {
                    makeCall(phoneNumber);
                } else {
                    Toast.makeText(MainActivity.this, "Введите номер", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    
    private void makeCall(String phoneNumber) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) 
            == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(intent);
        } else {
            Toast.makeText(this, "Нет разрешения на звонки", Toast.LENGTH_SHORT).show();
            checkPermissions();
        }
    }
    
    private void loadCallHistory() {
        callLogList = new ArrayList<>();
        callLogList.add(new CallLogItem("+7 (999) 123-45-67", "Входящий", "10:30", 
            android.R.drawable.sym_call_incoming));
        callLogList.add(new CallLogItem("Мама", "Исходящий", "09:15", 
            android.R.drawable.sym_call_outgoing));
        callLogList.add(new CallLogItem("+7 (916) 111-22-33", "Пропущенный", "Вчера", 
            android.R.drawable.sym_call_missed));
        
        adapter = new CallHistoryAdapter(callLogList);
        historyRecyclerView.setAdapter(adapter);
    }
    
    private class CallHistoryAdapter extends RecyclerView.Adapter<CallHistoryAdapter.ViewHolder> {
        private List<CallLogItem> items;
        
        public CallHistoryAdapter(List<CallLogItem> items) {
            this.items = items;
        }
        
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.call_history_item, parent, false);
            return new ViewHolder(view);
        }
        
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            CallLogItem item = items.get(position);
            holder.nameText.setText(item.getName());
            holder.typeText.setText(item.getType());
            holder.timeText.setText(item.getTime());
            holder.icon.setImageResource(item.getIcon());
            
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    phoneNumberInput.setText(item.getName());
                    phoneNumberInput.setSelection(phoneNumberInput.getText().length());
                }
            });
        }
        
        @Override
        public int getItemCount() {
            return items.size();
        }
        
        class ViewHolder extends RecyclerView.ViewHolder {
            TextView nameText, typeText, timeText;
            ImageView icon;
            
            ViewHolder(View itemView) {
                super(itemView);
                nameText = itemView.findViewById(R.id.nameText);
                typeText = itemView.findViewById(R.id.typeText);
                timeText = itemView.findViewById(R.id.timeText);
                icon = itemView.findViewById(R.id.callIcon);
            }
        }
    }
    
    private class CallLogItem {
        private String name;
        private String type;
        private String time;
        private int icon;
        
        public CallLogItem(String name, String type, String time, int icon) {
            this.name = name;
            this.type = type;
            this.time = time;
            this.icon = icon;
        }
        
        public String getName() { return name; }
        public String getType() { return type; }
        public String getTime() { return time; }
        public int getIcon() { return icon; }
    }
          }
