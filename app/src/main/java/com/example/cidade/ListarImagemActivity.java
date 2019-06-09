package com.example.cidade;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ListarImagemActivity extends AppCompatActivity {

    private ListView listImg;
    private CollectionReference collectionReference;
    private FirebaseFirestore db;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_imagem);

        listImg = findViewById(R.id.listImg);
        db = FirebaseFirestore.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    protected void onStart(){
        super.onStart();
        carregaImagens();
    }

    private void carregaImagens() {
        CollectionReference colecao = db.collection("users").document(mUser.getEmail()).collection("link");
        colecao.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<Uri> urls = new ArrayList<>();
                for(DocumentSnapshot doc : queryDocumentSnapshots){
                    String url = doc.get("link").toString();
                    urls.add(Uri.parse(url));
                }
                ArrayAdapter<Uri> adapter = new ImagemAdapter(ListarImagemActivity.this,R.layout.image_item,urls);

                listImg.setAdapter(adapter);
            }
        });
    }
}
