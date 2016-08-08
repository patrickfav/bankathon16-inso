package at.favre.app.bankathon16.parentsapp.adapter.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import at.favre.app.bankathon16.R;

/**
 * @author Florian Rauscha
 */
public class ChildViewHolder extends RecyclerView.ViewHolder {

    private TextView childImage;
    private TextView childName;
    private TextView amount;
    private ImageButton payment;

    public ChildViewHolder(View itemView, TextView childImage, TextView childName, TextView amount, ImageButton payment) {
        super(itemView);
        this.childImage = childImage;
        this.childName = childName;
        this.payment = payment;
        this.amount = amount;
    }

    public static ChildViewHolder newInstance(View child) {
        TextView childImage = (TextView) child.findViewById(R.id.childImage);
        TextView childName = (TextView) child.findViewById(R.id.childName);
        TextView amount = (TextView) child.findViewById(R.id.amount);
        ImageButton payment = (ImageButton) child.findViewById(R.id.payment);
        return new ChildViewHolder(child, childImage, childName, amount, payment);
    }

    public TextView getChildImage() {
        return childImage;
    }

    public TextView getChildName() {
        return childName;
    }

    public ImageButton getPayment() {
        return payment;
    }

    public TextView getAmount() {
        return amount;
    }
}

