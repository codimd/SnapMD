package de.claudiuscoenen.hackmdsnapshot.padselection;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.claudiuscoenen.hackmdsnapshot.R;
import de.claudiuscoenen.hackmdsnapshot.model.Pad;


class PadListAdapter extends RecyclerView.Adapter<PadListAdapter.ViewHolder> {

	private List<Pad> pads = new ArrayList<>();
	private Listener listener;

	PadListAdapter(Listener listener) {
		this.listener = listener;
		setHasStableIds(true);
	}

	void setPads(List<Pad> pads) {
		this.pads = pads;
		notifyDataSetChanged();
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_pad_selection_list_item, null);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		holder.setPad(pads.get(position));
		holder.itemView.setOnClickListener(view -> {
			Pad selectedPad = pads.get(holder.getAdapterPosition());
			listener.onPadSelected(selectedPad);
		});
	}

	@Override
	public int getItemCount() {
		return pads.size();
	}

	@Override
	public long getItemId(int position) {
		return pads.get(position).getId().hashCode();
	}

	static class ViewHolder extends RecyclerView.ViewHolder {

		@BindView(R.id.txt_title)
		TextView titleText;

		ViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}

		void setPad(Pad pad) {
			titleText.setText(pad.getText());
		}
	}

	interface Listener {
		void onPadSelected(Pad pad);
	}
}
