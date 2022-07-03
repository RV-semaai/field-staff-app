package com.semaai.agent.activity.login;

import static com.yalantis.ucrop.UCrop.REQUEST_CROP;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.semaai.agent.R;
import com.semaai.agent.activity.CropActivity;
import com.semaai.agent.databinding.ActivityAccountDashboardBinding;
import com.semaai.agent.network.ApiEndPoints;
import com.semaai.agent.network.ApiStringModel;
import com.semaai.agent.utils.CameraUtils;
import com.semaai.agent.utils.Common;
import com.semaai.agent.utils.Constant;
import com.semaai.agent.utils.ToastMsg;
import com.semaai.agent.viewmodel.login.AccountDashboardViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AccountDashboardActivity extends AppCompatActivity {

    public String TAG = AccountDashboardActivity.class.getSimpleName() + "-->";
    ActivityAccountDashboardBinding binding;
    AccountDashboardViewModel viewModel;
    private Dialog progressDialog;
    ToastMsg toastMsg = new ToastMsg();
    public static final int MediaTypeImage = 1;
    public static final int GalleryImage = 2;
    private String imageStoragePath;
    private static final int CameraCaptureImageRequestCode = 100;
    Boolean callOnResume = true;

    private String userId;
    private String partnerId;
    private String acName;
    private String acEmail;
    private String langCode;
    private String stateId;
    private String districtId;
    private String subDistrictId;
    private String villageId;
    private String acStreet = "";
    private String acZip = "";
    private String acGender = "";
    private String acBDate = "";
    private String imgPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(AccountDashboardViewModel.class);
        binding = DataBindingUtil.setContentView(AccountDashboardActivity.this, R.layout.activity_account_dashboard);
        binding.setLifecycleOwner(this);
        binding.setAccountDashboardViewModel(viewModel);

        intview();
        onClick();
    }

    private void intview() {

        progressDialog = new Dialog(this);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.layout_progressbar);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCancelable(false);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (callOnResume) {
            apiCall();
        }
    }

    private void apiCall() {
        progressDialog.show();
        callOnResume = true;
        String currentString = Constant.cookie;
        String[] separated = currentString.split("=");
        AndroidNetworking.get(ApiEndPoints.profile + Constant.userId)
                .setTag("test")
                .addHeaders("X-Openerp-Session-Id", separated[1])
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        Log.e(TAG, "onResponse: >>> account " + response);
                        try {
                            if (response.getString(ApiStringModel.status).equals("200") || response.getString(ApiStringModel.message).equals("Success")) {
                                JSONArray jsonArray = response.getJSONArray(ApiStringModel.data);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject dataobject = jsonArray.getJSONObject(i);

                                    if (!dataobject.getString(ApiStringModel.image1920).equals("false")) {
                                        imgPath = dataobject.getString(ApiStringModel.image1920);
                                        Constant.profileImage = imgPath;
                                        byte[] decodedString = Base64.decode(dataobject.getString(ApiStringModel.image1920), Base64.DEFAULT);
                                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                        Glide.with(AccountDashboardActivity.this).load(decodedByte).dontTransform().circleCrop().into(binding.ivAccountProfile);
                                    }

                                    if (!dataobject.getString(ApiStringModel.name).equals("false")) {
                                        acName = dataobject.getString(ApiStringModel.name);
                                        binding.tvProfilename.setText(dataobject.getString(ApiStringModel.name));
                                    }
                                    if (!dataobject.getString(ApiStringModel.acPhoneNumber).equals("false")) {
                                        binding.tvProfilePhoneNumber.setText(dataobject.getString(ApiStringModel.acPhoneNumber));
                                    }

                                    if (!dataobject.getString(ApiStringModel.street).equals("false")) {
                                        acStreet = dataobject.getString(ApiStringModel.street);
                                        binding.tvProfileAddressReal.setText(dataobject.getString(ApiStringModel.street));
                                    }


                                    if (!dataobject.getString(ApiStringModel.email).equals("false")) {
                                        acEmail = dataobject.getString(ApiStringModel.email);
                                        binding.tvProfileEmailReal.setText(dataobject.getString(ApiStringModel.email));
                                    }

                                    if (!dataobject.getString(ApiStringModel.function).equals("false")) {
                                        Constant.jobTitle = dataobject.getString(ApiStringModel.function);
                                        binding.tvJobTitle.setText(Constant.jobTitle);

                                    }

                                    if (!dataobject.getString(ApiStringModel.gender).equals("false")) {
                                        String upperString = dataobject.getString(ApiStringModel.gender).substring(0, 1).toUpperCase() +
                                                dataobject.getString(ApiStringModel.gender).substring(1).toLowerCase();
                                        acGender = dataobject.getString(ApiStringModel.gender);
                                        if (Constant.setLanguage.equals("in")) {
                                            if (upperString.equals("Male")) {
                                                binding.tvProfileGenderName.setText(R.string.male);
                                            } else if (upperString.equals("Female")) {
                                                binding.tvProfileGenderName.setText(R.string.female);
                                            } else if (upperString.equals("Other")) {
                                                binding.tvProfileGenderName.setText(R.string.acOther);
                                            } else {
                                                binding.tvProfileGenderName.setText(R.string.acGender);
                                            }
                                        } else {
                                            binding.tvProfileGenderName.setText(upperString);
                                        }
                                    } else {
                                        binding.tvProfileGenderName.setText("--");
                                        acGender = "";
                                    }


                                    String checkdate = dataobject.getString(ApiStringModel.birthday).toLowerCase(Locale.ROOT);
                                    if (!checkdate.contains("false")) {
                                        String deliveryDate = dataobject.getString(ApiStringModel.birthday);
                                        Log.e(TAG, "onResponse: >>>> check date 22 " + deliveryDate);
                                        SimpleDateFormat dateFormatprev = new SimpleDateFormat("yyyy-MM-dd");
                                        Date d = null;
                                        try {
                                            d = dateFormatprev.parse(deliveryDate);
                                        } catch (ParseException e) {
                                            Log.e(TAG, "onResponse: catch >> " + e.getMessage());
                                            e.printStackTrace();
                                        }
                                        SimpleDateFormat dateFormat;
                                        if (Constant.setLanguage.equals("in")) {
                                             dateFormat = new SimpleDateFormat("dd MMMM yyyy", new Locale("in"));
                                        }else {
                                            dateFormat = new SimpleDateFormat("dd MMMM yyyy");
                                        }
                                        Log.e("TAG", "onBindViewHolder: check date " + d);
                                        String changedDate = dateFormat.format(d);
                                        Log.e(TAG, "onResponse: >>>> check date " + changedDate);
                                        acBDate = dataobject.getString(ApiStringModel.birthday);
                                        binding.tvProfileBirth.setText(changedDate);
                                    } else {
                                        binding.tvProfileBirth.setText("--");
                                        acBDate = "";
                                    }

                                    try {
                                        JSONArray teamobject = dataobject.getJSONArray(ApiStringModel.teamId);
                                        if (!teamobject.equals("false")) {
                                            binding.tvTeamPati.setText(getString(R.string.acTeam) + ": " + teamobject.getString(1));
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    try {
                                        JSONArray partnerobject = dataobject.getJSONArray(ApiStringModel.partnerId);
                                        if (!partnerobject.equals("false")) {
                                            partnerId = partnerobject.getString(0);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    langCode = dataobject.getString(ApiStringModel.langCode);
                                    userId = dataobject.getString(ApiStringModel.userId);
                                    acZip = dataobject.getString(ApiStringModel.zip);

                                    //state
                                    JSONArray stateobject = dataobject.getJSONArray(ApiStringModel.stateId);
                                    if (!stateobject.equals("false")) {
                                        stateId = stateobject.getString(0);
                                    }
                                    //districts
                                    JSONArray districtsobject = dataobject.getJSONArray(ApiStringModel.districtId);
                                    if (!districtsobject.equals("false")) {
                                        districtId = districtsobject.getString(0);
                                    }
                                    //sub_districts
                                    JSONArray sub_disobject = dataobject.getJSONArray(ApiStringModel.subDistrictId);
                                    if (!sub_disobject.equals("false")) {
                                        subDistrictId = sub_disobject.getString(0);
                                    }
                                    //village
                                    JSONArray villageobject = dataobject.getJSONArray(ApiStringModel.villageId);
                                    if (!villageobject.equals("false")) {
                                        villageId = villageobject.getString(0);
                                    }

                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            toastMsg.showToast(AccountDashboardActivity.this, getString(R.string.errorMessage));
                        }
                        Log.e(TAG, "onResponse: >>>>> " + response);
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                        Log.e(TAG, "onError: >>>>> " + error.getMessage());
                        progressDialog.dismiss();
                        toastMsg.showToast(AccountDashboardActivity.this, getString(R.string.errorMessage));
                    }
                });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void onClick() {

        binding.ivProfileEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callOnResume = true;
                startActivity(new Intent(AccountDashboardActivity.this, AccountActivity.class));
            }
        });

        binding.cvLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gone();
            }
        });

        binding.clTopview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gone();
            }
        });

        binding.clHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gone();
            }
        });
        binding.ivBackarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        binding.svNested.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gone();
                return false;
            }
        });
        binding.menu.clHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        binding.cvLogoutDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.cvLogoutDialog.setVisibility(View.VISIBLE);
            }
        });
        binding.tvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.cnstGalleryCamera.getVisibility() == View.GONE) {
                    binding.cvLogoutDialog.setVisibility(View.VISIBLE);
                } else {
                    gone();
                }
            }
        });
        binding.logoutDialog.cvLogoutYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = getSharedPreferences("Login", MODE_PRIVATE).edit();
                editor.putString("password", "");
                editor.apply();
                Intent intent = new Intent(AccountDashboardActivity.this, SplashScreenActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        binding.logoutDialog.cvLogoutNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gone();
            }
        });

        binding.ivFloatingCameraBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.cvLogoutDialog.getVisibility() == View.GONE) {
                    binding.cnstGalleryCamera.setVisibility(View.VISIBLE);
                } else {
                    gone();
                }
            }
        });

        binding.galleryCameraDialog.tvGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callOnResume = false;
                Intent intent1 = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityIfNeeded(intent1, GalleryImage);

                gone();
            }
        });

        binding.galleryCameraDialog.tvCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callOnResume = false;
                captureImage();
                gone();
            }
        });

    }

    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = CameraUtils.getOutputMediaFile(MediaTypeImage);
        if (file != null) {
            imageStoragePath = file.getAbsolutePath();
        }
        Uri fileUri = CameraUtils.getOutputMediaFileUri(getApplicationContext(), file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityIfNeeded(intent, CameraCaptureImageRequestCode);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == GalleryImage) {
            try {

                Constant.whichCameraImage = 0;
                Constant.cameraImage1 = data.getData();
                Constant.cameraImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                Common.SetTempBitmap(AccountDashboardActivity.this, Constant.cameraImage);
                Intent intent = new Intent(AccountDashboardActivity.this, CropActivity.class);
                startActivityIfNeeded(intent, REQUEST_CROP);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }else{

            binding.cnstGalleryCamera.setVisibility(View.VISIBLE);

        }


        if (requestCode == CameraCaptureImageRequestCode) {
            if (resultCode == RESULT_OK) {
                try {
                    ExifInterface ei = new ExifInterface(imageStoragePath);
                    int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_UNDEFINED);

                    Common.rotation = orientation;

                    Constant.whichCameraImage = 0;
                    Constant.cameraImage1 = Uri.fromFile(new File(imageStoragePath));
                    Constant.cameraImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.fromFile(new File(imageStoragePath)));
                    Common.SetTempBitmap(AccountDashboardActivity.this, Constant.cameraImage);
                    Intent intent = new Intent(AccountDashboardActivity.this, CropActivity.class);
                    startActivityIfNeeded(intent, REQUEST_CROP);

                } catch (IOException e) {
                    e.printStackTrace();
                }

                Log.e("TAG", "onActivityResult:-------------- " + imageStoragePath);

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), getString(R.string.userCancelledImageCapture), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.sorryFailedToCaptureImage), Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == REQUEST_CROP) {
            if (resultCode == Activity.RESULT_OK) {

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Bitmap bitmap = BitmapFactory.decodeFile(Common.GetTempPath(AccountDashboardActivity.this));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 40, baos);
                byte[] imageBytes = baos.toByteArray();
                imgPath = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                updateData();
                gone();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), getString(R.string.userCancelledImageCapture), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateData() {
        progressDialog.show();
        String currentString = Constant.cookie;
        String[] separated = currentString.split("=");

        JSONObject object = new JSONObject();
        JSONObject params = new JSONObject();
        JSONObject data = new JSONObject();
        try {
//            data.put(ApiStringModel.user_id, Constant.user_id);
            data.put(ApiStringModel.partnerId, partnerId);
            data.put(ApiStringModel.name, acName);
            data.put(ApiStringModel.langCode, langCode);
            data.put(ApiStringModel.stateId, stateId);
            data.put(ApiStringModel.districtId, districtId);
            data.put(ApiStringModel.subDistrictId, subDistrictId);
            data.put(ApiStringModel.villageId, villageId);
            data.put(ApiStringModel.street, acStreet);
            data.put(ApiStringModel.street2, "");
            data.put(ApiStringModel.zip, acZip);
            data.put(ApiStringModel.email, acEmail);
            data.put(ApiStringModel.gender, acGender);
            data.put(ApiStringModel.birthday, acBDate);
            data.put(ApiStringModel.image1920, imgPath);
            params.put(ApiStringModel.data, data);
            object.put(ApiStringModel.params, params);
            Log.e(TAG, "snedDataApiCall: >>>> " + new Gson().toJson(object));
            AndroidNetworking.put(ApiEndPoints.profile)
                    .addJSONObjectBody(object) // posting json
                    .addHeaders("X-Openerp-Session-Id", separated[1])
                    .setTag("test")
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.e(TAG, "onResponse: >>>>>>>>>>>>>>>" + response);
                            try {
                                JSONObject result = response.getJSONObject(ApiStringModel.result);
                                if (result.getInt(ApiStringModel.status) == 200 && result.getString(ApiStringModel.message).equals("Success")) {
                                    toastMsg.showToast(AccountDashboardActivity.this, getString(R.string.updateSuccess));
                                    Constant.profileImage = imgPath;
                                } else {
                                    ToastMsg.showToast(AccountDashboardActivity.this, getString(R.string.fillDetails));
                                }
                                progressDialog.dismiss();
                                apiCall();
                            } catch (Exception e) {
                                toastMsg.showToast(AccountDashboardActivity.this, getString(R.string.errorMessage));
                                progressDialog.dismiss();
                                callOnResume = true;
                            }
                            // do anything with response
                        }

                        @Override
                        public void onError(ANError error) {
                            // handle error
                            toastMsg.showToast(AccountDashboardActivity.this, getString(R.string.errorMessage));
                            Log.e(TAG, "error: >>>>>>>>>>>>>>>" + error);
                            progressDialog.dismiss();
                            callOnResume = true;
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void gone() {
        binding.cnstGalleryCamera.setVisibility(View.GONE);
        binding.cvLogoutDialog.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        if (binding.cnstGalleryCamera.getVisibility() == View.GONE && binding.cvLogoutDialog.getVisibility() == View.GONE) {
            super.onBackPressed();
        } else {
            gone();
        }
    }
}