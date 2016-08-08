package at.favre.app.bankathon16.parentsapp.adapter;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.List;

import at.favre.app.bankathon16.R;
import at.favre.app.bankathon16.misc.Util;
import at.favre.app.bankathon16.model.User;
import at.favre.app.bankathon16.parentsapp.NfcRegisterChildActivity;
import at.favre.app.bankathon16.parentsapp.ParentDetailsActivity;
import at.favre.app.bankathon16.parentsapp.ParentMainActivity;
import at.favre.app.bankathon16.parentsapp.adapter.holder.ChildViewHolder;

/**
 * @author Florian Rauscha
 */
public class ChildListAdapter extends RecyclerView.Adapter<ChildViewHolder> implements PopupMenu.OnMenuItemClickListener, View.OnClickListener {

    private final Context mContext;
    private List<User> mData;
    private PopupMenu popup;

    public ChildListAdapter(Context context) {
        mContext = context;
        mData = new ArrayList<>();
    }

    public void setData(List<User> data) {
        mData = data;
    }

    public void add(User user) {
        mData.add(user);
    }

    public void clear() {
        mData.clear();
    }

    public void remove(int position){
        if (position < getItemCount()  ) {
            mData.remove(position);
            notifyItemRemoved(position);
        }
    }

    public ChildViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_child, parent, false);
        return ChildViewHolder.newInstance(view);
    }

    @Override
    public void onBindViewHolder(ChildViewHolder holder, final int position) {
        final User item = mData.get(position);

        holder.getChildImage().setText(item.getName().substring(0,1));
        holder.getChildName().setText(item.getName());
        holder.getAmount().setText(Util.formatWithCurrencyCode(item.getAmountInCent(), "€"));

        holder.getPayment().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAmountActivity(item);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParentDetailsActivity.start(v.getContext(), item);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                NfcRegisterChildActivity.start(v.getContext(), item.getName(), item.getId());
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public void onClick(View v) {
        openContextMenu(v);
    }

    private void openAmountActivity(User user) {
        ((ParentMainActivity) mContext).onChildTouched(user);
    }

    private void openContextMenu(View v) {
        popup = new PopupMenu(v.getContext(), v);
        popup.inflate(R.menu.payment_menu);
        popup.setOnMenuItemClickListener(this);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        item.setChecked(!item.isChecked());
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.action_school:
                popup.show();
                return true;
            case R.id.action_food:
                popup.show();
                return true;
            case R.id.action_games:
                popup.show();
                return true;
            default:
                return true;
        }
    }

}
