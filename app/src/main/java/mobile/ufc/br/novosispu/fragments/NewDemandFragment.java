package mobile.ufc.br.novosispu.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.util.Date;

import mobile.ufc.br.novosispu.MainActivity;
import mobile.ufc.br.novosispu.R;
import mobile.ufc.br.novosispu.entities.Demand;
import mobile.ufc.br.novosispu.service.DemandService;
import mobile.ufc.br.novosispu.service.UserService;

import static mobile.ufc.br.novosispu.Constants.FIREBASE_CHILD_DEMANDS;
import static mobile.ufc.br.novosispu.Constants.FRAGMENT_HOME_ID;

public class NewDemandFragment extends Fragment {

    private static final int REQUEST_IMAGE_CAPTURE = 111;

    private Button newDemandButton;
    private Button capturePhotoButton;
    private EditText descriptionNewDemandEditText;
    private EditText titleNewDemandEditText;
    private DemandService demandService;
    private UserService userService;

    private ImageView mImageLabel;

    private Bitmap imageBitmap;

    public NewDemandFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static NewDemandFragment newInstance() {
        NewDemandFragment fragment = new NewDemandFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        demandService = new DemandService();
        userService = new UserService();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_demand, container, false);
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        newDemandButton = (Button) getView().findViewById(R.id.newDemandButton);
        capturePhotoButton = (Button) getView().findViewById(R.id.capturePhotoButton);
        titleNewDemandEditText = (EditText) getView().findViewById(R.id.titleNewDemandEditText);
        descriptionNewDemandEditText = (EditText) getView().findViewById(R.id.descriptionNewDemandEditText);
        mImageLabel = (ImageView) getView().findViewById(R.id.imageFromCamera);

        newDemandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Demand demand = new Demand();
                demand.setTitle(titleNewDemandEditText.getText().toString());
                demand.setDescription(descriptionNewDemandEditText.getText().toString());
                demand.setTime(new Date().getTime());
                demand.setUserKey(userService.getCurrentUserKey());

                // Image
                if(imageBitmap != null) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    String imageEncoded = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);

                    demand.setImageUrl(imageEncoded);
                }

                demandService.save(demand);

                ((MainActivity)getActivity()).changeFragmentTo(FRAGMENT_HOME_ID);
            }
        });

        capturePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLaunchCamera();
            }
        });
    }

    public void onLaunchCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == getActivity().RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            mImageLabel.setImageBitmap(imageBitmap);
            mImageLabel.getLayoutParams().height = 500;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
