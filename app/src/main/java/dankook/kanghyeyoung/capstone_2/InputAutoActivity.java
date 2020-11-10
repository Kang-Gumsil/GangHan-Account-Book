package dankook.kanghyeyoung.capstone_2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static dankook.kanghyeyoung.capstone_2.AccountBookDB.insert;
import static dankook.kanghyeyoung.capstone_2._FORMAT.DATE_DB_FORMAT;
import static dankook.kanghyeyoung.capstone_2._FORMAT.DATE_TIME_FORMAT;
import static dankook.kanghyeyoung.capstone_2._FORMAT.DECIMAL_FORMAT;

public class InputAutoActivity extends AppCompatActivity {
    String TAG = "InputAutoActivity";

    int receiptType;
    private static final int EMART_RECEIPT = 0;
    private static final int EMARTMALL_RECEIPT = 1;

    private static final int REQUEST_IMAGE_CAPTURE = 103;
    private static final int REQUEST_IMAGE_GET_EMART = 104;
    private static final int REQUEST_IMAGE_GET_EMARTMALL = 105;

    private String mImageFilePath;
    private Uri mPhotoUri;
    FirebaseFirestore db;
    SpecDetailAdapter mAdapter;
    PopupMenu mPopupMenu;
    Python mPython; // 파이썬 사용 관련 변수
    PyObject mObjectEmart; // 파이썬 사용 관련 변수
    PyObject mObjectEmartmall; // 파이썬 사용 관련 변수

