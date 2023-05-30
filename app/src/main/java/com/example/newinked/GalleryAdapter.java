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
import com.squareup.picasso.Picasso;

import java.util.List;


//adaptador para tatuador.
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
            convertView = LayoutInflater.from(context).inflate(R.layout.galley_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.imageView = convertView.findViewById(R.id.gallery_image);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String imageUrl = imageUrls.get(position);
        Picasso.get().load(imageUrl).into(viewHolder.imageView);

        viewHolder.imageView.setOnTouchListener(new View.OnTouchListener() {
            private boolean isLongPress = false;
            private Handler handler = new Handler();

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Iniciar un retraso de 5 segundos para borrar la imagen
                        isLongPress = false;
                        handler.postDelayed(longPressRunnable, 5000);
                        v.animate().scaleX(1.f).scaleY(1.4f).setDuration(100).start();
                        break;
                    case MotionEvent.ACTION_UP:
                        // Cancelar el retraso y restablecer la escala
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
                    int index = imageUrls.indexOf(imageUrl);
                    if (index != -1) {
                        imageUrls.remove(index);
                        notifyDataSetChanged();
                        // Aquí también puedes agregar la lógica para eliminar la imagen de la base de datos
                        Toast.makeText(context, "Imagen borrada", Toast.LENGTH_SHORT).show();
                    }
                }
            };
        });

        return convertView;
    }

    private static class ViewHolder {
        ImageView imageView;
    }
}
