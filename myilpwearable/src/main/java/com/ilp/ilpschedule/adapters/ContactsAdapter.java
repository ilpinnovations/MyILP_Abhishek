package com.ilp.ilpschedule.adapters;

/**
 * Created by 1007546 on 18-10-2016.
 */

import android.content.Context;
import android.content.Intent;
import android.support.wearable.view.CircledImageView;
import android.support.wearable.view.WearableListView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ilp.ilpschedule.R;
import com.ilp.ilpschedule.activities.ContactDetailsActivity;
import com.ilp.ilpschedule.beans.ContactsBean;

import java.util.ArrayList;

public class ContactsAdapter extends WearableListView.Adapter {
    private ArrayList<ContactsBean> mItems;
    private final LayoutInflater mInflater;
    private Context mContext;



    public ContactsAdapter(Context context, ArrayList<ContactsBean> items) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mItems = items;
    }

    @Override
    public WearableListView.ViewHolder onCreateViewHolder(
            ViewGroup viewGroup, int i) {
        return new ItemViewHolder(mInflater.inflate(R.layout.contacts_row, null));
    }

    @Override
    public void onBindViewHolder(WearableListView.ViewHolder viewHolder,
                                 int position) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) viewHolder;
        CircledImageView circledView = itemViewHolder.mCircledImageView;
        setupImages(circledView, mItems.get(position).getContactName().charAt(0));

        TextView textView = itemViewHolder.mItemTextView;
        textView.setText(mItems.get(position).getContactName());

        // Text view for the phone number
        // deprecated because of lack of space
//        TextView numberView = itemViewHolder.mNumberTextView;
//        numberView.setText(mItems.get(position).getContactNumber());
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    private static class ItemViewHolder extends WearableListView.ViewHolder {
        private CircledImageView mCircledImageView;
        private TextView mItemTextView;

//        private TextView mNumberTextView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            mCircledImageView = (CircledImageView)
                    itemView.findViewById(R.id.circle);
            mItemTextView = (TextView) itemView.findViewById(R.id.name);
//            mNumberTextView = (TextView) itemView.findViewById(R.id.number);
        }
    }

    private void setupImages(CircledImageView circledView, char startingChar) {
        switch (startingChar) {
            case 'A':
                circledView.setImageResource(R.drawable.a_icon);
                break;
            case 'B':
                circledView.setImageResource(R.drawable.b_icon);
                break;
            case 'C':
                circledView.setImageResource(R.drawable.c_icon);
                break;
            case 'D':
                circledView.setImageResource(R.drawable.d_icon);
                break;
            case 'E':
                circledView.setImageResource(R.drawable.e_icon);
                break;
            case 'F':
                circledView.setImageResource(R.drawable.f_icon);
                break;
            case 'G':
                circledView.setImageResource(R.drawable.g_icon);
                break;
            case 'H':
                circledView.setImageResource(R.drawable.h_icon);
                break;
            case 'I':
                circledView.setImageResource(R.drawable.i_icon);
                break;
            case 'J':
                circledView.setImageResource(R.drawable.j_icon);
                break;
            case 'K':
                circledView.setImageResource(R.drawable.k_icon);
                break;
            case 'L':
                circledView.setImageResource(R.drawable.l_icon);
                break;
            case 'M':
                circledView.setImageResource(R.drawable.m_icon);
                break;
            case 'N':
                circledView.setImageResource(R.drawable.n_icon);
                break;
            case 'O':
                circledView.setImageResource(R.drawable.o_icon);
                break;
            case 'P':
                circledView.setImageResource(R.drawable.p_icon);
                break;
            case 'Q':
                circledView.setImageResource(R.drawable.q_icon);
                break;
            case 'R':
                circledView.setImageResource(R.drawable.r_icon);
                break;
            case 'S':
                circledView.setImageResource(R.drawable.s_icon);
                break;
            case 'T':
                circledView.setImageResource(R.drawable.t_icon);
                break;
            case 'U':
                circledView.setImageResource(R.drawable.u_icon);
                break;
            case 'V':
                circledView.setImageResource(R.drawable.v_icon);
                break;
            case 'W':
                circledView.setImageResource(R.drawable.w_icon);
                break;
            case 'X':
                circledView.setImageResource(R.drawable.x_icon);
                break;
            case 'Y':
                circledView.setImageResource(R.drawable.y_icon);
                break;
            case 'Z':
                circledView.setImageResource(R.drawable.z_icon);
                break;

        }


    }
}