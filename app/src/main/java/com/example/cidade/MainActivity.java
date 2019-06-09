package com.example.cidade;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_CHOOSE = 1234;
    private ImageView imgSelector;
    private List<Uri> mSelected;
    private StorageReference mStorage;
    private FirebaseFirestore db;
    private FirebaseUser mUser;
    private StorageReference userReference;
    private EditText edtCidade;
    private EditText edtLocal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgSelector = findViewById(R.id.imgSelector);
        edtCidade = findViewById(R.id.edtCidade);
        edtLocal = findViewById(R.id.edtLocal);
        mStorage = FirebaseStorage.getInstance().getReference();
        db = FirebaseFirestore.getInstance();
        Picasso.get().load(R.drawable.placeholder).into(imgSelector);
    }

    @Override
    protected void onStart(){
        super.onStart();
    }

    public void salvar(View view){
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        userReference = mStorage.child("local/"+ mUser.getEmail()+"/"+ Util.getTimeStamp()+".png");
        userReference.putFile(mSelected.get(0))
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        userReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                CollectionReference collectionReference = db.collection("users").document(mUser.getEmail())
                                        .collection("link");
                                Map<String,Object> link = new HashMap<>();
                                link.put("cidade", edtCidade.getText().toString());
                                link.put("local", edtLocal.getText().toString());
                                link.put("link", uri.toString());
                                collectionReference.add(link);
                                Toast.makeText(MainActivity.this, "Cadastrado", Toast.LENGTH_SHORT).show();
                                Picasso.get().load(R.drawable.placeholder).into(imgSelector);
                            }
                        });
                    }
                });
    }

    public void selectImage(View view){
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions
                .request(Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if(aBoolean) {
                            Matisse.from(MainActivity.this)
                                    .choose(MimeType.ofImage())
                                    .countable(false)
                                    .thumbnailScale(0.9f)
                                    .imageEngine(new GlideV4Engine())
                                    .forResult(REQUEST_CODE_CHOOSE);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK){
            mSelected = Matisse.obtainResult(data);
            Log.d("Matisse", "mSelected: " + mSelected);

            Picasso.get().load(mSelected.get(0)).into(imgSelector);
        }
    }

    public void openList(View view){
        Intent intent = new Intent(MainActivity.this, ListarImagemActivity.class);
        startActivity(intent);
    }
}
