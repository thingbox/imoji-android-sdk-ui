package io.imoji.sdk.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;

import java.io.IOException;

import io.imoji.sdk.ui.utils.EditorBitmapCache;

public class ImojiEditorActivity extends AppCompatActivity {

    public static final int START_EDITOR_REQUEST_CODE = 1001;
    private final static int PICK_IMAGE_REQUEST_CODE = 1002;
    public static final String IMOJI_MODEL_BUNDLE_ARG_KEY = "IMOJI_MODEL_BUNDLE_ARG_KEY";
    public static final String CREATE_TOKEN_BUNDLE_ARG_KEY = "CREATE_TOKEN_BUNDLE_ARG_KEY";
    public static final String TAG_IMOJI_BUNDLE_ARG_KEY = "TAG_IMOJI_BUNDLE_ARG_KEY";
    public static final String RETURN_IMMEDIATELY_BUNDLE_ARG_KEY = "RETURN_IMMEDIATELY_BUNDLE_ARG_KEY";

    private ImojiEditorFragment mImojiEditorFragment;
    private boolean tagImojis = true;
    private boolean returnImmediately = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imoji_editor);

        if (savedInstanceState == null) {
            tagImojis = getIntent().getBooleanExtra(TAG_IMOJI_BUNDLE_ARG_KEY, true);
            returnImmediately = getIntent().getBooleanExtra(RETURN_IMMEDIATELY_BUNDLE_ARG_KEY, false);

            Bitmap inputBitmap = EditorBitmapCache.getInstance().get(EditorBitmapCache.Keys.INPUT_BITMAP);
            if (inputBitmap == null) {
                pickImageFromGallery();
            } else {
                createFragment(inputBitmap);
            }
        } else {
            mImojiEditorFragment = (ImojiEditorFragment) getSupportFragmentManager().findFragmentByTag(ImojiEditorFragment.FRAGMENT_TAG);
        }
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.imoji_editor_activity_image_picker_title)), PICK_IMAGE_REQUEST_CODE);
    }

    private void createFragment(Bitmap inputBitmap) {
        mImojiEditorFragment = ImojiEditorFragment.newInstance(tagImojis, returnImmediately);
        mImojiEditorFragment.setEditorBitmap(inputBitmap);
        getSupportFragmentManager().beginTransaction().add(R.id.container, mImojiEditorFragment, ImojiEditorFragment.FRAGMENT_TAG).commit();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                createFragment(bitmap);
            } catch (IOException e) {
                finishActivity();
            }
        } else {
            finishActivity();
        }
    }

    private void finishActivity() {
        setResult(Activity.RESULT_CANCELED, null);
        finish();
    }
}
