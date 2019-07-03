package com.codepath.android.lollipopexercise.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.codepath.android.lollipopexercise.R;
import com.codepath.android.lollipopexercise.activities.DetailsActivity;
import com.codepath.android.lollipopexercise.models.Contact;

import java.util.List;

// Provide the underlying view for an individual list item.
public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.VH> {
    private Activity mContext;
    private List<Contact> mContacts;

    public ContactsAdapter(Activity context, List<Contact> contacts) {
        mContext = context;
        if (contacts == null) {
            throw new IllegalArgumentException("contacts must not be null");
        }
        mContacts = contacts;
    }

    // Inflate the view based on the viewType provided.
    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        return new VH(itemView, mContext);
    }

    // Display data at the specified position
    @Override
    public void onBindViewHolder(final VH holder, int position) {
        Contact contact = mContacts.get(position);
        holder.rootView.setTag(contact);
        holder.tvName.setText(contact.getName());
        loadImagePalette(mContext, contact.getThumbnailDrawable(), holder.ivProfile, holder.vPalette, holder.tvName);
    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }

    public static void loadImagePalette(Context context, Integer resource, final ImageView iv, final View vPalette, final TextView ...tvs) {
        SimpleTarget<Bitmap> target = new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                // Insert the bitmap into the profile image view
                iv.setImageBitmap(resource);
                // Use generate() method from the Palette API to get the vibrant color from the bitmap
                Palette palette = Palette.generate(resource);
                // Set the result as the background color for `R.id.vPalette` view containing the contact's name.
                vPalette.setBackgroundColor(palette.getDominantColor(0xFFFFFF));
                int textColor = palette.getDominantSwatch().getTitleTextColor();
                for (TextView tv : tvs) {
                    tv.setTextColor(textColor);
                }
            }
        };
        Glide.with(context).load(resource).asBitmap().centerCrop().into(target);
    }

    // Provide a reference to the views for each contact item
    public class VH extends RecyclerView.ViewHolder {
        final View rootView;
        final ImageView ivProfile;
        final TextView tvName;
        final View vPalette;

        public VH(View itemView, final Context context) {
            super(itemView);
            rootView = itemView;
            ivProfile = (ImageView)itemView.findViewById(R.id.ivProfile);
            tvName = (TextView)itemView.findViewById(R.id.tvName);
            vPalette = itemView.findViewById(R.id.vPalette);

            // Navigate to contact details activity on click of card view.
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // LEARN: How does this tagging system work?
                    final Contact contact = (Contact)v.getTag();
                    if (contact != null) {
                        // Fire an intent when a contact is selected
                        Intent intent = new Intent(mContext, DetailsActivity.class);
                        // Pass contact object in the bundle and populate details activity.
                        intent.putExtra(DetailsActivity.EXTRA_CONTACT, contact);
                        // FIXME: The way I've set this up creates a popping animation, since there's a mismatch in the number of fields in the two parent views.
                        ActivityOptionsCompat options = ActivityOptionsCompat
                                .makeSceneTransitionAnimation(mContext, new Pair<View, String>(rootView, "contact"));
                        mContext.startActivity(intent, options.toBundle());
                    }
                }
            });
        }
    }
}
