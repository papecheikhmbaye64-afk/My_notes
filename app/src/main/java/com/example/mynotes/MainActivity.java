package com.example.mynotes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.snackbar.Snackbar;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NoteAdapter.OnNoteListener {

    private DatabaseHelper db;
    private List<Note> allNotes = new ArrayList<>();
    private final List<Note> displayedNotes = new ArrayList<>();

    private NoteAdapter adapter;
    private RecyclerView rv;
    private TextView tvEmpty, tvCount, tvMainTitle;
    private LinearLayout palette;
    private RelativeLayout root;
    private EditText searchField;

    private boolean isFavFilter = false;
    private String lastSearch = "";
    private int currentSortType = 0;
    private boolean isDarkMode = false;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseHelper(this);
        prefs = getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE);
        isDarkMode = prefs.getBoolean("IsDark", false);

        root = findViewById(R.id.mainRootLayout);
        tvMainTitle = findViewById(R.id.mainTitle);
        rv = findViewById(R.id.recyclerViewNotes);
        tvEmpty = findViewById(R.id.emptyTextView);
        tvCount = findViewById(R.id.notesCountTextView);
        palette = findViewById(R.id.colorPaletteLayout);
        searchField = findViewById(R.id.searchEditText);
        Button btnFav = findViewById(R.id.btnFilterFavorites);
        Button btnSort = findViewById(R.id.btnSort);
        ImageView btnTheme = findViewById(R.id.btnToggleTheme);
        View fab = findViewById(R.id.fabAdd);

        applyThemeUI();

        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NoteAdapter(displayedNotes, this);
        rv.setAdapter(adapter);

        // ── Swipe-to-delete ──────────────────────────────────────────────────
        ItemTouchHelper.SimpleCallback swipeCallback = new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(@NonNull RecyclerView rv,
                                  @NonNull RecyclerView.ViewHolder vh,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false; // pas de drag-and-drop
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Note deletedNote = displayedNotes.get(position);

                // Suppression immédiate de la liste affichée
                displayedNotes.remove(position);
                allNotes.remove(deletedNote);
                adapter.notifyItemRemoved(position);
                updateCountDisplay();

                // Snackbar avec action Annuler
                Snackbar.make(root, "Note \"" + deletedNote.getTitle() + "\" supprimée", Snackbar.LENGTH_LONG)
                        .setAction("Annuler", v -> {
                            // Restauration
                            allNotes.add(deletedNote);
                            filterAndSort();
                        })
                        .addCallback(new Snackbar.Callback() {
                            @Override
                            public void onDismissed(Snackbar sb, int event) {
                                // Suppression en base seulement si l'utilisateur n'a pas annulé
                                if (event != DISMISS_EVENT_ACTION) {
                                    db.deleteNote(deletedNote.getId());
                                }
                            }
                        })
                        .show();
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView rv,
                                    @NonNull RecyclerView.ViewHolder vh,
                                    float dX, float dY, int actionState, boolean isActive) {

                if (actionState != ItemTouchHelper.ACTION_STATE_SWIPE) {
                    super.onChildDraw(c, rv, vh, dX, dY, actionState, isActive);
                    return;
                }

                View itemView = vh.itemView;
                int itemHeight = itemView.getBottom() - itemView.getTop();

                // Fond rouge
                ColorDrawable background = new ColorDrawable(Color.parseColor("#EB5757"));
                if (dX > 0) { // glissement vers la droite
                    background.setBounds(itemView.getLeft(), itemView.getTop(),
                            itemView.getLeft() + (int) dX, itemView.getBottom());
                } else { // glissement vers la gauche
                    background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(),
                            itemView.getRight(), itemView.getBottom());
                }
                background.draw(c);

                // Icône corbeille (utilise ic_delete si disponible, sinon dessin manuel)
                Drawable deleteIcon = ContextCompat.getDrawable(MainActivity.this,
                        android.R.drawable.ic_menu_delete);
                if (deleteIcon != null) {
                    deleteIcon.setTint(Color.WHITE);
                    int iconMargin = (itemHeight - deleteIcon.getIntrinsicHeight()) / 2;
                    int iconTop = itemView.getTop() + iconMargin;
                    int iconBottom = iconTop + deleteIcon.getIntrinsicHeight();

                    if (dX > 0) { // droite : icône à gauche
                        int iconLeft = itemView.getLeft() + iconMargin;
                        int iconRight = iconLeft + deleteIcon.getIntrinsicWidth();
                        deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                    } else { // gauche : icône à droite
                        int iconRight = itemView.getRight() - iconMargin;
                        int iconLeft = iconRight - deleteIcon.getIntrinsicWidth();
                        deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                    }
                    deleteIcon.draw(c);
                }

                super.onChildDraw(c, rv, vh, dX, dY, actionState, isActive);
            }
        };
        new ItemTouchHelper(swipeCallback).attachToRecyclerView(rv);
        // ─────────────────────────────────────────────────────────────────────

        fab.setOnClickListener(v -> palette.setVisibility(
                palette.getVisibility() == View.GONE ? View.VISIBLE : View.GONE));
        setupPalette();

        searchField.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                lastSearch = s.toString();
                filterAndSort();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        btnFav.setOnClickListener(v -> {
            isFavFilter = !isFavFilter;
            btnFav.setAlpha(isFavFilter ? 0.4f : 1.0f);
            filterAndSort();
        });

        btnSort.setOnClickListener(v -> openSortDialog());
        btnTheme.setOnClickListener(v -> {
            isDarkMode = !isDarkMode;
            prefs.edit().putBoolean("IsDark", isDarkMode).apply();
            applyThemeUI();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        searchField.setText(lastSearch);
        loadNotes();
    }

    private void loadNotes() {
        allNotes = db.getAllNotes();
        filterAndSort();
    }

    private void filterAndSort() {
        displayedNotes.clear();
        int favCount = 0;

        for (Note n : allNotes) {
            if (n.isFavorite() == 1) favCount++;
            boolean matchesFav = !isFavFilter || n.isFavorite() == 1;
            boolean matchesSearch = n.getTitle().toLowerCase().contains(lastSearch.toLowerCase());
            if (matchesFav && matchesSearch) displayedNotes.add(n);
        }

        tvCount.setText(allNotes.size() + " notes • " + favCount + " favorites");

        if (currentSortType == 0) Collections.sort(displayedNotes, (n1, n2) -> Long.compare(n2.getDateModified(), n1.getDateModified()));
        else if (currentSortType == 1) Collections.sort(displayedNotes, (n1, n2) -> Long.compare(n2.getDateCreated(), n1.getDateCreated()));
        else if (currentSortType == 2) Collections.sort(displayedNotes, (n1, n2) -> n1.getTitle().compareToIgnoreCase(n2.getTitle()));
        else if (currentSortType == 3) Collections.sort(displayedNotes, (n1, n2) -> n2.getTitle().compareToIgnoreCase(n1.getTitle()));
        else if (currentSortType == 4) Collections.sort(displayedNotes, (n1, n2) -> Integer.compare(n2.isFavorite(), n1.isFavorite()));

        if (displayedNotes.isEmpty()) {
            tvEmpty.setText(!lastSearch.isEmpty() ? "Aucune note trouvée" : "Aucune notes");
            tvEmpty.setVisibility(View.VISIBLE);
            rv.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rv.setVisibility(View.VISIBLE);
        }
        adapter.updateList(displayedNotes);
    }

    private void updateCountDisplay() {
        int favCount = 0;
        for (Note n : allNotes) if (n.isFavorite() == 1) favCount++;
        tvCount.setText(allNotes.size() + " notes • " + favCount + " favorites");
        if (displayedNotes.isEmpty()) {
            tvEmpty.setText(!lastSearch.isEmpty() ? "Aucune note trouvée" : "Aucune notes");
            tvEmpty.setVisibility(View.VISIBLE);
            rv.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rv.setVisibility(View.VISIBLE);
        }
    }

    private void openSortDialog() {
        String[] options = {"Date de modification", "Date de création", "Alphabétique (A → Z)", "Alphabétique (Z → A)", "Favorites en premier"};
        new AlertDialog.Builder(this)
                .setTitle("Trier les notes par :")
                .setItems(options, (d, index) -> {
                    currentSortType = index;
                    filterAndSort();
                }).show();
    }

    private void applyThemeUI() {
        int bg = isDarkMode ? Color.parseColor("#121212") : Color.parseColor("#FAFAFA");
        int txt = isDarkMode ? Color.WHITE : Color.BLACK;
        root.setBackgroundColor(bg);
        tvMainTitle.setTextColor(txt);
    }

    private void setupPalette() {
        View.OnClickListener click = v -> {
            String col = "828282";
            int id = v.getId();
            if (id == R.id.colorVert) col = "219653";
            else if (id == R.id.colorRouge) col = "EB5757";
            else if (id == R.id.colorBleu) col = "2F80ED";
            else if (id == R.id.colorJaune) col = "F2C94C";
            else if (id == R.id.colorOrange) col = "F2994A";

            palette.setVisibility(View.GONE);
            Intent i = new Intent(this, NoteActivity.class);
            i.putExtra("COLOR", col);
            startActivity(i);
        };
        findViewById(R.id.colorGris).setOnClickListener(click);
        findViewById(R.id.colorVert).setOnClickListener(click);
        findViewById(R.id.colorRouge).setOnClickListener(click);
        findViewById(R.id.colorBleu).setOnClickListener(click);
        findViewById(R.id.colorJaune).setOnClickListener(click);
        findViewById(R.id.colorOrange).setOnClickListener(click);
    }

    @Override public void onNoteClick(Note note) {
        Intent i = new Intent(this, NoteActivity.class);
        i.putExtra("ID", note.getId());
        i.putExtra("TITLE", note.getTitle());
        i.putExtra("CONTENT", note.getContent());
        i.putExtra("COLOR", note.getColor());
        i.putExtra("FAV", note.isFavorite());
        i.putExtra("CREATED", note.getDateCreated());
        startActivity(i);
    }

    @Override public void onNoteDoubleTap(Note note) {
        note.setFavorite(note.isFavorite() == 1 ? 0 : 1);
        db.updateNote(note);
        loadNotes();
        Snackbar.make(root, note.isFavorite() == 1 ? "Ajouté aux favoris ★" : "Retiré des favoris", Snackbar.LENGTH_SHORT).show();
    }

    @Override public void onNoteLongClick(Note note) {
        new AlertDialog.Builder(this)
                .setMessage("Supprimer la note \"" + note.getTitle() + "\" ?")
                .setPositiveButton("Supprimer", (d, w) -> {
                    db.deleteNote(note.getId());
                    loadNotes();
                    Toast.makeText(this, "Note supprimée", Toast.LENGTH_SHORT).show();
                }).setNegativeButton("Annuler", null).show();
    }
}