    ImageButton mImageButtonClose;
    ImageView mImageViewReceipt;
    Button mButtonAddImage;
    Button mButtonRegister;
    EditText mInputCat;
    EditText mInputPrice;
    EditText mInputPlace;
    TextView mTextViewDate;
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_auto);

        /* view 참조 */
        mImageButtonClose = findViewById(R.id.imageButton_close);
        mImageViewReceipt = findViewById(R.id.imageView_receipt);
        mButtonAddImage = findViewById(R.id.button_addImage);
        mButtonRegister = findViewById(R.id.button_register);
        mInputCat = findViewById(R.id.textView_cat);
        mInputPrice = findViewById(R.id.textView_price);
        mInputPlace = findViewById(R.id.textView_place);
        mTextViewDate = findViewById(R.id.textView_date);
        mRecyclerView = findViewById(R.id.recyclerView);

        /* help button */
        findViewById(R.id.imageButton_help).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HelpActivity.class);
                startActivity(intent);
            }
        });

        /* recyclerView 설정 및 어댑터 설정 */
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new SpecDetailAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        /* recyclerView 아이템간 구분선 추가 */
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(mRecyclerView.getContext(),
                        new LinearLayoutManager(getApplicationContext()).getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        /* 날짜 설정 */
        final Calendar cal = Calendar.getInstance();
        DateTimePickerDialog.setDate(cal, mTextViewDate);

        /* mInputPrice에 입력할 때 1000단위 컴마 찍도록 리스너 설정 */
        mInputPrice.addTextChangedListener(textWatcher);

        // 날짜 선택 다이얼로그를 호출하는 클릭 이벤트 정의
        mTextViewDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DateTimePickerDialog dateTimePickerDialog = new DateTimePickerDialog(InputAutoActivity.this);

                // 커스텀 다이얼로그 호출 - 이때 결과를 출력할 TextView를 매개변수로 같이 넘겨줌
                dateTimePickerDialog.callFunction(mTextViewDate, cal);
                try {
                    cal.setTime(DATE_TIME_FORMAT.parse(mTextViewDate.getText().toString()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        /* firestore 인스턴스 초기화 */
        db = FirebaseFirestore.getInstance();

        /* 파이썬 실행하기 위한 코드 */
        if (!Python.isStarted())
            Python.start(new AndroidPlatform(this));

        mPython = Python.getInstance();
        mObjectEmart = mPython.getModule("GetOcrResult");
        mObjectEmartmall = mPython.getModule("GetOcrResult2");

        /* 닫기 버튼 이벤트 리스너 설정 */
        mImageButtonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        /* 사진 추가하기 버튼 리스너 설정 */
        mButtonAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "사진 추가 버튼 클릭됨");
                mPopupMenu = new PopupMenu(getApplicationContext(), v);
                mPopupMenu.getMenuInflater().inflate(R.menu.image_menu, mPopupMenu.getMenu());
                mPopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            // 사진 촬영
                            case R.id.item_camera:
                                Log.d(TAG, "item_camera selected");
                                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                                    File photoFile = null;
                                    try {
                                        photoFile = createImageFile();
                                    } catch (IOException ex) {
                                        // Error occurred while creating the File
                                    }
                                    if (photoFile != null) {
                                        mPhotoUri = FileProvider.getUriForFile(getApplicationContext(),
                                                getPackageName(), photoFile);

                                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoUri);
                                        Log.d(TAG, "item_camera: startActivityForResult 호출 직전");
                                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                                    }
                                }
                                break;

                            // 이미지 가져오기 - 이마트 영수증
                            case R.id.item_emart:
                                Intent intent = new Intent();
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                Log.d(TAG, "item_emart: startActivityForResult 호출 직전");
                                startActivityForResult(intent, REQUEST_IMAGE_GET_EMART);
                                break;
                            // 이미지 가져오기 - 이마트몰 영수증
                            case R.id.item_emartmall:
                                intent = new Intent();
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                Log.d(TAG, "item_emartmall: startActivityForResult 호출 직전");
                                startActivityForResult(intent, REQUEST_IMAGE_GET_EMARTMALL);
                                break;
                        }
                        return false;
                    }
                });
                mPopupMenu.show();
            }
        });

        mButtonRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int type = 1;
                int catMain = 0;
                int catSub = -1;
                Log.d(TAG, "mAdapter item count:" + mAdapter.getItemCount());
                if (mAdapter.getItemCount() > 1) {
                    catMain = 15;
                    catSub = -1;
                }
                String place = mInputPlace.getText().toString();
                int price = Integer.parseInt(mInputPrice.getText().toString().replaceAll("\\,", ""));
                Date date = new Date();
                try {
                    date = DATE_TIME_FORMAT.parse(mTextViewDate.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Spec spec = new Spec(type, price, place, catMain, catSub, date);
                Log.d(TAG, "spec::type:" + type + ", price:" + price + ", place:" + place + ", catMain:" + catMain + ", catSub:" + catSub);

                for (int i = 0; i < mAdapter.getItemCount(); i++) {
                    SpecDetail specDetailItem = mAdapter.getItem(i);
                    if (specDetailItem.getCatMain() == -1)
                        specDetailItem.setCatMain(14);
                    spec.addSpecDetail(specDetailItem);
                }

                int insertKey = insert(spec);
                Log.d(TAG, "insert 결과:" + insertKey);

                setResult(RESULT_OK);
                finish();
            }
        });
    }

    /* 이미지에 대해 OCR 처리 */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 이마트 영수증 이미지 가져오는 경우
        if (requestCode == REQUEST_IMAGE_GET_EMART && resultCode == RESULT_OK) {
            try {
                receiptType = EMART_RECEIPT;
                startCrop(data.getData());
            } catch (Exception e) {
                Log.d(TAG, e.toString());
            }

            // 이마트몰 영수증 이미지 가져오는 경우
        } else if (requestCode == REQUEST_IMAGE_GET_EMARTMALL && resultCode == RESULT_OK) {
            try {
                receiptType = EMARTMALL_RECEIPT;
                startCrop(data.getData());
            } catch (Exception e) {
                e.printStackTrace();
            }

            // 이마트 영수증 촬영하고 크롭 전인 경우
        } else if (resultCode == RESULT_OK && requestCode == REQUEST_IMAGE_CAPTURE) {
            Log.d("InputAutoActivity", "onActivityResult에서 startCrop()이전");
            receiptType = EMART_RECEIPT;
            startCrop(mPhotoUri);

            // 이마트 영수증 촬영하고 크롭까지 마친 경우
        } else if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            Log.d("InputAutoActivity", "onActivityResult에서 startCrop()이후");
            Uri imageUriResultCrop = UCrop.getOutput(data);

            if (imageUriResultCrop != null) {
                mImageViewReceipt.setImageURI(null);
                mImageViewReceipt.setImageURI(imageUriResultCrop);
                mImageFilePath = imageUriResultCrop.getPath();

                // 리싸이클러뷰 어댑터 아이템 초기화
                mAdapter.clear();
                mAdapter.notifyDataSetChanged();

                if (receiptType == EMART_RECEIPT) {
                    getReceiptOCR(mObjectEmart);
                } else {
                    getReceiptOCR(mObjectEmartmall);
                }
            }
        }
    }

    /* Crop */
    private void startCrop(@NonNull Uri uri) {
        String destinationFileName = "temp.jpg";

        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileName)));
        uCrop.withOptions(getCropOptions());
        uCrop.start(this);
    }

    private UCrop.Options getCropOptions() {
        UCrop.Options options = new UCrop.Options();
        options.setCompressionQuality(100);
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        options.setHideBottomControls(false);
        options.setFreeStyleCropEnabled(true);
        options.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        options.setToolbarColor(getResources().getColor(R.color.colorPrimary));

        return options;
    }

    /* jpg 파일을 만들어서 File return */
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "TEST_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,      /* prefix */
                ".jpg",         /* suffix */
                storageDir          /* directory */
        );
        Log.d(TAG, "createImageFile() 리턴 전");
        return image;
    }

    public void getReceiptOCR(PyObject module) {
        Log.d(TAG, "getReciptOCR 진입");
        try {
            PyObject obj = module.callAttr("getOCRResult", "temp_image", mImageFilePath, "jpg");
            String result = obj.toString();
            Log.d(TAG, "OCR 결과:" + result);

            // 장소, 날짜, 제품과 가격, 총합계금액 추출
            PyObject obj2 = module.callAttr("extractPlace", obj);
            PyObject obj3 = module.callAttr("extractDate", obj);
            PyObject obj4 = module.callAttr("extractProduct", obj);
            PyObject obj5 = module.callAttr("extractTotalPrice", obj);


            String totalPriceTemp = obj5.toString(); // 총 합계 금액 설정
            totalPriceTemp = totalPriceTemp.replaceFirst("[,.]", ""); // ','나 '.' 제거
            int totalPrice = Integer.parseInt(totalPriceTemp);
            mInputPrice.setText(DECIMAL_FORMAT.format(totalPrice)); // textView에는 콤마 찍어서 텍스트 설정

            // 거래처 설정
            final String place = obj2.toString().trim();
            mInputPlace.setText(place);

            // 날짜 설정
            String date = obj3.toString().trim();
            Calendar cal = Calendar.getInstance();
            try {
                cal.setTime(DATE_DB_FORMAT.parse(date));
            } catch (ParseException e) {
                Log.d(TAG, "date parseException : " + e.toString());


            }
            mTextViewDate.setText(DATE_TIME_FORMAT.format(new Date(cal.getTimeInMillis())));

            // 품목명과 가격 분리
            List<PyObject> productPriceList = obj4.asList();
            PyObject obj6 = productPriceList.get(0);
            PyObject obj7 = productPriceList.get(1);

            String products = obj6.toString();  // products => '['품목1', '품목2']' 형태
            String prices = obj7.toString();  // prices => '['2300', '3000']' 형태
            products = products.replaceFirst("^\\[", "")
                    .replaceFirst("\\]$", "");  // products의 양 끝 '[', ']' 제거 & ''제거
            products = products.replaceAll("'", "");  // prices의 양 끝 '[', ']' 제거 & ''제거
            prices = prices.replaceFirst("^\\[", "")
                    .replaceFirst("\\]$", "");
            prices = prices.replaceAll("[' ]", "");
            String[] productArr = products.split(",");  // productArr => [품목1, 품목2] 형태
            String[] priceArr = prices.split(",");  // priceArr => [2300, 3000] 형태

            // 품목의 개수와 해당 품목들의 가격 수가 같은 경우에 대해서만 품목 나타냄
            // 같지 않은 경우에는 품목과 가격을 매칭할 수 없음
            Log.d(TAG, "제품 개수 : " + productArr.length);
            if (productArr.length == priceArr.length) {
                for (int i = 0; i < productArr.length; i++) {
                    String rawName = productArr[i].trim();
                    String documentName = processProductName(rawName);
                    addToAdapter(rawName, documentName, Integer.parseInt(priceArr[i]));
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "error from getReceiptOCR");
            String message = "인식에 실패했습니다. 다시 한번 시도해 주세요.";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }

    }

    /* 제품명으로 카테고리 찾고, 세부내역에 넣기 */
    private void addToAdapter(final String rawName, final String documentName, final int price) {
        DocumentReference docRef = db.collection("emartmall").document(documentName);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        Map<String, Object> data = document.getData();
                        int temp_main_cat = (int) Math.round((Double) data.get("main_cat"));
                        int temp_sub_cat = (int) Math.round((Double) data.get("sub_cat"));
                        mAdapter.addItem(new SpecDetail(temp_main_cat, temp_sub_cat, rawName, price));
                        mAdapter.notifyDataSetChanged();

                    } else {
                        Log.d(TAG, "No such document");
                        mAdapter.addItem(new SpecDetail(-1, -1, rawName, price));
                        mAdapter.notifyDataSetChanged();
                    }

                } else {
                    // error handler
                }
            }
        });
    }

    private String processProductName(String beforeName) {
        String afterName = beforeName.replaceFirst("\\*", "");
        afterName = afterName.trim();
        afterName = afterName.replaceFirst("^[\\*★].*", ""); // 맨 앞에 ★나 *가 있으면 제거
        afterName = afterName.replaceAll("^[★\\(].*[\\)★]", ""); // '(' 나 '★'로 시작하면 ')' 나 '★'까지 제거
        afterName = afterName.replaceAll(" ", ""); // 공백 제거
        afterName = afterName.replaceAll("/", "#"); // '/'를 '#'로 변경
        afterName = afterName.replaceAll("^(SSG.Fresh|청정원|자주\\[JAJU\\])", ""); // SSG.Fresh, JAJU 제거

        if (afterName.length() > 11) { // 11글자로 자르기
            afterName = afterName.substring(0, 11);
        }

        return afterName;
    }

    /* mInputPrice 1000단위 컴마 찍기 */
    private TextWatcher textWatcher = new TextWatcher() {
        String priceResult;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!TextUtils.isEmpty(s.toString()) && !s.toString().equals(priceResult)) {
                priceResult = DECIMAL_FORMAT.format(Double.parseDouble(s.toString().replaceAll(",", "")));
                mInputPrice.setText(priceResult);
                mInputPrice.setSelection(priceResult.length());
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };
}
