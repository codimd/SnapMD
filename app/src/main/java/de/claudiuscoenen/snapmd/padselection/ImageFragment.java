package de.claudiuscoenen.snapmd.padselection;


import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.claudiuscoenen.snapmd.R;


public class ImageFragment extends Fragment {

	private static final String ARG_IMAGE_URI = "ARG_IMAGE_URI";

	@BindView(R.id.image)
	ImageView imageView;

	private Uri imageUri;


	public ImageFragment() {
		// Required empty public constructor
	}

	public static ImageFragment newInstance(Uri imageUri) {
		ImageFragment fragment = new ImageFragment();
		Bundle args = new Bundle();
		args.putParcelable(ARG_IMAGE_URI, imageUri);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			imageUri = getArguments().getParcelable(ARG_IMAGE_URI);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_image, container, false);
		ButterKnife.bind(this, view);

		if (imageUri != null) {
			Picasso.with(getContext()).load(imageUri).fit().into(imageView);
		}

		return view;
	}
}
