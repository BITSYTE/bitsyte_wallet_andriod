package com.wallet.bitsyte.bitsite_wallet.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wallet.bitsyte.bitsite_wallet.ListObjects.WalletObject;
import com.wallet.bitsyte.bitsite_wallet.R;

import java.util.ArrayList;

/**
 *
 */
public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

	private ArrayList<WalletObject> mDataset = new ArrayList<>();

	public static class ViewHolder extends RecyclerView.ViewHolder {
		public TextView wnTextView;
		public TextView wMTextView;
		public ImageView wIImageView;
		public ViewHolder(View v) {
			super(v);
			wnTextView = (TextView) v.findViewById(R.id.walletName);
			wMTextView = (TextView) v.findViewById(R.id.walletMoney);
			wIImageView = (ImageView) v.findViewById(R.id.walletIcon);
		}
	}

	public HomeAdapter(ArrayList<WalletObject> dataset) {
		mDataset.clear();
		mDataset.addAll(dataset);
	}

	@Override
	public HomeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_wallets, parent, false);
		ViewHolder vh = new ViewHolder(v);
		return vh;
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		holder.wnTextView.setText(mDataset.get(position).getWalletName());
		holder.wMTextView.setText(mDataset.get(position).getWalletMoney());
		if(mDataset.get(position).getWalletImage().equals("1")) {
			holder.wIImageView.setBackgroundResource(R.drawable.biticon);
		}
		if(mDataset.get(position).getWalletImage().equals("2")) {
			holder.wIImageView.setBackgroundResource(R.drawable.biticon2);
		}
		if(mDataset.get(position).getWalletImage().equals("3")) {
			holder.wIImageView.setBackgroundResource(R.drawable.biticon3);
		}
		if(mDataset.get(position).getWalletImage().equals("4")) {
			holder.wIImageView.setBackgroundResource(R.drawable.biticon4);
		}

	}

	@Override
	public int getItemCount() {
		return mDataset.size();
	}

}
