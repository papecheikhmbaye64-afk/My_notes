package com.example.mynotes;

import android.graphics.Color;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private List<Note> notes;
    private final OnNoteListener listener;

    public interface OnNoteListener {
        void onNoteClick(Note note);
        void onNoteDoubleTap(Note note);
        void onNoteLongClick(Note note);
    }

    public NoteAdapter(List<Note> notes, OnNoteListener listener) {
        this.notes = notes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NoteViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int pos) {
        Note note = notes.get(pos);
        holder.tvTitle.setText(note.getTitle());

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.FRENCH);
        holder.tvDate.setText("Modifiée : " + sdf.format(new Date(note.getDateModified())));

        try {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#" + note.getColor()));
        } catch (Exception e) {
            holder.cardView.setCardBackgroundColor(Color.GRAY);
        }

        holder.ivFavorite.setVisibility(note.isFavorite() == 1 ? View.VISIBLE : View.GONE);

        GestureDetector gd = new GestureDetector(holder.itemView.getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(@NonNull MotionEvent e) {
                listener.onNoteClick(note);
                return true;
            }
            @Override
            public boolean onDoubleTap(@NonNull MotionEvent e) {
                listener.onNoteDoubleTap(note);
                return true;
            }
            @Override
            public void onLongPress(@NonNull MotionEvent e) {
                listener.onNoteLongClick(note);
            }
        });

        holder.itemView.setOnTouchListener((v, event) -> {
            gd.onTouchEvent(event);
            return true;
        });
    }

    @Override
    public int getItemCount() { return notes.size(); }

    public void updateList(List<Note> newList) {
        this.notes = newList;
        notifyDataSetChanged();
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDate;
        ImageView ivFavorite;
        CardView cardView;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.textViewTitle);
            tvDate = itemView.findViewById(R.id.textViewDate);
            ivFavorite = itemView.findViewById(R.id.imageViewFavorite);
            cardView = itemView.findViewById(R.id.noteItemCard);
        }
    }
}