package com.example.newinked;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.List;

public class GalleryAdapter extends BaseAdapter {
    private Context context;
    private List<String> imageUrls;

    public GalleryAdapter(Context context, List<String> imageUrls) {
        this.context = context;
        this.imageUrls = imageUrls;
    }

    @Override
    public int getCount() {
        return imageUrls.size();
    }

    @Override
    public Object getItem(int position) {
        return imageUrls.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.gallery_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.imageView = convertView.findViewById(R.id.gallery_image);
            convertView.setTag(viewHolder);

            //setPadding
            viewHolder.imageView.setPadding(8, 8, 8, 8);
            viewHolder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String imageUrl = imageUrls.get(position);
        Picasso.get().load(imageUrl).into(viewHolder.imageView);

        viewHolder.imageUrl = imageUrl;

        viewHolder.imageView.setOnTouchListener(new View.OnTouchListener() {
            private boolean isLongPress = false;
            private Handler handler = new Handler();

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Borra imagen con long press
                        isLongPress = false;
                        handler.postDelayed(longPressRunnable, 5000);
                        v.animate().scaleX(1.8f).scaleY(1.8f).setDuration(100).start();
                        break;
                    case MotionEvent.ACTION_UP:
                        // Reescala
                        if (!isLongPress) {
                            handler.removeCallbacks(longPressRunnable);
                            v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start();
                        }
                        break;
                }
                return true;
            }

            private Runnable longPressRunnable = new Runnable() {
                @Override
                public void run() {
                    // Realizar el borrado de la imagen
                    int index = imageUrls.indexOf(viewHolder.imageUrl); // Acceder a la URL desde el ViewHolder
                    if (index != -1) {
                        String imageUrlToRemove = imageUrls.get(index);

                        // Borrar en Firebase Realtime Database
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("tatuadores").child("imagenes");
                        Query query = databaseReference.orderByChild("imageUrl").equalTo(imageUrlToRemove);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    snapshot.getRef().removeValue();
                                    break;
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(context, "Error al borrar la imagen", Toast.LENGTH_SHORT).show();
                            }
                        });

                        //borrado con firestore
                        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                        CollectionReference galleryRef = firestore.collection("gallery");
                        com.google.firebase.firestore.Query firestoreQuery = galleryRef.whereEqualTo("imageUrl", imageUrlToRemove);
                        firestoreQuery.get().addOnCompleteListener(task -> {
                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                QueryDocumentSnapshot document = (QueryDocumentSnapshot) task.getResult().getDocuments().get(0);
                                document.getReference().delete();
                            } else {
                                Toast.makeText(context, "Error al borrar la imagen", Toast.LENGTH_SHORT).show();
                            }
                        });

                        imageUrls.remove(index);
                        notifyDataSetChanged();
                        Toast.makeText(context, "Imagen borrada", Toast.LENGTH_SHORT).show();
                    }
                }
            };
        });

        return convertView;
    }

    private static class ViewHolder {
        ImageView imageView;
        String imageUrl; // Campo adicional para guardar la URL de la imagen
    }
}
